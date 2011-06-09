package no.statkart.skif.skiftest.service.testb;

import com.google.inject.Inject;
import com.google.inject.Provider;
import no.statkart.skif.skiftest.service.ServiceSelector;

import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import java.util.List;

/**
 * @author Henrik Fredholm
 * @since 1.1
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

    @Override
    public String m2(List<String> callSpec) {
        return "BService.m2" + serviceSelector.get().callService(callSpec);
    }

    @Override
    public String m3(List<String> callSpec) {
        return "BService.m3" + serviceSelector.get().callService(callSpec);
    }
}
