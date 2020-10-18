package com.isma.soli.ad.LoginScreen;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.res.ResourcesCompat;
import androidx.core.os.ConfigurationCompat;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.res.Resources;
import android.os.Bundle;
import android.os.Handler;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.FirebaseTooManyRequestsException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;
import com.isma.soli.ad.InternalDatabase.DBSimpleIntel;
import com.isma.soli.ad.Map.MapsActivity;
import com.isma.soli.ad.R;
import com.isma.soli.ad.Util.HelperClass;
import com.isma.soli.ad.Util.StaticValues;
import com.yarolegovich.lovelydialog.LovelyInfoDialog;
import com.yarolegovich.lovelydialog.LovelyStandardDialog;
import com.yarolegovich.lovelydialog.LovelyTextInputDialog;

import java.util.HashMap;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

public class LoginActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private String deviceToken;
    private DatabaseReference mUserRef;
    private String currentUserId;
    private EditText UserTel, UserPassword,et_nickname;
    private Button btn_login;
    private ProgressDialog loadingBar;

    private ImageView im_country_flag;
    private HelperClass helperClass = new HelperClass();

    private Boolean isConnected= false;
    private Boolean isAuto = false;

    Handler handler = new Handler();

    Runnable runnable = new Runnable() {
        @Override
        public void run()
        {
            ReStartSms();
        }
    };

    Dialog lovelyTextInputDialog;
    Locale locale;

    private String selectedCountry="";
    private String followNumber= "xxxxxxxx";


    final String []items  =  {"Belgique", "France"};



    private void ChangeLovelyTextInputDialog(String title, String message, final PhoneAuthCredential credential)
    {

        if (lovelyTextInputDialog.isShowing())
         {
             lovelyTextInputDialog.dismiss();

             lovelyTextInputDialog = new LovelyStandardDialog(this, LovelyStandardDialog.ButtonLayout.VERTICAL)
                .setTopColorRes(R.color.colorPrimary)
                .setButtonsColorRes(R.color.colorPrimary)
                .setIcon(R.drawable.icon8_message)
                .setTitle(title)
                .setMessage(message)
                .setCancelable(false)
                .setPositiveButton(android.R.string.ok, new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        signInWithPhoneAuthCredential(credential);
                    }
                })
                .show();
         }
    }
    private void DismissLoadingBar()
    {
        if (loadingBar.isShowing())
        {
            loadingBar.dismiss();
        }
    }


    PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks()
    {

        @Override
        public void onVerificationCompleted(PhoneAuthCredential credential) {

            // This callback will be invoked in two situations:
            // 1 - Instant verification. In some cases the phone number can be instantly
            //     verified without needing to send or enter a verification code.
            // 2 - Auto-retrieval. On some devices Google Play services can automatically
            //     detect the incoming verification SMS and perform verification without
            //     user action.

            isAuto = true;
            DismissLoadingBar();

            Log.i("CheckPhone", "onVerificationCompleted:" + credential);

            if (!isConnected)
            {
                ChangeLovelyTextInputDialog(getString(R.string.VerifSms), "Le sms a été récupéré automatiquement.", credential );
            }

        }

        @Override
        public void onCodeAutoRetrievalTimeOut(@NonNull String s)
        {
            super.onCodeAutoRetrievalTimeOut(s);

            lovelyTextInputDialog.cancel();
            try {
                if(loadingBar.isShowing())
                {
                    loadingBar.cancel();
                }
                helperClass.simpleDialog("Erreur...", "Si le sms n'arrive pas, réessayez. S'il n'arrive toujours pas, " +
                        "redémarrez votre téléphone et réinstallez l'application.\nSi le " +
                        "problème persiste, contactez nous via notre page Facebook \"Help&Services\" ", LoginActivity.this);
            }catch (Exception e)
            {}
        }

        @Override
        public void onVerificationFailed(FirebaseException e)
        {
            // This callback is invoked in an invalid request for verification is made,
            // for instance if the the phone number format is not valid.
            Log.i("CheckPhone", "onVerificationFailed", e);

            if (e instanceof FirebaseAuthInvalidCredentialsException) {
                // Invalid request
                // ...
            } else if (e instanceof FirebaseTooManyRequestsException) {
                // The SMS quota for the project has been exceeded
                // ...
            }
            loadingBar.dismiss();
            helperClass.simpleDialog("Erreur", e.getMessage().toString(), LoginActivity.this);

            // Show a message and update the UI
            // ...
        }

        @Override
        public void onCodeSent(@NonNull String verificationId,
                               @NonNull PhoneAuthProvider.ForceResendingToken token) {


            DismissLoadingBar();
            Log.i("CheckPhone", "onCodeSent:" + verificationId);
            if (!isAuto) {
                final String VerifID = verificationId;

                if (loadingBar.isShowing()) {
                    loadingBar.dismiss();
                }
                lovelyTextInputDialog = new LovelyTextInputDialog(LoginActivity.this, R.style.EditTextTintTheme)
                        .setTopColorRes(R.color.colorPrimary)
                        .setTitle(getString(R.string.VerifSms))
                        .setMessage("Veuillez rentrer le code reçu par sms.")
                        .setIcon(R.drawable.icon8_message)
                        .setInputFilter("Veuillez entrer le code reçu par sms.", new LovelyTextInputDialog.TextFilter() {
                            @Override
                            public boolean check(String text) {
                                return text.length() > 4;
                            }
                        })
                        .setCancelable(false)
                        .setHint("xxxxxx")
                        .setConfirmButton(android.R.string.ok, new LovelyTextInputDialog.OnTextInputConfirmListener()
                        {
                            @Override
                            public void onTextInputConfirmed(String text) {
                                if (!TextUtils.isEmpty(text)) {
                                    if (!isConnected) {
                                        PhoneAuthCredential credential = PhoneAuthProvider.getCredential(VerifID, text);
                                        signInWithPhoneAuthCredential(credential);
                                    }
                                }
                            }
                        })
                        .show();
            }
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        InitalizeFields();
        btn_login.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                if (helperClass.isNetworkAvailableDialog(LoginActivity.this))
                {
                    if (et_nickname.getText().toString().length() < 3 || et_nickname.getText().toString().length()>15) {

                        //TODO CHANGER
                        helperClass.simpleDialog("Prénom", "Veuillez entrer entre 3 et 15 caractères.", LoginActivity.this);
                    } else
                        {


                        if (UserTel.getText().toString().length() == 12)
                        {
                            AllowUserToLogin();
                        } else
                            {
                            //TODO CHANGER
                                DialogFormat();
                        }
                    }
                }
            }
        });





    }

    private void DialogFormat()
    {
        helperClass.simpleDialog("Mauvais format", "Veuillez utiliser le format suivant : \n"+ ReturnForm()+ followNumber + ".\nCliquez sur le drapeau pour changer de pays.", LoginActivity.this);

    }



    private void InitalizeFields() {


        im_country_flag = (ImageView) findViewById(R.id.im_country_flag);
        mAuth = FirebaseAuth.getInstance();
        mUserRef = FirebaseDatabase.getInstance().getReference().child(StaticValues.UserPath);
        UserTel = (EditText) findViewById(R.id.et_tel);
        //UserPassword = (EditText) findViewById(R.id.et_password);
        btn_login = (Button) findViewById(R.id.btn_login);
        et_nickname = (EditText) findViewById(R.id.et_nickname);

        locale = ConfigurationCompat.getLocales(Resources.getSystem().getConfiguration()).get(0);

      //  Locale.ENGLISH;


        try {

            new LovelyInfoDialog(LoginActivity.this)
                    .setTopColorRes(R.color.colorPrimary)
                    .setIcon(android.R.drawable.ic_dialog_info)
                    //This will add Don't show again checkbox to the dialog. You can pass any ID as argument
                    .setTitle("Vérification du compte")
                    .setMessage("Afin d'être sûr que les annonces sont postées par de vraies personnes et qu'il n'y a pas de faux profil, nous demandons une vérification par sms du compte. Par défaut, personne ne peut accéder à votre numéro.")
                    .show();

        }catch (Exception e){}


        im_country_flag.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
            AlertDialog alertDialog= new AlertDialog.Builder(LoginActivity.this)
                .setCancelable(true)
                .setItems(items, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i)
                    {
                        String country = items[i];
                        setSelectedCountry(getPhonePrefixeFromCountry(country));
                        SetHint();
                        SetImageFlagFromCountry(country);
                        Log.i("CheckCountry", items[i]);
                    }
                })
                .setIcon(android.R.drawable.ic_dialog_info)
                .show();
            alertDialog.setCanceledOnTouchOutside(true);
            }
        });
        SetHint();

        // btn_register = (Button) findViewById(R.id.btn_register);

    }

    private String getPhonePrefixeFromCountry(String country)
    {
            switch (country)
            {
                case "Belgique":
                {
                    return "+324";
                }
                case "France":
                {
                    return "+336";
                }
                default:
                {
                    return "+324";
                }
            }
    }
    private void SetImageFlagFromCountry(String country)
    {
            switch (country)
            {
                case "Belgique":
                    {
                        im_country_flag.setImageDrawable(ResourcesCompat.getDrawable(getResources(), R.drawable.icons8_belgique, null));
                        break;
                    }
                case "France":
                    {
                        im_country_flag.setImageDrawable(ResourcesCompat.getDrawable(getResources(), R.drawable.icons8_france, null));
                        break;
                    }
                default:
                    {
                        im_country_flag.setImageDrawable(ResourcesCompat.getDrawable(getResources(), R.drawable.icons8_belgique, null));
                    }
            }
    }
    private void setSelectedCountry(String prefixe)
    {
        selectedCountry = prefixe;
    }

    private void SetHint()
    {
        UserTel.setHint(ReturnForm() + followNumber);
    }
    private String ReturnForm()
    {
        if (TextUtils.isEmpty(selectedCountry))
        {
            locale.getDisplayCountry();
            Log.i("CheckLocale", locale.getDisplayCountry());
            String country = locale.getDisplayCountry();
            TelephonyManager tm = (TelephonyManager) this.getSystemService(Context.TELEPHONY_SERVICE);
            String countryCodeValue = "";
            if (tm != null) {
                countryCodeValue = tm.getNetworkCountryIso();
            }
            if (!TextUtils.isEmpty(countryCodeValue))
            {
                if (countryCodeValue.equals("be")) {
                    setSelectedCountry("+324");
                    SetImageFlagFromCountry("Belgique");
                    return "+324";
                } else {
                    if (countryCodeValue.equals("fr"))
                    {
                        setSelectedCountry("+336");
                        SetImageFlagFromCountry("France");
                        return "+336";
                    }
                }
            } else
                {
                if (country.equals("Belgique"))
                {
                    setSelectedCountry("+324");
                    SetImageFlagFromCountry("Belgique");
                    return "+324";
                } else
                    {
                    if (country.equals("France"))
                    {
                        setSelectedCountry("+336");
                        SetImageFlagFromCountry("France");
                        return "+336";
                    }
                }
            }
            setSelectedCountry("+324");
            SetImageFlagFromCountry("Belgique");
            return "+324";
        }
        else
        {
            return selectedCountry;
        }
    }

    private boolean VerifyForm(String phonenumber)
    {
        String verif = "";
        for (int i = 0; i < 4; i++)
        {
            verif = verif + phonenumber.charAt(i);
        }
        if (!verif.equals("+337"))
        {
            return verif.equals(selectedCountry);
        }
        else
        {
            return selectedCountry.equals("+336");
        }
    }

    private void signInWithPhoneAuthCredential(PhoneAuthCredential credential)
    {
        loadingBar = new ProgressDialog(this);
        loadingBar.setTitle(getString(R.string.CreatedAcc));
        loadingBar.setMessage(getString(R.string.waiting));
        loadingBar.setCancelable(false);
        loadingBar.setCanceledOnTouchOutside(false);
        loadingBar.show();
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful())
                        {
                            isConnected = true;
                            // Sign in success, update UI with the signed-in user's information
                            Log.i("CheckUser", "signInWithCredential:success");

                            FirebaseUser user = task.getResult().getUser();

                            final String phoneNumer = user.getPhoneNumber();
                            Log.i("phoneNumner", phoneNumer);
                            final String uid = user.getUid();
                            Log.i("phoneUID", uid);
                            HashMap<String, Object> addUser = new HashMap<>();
                            final String name = et_nickname.getText().toString();


                            //TODO CHECK THIS
                           /* if (TextUtils.isEmpty(name))
                            {
                                return;
                            }*/

                            addUser.put(StaticValues.PhoneNumber, phoneNumer);
                            addUser.put(StaticValues.UserName, name);


                            FirebaseDatabase.getInstance().getReference().child(StaticValues.UserPath).child(uid).updateChildren(addUser).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task)
                                {
                                    if (task.isSuccessful())
                                    {
                                        DBSimpleIntel.getInstance(LoginActivity.this).addElementTodB(StaticValues.UserID, uid);
                                        DBSimpleIntel.getInstance(LoginActivity.this).addElementTodB(StaticValues.UserName, name);
                                        DBSimpleIntel.getInstance(LoginActivity.this).addElementTodB(StaticValues.PhoneNumber, phoneNumer);



                                        FirebaseInstanceId.getInstance().getInstanceId().addOnSuccessListener(new OnSuccessListener<InstanceIdResult>() {
                                            @Override
                                            public void onSuccess(InstanceIdResult instanceIdResult) {


                                              //  handler.removeCallbacks(runnable);
                                                deviceToken = instanceIdResult.getToken();
                                                //write internal DB
                                                DBSimpleIntel.getInstance(LoginActivity.this).addElementTodB(StaticValues.UserToken, deviceToken);
                                                FirebaseDatabase.getInstance().getReference().child(StaticValues.UserPath).child(uid).child(StaticValues.UserToken).setValue(deviceToken);

                                                loadingBar.dismiss();
                                                Toast.makeText(LoginActivity.this, "Bonjour "+ name +" !" , Toast.LENGTH_LONG).show();
                                                helperClass.SendUserToOtherActivityAndFinishThisActivity(LoginActivity.this, MapsActivity.class, LoginActivity.this);


                                            }
                                        });

                                    }
                                    else
                                    {
                                        //Error?
                                        loadingBar.dismiss();

                                       // Toast.makeText(LoginActivity.this, "Erreur lors de la création de votre compte!", Toast.LENGTH_LONG).show();
                                        //TODO CHANGE
                                        helperClass.simpleDialog("Erreur", "Erreur lors de la création de votre compte!\nVeuillez recommencer, merci.", LoginActivity.this);
                                        String phone = DBSimpleIntel.getInstance(LoginActivity.this).getLastValue(StaticValues.PhoneNumber);
                                        String name = DBSimpleIntel.getInstance(LoginActivity.this).getLastValue(StaticValues.UserName);
                                        if (TextUtils.isEmpty(phone) || TextUtils.isEmpty(name)) {
                                            mAuth.signOut();
                                        }
                                    }
                                }
                            });




                            //send in firebase
                            //send in db

                            // ...
                        } else
                            {
                            // Sign in failed, display a message and update the UI

                            loadingBar.dismiss();
                            helperClass.simpleDialog("Erreur", "Erreur, numéro incorrect, code incorrect, ou connexion perdue.", LoginActivity.this);

                            Log.i("CheckUser", "signInWithCredential:failure", task.getException());
                            if (task.getException() instanceof FirebaseAuthInvalidCredentialsException) {
                                // The verification code entered was invalid
                            }
                        }
                    }
                });
    }

    private void AllowUserToLogin()
    {


        loadingBar = new ProgressDialog(this);
        loadingBar.setTitle("Envoi du sms");
        loadingBar.setMessage(getString(R.string.waiting));
        loadingBar.setCancelable(false);
        loadingBar.setCanceledOnTouchOutside(false);
        loadingBar.show();

        String phonenumber = UserTel.getText().toString();




        if (VerifyForm(phonenumber))
        {
            //handler.postDelayed(runnable, 20000);

            PhoneAuthProvider.getInstance().verifyPhoneNumber(
                    phonenumber,        // Phone number to verify
                    60,                 // Timeout duration
                    TimeUnit.SECONDS,   // Unit of timeout
                    this,               // Activity (for callback binding)
                    mCallbacks);        // OnVerificationStateChangedCallbacks
        } else {
            loadingBar.dismiss();
            //TODO CHANGER
            Log.i("CheckLogin", "wrong format");
            DialogFormat();

        }

        }

    private void ReStartSms()
    {

    }
    private void StartDelay()
    {
        int delay = 45 * 1000;
        handler.postDelayed(runnable, delay);
    }
}
