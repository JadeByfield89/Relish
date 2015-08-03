package relish.permoveo.com.relish.network.request.google;

import android.text.TextUtils;

import com.google.api.client.http.GenericUrl;
import com.google.api.client.http.HttpRequest;
import com.google.api.client.http.HttpRequestFactory;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.IOException;

import relish.permoveo.com.relish.interfaces.IRequestable;
import relish.permoveo.com.relish.network.API;
import relish.permoveo.com.relish.network.request.RelishRequest;
import relish.permoveo.com.relish.network.response.google.PlaceDetailsResponse;
import relish.permoveo.com.relish.util.ConstantUtil;

/**
 * Created by rom4ek on 03.08.2015.
 */
public class GoogleAuthorRequest extends RelishRequest<String, Void, String> {

    public GoogleAuthorRequest(IRequestable callback) {
        super(callback);
    }

    @Override
    protected String doInBackground(String... params) {
        HttpRequestFactory httpRequestFactory = API.createRequestFactory();
        HttpRequest request = null;
        PlaceDetailsResponse response = null;
        try {
            request = httpRequestFactory.buildGetRequest(new GenericUrl(ConstantUtil.GOOGLE_PLUS_PERSON_URL + "/" + params[0]));
            request.getUrl().put("key", ConstantUtil.GOOGLE_API_KEY);

            String json = request.execute().parseAsString();
            JsonParser parser = new JsonParser();
            JsonObject userObj = parser.parse(json).getAsJsonObject();
            if (!userObj.has("error")) {
                if (userObj.has("image")) {
                    JsonObject imageObj = userObj.get("image").getAsJsonObject();
                    return imageObj.get("url").getAsString();
                }
            }
            return null;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    protected void onPostExecute(String s) {
        super.onPostExecute(s);
        if (!TextUtils.isEmpty(s)) {
            callback.completed(s);
        } else {
            callback.failed();
        }
    }
}
