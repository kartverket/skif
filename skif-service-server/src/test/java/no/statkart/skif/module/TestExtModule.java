package no.statkart.skif.module;

import com.google.inject.Provides;
import com.google.inject.Singleton;
import com.google.inject.name.Named;
import com.google.inject.name.Names;
import no.statkart.skif.SkifModule;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Henrik Fredholm
 * @since 2.0
 */
public class TestExtModule extends SkifModule {

    public TestExtModule(ModuleConfiguration moduleConfiguration) {
        super(moduleConfiguration);
    }

    @Override
    protected void configure() {
        bind(List.class).annotatedWith(Names.named("testExt")).to(ArrayList.class).in(Singleton.class); // OBS! Her er det kun bindingen fra @Named List som er singleton, ikke klassen ArrayList
    }

    @Provides
    @Named("testExt2")
    List getList(@Named("test") List list) {
        return list;
    }

}
