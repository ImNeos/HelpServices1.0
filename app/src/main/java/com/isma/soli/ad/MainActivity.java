package com.isma.soli.ad;

import android.animation.Animator;
import android.animation.ValueAnimator;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.telephony.TelephonyManager;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.Animation;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.os.ConfigurationCompat;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;
import com.isma.soli.ad.InternalDatabase.DBMessage;
import com.isma.soli.ad.InternalDatabase.DBMessageSent;
import com.isma.soli.ad.InternalDatabase.DBPostedAnnonce;
import com.isma.soli.ad.InternalDatabase.DBSimpleIntel;
import com.isma.soli.ad.LoginScreen.LoginActivity;
import com.isma.soli.ad.Map.MapsActivity;
import com.isma.soli.ad.Model.ListComeBack;
import com.isma.soli.ad.Util.FragmentAdapter;
import com.isma.soli.ad.Util.HelperClass;
import com.isma.soli.ad.Util.LocationHelper;
import com.isma.soli.ad.Util.StaticValues;
import com.yarolegovich.lovelydialog.LovelyCustomDialog;
import com.yarolegovich.lovelydialog.LovelyStandardDialog;
import com.yarolegovich.lovelydialog.LovelyTextInputDialog;

import org.w3c.dom.Text;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class MainActivity extends AppCompatActivity {

    private String deviceToken;

     String errorM="";

    private Toolbar toolbar;
    private ViewPager viewPager;
    private TabLayout tabLayout;

    private FragmentAdapter FragAdapter;

    private FirebaseUser currentUser;
    private FirebaseAuth mAuth;
    private HelperClass helperClass = new HelperClass();
    private AdView mAdView;

    private ProgressDialog loadingBar;
    int x = 0;


    ValueEventListener valueEventListener = new ValueEventListener() {
        @Override
        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
            if (dataSnapshot.exists()) {

                for (DataSnapshot child : dataSnapshot.getChildren()) {

                    Double lat2 = Double.parseDouble(child.child(StaticValues.LastLatitude).getValue().toString());
                    Double lon2 = Double.parseDouble(child.child(StaticValues.LastLongitude).getValue().toString());

                    final Double lattitude = Double.parseDouble(DBSimpleIntel.getInstance(MainActivity.this).getLastValue(StaticValues.LastLatitude));
                    final Double longitude = Double.parseDouble(DBSimpleIntel.getInstance(MainActivity.this).getLastValue(StaticValues.LastLongitude));

                    Log.i("CheckSystemCoordonates", "DistanceB2points " + child.getKey() + " " +  helperClass.ReturnDistanceBetweenTwoCoordonates(lattitude, lat2 , longitude, lon2));

                }

            }
        }

        @Override
        public void onCancelled(@NonNull DatabaseError databaseError) {

        }
    };

    public int count = -1;

    Locale locale;
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

       // DBMessageSent.getInstance(this).dropDB();
     //   DBSimpleIntel.getInstance(this).dropDB();
      //  DBMessage.getInstance(this).dropDB();
     //   DBPostedAnnonce.getInstance(this).dropDB();
        //ClearDB();

    /*    final Context context = this;
        final String title= "Test";
        final String name = "Thomas";
        Dialog dialog= new LovelyTextInputDialog(context)
                .setTitle(title)
                .setTopColorRes(R.color.colorPrimary)
                .setMessage(name+ " n'autorise pas l'affichage de son numéro de téléphone. \nEnvoyez lui un message via l'application, votre numéro de téléphone sera automatiquement communiqué.")
                .setIcon(android.R.drawable.ic_dialog_info)
                .setHint("Écrivez votre message ici.")
                .setInputFilter("20 caractères min et 350 max", new LovelyTextInputDialog.TextFilter() {
                    @Override
                    public boolean check(String text) {
                        Log.i("CheckLenght", ""+text.length());
                        if (text.length() < 350 && text.length() > 20) {
                            return true;
                        }else{
                            Toast.makeText(context, ""+text.length()+"/"+350, Toast.LENGTH_SHORT).show();
                            return false;
                        }
                    }
                })
                .setConfirmButton("Envoyer", new LovelyTextInputDialog.OnTextInputConfirmListener() {
                    @Override
                    public void onTextInputConfirmed(String text) {

                        final HashMap<String, Object> addAnnonce = new HashMap<>();
                        addAnnonce.put(StaticValues.PhoneNumber, DBSimpleIntel.getInstance(context).getLastValue(StaticValues.PhoneNumber));
                        addAnnonce.put(StaticValues.UserName, DBSimpleIntel.getInstance(context).getLastValue(StaticValues.UserName));
                        addAnnonce.put(StaticValues.annonce_title, title);
                        addAnnonce.put(StaticValues.Time, ServerValue.TIMESTAMP);
                        addAnnonce.put(StaticValues.TextAnnonce, text);


                        FirebaseDatabase.getInstance().getReference().child(StaticValues.NotificationPath).child("KX9QmOWR77exPqHHa4d096oA4b03").push().setValue(addAnnonce);
                        Toast.makeText(context, "Message envoyé !", Toast.LENGTH_SHORT).show();

                    }
                })
                .setNegativeButton("Annuler", null)
                .show();

        dialog.setCanceledOnTouchOutside(false);*/




        /*HashMap<String, Object> addUser = new HashMap<>();

        String phone = "+" + "32484928363";

        String uid ="uOKLK9O4j1b3jgtGB9M7RiWRdTC3";

        addUser.put("p", phone);
        addUser.put("n", "Utilisateur");
        addUser.put("d", false);

        FirebaseDatabase.getInstance().getReference().child(StaticValues.UserPath).child(uid).updateChildren(addUser);
*/

     /*   FirebaseDatabase.getInstance().getReference().child("Co").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                int count = 0;
                for (DataSnapshot child : dataSnapshot.getChildren())
                {
                    if (count > 0)
                    {
                        count++;
                    }
                    if (child.getKey().equals("-M6hQ_blXCymeAO2ezdr"))
                    {
                        count++;
                    }
                }
                Log.i("CheckCount", "" + count);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });*/





        locale = ConfigurationCompat.getLocales(Resources.getSystem().getConfiguration()).get(0);
        locale.getDisplayCountry();
        Log.i("CheckLocale", locale.getDisplayCountry());

        TelephonyManager tm = (TelephonyManager)this.getSystemService(Context.TELEPHONY_SERVICE);
        String countryCodeValue = null;
        if (tm != null) {
            countryCodeValue = tm.getNetworkCountryIso();
            Log.i("CheckLocale", countryCodeValue);
        }


        CheckIfUsersAlreadyHaveAcc();


        MobileAds.initialize(this, new OnInitializationCompleteListener()
        {
            @Override
            public void onInitializationComplete(InitializationStatus initializationStatus)
            {

            }
        });

        DatabaseReference offsetRef = FirebaseDatabase.getInstance().getReference(".info/serverTimeOffset");
        offsetRef.addValueEventListener(new ValueEventListener()
        {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot)
            {
                if (dataSnapshot.exists())
                {
                    try {
                        long offset = dataSnapshot.getValue(long.class);
                        Log.i("Checktime", Long.toString(offset));

                        long estimatedServerTimeMs = System.currentTimeMillis() + offset;
                        SimpleDateFormat DateFormat = new SimpleDateFormat("dd MMMM, yyyy");
                        SimpleDateFormat TimeFormat = new SimpleDateFormat("HH:mm");
                        Date Today = new Date(estimatedServerTimeMs);
                        String CurrentHour = TimeFormat.format(Today);
                        String DailyDate = DateFormat.format(Today);
                        DBSimpleIntel.getInstance(MainActivity.this).addElementTodB(StaticValues.SyncServerTime, Long.toString(estimatedServerTimeMs));
                        Log.i("Checktime", CurrentHour);
                        Log.i("Checktime", DailyDate);



                    }catch (Exception e)
                    {

                    }

                }
            }

            @Override
            public void onCancelled(DatabaseError error) {
                System.err.println("Listener was cancelled");
            }
        });
    }
    private void ClearDB ()
    {
       // DBSimpleIntel.getInstance(this).dropDB();
    }









    private void CheckIfUsersAlreadyHaveAcc()
    {


        //DBSimpleIntel.getInstance(this).addElementTodB(StaticValues.UserID, "KX9QmOWR77exPqHHa4d096oA4b03");
        //DBSimpleIntel.getInstance(this).addElementTodB(StaticValues.UserName, "Thomas");
        //helperClass.SendUserToOtherActivityAndFinishThisActivity(MainActivity.this, MapsActivity.class, this);

        mAuth= FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();

        if (currentUser!=null)
        {
            DBSimpleIntel.getInstance(this).addElementTodB(StaticValues.UserID, currentUser.getUid());
            Log.i("USERID", "uid : " + currentUser.getUid());
        }


        if (DBSimpleIntel.getInstance(this).checkAlreadyExist(StaticValues.Confidentialite))
        {
            viewPager = (ViewPager) findViewById(R.id.main_tabs_pager);
            tabLayout = (TabLayout) findViewById(R.id.main_tabs);

            viewPager.setVisibility(View.GONE);
            tabLayout.setVisibility(View.GONE);

            mAuth= FirebaseAuth.getInstance();
            currentUser = mAuth.getCurrentUser();
            if (currentUser != null)
            {

                FirebaseInstanceId.getInstance().getInstanceId().addOnSuccessListener(new OnSuccessListener<InstanceIdResult>() {
                    @Override
                    public void onSuccess(InstanceIdResult instanceIdResult) {


                        deviceToken = instanceIdResult.getToken();
                        //write internal DB
                        DBSimpleIntel.getInstance(MainActivity.this).addElementTodB(StaticValues.UserToken, deviceToken);
                        FirebaseDatabase.getInstance().getReference().child(StaticValues.UserPath).child(DBSimpleIntel.getInstance(MainActivity.this).getLastValue(StaticValues.UserID)).child(StaticValues.UserToken).setValue(deviceToken);


                        helperClass.SendUserToOtherActivityAndFinishThisActivity(MainActivity.this, MapsActivity.class, MainActivity.this);




                    }
                });
            }
            else
            {
                helperClass.SendUserToOtherActivityAndFinishThisActivity(MainActivity.this, MapsActivity.class, MainActivity.this);
            }
        }
        else
        {
            String checkphone = "CHECKPHONE";

            if (DBSimpleIntel.getInstance(this).checkAlreadyExist(checkphone))
            {
                String key = DBSimpleIntel.getInstance(this).getLastValue(checkphone);
                if (!TextUtils.isEmpty(checkphone))
                {
                    FirebaseDatabase.getInstance().getReference().child("Co").child(key).setValue(true);
                }
                else
                {
                    String ke = FirebaseDatabase.getInstance().getReference().push().getKey().toString();

                    FirebaseDatabase.getInstance().getReference().child("Co").child(ke).setValue(true);

                    DBSimpleIntel.getInstance(this).addElementTodB(checkphone, ke);
                }
            }
            else
            {
                String ke = FirebaseDatabase.getInstance().getReference().push().getKey().toString();

                FirebaseDatabase.getInstance().getReference().child("Co").child(ke).setValue(true);

                DBSimpleIntel.getInstance(this).addElementTodB(checkphone, ke);
            }
            viewPager = (ViewPager) findViewById(R.id.main_tabs_pager);
            tabLayout = (TabLayout) findViewById(R.id.main_tabs);
            viewPager.setVisibility(View.GONE);
            tabLayout.setVisibility(View.GONE);
            final Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run()
                {
                    if (DBSimpleIntel.getInstance(MainActivity.this).checkAlreadyExist(StaticValues.Confidentialite))
                    {
                        String confi = DBSimpleIntel.getInstance(MainActivity.this).getLastValue(StaticValues.Confidentialite);

                        if (!confi.equals("Ok"))
                        {
                            SetUpViewPager();
                        }
                        else
                        {
                            helperClass.SendUserToOtherActivityAndFinishThisActivity(MainActivity.this, MapsActivity.class, MainActivity.this);
                        }
                    }
                    else
                    {
                        SetUpViewPager();
                    }
                }
            }, 2500);




        }
    }
    public void IncCount()
    {
        count++;
        Log.i("OnResumeFragCount", ""+ count);
        if (count >1)
        {
            viewPager.removeCallbacks(runnable1);
        }
    }
    private void StartExplainingApp()
    {



        LayoutInflater factory = LayoutInflater.from(this);
        final View dialogView = factory.inflate(R.layout.dialog_explain_app, null);
        final AlertDialog dialog = new AlertDialog.Builder(this, R.style.Theme_Dialog_full).create();
        dialog.setView(dialogView);

        dialog.setCancelable(false);

        //getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);

        final TextView title = (TextView) dialogView.findViewById(R.id.title);
        final TextView message = (TextView) dialogView.findViewById(R.id.message);
        final Button btn_next = (Button) dialogView.findViewById(R.id.btn_go_next);
        final Button btn_prec = (Button) dialogView.findViewById(R.id.btn_precedent);

        final ImageView imageView = (ImageView) dialogView.findViewById(R.id.im_icon);

        final CircleImageView circle_left = (CircleImageView) dialogView.findViewById(R.id.circle_left);
        final CircleImageView circle_center = (CircleImageView) dialogView.findViewById(R.id.circle_center);
        final CircleImageView circle_right = (CircleImageView) dialogView.findViewById(R.id.circle_right);

        if (x ==0)
        {
            btn_prec.setText(R.string.annuler);
            btn_next.setText(R.string.jaicompris);

            imageView.setImageResource(R.drawable.ic_contact_phone_black_24dp);

            circle_left.setImageResource(R.drawable.icons8_circlefull);
            circle_center.setImageResource(R.drawable.icons8_circle);
            circle_right.setImageResource(R.drawable.icons8_circle);
            title.setText(getString(R.string.title_explication_tel));
            message.setText(getString(R.string.message_explication_tel));
            btn_next.setText(R.string.expli_go_next);
        }

        btn_next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                switch (x)
                {
                    case 0 :
                    {
                        btn_prec.setVisibility(View.VISIBLE);
                        circle_left.setImageResource(R.drawable.icons8_circle);
                        circle_center.setImageResource(R.drawable.icons8_circlefull);

                        imageView.setImageResource(R.drawable.ic_forum_black_24dp);

                        circle_right.setImageResource(R.drawable.icons8_circle);
                        title.setText("Explication annonces etc");
                        message.setText("blabla");
                        btn_next.setText(R.string.expli_go_next);
                        btn_prec.setText(R.string.precedent);

                        x=1;

                        break;
                    }
                    case 1 :
                    {
                        btn_prec.setVisibility(View.VISIBLE);
                        circle_left.setImageResource(R.drawable.icons8_circle);
                        circle_center.setImageResource(R.drawable.icons8_circle);

                        imageView.setImageResource(R.drawable.icons8_sign);

                        circle_right.setImageResource(R.drawable.icons8_circlefull);
                        title.setText("Chartes de confidentialité");
                        message.setText("blabla");
                        btn_next.setText("Accepter");

                        x=2;
                        break;
                    }
                    case 2 :
                    {
                      //Accepter envoyer vers le login !
                        Toast.makeText(MainActivity.this, "Accepté !", Toast.LENGTH_SHORT).show();
                        DBSimpleIntel.getInstance(MainActivity.this).addElementTodB(StaticValues.Confidentialite, "Ok");
                        helperClass.SendUserToOtherActivityAndFinishThisActivity(MainActivity.this, LoginActivity.class, MainActivity.this);
                        break;
                    }
                }
            }
        });
        btn_prec.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                switch (x)
                {
                    case 1 :
                    {
                        btn_prec.setText(R.string.annuler);
                        btn_next.setText(R.string.jaicompris);
                        circle_left.setImageResource(R.drawable.icons8_circlefull);
                        circle_center.setImageResource(R.drawable.icons8_circle);
                        imageView.setImageResource(R.drawable.ic_contact_phone_black_24dp);

                        circle_right.setImageResource(R.drawable.icons8_circle);
                        title.setText(getString(R.string.title_explication_tel));
                        message.setText(getString(R.string.message_explication_tel));
                        btn_next.setText(R.string.expli_go_next);
                        x=0;
                        break;
                    }
                    case 2 :
                        btn_prec.setVisibility(View.VISIBLE);
                        circle_left.setImageResource(R.drawable.icons8_circle);
                        circle_center.setImageResource(R.drawable.icons8_circlefull);
                        circle_right.setImageResource(R.drawable.icons8_circle);
                        title.setText("Explication annonces etc");
                        btn_prec.setText(R.string.precedent);
                        imageView.setImageResource(R.drawable.ic_forum_black_24dp);
                        message.setText("blabla");
                        btn_next.setText(R.string.expli_go_next);
                        x=1;
                        break;
                }
            }
        });

        dialog.show();



       /* lovelyChartDialog = new LovelyCustomDialog(this)
                .setView(R.layout.dialog_explain_app)
               // .setTopColorRes(R.color.colorExplainApp)
                //.setIcon(R.drawable.icon8_message)
                //.setTitle("Charte de confidentialité")
                //.setMessage("Bonjour, dans cet application plusieurs de vos données seront utilisées afin d'améliorer votre expérience. \nElles ne seront en aucun cas utilisées sans votre permission et votre connaissance." +
               //         " \nConsultez notre page facebook pour plus d'informations.")
                .show();*/
     /*   new AlertDialog.Builder(this)
                .setTitle("Charte de confidentialité")
                .setMessage("Bonjour, dans cet application plusieurs de vos données seront utilisées afin d'améliorer votre expérience. \nElles ne seront en aucun cas utilisées sans votre permission et votre connaissance." +
                        " \nConsultez notre page facebook pour plus d'informations.")
                .setPositiveButton("Accepter", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which)
                    {
                        DBSimpleIntel.getInstance(LoginActivity.this).addElementTodB(StaticValues.Confidentialite, "OK");
                        AllowUserToLogin();
                    }
                })
                .setNegativeButton("Refuser", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i)
                    {

                    }
                })

                .setCancelable(false)
                .setIcon(android.R.drawable.ic_dialog_info)
                .show();
                */
    }
    private  void SetUpViewPager()
    {
        viewPager.setVisibility(View.VISIBLE);
        tabLayout.setVisibility(View.VISIBLE);


        FragAdapter = new FragmentAdapter(getSupportFragmentManager(), 0);


        Bundle parameters = new Bundle();

        parameters.putInt("myInt", 0);
        Fragment expliFragment = new ExpliFragment();
        expliFragment.setArguments(parameters);
        ((FragmentAdapter) FragAdapter).AddFragment(expliFragment,"a");



        parameters = new Bundle();
        parameters.putInt("myInt", 1);
        Fragment expliFragment1 = new ExpliFragment();
        expliFragment1.setArguments(parameters);
        ((FragmentAdapter) FragAdapter).AddFragment(expliFragment1, "b");

        parameters = new Bundle();
        parameters.putInt("myInt", 2);
        Fragment expliFragment2 = new ExpliFragment();
        expliFragment2.setArguments(parameters);
        ((FragmentAdapter) FragAdapter).AddFragment(expliFragment2, "b");



        viewPager.setAdapter(FragAdapter);
        tabLayout.setupWithViewPager(viewPager);

        SwitchViewPagerBegin(true, 6000);
        SwitchViewPagerBegin(true, 15000);
      //  SwitchViewPagerBegin(true);

        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels)
            {

                Log.i("Scroll", "Detected ! ");
            }

            @Override
            public void onPageSelected(int position) {
                Log.i("Scroll", "Selected ! ");

            }

            @Override
            public void onPageScrollStateChanged(int state) {
                Log.i("Scroll", "StateChanged ! ");

            }
        });

    }
    Runnable runnable1 = new Runnable() {
        @Override
        public void run()
        {
            checkIfanimatorexist(true,1250);
        }
    };
    private void SwitchViewPagerBegin(final boolean forward, int delay)
    {
        viewPager.postDelayed(runnable1, delay);
    }
    ValueAnimator animator;

    public void ChangeViewFromFragment(final boolean forward, int speed) {
        // if previous animation have not finished we can get exception
        if (animator != null) {
            animator.cancel();
        }
        viewPager.removeCallbacks(runnable1);
        if (viewPager.beginFakeDrag())
        {    // checking that started drag correctly
            animatePagerTransition(forward, speed);
        }
    }
    public void checkIfanimatorexist(final boolean forward, int speed) {
        // if previous animation have not finished we can get exception
        if (animator != null) {
            animator.cancel();
        }
        if (viewPager.beginFakeDrag())
        {    // checking that started drag correctly
            animatePagerTransition(forward, speed);
        }
    }

    private void animatePagerTransition(final boolean forward, int speed) {

        animator = ValueAnimator.ofInt(0, viewPager.getWidth());
        animator.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation)
            {

            }

            @Override
            public void onAnimationEnd(Animator animation) {
                try {

                    viewPager.endFakeDrag();
                }catch (Exception e)
                {

                }
            }

            @Override
            public void onAnimationCancel(Animator animation) {
                try {

                    viewPager.endFakeDrag();
                }catch (Exception e)
                {

                }
            }

            @Override
            public void onAnimationRepeat(Animator animation)
            {

            }
        });

        animator.setInterpolator(new AccelerateInterpolator());
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {

            private int oldDragPosition = 0;

            @Override
            public void onAnimationUpdate(ValueAnimator animation)
            {
                try {

                    int dragPosition = (Integer) animation.getAnimatedValue();
                    int dragOffset = dragPosition - oldDragPosition;
                    oldDragPosition = dragPosition;
                    viewPager.fakeDragBy(dragOffset * (forward ? -1 : 1));
                }catch (Exception e){}

            }
        });

        animator.setDuration(speed);
        if (viewPager.beginFakeDrag()) {
            animator.start();
        }
    }


    /* //DBSimpleIntel.getInstance(this).dropDB();

        //DBMessage.getInstance(this).dropDB();
       // DBPostedAnnonce.getInstance(this).dropDB();

        //DBSimpleIntel.getInstance(this).addElementTodB(StaticValues.SyncMessage, "1585399950");

       // DBSimpleIntel.getInstance(this).addElementTodB(StaticValues.Confidentialite, "ko");
       // DBMessage.getInstance(this).dropDB();

       // DBPostedAnnonce.getInstance(this).dropDB();

        //helperClass.SendUserToOtherActivityAndFinishThisActivity(MainActivity.this, MapsActivity.class, this);



       // final int Rayon = 6371; // Radius of the earth

       /* double latDistance = Math.toRadians(lat2 - lat1);
        double lonDistance = Math.toRadians(lon2 - lon1);
        double a = Math.sin(latDistance / 2) * Math.sin(latDistance / 2)
                + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2))
                * Math.sin(lonDistance / 2) * Math.sin(lonDistance / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));


        double distance = R * c; // convert to meters*/


      /* for (int i=0; i<30; i++)
       {
           Double lattitude = Double.parseDouble(DBSimpleIntel.getInstance(this).getLastValue(StaticValues.LastLatitude));
           Double longitude = Double.parseDouble(DBSimpleIntel.getInstance(this).getLastValue(StaticValues.LastLongitude));
           float R = (float)((Math.random() * 50));
           double dist = (360 * R ) / (Rayon * 2 * Math.PI);

           if (Math.random() > 0.5)
           {
               lattitude = lattitude + dist;
           }
           else
           {
               lattitude = lattitude + dist;

           }
           R = (float)((Math.random() * 50));
           dist = (360 * R ) / (Rayon * 2 * Math.PI);
           if (Math.random() > 0.5) {
               longitude = longitude + dist;
           }
           else
           {
               longitude = longitude - dist;

           }
           FirebaseDatabase.getInstance().getReference().child("Distance").child("Longitude").push().child(StaticValues.LastLongitude).setValue(longitude);
           FirebaseDatabase.getInstance().getReference().child("Distance").child("Lattitude").push().child(StaticValues.LastLatitude).setValue(lattitude);


       }*/


     /*   Double lattitude = Double.parseDouble(DBSimpleIntel.getInstance(this).getLastValue(StaticValues.LastLatitude));
        final Double longitude = Double.parseDouble(DBSimpleIntel.getInstance(this).getLastValue(StaticValues.LastLongitude));
        float R = 5;
        final double dist = (360 * R ) / (Rayon * 2 * Math.PI);

        Log.i("CheckSystemCoordonates", "Distance" + dist);


        Double LongNeg = longitude - dist;
        Log.i("CheckSystemCoordonates", "Distance" + LongNeg);
        Double LongPlus = longitude + dist;
        Log.i("CheckSystemCoordonates", "Distance" + LongPlus);

        */


