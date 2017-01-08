package at.gren.tuwien.weihnachtsmarkt.data;

import java.util.List;
import java.util.Map;

import javax.inject.Inject;
import javax.inject.Singleton;

import at.gren.tuwien.weihnachtsmarkt.data.model.Weihnachtsmarkt;
import at.gren.tuwien.weihnachtsmarkt.data.model.FeatureCollection;
import at.gren.tuwien.weihnachtsmarkt.data.remote.GovernmentDataService;
import rx.Observable;
import rx.functions.Func1;
import at.gren.tuwien.weihnachtsmarkt.data.local.DatabaseHelper;
import at.gren.tuwien.weihnachtsmarkt.data.local.PreferencesHelper;

@Singleton
public class DataManager {

    private final GovernmentDataService mGovernmentDataService;
    private final DatabaseHelper mDatabaseHelper;
    private final PreferencesHelper mPreferencesHelper;

    @Inject
    public DataManager(GovernmentDataService governmentDataService, PreferencesHelper preferencesHelper,
                       DatabaseHelper databaseHelper) {
        mGovernmentDataService = governmentDataService;
        mPreferencesHelper = preferencesHelper;
        mDatabaseHelper = databaseHelper;
    }

    public PreferencesHelper getPreferencesHelper() {
        return mPreferencesHelper;
    }

    public Observable<Weihnachtsmarkt> syncMärkte() {
        return mGovernmentDataService.getWeihnachtsmärkteUndSilvesterständeWien()
                .concatMap(new Func1<FeatureCollection, Observable<Weihnachtsmarkt>>() {
                    @Override
                    public Observable<Weihnachtsmarkt> call(FeatureCollection märkte) {
                        return mDatabaseHelper.setMärkte(märkte.features());
                    }
                });
    }

    public Observable<List<Weihnachtsmarkt>> getMärkte() {
        return mDatabaseHelper.getMärkte().distinct();
    }

    public void updateRatings(Map ratings) {
        mDatabaseHelper.updateRatings(ratings);
    }
}
