package at.gren.tuwien.weihnachtsmarkt.data.local;

import android.content.Context;
import android.content.SharedPreferences;
import android.location.Location;

import javax.inject.Inject;
import javax.inject.Singleton;

import at.gren.tuwien.weihnachtsmarkt.injection.ApplicationContext;

@Singleton
public class PreferencesHelper {

    public static final String PREF_FILE_NAME = "android_boilerplate_pref_file";

    private final SharedPreferences mPref;

    @Inject
    public PreferencesHelper(@ApplicationContext Context context) {
        mPref = context.getSharedPreferences(PREF_FILE_NAME, Context.MODE_PRIVATE);
    }

    public void clear() {
        mPref.edit().clear().apply();
    }

    public void storeLocation(Location location) {
        SharedPreferences.Editor editor = mPref.edit();
        editor.putLong("latitude", (long) location.getLatitude());
        editor.putLong("longitude", (long) location.getLongitude());
        editor.apply();
    }

    public boolean hasLocation() {
        return mPref.getLong("latitude", 0) != 0;
    }

    public long getLocationLatitude() {
        return mPref.getLong("latitude", 0);
    }

    public long getLocationLongitude() {
        return mPref.getLong("longitude", 0);
    }
}
