package com.isma.soli.ad.SeekAnnonce;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.core.content.PermissionChecker;
import androidx.core.os.ConfigurationCompat;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

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
import android.location.Address;
import android.location.Geocoder;
import android.media.Image;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.signature.ObjectKey;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.isma.soli.ad.Image.FullScreenImageActivity;
import com.isma.soli.ad.InternalDatabase.DBMessageSent;
import com.isma.soli.ad.InternalDatabase.DBSimpleIntel;
import com.isma.soli.ad.Model.AnnonceClass;
import com.isma.soli.ad.Model.AnnonceIDClass;
import com.isma.soli.ad.Model.MessageSentClass;
import com.isma.soli.ad.PostAnnonce.CreateAnnonceActivity;
import com.isma.soli.ad.R;
import com.isma.soli.ad.Util.HelperClass;
import com.isma.soli.ad.Util.StaticValues;
import com.nabinbhandari.android.permissions.PermissionHandler;
import com.nabinbhandari.android.permissions.Permissions;
import com.yarolegovich.lovelydialog.LovelyChoiceDialog;
import com.yarolegovich.lovelydialog.LovelyCustomDialog;
import com.yarolegovich.lovelydialog.LovelyStandardDialog;
import com.yarolegovich.lovelydialog.LovelyTextInputDialog;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;

public class SeekAnnonceActivity extends AppCompatActivity {


    private ProgressDialog loadingBar;
    private List<AnnonceClass> annonceClassList = new ArrayList<>();
    ListAnnonceAdapter adapter;
    RecyclerView recyclerHelp;
    Context context;
    String currentuserID;
    HelperClass helperClass = new HelperClass();
    private Toolbar toolbar;
    Boolean isAlreadyTrigged = false;
    long count=0;
    long countCheck = 0;

    String lastKey = "";
    boolean isLoading = true;
    ContextWrapper cw;
    File directory;




    int countStartLoading =0;
    int countAllFailAnnoce=0;
    int countStartQueryingAnnonce =0;
    int countFailRestartLoading = 0;
    int countInFirebaseAnnonce=0;

    /*** Value needed to query ***/

    String query="";
    int ChosenRadius = 5;
    int MaxRadius= 30;
    int numberOfLoad=0;
    int RadiusStep = 5;
    int loadingNumber =5;

    private StorageReference downloadRef;

    /****************/

    List <AnnonceIDClass> annonceIDClassList = new ArrayList<>();
    List <String> ListOfCenterToCheck = new ArrayList<>();
    HashSet<String> ListOfCenter = new HashSet<>();
    List <String> ListOfLatLng = new ArrayList<>();
    HashSet<String> ListOfCenterToDelete = new HashSet<>();

