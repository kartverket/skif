package no.statkart.skif.test.guice.patterns.privatemodule;

import com.google.inject.Inject;

/**
 * @author Henrik Fredholm
 * @since 2.0
 */
public class M2 extends M {
    @Inject
    public M2(C c) {
        super(c);
    }
}
