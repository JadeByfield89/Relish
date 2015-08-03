package relish.permoveo.com.relish.network.response.google;

import android.text.TextUtils;

import com.google.api.client.util.Key;

/**
 * Created by rom4ek on 25.07.2015.
 */
public class GoogleResponse {
    @Key(value = "error_message")
    public String error;

    @Key
    public String status;

    public boolean isSuccessful() {
        return !TextUtils.isEmpty(status) && (status.equals("OK") || status.equals("ZERO_RESULTS"));
    }
}