    private void ClearEveryting()
    {
        isAlreadyTrigged = false;
        count=0;
        countCheck = 0;
        isLoading = true;
        countStartLoading = 0;
        countAllFailAnnoce = 0;
        countStartQueryingAnnonce =0;
        countFailRestartLoading = 0;
        countInFirebaseAnnonce=0;
        numberOfLoad=0;
        annonceClassList.clear();
        annonceIDClassList.clear();
        ListOfCenter.clear();
        ListOfCenterToCheck.clear();
        adapter.notifyDataSetChanged();
        ListOfCenterToDelete.clear();
        ListOfLatLng.clear();
    }
    ValueEventListener annonceListener = new ValueEventListener() {
        @Override
        public void onDataChange(@NonNull DataSnapshot dataSnapshot)
        {
            AddElementToAnnonceList(dataSnapshot);
        }

        @Override
        public void onCancelled(@NonNull DatabaseError databaseError) {

        }
    };
    ValueEventListener getAnnonceIDAroundListener = new ValueEventListener() {
        @Override
        public void onDataChange(@NonNull DataSnapshot dataSnapshot)
        {
            if (dataSnapshot.exists())
            {


                for (int i =0; i< ListOfCenterToCheck.size(); i++)
                {
                    if (!dataSnapshot.getKey().equals(query+"l")) {

                        if (!ListOfCenterToCheck.get(i).equals(StaticValues.SeekLoca)) {

                            int keyList = Integer.parseInt(ListOfCenterToCheck.get(i));
                            int keyInterval = Integer.parseInt(dataSnapshot.getKey());
                            if (keyInterval == keyList) {
                                ListOfCenterToCheck.remove(i);
                                break;
                            }
                        }
                    }
                    else
                    {
                        if (ListOfCenterToCheck.get(i).equals(StaticValues.SeekLoca))
                        {
                            ListOfCenterToCheck.remove(i);
                            break;
                        }
                    }
                }
                countCheck = dataSnapshot.getChildrenCount();
                for (DataSnapshot child : dataSnapshot.getChildren())
                {
                    countStartQueryingAnnonce++;

                    Double lat2 = Double.parseDouble(child.child(StaticValues.LastLatitude).getValue().toString());
                    Double lon2 = Double.parseDouble(child.child(StaticValues.LastLongitude).getValue().toString());

                    final Double lattitude = Double.parseDouble(DBSimpleIntel.getInstance(context).getLastValue(StaticValues.LastLatitude));
                    final Double longitude = Double.parseDouble(DBSimpleIntel.getInstance(context).getLastValue(StaticValues.LastLongitude));

                    float distance = helperClass.ReturnDistanceBetweenTwoCoordonates(lattitude, lat2 , longitude, lon2);
                    Log.i("CheckSystemCoordonates", "DistanceB2points " + child.getKey() + " " + distance );


                    String datakey = dataSnapshot.getKey();

                    if (datakey.equals(query+"l"))
                    {
                        DBSimpleIntel.getInstance(context).addElementTodB(child.getKey()+ StaticValues.SeekLoca, "ok");
                    }


                    if (distance <= ChosenRadius || (datakey.equals(query+"l"))) { //&& different de locaPost

                        AddElementToListOfIDAnnonce(child, distance);
                        ListOfCenterToDelete.add(child.getKey() + ";" +dataSnapshot.getKey());
                        ListOfLatLng.add(child.getKey() + ";" + lat2 + ";" + lon2);
                    }
                    else
                    {
                        for (int i =0; i< annonceIDClassList.size(); i++)
                        {
                            if (annonceIDClassList.get(i).equals(child.getKey()))
                            {
                                annonceIDClassList.remove(i);
                                break;
                            }
                        }
                    }

                    if (ListOfCenterToCheck.size() == 0 && (countCheck == countStartQueryingAnnonce))
                    {
                        //Query !
                        countStartQueryingAnnonce = 0;
                        SortListByDistance();
                        StartGettingAnnonce();
                        ListOfCenter.clear();
                        Log.i("CheckQuery", "Got all, can start querying ! ");
                    }
                    else
                    {
                        if (countCheck == countStartQueryingAnnonce) {
                            countCheck = 0;
                            countStartQueryingAnnonce = 0;
                        }
                    }

                }

            }
            else
            {

                for (int i =0; i< ListOfCenterToCheck.size(); i++)
                {
                    if (!dataSnapshot.getKey().equals(query+"l")) {

                        if (!ListOfCenterToCheck.get(i).equals(StaticValues.SeekLoca)) {
                            int keyInterval = Integer.parseInt(dataSnapshot.getKey());
                            int keyList = Integer.parseInt(ListOfCenterToCheck.get(i));
                            if (keyInterval == keyList) {
                                ListOfCenterToCheck.remove(i);
                                if (ListOfCenterToCheck.size() == 0) // On finit par un interval qui ne contient pas de data, il faut start la query !
                                {
                                    //Query !
                                    SortListByDistance();
                                    StartGettingAnnonce();
                                    countStartQueryingAnnonce = 0;
                                    ListOfCenter.clear();
                                    Log.i("CheckQuery", "Got all, can start querying ! ");
                                }
                                break;
                            }
                        }
                    }
                    else
                    {
                        if (ListOfCenterToCheck.get(i).equals(StaticValues.SeekLoca))
                        {
                            ListOfCenterToCheck.remove(i);
                            if (ListOfCenterToCheck.size() == 0) // On finit par un interval qui ne contient pas de data, il faut start la query !
                            {
                                //Query !
                                SortListByDistance();
                                StartGettingAnnonce();
                                countStartQueryingAnnonce = 0;
                                ListOfCenter.clear();
                                Log.i("CheckQuery", "Got all, can start querying ! ");
                            }
                            break;
                        }
                    }
                }
            }
        }

        @Override
        public void onCancelled(@NonNull DatabaseError databaseError) {

        }
    };
    private void AucuneAnnonce()
    {
        String text;
        if (MaxRadius > ChosenRadius)
        {
            text = "Augmenter le rayon";
        }
        else
        {
            text = "Réessayer plus tard";
        }
        new LovelyStandardDialog(this, LovelyStandardDialog.ButtonLayout.VERTICAL)
                .setTopColorRes(R.color.colorPrimary)
                .setIcon(android.R.drawable.ic_dialog_info)
                .setTitle("Aucune annonce")
                .setMessage("Aucune annonce n'a été trouvée à proximité de chez vous. \nNous venons de lancer l'application, il se peut qu'il n'y ait pas beaucoup d'annonces postées. \nSi vous avez un service à proposer ou une demande particulière" +
                        ", n'hésitez pas à poster une annonce.")
                .setPositiveButton(text, new View.OnClickListener()
                {
                    @Override
                    public void onClick(View v)
                    {
                        if (MaxRadius > ChosenRadius)
                        {
                            ChoiceRadius();
                        }
                        else
                        {
                            finish();
                        }
                    }
                })
                .setNegativeButton("Poster une annonce", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        helperClass.SendUserToOtherActivityAndFinishThisActivity(SeekAnnonceActivity.this, CreateAnnonceActivity.class, SeekAnnonceActivity.this);
                    }
                })
                .show();
    }
    private void StartGettingAnnonce()
    {
       // DBSimpleIntel.getInstance(context).addElementTodB(StaticValues.country, "France");
        String country = DBSimpleIntel.getInstance(context).getLastValue(StaticValues.country);
        if (TextUtils.isEmpty(country))
        {
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
        if (annonceIDClassList.size() == 0)
        {
            if (loadingBar.isShowing())
            {
                loadingBar.dismiss();
            }
            AucuneAnnonce();

        }

        if (annonceIDClassList.size() >= loadingNumber)
        {
            numberOfLoad =numberOfLoad + loadingNumber;
            if (annonceIDClassList.size() > numberOfLoad) {
                for (int i = numberOfLoad-loadingNumber; i < numberOfLoad; i++) {
                    FirebaseDatabase.getInstance().getReference().child(StaticValues.AnnoncePath).child(country).child(query).child(annonceIDClassList.get(i).getID())
                            .addListenerForSingleValueEvent(annonceListener);
                }
            }
            else
            {
                for (int i = numberOfLoad-loadingNumber; i < annonceIDClassList.size(); i++) {
                    FirebaseDatabase.getInstance().getReference().child(StaticValues.AnnoncePath).child(country).child(query).child(annonceIDClassList.get(i).getID())
                            .addListenerForSingleValueEvent(annonceListener);
                }
            }
        }

        else
        {
            for (int i =0; i< annonceIDClassList.size();i++)
            {
                FirebaseDatabase.getInstance().getReference().child(StaticValues.AnnoncePath).child(country).child(query).child(annonceIDClassList.get(i).getID())
                        .addListenerForSingleValueEvent(annonceListener);
            }
        }
    }
    private void AddElementToListOfIDAnnonce(DataSnapshot child, float distance)
    {
        AnnonceIDClass annonceIDClass = new AnnonceIDClass(child.getKey(), distance);


        for (int i =0; i< annonceIDClassList.size();i++)
        {
            if (annonceIDClassList.get(i).getID().equals(child.getKey()))
            {
                Log.i("CheckKey", "Returning!");
                return;
            }
        }


        annonceIDClassList.add(annonceIDClass);

       // SortListByDistance();

    }
    private void SortListByDistance()
    {
       /* if (annonceIDClassList.size() > 1)
        {
            boolean en_desordre = true;
            for (int i = 0; i < annonceIDClassList.size() && en_desordre; ++i)
            {
              //   Supposons le tableau ordonné
                en_desordre = false;
               //  Vérification des éléments des places j et j+1
                for (int j = 0; j < annonceIDClassList.size()-i-1; j++)
                {
                  //   Si les 2 éléments sont mal triés
                    if (annonceIDClassList.get(j).getDistance() > annonceIDClassList.get(j + 1).getDistance())
                    {
                      //   Inversion des 2 éléments
                        AnnonceIDClass annonceIDClass1 = annonceIDClassList.get(j + 1);
                        annonceIDClassList.set(j + 1, annonceIDClassList.get(j));
                        annonceIDClassList.set(j, annonceIDClass1);

                       //  Le tableau n'est toujours pas trié
                        en_desordre = false;
                    }
                }
            }
        }*/
       if (annonceIDClassList.size()>1)
       {
           for(int i=0; i < annonceIDClassList.size(); i++)
           {
               for (int j = 1; j < (annonceIDClassList.size() - i); j++)
               {
                   if (annonceIDClassList.get(j - 1).getDistance() > annonceIDClassList.get(j).getDistance())
                   {
                       //echanges des elements
                       AnnonceIDClass annonceIDClass1 = annonceIDClassList.get(j - 1);
                       annonceIDClassList.set(j - 1, annonceIDClassList.get(j));
                       annonceIDClassList.set(j, annonceIDClass1);

                   }

               }
           }

       }
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        if (item.getItemId()==R.id.call_search)
        {
            ChoiceType();
        }
        else
        {
            finish();
        }
        return super.onOptionsItemSelected(item);
    }
    FusedLocationProviderClient fusedLocationProviderClient;
    private LocationRequest locationRequest;
    private boolean checkifDataNull(DataSnapshot child, String key)
    {
        if (child.child(key).exists())
        {
            if (!TextUtils.isEmpty(child.child(key).getValue().toString()))
            {
                Log.i("checkNull", key + "Ok");
                return true;


            }
            else
            {
                Log.i("checkNull", key + "null");
              //  IncremeentCount();
                return false;
            }
        }
        else
        {
            Log.i("checkNull", key + "null");
           // IncremeentCount();
            return false;
        }
    }


    private AdView mAdView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_annonce);


      /*  mAdView = findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);*/




        //TODO REVOIR LA LOGIQUE DE CE TRUC ...  2020-03-30 01:01:46.576 8133-8133/com.isma.soli.ad I/StartQuery: Request Loca update called multiplie time wtf ...
        Init();
        ChoiceType();


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.menu_seek_annonce, menu);

        return true;
    }
    private void initScrollListener()
    {
        recyclerHelp.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
            }

            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

                LinearLayoutManager linearLayoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();

                if (!isLoading) {
                    if (linearLayoutManager != null && linearLayoutManager.findLastCompletelyVisibleItemPosition() == annonceClassList.size() - 1) {
                        //bottom of list!
                        isLoading = true;
                       loadMore();

                    }
                }
            }
        });
    }

    private void loadMore()
    {
      //  annonceClassList.add( new AnnonceClass("a", "b", "c", "f", 125f,"g", "0", "2"));
      //  adapter.notifyItemInserted(annonceClassList.size() - 1);


        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                try {
                    annonceClassList.remove(annonceClassList.size() - 1);
                    int scrollPosition = annonceClassList.size();
                    adapter.notifyItemRemoved(scrollPosition);
                    int currentSize = scrollPosition;
                    int nextLimit = currentSize + 10;

                    StartGettingAnnonce();
                /*while (currentSize - 1 < nextLimit) {
                    annonceClassList.add( new AnnonceClass("a", "b", "c", "f", 125f,"g", "0", "0"));
                    currentSize++;
                }*/

                    //  annonceClassList.add( new AnnonceClass("a", "b", "c", "f", 125f,"g", "0", "2"));

                    //  isLoading = false;
                } catch (Exception e){}
            }
        }, 500);


    }
    private void Init() {
        context = SeekAnnonceActivity.this;
        recyclerHelp = (RecyclerView) findViewById(R.id.recycle_help);
        LinearLayoutManager layoutManager = new LinearLayoutManager(context);
        recyclerHelp.addItemDecoration(new DividerItemDecoration(context, DividerItemDecoration.HORIZONTAL));

        recyclerHelp.setLayoutManager(layoutManager);
        adapter = new ListAnnonceAdapter(context, annonceClassList, query, SeekAnnonceActivity.this);
        recyclerHelp.setAdapter(adapter);

        if (DBSimpleIntel.getInstance(context).checkAlreadyExist(StaticValues.UserID)) {
            currentuserID = DBSimpleIntel.getInstance(SeekAnnonceActivity.this).getLastValue(StaticValues.UserID);
        }
        else
        {
            currentuserID = "Empty";
        }

         cw = new ContextWrapper(context);
         directory = cw.getDir(StaticValues.DirectoryImageName, Context.MODE_PRIVATE);

        toolbar = (Toolbar) findViewById(R.id.main_page_toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        toolbar.setTitleTextAppearance(this, R.style.AveriaLight);
        getSupportActionBar().setTitle("Annonces"); //string is custom name you want


     /*   annonceClassList.add( new AnnonceClass("a", "b", "c", "f", 125f,"g", "0", "0"));
        annonceClassList.add( new AnnonceClass("a", "b", "c", "f", 125f,"g", "0", "0"));
        annonceClassList.add( new AnnonceClass("a", "b", "c", "f", 125f,"g", "0", "0"));
        annonceClassList.add( new AnnonceClass("a", "b", "c", "f", 125f,"g", "0", "0"));
        annonceClassList.add( new AnnonceClass("a", "b", "c", "f", 125f,"g", "0", "0"));

        annonceClassList.add( new AnnonceClass("a", "b", "c", "f", 125f,"g", "0", "0"));
        annonceClassList.add( new AnnonceClass("a", "b", "c", "f", 125f,"g", "0", "0"));
        annonceClassList.add( new AnnonceClass("a", "b", "c", "f", 125f,"g", "0", "0"));
        annonceClassList.add( new AnnonceClass("a", "b", "c", "f", 125f,"g", "0", "0"));
        annonceClassList.add( new AnnonceClass("a", "b", "c", "f", 125f,"g", "0", "0"));

        annonceClassList.add( new AnnonceClass("a", "b", "c", "f", 125f,"g", "0", "2"));*/


        initScrollListener();


        loadingBar = new ProgressDialog(this);
    }
    private void ChoiceType()
    {

        String[] items = {"Propositions de service", "Demandes de service"};
        Dialog dialog = new LovelyChoiceDialog(this)
                .setTopColorRes(R.color.colorPrimary)
                .setTitle("Consulter les annonces de : ")
                .setIcon(android.R.drawable.ic_dialog_info)
                .setItems(items, new LovelyChoiceDialog.OnItemSelectedListener<String>() {
                    @Override
                    public void onItemSelected(int position, String item) {
                     switch (position)
                     {
                         case 0 :
                             query = StaticValues.Offreur;
                             getSupportActionBar().setTitle("Services"); //string is custom name you want

                             ChoiceRadius();
                             //Init();
                             //VerifyIfLocationEnaledAndSearchForLocations();
                             break;

                         case 1 :
                             query = StaticValues.Demandeur;
                             getSupportActionBar().setTitle("Demandes"); //string is custom name you want
                             ChoiceRadius();
                             //Init();
                             //VerifyIfLocationEnaledAndSearchForLocations();
                             break;

                         //case 2 :
                         //    break;
                     }
                    }
                })
                .show();


        dialog.setCanceledOnTouchOutside(false);
        dialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialogInterface) {
              //  finish();
            }
        });
    }
    private void ChoiceRadius()
    {
        numberOfLoad = 0;
        List <String> ListOfRadius = new ArrayList<>();
        int max = MaxRadius/RadiusStep;
        for (int i =0; i<max; i++)
        {
            ListOfRadius.add((RadiusStep * (i+1)) + " kilomètres");
        }
        String[] items = new String[ListOfRadius.size()];
        for (int i =0; i<ListOfRadius.size(); i++)
        {
            items[i] = ListOfRadius.get(i);
        }
        Dialog dialog = new LovelyChoiceDialog(this)
                .setTopColorRes(R.color.colorPrimary)
                .setTitle("Choisir le rayon de recherche autour de vous")
                .setIcon(android.R.drawable.ic_dialog_info)
                .setItems(items, new LovelyChoiceDialog.OnItemSelectedListener<String>()
                {
                    @Override
                    public void onItemSelected(int position, String item) {

                        ChosenRadius = RadiusStep * (position+1);
                        Log.i("CheckRadius", "Rayon "+ ChosenRadius);
                        VerifyIfLocationEnaledAndSearchForLocations();
                    }
                })
                .show();


        dialog.setCanceledOnTouchOutside(false);
        dialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialogInterface) {
           //     finish();
            }
        });
    }
    private void VerifyIfLocationEnaledAndSearchForLocations()
    {


        if (helperClass.isLocationEnabled(this))
        {
            ClearEveryting();
            callPermissions();
            setLoadingBar("Localisation", "Nous cherchons votre localisation");
        }
        else
        {

            new AlertDialog.Builder(context)
                    .setTitle("Localisation")
                    .setMessage("Merci d'activer manuellement la localisation. \nPour cela, aller dans la barre notification et activer la localisation en cliquant sur le logo localisation.")
                    .setCancelable(false)
                    .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which)
                        {
                            VerifyIfLocationEnaledAndSearchForLocations();
                        }
                    })
                    .setNegativeButton("Annuler", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            finish();
                        }
                    })
                    .setIcon(android.R.drawable.ic_dialog_info)
                    .show();
        }

    }
    public void InitForQuery(final LatLng latLng)
    {
        if (helperClass.isNetworkAvailable(this))
        {

            setLoadingBar("Recherche en cours","Nous recherchons des personnes à proximité de chez vous");

            String country = getCountryFromLatLng(latLng);
           // country = "France";
            //DBSimpleIntel.getInstance(context).addElementTodB(StaticValues.country, "France");
            Double Latt = latLng.latitude;
            Double Long = latLng.longitude;
            if (TextUtils.isEmpty(country))
            {
                country = DBSimpleIntel.getInstance(context).getLastValue(StaticValues.country); //last country


                        if (TextUtils.isEmpty(country))

                        {
                           // loadingBar.dismiss();
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
                            if (!TextUtils.isEmpty(country)) {
                                StartQueryInCircle(Latt, Long, country);
                                //DBSimpleIntel.getInstance(context).addElementTodB(StaticValues.country, country);
                                Log.i("CheckLocale", locale.getDisplayCountry());
                            }
                        }
                        else
                        {
                            Log.i("CheckCodeIssue", "Can't get country. Using previous postcode : " + country);
                            //Query with this country !
                            StartQueryInCircle(Latt, Long, country);
                        }
            }

            else
            {
                StartQueryInCircle(Latt, Long, country);
                //Query with actual country
            }
        }
        else
        {
            loadingBar.dismiss();
            String[] items = {"Réessayer", "Annuler"};
            new LovelyChoiceDialog(this)
                    .setTopColorRes(R.color.colorPrimary)
                    .setTitle("Pas de connexion internet")
                    .setIcon(android.R.drawable.ic_dialog_info)
                    .setItems(items, new LovelyChoiceDialog.OnItemSelectedListener<String>() {
                        @Override
                        public void onItemSelected(int position, String item) {
                            switch (position) {
                                case 0:
                                    Log.i("StartQuery", "StartQuery");

                                    InitForQuery(latLng);
                                    break;

                                case 1:
                                    finish();

                                    break;

                            }
                        }
                    })
                    .show();
        }
    }
    private void StartQueryInCircle(Double Latt, Double Long, String country)
    {


        //country = "France";


        Log.i("CheckSystemCoordonates", "lattitude " + Latt);

        String centerInt = helperClass.returnCenter(Latt);

        Log.i("CheckSystemCoordonates", "centerInt " + centerInt);

        double dist = (360 * ChosenRadius ) / (StaticValues.Rayon * 2 * Math.PI * Math.cos(Math.toRadians(Latt)));

        Double LongNeg = Long - dist;
        Log.i("CheckSystemCoordonates", "Distance " + LongNeg);
        Double LongPlus = Long + dist;
        Log.i("CheckSystemCoordonates", "Distance " + LongPlus);

        float posBorn =  helperClass.ReturnDistanceBetweenTwoCoordonates(Latt, (Double.parseDouble(centerInt)+ 450) / 10000,0,0);
        float negBorn=  helperClass.ReturnDistanceBetweenTwoCoordonates(Latt, (Double.parseDouble(centerInt)- 450) / 10000,0,0);


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

        int Num = ChosenRadius/5;

        DatabaseReference DBRef =  FirebaseDatabase.getInstance().getReference().child("Distance").child("Interval").child(country).child(query);

        switch (Num)
        {
            case 1 :
            {
                //TODO Revoir
                DBRef.child(centerInt).orderByChild(StaticValues.LastLongitude).startAt(LongNeg).endAt(LongPlus)
                        .addListenerForSingleValueEvent(getAnnonceIDAroundListener);
                int centerIntDouble = Integer.parseInt(centerInt);
                int centerOne = centerIntDouble+900;
                int centerTwo = centerIntDouble-900;
                ListOfCenter.add(Integer.toString(centerIntDouble));
                AddPost(country);
                if (isPlusLoinDelaBornepos)
                {
                    ListOfCenter.add(Integer.toString(centerTwo));
                    ListOfCenterToCheck.addAll(ListOfCenter);
                    Log.i("CheckSystemCoordonates", "centerTwo " + centerTwo);
                    DBRef.child(Integer.toString(centerTwo)).orderByChild(StaticValues.LastLongitude).startAt(LongNeg).endAt(LongPlus)
                            .addListenerForSingleValueEvent(getAnnonceIDAroundListener);


                }
                else
                {
                    ListOfCenter.add(Integer.toString(centerOne));
                    ListOfCenterToCheck.addAll(ListOfCenter);
                    Log.i("CheckSystemCoordonates", "centerOne " + centerOne);
                    DBRef.child(Integer.toString(centerOne)).orderByChild(StaticValues.LastLongitude).startAt(LongNeg).endAt(LongPlus)
                            .addListenerForSingleValueEvent(getAnnonceIDAroundListener);


                }

                break;
            }
            case 2 :
            {
                //TODO Revoir
                int centerIntDouble = Integer.parseInt(centerInt);
                int centerOne = centerIntDouble+900;
                int centerTwo = centerIntDouble-900;
                ListOfCenter.add(Integer.toString(centerIntDouble));
                ListOfCenter.add(Integer.toString(centerTwo));
                ListOfCenter.add(Integer.toString(centerOne));
                AddPost(country);
                ListOfCenterToCheck.addAll(ListOfCenter);

                DBRef.child(centerInt).orderByChild(StaticValues.LastLongitude).startAt(LongNeg).endAt(LongPlus)
                        .addListenerForSingleValueEvent(getAnnonceIDAroundListener);

                DBRef.child(Integer.toString(centerOne)).orderByChild(StaticValues.LastLongitude).startAt(LongNeg).endAt(LongPlus)
                        .addListenerForSingleValueEvent(getAnnonceIDAroundListener);

                DBRef.child(Integer.toString(centerTwo)).orderByChild(StaticValues.LastLongitude).startAt(LongNeg).endAt(LongPlus)
                        .addListenerForSingleValueEvent(getAnnonceIDAroundListener);
                break;
            }
            default:
            {
                //TODO à refaire... distinguer les cas quand on fait +5 ou +10.
                // Pour 5km il faut 2 interval.
                // Pour 10km il faut 3 interval.
                // Pour 15km il faut 4 interval
                // Pour 20km il faut 5 interval
                // Pour 25km il faut 6 interval
                int centerIntDouble = Integer.parseInt(centerInt);
                ListOfCenter.add(Integer.toString(centerIntDouble));

                for (int i = 1; i< Num; i++)
                {

                    int centerOne = centerIntDouble+900*(i);
                    int centerTwo = centerIntDouble-900*(i);
                    if (i==Num-1)
                    {
                        if (isPlusLoinDelaBornepos)
                        {
                            ListOfCenter.add(Integer.toString(centerTwo));

                        }
                        else
                        {
                            ListOfCenter.add(Integer.toString(centerOne));

                        }
                    }
                    else
                    {
                        ListOfCenter.add(Integer.toString(centerTwo));
                        ListOfCenter.add(Integer.toString(centerOne));
                    }

                }
                AddPost(country);
                ListOfCenterToCheck.addAll(ListOfCenter);
                ListOfCenter.clear();
                DBRef.child(centerInt).orderByChild("Lo").startAt(LongNeg).endAt(LongPlus)
                        .addListenerForSingleValueEvent(getAnnonceIDAroundListener);


                Log.i("CheckSystemCoordonates", "center " + centerIntDouble);


                for (int i = 1; i< Num; i++)
                {
                    int centerOne = centerIntDouble+900*(i);
                    int centerTwo = centerIntDouble-900*(i);
                    if (i==Num-1)
                    {
                        if (isPlusLoinDelaBornepos)
                        {
                            Log.i("CheckSystemCoordonates", "centerTwo " + centerTwo);
                            DBRef.child(Integer.toString(centerTwo)).orderByChild(StaticValues.LastLongitude).startAt(LongNeg).endAt(LongPlus)
                                    .addListenerForSingleValueEvent(getAnnonceIDAroundListener);
                        }
                        else
                        {
                            Log.i("CheckSystemCoordonates", "centerOne " + centerOne);
                            DBRef.child(Integer.toString(centerOne)).orderByChild(StaticValues.LastLongitude).startAt(LongNeg).endAt(LongPlus)
                                    .addListenerForSingleValueEvent(getAnnonceIDAroundListener);
                        }
                    }
                    else
                    {
                        Log.i("CheckSystemCoordonates", "centerOne " + centerOne);
                        Log.i("CheckSystemCoordonates", "centerTwo " + centerTwo);
                        DBRef.child(Integer.toString(centerOne)).orderByChild(StaticValues.LastLongitude).startAt(LongNeg).endAt(LongPlus)
                                .addListenerForSingleValueEvent(getAnnonceIDAroundListener);


                        DBRef.child(Integer.toString(centerTwo)).orderByChild(StaticValues.LastLongitude).startAt(LongNeg).endAt(LongPlus)
                                .addListenerForSingleValueEvent(getAnnonceIDAroundListener);
                    }

                }
                break;
            }
        }

    }
    private void AddPost(String country)
    {
        ListOfCenter.add(StaticValues.SeekLoca);
        FirebaseDatabase.getInstance().getReference().child("Loca").child(country).child(query+"l").addListenerForSingleValueEvent(getAnnonceIDAroundListener);
    }


    /*** Location fonction ***/
    public void requestLocationUpdate() {


        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PermissionChecker.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PermissionChecker.PERMISSION_GRANTED)
        {


            fusedLocationProviderClient = new FusedLocationProviderClient(this);
            locationRequest = new LocationRequest();
            locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
            locationRequest.setInterval(5000);
            locationRequest.setFastestInterval(3000);
            locationRequest.setSmallestDisplacement(10f);


            //fusedLocationProviderClient.removeLocationUpdates(locationCallback);

           fusedLocationProviderClient.requestLocationUpdates(locationRequest, new LocationCallback()
            {
                @Override
                public void onLocationResult(LocationResult locationResult) {
                    super.onLocationResult(locationResult);

                    Log.i("CheckLoca", "Lat" +locationResult.getLastLocation().getLatitude());
                    Log.i("CheckLong", "Long" +locationResult.getLastLocation().getLongitude());
                    Log.i("CheckLoca", "Lat" +locationResult.getLastLocation().getLatitude());
                    Log.i("CheckLong", "Long" +locationResult.getLastLocation().getLongitude());


                    double Longit = locationResult.getLastLocation().getLongitude();
                    double Latit = locationResult.getLastLocation().getLatitude();
                    LatLng latLong = new LatLng(Latit, Longit);

                    if (!TextUtils.isEmpty(Double.toString(Longit)) && !TextUtils.isEmpty(Double.toString(Latit)))
                    {
                        DBSimpleIntel.getInstance(context).addElementTodB(StaticValues.LastLatitude, Double.toString(Latit));
                        DBSimpleIntel.getInstance(context).addElementTodB(StaticValues.LastLongitude, Double.toString(Longit));
                    }

                    Log.i("StartQuery", "Request Loca update");

                    if (!isAlreadyTrigged) {
                       // StartQuery(latLong);
                        InitForQuery(latLong);
                        fusedLocationProviderClient.removeLocationUpdates(this);

                        Log.i("removeloca", "remove");
                    }


                }
            }, getMainLooper());

        }else callPermissions();
    }
    public void callPermissions(){
        String[] permissions = {Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION};
        Permissions.check(this/*context*/, permissions, null/*rationale*/, null/*options*/, new PermissionHandler() {
            @Override
            public void onGranted() {
                DBSimpleIntel.getInstance(context).addElementTodB(StaticValues.PermissionGrantedLoca, "OK");
                if (!isAlreadyTrigged) {
                    requestLocationUpdate();
                }
            }

            @Override
            public void onDenied(Context context, ArrayList<String> deniedPermissions) {
                super.onDenied(context, deniedPermissions);
                finish();
            }
        });
    }
    private String getCountryFromLatLng(LatLng latLng){

        double latit= latLng.latitude;
        double longit=latLng.longitude;

        Geocoder geocoder;
        List<Address> addresses;
        geocoder = new Geocoder(context, Locale.ENGLISH);

        String address = null;
        String city = null;
        String state = null;
        String country = null;
        String postalCode = null;
        String knonName = null;
        try {
            addresses = geocoder.getFromLocation(latit, longit, 1);

           // address = addresses.get(0).getAddressLine(0);
           // DBSimpleIntel.getInstance(context).addElementTodB(StaticValues.adress, address);

            //city = addresses.get(0).getLocality();
           // DBSimpleIntel.getInstance(context).addElementTodB(StaticValues.city, city);

          //  state = addresses.get(0).getAdminArea();
          //  DBSimpleIntel.getInstance(context).addElementTodB(StaticValues.state, state);

            country = addresses.get(0).getCountryName();
            //country= "France";
            if (!TextUtils.isEmpty(country)) {
                DBSimpleIntel.getInstance(context).addElementTodB(StaticValues.country, country);
                Log.i("Checkcountry", "Pays " + country);
            }
           // DBSimpleIntel.getInstance(context).addElementTodB(StaticValues.country, country);

            postalCode = addresses.get(0).getPostalCode();
            if (!TextUtils.isEmpty(postalCode)) {
                DBSimpleIntel.getInstance(context).addElementTodB(StaticValues.postalCode, postalCode);
            }

         //   knonName = addresses.get(0).getFeatureName();
         //   DBSimpleIntel.getInstance(context).addElementTodB(StaticValues.knonName, knonName);

        } catch (IOException e)
        {
            e.printStackTrace();
            Log.i("CheckException", "getLocationDetails" +
                    ": SecurityException: " + e.getMessage() );
        }
        Log.i("PostalCode", "Code " + postalCode);

       return country;



    }
    private String getTownFromLatLng(LatLng latLng){

        double latit= latLng.latitude;
        double longit=latLng.longitude;

        Geocoder geocoder;
        List<Address> addresses;
        geocoder = new Geocoder(context, Locale.ENGLISH);

        String address = null;
        String city = null;
        String state = null;
        String country = null;
        String postalCode = null;
        String knonName = null;
        try {
            addresses = geocoder.getFromLocation(latit, longit, 1);

            // address = addresses.get(0).getAddressLine(0);
            // DBSimpleIntel.getInstance(context).addElementTodB(StaticValues.adress, address);

            city = addresses.get(0).getLocality();
            if (!TextUtils.isEmpty(city)) {
                DBSimpleIntel.getInstance(context).addElementTodB(StaticValues.city, city);
                Log.i("CheckTown", "ville " + city);
            }




        } catch (IOException e)
        {
            e.printStackTrace();
            Log.i("CheckException", "getLocationDetails" +
                    ": SecurityException: " + e.getMessage() );
        }
        Log.i("PostalCode", "Code " + postalCode);

        return city;



    }

    /***/

    public void setLoadingBar(String title, String message)
    {
        if (!loadingBar.isShowing())
        {
            try {

                loadingBar = new ProgressDialog(this);
                loadingBar.setCanceledOnTouchOutside(false);
                loadingBar.show();
                loadingBar.setOnCancelListener(new DialogInterface.OnCancelListener() {
                    @Override
                    public void onCancel(DialogInterface dialogInterface) {
                        finish();
                    }
                });
            }catch (Exception e){}
        }
        loadingBar.setTitle(title);
        loadingBar.setMessage(message);
    }
    private void CheckIfFailed()
    {
        countFailRestartLoading++;
        countAllFailAnnoce++;
        if (countFailRestartLoading == loadingNumber) //toutes les annonces ont échouées
        {
            if (annonceIDClassList.size() > annonceClassList.size())
            {

                //on reload d'autres!
                Log.i("ChildCheckCount", "Failed, callling refresh ! ");
                countStartLoading = 0;
                countFailRestartLoading=0;
                annonceClassList.add(new AnnonceClass("a", "b", "-1", "f", 125f, "g", "0", "2", "r", false)); //show loading bar!
                adapter.notifyItemInserted(annonceClassList.size() - 1);
                isLoading = false;
                return;
            } else
            {
                AucuneAnnonce();
                return;
            }
        }
        if (countAllFailAnnoce == annonceIDClassList.size())
        {
            //tout a fail ya pu d'annonce
            countFailRestartLoading = 0;
            countAllFailAnnoce = 0;
            if (loadingBar.isShowing())
            {
                loadingBar.dismiss();
            }
            AucuneAnnonce();
           // if (MaxRadius > ChosenRadius)
           // {
           //     ChoiceRadius();
          //  }

            return;
        }
        if (countStartLoading == loadingNumber)
        {
            //le de
        }

    }
    private void AddElementToAnnonceList(DataSnapshot child)
    {
        countStartLoading++;
        lastKey = child.getKey();

        final String time;
        if (checkifDataNull(child, StaticValues.Time))
        {
            time= child.child(StaticValues.Time).getValue().toString();
            String officialTime = DBSimpleIntel.getInstance(context).getLastValue(StaticValues.SyncServerTime);

            long longtime = Long.parseLong(time);
            long longofftime = Long.parseLong(officialTime);

            if (longofftime - longtime > StaticValues.TimeOut)
            {
           /*     //TODO DELTE L'ANNONCE
                Log.i("DeleteAnnoce", "Deleting !");
                if (child.child(StaticValues.UserID).exists())
                {
                    String userID = child.child(StaticValues.UserID).getValue().toString();
                    try
                    {
                        String key = child.getKey();
                        ListComeBack <String> ListTampon = new ArrayList<>();
                        ListTampon.addAll(ListOfCenterToDelete);

                        for (int i =0 ; i< ListTampon.size(); i++)
                        {
                            String cut[] = ListTampon.get(i).split(";");
                            if (cut[0].equals(key))
                            {
                                String ceneter = cut[1];
                                helperClass.DeleteAnnonce(query, key, context, ceneter, userID);
                                break;

                            }
                        }
                    }
                    catch (Exception e)
                    {
                        Log.i("DeleteAnnoce", e.getMessage().toString());
                    }
                }
                else
                {
                    String key = child.getKey();
                    ListComeBack <String> ListTampon = new ArrayList<>();
                    ListTampon.addAll(ListOfCenterToDelete);

                    for (int i =0 ; i< ListTampon.size(); i++)
                    {
                        String cut[] = ListTampon.get(i).split(";");
                        if (cut[0].equals(key))
                        {
                            String ceneter = cut[1];
                            helperClass.DeleteAnnonce(query, key, context, ceneter, "");
                            break;

                        }
                    }
                }

                CheckIfFailed();
                return;*/
            }
        }
        else
        {
            //delete?
            CheckIfFailed();
            return;
        }
        if (!checkifDataNull(child, StaticValues.UserID)){
            //TODO Bug? Delete l'annonce
            CheckIfFailed();
            return;
        }
        final String userID = child.child(StaticValues.UserID).getValue().toString();
        if (userID.equals(currentuserID))
        {
            CheckIfFailed();
            return;
        }

        if (!checkifDataNull(child, StaticValues.annonce_title))
        {
            CheckIfFailed();
            return;
        }
        if (!checkifDataNull(child, StaticValues.annonce))
        {
            CheckIfFailed();
            return;
        }
        final String title = child.child(StaticValues.annonce_title).getValue().toString();
        final String heart = child.child(StaticValues.annonce).getValue().toString();
        // Ces trois trucs sont obligatoires dans l'annonce !!!
        Log.i("CheckTime", "Time " + time);


        final String prix;

        if (child.child(StaticValues.prix).exists())
        {
            prix = child.child(StaticValues.prix).getValue().toString();
        } else {
            prix = "0";
        }
        float Findis = -1;
        for (int i =0; i< annonceIDClassList.size(); i++)
        {
            if (annonceIDClassList.get(i).getID().equals(lastKey))
            {
                Findis = annonceIDClassList.get(i).getDistance();
                break;
            }
        }
        if (Findis == -1)
        {
            CheckIfFailed();
            return;
        }
        String town = "";
        for (int i =0; i< ListOfLatLng.size(); i++)
        {
            String key = child.getKey();
            String cut[] = ListOfLatLng.get(i).split(";");
            if (cut[0].equals(key))
            {
               try {
                   Double Lat = Double.parseDouble(cut[1]);
                   Double Long = Double.parseDouble(cut[2]);
                   LatLng latLong = new LatLng(Lat, Long);
                   town = getTownFromLatLng(latLong);

               }catch (Exception e){}

                break;

            }
        }



        final String townFinal = town;

        final String finalKeyAnnonce = child.getKey();


        final int countTest= countStartLoading;

        final float finalFindis = Findis;
        FirebaseDatabase.getInstance().getReference().child(StaticValues.UserPath).child(userID).child(StaticValues.UserName).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot)
            {
                boolean image = false;
                final String imagePath = finalKeyAnnonce + ".jpg";
                File myImageFile = new File(directory, imagePath);
                if (myImageFile.exists())
                {
                    image = true;
                }
                else
                {
                    downloadFile(finalKeyAnnonce);
                }
                countInFirebaseAnnonce++;
                if (dataSnapshot.exists())
                {
                    String username = dataSnapshot.getValue().toString();
                    if (annonceClassList.size() > 1) {

                        Log.i("CheckDistance", "Distance " + finalFindis);


                        for (int i = 0; i < annonceClassList.size(); i++) {

                            Log.i("CheckDistance", "Distance " + i + "  " + annonceClassList.get(i).getDistance());
                            Log.i("CheckDistance", "Distance " + (i + 1) + "  " + annonceClassList.get(i).getDistance());

                            if (i == annonceClassList.size() - 1) {

                                if (annonceClassList.get(i).getDistance() > finalFindis) {
                                    annonceClassList.add(i - 1, new AnnonceClass(title, heart, userID, prix, finalFindis, username, time, townFinal, finalKeyAnnonce, image));
                                    adapter.notifyDataSetChanged();
                                    break;
                                    // return;
                                } else {
                                    annonceClassList.add(new AnnonceClass(title, heart, userID, prix, finalFindis, username, time, townFinal, finalKeyAnnonce, image));
                                    adapter.notifyDataSetChanged();
                                    break;
                                    // return;
                                }
                            } else {
                                if (finalFindis <= annonceClassList.get(i).getDistance()) {
                                    annonceClassList.add(0, new AnnonceClass(title, heart, userID, prix, finalFindis, username,time, townFinal, finalKeyAnnonce, image));
                                    adapter.notifyDataSetChanged();
                                    break;
                                    // return;
                                } else if (finalFindis > annonceClassList.get(i).getDistance() && finalFindis <= annonceClassList.get(i + 1).getDistance()) {
                                    annonceClassList.add(i + 1, new AnnonceClass(title, heart, userID, prix, finalFindis, username,time, townFinal, finalKeyAnnonce, image));
                                    adapter.notifyDataSetChanged();
                                    break;
                                    // return;
                                }
                            }
                        }
                        //Ajouter en fonction de la distance !
                        for (int i = 0; i < annonceClassList.size(); i++) {
                            Log.i("CheckDistance", "Distance " + annonceClassList.get(i).getDistance());
                        }

                    } else {
                        if (annonceClassList.size() == 0) {
                            annonceClassList.add(new AnnonceClass(title, heart, userID, prix, finalFindis, username, time, townFinal, finalKeyAnnonce, image));
                            //  annonceClassList.add(new AnnonceClass(title, heart, userID, prix, finalFindis, username, time, "0"));
                            //  annonceClassList.add(new AnnonceClass(title, heart, userID, prix, finalFindis, username, time,"1"));
                            adapter.notifyDataSetChanged();
                        } else {
                            if (annonceClassList.get(0).getDistance() < finalFindis) {
                                annonceClassList.add(new AnnonceClass(title, heart, userID, prix, finalFindis, username, time, townFinal,finalKeyAnnonce, image));
                                adapter.notifyDataSetChanged();
                            } else {
                                annonceClassList.add(0, new AnnonceClass(title, heart, userID, prix, finalFindis,username, time, townFinal, finalKeyAnnonce, image));
                                adapter.notifyDataSetChanged();
                            }
                        }

                    }
                    if (loadingBar.isShowing()) {
                        loadingBar.dismiss();
                    }
                  /*  if (countInFirebaseAnnonce == loadingNumber - countFailRestartLoading)
                    {
                        if (annonceIDClassList.size() - countAllFailAnnoce > annonceClassList.size())
                        {
                            if (isLoading)
                            {
                                countInFirebaseAnnonce=0;
                                countStartLoading = 0;
                                countFailRestartLoading = 0; //à pour conséquence de renvoyer la fct --> trouver un autre moyen
                                annonceClassList.add(new AnnonceClass("a", "b", "-1", "f", 125f, "g", "0", "2", "f", false)); //show loading bar!
                                adapter.notifyItemInserted(annonceClassList.size() - 1);
                                isLoading = false;
                            }
                        } else
                        {
                             //annonceClassList.add(new AnnonceClass("a", "b", "-2", "f", 125f, "g", "0", "3", "f", false)); //show loading bar!
                        }


                    }
                    else
                    {
                        Log.i("ChildCheckCount", "Final " + countTest + "Start " + countStartLoading);

                    }*/
                 /*   if (annonceIDClassList.size()-countAllFailAnnoce > annonceClassList.size()) { //atttention comme on fait annonceclasslist.add(Loading) ça augmente e 1 ...
                        if (countTest  == loadingNumber - countFailRestartLoading)
                        {
                            if (isLoading) {
                                Log.i("ChildCheckCount", "10 ! Calling refresh !!!");

                                countStartLoading = 0;
                                countFailRestartLoading = 0; //à pour conséquence de renvoyer la fct --> trouver un autre moyen
                                annonceClassList.add(new AnnonceClass("a", "b", "c", "f", 125f, "g", "0", "2")); //show loading bar!
                                adapter.notifyItemInserted(annonceClassList.size() - 1);
                                isLoading = false;
                            }
                        } else
                        {
                            Log.i("ChildCheckCount", "Final " + countTest + "Start " + countStartLoading);
                        }
                    }
                    else
                    {
                        // annonceClassList.add(new AnnonceClass("a", "b", "c", "f", 125f, "g", "0", "2")); //show loading bar!
                        // --> dire qu'il n'y a plus rien à load !
                    }*/


                }

                //TODO CHECK THIS !!!
                else
                {
                    Log.i("CheckBroken", finalKeyAnnonce);
                    CheckIfFailed();
                }
                if (countInFirebaseAnnonce == loadingNumber - countFailRestartLoading)
                {
                    if (annonceIDClassList.size() - countAllFailAnnoce > annonceClassList.size())
                    {
                        if (isLoading)
                        {
                            countInFirebaseAnnonce=0;
                            countStartLoading = 0;
                            countFailRestartLoading = 0; //à pour conséquence de renvoyer la fct --> trouver un autre moyen
                            annonceClassList.add(new AnnonceClass("a", "b", "-1", "f", 125f, "g", "0", "2", "f", false)); //show loading bar!
                            adapter.notifyItemInserted(annonceClassList.size() - 1);
                            isLoading = false;
                        }
                    } else
                    {
                        //annonceClassList.add(new AnnonceClass("a", "b", "-2", "f", 125f, "g", "0", "3", "f", false)); //show loading bar!
                    }


                }
                else
                {
                    Log.i("ChildCheckCount", "Final " + countTest + "Start " + countStartLoading);

                }


            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError)
            {

            }
        });

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
    private void downloadFile(String uid)
    {
        String country = CheckIfCountryIsEmpty();

        downloadRef= FirebaseStorage.getInstance().getReference().child(country).child(query);
        String imagePath = uid + ".jpg";
        Log.i("CheckImagePath", ""+ imagePath);
        final StorageReference RestoRef = downloadRef.child(imagePath);

        ContextWrapper cw = new ContextWrapper(context.getApplicationContext());
        final File directory = cw.getDir(StaticValues.DirectoryImageName, Context.MODE_PRIVATE);

        File myImageFile = new File(directory, imagePath);

        //myImageFile.delete(); //supprime l'ancienne photo!
        final String keyid = uid;
        if(!myImageFile.exists())
        {
            RestoRef.getFile(myImageFile).addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot)
                {
                    Log.e("firebase ",";local tem file created  created ");
                    //adapter.notifyDataSetChanged();
                    for (int i =0; i< annonceClassList.size(); i++)
                    {
                        if (annonceClassList.get(i).getKey().equals(keyid))
                        {
                            annonceClassList.get(i).setImage(true);
                            adapter.notifyDataSetChanged();
                            break;
                        }
                    }
                }
            }).addOnFailureListener(new OnFailureListener()
            {
                @Override
                public void onFailure(@NonNull Exception exception) {
                    Log.e("firebase ",";local tem file not created  created " +exception.toString());
                }
            });
        }
    }



}

class ListAnnonceAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    Context context;
    List<AnnonceClass> annonceClassList = new ArrayList<>();
    HelperClass helperClass = new HelperClass();
    private ProgressDialog loadingBar;
    String query;
    int pos = 0;
    Activity activity;
    ContextWrapper cw ;
    File directory ;

    public ListAnnonceAdapter(Context context, List<AnnonceClass> annonceClassList, String query, Activity activity) {
        this.context = context;
        this.annonceClassList = annonceClassList;        
        loadingBar = new ProgressDialog(context);
        this.query = query;
        this.activity = activity;
        cw = new ContextWrapper(context);
        directory = cw.getDir(StaticValues.DirectoryImageName, Context.MODE_PRIVATE);
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType)
    {

        if (viewType == 1) {
            View view = LayoutInflater.from(context).inflate(R.layout.model_annonce, parent, false);
            return new ItemAnnonceHolder(view);
        } else if (viewType == 2) {
            View view = LayoutInflater.from(context).inflate(R.layout.model_show_more, parent, false);
            return new LoadingViewHolder (view);
        }
        else
        {
            if (viewType == 3)
            {
                View view = LayoutInflater.from(context).inflate(R.layout.model_no_more, parent, false);
                return new LoadingViewHolder (view);
            }
            else
            if (viewType == 4)
            {
                View view = LayoutInflater.from(context).inflate(R.layout.model_annonce_image, parent, false);
                return new ItemImageHolder (view);
            }
        }

        return null;

    }
    public void setLoadingBar(String title, String message)
    {
        if (!loadingBar.isShowing())
        {
            loadingBar = new ProgressDialog(context);
            loadingBar.show();
        }
        loadingBar.setTitle(title);
        loadingBar.setMessage(message);
    }
    @Override
    public int getItemViewType(int position) {

        if (annonceClassList.get(position).getUserID().equals("-1"))
        {
            return 2;
        }
        else {
            if (annonceClassList.get(position).getUserID().equals("-2")) {
                return 3;
            }
            else {
                if( annonceClassList.get(position).getImage())
                {
                    return 4;
                }
                else {
                    return 1;
                }
            }
        }
    }
    private void cutLoadingBar()
    {
        if (loadingBar.isShowing()) {
            loadingBar.dismiss();
        }
    }
    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, final int position) {


        if (holder instanceof ItemAnnonceHolder) {

            if (query.equals(StaticValues.Demandeur)) {
                //demandeur
               // ((ItemAnnonceHolder) holder).cardView.setCardBackgroundColor(context.getResources().getColor(R.color.UnderPrim));
                ((ItemAnnonceHolder) holder).lbl_coup_de_pouce.setText("Prix solidaire : ");

            } else {
               // ((ItemAnnonceHolder) holder).cardView.setCardBackgroundColor(context.getResources().getColor(R.color.UnderPrim));
                ((ItemAnnonceHolder) holder).lbl_coup_de_pouce.setText("Prix solidaire : ");

                //proposeur

            }
            String town = annonceClassList.get(position).getType();


            if (!TextUtils.isEmpty(town))
            {
                ((ItemAnnonceHolder) holder).lbl_town.setText(town+", ");

            }
            else
            {
                ((ItemAnnonceHolder) holder).lbl_town.setText("Distance de vous : ");

            }
            ((ItemAnnonceHolder) holder).lbl_name.setText(annonceClassList.get(position).getName());
            ((ItemAnnonceHolder) holder).lbl_heart.setText(annonceClassList.get(position).getHeart());
            ((ItemAnnonceHolder) holder).lbl_price.setText(annonceClassList.get(position).getPrix());
            ((ItemAnnonceHolder) holder).lbl_title.setText(annonceClassList.get(position).getTitle());


            ((ItemAnnonceHolder) holder).lbl_distance.setText("" + annonceClassList.get(position).getDistance()+" ");



            if (DBSimpleIntel.getInstance(context).checkAlreadyExist(annonceClassList.get(position).getKey()+ StaticValues.SeekLoca))
            {
                ((ItemAnnonceHolder) holder).lbl_klm.setText("Km " + "(Livraison possible)");

            }
            else
            {
                ((ItemAnnonceHolder) holder).lbl_klm.setText("Km");

            }


            String time = helperClass.returnDataFromMillis(Long.parseLong(annonceClassList.get(position).getTime()));
            Log.i("CheckTime", "Title R " + time);

            ((ItemAnnonceHolder) holder).lbl_time.setText(time);


            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    pos = position;
                    if(helperClass.isUserLoggedInDialog(context, "Afin de contacter cette personne, veuillez vous inscrire.", activity))
                    {
                        LaunchDialog(annonceClassList.get(position).getUserID(), annonceClassList.get(position).getTitle(), annonceClassList.get(position).getName());
                    }
                }
            });
        }
        else
        {
            if (holder instanceof ItemImageHolder)
            {
                if (query.equals(StaticValues.Demandeur)) {
                    //demandeur
                  //  ((ItemImageHolder) holder).cardView.setCardBackgroundColor(context.getResources().getColor(R.color.UnderPrim));
                    ((ItemImageHolder) holder).lbl_coup_de_pouce.setText("Prix solidaire : ");

                } else {
                   // ((ItemImageHolder) holder).cardView.setCardBackgroundColor(context.getResources().getColor(R.color.UnderPrim));
                    ((ItemImageHolder) holder).lbl_coup_de_pouce.setText("Prix solidaire : ");

                    //proposeur

                }
                String town = annonceClassList.get(position).getType();


                if (!TextUtils.isEmpty(town))
                {
                    ((ItemImageHolder) holder).lbl_town.setText(town+", ");

                }
                else
                {
                    ((ItemImageHolder) holder).lbl_town.setText("Distance de vous : ");

                }
                ((ItemImageHolder) holder).lbl_name.setText(annonceClassList.get(position).getName());
                ((ItemImageHolder) holder).lbl_heart.setText(annonceClassList.get(position).getHeart());
                ((ItemImageHolder) holder).lbl_price.setText(annonceClassList.get(position).getPrix());
                ((ItemImageHolder) holder).lbl_title.setText(annonceClassList.get(position).getTitle());


                ((ItemImageHolder) holder).lbl_distance.setText("" + annonceClassList.get(position).getDistance()+" ");



                if (DBSimpleIntel.getInstance(context).checkAlreadyExist(annonceClassList.get(position).getKey()+ StaticValues.SeekLoca))
                {
                    ((ItemImageHolder) holder).lbl_klm.setText("Km " + "(Livraison possible)");

                }
                else
                {
                    ((ItemImageHolder) holder).lbl_klm.setText("Km");

                }


                String time = helperClass.returnDataFromMillis(Long.parseLong(annonceClassList.get(position).getTime()));
                Log.i("CheckTime", "Title R " + time);

                ((ItemImageHolder) holder).lbl_time.setText(time);


                holder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        pos = position;
                        if(helperClass.isUserLoggedInDialog(context, "Afin de contacter cette personne, veuillez vous inscrire.", activity))
                        {
                            LaunchDialog(annonceClassList.get(position).getUserID(), annonceClassList.get(position).getTitle(), annonceClassList.get(position).getName());
                        }
                    }
                });

                File myImageFile = new File(directory, annonceClassList.get(position).getKey()+".jpg");

                if(myImageFile.exists())
                {
                    Glide.with(context)
                            .load(Uri.fromFile(myImageFile)) // Uri of the picture
                            .signature(new ObjectKey(String.valueOf(System.currentTimeMillis())))
                            .into(((ItemImageHolder)holder).imageView);


                    ((ItemImageHolder) holder).imageView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            Intent intent = new Intent(context, FullScreenImageActivity.class);
                            String ID = annonceClassList.get(position).getKey() + ".jpg";
                            if (!TextUtils.isEmpty(ID))
                            {
                                intent.putExtra(StaticValues.IntentIDFullImage, ID);
                                context.startActivity(intent);
                            }
                        }
                    });
                }

            }
        }
    }

    private void LaunchDialog(final String userID, final String title, final String name) {
        //TODO Regarder si l'utilisateur a déjà
        setLoadingBar("Chargement", "Merci de patienter");
        FirebaseDatabase.getInstance().getReference().child(StaticValues.UserPath).child(userID).child(StaticValues.PhonePermissionPath).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    cutLoadingBar();

                    if (dataSnapshot.getValue().toString().equals("true")) {
                        Log.i("CheckPermiss", "true");


                        new LovelyStandardDialog(context, LovelyStandardDialog.ButtonLayout.VERTICAL)
                                .setTitle(title)
                                .setTopColorRes(R.color.colorPrimary)
                                .setMessage("Voulez-vous appeler " + name + " ou lui envoyer un message?")
                                .setIcon(R.drawable.icon8_message)
                                .setPositiveButton("Appeler", new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        ContactUsers(userID, 1, title);
                                        UpdateCall();
                                    }
                                })
                                .setNegativeButton("Envoyer un message", new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        ContactUsers(userID, 0, title);
                                        UpdateCall();

                                    }
                                })
                                .show();
                    } else {
                        Log.i("CheckPermiss", "false");


                        Dialog dialog = new LovelyTextInputDialog(context)
                                .setTitle(title)
                                .setTopColorRes(R.color.colorPrimary)
                                .setMessage(name + " n'autorise pas l'affichage de son numéro de téléphone. \nEnvoyez lui un message via l'application, votre numéro de téléphone sera automatiquement communiqué.")
                                .setIcon(R.drawable.icon8_message)
                                .setHint("Écrivez votre message ici.")
                                .setInputFilter("20 caractères min et 350 max", new LovelyTextInputDialog.TextFilter() {
                                    @Override
                                    public boolean check(String text) {
                                        Log.i("CheckLenght", "" + text.length());
                                        if (text.length() < 350 && text.length() > 20) {
                                            return true;
                                        } else {
                                            Toast.makeText(context, "" + text.length() + "/" + 350, Toast.LENGTH_SHORT).show();
                                            return false;
                                        }
                                    }
                                })
                                .setConfirmButton("Envoyer", new LovelyTextInputDialog.OnTextInputConfirmListener() {
                                    @Override
                                    public void onTextInputConfirmed(final String text) {

                                        setLoadingBar(title, "Envoi en cours du message...");
                                        final HashMap<String, Object> addAnnonce = new HashMap<>();
                                        addAnnonce.put(StaticValues.PhoneNumber, DBSimpleIntel.getInstance(context).getLastValue(StaticValues.PhoneNumber));
                                        addAnnonce.put(StaticValues.UserName, DBSimpleIntel.getInstance(context).getLastValue(StaticValues.UserName));
                                        addAnnonce.put(StaticValues.annonce_title, title);
                                        addAnnonce.put(StaticValues.Time, DBSimpleIntel.getInstance(context).getLastValue(StaticValues.SyncServerTime));
                                        addAnnonce.put(StaticValues.TextAnnonce, text);

                                        final String key = FirebaseDatabase.getInstance().getReference().push().getKey().toString();

                                        FirebaseDatabase.getInstance().getReference().child(StaticValues.NotificationPath).child(userID).child(key).setValue(addAnnonce).addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                if (task.isSuccessful()) {
                                                    cutLoadingBar();
                                                    MessageSentClass messageSentClass = new MessageSentClass(key, userID, name, title, text, DBSimpleIntel.getInstance(context).getLastValue(StaticValues.SyncServerTime));
                                                    DBMessageSent.getInstance(context).addElementTodB(messageSentClass);
                                                    helperClass.simpleDialog(title, "Message envoyé!", context);
                                                } else {
                                                    cutLoadingBar();
                                                    helperClass.simpleDialog(title, "Le message n'a pas été envoyé, vérifier votre connexion internet...", context);

                                                }
                                            }
                                        }).addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                cutLoadingBar();
                                                helperClass.simpleDialog(title, "Le message n'a pas été envoyé, vérifier votre connexion internet...", context);
                                            }
                                        });


                                        // Toast.makeText(context, "Message envoyé !", Toast.LENGTH_SHORT).show();

                                    }
                                })
                                .setNegativeButton("Annuler", null)
                                .show();

                        dialog.setCanceledOnTouchOutside(false);

                       /* new LovelyStandardDialog(context, LovelyStandardDialog.ButtonLayout.VERTICAL)
                                .setTitle(title)
                                .setTopColorRes(R.color.colorPrimary)
                                .setMessage(name+ " n'autorise pas l'affichage de son numéro de téléphone. \nVoulez-vous lui envoyer une notification avec votre numéro de téléphone afin d'être recontacté?")
                                .setIcon(android.R.drawable.ic_dialog_info)
                                .setPositiveButton("Oui", new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        helperClass.simpleDialog(title, "La notification a été envoyée.", context);
                                        //TODO Send notification

                                        final HashMap<String, Object> addAnnonce = new HashMap<>();
                                        addAnnonce.put(StaticValues.PhoneNumber, DBSimpleIntel.getInstance(context).getLastValue(StaticValues.PhoneNumber));
                                        addAnnonce.put(StaticValues.UserName, DBSimpleIntel.getInstance(context).getLastValue(StaticValues.UserName));
                                        addAnnonce.put(StaticValues.annonce_title, title);
                                        addAnnonce.put(StaticValues.Time, ServerValue.TIMESTAMP);

                                        FirebaseDatabase.getInstance().getReference().child(StaticValues.NotificationPath).child(userID).push().setValue(addAnnonce);
                                    }
                                })
                                .setNegativeButton("Non", new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {

                                    }
                                })
                                .show();*/
                    }
                }
                else
                {
                    Toast.makeText(context, "Cette annonce n'est plus disponible.", Toast.LENGTH_SHORT).show();
                    cutLoadingBar();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


    }

    private void UpdateCall()
    {
        FirebaseDatabase.getInstance().getReference().child("Call").push().setValue(true);
    }
    private void ContactUsers(String userID, final int x, final String title)
    {
        if (DBSimpleIntel.getInstance(context).checkAlreadyExist(userID))
        {
            String phonenumber =DBSimpleIntel.getInstance(context).getLastValue(userID);
            Log.i("CheckPhoneKnoww", "Phone : " + phonenumber);

            if (x==0) //sendSMS
            {
                helperClass.SendSms(phonenumber, context, "Bonjour, j'ai vu votre annonce \""+title + "\"  sur Help\u0026Services...");
            }
            else //Cal
            {
                helperClass.CallUsers(phonenumber, context);
            }
        }
        else {
            FirebaseDatabase.getInstance().getReference().child(StaticValues.UserPath).child(userID).child(StaticValues.PhoneNumber).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()) {

                        String phonenumber =dataSnapshot.getValue().toString();
                        if (x==0) //sendSMS
                        {
                            helperClass.SendSms(phonenumber, context, "Bonjour, j'ai vu votre annonce \""+title + "\"  sur Help\u0026Services...");
                        }
                        else //Cal
                        {
                            helperClass.CallUsers(phonenumber, context);
                        }
                    }

                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        }
    }


    @Override
    public int getItemCount() {
        return annonceClassList.size();
    }
}
class ItemAnnonceHolder extends RecyclerView.ViewHolder {

