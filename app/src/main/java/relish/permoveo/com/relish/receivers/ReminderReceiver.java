package relish.permoveo.com.relish.receivers;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.support.v7.app.NotificationCompat;
import android.text.Spannable;
import android.text.SpannableString;

import java.util.Locale;
import java.util.Random;

import relish.permoveo.com.relish.R;
import relish.permoveo.com.relish.activities.MainActivity;
import relish.permoveo.com.relish.model.Invite;
import relish.permoveo.com.relish.util.ConstantUtil;

public class ReminderReceiver extends BroadcastReceiver {
    private NotificationManager nm;
    private int notificationId = 1377;

    public ReminderReceiver() {
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        nm = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        Notification notification = getNotification(context, intent);
        if (notification != null) {
            if ((Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN)) {
                notification.priority = Notification.PRIORITY_MAX;
            }

            try {
                nm.notify(notificationId, notification);
            } catch (SecurityException var6) {
                notification.defaults = 5;
                nm.notify(notificationId, notification);
            }
        }
    }

    private Notification getNotification(Context context, Intent intent) {
        Invite invite = (Invite) intent.getSerializableExtra(ConstantUtil.INVITE_EXTRA);
        String title = invite.title;
        String alert = "@" + invite.name + ", " + invite.getFormattedDate() + ", " + invite.getFormattedTime() + "\n";
        String fromNow = "";

        switch (invite.reminder) {
            case 900:
                fromNow = "15 minutes from now";
                break;
            case 1800:
                fromNow = "30 minutes from now";
                break;
            case 3600:
                fromNow = "1 hour from now";
                break;
            case 7200:
                fromNow = "2 hours from now";
                break;
        }

        SpannableString message = new SpannableString(alert + fromNow);
        message.setSpan(new android.text.style.StyleSpan(android.graphics.Typeface.BOLD), alert.length(), message.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

        String tickerText = String.format(Locale.getDefault(), "%s: %s", new Object[]{title, alert});
        Random random = new Random();
        int contentIntentRequestCode = random.nextInt();
        String packageName = context.getPackageName();

        Intent contentIntent = new Intent(context, MainActivity.class);
        contentIntent.setPackage(packageName);
        contentIntent.putExtra(ConstantUtil.NOTIFICATION_ID_EXTRA, notificationId);
        contentIntent.putExtra(ConstantUtil.TO_INVITES_LIST, true);

        PendingIntent pContentIntent = PendingIntent.getActivity(context, contentIntentRequestCode, contentIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        NotificationCompat.Builder parseBuilder = new NotificationCompat.Builder(context);
        parseBuilder.setContentTitle(title)
                .setContentText(message)
                .setTicker(tickerText)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setLargeIcon(null)
                .setContentIntent(pContentIntent)
                .setAutoCancel(true)
                .setDefaults(-1);

        if (alert.length() > 38) {
            parseBuilder.setStyle((new NotificationCompat.BigTextStyle()).bigText(message));
        }

        return parseBuilder.build();
    }
}
