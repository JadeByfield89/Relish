package relish.permoveo.com.relish.util;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.provider.Settings;
import android.view.ContextThemeWrapper;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ListAdapter;

import relish.permoveo.com.relish.R;

/**
 * Created by rom4ek on 25.07.2015.
 */
public class DialogUtil {

    public static void showComplainDialog(final Activity activity, String title, int items, DialogInterface.OnClickListener onItemClickListener) {
        ContextThemeWrapper themedContext;
        themedContext = new ContextThemeWrapper(activity, R.style.RelishDialog);
        AlertDialog.Builder d = new AlertDialog.Builder(themedContext);
        d.setTitle(title);
        d.setItems(items, onItemClickListener);
        d.show();
    }

    public static void showListViewDialog(final Activity activity, String title, ListAdapter adapter, DialogInterface.OnClickListener onItemClickListener, final DialogInterface.OnClickListener onNoClickListener) {
        showListViewDialog(activity, title, adapter, -1, onItemClickListener, onNoClickListener);
    }

    public static void showListViewDialog(final Activity activity, String title, ListAdapter adapter, int selected, DialogInterface.OnClickListener onItemClickListener, final DialogInterface.OnClickListener onNoClickListener) {
        ContextThemeWrapper themedContext;
        themedContext = new ContextThemeWrapper(activity, R.style.RelishDialog);
        AlertDialog.Builder d = new AlertDialog.Builder(themedContext);
        d.setCancelable(false);
        d.setTitle(title);
        d.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                if (onNoClickListener != null)
                    onNoClickListener.onClick(dialog, which);
            }
        });
        if (selected == -1)
            d.setAdapter(adapter, onItemClickListener);
        else
            d.setSingleChoiceItems(adapter, selected, onItemClickListener);
        d.show();
    }

    public static Dialog showConfirmDialog(final Activity activity, String title, String message, String confirm, String reject, DialogInterface.OnClickListener onYesClickListener, final DialogInterface.OnClickListener onNoClickListener) {
        ContextThemeWrapper themedContext;
        themedContext = new ContextThemeWrapper(activity, R.style.RelishDialog);
        AlertDialog.Builder d = new AlertDialog.Builder(themedContext);
        d.setCancelable(false);
        d.setTitle(title);
        d.setMessage(message);
        d.setPositiveButton(confirm, onYesClickListener);
        d.setNegativeButton(reject, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                if (onNoClickListener != null)
                    onNoClickListener.onClick(dialog, which);
            }
        });
        return d.show();
    }

    public static void showConfirmDialog(final Activity activity, String title, String message, DialogInterface.OnClickListener onClickListener) {
        showConfirmDialog(activity, title, message, onClickListener, null);
    }

    public static void showConfirmDialog(final Activity activity, String title, String message, DialogInterface.OnClickListener onYesClickListener, final DialogInterface.OnClickListener onNoClickListener) {
        showConfirmDialog(activity, title, message, "Yes", "No", onYesClickListener, onNoClickListener);
    }

    public static Dialog showErrorDialog(final Activity activity, String message) {
        return showErrorDialog(activity, message, null);
    }

    public static Dialog showErrorDialog(final Activity activity, String message, DialogInterface.OnClickListener onClickListener) {
        return showErrorDialog(activity, "Warning!", message, onClickListener);
    }

    public static Dialog showErrorDialog(final Activity activity, String title, String message, String button, DialogInterface.OnClickListener onClickListener) {
        ContextThemeWrapper themedContext;
        themedContext = new ContextThemeWrapper(activity, R.style.RelishDialog);
        AlertDialog.Builder d = new AlertDialog.Builder(themedContext);
        d.setCancelable(false);
        d.setTitle(title);
        d.setMessage(message);
        if (onClickListener == null) {
            d.setNeutralButton(button, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            });
        } else {
            d.setNeutralButton(button, onClickListener);
        }
        return d.show();
    }

    public static Dialog showErrorDialog(final Activity activity, String title, String message, DialogInterface.OnClickListener onClickListener) {
        return showErrorDialog(activity, title, message, "Ok", onClickListener);
    }

    public static Dialog showSettingsDialog(final Activity activity, String message) {
        ContextThemeWrapper themedContext;
        themedContext = new ContextThemeWrapper(activity, R.style.RelishDialog);
        AlertDialog.Builder d = new AlertDialog.Builder(themedContext);

        d.setTitle("Warning!")
                .setCancelable(true)
                .setMessage(message)
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Intent viewIntent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                        activity.startActivity(viewIntent);
                    }
                })
                .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
        return d.show();
    }

    public static void showInputDialog(final Activity activity, String title, String message, View label, DialogInterface.OnClickListener listener) {
        ContextThemeWrapper themedContext;
        themedContext = new ContextThemeWrapper(activity, R.style.RelishDialog);
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(themedContext);

        // Setting Dialog Title
        alertDialog.setTitle(title);

        // Setting Dialog Message
        alertDialog.setMessage(message);
        alertDialog.setCancelable(true);
        alertDialog.setView(label);
        label.requestFocus();
        InputMethodManager imm = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.showSoftInput(label, InputMethodManager.SHOW_IMPLICIT);

        alertDialog.setPositiveButton("Ok", listener);
        // Setting Negative "NO" Button

        alertDialog.setNegativeButton("Cancel",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                }
        );
        alertDialog.show();
    }

}