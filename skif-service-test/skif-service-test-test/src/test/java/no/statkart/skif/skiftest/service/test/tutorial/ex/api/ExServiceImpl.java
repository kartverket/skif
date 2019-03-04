package no.statkart.skif.skiftest.service.test.tutorial.ex.api;

import com.google.common.base.Preconditions;

public class ExServiceImpl implements ExService {
    @Override
    public C doEx(A a, B b) {
        Preconditions.checkNotNull(a, "a kan ikke være null");
        Preconditions.checkNotNull(b, "b kan ikke være null");
        if (b.getY() < 0) {
            throw new ExException("B.y kan ikke være 0", a, b);
        } else if (a.getX() % b.getY() !=0) {
            throw new ExException("B.y må være et multiplum av a.x", a, b);
        }
        return new C(a.getX() + b.getY() , a.getX()/b.getY());
    }
}
