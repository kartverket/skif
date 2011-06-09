package no.statkart.skif.skiftest.service.test2;

import no.statkart.skif.skiftest.domain.A;
import no.statkart.skif.skiftest.domain.B;

public class Test2ServiceImpl implements Test2Service {

    @Override
    public B a2B(A a) {
        B b = new B();
        b.setText(a.getText());
        return b;
    }
}
