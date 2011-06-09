package no.statkart.skif.mapper;


/**
 * Default ObjectFactory implementasjon som oppretter
 * et nytt tom objekt hver gang  {@link #getInitialObject} kalles.
 * @author Henrik Fredholm
 */
public class DefaultObjectFactory implements ObjectFactory {
    @Override
    public <S, T> T getInitialObject(S source, Class<T> target) throws InstantiationException, IllegalAccessException {
            return target.newInstance();
    }

    @Override
    public Mapping getMapping() {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void setMapping(Mapping m) {
        //To change body of implemented methods use File | Settings | File Templates.
    }
}