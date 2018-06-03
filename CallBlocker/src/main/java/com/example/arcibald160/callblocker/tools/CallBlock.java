package com.example.arcibald160.callblocker.tools;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Binder;
import android.os.Build;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.telephony.TelephonyManager;
import android.util.Log;

import com.example.arcibald160.callblocker.BlockAllNotification;
import com.example.arcibald160.callblocker.MainActivity;
import com.example.arcibald160.callblocker.R;
import com.example.arcibald160.callblocker.data.BlockListContract;

import java.lang.reflect.Method;
import java.util.Date;

public class CallBlock extends BroadcastReceiver {
    // This String will hold the incoming phone number
    private String number;
    private static final String TAG = "CallBlockReceiver";

    @Override
    public void onReceive(Context context, Intent intent) {

        // If, the received action is not a type of "Phone_State", ignore it
        if (!intent.getAction().equals("android.intent.action.PHONE_STATE")) {
            return;
        }

        if (intent.getExtras().getString(TelephonyManager.EXTRA_STATE).equals(TelephonyManager.EXTRA_STATE_RINGING)){
            // Fetch the number of incoming call
            number = intent.getStringExtra(TelephonyManager.EXTRA_INCOMING_NUMBER);

            // TODO: remove hardcoded values and make better solution
            String countryCode = "+385";
            if (number.contains(countryCode)) {
                number = number.replace(countryCode, "0");
            }

//            // debug purposes e-is ususally error but this is hack for broadcaste receiver
//            Log.e(TAG, number);

            BlockChecker blockChecker = new BlockChecker(context, number);

            if(blockChecker.canBlock()) {
                // If yes, invoke the method
                try {
                    declinePhone();
                    addToBlockedCallsList(number, context);
                    raiseNotification(context);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            return;
        }
    }

    private void raiseNotification(Context context) {

        Intent mainAppIntent = new Intent(context, MainActivity.class);
        mainAppIntent.putExtra(context.getString(R.string.default_fragment), 1);
        // Prepare a notification with vibration, sound and lights
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context)
                .setSmallIcon(android.R.drawable.stat_notify_missed_call)
                .setContentTitle(context.getString(R.string.notification_title))
                .setContentText(number)
                .setVibrate(new long[]{0, 400, 250, 400})
                .setAutoCancel(true)
                .setContentIntent(PendingIntent.getActivity(
                        context,
                        0,
                        mainAppIntent,
                        PendingIntent.FLAG_UPDATE_CURRENT)
                );

        // You must set the Notification Channel ID
        // if your app is targeting API Level 26 and up (Android O)
        // More info: http://bit.ly/2Bzgwl7
        builder.setChannelId(context.getString(R.string.notification_channel));

        // Get an instance of the NotificationManager service
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(context.NOTIFICATION_SERVICE);

        // Build the notification and display it
        notificationManager.notify(1, builder.build());
    }

    // list of recently blocked calls gets filled here
    private void addToBlockedCallsList(String number, Context context) {
        Date current = new Date();

        java.sql.Date sqlDate = new java.sql.Date(current.getTime());
        java.sql.Time sqlTime = new java.sql.Time(current.getTime());

        // insert new values to blocked contacts list (used in recently)
        ContentValues mBlockedList = new ContentValues();
        mBlockedList.put(BlockListContract.BlockedCallsReceived.COLUMN_NUMBER, number);
        mBlockedList.put(BlockListContract.BlockedCallsReceived.COLUMN_DATE, String.valueOf(sqlDate));
        mBlockedList.put(BlockListContract.BlockedCallsReceived.COLUMN_TIME, String.valueOf(sqlTime));

        context.getContentResolver().insert(
                BlockListContract.BlockedCallsReceived.CONTENT_URI, // the user dictionary content URI
                mBlockedList                                        // the values to insert
        );
    }

    private void declinePhone() throws Exception {
        try {
            String serviceManagerName = "android.os.ServiceManager";
            String serviceManagerNativeName = "android.os.ServiceManagerNative";
            String telephonyName = "com.android.internal.telephony.ITelephony";

            Class<?> telephonyClass;
            Class<?> telephonyStubClass;
            Class<?> serviceManagerClass;
            Class<?> serviceManagerNativeClass;

            Method telephonyEndCall;
            Object telephonyObject;
            Object serviceManagerObject;
            telephonyClass = Class.forName(telephonyName);
            telephonyStubClass = telephonyClass.getClasses()[0];
            serviceManagerClass = Class.forName(serviceManagerName);
            serviceManagerNativeClass = Class.forName(serviceManagerNativeName);

            Method getService = // getDefaults[29];
                    serviceManagerClass.getMethod("getService", String.class);
            Method tempInterfaceMethod = serviceManagerNativeClass.getMethod("asInterface", IBinder.class);

            Binder tmpBinder = new Binder();
            tmpBinder.attachInterface(null, "fake");
            serviceManagerObject = tempInterfaceMethod.invoke(null, tmpBinder);
            IBinder retbinder = (IBinder) getService.invoke(serviceManagerObject, "phone");
            Method serviceMethod = telephonyStubClass.getMethod("asInterface", IBinder.class);
            telephonyObject = serviceMethod.invoke(null, retbinder);
            telephonyEndCall = telephonyClass.getMethod("endCall");
            telephonyEndCall.invoke(telephonyObject);
        } catch (Exception e) {
            e.printStackTrace();
            Log.d("unable", "msg cant dissconect call....");
        }
    }
}
