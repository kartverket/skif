package no.statkart.skif.service.util;

import com.google.inject.Inject;
import no.statkart.skif.service.ServiceRequestContext;
import no.statkart.skif.service.TxMode;
import no.statkart.skif.service.scope.ServiceRequestScope;

import java.security.Principal;
import java.util.concurrent.Callable;

/**
 * Utfører gitt kode i et service request scope. På den måten kan man kalle inn til en SKIF-EJB fra vanlig JEE-kode.
 * Denne klassen bør ikke brukes dersom man ikke vet at man befinner seg utenfor SKIF. Den er heller ikke laget for
 * single-vm.
 */
@SuppressWarnings("UnusedDeclaration")
public class ServiceRequestScopeTemplate {
    private final ServiceRequestScope serviceRequestScope;

    @Inject
    public ServiceRequestScopeTemplate(ServiceRequestScope serviceRequestScope) {
        this.serviceRequestScope = serviceRequestScope;
    }

    /**
     * Tilsvarende som {@link #execute(String, Principal, Runnable)}, men bruker klassenavnet til
     * {@code Runnable}-implementasjonen som {@code serviceName}.
     */
    public void execute(Principal principal, Runnable runnable) {
        execute(runnable.getClass().getName(), principal, runnable);
    }

    public void execute(String serviceName, Principal principal, Runnable runnable) {
        ServiceRequestContext serviceRequestContext = createServiceRequestContext(serviceName, principal);

        serviceRequestScope.enter();
        try {
            serviceRequestScope.seed(ServiceRequestContext.class, serviceRequestContext);
            runnable.run();
        } finally {
            serviceRequestScope.exit();
        }
    }

    /**
     * Tilsvarende som {@link #execute(String, java.security.Principal, Callable)}, men bruker klassenavnet til
     * {@code Callable}-implementasjonen som {@code serviceName}.
     */
    public <V> V execute(Principal principal, Callable<V> callable) throws Exception {
        return execute(callable.getClass().getName(), principal, callable);
    }

    public <V> V execute(String serviceName, Principal principal, Callable<V> callable) throws Exception {
        ServiceRequestContext serviceRequestContext = createServiceRequestContext(serviceName, principal);

        serviceRequestScope.enter();
        try {
            serviceRequestScope.seed(ServiceRequestContext.class, serviceRequestContext);
            return callable.call();
        } finally {
            serviceRequestScope.exit();
        }
    }

    private ServiceRequestContext createServiceRequestContext(String serviceName, Principal principal) {
        // CallId settes til 0 fordi dette i seg selv ikke er et call context egentlig.
        // Dette kan brukes til å skille denne klassene fra SkifWSInterceptor, siden den fyller inn en callId.
        //noinspection UnnecessaryLocalVariable
        ServiceRequestContext serviceRequestContext = new ServiceRequestContext(
                principal,
                serviceName,
                0,
                TxMode.NOT_IN_EJB,
                false,
                null
        );
        return serviceRequestContext;
    }
}
