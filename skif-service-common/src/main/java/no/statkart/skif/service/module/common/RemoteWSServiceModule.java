package no.statkart.skif.service.module.common;

import com.google.inject.Binder;
import no.statkart.skif.module.ModuleConfiguration;
import no.statkart.skif.module.ModuleWithStrategy;
import no.statkart.skif.service.LoginUserHolder;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

/**
 * Klientmodul for å binde opp direkte tilgang til webservices.
 *
 * @author Tor Egil R. Strand
 * @since 2.1
 */
public class RemoteWSServiceModule extends ModuleWithStrategy<RemoteWSServiceModuleStrategy> {
   protected final Set<Class<?>> services = new HashSet<Class<?>>();

   /**
    * Oppretter en ny klientmodul for direkte tilgang til gitte webservices.
    *
    * @param moduleConfiguration konfigurasjon
    * @param services interface til webservices som skal gjøres tilgjengelig på klienten
    */
   public RemoteWSServiceModule(ModuleConfiguration moduleConfiguration, Collection<Class<?>> services) {
      super(RemoteWSServiceModuleStrategy.class, moduleConfiguration);

      this.services.addAll(services);
   }

   @Override
   protected void configure() {
      requireBindings();
      configureServices(RemoteWSServiceModule.this.binder());
   }

   protected void requireBindings() {
        requireBinding(LoginUserHolder.class);
        getStrategy().requireBindings(binder());
    }

   protected void configureServices(Binder outerBinder) {
      final RemoteWSServiceModuleStrategy strategy = getStrategy();
        for (Class<?> service : services) {
            strategy.bindCallServiceChainFactoryForService(outerBinder, service);
            strategy.bindService(outerBinder, service);
        }
   }
}
