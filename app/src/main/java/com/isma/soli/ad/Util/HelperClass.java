package com.isma.soli.ad.Util;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.PermissionChecker;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.isma.soli.ad.InternalDatabase.DBSimpleIntel;
import com.isma.soli.ad.LoginScreen.LoginActivity;
import com.isma.soli.ad.Map.MapsActivity;
import com.isma.soli.ad.R;
import com.nabinbhandari.android.permissions.PermissionHandler;
import com.nabinbhandari.android.permissions.Permissions;
import com.yarolegovich.lovelydialog.LovelyChoiceDialog;
import com.yarolegovich.lovelydialog.LovelyInfoDialog;
import com.yarolegovich.lovelydialog.LovelyStandardDialog;
import com.yarolegovich.lovelydialog.LovelyTextInputDialog;

import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import static android.content.Context.LOCATION_SERVICE;
import static android.os.Looper.getMainLooper;
import static android.telephony.CellLocation.requestLocationUpdate;
import static androidx.constraintlayout.widget.Constraints.TAG;

public class HelperClass
{

    private  FusedLocationProviderClient client;

    SimpleDateFormat FullDateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm");


    private static final String FINE_LOCATION = Manifest.permission.ACCESS_FINE_LOCATION;
    private static final String COURSE_LOCATION = Manifest.permission.ACCESS_COARSE_LOCATION;
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1234;

    public void SendUserToOtherActivity(Activity Activity, Class<?> T, Context context) //fonction qui permet d'aller de n'importe quelle activity à n'importe quelle activity
        {
            Intent intent = new Intent(Activity, T);
            context.startActivity(intent);
        }

        public void SendUserToOtherActivityAndFinishThisActivity(Activity Activity, Class<?> T, Context context) //fonction qui permet d'aller de n'importe quelle activity à n'importe quelle activity en fermant l'activité d'où on vient
        {
            Intent intent = new Intent(Activity, T);
            context.startActivity(intent);
            Activity.finish();
        }

    public void SendUserToOtherActivityAndFinishAllActivity(Activity Activity, Class<?> T, Context context) //fonction qui permet d'aller de n'importe quelle activity à n'importe quelle activity en fermant l'activité d'où on vient
    {
        Intent intent = new Intent(Activity, T);
        context.startActivity(intent);
        Activity.finish();
    }

