package at.gren.tuwien.weihnachtsmarkt.util;

import android.content.Context;
import android.provider.Settings;

import java.util.UUID;


import at.gren.tuwien.weihnachtsmarkt.data.DataManager;


public final class DeviceIdUtils {

    public static String getDeviceID (Context context, DataManager mDataManager) {

        //if DeviceID has already been saved to the preferences get it from there
        if (mDataManager.getPreferencesHelper().hasDeviceId())
        {
            return mDataManager.getPreferencesHelper().getDeviceId();
        }

        /*// try 1
        final String try1 = ((TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE)).getDeviceId();
        if(try1 != null && !try1.isEmpty()) {
            Timber.d("DeviceId: Try 1");
            dataManager.getPreferencesHelper().storeDeviceId(try1);
            return try1;
        }*/

        // try 2
        final String try2 = android.os.Build.SERIAL;
        if( try2 != null && !try2.isEmpty() && !try2.equals("unknown"))   {
            mDataManager.getPreferencesHelper().storeDeviceId(try2);
            return try2;
        }

        //try 3
        final String try3 = Settings.Secure.getString(context.getContentResolver(),
                Settings.Secure.ANDROID_ID);
        if( try3 != null && !try3.isEmpty() ) {
            mDataManager.getPreferencesHelper().storeDeviceId(try3);
            return try3;
        }

        // try 4
        final String try4 = UUID.randomUUID().toString();
        mDataManager.getPreferencesHelper().storeDeviceId(try4);
        return try4;
    }

}