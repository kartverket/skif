package no.statkart.skif.service.test.service;

/**
 * @author Henrik Fredholm
 * @since 2.0
 */
public class Test1ServiceImpl implements Test1Service {
    @Override
    public String helloWorld(String s) {
        return "Hello1: " + s;
    }
}
