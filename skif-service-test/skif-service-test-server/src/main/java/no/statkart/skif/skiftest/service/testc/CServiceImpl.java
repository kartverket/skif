package no.statkart.skif.skiftest.service.testc;

import com.google.inject.Inject;
import com.google.inject.Provider;
import no.statkart.skif.skiftest.service.ServiceSelector;

import java.util.List;

/**
 * @author Henrik Fredholm
 * @since 2.0
 */
@SuppressWarnings("unused")
public class CServiceImpl implements CService {
    private final Provider<ServiceSelector> serviceSelector;

    @Inject
    public CServiceImpl(Provider<ServiceSelector> serviceSelector) {
        this.serviceSelector = serviceSelector;
    }

    @Override
    public String m1(List<String> callSpec) {
        return "CService.m1" + serviceSelector.get().callService(callSpec);
    }

    @Override
    public String m2(List<String> callSpec) {
        return "CService.m2" + serviceSelector.get().callService(callSpec);
    }

    @Override
    public String m3(List<String> callSpec) {
        return "CService.m3" + serviceSelector.get().callService(callSpec);
    }
}
