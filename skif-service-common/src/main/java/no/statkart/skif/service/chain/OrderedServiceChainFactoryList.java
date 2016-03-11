package no.statkart.skif.service.chain;

import com.google.inject.Inject;
import no.statkart.skif.service.proxy.ProxyHandler;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Set;

/**
 * @author Henrik Fredholm
 * @since 2.0
 */
public abstract class OrderedServiceChainFactoryList<F extends ServiceChainFactory<S>, S> {
    final private List<F> orderedFactoryList;

    @Inject
    public OrderedServiceChainFactoryList(Set<F> factories) {
        this.orderedFactoryList = orderInSequence(factories);
    }

    private List<F> orderInSequence(Set<F> factories) {
        List<F> factoryList = new ArrayList<F>(factories);
        Collections.sort(factoryList, new Comparator<ServiceChainFactory<?>>() {
            @Override
            public int compare(ServiceChainFactory<?> o1, ServiceChainFactory<?> o2) {
                final float diff = o1.getChainPosition() - o2.getChainPosition();
                if (diff < 0) {
                    return -1;
                } else if (diff == 0) {
                    return 0;
                } else {
                    return 1;
                }
            }
        });
        return factoryList;
    }

    public ProxyHandler<S> buildChain() {
        ProxyHandler<S> firstInChain = null;
        for (ServiceChainFactory<S> factory : orderedFactoryList) {
            if (firstInChain == null) {
                firstInChain = factory.createChain();
            } else {
                firstInChain = factory.extendChain(firstInChain);
            }
        }
        return firstInChain;
    }


    public List<F> getOrderedFactoryList() {
        return orderedFactoryList;
    }
}
