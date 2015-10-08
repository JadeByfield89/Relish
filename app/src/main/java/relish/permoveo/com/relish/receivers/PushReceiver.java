package relish.permoveo.com.relish.receivers;

import android.app.Activity;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.NotificationCompat;
import android.text.TextUtils;
import android.util.Log;
import android.util.TypedValue;

import com.parse.ParsePushBroadcastReceiver;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Locale;
import java.util.Random;

import relish.permoveo.com.relish.R;
import relish.permoveo.com.relish.activities.MainActivity;
import relish.permoveo.com.relish.model.Invite;
import relish.permoveo.com.relish.util.BitmapUtil;
import relish.permoveo.com.relish.util.ConstantUtil;

/**
 * Created by rom4ek on 07.09.2015.
 */
public class PushReceiver extends ParsePushBroadcastReceiver {

    private NotificationManager nm;
    private Context context;
    private int notificationId = 1488;
    private Notification notification;

    @Override
    protected Class<? extends Activity> getActivity(Context context, Intent intent) {
        intent.putExtra(ConstantUtil.NOTIFICATION_ID_EXTRA, notificationId);
        intent.putExtra(ConstantUtil.TO_INVITES_LIST, true);
        return MainActivity.class;
    }

    @Override
    protected void onPushReceive(Context context, Intent intent) {
        JSONObject pushData = null;
        nm = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        this.context = context;

        try {
            pushData = new JSONObject(intent.getStringExtra("com.parse.Data"));
        } catch (JSONException var7) {
            Log.e("ParsePushReceiver", "Unexpected JSONException when receiving push data: ", var7);
        }

        String action = null;
        String image = null;
        if (pushData != null) {
            action = pushData.optString("action", null);
            image = pushData.optString(ConstantUtil.SENDER_IMAGE_KEY, null);
        }

        if (action != null) {
            Bundle notification = intent.getExtras();
            Intent broadcastIntent = new Intent();
            broadcastIntent.putExtras(notification);
            broadcastIntent.setAction(action);
            broadcastIntent.setPackage(context.getPackageName());
            context.sendBroadcast(broadcastIntent);
        }


        this.notification = this.getNotification(context, intent);
        if (notification != null) {
//            notification.icon = R.mipmap.ic_notification_icon;

            if ((Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN)) {
                notification.priority = Notification.PRIORITY_MAX;
            }
        }
        if (notification != null && TextUtils.isEmpty(image)) {
            try {
                nm.notify(notificationId, notification);
            } catch (SecurityException var6) {
                notification.defaults = 5;
                nm.notify(notificationId, notification);
            }
        } else if (notification != null) {
            new LoadAvatarTask().execute(image);
        }

    }

