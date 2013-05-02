package no.statkart.skif.mapper;


import no.statkart.skif.exception.ImplementationException;
import no.statkart.skif.internal.util.InternalClassUtils;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;

/**
 * ObjectFactory implementasjon som håndterer opprettelse av objekter med rot i Spif.
 * Target-objektene kan være net.sf.spif.BubbleId, da kopieres id-verdien fra source-objektet.
 * Target-objektene kan også være koder, da benyttes fromInt()-metoden for å opprette instanser basert på koden ("getValue") i source-objektet.
 *
 * @author Steinar Hansen
 */
public class DefaultSpifObjectFactoryWithKodeAndIdHandling implements ObjectFactory {
    @Override
    public <S, T> T getInitialObject(S source, Class<T> target) throws InstantiationException, IllegalAccessException, NoSuchMethodException, InvocationTargetException {
        T retVal = null;
        if (isKode(target)) {
            //KodeId til Kode
            Method getValueMethod = source.getClass().getMethod("getValue");
            //Må sette at constructor-parameter-typen skal være Object, siden getKode returnerer en int.
            Method fromIntMethod = target.getMethod("fromInt", int.class);
            final Long value = (Long) getValueMethod.invoke(source);
            retVal = (T) fromIntMethod.invoke(target, value.intValue());
        } else if (implementsBubbleId(target)) {
            try {
                List<Class<?>> targetInterfaces = InternalClassUtils.getAllInterfaces(target);
                for (int i = 0; i < targetInterfaces.size(); i++) {
                    Class aTargetInterface = targetInterfaces.get(i);
                    if(aTargetInterface.getName().equals("net.sf.spif.BubbleId")){
                        Method getIdMethodOnSource = source.getClass().getMethod("getValue");
                        Constructor constructor = target.getDeclaredConstructor(long.class);
                        constructor.setAccessible(true);
                        retVal = (T) constructor.newInstance(getIdMethodOnSource.invoke(source));
                        break;
                    }
                }

            } catch (NoSuchMethodException e) {
                throw new ImplementationException("Could not find constructor for target class", e);
            } catch (InvocationTargetException e) {
                throw new ImplementationException("Could not invoke constructor for target class", e);
            } catch (InstantiationException e) {
                throw new ImplementationException("Could not instantiate target class", e);
            } catch (IllegalAccessException e) {
                throw new ImplementationException("Could not access target class", e);
            }
        } else {
            //For å kunne kjøre private constructorer.
            Constructor c = target.getDeclaredConstructor();
            c.setAccessible(true);
            retVal = (T) c.newInstance();
        }

        return retVal;
    }

    private <S> boolean isKode(Class target) {
        List<Class<?>> classes = InternalClassUtils.getAllSuperclasses(target);
        for (int i = 0; i < classes.size(); i++) {
            Class aSuperclass = classes.get(i);
            if(aSuperclass.getName().equals("no.statkart.matrikkel.domene.Enum")){
                return true;
            }
        }
        return false;
    }

    private boolean implementsBubbleId(Class target) {
        List<Class<?>> classes = InternalClassUtils.getAllInterfaces(target);
        for (int i = 0; i < classes.size(); i++) {
            Class anInterface = classes.get(i);
            if (anInterface.getName().equals("net.sf.spif.BubbleId")) {
                return true;
            }
        }
        return false;
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