        public void SendUserToOtherActivityFragment(Context context, Class<?> T) //fonction qui permet d'aller de n'importe quelle activity à n'importe quelle activity
        {
            Intent intent = new Intent(context, T);
            context.startActivity(intent);
        }
    public void SendUserToOtherActivityFragmentAndfinish(Context context, Class<?> T) //fonction qui permet d'aller de n'importe quelle activity à n'importe quelle activity
    {
        Intent intent = new Intent(context, T);
        context.startActivity(intent);
        ((Activity) context).finish();
    }
    public void simpleDialog(String title, String hart, Context context)
    {
        new LovelyInfoDialog(context)
                .setTopColorRes(R.color.colorPrimary)
                .setIcon(android.R.drawable.ic_dialog_info)
                .setConfirmButtonText("Ok")
                .setTitle(title)
                .setMessage(hart)
                .show();
    }
    public float round(float d, int decimalPlace)
    {
        BigDecimal bd = new BigDecimal(Float.toString(d));
        bd = bd.setScale(decimalPlace, BigDecimal.ROUND_HALF_UP);
        return bd.floatValue();
    }
    public  void CallUsers(String phone, Context context)
    {
        Log.i("CheckCall", "Calling");
        Intent callIntent = new Intent(Intent.ACTION_DIAL);
        callIntent.setData(Uri.parse("tel:" +phone));//change the number
        context.startActivity(callIntent);
    }
    public void SendSms(String phone, Context context, String body)
    {
        Log.i("CheckSms", "Sending sms");
        Intent sendIntent = new Intent(Intent.ACTION_VIEW);
        sendIntent.setData(Uri.parse("sms:" + phone));
        sendIntent.putExtra("sms_body", body);
        context.startActivity(sendIntent);
    }
    public void ChooseBetweenCallORSendSms(final String phone, String name, final Context context, final String body)
    {

        new LovelyStandardDialog(context, LovelyStandardDialog.ButtonLayout.VERTICAL)
                .setTopColorRes(R.color.colorPrimary)
                .setIcon(android.R.drawable.ic_dialog_info)
                .setTitle("Contacter " + name)
                .setIcon(R.drawable.icon8_message)
                .setPositiveButton("Appeler", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        CallUsers(phone, context);
                    }
                })
                .setNegativeButton("Envoyer un message", new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        SendSms(phone, context, body);

                    }
                })
                .show();
        /*new AlertDialog.Builder(context)
                .setTitle("Contacter " + name)

                // Specifying a listener allows you to take an action before dismissing the dialog.
                // The dialog is automatically dismissed when a dialog button is clicked.
                .setPositiveButton("Appeler", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which)
                    {
                        CallUsers(phone, context);
                    }
                })
                .setNegativeButton("Envoyer un message", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        SendSms(phone, context, body);
                    }
                })

                .setIcon(android.R.drawable.ic_dialog_info)
                .show();*/
    }
    public boolean isNetworkAvailable(Context context) {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        if (activeNetworkInfo != null && activeNetworkInfo.isConnected())
        {
            Log.i("CheckNewtork", "Connexion true" );

        }
        else
        {
            Log.i("CheckNewtork", "Connexion false" );

        }
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }
    public boolean isNetworkAvailableDialog( Context context)
    {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        if (activeNetworkInfo != null && activeNetworkInfo.isConnected())
        {
            Log.i("CheckNewtork", "Connexion true" );
        }
        else
        {
            Log.i("CheckNewtork", "Connexion false" );
            simpleDialog("Connexion", "Veuillez vous connecter à internet", context);

        }
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }
    public boolean isUserLoggedInDialog(final Context context, String title, final Activity activity)
    {
        FirebaseUser currentUser;
        FirebaseAuth mAuth;
        mAuth= FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();
        String uDDB = DBSimpleIntel.getInstance(context).getLastValue(StaticValues.UserID);
        if(!TextUtils.isEmpty(uDDB))
        {
            Log.i("CheckUserID", ""+ uDDB);
        }

        if (currentUser != null && !TextUtils.isEmpty(uDDB))
        {
            return true;
        }
        else
        {
            new LovelyStandardDialog(context, LovelyStandardDialog.ButtonLayout.VERTICAL)
                    .setTopColorRes(R.color.colorPrimary)
                    .setIcon(android.R.drawable.ic_dialog_info)
                    .setTitle("Inscription")
                    .setMessage(title)
                    .setPositiveButton("M'inscrire", new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            SendUserToOtherActivity(activity, LoginActivity.class, context);
                        }
                    })
                    .setNegativeButton("Plus tard", new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                        }
                    })
                    .show();
            //DialogLogin!
            return false;
        }
    }
    public boolean isLocationEnabled(Context context)
    {
        LocationManager lm = (LocationManager)context.getSystemService(LOCATION_SERVICE);
        boolean gps_enabled = false;
        boolean network_enabled = false;
        try {
            gps_enabled = lm.isProviderEnabled(LocationManager.GPS_PROVIDER);
        } catch(Exception ex) {}

        if (gps_enabled)
        {
            Log.i("CheckNewtork", "Gps true" );
        }
        else
        {
            Log.i("CheckNewtork", "Gps false" );
        }

        try {
            network_enabled = lm.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
        } catch(Exception ex) {}


        if (network_enabled)
        {
            Log.i("CheckNewtork", "Network true" );
        }
        else
        {
            Log.i("CheckNewtork", "Network false" );
        }
        return (gps_enabled);

    }

    public void getLocationDetails(LatLng latLng, Context context){

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

          //  city = addresses.get(0).getLocality();
           // DBSimpleIntel.getInstance(context).addElementTodB(StaticValues.city, city);

           // state = addresses.get(0).getAdminArea();
           // DBSimpleIntel.getInstance(context).addElementTodB(StaticValues.state, state);

            country = addresses.get(0).getCountryName();
            if (!TextUtils.isEmpty(country))
            {
                DBSimpleIntel.getInstance(context).addElementTodB(StaticValues.country, country);
                Log.i("CountryName", country);
            }

            postalCode = addresses.get(0).getPostalCode();
            if (!TextUtils.isEmpty(postalCode))
             {
                 DBSimpleIntel.getInstance(context).addElementTodB(StaticValues.postalCode, postalCode);

             }

            //knonName = addresses.get(0).getFeatureName();
            //DBSimpleIntel.getInstance(context).addElementTodB(StaticValues.knonName, knonName);

        } catch (IOException e) {
            e.printStackTrace();
            Log.e(TAG, "getLocationDetails" +
                    ": SecurityException: " + e.getMessage() );
        }
        Log.i("PostalCode", "Code " + postalCode);

      //  ((MapsActivity)context).Test();


    }

    public boolean VerifyIsLocationIsEnabled(Context context)
    {
        String requiredPermission1 = android.Manifest.permission.ACCESS_COARSE_LOCATION;
        String requiredPermission2 = android.Manifest.permission.ACCESS_FINE_LOCATION;

        int checkVal1 = context.checkCallingOrSelfPermission(requiredPermission1);
        int checkVal2 = context.checkCallingOrSelfPermission(requiredPermission2);

        if (checkVal1==PackageManager.PERMISSION_GRANTED && checkVal2==PackageManager.PERMISSION_GRANTED)
        {
            return true;
        }
        else
        {
            return  false;
        }
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
                      getInstantDeviceLocation(context, activity);
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
                DBSimpleIntel.getInstance(context).addElementTodB(StaticValues.PermissionGrantedLoca, "OK");
                requestLocationUpdate(context,activity);
            }

            @Override
            public void onDenied(Context context, ArrayList<String> deniedPermissions)
            {
                super.onDenied(context, deniedPermissions);
               // getInstantDeviceLocation(context, activity);
            }
        });
    }
    public void requestLocationUpdate(final Context context, final Activity activity)
    {
        FusedLocationProviderClient fusedLocationProviderClient;
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
                        DBSimpleIntel.getInstance(context).addElementTodB(StaticValues.LastLatitude, Double.toString(Latit));
                        DBSimpleIntel.getInstance(context).addElementTodB(StaticValues.LastLongitude, Double.toString(Longit));
                        getLocationDetails(latLong, context);
                    }
                }
            }, getMainLooper());

        }else getInstantDeviceLocation(context,activity);
    }
    public String returnDataFromMillis(Long time)
    {
        Date result = new Date(time);
        return  FullDateFormat.format(result);

    }
    public boolean CheckSizeDialog(Context context, String tocheck, int maxsize, String errorMessage)
    {
        if (tocheck.length() < maxsize)
        {
            return true;
        }
        else
        {
            simpleDialog("Trop de caractères", errorMessage, context);
            return false;
        }
    }
    public void FeedBack(final Context context)
    {

        String av= "";
        if (DBSimpleIntel.getInstance(context).checkAlreadyExist(StaticValues.FeedBack))
        {
             av = DBSimpleIntel.getInstance(context).getLastValue(StaticValues.FeedBack);

        }
        Dialog dialog = new LovelyTextInputDialog(context, R.style.EditTextTintTheme)
                .setTopColorRes(R.color.colorPrimary)
                .setTitle("Feedback")
                .setMessage("Donnez nous votre avis !")
                .setHint("Entrez votre avis...")
                .setInitialInput(av)
                .setConfirmButton(android.R.string.ok, new LovelyTextInputDialog.OnTextInputConfirmListener()
                {
                    @Override
                    public void onTextInputConfirmed(String text)
                    {
                        if (!text.equals("")) {

                            FirebaseDatabase.getInstance().getReference().child("Feedback").push().setValue(text).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    DBSimpleIntel.getInstance(context).Deletecom(StaticValues.FeedBack);
                                }
                            });
                            Toast.makeText(context, "Merci de votre avis!", Toast.LENGTH_SHORT).show();
                        }
                    }
                })
                .addTextWatcher(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                    }

                    @Override
                    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                        String fb = charSequence.toString();
                        DBSimpleIntel.getInstance(context).addElementTodB(StaticValues.FeedBack, fb);
                    }

                    @Override
                    public void afterTextChanged(Editable editable) {

                    }
                })
                .show();

        dialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialogInterface) {

            }
        });
    /*    txt_fb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String url = "https://www.facebook.com/HelpServices-111304393854486";
                Intent i = new Intent(Intent.ACTION_VIEW);
                i.setData(Uri.parse(url));
                context.startActivity(i);
            }
        });*/
    }

   /* public String RoundToFour(float number) {
        String RoundLatt = Double.toString(number);
        Log.i("CheckSystemInterval", "RoundLatt" + number);

        number = number * 10000; //On peut pas avoir de point dans Firebase donc au lieu de stocker 50.4444 on stock 504444


        int count = 0;
        for (int i = 0; i < RoundLatt.length(); i++) {
            //on regarde quand il y a une virgule pour savoir où supprimer les chiffres en trop
            if ((RoundLatt.charAt(i) == '.')) {
                count = i; //la virgule est en i.
                break;
            }
        }

        //
        String RoundLatt2 = Double.toString(number);
        Log.i("CheckSystemInterval", "RoundLatt2 " + RoundLatt2);

        String reformLatt = "";
        for (int i = 0; i < RoundLatt2.length(); i++) {
            if (i == count + 4) //si i >= count ça veut dire qu'on serait 4 chiffres après la virgule si on avait pas fait * 10000
            {
                for (int y = 0; y < i; y++) {
                    reformLatt = reformLatt + RoundLatt2.charAt(y);
                }
                break; //Et voilà on récupère 504444
            }
        }
        Log.i("CheckSystemInterval", "reformLatt " + reformLatt);
        return reformLatt;
    }*/
    public String returnCenter(Double lattitude)
    {
        int multiple = 10000;
        int DistRayonToDegre = 450;
        double intervalLatt= ((lattitude*multiple) - 500000)/DistRayonToDegre;  //On prend la lattitude 50 comme référence

        int N = (int) intervalLatt; //on prend la valeur entière N = nombre d'interval entre la référence 50 et la lattitude choisie


        //IMPAIR --> Si on prend un pas de 1 comme interval on a [47, 49] , [49, 51] , [51, 53] ,... soit une distance de 2 entre chaque extrémité
        //Donc si j'ai une lattitude de 53.4, je ne peux pas prendre 53 comme centre de mon interval mais je dois prendre 54.
        //Si impair on fait N+1
        //Sinon N reste N.

        if (Math.abs(N) % 2 ==1)
        {
            if ( N >0) {
                N = N + 1; //pour N < 0 et N > 0 ça revient au même
            }
            else
            {
                N = N+1;
            }

        }
        else
        {
            N = N;
        }
        // int test = 500000 + N * 450;

        //float centreLatt = 50 + (float) N * DistRayonToDegre;

        int centreLatt = 500000 +  N * DistRayonToDegre;

        Log.i("CheckSystemInterval", "Centre " + centreLatt);


        return Integer.toString(centreLatt);
    }
    /*public String returnCenter(Double lattitude)
    {
        float DistRayonToDegre = 0.045f;
        double intervalLatt= (lattitude - 50)/DistRayonToDegre;  //On prend la lattitude 50 comme référence

        int N = (int) intervalLatt; //on prend la valeur entière N = nombre d'interval entre la référence 50 et la lattitude choisie


        //IMPAIR --> Si on prend un pas de 1 comme interval on a [47, 49] , [49, 51] , [51, 53] ,... soit une distance de 2 entre chaque extrémité
        //Donc si j'ai une lattitude de 53.4, je ne peux pas prendre 53 comme centre de mon interval mais je dois prendre 54.
        //Si impair on fait N+1
        //Sinon N reste N.

        if (Math.abs(N) % 2 ==1)
        {
            if ( N >0) {
                N = N + 1; //pour N < 0 et N > 0 ça revient au même
            }
            else
            {
                N = N+1;
            }

        }
        else
        {
            N = N;
        }
       // int test = 500000 + N * 450;

        //float centreLatt = 50 + (float) N * DistRayonToDegre;

        float centreLatt = 50 +  N * DistRayonToDegre;

        Log.i("CheckSystemInterval", "Centre" + centreLatt);


        return RoundToFour(centreLatt);
    }*/
    public float ReturnDistanceBetweenTwoCoordonates(double lat1, double lat2, double lon1, double lon2) {
        final int R = 6371; // Radius of the earth

        Log.i("CheckCalculCoordonates", "lat1 " + lat1);

        Log.i("CheckCalculCoordonates", "lat2 " + lat2);


        double latDistance = Math.toRadians(lat2 - lat1);
        double lonDistance = Math.toRadians(lon2 - lon1);
        double a = Math.sin(latDistance / 2) * Math.sin(latDistance / 2)
                + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2))
                * Math.sin(lonDistance / 2) * Math.sin(lonDistance / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));


        double distance = R * c; // convert to meters

        //  double height = el1 - el2;

        //  distance = Math.pow(distance, 2) + Math.pow(height, 2);
        int around = (int) distance;

        float findis = 0f;
        double dis_around = distance - around;

        if (dis_around <= 0.25) {
            findis = (float) around;
        } else {
            if ((dis_around > 0.25 && dis_around <= 0.5) || (dis_around > 0.5 && dis_around <= 0.75)) {
                findis = (float) around + 0.5f;
            } else {
                if (dis_around > 0.75) {
                    findis = (float) around + 1f;
                }
            }
        }
        return findis;
    }

    public void DeleteAnnonce(String query, String ID, Context context, String centerInt, String userID)
    {



        ContextWrapper cw = new ContextWrapper(context);
        final File directory = cw.getDir(StaticValues.DirectoryImageName, Context.MODE_PRIVATE); // path to /data/data/yourapp/app_imageDir

        String FilePath = ID + ".jpg";

        final File myImageFile = new File(directory, FilePath); // Create image file

        try {
            if (myImageFile.exists()) {
                myImageFile.delete();
            }
        }
        catch (Exception e){}

        StorageReference RestoRef = FirebaseStorage.getInstance().getReference().child(DBSimpleIntel.getInstance(context).getLastValue(StaticValues.country
        )).child(query).child(FilePath);
        RestoRef.delete();

        if (DBSimpleIntel.getInstance(context).checkAlreadyExist(StaticValues.country));
        {
            String country = DBSimpleIntel.getInstance(context).getLastValue(StaticValues.country);
            if (!TextUtils.isEmpty(country))
            {

                if (!TextUtils.isEmpty(centerInt)) {
                    if (!TextUtils.isEmpty(userID)) {
                        FirebaseDatabase.getInstance().getReference().child(StaticValues.UserPath).child(userID).child(StaticValues.annonce).child(ID).removeValue();
                    }
                    FirebaseDatabase.getInstance().getReference().child(StaticValues.Distance).child(StaticValues.Interval).child(country).child(query).child(centerInt).child(ID).removeValue();
                    FirebaseDatabase.getInstance().getReference().child(StaticValues.AnnoncePath).child(country).child(query).child(ID).removeValue();
                }
                if (DBSimpleIntel.getInstance(context).checkAlreadyExist(ID + StaticValues.CreateLoca))
                {
                    FirebaseDatabase.getInstance().getReference().child(StaticValues.LocaPath).child(country).child(query+"l").child(ID).removeValue();
                }
            }

        }
    }
    public static void hideKeyboard(Activity activity) {
        InputMethodManager imm = (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
        //Find the currently focused view, so we can grab the correct window token from it.
        View view = activity.getCurrentFocus();
        //If no view currently has focus, create a new one, just so we can grab a window token from it
        if (view == null) {
            view = new View(activity);
        }
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }


    public void VerifyIfUsersIsInBlackList(Context context)
    {
        String uid = DBSimpleIntel.getInstance(context).getLastValue(StaticValues.UserID);
        if (!TextUtils.isEmpty(uid)) {
            FirebaseDatabase.getInstance().getReference().child("BlackList").child(uid).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()) {
                        FirebaseAuth mAuth;
                        mAuth = FirebaseAuth.getInstance();
                        mAuth.signOut();
                        if (dataSnapshot.child("m").exists()) {
                            try {

                                Log.i("CheckBlackList", dataSnapshot.child("m").getValue().toString());
                            } catch (Exception e) {
                            }
                        } else {
                            Log.i("CheckBlackList", "False");
                        }
                    } else {
                        Log.i("CheckBlackList", "False");

                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });

        }
    }
}
