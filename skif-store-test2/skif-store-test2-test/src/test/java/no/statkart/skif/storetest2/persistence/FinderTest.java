package no.statkart.skif.storetest2.persistence;

import com.google.common.base.Preconditions;
import com.google.inject.Inject;
import no.statkart.skif.store.persistence.PersistenceSessionManager;
import no.statkart.skif.storetest2.util.testsupport.StoreTest2ServerTestCase;
import org.hibernate.Session;

import static no.statkart.skif.storetest2.persistence.With.with;
import static org.fest.assertions.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.testng.Assert.assertEquals;

/**
 * Tester bruk av Oracle ARRAY i quiries via Hibernate user type og jdbc
 *
 * @author Henrik Fredholm
 */
public class FinderTest extends StoreTest2ServerTestCase {
    @Inject
    PersistenceSessionManager persistenceSessionManager;

    public void test() {
        final Session s = mock(Session.class);
        final H<Session> sessionH = new H<Session>(s);
        with(new X(sessionH) {
            @Override
            void x() {
                assertEquals(sessionH.get(), s);
                //To change body of implemented methods use File | Settings | File Templates.
            }
        });

    }
}

abstract class X {
    final private H[] hs;
    public X(H<?>... hs) {this.hs = hs;}
    public void start() { for (int i=0; i<hs.length; i++) {hs[i].start();}}
    public void end() {for (int i=0; i<hs.length; i++) {hs[i].start();}}
    abstract void x();
}

class With {
    public static void with(X x) {
        x.start();
        x.x();
        x.end();
    }
}

class H<T> {
    private boolean inScope;
    private final T t;

    H(T t) {
        this.t = t;
    }

    public void  start() {inScope=true;}
    public void  end() {inScope=false;}
    public T get() {
        Preconditions.checkState(inScope); return t;
    }
}

