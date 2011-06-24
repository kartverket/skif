package no.statkart.skif.service.test.service;

import com.google.inject.Inject;
import com.google.inject.Provider;

import java.io.Serializable;
import java.util.List;

/**
 * @author Henrik Fredholm
 * @since 1.1
 */
public class AServiceImpl implements AService {
    private final Provider<ServiceSelector> serviceSelector;

    @Inject
    public AServiceImpl(Provider<ServiceSelector> serviceSelector) {
        this.serviceSelector = serviceSelector;
    }

    @Override
    public String m1(List<String> callSpec) {
        return "AService.m1" + serviceSelector.get().callService(callSpec);
    }

    @Override
    public String m2(List<String> callSpec) {
        return "AService.m2" + serviceSelector.get().callService(callSpec);
    }

    @Override
    public String m3(List<String> callSpec) {
        return "AService.m3" + serviceSelector.get().callService(callSpec);
    }
}
