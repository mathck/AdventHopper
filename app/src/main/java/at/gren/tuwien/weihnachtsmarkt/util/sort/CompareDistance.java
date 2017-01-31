package at.gren.tuwien.weihnachtsmarkt.util.sort;

import java.util.Comparator;

import at.gren.tuwien.weihnachtsmarkt.data.model.Weihnachtsmarkt;
import at.gren.tuwien.weihnachtsmarkt.util.DistanceUtil;

public class CompareDistance implements Comparator<Weihnachtsmarkt> {


    private boolean hasLocation;
    private double userLat;
    private double userLong;

    public CompareDistance(boolean hasLocation, double userLat, double userLong) {
        this.hasLocation = hasLocation;
        this.userLat = userLat;
        this.userLong = userLong;
    }

    @Override
    public int compare(Weihnachtsmarkt markt1, Weihnachtsmarkt markt2) {

        if(hasLocation) {

            Double distance1 = (double) DistanceUtil.getDistance(
                    userLat,
                    userLong,
                    markt1.geometry().coordinates().get(0),
                    markt1.geometry().coordinates().get(1));

            Double distance2 = (double) DistanceUtil.getDistance(
                    userLat,
                    userLong,
                    markt2.geometry().coordinates().get(0),
                    markt2.geometry().coordinates().get(1));

            return distance1.compareTo(distance2);
        }

        return markt1.properties().BEZEICHNUNG().compareTo(markt2.properties().BEZEICHNUNG());
    }
}
