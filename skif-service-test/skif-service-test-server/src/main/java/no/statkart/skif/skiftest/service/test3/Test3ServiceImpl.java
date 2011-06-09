package no.statkart.skif.skiftest.service.test3;

import no.statkart.skif.skiftest.domain.A;
import no.statkart.skif.skiftest.domain.B;
import no.statkart.skif.skiftest.exception.SimpleException;

public class Test3ServiceImpl implements Test3Service {

    @Override
    public A b2A(B b) {
        A a = new A();
        a.setText(b.getText());
        return a;
    }

    @Override
    public String testThrowExcpetion(String exceptionClass, String message) throws SimpleException {
        if (exceptionClass!=null) {
            new SimpleException(message);
        }
        return " No exception: " + message;
    }
}
