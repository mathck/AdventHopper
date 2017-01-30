package at.gren.tuwien.weihnachtsmarkt.injection.component;

import at.gren.tuwien.weihnachtsmarkt.ui.detailed.DetailedActivity;
import at.gren.tuwien.weihnachtsmarkt.ui.map.MapActivity;
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
    void inject(DetailedActivity detailedActivity);
    void inject(MapActivity mapActivity);

}
