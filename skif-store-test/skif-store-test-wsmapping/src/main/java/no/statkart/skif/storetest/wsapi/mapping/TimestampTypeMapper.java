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
    public void mapDomainObject(Timestamp source, no.statkart.skif.storetest.wsapi.domain.basetyper.Timestamp target) {
        super.mapDomainObject(source, target);

        target.setTime(map.d2w(source.getTime()));
        target.setNanos(map.d2w(source.getNanos()));

    }

    @Override
    public void mapWsapiObject(no.statkart.skif.storetest.wsapi.domain.basetyper.Timestamp source, Timestamp target) {
        super.mapWsapiObject(source, target);

    }

    @Override
    protected Timestamp getInitialDomainObject(no.statkart.skif.storetest.wsapi.domain.basetyper.Timestamp source) throws NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException {
        Timestamp timestamp = new Timestamp(map.w2d(source.getTime()));
        timestamp.setNanos(map.w2d(source.getNanos()));
        return timestamp;
    }
}
