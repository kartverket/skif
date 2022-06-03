package no.statkart.skif.service.module.common;

import com.google.inject.Provides;
import com.google.inject.Singleton;
import com.google.inject.multibindings.OptionalBinder;
import com.google.inject.util.Providers;
import no.statkart.skif.ServiceMode;
import no.statkart.skif.config.Configuration;
import no.statkart.skif.module.ModuleConfiguration;
import no.statkart.skif.module.ModuleWithStrategy;
import no.statkart.skif.service.*;
import no.statkart.skif.store.SnapshotVersion;
import no.statkart.skif.store.SnapshotVersionContext;

import javax.net.ssl.HostnameVerifier;

/**
 * @author Henrik Fredholm
 * @since 2.0
 */
public class RemoteServerModule extends ModuleWithStrategy<RemoteServerModuleStrategy> {
    private Class<? extends ServiceContext> serviceContextClass = DefaultServiceContext.class;
    private Class<? extends LoginUserHolder> userLoginHolderClass = LoginUserHolderImpl.class;
    private Class<? extends ServerUrlHolder> serverUrlHolderClass = ServerUrlHolderImpl.class;
    private Class<? extends HostnameVerifier> hostnameVerifierClass;

    public RemoteServerModule(ModuleConfiguration configuration) {
        super(RemoteServerModuleStrategy.class, configuration);
    }

    public Class<? extends ServiceContext> getServiceContextClass() {
        return serviceContextClass;
    }

    public RemoteServerModule setServiceContextClass(Class<? extends ServiceContext> serviceContextClass) {
        this.serviceContextClass = serviceContextClass;
        return this;
    }

    public Class<? extends LoginUserHolder> getUserLoginHolderClass() {
        return userLoginHolderClass;
    }

    public RemoteServerModule setUserLoginHolderClass(Class<? extends LoginUserHolder> userLoginHolderClass) {
        this.userLoginHolderClass = userLoginHolderClass;
        return this;
    }

    public Class<? extends ServerUrlHolder> getServerUrlHolderClass() {
        return serverUrlHolderClass;
    }

    public RemoteServerModule setServerUrlHolderClass(Class<? extends ServerUrlHolder> serverUrlHolderClass) {
        this.serverUrlHolderClass = serverUrlHolderClass;
        return this;
    }

    public Class<? extends HostnameVerifier> getHostnameVerifierClass() {
        return hostnameVerifierClass;
    }

    public RemoteServerModule setHostnameVerifierClass(Class<? extends HostnameVerifier> hostnameVerifierClass) {
        this.hostnameVerifierClass = hostnameVerifierClass;
        return this;
    }

    @Override
    protected void configure() {
        setStrategyInstance();

        // Standard bindinger som må være med
        bind(ServiceMode.class).toInstance(moduleConfiguration.getServiceMode());
        bind(ServiceContext.class).to(serviceContextClass);
        bind(serviceContextClass).in(Singleton.class);

        bind(Configuration.class).toInstance(moduleConfiguration.getConfiguration());
        bind(ModuleConfiguration.class).toInstance(moduleConfiguration);

        bind(LoginUserHolder.class).to(userLoginHolderClass);
        bind(userLoginHolderClass).in(Singleton.class);
        bind(ServerUrlHolder.class).to(serverUrlHolderClass);
        bind(serverUrlHolderClass).in(Singleton.class);

        // SnapshotVersionContext har trådlokal verdi og bindes opp som ekte singleton istedet for med ServiceRequest scope
        bind(SnapshotVersionContext.class).toInstance(SnapshotVersionContext.getInstance());

        if (hostnameVerifierClass != null) {
            OptionalBinder.newOptionalBinder(binder(), HostnameVerifier.class).setBinding().to(hostnameVerifierClass);
        }
        strategy.configure(binder());

    }

    @Provides
    SnapshotVersion snapshotVersionProvider(SnapshotVersionContext snapshotVersionContext) {
        return snapshotVersionContext.getSnapshotVersion();
    }
}
