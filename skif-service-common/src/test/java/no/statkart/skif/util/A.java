package no.statkart.skif.util;

import java.io.Serializable;

/**
 */
public class A implements Serializable {
    private static final long serialVersionUID = 1L;

    public String a;
    public transient boolean b;

    public A(String a, boolean b) {
        this.a = a;
        this.b = b;
    }
}
