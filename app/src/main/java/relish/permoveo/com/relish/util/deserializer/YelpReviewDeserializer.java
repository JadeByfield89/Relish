package relish.permoveo.com.relish.util.deserializer;

import com.google.gson.Gson;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;

import java.lang.reflect.Type;

import relish.permoveo.com.relish.model.yelp.YelpReview;

/**
 * Created by Roman on 03.08.15.
 */
public class YelpReviewDeserializer implements JsonDeserializer<YelpReview> {
    @Override
    public YelpReview deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        Gson gson = new Gson();
        YelpReview review = gson.fromJson(json, YelpReview.class);
        JsonObject userObj = json.getAsJsonObject()
                .get("user").getAsJsonObject();

        review.setAuthorName(userObj.get("name").getAsString());
        if (userObj.has("image_url"))
            review.setAuthorImage(userObj.get("image_url").getAsString());
        return review;
    }
}
