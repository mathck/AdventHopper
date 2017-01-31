package at.gren.tuwien.weihnachtsmarkt.util.sort;

import java.util.Comparator;

import javax.inject.Inject;

import at.gren.tuwien.weihnachtsmarkt.data.DataManager;
import at.gren.tuwien.weihnachtsmarkt.data.model.Weihnachtsmarkt;
import at.gren.tuwien.weihnachtsmarkt.util.DistanceUtil;

public class CompareDistance implements Comparator<Weihnachtsmarkt> {

    private final DataManager mDataManager;

    @Inject
    public CompareDistance(DataManager dataManager) {
        mDataManager = dataManager;
    }

    @Override
    public int compare(Weihnachtsmarkt markt1, Weihnachtsmarkt markt2) {

        if(mDataManager.getPreferencesHelper().hasLocation()) {

            double userLat = mDataManager.getPreferencesHelper().getLocationLatitude();
            double userLong = mDataManager.getPreferencesHelper().getLocationLongitude();

            Double distance1 = (double) DistanceUtil.getDistance(
                    userLat,
                    userLong,
                    markt1.geometry().coordinates().get(0),
                    markt1.geometry().coordinates().get(1));

            Double distance2 = (double) DistanceUtil.getDistance(
                    userLat,
                    userLong,
                    markt1.geometry().coordinates().get(0),
                    markt1.geometry().coordinates().get(1));

            return distance1.compareTo(distance2);
        }

        return markt1.properties().BEZEICHNUNG().compareTo(markt2.properties().BEZEICHNUNG());
    }
}
