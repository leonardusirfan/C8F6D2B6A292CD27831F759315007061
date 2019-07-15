package id.net.gmedia.pal;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.support.v4.app.NotificationCompat;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import id.net.gmedia.pal.Activity.Approval.ApprovalPO;
import id.net.gmedia.pal.Activity.Approval.ApprovalPelanggan;
import id.net.gmedia.pal.Activity.Approval.ApprovalRetur;
import id.net.gmedia.pal.Activity.Approval.ApprovalSo;
import id.net.gmedia.pal.Util.AppSharedPreferences;

public class AppFirebaseMessagingService extends FirebaseMessagingService {
    @Override
    public void onNewToken(String s) {
        super.onNewToken(s);
        AppSharedPreferences.setFcmId(this, s);
    }

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);

        String title = "PAL";
        String body = "anda mendapat notifikasi";
        String type = "";

        if(remoteMessage.getData() != null){
            title = remoteMessage.getData().get("title");
            body = remoteMessage.getData().get("body");
            type = remoteMessage.getData().get("type");
        }

        if(remoteMessage.getNotification() != null){
            title = remoteMessage.getNotification().getTitle();
            body = remoteMessage.getNotification().getBody();
        }

        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        String NOTIFICATION_CHANNEL_ID = getResources().getString(R.string.notification_channel_id);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel notificationChannel =
                    new NotificationChannel(NOTIFICATION_CHANNEL_ID, title,
                            NotificationManager.IMPORTANCE_HIGH);

            // Configure the notification channel.
            notificationChannel.setDescription(title);
            notificationChannel.enableLights(true);
            notificationChannel.setLightColor(Color.RED);
            notificationChannel.setVibrationPattern(new long[]{0, 1000, 500, 1000});
            notificationChannel.enableVibration(true);
            notificationManager.createNotificationChannel(notificationChannel);
        }

        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this, NOTIFICATION_CHANNEL_ID);

        notificationBuilder.setAutoCancel(true)
                .setDefaults(Notification.DEFAULT_ALL)
                .setWhen(System.currentTimeMillis())
                .setSmallIcon(R.mipmap.ic_launcher)
                .setTicker(title)
                //     .setPriority(Notification.PRIORITY_MAX)
                /*.setContentTitle(remoteMessage.getNotification().getTitle())
                .setContentText(remoteMessage.getNotification().getBody())
                .setContentInfo(remoteMessage.getNotification().getTag());*/
                .setContentTitle(title)
                .setContentText(body);
        //.setContentInfo(remoteMessage.getData().get("key_1"));

        // notification click action
        if(AppSharedPreferences.getJabatan(this).equals("Manager") ||
                AppSharedPreferences.getJabatan(this).equals("Direktur")){
            if(type != null){
                switch (type){
                    case "customer":{
                        Intent notificationIntent = new Intent(this, ApprovalPelanggan.class);
                        PendingIntent resultPendingIntent = PendingIntent.getActivity(this, 0,
                                notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);
                        notificationBuilder.setContentIntent(resultPendingIntent);
                        break;
                    }
                    case "purchase_order":{
                        Intent notificationIntent = new Intent(this, ApprovalPO.class);
                        PendingIntent resultPendingIntent = PendingIntent.getActivity(this, 0,
                                notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);
                        notificationBuilder.setContentIntent(resultPendingIntent);
                        break;
                    }
                    case "sales_order":{
                        Intent notificationIntent = new Intent(this, ApprovalSo.class);
                        PendingIntent resultPendingIntent = PendingIntent.getActivity(this, 0,
                                notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);
                        notificationBuilder.setContentIntent(resultPendingIntent);
                        break;
                    }
                    case "retur_jual":{
                        Intent notificationIntent = new Intent(this, ApprovalRetur.class);
                        PendingIntent resultPendingIntent = PendingIntent.getActivity(this, 0,
                                notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);
                        notificationBuilder.setContentIntent(resultPendingIntent);
                        break;
                    }
                    default:{
                        Intent notificationIntent = new Intent(this, MainActivity.class);
                        PendingIntent resultPendingIntent = PendingIntent.getActivity(this, 0,
                                notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);
                        notificationBuilder.setContentIntent(resultPendingIntent);
                        break;
                    }
                }
            }
            else{
                Intent notificationIntent = new Intent(this, MainActivity.class);
                PendingIntent resultPendingIntent = PendingIntent.getActivity(this, 0,
                        notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);
                notificationBuilder.setContentIntent(resultPendingIntent);
            }

            notificationManager.notify(1, notificationBuilder.build());
        }
    }
}