    public TextView lbl_title, lbl_heart, lbl_name, lbl_price, lbl_distance, lbl_time, lbl_coup_de_pouce, lbl_town, lbl_klm;

    public CardView cardView;

    public ItemAnnonceHolder(View itemView) {
        super(itemView);
        lbl_time = itemView.findViewById(R.id.txt_time);
        cardView = itemView.findViewById(R.id.cardview);

        lbl_coup_de_pouce = itemView.findViewById(R.id.LayPouce);
        lbl_town = itemView.findViewById(R.id.LayDist);

        lbl_klm = itemView.findViewById(R.id.txt_km);

        lbl_name = itemView.findViewById(R.id.txt_name);
        lbl_heart = itemView.findViewById(R.id.txt_heart);
        lbl_price = itemView.findViewById(R.id.txt_prix);
        lbl_title = itemView.findViewById(R.id.txt_title);
        lbl_distance = itemView.findViewById(R.id.txt_distance);


    }
}

class ItemImageHolder extends RecyclerView.ViewHolder {

    public TextView lbl_title, lbl_heart, lbl_name, lbl_price, lbl_distance, lbl_time, lbl_coup_de_pouce, lbl_town, lbl_klm;

    public CardView cardView;
    ImageView imageView;

    public ItemImageHolder(View itemView) {
        super(itemView);
        lbl_time = itemView.findViewById(R.id.txt_time);
        cardView = itemView.findViewById(R.id.cardview);

        lbl_coup_de_pouce = itemView.findViewById(R.id.LayPouce);
        lbl_town = itemView.findViewById(R.id.LayDist);

        lbl_klm = itemView.findViewById(R.id.txt_km);

        lbl_name = itemView.findViewById(R.id.txt_name);
        lbl_heart = itemView.findViewById(R.id.txt_heart);
        lbl_price = itemView.findViewById(R.id.txt_prix);
        lbl_title = itemView.findViewById(R.id.txt_title);
        lbl_distance = itemView.findViewById(R.id.txt_distance);

        imageView = itemView.findViewById(R.id.imageview);


    }
}


class LoadingViewHolder extends RecyclerView.ViewHolder {

        ProgressBar progressBar;

        public LoadingViewHolder(@NonNull View itemView) {
            super(itemView);
            progressBar = itemView.findViewById(R.id.progressBar);
        }

}

