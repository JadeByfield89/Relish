package relish.permoveo.com.relish.util;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.support.v4.app.ShareCompat;

/**
 * Created by rom4ek on 07.09.2015.
 */
public class SharingUtil {

    public static Intent getTwitterIntent(Context ctx, String shareText) {
        Intent shareIntent;
        if (doesPackageExist(ctx, "com.twitter.android")) {
            shareIntent = new Intent(Intent.ACTION_SEND);
            shareIntent.setClassName("com.twitter.android",
                    "com.twitter.android.composer.ComposerActivity");
            shareIntent.setType("text/*");
            shareIntent.putExtra(android.content.Intent.EXTRA_TEXT, shareText);
            return shareIntent;
        } else {
            String tweetUrl = "https://twitter.com/intent/tweet?text=" + shareText;
            Uri uri = Uri.parse(tweetUrl);
            shareIntent = new Intent(Intent.ACTION_VIEW, uri);
            return shareIntent;
        }
    }

    public static Intent getFacebookIntent(Context ctx, String shareText) {
        Intent shareIntent;
        if (doesPackageExist(ctx, "com.facebook.katana")) {
            shareIntent = new Intent(Intent.ACTION_SEND);
            shareIntent.setClassName("com.facebook.katana",
                    "com.facebook.composer.shareintent.ImplicitShareIntentHandlerDefaultAlias");
            shareIntent.setType("text/*");
            shareIntent.putExtra(android.content.Intent.EXTRA_TEXT, shareText);
            return shareIntent;
        } else {
            String facebookUrl = "http://www.facebook.com/sharer.php?u=" + shareText + "&description=" + shareText;
            Uri uri = Uri.parse(facebookUrl);
            shareIntent = new Intent(Intent.ACTION_VIEW, uri);
            return shareIntent;
        }
    }

    public static Intent getPlusIntent(Activity ctx, String shareText) {
        Intent shareIntent;
        if (doesPackageExist(ctx, "com.google.android.apps.plus")) {
            shareIntent = ShareCompat.IntentBuilder.from(ctx)
                    .setType("text/plain")
                    .setText(shareText)
                    .getIntent()
                    .setPackage("com.google.android.apps.plus");
        } else {
            String plusUrl = "https://plus.google.com/share?url=" + shareText;
            Uri uri = Uri.parse(plusUrl);
            shareIntent = new Intent(Intent.ACTION_VIEW, uri);
            return shareIntent;
        }
        return shareIntent;
    }

    private static boolean doesPackageExist(Context context, String packageName) {
        PackageManager pm = context.getPackageManager();
        try {
            pm.getPackageInfo(packageName, PackageManager.GET_ACTIVITIES);
            return true;
        } catch (PackageManager.NameNotFoundException e) {
            return false;
        }
    }
}
