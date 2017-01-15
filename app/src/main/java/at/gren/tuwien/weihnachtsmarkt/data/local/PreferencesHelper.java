package at.gren.tuwien.weihnachtsmarkt.data.local;

import android.content.Context;
import android.content.SharedPreferences;
import android.location.Location;

import javax.inject.Inject;
import javax.inject.Singleton;

import at.gren.tuwien.weihnachtsmarkt.data.DataManager;
import at.gren.tuwien.weihnachtsmarkt.injection.ApplicationContext;
import at.gren.tuwien.weihnachtsmarkt.util.DeviceIdUtils;

@Singleton
public class PreferencesHelper {

    public static final String PREF_FILE_NAME = "android_boilerplate_pref_file";

    private final SharedPreferences mPref;
    private final Context mContext;

    @Inject
    public PreferencesHelper(@ApplicationContext Context context) {
        mContext = context;
        mPref = context.getSharedPreferences(PREF_FILE_NAME, Context.MODE_PRIVATE);
    }

    public void clear() {
        mPref.edit().clear().apply();
    }

    public void storeDeviceId(String deviceId){
        SharedPreferences.Editor editor = mPref.edit();
        editor.putString("deviceId", deviceId);
        editor.apply();
    }

    public boolean hasDeviceId(){
        return mPref.getString("deviceId", "null") != "null";
    }

    public String getDeviceId(){
        return mPref.getString("deviceId", "null");
    }

    public void storeLocation(Location location) {
        SharedPreferences.Editor editor = mPref.edit();
        editor.putFloat("latitude", (float) location.getLatitude());
        editor.putFloat("longitude", (float) location.getLongitude());
        editor.apply();
    }

    public boolean hasLocation() {
        return mPref.getFloat("latitude", 0) != 0;
    }

    public float getLocationLatitude() {
        return mPref.getFloat("latitude", 0);
    }

    public float getLocationLongitude() {
        return mPref.getFloat("longitude", 0);
    }
}
