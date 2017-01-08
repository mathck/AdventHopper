package at.gren.tuwien.weihnachtsmarkt.injection.module;

import android.app.Application;
import android.content.Context;

import javax.inject.Singleton;

import at.gren.tuwien.weihnachtsmarkt.data.remote.GovernmentDataService;
import dagger.Module;
import dagger.Provides;
import at.gren.tuwien.weihnachtsmarkt.injection.ApplicationContext;

/**
 * Provide application-level dependencies.
 */
@Module
public class ApplicationModule {
    protected final Application mApplication;

    public ApplicationModule(Application application) {
        mApplication = application;
    }

    @Provides
    Application provideApplication() {
        return mApplication;
    }

    @Provides
    @ApplicationContext
    Context provideContext() {
        return mApplication;
    }

    @Provides
    @Singleton
    GovernmentDataService provideGovernmentDataService() {
        return GovernmentDataService.Creator.newGovernmentDataService();
    }
}
