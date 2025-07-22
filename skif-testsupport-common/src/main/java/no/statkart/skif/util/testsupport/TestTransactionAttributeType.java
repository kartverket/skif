package no.statkart.skif.util.testsupport;

import no.statkart.skif.service.RunOnServerService;
import no.statkart.skif.service.RunOnServerWithTxBeanManagedService;
import no.statkart.skif.service.RunOnServerWithTxNotSupportedService;
import no.statkart.skif.service.RunOnServerWithTxRequiredService;
import no.statkart.skif.service.RunOnServerWithTxRequiresNewService;

/**
 * Enumerasjon som angir hvilken transaksjonsstøtte som en server-test-metode skal avvikles under på serveren. Hver
 * enumerert verdi er avhengig av serveren implementere en gitt service den ønskede transaksjonsstøtten
 *
 * @author Henrik Fredholm
 * @see  TestTransactionAttribute
 * @see  no.statkart.skif.service.RunOnServerWithTxNotSupportedService
 * @see  no.statkart.skif.service.RunOnServerWithTxRequiresNewService
 * @see  no.statkart.skif.service.RunOnServerWithTxBeanManagedService
 *
 * @since 2.1
 */
public enum TestTransactionAttributeType {
    TX_NOT_SUPPORTED (RunOnServerWithTxNotSupportedService.class),
    TX_REQUIRED(RunOnServerWithTxRequiredService.class),
    TX_REQUIRES_NEW(RunOnServerWithTxRequiresNewService.class),
    TX_BEAN(RunOnServerWithTxBeanManagedService.class);

    private Class<? extends RunOnServerService> serviceClass;
    TestTransactionAttributeType(Class<? extends RunOnServerService> serviceClass) {
        this.serviceClass = serviceClass;
    }

    public Class<? extends RunOnServerService> getServiceClass() {
        return serviceClass;
    }
}
