package no.statkart.skif.skiftest.service;

import com.google.inject.Inject;
import no.statkart.skif.exception.ImplementationException;
import no.statkart.skif.skiftest.service.testa.AService;
import no.statkart.skif.skiftest.service.testb.BService;
import no.statkart.skif.skiftest.service.testc.CService;

import java.util.List;

/**
 * @author Henrik Fredholm
 * @since 2.0
 */
public class ServiceSelector {
    private final AService aService;
    private final BService bService;
    private final CService cService;


    @Inject
    public ServiceSelector(AService aService, BService bService, CService cService) {
        this.aService = aService;
        this.bService = bService;
        this.cService = cService;
    }

    public String callService(List<String> callSpec) {
        if (callSpec.size() == 0) return "";
        final List<String> subList = callSpec.subList(1, callSpec.size());
        final String s = callSpec.get(0);
        final String[] split = s.split("\\.");
        if (split[0].equals("AService")) {
            if (split[1].equals("m1")) {
                return " " + aService.m1(subList);
            } else if (split[1].equals("m2")) {
                return " " + aService.m2(subList);
            } else if (split[1].equals("m3")) {
                return " " + aService.m3(subList);
            } else {
                throw new ImplementationException("Illegal callSpec: " + callSpec);
            }
        } else if (split[0].equals("BService")) {
            if (split[1].equals("m1")) {
                return " " + bService.m1(subList);
            } else if (split[1].equals("m2")) {
                return " " + bService.m2(subList);
            } else if (split[1].equals("m3")) {
                return " " + bService.m3(subList);
            } else {
                throw new ImplementationException("Illegal callSpec: " + callSpec);
            }

        } else if (split[0].equals("CService")) {
            if (split[1].equals("m1")) {
                return " " + cService.m1(subList);
            } else if (split[1].equals("m2")) {
                return " " + cService.m2(subList);
            } else if (split[1].equals("m3")) {
                return " " + cService.m3(subList);
            } else {
                throw new ImplementationException("Illegal callSpec: " + callSpec);
            }
        } else {
            throw new ImplementationException("Illegal callSpec: " + callSpec);
        }
    }
}
