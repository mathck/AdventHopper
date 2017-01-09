package at.gren.tuwien.weihnachtsmarkt.injection.component;

import android.app.Application;
import android.content.Context;

import javax.inject.Singleton;

import dagger.Component;
import at.gren.tuwien.weihnachtsmarkt.data.DataManager;
import at.gren.tuwien.weihnachtsmarkt.data.SyncService;
import at.gren.tuwien.weihnachtsmarkt.data.local.DatabaseHelper;
import at.gren.tuwien.weihnachtsmarkt.data.local.PreferencesHelper;
import at.gren.tuwien.weihnachtsmarkt.injection.ApplicationContext;
import at.gren.tuwien.weihnachtsmarkt.injection.module.ApplicationModule;
import at.gren.tuwien.weihnachtsmarkt.util.RxEventBus;

@Singleton
@Component(modules = ApplicationModule.class)
public interface ApplicationComponent {

    void inject(SyncService syncService);

    @ApplicationContext Context context();
    Application application();
    PreferencesHelper preferencesHelper();
    DatabaseHelper databaseHelper();
    DataManager dataManager();
    RxEventBus eventBus();
}
