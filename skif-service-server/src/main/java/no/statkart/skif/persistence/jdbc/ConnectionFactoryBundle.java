package no.statkart.skif.persistence.jdbc;

import java.util.Arrays;
import java.util.List;

/**
 * @author Henrik Fredholm
 */
public class ConnectionFactoryBundle {
    protected final ConnectionFactory[] bundle;

    public ConnectionFactoryBundle(ConnectionFactory... bundle) {
        this.bundle = bundle;
    }

    public List<ConnectionFactory> getBundle() {
        return Arrays.asList(bundle);
    }
}
