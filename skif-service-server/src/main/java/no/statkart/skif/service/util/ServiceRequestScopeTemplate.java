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

    public void execute(Principal principal, Runnable runnable) {
        ServiceRequestContext serviceRequestContext = createServiceRequestContext(principal);

        serviceRequestScope.enter();
        try {
            serviceRequestScope.seed(ServiceRequestContext.class, serviceRequestContext);
            runnable.run();
        } finally {
            serviceRequestScope.exit();
        }
    }

    public <V> V execute(Principal principal, Callable<V> callable) throws Exception {
        ServiceRequestContext serviceRequestContext = createServiceRequestContext(principal);

        serviceRequestScope.enter();
        try {
            serviceRequestScope.seed(ServiceRequestContext.class, serviceRequestContext);
            return callable.call();
        } finally {
            serviceRequestScope.exit();
        }
    }

    private ServiceRequestContext createServiceRequestContext(Principal principal) {
        // CallId settes til 0 fordi dette i seg selv ikke er et call context egentlig.
        // Dette kan brukes til å skille denne klassene fra SkifWSInterceptor, siden den fyller inn en callId.
        //noinspection UnnecessaryLocalVariable
        ServiceRequestContext serviceRequestContext = new ServiceRequestContext(
                principal,
                0,
                TxMode.NOT_IN_EJB,
                false,
                null
        );
        return serviceRequestContext;
    }
}
