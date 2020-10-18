package com.isma.soli.ad.Map;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.isma.soli.ad.InternalDatabase.DBSimpleIntel;
import com.isma.soli.ad.PostAnnonce.CreateAnnonceActivity;
import com.isma.soli.ad.Profil.ProfilActivity;
import com.isma.soli.ad.R;
import com.isma.soli.ad.SeekAnnonce.SeekAnnonceActivity;
import com.isma.soli.ad.Util.HelperClass;
import com.isma.soli.ad.Util.StaticValues;

import java.io.IOException;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class MapsActivity extends AppCompatActivity {



    private static final String TAG = "MapsActivity";


    HelperClass helperClass = new HelperClass();
    Button btn_profil, btn_help, btn_annonce;
    private AdView mAdView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        InitFields();

        btn_profil.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (helperClass.isUserLoggedInDialog(MapsActivity.this, "Afin d'accéder à votre profil, veuillez vous inscrire.", MapsActivity.this)) {
                    //if (isVersionChecked)
                        helperClass.SendUserToOtherActivity(MapsActivity.this, ProfilActivity.class, MapsActivity.this);
                }
            }
        });
        btn_help.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               // if (isVersionChecked)
                    helperClass.SendUserToOtherActivity(MapsActivity.this, SeekAnnonceActivity.class, MapsActivity.this);

            }
        });
        btn_annonce.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               // if (isVersionChecked)
                    helperClass.SendUserToOtherActivity(MapsActivity.this, CreateAnnonceActivity.class, MapsActivity.this);

            }
        });


       /* mAdView = findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);

        mAdView.setAdListener(new AdListener() {
            @Override
            public void onAdFailedToLoad(int errorCode) {
                Log.i("onAdFailedToLoad", "This is why: " + errorCode);

            }
        });*/

        String name = DBSimpleIntel.getInstance(this).getLastValue(StaticValues.UserName);
        {
            if (!TextUtils.isEmpty(name))
            {
                try {
                    Toast.makeText(this, "Bonjour " + name+"!", Toast.LENGTH_LONG).show();
                }catch (Exception e){}
            }
            else
            {
                try {
                    Toast.makeText(this, "Bonjour !", Toast.LENGTH_LONG).show();
                }catch (Exception e){}
            }
        }

        //TODO Check if internet et la localisation est activée !

        // helperClass.isNetworkAvailableDialog(this);
        SyncAppVersion();
        // helperClass.getInstantDeviceLocation(this, MapsActivity.this);




    }




    private void SyncAppVersion()
    {
        FirebaseDatabase.getInstance().getReference().child(StaticValues.AdminPath).orderByValue().startAt(StaticValues.AppVersion +1).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot)
            {
                if (dataSnapshot.exists())
                {
                    String version = dataSnapshot.child("Version").getValue().toString();
                    if (Integer.parseInt(version) > StaticValues.AppVersion)
                    {

                        Log.i("UpdateAppVersion", "RequiresUpdate!");
                        Dialog dialog = new AlertDialog.Builder(MapsActivity.this)
                                .setTitle("Mise à jour disponible")
                                .setMessage("Bonjour, une mise à jour est disponible sur le Google Play. Merci de l'installer afin que nos services fonctionnent au mieux. ")

                                // Specifying a listener allows you to take an action before dismissing the dialog.
                                // The dialog is automatically dismissed when a dialog button is clicked.
                                .setPositiveButton("Mettre à jour", new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int which)
                                    {
                                        Intent intent = new Intent(Intent.ACTION_VIEW);
                                        intent.setData(Uri.parse("https://play.google.com/store/apps/details?id=com.isma.soli.ad"));
                                        startActivity(intent);
                                    }
                                })
                                .setNegativeButton("Plus tard", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {

                                    }
                                })


                                .setIcon(android.R.drawable.ic_dialog_info)
                                .show();

                    }
                    else
                    {
                        Log.i("UpdateAppVersion", "No update found!");

                    }
                }
                else
                {
                    Log.i("UpdateAppVersion", "Doesnt exist");

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError)
            {
                Log.i("UpdateAppVersion", "error : " + databaseError.toString());
            }
        });
    }

    @Override
    protected void onResume() {
        SetUserName();
        helperClass.VerifyIfUsersIsInBlackList(MapsActivity.this);
       // if (helperClass.isNetworkAvailableDialog(MapsActivity.this)) {
            SyncAppVersion();
        //}
        super.onResume();
    }

    private void SetUserName()
    {
        FirebaseUser currentUser;
        FirebaseAuth mAuth;
        mAuth= FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();
        String uDDB = DBSimpleIntel.getInstance(MapsActivity.this).getLastValue(StaticValues.UserID);
        if(!TextUtils.isEmpty(uDDB))
        {
            Log.i("Checkuser", ""+ uDDB);
        }

        if (currentUser != null && !TextUtils.isEmpty(uDDB))
        {

            if (!DBSimpleIntel.getInstance(MapsActivity.this).checkAlreadyExist("USERTESTTEST"))
            {


                String user = DBSimpleIntel.getInstance(MapsActivity.this).getLastValue(StaticValues.UserName);
                if (!TextUtils.isEmpty(user)) {

                    Log.i("Checkuser", user);
                    FirebaseDatabase.getInstance().getReference().child(StaticValues.UserPath).child(DBSimpleIntel.getInstance(MapsActivity.this).getLastValue(StaticValues.UserID)).child(StaticValues.UserName).setValue(user).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task)
                        {
                            DBSimpleIntel.getInstance(MapsActivity.this).addElementTodB("USERTESTTEST", "ok");
                        }
                    });

                }
            }
            else
            {
                Log.i("Checkusernam", "already");

            }
            if (!DBSimpleIntel.getInstance(MapsActivity.this).checkAlreadyExist("USERPHONETEST"))
            {

                String phonenumber = DBSimpleIntel.getInstance(MapsActivity.this).getLastValue(StaticValues.PhoneNumber);
                if (!TextUtils.isEmpty(phonenumber))
                {
                    Log.i("Checkuser", phonenumber);
                    FirebaseDatabase.getInstance().getReference().child(StaticValues.UserPath).child(DBSimpleIntel.getInstance(MapsActivity.this).getLastValue(StaticValues.UserID)).child(StaticValues.PhoneNumber).setValue(phonenumber).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            DBSimpleIntel.getInstance(MapsActivity.this).addElementTodB("USERPHONETEST", "ok");
                        }
                    });

                }
            }
            else
            {
                Log.i("Checkuserphone", "already");

            }
            if (!DBSimpleIntel.getInstance(MapsActivity.this).checkAlreadyExist("USERPHONEPERMI"))
            {

                String permi = DBSimpleIntel.getInstance(MapsActivity.this).getLastValue(StaticValues.PhonePermissionPath);
                if (!TextUtils.isEmpty(permi))
                {
                    if (permi.equals("true"))
                    {
                        FirebaseDatabase.getInstance().getReference().child(StaticValues.UserPath).child(DBSimpleIntel.getInstance(MapsActivity.this).getLastValue(StaticValues.UserID)).
                                child(StaticValues.PhonePermissionPath).setValue(true).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                DBSimpleIntel.getInstance(MapsActivity.this).addElementTodB("USERPHONEPERMI", "ok");

                            }
                        });
                    }
                    else if (permi.equals("false"))
                    {
                        FirebaseDatabase.getInstance().getReference().child(StaticValues.UserPath).child(DBSimpleIntel.getInstance(MapsActivity.this).getLastValue(StaticValues.UserID)).
                                child(StaticValues.PhonePermissionPath).setValue(false).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                DBSimpleIntel.getInstance(MapsActivity.this).addElementTodB("USERPHONEPERMI", "ok");
                            }
                        });
                    }
                    Log.i("Checkuserpermi", permi);

                }
            }
            else
            {
                Log.i("Checkuserpermi", "already");

            }
        }
        else
        {
            Log.i("Checkusername", "noaccc");

        }

    }

    private void InitFields()
    {
        btn_help = (Button) findViewById(R.id.btn_help);
        btn_profil = (Button) findViewById(R.id.btn_profil);
        btn_annonce = (Button) findViewById(R.id.btn_post_annonce);

    }

}
