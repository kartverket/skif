package no.statkart.skif.util.testsupport;

import no.statkart.skif.service.BeanManagedTransactionRunOnServerService;
import no.statkart.skif.service.ContainerManagedNotSupportedTransactionRunOnServerService;
import no.statkart.skif.service.ContainerManagedRequiresNewTransactionRunOnServerService;
import no.statkart.skif.service.RunOnServerService;

/**
 * Enumerasjon som angir hvilken transaksjonsstøtte som en server-test-metode skal avvikles under på serveren. Hver
 * enumerert verdi er avhengig av serveren implementere en gitt service den ønskede transaksjonsstøtten
 *
 * @author Henrik Fredholm
 * @see  TestServerMethodTransactionAttribute
 * @see  no.statkart.skif.service.ContainerManagedNotSupportedTransactionRunOnServerService
 * @see  no.statkart.skif.service.ContainerManagedRequiresNewTransactionRunOnServerService
 * @see  no.statkart.skif.service.BeanManagedTransactionRunOnServerService
 *
 * @since 2.1
 */

public enum TestServerMethodTransactionAttributeType {
    NOT_SUPPORTED (ContainerManagedNotSupportedTransactionRunOnServerService.class),
    REQUIRES_NEW(ContainerManagedRequiresNewTransactionRunOnServerService.class),
    BEAN(BeanManagedTransactionRunOnServerService.class);

    private Class<? extends RunOnServerService> serviceClass;
    TestServerMethodTransactionAttributeType(Class<? extends RunOnServerService> serviceClass) {
        this.serviceClass = serviceClass;
    }

    public Class<? extends RunOnServerService> getServiceClass() {
        return serviceClass;
    }
}
