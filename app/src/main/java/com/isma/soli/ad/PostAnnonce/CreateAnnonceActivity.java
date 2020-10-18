package com.isma.soli.ad.PostAnnonce;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.content.PermissionChecker;
import androidx.core.os.ConfigurationCompat;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.isma.soli.ad.InternalDatabase.DBPostedAnnonce;
import com.isma.soli.ad.InternalDatabase.DBSimpleIntel;
import com.isma.soli.ad.Map.MapsActivity;
import com.isma.soli.ad.Model.AnnonceClass;
import com.isma.soli.ad.R;
import com.isma.soli.ad.Util.HelperClass;
import com.isma.soli.ad.Util.StaticValues;
import com.isma.soli.ad.Util.StoreImageClass;
import com.nabinbhandari.android.permissions.PermissionHandler;
import com.nabinbhandari.android.permissions.Permissions;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;
import com.yarolegovich.lovelydialog.LovelyChoiceDialog;
import com.yarolegovich.lovelydialog.LovelyInfoDialog;
import com.yarolegovich.lovelydialog.LovelyStandardDialog;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;

import id.zelory.compressor.Compressor;

import static android.os.Looper.getMainLooper;

public class CreateAnnonceActivity extends AppCompatActivity {



    private Button btn_send, btn_cancel;
    private CheckBox checkBox;
    private EditText et_annonce, et_prix, et_title;
    private TextView txt_euros, txt_expli, txt_coup_de_pouce;
    private String PostCodeGlobal="";

    FusedLocationProviderClient fusedLocationProviderClient;


    private ProgressDialog loadingBar;
    private ScrollView scrollView;


    HelperClass helperClass = new HelperClass();
    private AdView mAdView;

    private boolean IsThereAnImage = false;

    private LinearLayout linearLayout;
    private CheckBox CheckBoxChoiceBesoin, CheckBoxChoicePropose, CheckLoca;

    private ImageView imageView;

