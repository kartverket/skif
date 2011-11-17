package no.statkart.skif.test.guice.patterns.privatemodule;

import com.google.inject.Inject;

/**
 * @author Henrik Fredholm
 * @since 2.0
 */
public class B2 {
    final M m;

    @Inject
    public B2(M m) {
        this.m = m;
    }
}
