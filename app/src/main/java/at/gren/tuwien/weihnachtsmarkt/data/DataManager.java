package at.gren.tuwien.weihnachtsmarkt.data;

import android.util.Log;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

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
import timber.log.Timber;

import static android.content.ContentValues.TAG;

@Singleton
public class DataManager {

    private final GovernmentDataService mGovernmentDataService;
    private final FirebaseService mFirebaseService;
    private final DatabaseHelper mDatabaseHelper;
    private final PreferencesHelper mPreferencesHelper;

    @Inject
    public DataManager(GovernmentDataService governmentDataService, FirebaseService FirebaseService, PreferencesHelper preferencesHelper,
                       DatabaseHelper databaseHelper) {
        mGovernmentDataService = governmentDataService;
        mFirebaseService = FirebaseService;
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

    public void syncRatings(){
        DatabaseReference dbRef = mFirebaseService.getFirebaseReference();
        dbRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                HashMap<String, Double> ratings = new HashMap();
                for (DataSnapshot christmasMarketData : dataSnapshot.getChildren()) {
                    String christmasMarketId = christmasMarketData.getKey().replace(".", "");
                    Double averageRating = calculateAverageRating(christmasMarketData);
                    ratings.put(christmasMarketId, averageRating);
                }
                DatabaseHelper.updateRatings(ratings);
            }

            @Override
            public void onCancelled(DatabaseError error) {
                Log.w(TAG, "Failed to read value.", error.toException());
            }
        });
    }

    private Double calculateAverageRating (DataSnapshot dataSnapshot) {
        Double rating = 0.0;
        Double ratingSum = 0.0;
        Double numberOfRatings = 0.0;

        for (DataSnapshot ratingData : dataSnapshot.getChildren()) {
            rating = ((Long)(ratingData.getValue())).doubleValue();

            if ((rating <= 5) && (rating > 0)) {
                ratingSum += rating;
                numberOfRatings++;
            }
        }
        Double averageRating = ratingSum / numberOfRatings;
        return (Math.round( averageRating * 2.0 )) / 2.0 ; // round to next half
    }

    public void setRating (String weihnachtsmarktId, Integer rating){
        DatabaseReference dbRef = mFirebaseService.getFirebaseReference();
        if ((rating <= 5) && (rating > 0)) {
            dbRef.child(weihnachtsmarktId).child("deviceIdX").setValue(rating); // TODO deviceId
        }
    }
}