    LinearLayout linImage;
    String modif, modif_title, modif_heart, modif_prix, modif_key, modif_type, modif_time;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_annonce);
        Init();
        btn_send.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                ActionOnBtnsend();
            }
        });

        checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b)
                {
                    et_prix.setVisibility(View.VISIBLE);
                    txt_euros.setVisibility(View.VISIBLE);
                    HideKeyBoard();
                }
                else
                {

                    et_prix.setVisibility(View.GONE);
                    txt_euros.setVisibility(View.GONE);
                }

             //   Removefocus();
                ScrollDown();
                HideKeyBoard();
               // Removefocus();
            }
        });

        CheckBoxChoicePropose.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b)
            {
                if (b)
                {
                    CheckBoxChoiceBesoin.setChecked(false);
                    RestaureVisiblity();
                    txt_expli.setText("Demandez-vous une rémunération pour cela ?");
                    checkBox.setChecked(false);



                    LivraiRestaureVisiblity();

                    //show layout
                    //shut other layout
                }
                else
                {

                    LivraiShutVisibility();
                    ShutVisibility();
                }
              //  Removefocus();
                ScrollDown();
                HideKeyBoard();
              //  Removefocus();
            }
        });
        CheckBoxChoiceBesoin.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b)
            {
                if (b)
                {
                    CheckBoxChoicePropose.setChecked(false);
                    RestaureVisiblity();
                    txt_expli.setText("Êtes-vous prêt à rémunérer pour cela ?");
                    checkBox.setChecked(false);



                    // Check if no view has focus:


                    //show layout
                    //shut other layout
                }
                else
                {
                    ShutVisibility();
                }
               // Removefocus();
                ScrollDown();
                HideKeyBoard();
               // Removefocus();

            }
        });

     /*   mAdView = findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);*/


        btn_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }

    private void ActionOnBtnsend()
    {
        if (helperClass.isUserLoggedInDialog(CreateAnnonceActivity.this,
                "Pour créer une annonce, veuillez vous inscrire. En attendant, votre annonce sera enregistrée comme brouillon.", CreateAnnonceActivity.this))
        {
            if (helperClass.isNetworkAvailableDialog(CreateAnnonceActivity.this))
            {
                if (helperClass.VerifyIsLocationIsEnabled(CreateAnnonceActivity.this))
                {
                    if (helperClass.isLocationEnabled(CreateAnnonceActivity.this))
                    {
                        helperClass.getInstantDeviceLocation(CreateAnnonceActivity.this, CreateAnnonceActivity.this);
                        final Handler handler = new Handler();
                        handler.postDelayed(new Runnable() {
                            @Override
                            public void run()
                            {
                                if ((CheckBoxChoiceBesoin.isChecked() && !CheckBoxChoicePropose.isChecked()) || (CheckBoxChoicePropose.isChecked() && !CheckBoxChoiceBesoin.isChecked())) {
                                    voidCheckPermissionPhoneNumber();
                                } else {
                                    helperClass.simpleDialog("Cocher", "Veuillez cocher le type d'annonce", CreateAnnonceActivity.this);
                                }
                            }
                        }, 100);

                    }
                    else
                    {
                       AlertDialog alertDialog= new AlertDialog.Builder(context)
                                .setTitle("Localisation")
                                .setMessage("Merci d'activer manuellement la localisation. \nPour cela, aller dans la barre notification et activer la localisation en cliquant sur le logo localisation.")
                                .setCancelable(true)
                                .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int which)
                                    {
                                        ActivateLocation(CreateAnnonceActivity.this);
                                    }
                                })
                                .setIcon(android.R.drawable.ic_dialog_info)
                                .show();
                       alertDialog.setCanceledOnTouchOutside(true);
                    }
                }
                else
                {
                    DialogExplainWhyUseLocation(CreateAnnonceActivity.this, CreateAnnonceActivity.this, "Nous utilisons la géolocalisation afin que votre annonce soit visible autour de vous. \nSi vous cochez la livraison, votre annonce sera également visible partout.");
                }
            }
        }
    }

    private void Removefocus()
    {
        et_title.clearFocus();
        et_annonce.clearFocus();
        et_prix.clearFocus();
    }
    private void HideKeyBoard()
    {
        InputMethodManager imm = (InputMethodManager) getSystemService(Activity.INPUT_METHOD_SERVICE);
        imm.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0);
    }
    private void LivraiShutVisibility()
    {
        linearLayout.setVisibility(View.GONE);

    }
    private void LivraiRestaureVisiblity()
    {
        linearLayout.setVisibility(View.VISIBLE);
    }

    private void ShutVisibility()
    {
        txt_euros.setVisibility(View.GONE);
        checkBox.setVisibility(View.GONE);
        txt_expli.setVisibility(View.GONE);
        txt_coup_de_pouce.setVisibility(View.GONE);
        et_prix.setVisibility(View.GONE);
        linImage.setVisibility(View.GONE);


    }
    private void RestaureVisiblity ()
    {
        txt_coup_de_pouce.setVisibility(View.VISIBLE);
        checkBox.setVisibility(View.VISIBLE);
        txt_expli.setVisibility(View.VISIBLE);


        if (CheckBoxChoicePropose.isChecked()) {
            linImage.setVisibility(View.VISIBLE);

            final String LastImageUri = DBSimpleIntel.getInstance(context).getLastValue(StaticValues.LastImageUri);
            if (!TextUtils.isEmpty(LastImageUri)) {
                //  ViewGroup.LayoutParams params = imageView.getLayoutParams();
                //   int width = params.width;
                // imageView.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, (int) dpWidth));

                IsThereAnImage = true;
                imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
                imageView.setImageURI(Uri.parse(LastImageUri));


            } else {
                IsThereAnImage = false;
                imageView.setScaleType(ImageView.ScaleType.FIT_CENTER);
                imageView.setImageResource(R.drawable.icons8_add_image);
                //SET SRC
            }
        }

    }
    private void ScrollDown()
    {
        scrollView.post(new Runnable() {
            @Override
            public void run() {
                scrollView.fullScroll(View.FOCUS_DOWN);
            }
        });
    }
    private void Init()
    {
        loadingBar = new ProgressDialog(this);

        scrollView = (ScrollView) findViewById(R.id.scroll_seek);
        et_title = (EditText) findViewById(R.id.et_annonce);
        txt_euros = (TextView) findViewById(R.id.txt_euros);
        txt_expli = (TextView) findViewById(R.id.txt_expli);
        txt_coup_de_pouce = (TextView) findViewById(R.id.txt_coup_de_pouce);
        imageView = (ImageView) findViewById(R.id.imageview);

        linImage = (LinearLayout) findViewById(R.id.lin_image);


        linearLayout = (LinearLayout) findViewById(R.id.li_livr);


        CheckLoca = (CheckBox) findViewById(R.id.checkLoca);


        et_annonce = (EditText) findViewById(R.id.et_annonce_heart);
        et_prix = (EditText) findViewById(R.id.et_remu);
        checkBox = (CheckBox) findViewById(R.id.checkbox);
        btn_send = (Button) findViewById(R.id.btn_send);
        btn_cancel = (Button) findViewById(R.id.btn_cancel);



        et_prix.clearFocus();
        et_annonce.clearFocus();
        et_title.clearFocus();


        if (helperClass.VerifyIsLocationIsEnabled(CreateAnnonceActivity.this))
        {
            helperClass.getInstantDeviceLocation(CreateAnnonceActivity.this, CreateAnnonceActivity.this);
        }

        CheckBoxChoiceBesoin = (CheckBox) findViewById(R.id.checkHelp);
        CheckBoxChoicePropose = (CheckBox) findViewById(R.id.checkServ);


        ShutVisibility();
        LivraiShutVisibility();


        modif = getIntent().getStringExtra(StaticValues.IntentModifAnnonce);
        if (!TextUtils.isEmpty(modif))
        {
            modif_heart = getIntent().getStringExtra(StaticValues.IntentModifHeart);
         //
            modif_title = getIntent().getStringExtra(StaticValues.IntentModifTitle);
            modif_prix = getIntent().getStringExtra(StaticValues.IntentModifPrix);
            modif_type = getIntent().getStringExtra(StaticValues.IntentModifType);
            modif_time = getIntent().getStringExtra(StaticValues.IntentModifTime);

            if (TextUtils.isEmpty(modif_prix) || modif_prix.equals(StaticValues.EmptyTag))
            {
                modif_prix = "0";
            }
            modif_key = getIntent().getStringExtra(StaticValues.IntentModifKey);

            et_title.setText(modif_title);
            et_annonce.setText(modif_heart);


            //ModifType !!

            if (modif_type.equals("0"))
            {
                RestaureVisiblity();
                CheckBoxChoiceBesoin.setChecked(true);
                txt_expli.setText("Êtes-vous prêt à rémunérer pour cela ?");

            }
            else
            {
                if (modif_type.equals("1"))
                {
                    CheckBoxChoicePropose.setChecked(true);
                    RestaureVisiblity();

                    txt_expli.setText("Demandez-vous une rémunération pour cela ?");

                }
            }

            if (!TextUtils.isEmpty(modif_prix))
            {
                et_prix.setText(modif_prix);
                et_prix.setVisibility(View.VISIBLE);
                checkBox.setChecked(true);
            }


        }
        else
        {
            if (DBSimpleIntel.getInstance(this).checkAlreadyExist(StaticValues.TitleAnnonce))
            {
                String title = DBSimpleIntel.getInstance(this).getLastValue(StaticValues.TitleAnnonce);
                if (!title.equals(StaticValues.EmptyTag))
                {
                    et_title.setText(title);
                }
            }

            if (DBSimpleIntel.getInstance(this).checkAlreadyExist(StaticValues.HeartAnnonce))
            {
                String annonce = DBSimpleIntel.getInstance(this).getLastValue(StaticValues.HeartAnnonce);
                if (!annonce.equals(StaticValues.EmptyTag))
                {
                    et_annonce.setText(annonce);
                }
            }

            if (DBSimpleIntel.getInstance(this).checkAlreadyExist(StaticValues.PriceAnnonce))
            {
                String prix = DBSimpleIntel.getInstance(this).getLastValue(StaticValues.PriceAnnonce);
                if (!prix.equals(StaticValues.EmptyTag) &&  !TextUtils.isEmpty(prix))
                {
                    et_prix.setText(prix);
                    checkBox.setChecked(true);
                }
                else
                {
                    et_prix.setVisibility(View.GONE);
                    txt_euros.setVisibility(View.GONE);
                }
            }
            else
            {
                et_prix.setVisibility(View.GONE);
                txt_euros.setVisibility(View.GONE);
            }




            et_title.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                }

                @Override
                public void onTextChanged(CharSequence charSequence, int i, int i1, int i2)
                {
                    WriteDB(StaticValues.TitleAnnonce, charSequence.toString());

                }

                @Override
                public void afterTextChanged(Editable editable) {

                }
            });
            et_annonce.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                }

                @Override
                public void onTextChanged(CharSequence charSequence, int i, int i1, int i2)
                {

                    WriteDB(StaticValues.HeartAnnonce, charSequence.toString());
                }

                @Override
                public void afterTextChanged(Editable editable) {

                }
            });
            et_prix.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                }

                @Override
                public void onTextChanged(CharSequence charSequence, int i, int i1, int i2)
                {

                    WriteDB(StaticValues.PriceAnnonce, charSequence.toString());
                }

                @Override
                public void afterTextChanged(Editable editable) {

                }
            });

        }






        imageView.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(android.view.View v)
            {
             final String   lastImageUri = DBSimpleIntel.getInstance(context).getLastValue(StaticValues.LastImageUri);
                //TODO CHEKC IF THERE IS AN IMAGE OR NOT
                if (!TextUtils.isEmpty(lastImageUri)) {
                    String[] items = {"Voir/Recadrer l'image", "Choisir une autre image", "Supprimer l'image"};
                    new LovelyChoiceDialog(context)
                            .setTopColorRes(R.color.colorPrimary)
                            .setTitle("Options")
                            .setIcon(android.R.drawable.ic_dialog_info)
                            .setItems(items, new LovelyChoiceDialog.OnItemSelectedListener<String>() {
                                @Override
                                public void onItemSelected(int pos, String item) {
                                    switch (pos) {
                                        case 0:
                                            Log.i("CheckUri", "AlreadyKnown");
                                            CropImage.activity(Uri.parse(lastImageUri)) //va automatiquent aller dans le onActivityResult et exéctuer le code
                                                    .setGuidelines(CropImageView.Guidelines.ON)
                                                    .start(CreateAnnonceActivity.this);

                                            break;

                                        case 1:
                                            Log.i("CheckUri", "AlreadyKnown");
                                            CropImage.activity() //va automatiquent aller dans le onActivityResult et exéctuer le code
                                                    .setGuidelines(CropImageView.Guidelines.ON)
                                                    .start(CreateAnnonceActivity.this);
                                            break;
                                        case 2:
                                        {
                                            //DELETE IMAGE
                                           // SendImageToFirebase(FirebaseDatabase.getInstance().getReference().push().getKey());
                                            DelelePreviousImage();
                                            DBSimpleIntel.getInstance(context).addElementTodB(StaticValues.LastImageUri, "");
                                            IsThereAnImage = false;
                                            break;
                                        }
                                    }
                                }
                            })
                            .show();
                }
                else {
                    Log.i("CheckUri", "AlreadyKnown");
                    CropImage.activity() //va automatiquent aller dans le onActivityResult et exéctuer le code
                            .setGuidelines(CropImageView.Guidelines.ON)
                            .start(CreateAnnonceActivity.this);
                }


            }
        });
    }
    private  void WriteDB( String path, String value)
    {
        DBSimpleIntel.getInstance(this).addElementTodB(path, value);
    }
    private void SendAnnonce()
    {
        final AnnonceClass annonceClass = new AnnonceClass();
        String annonce = et_annonce.getText().toString();
        if (!helperClass.CheckSizeDialog(CreateAnnonceActivity.this, annonce,StaticValues.LargeSize, "La taille du contenu l'annonce peut-être de maximum " + StaticValues.LargeSize+ " caractères."))
        {
            Log.i("CheckSize", "annonce");
            return;
        }
        annonceClass.setHeart(annonce);
        String title = et_title.getText().toString();
        if (!helperClass.CheckSizeDialog(CreateAnnonceActivity.this, title,StaticValues.MediumSize, "La taille du titre l'annonce peut-être de maximum " + StaticValues.MediumSize+ " caractères."))
        {
            Log.i("CheckSize", "title");
            return;
        }
        annonceClass.setTitle(title);
        String prix = et_prix.getText().toString();

        //TODO CHANGE PRICE !!!!


       float pricecontrol = 0f;
        if (!TextUtils.isEmpty(prix))
        {
            try {
                pricecontrol =Float.parseFloat(prix);
            }catch (Exception e){}
        }
        if (pricecontrol > 15)
        {
            helperClass.simpleDialog("Prix trop élévé",
                    "Le prix maximum est de 15 euros par unité. Néanmoins vous pouvez noter dans l'annonce un prix par paquet.\nMerci de votre compréhension.", this);
            return;
        }
        /*if (!helperClass.CheckSizeDialog(CreateAnnonceActivity.this, prix,StaticValues.PrizeSize, "Le prix ne peut être que de " + StaticValues.PrizeSize+ " chiffres."))
        {
            Log.i("CheckSize", "prix");
            return;
        }*/
        if (!TextUtils.isEmpty(annonce) && !TextUtils.isEmpty(title)) {
            final HashMap<String, Object> addAnnonce = new HashMap<>();

            addAnnonce.put(StaticValues.annonce, annonce);
            addAnnonce.put(StaticValues.annonce_title, title);
            long time = Long.parseLong(DBSimpleIntel.getInstance(CreateAnnonceActivity.this).getLastValue(StaticValues.SyncServerTime));
            addAnnonce.put(StaticValues.Time, time);
            annonceClass.setTime(Long.toString(time));
            addAnnonce.put(StaticValues.UserID, DBSimpleIntel.getInstance(CreateAnnonceActivity.this).getLastValue(StaticValues.UserID));

            if (checkBox.isChecked()) {
                if (!TextUtils.isEmpty(prix)) {
                    addAnnonce.put(StaticValues.prix, prix);
                    annonceClass.setPrix(prix);
                } else {
                    annonceClass.setPrix(StaticValues.EmptyTag);
                }
            }
            if (CheckBoxChoiceBesoin.isChecked() && !CheckBoxChoicePropose.isChecked())
            {
                addAnnonce.put(StaticValues.Type, 0);
                annonceClass.setType("0");
            } else {

                if (CheckBoxChoicePropose.isChecked() && !CheckBoxChoiceBesoin.isChecked())
                {
                    addAnnonce.put(StaticValues.Type, 1);
                    annonceClass.setType("1");
                }
                else
                {
                    Toast.makeText(this, "Cochez le type d'annonce", Toast.LENGTH_SHORT).show();
                    return;
            }
        }



            if (!TextUtils.isEmpty(modif))
            {
                //pass small post
              //  String postal = DBSimpleIntel.getInstance(CreateAnnonceActivity.this).getLastValue(StaticValues.postalCode);
               // addAnnonce.put(StaticValues.postalCode, Integer.parseInt(modif_postcode));
              //  PostCodeGlobal = modif_postcode;
                SendFirebase(addAnnonce, annonceClass);
                //TODO Voulez-vous modifier la position de cette annonce?
            }
            else
            {
                new AlertDialog.Builder(CreateAnnonceActivity.this)
                        .setMessage("Assurez-vous d'être bien au lieu faisant référence à l'annonce lorsque vous la postez puisque nous utilisons votre localisation afin de pouvoir trier les annonces de manière pertinente.")
                        // Specifying a listener allows you to take an action before dismissing the dialog.
                        // The dialog is automatically dismissed when a dialog button is clicked.
                        .setPositiveButton("Oui, continuer", new DialogInterface.OnClickListener()
                        {
                            public void onClick(DialogInterface dialog, int which)
                            {


                                 /*   String postal = DBSimpleIntel.getInstance(CreateAnnonceActivity.this).getLastValue(StaticValues.postalCode);
                                    String smallpost;
                                    if (!TextUtils.isEmpty(postal))
                                    {
                                        smallpost = "" + postal.charAt(0) + postal.charAt(1);
                                        PostCodeGlobal = smallpost;
                                       // addAnnonce.put(StaticValues.postalCode, Integer.parseInt(smallpost));
                                    }
                                    else
                                    {
                                        smallpost = "";
                                    }*/


                                    String lastLong = DBSimpleIntel.getInstance(CreateAnnonceActivity.this).getLastValue(StaticValues.LastLongitude);
                                    String lastLatt = DBSimpleIntel.getInstance(CreateAnnonceActivity.this).getLastValue(StaticValues.LastLatitude);

                                    if (TextUtils.isEmpty(lastLatt) || TextUtils.isEmpty(lastLong))
                                    {
                                        helperClass.simpleDialog("Erreur", "Nous n'avons pas réussi à récupérer votre localisation", CreateAnnonceActivity.this);
                                        return;
                                    }
                                    /*** Si on ne connait pas la pos bug? Return ***/
                                /*    if (TextUtils.isEmpty(lastLatt) || TextUtils.isEmpty(lastLong) || TextUtils.isEmpty(smallpost))
                                    {
                                        helperClass.simpleDialog("Erreur", "Nous n'avons pas réussi à récupérer votre localisation", CreateAnnonceActivity.this);
                                        return;
                                    }*/

                                    addAnnonce.put(StaticValues.LastLatitude,lastLatt);
                                    addAnnonce.put(StaticValues.LastLongitude,lastLong);
                                  //  addAnnonce.put(StaticValues.postalCode, Integer.parseInt(smallpost));
                                    SendFirebase(addAnnonce, annonceClass);

                            }
                        })
                        .setNegativeButton("Non, reposter l'annonce plus tard", new DialogInterface.OnClickListener()
                        {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i)
                            {
                                //TODO Demander d'entrer le code postal
                                Toast.makeText(CreateAnnonceActivity.this, "Ajouté au brouillon ! ", Toast.LENGTH_SHORT).show();

                            }
                        })

                        .setIcon(android.R.drawable.ic_dialog_info)
                        .show();
            }






        }
    }
    private void voidCheckPermissionPhoneNumber()
    {

        if (!DBSimpleIntel.getInstance(CreateAnnonceActivity.this).checkAlreadyExist(StaticValues.PhonePermissionPath)) {
            String[] items = {"Directement par téléphone", "Recevoir une notification depuis l'application"};
            Dialog dialog = new LovelyChoiceDialog(this)
                    .setTopColorRes(R.color.colorPrimary)
                    .setTitle("Comment me contacter ?")
                    .setIcon(android.R.drawable.ic_dialog_info)
                    .setItems(items, new LovelyChoiceDialog.OnItemSelectedListener<String>() {
                        @Override
                        public void onItemSelected(int position, String item) {
                            switch (position) {
                                case 0:


                                    FirebaseDatabase.getInstance().getReference().child(StaticValues.UserPath).child(DBSimpleIntel.getInstance(CreateAnnonceActivity.this).getLastValue(StaticValues.UserID))
                                            .child(StaticValues.PhonePermissionPath).setValue(true).addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            DBSimpleIntel.getInstance(CreateAnnonceActivity.this).addElementTodB(StaticValues.PhonePermissionPath, "true");
                                            SendAnnonce();
                                        }
                                    });
                                    break;

                                case 1:
                                    FirebaseDatabase.getInstance().getReference().child(StaticValues.UserPath).child(DBSimpleIntel.getInstance(CreateAnnonceActivity.this).getLastValue(StaticValues.UserID))
                                            .child(StaticValues.PhonePermissionPath).setValue(false).addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            DBSimpleIntel.getInstance(CreateAnnonceActivity.this).addElementTodB(StaticValues.PhonePermissionPath, "false");
                                            SendAnnonce();
                                        }
                                    });
                                    break;

                            }
                        }
                    })
                    .show();


            dialog.setCanceledOnTouchOutside(false);

        }
        else
        {
            SendAnnonce();
        }

    }
    private void SendFirebase(HashMap<String, Object> addAnnonce, final AnnonceClass annonceClass)
    {
        final String key;
        String centerInt= "";
        if (TextUtils.isEmpty(modif))
        {
            key = FirebaseDatabase.getInstance().getReference().push().getKey().toString();
            centerInt = helperClass.returnCenter(Double.parseDouble(addAnnonce.get(StaticValues.LastLatitude).toString()));
          //  DBSimpleIntel.getInstance(context).addElementTodB(key+StaticValues.KeyCenter, centerInt);
           // DBSimpleIntel.getInstance(context).addElementTodB(key+StaticValues.LastLongitude, DBSimpleIntel.getInstance(context).getLastValue(StaticValues.LastLongitude));
           // DBSimpleIntel.getInstance(context).addElementTodB(key+StaticValues.LastLatitude, DBSimpleIntel.getInstance(context).getLastValue(StaticValues.LastLatitude));

        }
        else
        {
            key = modif_key;
        }


        String country;
        country = CheckIfCountryIsEmpty();

        DatabaseReference AnnonceRef = FirebaseDatabase.getInstance().getReference().child(StaticValues.AnnoncePath).child(country);
        DatabaseReference DistRef=  FirebaseDatabase.getInstance().getReference().child(StaticValues.Distance).child(StaticValues.Interval).child(country);
       // DatabaseReference TagAnnonceRef = FirebaseDatabase.getInstance().getReference().child(StaticValues.TagPath).child(country).child(firstLetter).child(PostCodeGlobal);


        //TODO FORMATTER LES TAGS !!!
        String query="";
        if (addAnnonce.get(StaticValues.Type).toString().equals("0"))
        {
            query = StaticValues.Demandeur;
            AnnonceRef = AnnonceRef.child(StaticValues.Demandeur);
            //TagAnnonceRef = TagAnnonceRef.child(StaticValues.Demandeur);
            DistRef = DistRef.child(StaticValues.Demandeur);

        }
        else
        {
            query = StaticValues.Offreur;
            AnnonceRef = AnnonceRef.child(StaticValues.Offreur);
            //TagAnnonceRef = TagAnnonceRef.child(StaticValues.Offreur);
            DistRef = DistRef.child(StaticValues.Offreur);

        }
        //TODO Verify if users wants to delete livraison?
        if (TextUtils.isEmpty(modif))
        {
            DistRef = DistRef.child(centerInt);
            final HashMap<String, Object> addIDAnn = new HashMap<>();

            Float la =helperClass.round(Float.parseFloat(addAnnonce.get(StaticValues.LastLatitude).toString()),4);
            Float lo =  helperClass.round(Float.parseFloat(addAnnonce.get(StaticValues.LastLongitude).toString()), 4);
            String laaa = Float.toString(la);
            String looo = Float.toString(lo);

            Log.i("CheckLa", la + "");
            Log.i("CheckLo", lo + "");
            addIDAnn.put(StaticValues.LastLatitude, (float) Float.parseFloat(laaa));
            addIDAnn.put(StaticValues.LastLongitude, (float) Float.parseFloat(looo));
            DistRef.child(key).updateChildren(addIDAnn);



            if (CheckLoca.isChecked() &&  addAnnonce.get(StaticValues.Type).toString().equals("1"))
            {
                DatabaseReference LocaRef=  FirebaseDatabase.getInstance().getReference().child("Loca").child(country).child(query+"l").child(key);
                LocaRef.updateChildren(addIDAnn);
                DBSimpleIntel.getInstance(CreateAnnonceActivity.this).addElementTodB(key+StaticValues.CreateLoca, "ok");
            }
        }
        if (IsThereAnImage &&  addAnnonce.get(StaticValues.Type).toString().equals("1"))
        {
            SendImageToFirebase(key);
        }
        final String typeee = addAnnonce.get(StaticValues.Type).toString();

        if (!TextUtils.isEmpty(modif_time))
        {
            if (modif_time.equals(StaticValues.EmptyTag))
            {
                annonceClass.setTime(modif_time);
            }
        }

        addAnnonce.remove(StaticValues.LastLongitude);
        addAnnonce.remove(StaticValues.LastLatitude);
        addAnnonce.remove(StaticValues.Type);


        final String center = centerInt;
        AnnonceRef.child(key).updateChildren(addAnnonce).addOnCompleteListener(new OnCompleteListener<Void>()
        {
            @Override
            public void onComplete(@NonNull Task<Void> task)
            {
                if (task.isSuccessful()) {
                    WriteDB(StaticValues.PriceAnnonce, StaticValues.EmptyTag);
                    WriteDB(StaticValues.TitleAnnonce, StaticValues.EmptyTag);
                    WriteDB(StaticValues.HeartAnnonce, StaticValues.EmptyTag);
                    final HelperClass helperClass = new HelperClass();


                    if (!IsThereAnImage || typeee.equals("0"))
                    {
                       AnnoncePubliee();
                    }

                    int count = 1;
                    if (DBSimpleIntel.getInstance(CreateAnnonceActivity.this).checkAlreadyExist(StaticValues.SyncAnnonceCount))
                    {
                        count = Integer.parseInt(DBSimpleIntel.getInstance(CreateAnnonceActivity.this).getLastValue(StaticValues.SyncAnnonceCount));
                        count++;


                    }
                    final int finalCount = count;
                    Log.i("CheckCount", ""+count);
                    FirebaseDatabase.getInstance().getReference().child(StaticValues.UserPath).child(DBSimpleIntel.getInstance(CreateAnnonceActivity.this).getLastValue(StaticValues.UserID)).
                            child(StaticValues.annonce).child(key).setValue(count);


                    //TODO Enregister cela dans la base de données interne !   /***DONE***/

                    //TODO MODIFIER AUSSI

                    DBSimpleIntel.getInstance(CreateAnnonceActivity.this).addElementTodB(StaticValues.SyncAnnonceCount, Integer.toString(finalCount));
                    Log.i("CheckAnnonClass", "Title " + annonceClass.getTitle() + " Time " + annonceClass.getTime()+ " Heart " + annonceClass.getHeart());
                    DBPostedAnnonce.getInstance(CreateAnnonceActivity.this).addElementTodB(annonceClass, key,center);

                }
            }
        });
    }

    private void AnnoncePubliee()
    {
        final String title;
        if (!TextUtils.isEmpty(modif))
        {
            title = "L'annonce a bien été modifiée";
        }
        else
        {
            title = "L'annonce a bien été publiée";
        }


        new LovelyStandardDialog(this, LovelyStandardDialog.ButtonLayout.VERTICAL)
                .setTopColorRes(R.color.colorPrimary)
                .setIcon(android.R.drawable.ic_dialog_info)
                .setTitle("Succès!")
                .setMessage(title)
                .setPositiveButton(android.R.string.ok, new View.OnClickListener() {
                    @Override
                    public void onClick(View v)
                    {
                        finish();
                    }
                })
                .show();
    }

    public void getInstantDeviceLocation(final Context context, final Activity activity)
    {
        String[] permissions = {Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION};
        Permissions.check(context, permissions, null/*rationale*/, null/*options*/, new PermissionHandler()
        {
            @Override
            public void onGranted()
            {
                if (!loadingBar.isShowing()) {
                    loadingBar = new ProgressDialog(context);
                    loadingBar.setTitle("Localisation");
                    loadingBar.setMessage("Nous cherchons votre localisation");
                    loadingBar.setCanceledOnTouchOutside(false);
                    loadingBar.show();
                }
                requestLocationUpdate(context,activity);
            }

            @Override
            public void onDenied(Context context, ArrayList<String> deniedPermissions)
            {
                super.onDenied(context, deniedPermissions);
                finish();
                // getInstantDeviceLocation(context, activity);
            }
        });
    }
    public void requestLocationUpdate(final Context context, final Activity activity)
    {

        LocationRequest locationRequest;
        if (ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) == PermissionChecker.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) == PermissionChecker.PERMISSION_GRANTED) {
            fusedLocationProviderClient = new FusedLocationProviderClient(context);
            locationRequest = new LocationRequest();

            locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
            locationRequest.setInterval(5000);
            locationRequest.setFastestInterval(3000);
            locationRequest.setSmallestDisplacement(10f);

            fusedLocationProviderClient.requestLocationUpdates(locationRequest, new LocationCallback() {
                @Override
                public void onLocationResult(LocationResult locationResult) {
                    super.onLocationResult(locationResult);

                    Log.i("CheckLoca", "Lat" +locationResult.getLastLocation().getLatitude());
                    Log.i("CheckLong", "Long" +locationResult.getLastLocation().getLongitude());


                    double Longit = locationResult.getLastLocation().getLongitude();
                    double Latit = locationResult.getLastLocation().getLatitude();
                    LatLng latLong = new LatLng(Latit, Longit);
                    if (!TextUtils.isEmpty(Double.toString(Longit)) && !TextUtils.isEmpty(Double.toString(Latit)))
                    {
                        fusedLocationProviderClient.removeLocationUpdates(this);
                        DBSimpleIntel.getInstance(context).addElementTodB(StaticValues.LastLatitude, Double.toString(Latit));
                        DBSimpleIntel.getInstance(context).addElementTodB(StaticValues.LastLongitude, Double.toString(Longit));
                        helperClass.getLocationDetails(latLong, context);
                    }
                    if (loadingBar.isShowing())
                    {
                        Toast.makeText(context, "Localisation trouvée. Vous pouvez maintenant poster une annonce.", Toast.LENGTH_SHORT).show();
                        loadingBar.dismiss();
                    }

                }
            }, getMainLooper());

        }else getInstantDeviceLocation(context,activity);
    }
    public void DialogExplainWhyUseLocation(final Context context, final Activity activity, String title)
    {
        new LovelyStandardDialog(context, LovelyStandardDialog.ButtonLayout.VERTICAL)
                .setTopColorRes(R.color.colorPrimary)
                .setIcon(android.R.drawable.ic_dialog_info)
                .setTitle("Geolocalisation")
                .setMessage(title)
                .setPositiveButton("Ok", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                       ActivateLocation(activity);
                    }
                })
                .show();
    }
    public void ActivateLocation(final Activity activity)
    {
        if (helperClass.isLocationEnabled(context)) {
            getInstantDeviceLocation(context, activity);
        }
        else
        {
            AlertDialog alertDialog = new AlertDialog.Builder(context)
                    .setTitle("Localisation")
                    .setMessage("Merci d'activer manuellement la localisation")
                    .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            ActivateLocation(activity);
                        }
                    })
                    .setIcon(android.R.drawable.ic_dialog_info)
                    .show();

            alertDialog.setCanceledOnTouchOutside(true);
        }
    }


    private String FilePath="";
    Bitmap thumb_bitmap=null;
    File thum_filepath;
    Context context = CreateAnnonceActivity.this;

    ProgressDialog loadingBar1;

    StoreImageClass storeImageClass = new StoreImageClass();
    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data)
    { //include lbrary  implementation 'com.theartofdev.edmodo:android-image-cropper:2.7.0' && put in manifest <activity android:name="com.theartofdev.edmodo.cropper.CropImageActivity"
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);

            if (resultCode == -1)
            {
                DelelePreviousImage();
                final Uri resultUri = result.getUri();
                thum_filepath = new File (resultUri.getPath()); //import library
                DBSimpleIntel.getInstance(context).addElementTodB(StaticValues.LastImageUri, resultUri.toString());
                imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
                imageView.setImageURI(resultUri);
                IsThereAnImage = true;
                //Testcompress();

            }
            else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE)
            {
                Exception error = result.getError();
            }

        }
    }
    private void Testcompress()
    {
        String uriString = DBSimpleIntel.getInstance(context).getLastValue(StaticValues.LastImageUri);
        Uri uri = Uri.parse(uriString);
        thum_filepath = new File (uri.getPath()); //import library
        int taille = 300;
        try
        {
            thumb_bitmap = new Compressor(this)
                    .setMaxWidth(taille)
                    .setMaxHeight(taille
                    )
                    .setQuality(100)
                    .compressToBitmap(thum_filepath);
        }
        catch (IOException e)
        {
            e.printStackTrace();
            return;
        }

        int quality = 60;

        try {

            //String filepathstr=filepath.toString();
            long fileSizeInBytes = thum_filepath.length();
            float fileSizeInKB =  fileSizeInBytes / 1024;
            // Convert the KB to MegaBytes (1 MB = 1024 KBytes)
            float fileSizeInMB =  fileSizeInKB / 1024;

            Log.i("CheckSize", "Size " + fileSizeInMB);

            if (fileSizeInMB > 1)
            {
                quality = 50;

            }
        }catch (Exception e){}


     //   quality = 50;

        /*** SaveBitmapToMemory ***/
        ContextWrapper cw = new ContextWrapper(context);
        final File directory = cw.getDir(StaticValues.DirectoryImageName, Context.MODE_PRIVATE); // path to /data/data/yourapp/app_imageDir

        FilePath = "test" + ".jpg";

        final File myImageFile = new File(directory, FilePath); // Create image file
        FileOutputStream fos = null;

        if (myImageFile.exists())
        {
            Log.i("CheckFile", "Delete!");
            myImageFile.delete();
        }
        else
        {
            Log.i("CheckFile", "Exist!");

        }


        //   long fileSizeInBytes = 0;
        //  float fileSizeInKB = 0;

        // do {



        try {
            fos = new FileOutputStream(myImageFile);
            thumb_bitmap.compress(Bitmap.CompressFormat.JPEG, quality, fos);
            Log.i("HelpServicesImage", "Compress");
            //      fileSizeInBytes = myImageFile.length();
            //      fileSizeInKB = fileSizeInBytes / 1024;


        } catch (IOException e)
        {

            e.printStackTrace();
            return;
        } finally {

            try {
                Log.i("HelpServicesImage", "Closed");
                fos.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        // } while (fileSizeInKB > 200);

        /******/
    }
    private void DelelePreviousImage()
    {
        String LastImageUri = DBSimpleIntel.getInstance(context).getLastValue(StaticValues.LastImageUri);
        if (!TextUtils.isEmpty(LastImageUri))
        {
            thum_filepath = new File(Uri.parse(LastImageUri).getPath()); //import library
            thum_filepath.delete();
            IsThereAnImage = false;
            imageView.setScaleType(ImageView.ScaleType.FIT_CENTER);
            imageView.setImageResource(R.drawable.icons8_add_image);

        }

    }
    private void SendImageToFirebase(String UID)
    {
        String uriString = DBSimpleIntel.getInstance(context).getLastValue(StaticValues.LastImageUri);
        Uri uri = Uri.parse(uriString);
        thum_filepath = new File (uri.getPath()); //import library

        loadingBar1 = new ProgressDialog(this);
        loadingBar1.setTitle("Envoi en cours de l'image");
        loadingBar1.setMessage("Merci de patienter");
        loadingBar1.setCancelable(false);
        loadingBar1.setCanceledOnTouchOutside(false);
        loadingBar1.show();

        //TODO Modifier la taille  !!!

        int taille = 300;
        try
        {
            thumb_bitmap = new Compressor(this)
                    .setMaxWidth(taille)
                    .setMaxHeight(taille
                    )
                    .setQuality(100)
                    .compressToBitmap(thum_filepath);
        }
        catch (IOException e)
        {
            e.printStackTrace();
            return;
        }

        int quality = 60;

        try {

            //String filepathstr=filepath.toString();
            long fileSizeInBytes = thum_filepath.length();
            float fileSizeInKB =  fileSizeInBytes / 1024;
            // Convert the KB to MegaBytes (1 MB = 1024 KBytes)
            float fileSizeInMB =  fileSizeInKB / 1024;

            Log.i("CheckSize", "Size " + fileSizeInMB);

            if (fileSizeInMB > 1)
            {
                quality = 50;

            }
        }catch (Exception e){}




        /*** SaveBitmapToMemory ***/
        ContextWrapper cw = new ContextWrapper(context);
        final File directory = cw.getDir(StaticValues.DirectoryImageName, Context.MODE_PRIVATE); // path to /data/data/yourapp/app_imageDir

        FilePath = UID + ".jpg";

        final File myImageFile = new File(directory, FilePath); // Create image file
        FileOutputStream fos = null;

        if (myImageFile.exists())
        {
            Log.i("CheckFile", "Delete!");
            myImageFile.delete();
        }
        else
        {
            Log.i("CheckFile", "Exist!");
        }


     //   long fileSizeInBytes = 0;
      //  float fileSizeInKB = 0;

       // do {

        //TODO UPGRADE QUALITY
            try {
                fos = new FileOutputStream(myImageFile);
                thumb_bitmap.compress(Bitmap.CompressFormat.JPEG, quality, fos);
                Log.i("HelpServicesImage", "Compress");
          //      fileSizeInBytes = myImageFile.length();
          //      fileSizeInKB = fileSizeInBytes / 1024;


            } catch (IOException e)
            {

                e.printStackTrace();
                return;
            } finally {

                try {
                    Log.i("HelpServicesImage", "Closed");
                    fos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
       // } while (fileSizeInKB > 200);

        /******/

        if (myImageFile.exists())
        {
            String countryc = CheckIfCountryIsEmpty();
            final StorageReference filePath = FirebaseStorage.getInstance().getReference().child(countryc).child(StaticValues.Offreur)
                    .child(FilePath);


           // final StorageReference filePath = FirebaseStorage.getInstance().getReference().child("TEST2").child(FilePath);

            byte[] bytesArray = new byte[(int) myImageFile.length()];
            Log.i("CheckLoLength", "Longueur + " + myImageFile.length());

            FileInputStream fis = null;
            try
            {
                fis = new FileInputStream(myImageFile);
            } catch (FileNotFoundException e)
            {
                e.printStackTrace();
            }
            try
            {
                fis.read(bytesArray); //read file into bytes[]
            } catch (IOException e) {
                e.printStackTrace();
            }
            try
            {
                fis.close();
            } catch (IOException e)
            {
                e.printStackTrace();
            }
            filePath.putBytes(bytesArray).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {

                    if (task.isSuccessful()) {
                        Log.i("CheckLoUplo", "working !");
                        loadingBar1.dismiss();
                        DelelePreviousImage();
                        DBSimpleIntel.getInstance(context).addElementTodB(StaticValues.LastImageUri, "");
                        AnnoncePubliee();
                    }
                    else
                    {
                        loadingBar1.dismiss();
                        Toast.makeText(context, "Erreur... " + task.getException().toString(), Toast.LENGTH_SHORT).show();
                    }

                }
            });

        }
    }
    private String CheckIfCountryIsEmpty()
    {
        String country = DBSimpleIntel.getInstance(context).getLastValue(StaticValues.country);
        if (TextUtils.isEmpty(country))
        {
            Log.i("CheckCountry", "empty");
            Locale locale;
            locale = ConfigurationCompat.getLocales(Resources.getSystem().getConfiguration()).get(0);
            country = locale.getDisplayCountry();
            if (country.equals("Belgique"))
            {
                country = "Belgium";
            }
            else
            {
                country = "France";
            }
        }
        else
        {
            Log.i("CheckCountry", "Not empty");
        }

        return country;
    }

}