    @Override
    protected Notification getNotification(Context context, Intent intent) {
        JSONObject pushData = null;
        Invite.InviteType type = Invite.InviteType.DEFAULT;
        try {
            pushData = new JSONObject(intent.getStringExtra("com.parse.Data"));
            if (pushData != null && (pushData.has("alert") || pushData.has("title"))) {
                String title = pushData.optString("title", context.getString(R.string.app_name));
                String alert = pushData.optString("alert", "Notification received.");
                if (pushData.has("type")) {
                    type = Invite.InviteType.parse(pushData.getString("type"));
                }
                String tickerText = String.format(Locale.getDefault(), "%s: %s", new Object[]{title, alert});
                Bundle extras = intent.getExtras();
                Random random = new Random();
                int contentIntentRequestCode = random.nextInt();
                int acceptIntentRequestCode = random.nextInt();
                int declineIntentRequestCode = random.nextInt();
                int deleteIntentRequestCode = random.nextInt();
                String packageName = context.getPackageName();

                Intent contentIntent = new Intent("com.parse.push.intent.OPEN");
                contentIntent.putExtras(extras);
                contentIntent.setPackage(packageName);

                Intent deleteIntent = new Intent("com.parse.push.intent.DELETE");
                deleteIntent.putExtras(extras);
                deleteIntent.setPackage(packageName);

                Class clazz = this.getActivity(context, intent);

                Intent acceptIntent = new Intent(context, clazz);
                acceptIntent.putExtras(extras);
                acceptIntent.setPackage(packageName);
                if (type != null && type.equals(Invite.InviteType.RECEIVED)) {
                    acceptIntent.putExtra(ConstantUtil.INVITE_ID_EXTRA, pushData.getString("id"));
                }
                acceptIntent.putExtra(ConstantUtil.NOTIFICATION_ACTION_EXTRA, true);
                acceptIntent.putExtra(ConstantUtil.NOTIFICATION_ID_EXTRA, notificationId);
                acceptIntent.putExtra(ConstantUtil.TO_INVITES_LIST, true);

                Intent declineIntent = new Intent(context, clazz);
                declineIntent.putExtras(extras);
                declineIntent.setPackage(packageName);
                if (type != null && type.equals(Invite.InviteType.RECEIVED)) {
                    declineIntent.putExtra(ConstantUtil.INVITE_ID_EXTRA, pushData.getString("id"));
                }
                declineIntent.putExtra(ConstantUtil.NOTIFICATION_ACTION_EXTRA, false);
                declineIntent.putExtra(ConstantUtil.NOTIFICATION_ID_EXTRA, notificationId);
                declineIntent.putExtra(ConstantUtil.TO_INVITES_LIST, true);

                PendingIntent pDeclineIntent = PendingIntent.getActivity(context, declineIntentRequestCode, declineIntent, PendingIntent.FLAG_UPDATE_CURRENT);
                PendingIntent pAcceptIntent = PendingIntent.getActivity(context, acceptIntentRequestCode, acceptIntent, PendingIntent.FLAG_UPDATE_CURRENT);
                PendingIntent pContentIntent = PendingIntent.getBroadcast(context, contentIntentRequestCode, contentIntent, PendingIntent.FLAG_UPDATE_CURRENT);
                PendingIntent pDeleteIntent = PendingIntent.getBroadcast(context, deleteIntentRequestCode, deleteIntent, PendingIntent.FLAG_UPDATE_CURRENT);

                NotificationCompat.Builder parseBuilder = new NotificationCompat.Builder(context);
                parseBuilder.setContentTitle(title)
                        .setContentText(alert)
                        .setTicker(tickerText)
                        .setSmallIcon(this.getSmallIconId(context, intent))
                        .setLargeIcon(this.getLargeIcon(context, intent))
                        .setContentIntent(pContentIntent)
                        .setDeleteIntent(pDeleteIntent)
                        .setAutoCancel(true)
                        .setDefaults(-1);

                switch (type) {
                    case RECEIVED:
                        parseBuilder.addAction(R.drawable.ic_action_accept, context.getString(R.string.action_accept), pAcceptIntent);
                        parseBuilder.addAction(R.drawable.ic_action_decline, context.getString(R.string.action_decline), pDeclineIntent);
                        break;
                    case RESPONSE:
                        break;
                    case UPDATE:
                        break;
                }
                if (alert != null && alert.length() > 38) {
                    parseBuilder.setStyle((new NotificationCompat.BigTextStyle()).bigText(alert));
                }

                return parseBuilder.build();
            } else {
                return null;
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }

    private class LoadAvatarTask extends AsyncTask<String, Void, Bitmap> {

        @Override
        protected Bitmap doInBackground(String... params) {
            return BitmapUtil.getBitmapFromURL(params[0]);
        }

        @Override
        protected void onPostExecute(Bitmap bitmap) {
            super.onPostExecute(bitmap);

            if (bitmap != null) {
                int height = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 64.0f, context.getResources().getDisplayMetrics());
                int width = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 64.0f, context.getResources().getDisplayMetrics());
                bitmap = Bitmap.createScaledBitmap(bitmap, width, height, false);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
                    notification.largeIcon = BitmapUtil.getCircleBitmap(bitmap);
                else
                    notification.largeIcon = bitmap;

                try {
                    nm.notify(notificationId, notification);
                } catch (SecurityException var6) {
                    notification.defaults = 5;
                    nm.notify(notificationId, notification);
                }
            } else {
                notification.largeIcon = BitmapFactory.decodeResource(context.getResources(), R.mipmap.ic_notification_icon);

                try {
                    nm.notify(notificationId, notification);
                } catch (SecurityException var6) {
                    notification.defaults = 5;
                    nm.notify(notificationId, notification);
                }
            }
        }
    }
}
