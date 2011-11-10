package no.statkart.skif.service.test.service;

import com.google.inject.Inject;
import com.google.inject.Provider;

import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import java.util.List;

/**
 * @author Henrik Fredholm
 * @since 2.0
 */
public class BServiceImpl implements BService {
    private final Provider<ServiceSelector> serviceSelector;

    @Inject
    public BServiceImpl(Provider<ServiceSelector> serviceSelector) {
        this.serviceSelector = serviceSelector;
    }

    @Override
    public String m1(List<String> callSpec) {
        return "BService.m1" + serviceSelector.get().callService(callSpec);
    }

    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    @Override
    public String m2(List<String> callSpec) {
        return "BService.m2" + serviceSelector.get().callService(callSpec);
    }

    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    @Override
    public String m3(List<String> callSpec) {
        return "BService.m3" + serviceSelector.get().callService(callSpec);
    }
}
