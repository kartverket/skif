package no.statkart.skif.skiftest.service.test1;

/**
 * @author Roar Ingebrigtsen
 * @since 2.0
 */
public class Test1ServiceImpl implements Test1Service {
    @Override
    public String helloWorld(String message) {
        return "Hello1: " + message;
    }
}
