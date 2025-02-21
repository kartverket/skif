package no.statkart.skif.service.test.service;

import com.google.inject.Inject;
import com.google.inject.Provider;
import jakarta.ejb.TransactionAttribute;
import jakarta.ejb.TransactionAttributeType;

import java.util.List;

/**
 * Transaction attribute annotasjon ligger på implementasjosklassen siden det i dette tilfelle ikke finnes noen EJBBean
 * klasse, dvs implementasjonen kan kun brukes i SingleVm mode (dersom det hadde vært en EJBBean klasse hentes annotasjonene
 * derfra alltid - også i SingleVm mode)
 *
 * @author Henrik Fredholm
 * @since 2.0
 */
@SuppressWarnings("unused")
public class AServiceImpl implements AService {
    private final Provider<ServiceSelector> serviceSelector;

    @Inject
    public AServiceImpl(Provider<ServiceSelector> serviceSelector) {
        this.serviceSelector = serviceSelector;
    }

    @Override
    @TransactionAttribute(TransactionAttributeType.SUPPORTS)
    public String m1(List<String> callSpec) {
        return "AService.m1" + serviceSelector.get().callService(callSpec);
    }

    @Override
    @TransactionAttribute(TransactionAttributeType.SUPPORTS)
    public String m2(List<String> callSpec) {
        return "AService.m2" + serviceSelector.get().callService(callSpec);
    }

    @Override
    @TransactionAttribute(TransactionAttributeType.SUPPORTS)
    public String m3(List<String> callSpec) {
        return "AService.m3" + serviceSelector.get().callService(callSpec);
    }
}