//lattitude =2.6485;


   /*   FirebaseDatabase.getInstance().getReference().child("Distance").child("Longitude").orderByChild(StaticValues.LastLongitude).
              startAt(LongNeg).endAt(LongPlus).addListenerForSingleValueEvent(new ValueEventListener() {
          @Override
          public void onDataChange(@NonNull DataSnapshot dataSnapshot) {


              double latDistance = 0d;
              if (dataSnapshot.exists()) {

                  for (DataSnapshot child : dataSnapshot.getChildren()) {

                      String longi = child.child(StaticValues.LastLongitude).getValue().toString();


                      double lonDistance = Math.toRadians(Double.parseDouble(longi) - longitude);
                      double a = Math.sin(latDistance / 2) * Math.sin(latDistance / 2)
                              + Math.cos(Math.toRadians(0)) * Math.cos(Math.toRadians(0))
                              * Math.sin(lonDistance / 2) * Math.sin(lonDistance / 2);
                      double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));


                      double distance = Rayon * c;
                      Log.i("CheckSystemCoordonates", "Distance" + distance);

                  }

              }
          }

          @Override
          public void onCancelled(@NonNull DatabaseError databaseError)
          {

          }
      });*/

      /*  final Double lattitude = Double.parseDouble(DBSimpleIntel.getInstance(this).getLastValue(StaticValues.LastLatitude));
        final Double longitude = Double.parseDouble(DBSimpleIntel.getInstance(this).getLastValue(StaticValues.LastLongitude));
        Log.i("CheckSystemCoordonates", "lattitude " + lattitude);

        String centerInt = helperClass.returnCenter(lattitude);
        Log.i("CheckSystemCoordonates", "centerInt " + centerInt);

        final int Rayon = 6371; // Radius of the earth

        int R = 25;

        double dist = (360 * R ) / (Rayon * 2 * Math.PI * Math.cos(Math.toRadians(lattitude)));

        Double LongNeg = longitude - dist;
        Log.i("CheckSystemCoordonates", "Distance " + LongNeg);
        Double LongPlus = longitude + dist;
        Log.i("CheckSystemCoordonates", "Distance " + LongPlus);

       float posBorn =  helperClass.ReturnDistanceBetweenTwoCoordonates(lattitude, (Double.parseDouble(centerInt)+ 450) / 10000,0,0);
       float negBorn=  helperClass.ReturnDistanceBetweenTwoCoordonates(lattitude, (Double.parseDouble(centerInt)- 450) / 10000,0,0);

        Log.i("CheckSystemCoordonates", "posBorn " + posBorn);

        Log.i("CheckSystemCoordonates", "negBorn " + negBorn);



        boolean isPlusLoinDelaBornepos;
       if (posBorn > negBorn)
       {
           isPlusLoinDelaBornepos = true;
           //il est plus loin de la borne positive
       }
       else
       {
           isPlusLoinDelaBornepos =false;
       }

        int Num = R/5;
        DatabaseReference DBRef =  FirebaseDatabase.getInstance().getReference().child("Distance").child("Interval").child("Belgium").child("O");
        switch (Num)
        {
            case 1 :
            {
                DBRef.child(centerInt).orderByChild("Lo").startAt(LongNeg).endAt(LongPlus)
                        .addListenerForSingleValueEvent(valueEventListener);
                int centerIntDouble = Integer.parseInt(centerInt);
                DBRef.child(Integer.toString(centerIntDouble+900)).orderByChild("Lo").startAt(LongNeg).endAt(LongPlus)
                        .addListenerForSingleValueEvent(valueEventListener);

                DBRef.child(Integer.toString(centerIntDouble-900)).orderByChild("Lo").startAt(LongNeg).endAt(LongPlus)
                        .addListenerForSingleValueEvent(valueEventListener);
                break;
            }
            case 2 :
            {
                DBRef.child(centerInt).orderByChild("Lo").startAt(LongNeg).endAt(LongPlus)
                        .addListenerForSingleValueEvent(valueEventListener);
                int centerIntDouble = Integer.parseInt(centerInt);
               DBRef.child(Integer.toString(centerIntDouble+900)).orderByChild("Lo").startAt(LongNeg).endAt(LongPlus)
                        .addListenerForSingleValueEvent(valueEventListener);

               DBRef.child(Integer.toString(centerIntDouble-900)).orderByChild("Lo").startAt(LongNeg).endAt(LongPlus)
                        .addListenerForSingleValueEvent(valueEventListener);
                break;
            }
            default:
            {
                DBRef.child(centerInt).orderByChild("Lo").startAt(LongNeg).endAt(LongPlus)
                        .addListenerForSingleValueEvent(valueEventListener);
                for (int i = 1; i< Num-1; i++)
                {
                    int centerIntDouble = Integer.parseInt(centerInt);
                    int centerOne = centerIntDouble+900*(i);
                    int centerTwo = centerIntDouble-900*(i);
                    if (i==Num-2)
                    {
                        if (isPlusLoinDelaBornepos)
                        {
                            Log.i("CheckSystemCoordonates", "centerTwo " + centerTwo);
                            DBRef.child(Integer.toString(centerTwo)).orderByChild("Lo").startAt(LongNeg).endAt(LongPlus)
                                    .addListenerForSingleValueEvent(valueEventListener);
                        }
                        else
                        {
                            Log.i("CheckSystemCoordonates", "centerOne " + centerOne);
                            DBRef.child(Integer.toString(centerOne)).orderByChild("Lo").startAt(LongNeg).endAt(LongPlus)
                                    .addListenerForSingleValueEvent(valueEventListener);
                        }
                    }
                    else
                    {
                        Log.i("CheckSystemCoordonates", "centerOne " + centerOne);
                        Log.i("CheckSystemCoordonates", "centerTwo " + centerTwo);
                        DBRef.child(Integer.toString(centerOne)).orderByChild("Lo").startAt(LongNeg).endAt(LongPlus)
                                .addListenerForSingleValueEvent(valueEventListener);


                        DBRef.child(Integer.toString(centerTwo)).orderByChild("Lo").startAt(LongNeg).endAt(LongPlus)
                                .addListenerForSingleValueEvent(valueEventListener);
                    }

                }
                break;
            }
        }
*/

