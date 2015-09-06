package relish.permoveo.com.relish.receivers;

import android.app.Notification;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.util.TypedValue;

import com.parse.ParsePushBroadcastReceiver;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import relish.permoveo.com.relish.R;
import relish.permoveo.com.relish.util.ConstantUtil;

/**
 * Created by rom4ek on 07.09.2015.
 */
public class PushReceiver extends ParsePushBroadcastReceiver {

    private NotificationManager nm;
    private Context context;
    private Notification notification;

    private class LoadAvatarTask extends AsyncTask<String, Void, Bitmap> {

        @Override
        protected Bitmap doInBackground(String... params) {
            Bitmap bitmap = null;
            try {
                URL url = new URL(params[0]);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setDoInput(true);
                connection.connect();
                InputStream input = connection.getInputStream();
                Bitmap myBitmap = BitmapFactory.decodeStream(input);
                return myBitmap;
            } catch (IOException e) {
                e.printStackTrace();
            }
            return bitmap;
        }

        @Override
        protected void onPostExecute(Bitmap bitmap) {
            super.onPostExecute(bitmap);
            int notificationId = (int) System.currentTimeMillis();

            if (bitmap != null) {
                int height = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 75.0f, context.getResources().getDisplayMetrics());
                int width = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 75.0f, context.getResources().getDisplayMetrics());
                bitmap = Bitmap.createScaledBitmap(bitmap, width, height, false);
                notification.largeIcon = bitmap;

                try {
                    nm.notify(notificationId, notification);
                } catch (SecurityException var6) {
                    notification.defaults = 5;
                    nm.notify(notificationId, notification);
                }
            } else {
                notification.largeIcon = BitmapFactory.decodeResource(context.getResources(), R.mipmap.ic_launcher);

                try {
                    nm.notify(notificationId, notification);
                } catch (SecurityException var6) {
                    notification.defaults = 5;
                    nm.notify(notificationId, notification);
                }
            }
        }
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
        if (notification != null && TextUtils.isEmpty(image)) {
            int notificationId = (int) System.currentTimeMillis();

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
}
