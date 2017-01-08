package at.gren.tuwien.weihnachtsmarkt;

import android.content.Context;
import android.support.multidex.MultiDexApplication;

import at.gren.tuwien.weihnachtsmarkt.injection.component.ApplicationComponent;
import at.gren.tuwien.weihnachtsmarkt.injection.component.DaggerApplicationComponent;
import at.gren.tuwien.weihnachtsmarkt.injection.module.ApplicationModule;
import timber.log.Timber;

public class BoilerplateApplication extends MultiDexApplication {

    ApplicationComponent mApplicationComponent;

    @Override
    public void onCreate() {
        super.onCreate();

        if (BuildConfig.DEBUG) {
            Timber.plant(new Timber.DebugTree());
            //Fabric.with(this, new Crashlytics());
        }
    }

    public static BoilerplateApplication get(Context context) {
        return (BoilerplateApplication) context.getApplicationContext();
    }

    public ApplicationComponent getComponent() {
        if (mApplicationComponent == null) {
            mApplicationComponent = DaggerApplicationComponent.builder()
                    .applicationModule(new ApplicationModule(this))
                    .build();
        }
        return mApplicationComponent;
    }

    // Needed to replace the component with a test specific one
    public void setComponent(ApplicationComponent applicationComponent) {
        mApplicationComponent = applicationComponent;
    }
}
