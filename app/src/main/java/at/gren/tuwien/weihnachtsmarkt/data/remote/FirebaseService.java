package at.gren.tuwien.weihnachtsmarkt.data.remote;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import javax.inject.Inject;

import at.gren.tuwien.weihnachtsmarkt.injection.ApplicationContext;

import static android.content.ContentValues.TAG;

public class FirebaseService {

    private FirebaseDatabase mDatabase;
    private Context mContext;

    @Inject
    public FirebaseService(@ApplicationContext Context context) {
        mDatabase = FirebaseDatabase.getInstance();
        mContext = context;
    }

    public DatabaseReference getFirebaseReference(){
        DatabaseReference dbRef = mDatabase.getReference("weihnachtsmarkt");
        return dbRef;
    }

    public void getAverageRatings() {
        DatabaseReference dbRef = getFirebaseReference();
        dbRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                HashMap<String, Double> ratings = new HashMap();
                for (DataSnapshot christmasMarketData : dataSnapshot.getChildren()) {
                    String christmasMarketId = christmasMarketData.getKey().replace(".", "");
                    Double averageRating = calculateAverageRating(christmasMarketData);
                    ratings.put(christmasMarketId, averageRating);
                }

                // TODO this should be called in the data manager directly. How to pass the result?
                // Vorschlag:
                // FirebaseService die dbReferenz exposen lassen
                // und den Listener im SyncService attachen
                // und dann so http://stackoverflow.com/a/15544647/2880465
            }

            @Override
            public void onCancelled(DatabaseError error) {
                Log.w(TAG, "Failed to read value.", error.toException());
                Toast.makeText(mContext, "Fehler beim Lesen der Bewertungen", Toast.LENGTH_LONG).show();
            }
        });
    }

    public void getAverageRating(String christmasMarketId) {
        DatabaseReference dbRef = mDatabase.getReference("weihnachtsmarkt");
        DatabaseReference ratingDbReference = dbRef.child(christmasMarketId.replace(".", ""));

        ratingDbReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Double averageRating = calculateAverageRating(dataSnapshot);
            }

            @Override
            public void onCancelled(DatabaseError error) {
                Log.w(TAG, "Failed to read value.", error.toException());
                Toast.makeText(mContext, "Fehler beim Lesen der Bewertungen", Toast.LENGTH_LONG).show();
            }
        });
    }

    private Double calculateAverageRating (DataSnapshot dataSnapshot) {
        int rating = 0;
        int ratingSum = 0;
        int numberOfRatings = 0;

        for (DataSnapshot ratingData : dataSnapshot.getChildren()) {
            try {
                rating = Integer.parseInt((String) ratingData.getValue());
            } catch (NumberFormatException e) {
                Log.d(TAG, "Non-integer data found in rating database.");
                Toast.makeText(mContext, "Fehler beim Lesen der Bewertungen", Toast.LENGTH_LONG).show();
            }

            if ((rating <= 5) && (rating > 0)) {
                ratingSum += rating;
                numberOfRatings++;
            }
        }

        Double averageRating = Double.valueOf(ratingSum / numberOfRatings);
        return Math.round( averageRating * 2 ) / 2.0 ; // round to next half
    }
}
