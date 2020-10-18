package com.isma.soli.ad.FirebaseMessage;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Looper;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.isma.soli.ad.Profil.ProfilActivity;
import com.isma.soli.ad.R;
import com.isma.soli.ad.Util.StaticValues;

import java.util.Random;

public class NotificationService extends FirebaseMessagingService
{
    private static String MyChannelID = "H1PM2P";
    private static String MyChannelDescription = "Réponse";
    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);



        Log.i("CheckForeGround", "enter!");


        String title = remoteMessage.getData().get("title");
        String name = remoteMessage.getData().get("name");
        String phone = remoteMessage.getData().get("phone");

        Log.i("CheckForeGround", "Title "+ remoteMessage.getData().get("title"));
        Log.i("CheckForeGround", "Name "+ remoteMessage.getData().get("name"));
        Log.i("CheckForeGround", "Phone "+ remoteMessage.getData().get("phone"));


        String body =  name + " vous a envoyé un message !";

        if (!TextUtils.isEmpty(title) && (!TextUtils.isEmpty(name)) && !TextUtils.isEmpty(phone))
        {
            if (!title.equals("undefined") && !name.equals("undefined") && !phone.equals("undefined")) {
                showNotification(title, body, phone);
            }
        }
    }
    void showNotification(String title, String message, String phone) {

        Random random = new Random();
        int m = random.nextInt(9999 - 1000) + 1000;

        NotificationManager mNotificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O)
        {
            NotificationChannel channel = new NotificationChannel(phone,
                    getString(R.string.app_name),
                    NotificationManager.IMPORTANCE_DEFAULT);
            channel.setDescription(title);
         //   channel.notify((int) Math.random() * 15000), notification);
            mNotificationManager.createNotificationChannel(channel);
        }
        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(getApplicationContext(), phone)
                .setSmallIcon(R.drawable.icon8_message) // notification icon
                .setContentTitle(title) // title for notification
                .setContentText(message)// message for notification
                .setAutoCancel(true); // clear notification after click

       // Log.i("CheckCall", "Calling");
       // Intent callIntent = new Intent(Intent.ACTION_DIAL);
       // callIntent.setData(Uri.parse("tel:" +phone));//change the number

        Intent intent = new Intent(getApplicationContext(), ProfilActivity.class);
        intent.putExtra(StaticValues.IntentProfil, StaticValues.IntentProfil);
        PendingIntent pi = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        mBuilder.setContentIntent(pi);
        mNotificationManager.notify(m, mBuilder.build());
    }


}
