package at.gren.tuwien.weihnachtsmarkt.util;

public final class DistanceUtil {

    public static String getDistance (double lat_a, double lng_a, double lat_b, double lng_b)
    {
        double earthRadius = 6371;
        double latDiff = Math.toRadians(lat_b - lat_a);
        double lngDiff = Math.toRadians(lng_b - lng_a);

        double a = Math.sin(latDiff / 2) * Math.sin(latDiff / 2) +
                Math.cos(Math.toRadians(lat_a)) * Math.cos(Math.toRadians(lat_b)) *
                Math.sin(lngDiff / 2) * Math.sin(lngDiff / 2);

        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        double distance = earthRadius * c;

        //long meterConversion = Math.round(distance * 1000);

        return Double.toString(distance);
    }
}
