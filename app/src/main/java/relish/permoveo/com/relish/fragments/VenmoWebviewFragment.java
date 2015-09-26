package relish.permoveo.com.relish.fragments;

import android.app.Activity;
import android.app.Dialog;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import butterknife.Bind;
import butterknife.ButterKnife;
import relish.permoveo.com.relish.R;
import relish.permoveo.com.relish.util.SharedPrefsUtil;

/**
 * Created by byfieldj on 9/25/15.
 */
public class VenmoWebviewFragment extends DialogFragment {

    @Bind(R.id.venmo_wv)
    WebView webview;

    private String accessToken = "";

    private onVenmoAccessTokenRetrievedListener listener;


    private static final String VENMO_AUTH_URL = "https://api.venmo.com/v1/oauth/authorize?client_id=2954&scope=make_payments%20access_profile%20access_email%20access_phone%20access_balance&response_type=token";

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {


        View v = inflater.inflate(R.layout.fragment_venmo_webview, container, false);
        ButterKnife.bind(this, v);


        webview.setWebViewClient(new VenmoWebViewClient());
        webview.loadUrl(VENMO_AUTH_URL);
        return v;
    }

    private class VenmoWebViewClient extends WebViewClient {

        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {

            String callback_url = getString(R.string.venmo_callback_url);

            if (url.contains(callback_url)) {
                Uri uri = Uri.parse(url);

                accessToken = uri.getQueryParameter("access_token");
                listener.onVenmoAccessTokenRetrieved(accessToken);
                dismiss();
            }
            return super.shouldOverrideUrlLoading(view, url);
        }
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            listener = (onVenmoAccessTokenRetrievedListener) activity;
        } catch (ClassCastException e) {
            e.printStackTrace();
            Log.e("VenmoWebviewFragment", "Parent activity must implement onVenmoAccessTokenRetrievedListener!");
        }
    }

    public interface onVenmoAccessTokenRetrievedListener {
         void onVenmoAccessTokenRetrieved(String token);
    }


    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        return super.onCreateDialog(savedInstanceState);
    }
}
