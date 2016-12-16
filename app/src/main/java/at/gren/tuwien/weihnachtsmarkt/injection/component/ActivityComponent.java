package at.gren.tuwien.weihnachtsmarkt.injection.component;

import dagger.Subcomponent;
import at.gren.tuwien.weihnachtsmarkt.injection.PerActivity;
import at.gren.tuwien.weihnachtsmarkt.injection.module.ActivityModule;
import at.gren.tuwien.weihnachtsmarkt.ui.main.MainActivity;

/**
 * This component inject dependencies to all Activities across the application
 */
@PerActivity
@Subcomponent(modules = ActivityModule.class)
public interface ActivityComponent {

    void inject(MainActivity mainActivity);

}
