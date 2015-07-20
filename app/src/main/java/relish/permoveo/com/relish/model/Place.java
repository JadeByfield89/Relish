package relish.permoveo.com.relish.model;

import java.io.Serializable;

/**
 * Created by Roman on 20.07.15.
 */
public class Place implements Serializable {
    public String image;
    public String name;
    public double distance;
    public int rating;
    public PriceRanking priceRanking;

    public Place(String image, String name, double distance, int rating, PriceRanking priceRanking) {
        this.image = image;
        this.name = name;
        this.distance = distance;
        this.rating = rating;
        this.priceRanking = priceRanking;
    }

    public enum PriceRanking {LOW, MEDIUM, HIGH}

}
