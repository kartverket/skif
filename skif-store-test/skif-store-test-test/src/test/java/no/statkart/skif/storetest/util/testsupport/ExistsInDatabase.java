package no.statkart.skif.storetest.util.testsupport;

import com.google.inject.Inject;
import no.statkart.skif.service.RunOnServerMethod;
import org.hibernate.Session;

public class ExistsInDatabase extends RunOnServerMethod {

    @Inject
    Session session;

    final String className;
    final Long id;

    public ExistsInDatabase(String className, Long id) {
        this.className = className;
        this.id = id;
    }

    @Override
    public Boolean run() {
        Number count = session.createNativeQuery(String.format("select count(*) from %s where id=:id", className), Long.class)
            .setParameter("id", id)
            .uniqueResult();
        return count.longValue() > 0L;
    }

}
