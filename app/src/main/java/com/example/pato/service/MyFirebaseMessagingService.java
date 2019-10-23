package com.example.pato.service;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import androidx.core.app.NotificationCompat;

import com.example.pato.MainActivity;
import com.example.pato.OptionDatabase;
import com.example.pato.R;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

public class MyFirebaseMessagingService extends FirebaseMessagingService{

    private String databaseName = "option.db";
    private String tableName = "option";
    private SQLiteDatabase database;
    private OptionDatabase optionDatabase;
    private NotificationCompat.Builder notificationBuilder;
    private int BadgeCount = 1;

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        // Check if message contains a data payload.
        if (remoteMessage.getData().size() > 0 ) {
            if(remoteMessage.getData().get("title") != null && remoteMessage.getData().get("gcmUid") != null ){
                String title = remoteMessage.getData().get("title");
                String boardUid = remoteMessage.getData().get("gcmUid");
                if(selectOption(tableName)){
                    sendNotification(title,boardUid);
                }
            }else if(remoteMessage.getData().get("title") != null && remoteMessage.getData().get("writer") != null  && remoteMessage.getData().get("noteVersion") != null  && remoteMessage.getData().get("year") != null ){
                String title = remoteMessage.getData().get("title");
                String writer = remoteMessage.getData().get("writer");
                String noteVersion = remoteMessage.getData().get("noteVersion");
                String year = remoteMessage.getData().get("year");

                if(selectOption(tableName)) {
                    patchNotification(title, writer, noteVersion, year);
                }
            }
            ++BadgeCount;
        }

        if(remoteMessage.getFrom().equals("/topics/optionOn")){
                String title = remoteMessage.getData().get("title");
                String text = remoteMessage.getData().get("text");
                contestSendNotification(title,text);
            ++BadgeCount;
        }

    }

    private void patchNotification(String title, String writer, String noteVersion, String year) {
        Intent intent = new Intent(this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.putExtra("patchnotealarmcheck",true);
        intent.putExtra("title",title);
        intent.putExtra("noteVersion",noteVersion);
        intent.putExtra("year",year);

        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0 /* Request code */, intent,
                PendingIntent.FLAG_ONE_SHOT);

        String channelId = "pato";

        Drawable drawable = getResources().getDrawable(R.drawable.main_icon_paint);
        Bitmap bitmap = ((BitmapDrawable)drawable).getBitmap();

        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        notificationBuilder =
                new NotificationCompat.Builder(this)
                        .setSmallIcon(R.drawable.main_icon_paint)
                        .setContentTitle(writer)
                        .setContentText("새 댓글이 작성되었습니다.")
                        .setAutoCancel(true)
                        .setSound(defaultSoundUri)
                        .setPriority(Notification.PRIORITY_HIGH)
                        .setContentIntent(pendingIntent)
                        .setChannelId(channelId)
                        .setLargeIcon(bitmap);

        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(channelId,
                    channelId,
                    NotificationManager.IMPORTANCE_HIGH);
            notificationManager.createNotificationChannel(channel);
        }

        notificationManager.notify(BadgeCount /* ID of notification */, notificationBuilder.build());

    }

    private void sendNotification(String title, String boardUid) {

        Intent intent = new Intent(this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.putExtra("alarmcheck",true);
        intent.putExtra("bid",boardUid);

        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0 /* Request code */, intent,
                PendingIntent.FLAG_ONE_SHOT);

        String channelId = "pato";

        Drawable drawable = getResources().getDrawable(R.drawable.main_icon_paint);
        Bitmap bitmap = ((BitmapDrawable)drawable).getBitmap();

        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        notificationBuilder =
                new NotificationCompat.Builder(this)
                        .setSmallIcon(R.drawable.main_icon_paint)
                        .setContentTitle(title)
                        .setContentText("새 댓글이 작성되었습니다.")
                        .setAutoCancel(true)
                        .setSound(defaultSoundUri)
                        .setPriority(Notification.PRIORITY_HIGH)
                        .setContentIntent(pendingIntent)
                        .setChannelId(channelId)
                .setLargeIcon(bitmap);

        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(channelId,
                    channelId,
                    NotificationManager.IMPORTANCE_HIGH);
            notificationManager.createNotificationChannel(channel);
        }

        notificationManager.notify(BadgeCount /* ID of notification */, notificationBuilder.build());
    }

    private void contestSendNotification(String title, String text) {

        Intent intent = new Intent(this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.putExtra("contestalarmcheck",true);

        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0 /* Request code */, intent,
                PendingIntent.FLAG_ONE_SHOT);

        String channelId = "pato";

        Drawable drawable = getResources().getDrawable(R.drawable.main_icon_paint);
        Bitmap bitmap = ((BitmapDrawable)drawable).getBitmap();

        Uri defaultSoundUri= RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        notificationBuilder =
                new NotificationCompat.Builder(this)
                        .setSmallIcon(R.drawable.main_icon_paint)
                        .setContentTitle(title)
                        .setContentText(text)
                        .setAutoCancel(true)
                        .setSound(defaultSoundUri)
                        .setPriority(Notification.PRIORITY_HIGH)
                        .setContentIntent(pendingIntent)
                        .setChannelId(channelId)
                        .setLargeIcon(bitmap);

        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(channelId,
                    channelId,
                    NotificationManager.IMPORTANCE_HIGH);
            notificationManager.createNotificationChannel(channel);
        }

        notificationManager.notify(BadgeCount /* ID of notification */, notificationBuilder.build());
    }



    private boolean selectOption(String tableName){
        optionDatabase = new OptionDatabase(this, databaseName,null,1);
        database = optionDatabase.getWritableDatabase();

        String alarm = "";
        if(database != null){
            String sql = "select boardalarm from " + tableName;
            Cursor cursor = database.rawQuery(sql,null);
            cursor.moveToNext();
            alarm = cursor.getString(0);
        }

        if(alarm.equals("on")){
            return true;
        }else{
            return false;
        }
    }



}
