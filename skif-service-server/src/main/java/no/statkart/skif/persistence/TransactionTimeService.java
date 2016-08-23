package no.statkart.skif.persistence;

import java.sql.Timestamp;

/**
 * @since 2.8.0
 */
public interface TransactionTimeService {
    Timestamp getTransactionTime();
}
