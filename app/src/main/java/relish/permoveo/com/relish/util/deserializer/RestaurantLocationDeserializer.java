package relish.permoveo.com.relish.util.deserializer;

import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;

import java.lang.reflect.Type;

import relish.permoveo.com.relish.model.yelp.YelpPlace;

/**
 * Created by rom4ek on 03.08.2015.
 */
public class RestaurantLocationDeserializer implements JsonDeserializer<YelpPlace.PlaceLocation> {
    @Override
    public YelpPlace.PlaceLocation deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        try {
            JsonObject locationObj = json.getAsJsonObject();
            YelpPlace.PlaceLocation location = new YelpPlace.PlaceLocation();

            JsonObject coordinateObj = locationObj.get("coordinate").getAsJsonObject();
            location.lat = coordinateObj.get("latitude").getAsDouble();
            location.lng = coordinateObj.get("longitude").getAsDouble();

            JsonArray addressArray = locationObj.get("display_address").getAsJsonArray();
            StringBuilder builder = new StringBuilder();
            for (int i = 0; i < addressArray.size(); i++) {
                String part = addressArray.get(i).getAsString();
                builder.append(part)
                        .append(", ");
            }
            location.address = builder.toString().substring(0, builder.toString().length() - 2);
            return location;
        }catch(Exception e){
            e.printStackTrace();
            return null;
        }
    }
}
