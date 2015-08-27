package relish.permoveo.com.relish.network.request.google;

import com.google.api.client.http.GenericUrl;
import com.google.api.client.http.HttpRequest;
import com.google.api.client.http.HttpRequestFactory;

import java.io.IOException;

import relish.permoveo.com.relish.interfaces.IRequestable;
import relish.permoveo.com.relish.network.API;
import relish.permoveo.com.relish.network.request.RelishRequest;
import relish.permoveo.com.relish.network.response.google.PlaceDetailsResponse;
import relish.permoveo.com.relish.util.ConstantUtil;

/**
 * Created by rom4ek on 03.08.2015.
 */
public class PlaceDetailsRequest extends RelishRequest<String, Void, PlaceDetailsResponse> {

    public PlaceDetailsRequest(IRequestable callback) {
        super(callback);
    }

    @Override
    protected PlaceDetailsResponse doInBackground(String... params) {
        HttpRequestFactory httpRequestFactory = API.createRequestFactory();
        HttpRequest request = null;
        PlaceDetailsResponse response = null;
        try {
            request = httpRequestFactory.buildGetRequest(new GenericUrl(ConstantUtil.PLACE_DETAILS_URL));
            request.getUrl().put("key", ConstantUtil.GOOGLE_API_KEY);
            request.getUrl().put("reference", params[0]);

            String json = request.execute().parseAsString();
//            JsonParser parser = new JsonParser();
//            if (!parser.parse(json).getAsJsonObject().has("error_message")) {
//                JsonObject place = parser.parse(json).getAsJsonObject().get("result").getAsJsonObject();
//                json = gson.toJson(place);
//            }
            response = gson.fromJson(json, PlaceDetailsResponse.class);
            return response;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    protected void onPostExecute(PlaceDetailsResponse response) {
        super.onPostExecute(response);
        if (callback != null) {
            if (response == null) {
                callback.failed();
            } else if (!response.isSuccessful()) {
                callback.failed(response.error);
            } else {
                try {
                    if(response.place.openingHours != null && response.place.openingHours.weekdayText != null) {
                        callback.completed(response.place.reviews, response.place.openingHours.weekdayText);
                    } else {
                        callback.completed(response.place.reviews);
                    }
                }catch (NullPointerException e){
                    e.printStackTrace();
                }
            }
        }
    }
}