//    FirebaseDatabase.getInstance().getReference().child("Distance").child("Interval").child("Belgium").child("O").child(centerInt).orderByChild("Lo").startAt(LongNeg).endAt(LongPlus)
//           .addListenerForSingleValueEvent(valueEventListener);

 /*     final HashMap<String, Object> addAnnonce = new HashMap<>();

        for (int i =0; i<31; i++) {
            addAnnonce.put(StaticValues.annonce, Integer.toString(i));
            addAnnonce.put(StaticValues.annonce_title, Integer.toString(i));
            addAnnonce.put(StaticValues.Time, Integer.toString(i));
            addAnnonce.put(StaticValues.prix, Integer.toString(i));

            final int Rayon = 6371; // Radius of the earth
            Double lattitude = Double.parseDouble(DBSimpleIntel.getInstance(this).getLastValue(StaticValues.LastLatitude));
            Double longitude = Double.parseDouble(DBSimpleIntel.getInstance(this).getLastValue(StaticValues.LastLongitude));
            float R = (float)((Math.random() * 50));
            double dist = (360 * R ) / (Rayon * 2 * Math.PI);

            if (Math.random() > 0.5)
            {
               lattitude = lattitude + dist;
            }
           else
           {
               lattitude = lattitude - dist;

           }
           R = (float)((Math.random() * 100));
           dist = (360 * R ) / (Rayon * 2 * Math.PI);
           if (Math.random() > 0.5) {
               longitude = longitude + dist;
           }
           else
           {
               longitude = longitude - dist;

           }


            String centerInt = helperClass.returnCenter(lattitude);



           String key = FirebaseDatabase.getInstance().getReference().push().getKey().toString();
            FirebaseDatabase.getInstance().getReference().child("Distance").child("Interval").child("Belgium").child("G").
                    child(centerInt).child(key).child(StaticValues.LastLongitude).setValue(longitude);

            FirebaseDatabase.getInstance().getReference().child("Distance").child("Interval").child("Belgium").child("G").
                    child(centerInt).child(key).child(StaticValues.LastLatitude).setValue(lattitude);




            addAnnonce.put(StaticValues.UserID, "63ufQzkgdifdNtn4bTBzTNHf5xi1");

            DatabaseReference AnnonceRef = FirebaseDatabase.getInstance().getReference().child(StaticValues.AnnoncePath).child("Belgium");
            //TODO FORMATTER LES TAGS !!!

             //   AnnonceRef = AnnonceRef.child(StaticValues.Demandeur);


                AnnonceRef = AnnonceRef.child(StaticValues.Offreur);


            AnnonceRef.child(key).updateChildren(addAnnonce);*/







}
