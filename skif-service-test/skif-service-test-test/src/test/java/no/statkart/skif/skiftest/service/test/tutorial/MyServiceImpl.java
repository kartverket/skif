package no.statkart.skif.skiftest.service.test.tutorial;

import com.google.common.base.Preconditions;

class MyServiceImpl implements MyService {

    @Override
    public C myMethod(A a, B b) {
        Preconditions.checkNotNull(a, "a kan ikke være null");
        Preconditions.checkNotNull(b, "b kan ikke være null");
        if (b.getY() < 0) {
            throw new MyException("B.y kan ikke være 0", a, b);
        } else if (a.getX() % b.getY() !=0) {
            throw new MyException("B.y må være et multiplum av a.x", a, b);
        }
        return new C(a.getX() + b.getY() , a.getX()/b.getY());
    }
}
