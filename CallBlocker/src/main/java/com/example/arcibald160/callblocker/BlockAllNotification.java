package com.example.arcibald160.callblocker;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

import com.example.arcibald160.callblocker.tools.SharedPreferencesHelper;

public class BlockAllNotification extends Service {

    private static final int NOTIFICATION_ID = 1;

    public BlockAllNotification() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        if (intent.hasExtra(getString(R.string.stop_blockall))) {
            if (intent.getBooleanExtra(getString(R.string.stop_blockall), false)) {
                SharedPreferencesHelper sHelper = new SharedPreferencesHelper(getApplicationContext());
                sHelper.setBlockAllState(false);
                stopForeground(true);
                stopSelf();
            }
        }

        // intent to return to CallBlock application
        Intent showTaskIntent = new Intent(getApplicationContext(), MainActivity.class);
        showTaskIntent.setAction(Intent.ACTION_MAIN);
        showTaskIntent.addCategory(Intent.CATEGORY_LAUNCHER);
        showTaskIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);


        PendingIntent contentIntent = PendingIntent.getActivity(
                getApplicationContext(),
                0,
                showTaskIntent,
                PendingIntent.FLAG_UPDATE_CURRENT);

        // action button for disabling global block
        Intent disableIntent = new Intent(this, BlockAllNotification.class);
        disableIntent.putExtra(getString(R.string.stop_blockall), true);

        PendingIntent disablePendingIntent = PendingIntent.getService(
                this,
                0,
                disableIntent,
                PendingIntent.FLAG_CANCEL_CURRENT);

        Notification notification = new Notification.Builder(getApplicationContext())
                .setContentTitle(getString(R.string.app_name))
                .setContentText(getString(R.string.notification_text))
                .setSmallIcon(R.drawable.turn_on_foreground)
                .setWhen(System.currentTimeMillis())
                .setContentIntent(contentIntent)
                .addAction(0, "Show", contentIntent)
                .addAction(0, "Disable", disablePendingIntent)
                .build();

        startForeground(NOTIFICATION_ID, notification);

        return START_NOT_STICKY;
    }
}
