package no.statkart.skif.service.chain;

import com.google.inject.Inject;
import com.google.inject.Singleton;

import java.util.Set;

/**
 * @author Henrik Fredholm
 * @since 1.1
 */
@Singleton
public class OrderedCallServiceChainFactoryList<S> extends OrderedServiceChainFactoryList<CallServiceChainFactory<S>, S> {
    @Inject
    public OrderedCallServiceChainFactoryList(Set<CallServiceChainFactory<S>> factories) {
        super(factories);
    }
}
