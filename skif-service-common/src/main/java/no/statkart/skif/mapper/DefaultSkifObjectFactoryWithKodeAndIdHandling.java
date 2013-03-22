package no.statkart.skif.mapper;


import no.statkart.skif.exception.ImplementationException;
import no.statkart.skif.store.SnapshotVersion;
import no.statkart.skif.internal.util.InternalClassUtils;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;

/**
 * ObjectFactory implementasjon som håndterer opprettelse av objekter med rot i Skif.
 * Target-objektene kan være no.statkart.skif.store.BubbleId, da kopieres id-verdien fra source-objektet.
 * target-objektene kan også være koder, da opprettes KodeId-objekter, med samme verdi som koden den kommer fra, mens SnapshotVersion settes til Current.
 *
 * @author Steinar Hansen
 */
public class DefaultSkifObjectFactoryWithKodeAndIdHandling implements ObjectFactory {
    @Override
    public <S, T> T getInitialObject(S source, Class<T> target) throws InstantiationException, IllegalAccessException, NoSuchMethodException, InvocationTargetException {
        T retVal = null;
        if (isKode(target)) {
            //Kode til KodeId
            Long o = null;
            if (isSourceKode(source)) {
                Method getIdAsKodeIdMethod = source.getClass().getMethod("getIdAsKodeId");
                Object kodeId = getIdAsKodeIdMethod.invoke(source);
                Method getValue = kodeId.getClass().getMethod("getIdValue");
                o = (Long) getValue.invoke(kodeId);
            } else if (isSourceEnum(source)) {
                Method getKodeMethod = source.getClass().getMethod("getKode");
                Integer myInt = (Integer) getKodeMethod.invoke(source);
                o =  new Long(myInt.longValue());
            } else{
                o = new Long(0);
            }
            Constructor constructor = target.getDeclaredConstructor(Long.class, SnapshotVersion.class);
            constructor.setAccessible(true);
            retVal = (T) constructor.newInstance(o.longValue(), SnapshotVersion.CURRENT);
        } else if (implementsBubbleId(target)) {
            try {
                List<Class<?>> targetInterfaces = InternalClassUtils.getAllInterfaces(target);
                for (int i = 0; i < targetInterfaces.size(); i++) {
                    Class aTargetInterface = targetInterfaces.get(i);
                    if (aTargetInterface.getName().equals("no.statkart.skif.store.BubbleId")) {
                        Method getIdMethodOnSource = source.getClass().getMethod("getValue");
                        Constructor constructor = target.getDeclaredConstructor(Long.class);
                        constructor.setAccessible(true);
                        retVal = (T) constructor.newInstance(getIdMethodOnSource.invoke(source));
                        break;
                    }
                }

            } catch (NoSuchMethodException e) {
                throw new ImplementationException("Fant ikke konstruktøren", e);
            } catch (InvocationTargetException e) {
                throw new ImplementationException(e);
            } catch (InstantiationException e) {
                throw new ImplementationException(e);
            } catch (IllegalAccessException e) {
                throw new ImplementationException(e);
            }
        } else {
            retVal = target.newInstance();
        }

        return retVal;
    }

    private <S> boolean isSourceEnum(S source) {
        try {
            Class enumClass = Class.forName("no.statkart.matrikkel.domene.Enum");
            if(InternalClassUtils.isAssignable(source.getClass(), enumClass)){
                return true;
            }
        } catch (ClassNotFoundException e) {
            //Dette kan skje, og vi skal ikke feile av den grunn, bare returnere false;
        }
        return false;
    }

    private <S> boolean isSourceKode(S source) {
        try {
            Class kodeClass = Class.forName("no.statkart.matrikkel.domene.Kode");
            if(InternalClassUtils.isAssignable(source.getClass(), kodeClass)){
                return true;
            }
        } catch (ClassNotFoundException e) {
            //Dette kan skje, og vi skal ikke feile av den grunn, bare returnere false;
        }
        return false;
    }

    private <S> boolean isKode(Class target) {
        List<Class<?>> classes = InternalClassUtils.getAllSuperclasses(target);
        for (int i = 0; i < classes.size(); i++) {
            Class aSuperclass = classes.get(i);
            if (aSuperclass.getName().equals("no.statkart.skif.store.kodeliste.KodeId")) {
                return true;
            }
        }
        return false;
    }

    private boolean implementsBubbleId(Class target) {
        List<Class<?>> classes = InternalClassUtils.getAllInterfaces(target);
        for (int i = 0; i < classes.size(); i++) {
            Class anInterface = classes.get(i);
            if (anInterface.getName().equals("no.statkart.skif.store.BubbleId")) {
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
