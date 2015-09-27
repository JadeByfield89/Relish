package relish.permoveo.com.relish.constants;

import java.util.HashMap;

/**
 * Created by byfieldj on 8/17/15.
 */
public class YelpCategoryFilter {

    private HashMap<String, String> categoryMap;

    public YelpCategoryFilter() {
        categoryMap = new HashMap<String, String>();
        categoryMap.put("Caribbean", "caribbean");
        categoryMap.put("Chinese", "chinese");
        categoryMap.put("Delis", "delis");
        categoryMap.put("Thai", "thai");
        categoryMap.put("Japanese", "japanese");
        categoryMap.put("Buffets", "buffets");
        categoryMap.put("Soulfood", "soulfood");
        categoryMap.put("Brasseries", "brasseries");
        categoryMap.put("Mexican", "mexican");
        categoryMap.put("Kosher", "kosher");
        categoryMap.put("Greek", "greek");
        categoryMap.put("Halal", "halal");
        categoryMap.put("Spanish", "spanish");
        categoryMap.put("Gluten Free", "gluten_free");
        categoryMap.put("Breakfast & Brunch", "breakfast_brunch");
        categoryMap.put("Fast Food", "hotdogs");
        categoryMap.put("Seafood", "seafood");
        categoryMap.put("American", "tradamerican");
        categoryMap.put("Vegan", "vegan");
        categoryMap.put("Vegetarian", "vegetarian");
        categoryMap.put("Asian", "asianfusion");
        categoryMap.put("Italian", "italian");
        categoryMap.put("Latin American", "latin");
        categoryMap.put("Cafes", "cafes");


    }

    public String lookupCategoryIdentifier(String title) {
        return categoryMap.get(title);
    }

}
