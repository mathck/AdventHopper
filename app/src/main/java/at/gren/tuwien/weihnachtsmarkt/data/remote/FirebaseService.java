package at.gren.tuwien.weihnachtsmarkt.data.remote;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.LinkedHashMap;
import java.util.Map;

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

    public void getAverageRatings() {
        DatabaseReference dbRef = mDatabase.getReference("weihnachtsmarkt");

        dbRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Map ratings = new LinkedHashMap<>();
                for (DataSnapshot christmasMarketData : dataSnapshot.getChildren()) {
                    String christmasMarketId = christmasMarketData.getKey().replace(".", "");
                    int averageRating = calculateAverageRating(christmasMarketData);
                    ratings.put(christmasMarketId, averageRating);
                }

                // TODO create column for average rating in sqlDB
                // TODO update rating for weihnachtsmarkt in sqlDB
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
                int averageRating = calculateAverageRating(dataSnapshot);
            }

            @Override
            public void onCancelled(DatabaseError error) {
                Log.w(TAG, "Failed to read value.", error.toException());
                Toast.makeText(mContext, "Fehler beim Lesen der Bewertungen", Toast.LENGTH_LONG).show();
            }
        });
    }

    private int calculateAverageRating (DataSnapshot dataSnapshot) {
        int rating = 0;
        int ratingSum = 0;
        int numberOfRatings = 0;

        for (DataSnapshot ratingData : dataSnapshot.getChildren()) {
            try {
                rating = Integer.parseInt((String) ratingData.getValue());
            } catch (NumberFormatException e) {
                Log.d(TAG, "Non-integer data found in rating mDatabase.");
                Toast.makeText(mContext, "Fehler beim Lesen der Bewertungen", Toast.LENGTH_LONG).show();
            }

            if ((rating <= 5) && (rating > 0)) {
                ratingSum += rating;
                numberOfRatings++;
            }
        }

        return Math.round(ratingSum / numberOfRatings);
    }
}
