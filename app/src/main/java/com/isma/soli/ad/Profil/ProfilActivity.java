package com.isma.soli.ad.Profil;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.database.FirebaseDatabase;
import com.isma.soli.ad.InternalDatabase.DBSimpleIntel;
import com.isma.soli.ad.Map.MapsActivity;
import com.isma.soli.ad.PostAnnonce.CreateAnnonceActivity;
import com.isma.soli.ad.R;
import com.isma.soli.ad.Util.FragmentAdapter;
import com.isma.soli.ad.Util.HelperClass;
import com.isma.soli.ad.Util.StaticValues;
import com.yarolegovich.lovelydialog.LovelyChoiceDialog;
import com.yarolegovich.lovelydialog.LovelyStandardDialog;

public class ProfilActivity extends AppCompatActivity {

    private FragmentAdapter FragAdapter;
    private Toolbar toolbar;
    private ViewPager viewPager;
    private TabLayout tabLayout;
    private FloatingActionButton flt_add;
    private AdView mAdView;
    String intentgo;

    TextView Nom, Number;

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if (item.getItemId()==R.id.callfeedback)
        {
            HelperClass helperClass = new HelperClass();
            helperClass.FeedBack(ProfilActivity.this);
        }
        else {
            if (item.getItemId() == R.id.callFaceBook) {
                String FACEBOOK_URL = "https://www.facebook.com/HelpServices-111304393854486";
                String FACEBOOK_PAGE_ID = "Help&Services";



                Intent facebookIntent = new Intent(Intent.ACTION_VIEW);
                String facebookUrl = getFacebookPageURL(ProfilActivity.this, FACEBOOK_URL, FACEBOOK_PAGE_ID);
                facebookIntent.setData(Uri.parse(facebookUrl));
                try {
                    ProfilActivity.this.startActivity(facebookIntent);
                }catch (Exception e){}
            }
            else
            {
                if (item.getItemId() == R.id.callPermissions)
                {
                    String[] items = {"Directement par téléphone", "Recevoir une notification depuis l'application"};
                   Dialog dialog=  new LovelyChoiceDialog(ProfilActivity.this)
                            .setTopColorRes(R.color.colorPrimary)
                           .setTitle("Comment me contacter ?")
                            .setIcon(android.R.drawable.ic_dialog_info)
                            .setItems(items, new LovelyChoiceDialog.OnItemSelectedListener<String>()
                            {
                                @Override
                                public void onItemSelected(int pos, String item) {
                                    HelperClass helperClass = new HelperClass();
                                    if (helperClass.isNetworkAvailableDialog(ProfilActivity.this)) {
                                        switch (pos) {
                                            case 0:
                                                FirebaseDatabase.getInstance().getReference().child(StaticValues.UserPath).child(DBSimpleIntel.getInstance(ProfilActivity.this).getLastValue(StaticValues.UserID))
                                                        .child(StaticValues.PhonePermissionPath).setValue(true).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                    @Override
                                                    public void onComplete(@NonNull Task<Void> task) {
                                                        Toast.makeText(ProfilActivity.this, "Permission mise à jour !", Toast.LENGTH_LONG).show();
                                                        DBSimpleIntel.getInstance(ProfilActivity.this).addElementTodB(StaticValues.PhonePermissionPath, "true");
                                                    }
                                                });
                                                break;

                                            case 1:
                                                FirebaseDatabase.getInstance().getReference().child(StaticValues.UserPath).child(DBSimpleIntel.getInstance(ProfilActivity.this).getLastValue(StaticValues.UserID))
                                                        .child(StaticValues.PhonePermissionPath).setValue(false).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                    @Override
                                                    public void onComplete(@NonNull Task<Void> task) {
                                                        DBSimpleIntel.getInstance(ProfilActivity.this).addElementTodB(StaticValues.PhonePermissionPath, "false");
                                                        Toast.makeText(ProfilActivity.this, "Permission mise à jour !", Toast.LENGTH_LONG).show();
                                                    }
                                                });
                                                break;
                                        }
                                    }
                                }
                            })
                            .show();
                    dialog.setCanceledOnTouchOutside(false);
                }
                else {
                    if (!TextUtils.isEmpty(intentgo))
                    {
                        HelperClass helperClass = new HelperClass();
                        helperClass.SendUserToOtherActivityAndFinishThisActivity(ProfilActivity.this, MapsActivity.class, ProfilActivity.this);
                    }
                    else {
                        finish();
                    }
                }
            }
        }



        return super.onOptionsItemSelected(item);
    }

    public String getFacebookPageURL(Context context, String FACEBOOK_URL, String FACEBOOK_PAGE_ID) {
        PackageManager packageManager = context.getPackageManager();
        try {
            int versionCode = packageManager.getPackageInfo("com.facebook.katana", 0).versionCode;
            if (versionCode >= 3002850) { //newer versions of fb app
                return "fb://facewebmodal/f?href=" + FACEBOOK_URL;
            } else { //older versions of fb app
                return "fb://page/" + FACEBOOK_PAGE_ID;
            }
        } catch (PackageManager.NameNotFoundException e) {
            return FACEBOOK_URL; //normal web url
        }
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profil);
        SetViewPager();

       /* mAdView = findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);*/

    }

    private void SetViewPager()
    {
        viewPager = (ViewPager) findViewById(R.id.main_tabs_pager);
        toolbar = (Toolbar) findViewById(R.id.main_page_toolbar);
        tabLayout = (TabLayout) findViewById(R.id.main_tabs);


        Nom = (TextView) findViewById(R.id.NameConnect);
        Number = (TextView) findViewById(R.id.NumberConnect);

        if (DBSimpleIntel.getInstance(this).checkAlreadyExist(StaticValues.UserName))
        {
            Nom.setText(DBSimpleIntel.getInstance(this).getLastValue(StaticValues.UserName));
        }
        if (DBSimpleIntel.getInstance(this).checkAlreadyExist(StaticValues.PhoneNumber))
        {
            Number.setText(DBSimpleIntel.getInstance(this).getLastValue(StaticValues.PhoneNumber));
        }

        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        toolbar.setTitleTextAppearance(this, R.style.AveriaLight);
        getSupportActionBar().setTitle("Mon profil"); //string is custom name you want


        FragAdapter = new FragmentAdapter(getSupportFragmentManager(), 0);

        Fragment menuFragment = new MyAnnonceFragment();
        ((FragmentAdapter) FragAdapter).AddFragment(menuFragment,"Mes annonces");

        Fragment menuFragment2 = new MessagesFragment();
        ((FragmentAdapter) FragAdapter).AddFragment(menuFragment2, "Messages reçus");

        Fragment menuFragment3 = new MessageSentFragment();
        ((FragmentAdapter) FragAdapter).AddFragment(menuFragment3, "Messages envoyés");



        viewPager.setAdapter(FragAdapter);
        tabLayout.setupWithViewPager(viewPager);


         intentgo = getIntent().getStringExtra(StaticValues.IntentProfil);

        if (!TextUtils.isEmpty(intentgo))
        {
            if (intentgo.equals(StaticValues.IntentProfil))
            {
                new Handler().postDelayed(
                        new Runnable() {
                            @Override public void run() {
                                try{
                                    tabLayout.getTabAt(1).select();
                                }catch (Exception e){}

                            }
                        }, 100);
            }
        }

    }
    public void DialogNoAnnonce()
    {
        if (TextUtils.isEmpty(intentgo))
        {
            new LovelyStandardDialog(this, LovelyStandardDialog.ButtonLayout.VERTICAL)
                    .setTopColorRes(R.color.colorPrimary)
                    .setTitle("Annonce")
                    .setMessage("Aucune annonce publiée.")
                    .setIcon(android.R.drawable.ic_dialog_info)
                    .setPositiveButton("Créer une annonce", new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            HelperClass helperClass = new HelperClass();
                            helperClass.SendUserToOtherActivityAndFinishThisActivity(ProfilActivity.this, CreateAnnonceActivity.class, ProfilActivity.this);
                        }
                    })
                    .setNegativeButton("Annuler", null)
                    .show();
           /* new AlertDialog.Builder(this)
                    .setTitle("Annonce")
                    .setMessage("Aucune annonce publiée.")

                    // Specifying a listener allows you to take an action before dismissing the dialog.
                    // The dialog is automatically dismissed when a dialog button is clicked.
                    .setPositiveButton("Créer une annonce", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            HelperClass helperClass = new HelperClass();
                            helperClass.SendUserToOtherActivityAndFinishThisActivity(ProfilActivity.this, CreateAnnonceActivity.class, ProfilActivity.this);
                        }
                    })
                    .setNegativeButton("Annuler", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {

                        }
                    })


                    .setIcon(android.R.drawable.ic_dialog_info)
                    .show();
                    */
        }
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.menu_profil, menu);

        return true;
    }
}
