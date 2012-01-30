package no.statkart.skif.storetest.service.id;

import com.google.inject.Inject;
import com.google.inject.Provider;
import no.statkart.skif.config.Configuration;
import no.statkart.skif.service.sequence.DefaultSequenceBlockAllocatorServiceImpl;

import java.sql.Connection;

/**
 * @author Roar Ingebrigtsen
 * @since 2.0
 */
public class SequenceBlockAllocatorServiceImpl extends DefaultSequenceBlockAllocatorServiceImpl implements SequenceBlockAllocatorService {

    @Inject
    public SequenceBlockAllocatorServiceImpl(Provider<Connection> connectionProvider, Configuration configuration) {
        super(connectionProvider, configuration);
    }

}
