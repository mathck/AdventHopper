package at.gren.tuwien.weihnachtsmarkt.data.remote;

import android.util.Log;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;


import java.util.LinkedHashMap;
import java.util.Map;

import static android.content.ContentValues.TAG;

/**
 * Created by Andreas on 07.01.2017.
 */

public class FirebaseService {

    FirebaseDatabase database;

    public FirebaseService() {
        database = FirebaseDatabase.getInstance();
    }

    public void getAverageRating(String christmasMarketId) {
        DatabaseReference dbRef = database.getReference("weihnachtsmarkt");
        DatabaseReference ratingDbReference = dbRef.child(christmasMarketId);

        // Read from the database
        // Alternative: addValueEventListener
        ratingDbReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Integer averageRating = calculateAverageRating(dataSnapshot);
            }

            @Override
            public void onCancelled(DatabaseError error) {
                Log.w(TAG, "Failed to read value.", error.toException());
            }
        });
    }

    public void getAverageRatings() {
        DatabaseReference dbRef = database.getReference("weihnachtsmarkt");
        // Read from the database
        // Alternative: addValueEventListener
        dbRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Map ratings = new LinkedHashMap<>();
                for (DataSnapshot christmasMarketData : dataSnapshot.getChildren()) {
                    String christmasMarketId = christmasMarketData.getKey();
                    Integer averageRating = calculateAverageRating(christmasMarketData);
                    ratings.put(christmasMarketId,averageRating);
                }
            }

            @Override
            public void onCancelled(DatabaseError error) {
                Log.w(TAG, "Failed to read value.", error.toException());
            }
        });
    }

    private Integer calculateAverageRating (DataSnapshot dataSnapshot){
        Integer rating = 0;
        Integer ratingSum = 0;
        Integer numberOfRatings = 0;
        for (DataSnapshot ratingData : dataSnapshot.getChildren()) {
            try {
                rating = Integer.parseInt((String) ratingData.getValue());
            } catch (NumberFormatException e) {
                Log.d(TAG, "Non-integer data found in rating database.");
            }
            if ((rating <= 5) && (rating > 0)) {
                ratingSum += rating;
                numberOfRatings++;
            }
        }
        Integer averageRating = Math.round(ratingSum / numberOfRatings);
        return averageRating;
    }
}
