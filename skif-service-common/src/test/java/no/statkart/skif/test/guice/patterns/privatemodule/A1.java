package no.statkart.skif.test.guice.patterns.privatemodule;

import com.google.inject.Inject;

/**
 * @author Henrik Fredholm
 * @since 2.0
 */
public class A1{
    final B1 b;
    final C c;

    @Inject
    public A1(B1 b, C c) {
        this.b = b;
        this.c = c;
    }
}
