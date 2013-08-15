package no.statkart.skif.storetest.domain.relation;

import com.google.common.collect.ImmutableList;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import no.statkart.skif.mockup.AbstractMockupFacade;
import no.statkart.skif.mockup.AbstractMockupFactory;
import no.statkart.skif.storetest.mockup.*;

import java.util.List;

/**
 * MockupFacade for StoreTest tester
 *
 * @author Henrik Fredholm
 * @since 2.3
 */
@Singleton
public class RelationMockupFacade extends AbstractMockupFacade {

    @Inject private X1AMockupFactory x1AMockupFactory;
    @Inject private X1BOneMockupFactory x1BOneMockupFactory;

    @Override
    public List<? extends AbstractMockupFactory> getAllMockupFactories() {
        return ImmutableList.of(
                x1AMockupFactory,
                x1BOneMockupFactory
        );
    }

    public X1AMockupFactory getX1AMockupFactory() {
        return x1AMockupFactory;
    }

    public X1BOneMockupFactory getX1BOneMockupFactory() {
        return x1BOneMockupFactory;
    }
}
