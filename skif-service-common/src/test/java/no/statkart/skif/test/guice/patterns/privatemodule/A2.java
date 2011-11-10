package no.statkart.skif.test.guice.patterns.privatemodule;

import com.google.inject.Inject;

/**
 * @author Henrik Fredholm
 * @since 2.0
 */
public class A2  {
    final B2 b;
    final C c;

    @Inject
    public A2(B2 b, C c) {
        this.b = b;
        this.c = c;
    }
}
