package relish.permoveo.com.relish.network.response.google;

import android.text.TextUtils;

import com.google.gson.annotations.SerializedName;

/**
 * Created by rom4ek on 25.07.2015.
 */
public class GoogleResponse {
    @SerializedName("error_message")
    public String error;
    public String status;

    public boolean isSuccessful() {
        return !TextUtils.isEmpty(status) && (status.equals("OK") || status.equals("ZERO_RESULTS"));
    }
}
