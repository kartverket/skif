package no.statkart.skif.persistence;

import no.statkart.skif.service.scope.ServiceRequestScoped;

import java.sql.Timestamp;

/**
 * Dette er en enkel implementasjon som bruker tidspunktet denne tjenesten først blir etterspurt innenfor et
 * {@link no.statkart.skif.service.scope.ServiceRequestScope} som transaksjonstidspunkt. Dette betyr at flere
 * transaksjoner innenfor samme ServiceRequestScope får samme transaksjonstidspunkt. Hvis dette ikke er ønskelig, så må
 * man bruke en annen implementasjon.
 *
 * @since 2.8.0
 */
@ServiceRequestScoped
public class DefaultTransactionTimeServiceImpl implements TransactionTimeService {
    private final long transactionTime = System.currentTimeMillis();

    @Override
    public Timestamp getTransactionTime() {
        return new Timestamp(transactionTime);
    }
}
