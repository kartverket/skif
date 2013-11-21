package no.statkart.skif.storetest.mockup;

import com.google.inject.Injector;
import no.statkart.skif.config.Configuration;
import no.statkart.skif.config.SkifClientConfiguration;
import no.statkart.skif.config.SkifConfigConstants;
import no.statkart.skif.config.SkifServerConfiguration;
import no.statkart.skif.module.ModuleBuilder;
import no.statkart.skif.service.LoginUser;
import no.statkart.skif.service.LoginUserHolder;
import no.statkart.skif.service.ServerUrlHolder;
import no.statkart.skif.storetest.config.StoreTestClientModule;
import no.statkart.skif.storetest.config.StoreTestServerModule;

/**
 * Laster mockupsettet inn i databasen.
 *
 * @author Tor Egil R. Strand
 * @since 2.4.0
 */
public class LoadTestdata {
    public static void main(String[] args) {
        ModuleBuilder moduleBuilder = new ModuleBuilder();
        moduleBuilder.setModuleClass(StoreTestClientModule.class);
        moduleBuilder.setConfiguration(new SkifClientConfiguration());
        moduleBuilder.setSingleVmServerModuleClass(StoreTestServerModule.class);
        moduleBuilder.setSingleVmServerConfiguration(new SkifServerConfiguration());
        Injector injector = moduleBuilder.buildInjector();

        Configuration configuration = injector.getInstance(Configuration.class);

        String serverUrl = configuration.getString(SkifConfigConstants.SERVER_URL);
        final ServerUrlHolder serverUrlHolder = injector.getInstance(ServerUrlHolder.class);
        serverUrlHolder.set(serverUrl);

        LoginUserHolder userHolder = injector.getInstance(LoginUserHolder.class);
        String username = configuration.getString(SkifConfigConstants.SERVER_USERNAME);
        String password = configuration.getString(SkifConfigConstants.SERVER_PASSWORD);
        userHolder.set(new LoginUser(username, password));

        StoreTestMockupFacadeFactory mockupFacadeFactory = injector.getInstance(StoreTestMockupFacadeFactory.class);
        mockupFacadeFactory.getReadMockupFacadeAndSaveData();
    }
}
