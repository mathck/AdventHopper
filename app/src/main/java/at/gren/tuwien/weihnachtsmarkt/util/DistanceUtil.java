package at.gren.tuwien.weihnachtsmarkt.util;

import at.gren.tuwien.weihnachtsmarkt.data.model.Weihnachtsmarkt;

public final class DistanceUtil {

    private static String getDistance (double lat_a, double lng_a, double lat_b, double lng_b)
    {
        double earthRadius = 6371;
        double latDiff = Math.toRadians(lat_b - lat_a);
        double lngDiff = Math.toRadians(lng_b - lng_a);

        double a = Math.sin(latDiff / 2) * Math.sin(latDiff / 2) +
                Math.cos(Math.toRadians(lat_a)) * Math.cos(Math.toRadians(lat_b)) *
                Math.sin(lngDiff / 2) * Math.sin(lngDiff / 2);

        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        double distance = earthRadius * c;

        int meterConversion = (int) Math.round(distance * 1000);

        return Integer.toString(meterConversion);
    }

    public static String getDistanceToMarket(double userLat, double userLong, Weihnachtsmarkt markt) {

        double marktLocationLatitude = markt.geometry().coordinates().get(0);
        double marktLocationLongitude = markt.geometry().coordinates().get(1);

        String distance = DistanceUtil.getDistance( marktLocationLatitude,
                                                    marktLocationLongitude,
                                                    userLat,
                                                    userLong);

        return distance + " m";
    }
}
