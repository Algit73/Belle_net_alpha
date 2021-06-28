package com.soluk.belle_net_alpha;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

public class FireBase_Push_Notification_Service extends FirebaseMessagingService
{

    static final int BELLENET_REQUEST_CODE = 765337;
    public FireBase_Push_Notification_Service()
    {
    }


    @Override
    public void onMessageReceived(@NonNull RemoteMessage remoteMessage)
    {
        super.onMessageReceived(remoteMessage);
        sendNotification(remoteMessage);
    }

    private void sendNotification(RemoteMessage remoteMessage)
    {
        Intent intent = new Intent(this, Main_Activity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this,
                BELLENET_REQUEST_CODE, intent, PendingIntent.FLAG_ONE_SHOT);
        String channelId = getString(R.string.bellenet_notification_id);
        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder notificationBuilder;
        notificationBuilder =
                new NotificationCompat.Builder(this, channelId)
                        .setSmallIcon(R.drawable.bellenet_bw_small_icon)
                        .setContentTitle(remoteMessage.getNotification().getTitle())
                        .setContentText(remoteMessage.getNotification().getBody())
                        .setAutoCancel(true)
                        .setSound(defaultSoundUri)
                        //.setContentTitle(remoteMessage.getData.get("your_key1")
                          //      .setContentText(remoteMessage.getData.get("your_key2")
                        .setContentIntent(pendingIntent);
        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

// Since android Oreo notification channel is needed.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
        {
            NotificationChannel channel = new NotificationChannel(channelId,
                    "Channel human readable title",
                    NotificationManager.IMPORTANCE_DEFAULT);
            notificationManager.createNotificationChannel(channel);
        }
        notificationManager.notify(BELLENET_REQUEST_CODE, notificationBuilder.build());
    }
    @Override
    public void onNewToken(String s)
    {
        super.onNewToken(s);
        Log.d("main_activity","device token: " + s);
    }

}