package at.gren.tuwien.weihnachtsmarkt.data.remote;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import javax.inject.Inject;

public class FirebaseService {

    final FirebaseDatabase mDatabase;

    @Inject
    public FirebaseService() {
        mDatabase = FirebaseDatabase.getInstance();
    }

    public DatabaseReference getFirebaseReference(){
        return mDatabase.getReference("weihnachtsmarkt");
    }
    /*
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
    */
}
