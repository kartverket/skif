package no.statkart.skif.test.guice.patterns.privatemodule;

import com.google.inject.Inject;

/**
 * @author Henrik Fredholm
 * @since 1.1
 */
public class M1 extends M {
    @Inject
    public M1(C c) {
        super(c);
    }
}
