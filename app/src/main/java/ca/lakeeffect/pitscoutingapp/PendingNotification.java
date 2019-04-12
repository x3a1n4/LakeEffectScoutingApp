package ca.lakeeffect.pitscoutingapp;

import android.app.Activity;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.support.v4.app.NotificationCompat;

import java.util.Random;


public class PendingNotification extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        send(context);
    }

    public void send(Context context) {

        String message;

        //If there are no pending messages, dont send anything
        if(context.getSharedPreferences("pendingMessages", Activity.MODE_PRIVATE).getInt("messageAmount", -1) <= 0){
            return;
        }
        
        //Create the message
        message = "You have "+ context.getSharedPreferences("pendingMessages", Activity.MODE_PRIVATE).getInt("messageAmount", -1) +" unsent data. Contact Ajay to get the data into the database ASAP!";
        
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        //Set title
        NotificationCompat.BigTextStyle style = new NotificationCompat.BigTextStyle()
                .setBigContentTitle("Unsent Data!")
                .bigText(message);

        //Crate notification
        NotificationCompat.Builder notification = new NotificationCompat.Builder(context)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setLargeIcon(BitmapFactory.decodeResource(context.getResources(),R.mipmap.ic_launcher))
                .setContentTitle("Unsent Data!")
                .setContentText(message)
                .setPriority(NotificationCompat.PRIORITY_MAX)
                .setVibrate(new long[]{1000,1000,1000,1000,1000})
                .setStyle(style);
        //If you want to have multiple notifications co-exist, create a new number each time
        notificationManager.notify(1, notification.build());
    }

}
