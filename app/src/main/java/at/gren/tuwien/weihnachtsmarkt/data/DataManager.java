package at.gren.tuwien.weihnachtsmarkt.data;

import java.util.HashMap;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Singleton;

import at.gren.tuwien.weihnachtsmarkt.data.local.DatabaseHelper;
import at.gren.tuwien.weihnachtsmarkt.data.local.PreferencesHelper;
import at.gren.tuwien.weihnachtsmarkt.data.model.FeatureCollection;
import at.gren.tuwien.weihnachtsmarkt.data.model.Weihnachtsmarkt;
import at.gren.tuwien.weihnachtsmarkt.data.remote.FirebaseService;
import at.gren.tuwien.weihnachtsmarkt.data.remote.GovernmentDataService;
import rx.Observable;
import rx.functions.Func1;

@Singleton
public class DataManager {

    private final GovernmentDataService mGovernmentDataService;
    private final FirebaseService mFirebaseService;
    private final DatabaseHelper mDatabaseHelper;
    private final PreferencesHelper mPreferencesHelper;

    @Inject
    public DataManager(GovernmentDataService governmentDataService, FirebaseService mFirebaseService, PreferencesHelper preferencesHelper,
                       DatabaseHelper databaseHelper) {
        mGovernmentDataService = governmentDataService;
        this.mFirebaseService = mFirebaseService;
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

    public void getRatings(){
        //mFirebaseService.getAverageRatings();
    }

    public void updateRatings(HashMap<String, Integer> ratings) {
        mDatabaseHelper.updateRatings(ratings);
    }
}
