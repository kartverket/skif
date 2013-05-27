package no.statkart.skif.storetest.wsapi.mapping;

import java.lang.reflect.InvocationTargetException;
import java.sql.Timestamp;

/**
 * @author Roar Ingebrigtsen
 * @since 2.0
 */
public class TimestampTypeMapper extends AbstractStoreTestTypeMapper<no.statkart.skif.storetest.wsapi.domain.basetyper.Timestamp, Timestamp> {

    protected TimestampTypeMapper() {
        super(no.statkart.skif.storetest.wsapi.domain.basetyper.Timestamp.class, Timestamp.class);
    }

    @Override
    public no.statkart.skif.storetest.wsapi.domain.basetyper.Timestamp mapDomainObject(Timestamp source) {
        no.statkart.skif.storetest.wsapi.domain.basetyper.Timestamp target = createWsapiT();
        target.setTime(getMapping().d2w(source.getTime()));
        target.setNanos(getMapping().d2w(source.getNanos()));
        return target;
    }

    @Override
    public Timestamp mapWsapiObject(no.statkart.skif.storetest.wsapi.domain.basetyper.Timestamp source) {
        Timestamp timestamp = new Timestamp(getMapping().w2d(source.getTime()));
        timestamp.setNanos(getMapping().w2d(source.getNanos()));
        return timestamp;
    }
}
