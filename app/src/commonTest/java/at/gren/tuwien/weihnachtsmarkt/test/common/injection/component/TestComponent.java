package at.gren.tuwien.weihnachtsmarkt.test.common.injection.component;

import javax.inject.Singleton;

import dagger.Component;
import at.gren.tuwien.weihnachtsmarkt.injection.component.ApplicationComponent;
import at.gren.tuwien.weihnachtsmarkt.test.common.injection.module.ApplicationTestModule;

@Singleton
@Component(modules = ApplicationTestModule.class)
public interface TestComponent extends ApplicationComponent {

}
