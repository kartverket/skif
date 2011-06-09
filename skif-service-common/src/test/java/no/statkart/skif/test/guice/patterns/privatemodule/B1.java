package no.statkart.skif.test.guice.patterns.privatemodule;

import com.google.inject.Inject;

/**
 * @author Henrik Fredholm
 * @since 1.1
 */
public class B1 {
    final M m;

    @Inject
    public B1(M m) {
        this.m = m;
    }
}
