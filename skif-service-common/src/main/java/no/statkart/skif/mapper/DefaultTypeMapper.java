package no.statkart.skif.mapper;

import no.statkart.skif.exception.ImplementationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;


/**
 * <p>Denne klassen forsøker å mappe to typer mellom hverandre ved å basere seg på to antagelser:
 * <ul>
 * <li>Klassenavnene er like, klassene ligger bare i forskjellige pakker</li>
 * <li>Feltnavnene, merk feltnavnene, ikke accessormetodene, er like i klassene</li>
 * </ul>
 * </p>
 * <p/>
 * <p>For å benytte klassen så setter man inn denne med {@link AbstractMapper#setDefaultMapper(TypeMapper)}.
 * For å benytte DefaultTypeMapper må også en packageMapping legges inn. Ved bruk av addPackageMapping er det mulig å
 * mappe alle klasser i en pakke til klasser i en annen pakke med samme navn.
 * Man trenger ikke å legge inn mappinger begge veier.</p>
 * <p/>
 * <p>F.eks.
 * <blockquote><pre>
 * DefaultTypeMapper typeMapper = new DefaultTypeMapper();
 * typeMapper.addPackageMapping("no.statkart.grunnbok.borett.info.wsapi.domain", "no.statkart.grunnbok.borett.info.domain");
 * setDefaultTypeMapper(typeMapper);
 * </pre></blockquote></p>
 * <p/>
 * <p>Hensikten med defaultmapperen er at den skal benyttes ved "defaulting" som i en switch-statement. Dersom ingen annen typemapping
 * finnes så faller typemappingen tilbake til denne.</p>
 * <p/>
 * <p>Denne klassen har en spesiell håndtering av typer som er ListIterable i den ene klassen og Collections i den andre.
 * Slike klasser forventes å ha et felt med navn "item" som da får satt verdien til en ArrayList, og deretter blir innholdet
 * av denne ArrayListen satt til innholdet av den andre klassens Collection, med mapping av elementene, naturligvis.</p>
 * <p/>
 * <p>Det er også mulig å benytte mapperen som en superklasse for implementasjon av
 * type-mappere på lik linje med f.eks. GrunnbokBorettInfoTypeMapper. Da vil default type mapperen håndtere felter med samme
 * navn, slik at man bare trenger å håndtere de spesielle feltene i subklassen. I disse tilfellen så legger man til den nye
 * type-mapper subklassen ved bruk av AbstractMapper sin addMapper-metode, som vanlig.</p>
 * <p/>
 * <p/>
 * <p>F.eks.
 * <blockquote><pre>
 * public class SpesiellTypeMapper extends DefaultTypeMapper<Beloep, no.statkart.grunnbok.borett.info.domain.Beloep> {
 * <p/>
 *   public SpesiellTypeMapper() {
 *       super(Beloep.class, no.statkart.grunnbok.borett.info.domain.Beloep.class);
 *   }
 * <p/>
 *   public void mapDomainObject(no.statkart.grunnbok.borett.info.domain.Beloep source, Beloep target) {
 *       super.mapDomainObject(source, target);//setter f.eks. verdiene i 3 felter som har samme feltnavn i begge klassene
 *       target.setAnnenVerdi(mapping.w2d(source.getVerdi());
 *   }
 * <p/>
 *   public void mapWsapiObject(Beloep source, no.statkart.grunnbok.borett.info.domain.Beloep target) {
 *       super.mapWsapiObject(source, target);//ditto
 *       target.setVerdi(mapping.d2w(source.getAnnenVerdi());
 *   }
 * }
 * </pre></blockquote></p>
 *
 * @author Steinar Hansen
 */
public class DefaultTypeMapper<WsapiT, DomainT> implements TypeMapper<WsapiT, DomainT> {
    private Logger logger = LoggerFactory.getLogger(DefaultTypeMapper.class);

    private ObjectFactory domainObjectFactory;
    private ObjectFactory wsapiObjectFactory;

    Mapping mapping;

    Map<String, String> wsapiPkg2domainPkg = new HashMap<String, String>();
    Map<String, String> domainPkg2wsapiPkg = new HashMap<String, String>();
    private Class<WsapiT> wsapiClass;
    private Class<DomainT> domainClass;

    public DefaultTypeMapper() {
    }

    public void addPackageMapping(String wsapiPackage, String domainPackage) {
        if (wsapiPackage == null || domainPackage == null) {
            return;
        }
        if (!wsapiPackage.endsWith(".")) {
            wsapiPackage += ".";
        }
        if (!domainPackage.endsWith(".")) {
            domainPackage += ".";
        }
        this.wsapiPkg2domainPkg.put(wsapiPackage, domainPackage);
        this.domainPkg2wsapiPkg.put(domainPackage, wsapiPackage);
    }

    protected DefaultTypeMapper(Class<WsapiT> wsapiClass, Class<DomainT> domainClass) {
        this.wsapiClass = wsapiClass;
        this.domainClass = domainClass;
    }


    public Mapping getMapping() {
        return mapping;
    }

    public void setMapping(Mapping mapping) {
        this.mapping = mapping;
    }

    @Override
    public Class<WsapiT> getWsapiClass() {
        return wsapiClass;
    }

    @Override
    public Class<DomainT> getDomainClass() {
        return domainClass;
    }

    @Override
    public ObjectFactory getWsapiObjectFactory() {
        return wsapiObjectFactory;
    }

    @Override
    public void setWsapiObjectFactory(ObjectFactory factory) {
        this.wsapiObjectFactory = factory;
    }


    @Override
    public ObjectFactory getDomainObjectFactory() {
        return domainObjectFactory;
    }

    @Override
    public void setDomainObjectFactory(ObjectFactory factory) {
        this.domainObjectFactory = factory;
    }

    private String findPackage(String packageString) {
        packageString += ".";
        if (this.wsapiPkg2domainPkg.containsKey(packageString)) {
            return wsapiPkg2domainPkg.get(packageString);
        } else if (this.domainPkg2wsapiPkg.containsKey(packageString)) {
            return domainPkg2wsapiPkg.get(packageString);
        } else {
            return null;
        }

    }

    private Class findTargetClassFromSourceClass(Class sourceClass) throws ClassNotFoundException, NoSuchFieldException {
        Package aPackage = sourceClass.getPackage();
        if (aPackage != null) {
            String sourceClassPackage = aPackage.getName();
            String targetPackage = findPackage(sourceClassPackage);
            if (targetPackage != null) {
                //Hvis kildeklassen har et felt som heter 'item' så er dette en collection-klasse.
                if (checkHasField(sourceClass, "item")) {
                    return Class.forName("java.util.ArrayList");
                //Eller hvis kildeklassen har et felt som heter 'liste' så er dette en collection-klasse.
                }else if (checkHasField(sourceClass, "liste")) {
                    return Class.forName("java.util.ArrayList");
                } else {
                    return Class.forName(targetPackage + sourceClass.getSimpleName());
                }
            } else {
                throw new MappingException("Kunne ikke mappe: " + sourceClass.toString() + ", fant ingen pakke-mapping for pakke: " + aPackage.toString());
            }
        } else {
            throw new ImplementationException("Kunne ikke mappe: " + sourceClass.toString() + ", fant ingen pakke ( package=null ) for denne klassen.");
        }
    }


    @Override
    public final WsapiT mapDomainObject(DomainT source) {
        WsapiT target = null;
        try {
            target = getInitialWsapiObject(source);
        } catch (Exception e) {
            logger.error("Feilet under oppretting av target objekt med kildetype: "+source.getClass().getName(), e);
            throw new MappingException(e);
        }
        mapDomainObject(source, target);
        return target;
    }


    @Override
    public final DomainT mapWsapiObject(WsapiT source) {
        DomainT target = null;
        try {
            target = getInitialDomainObject(source);
        } catch (Exception e) {
            logger.error("Feilet under oppretting av target objekt med kildetype: "+source.getClass().getName(), e);
            throw new MappingException(e);
        }
        mapWsapiObject(source, target);
        return target;
    }

    protected WsapiT getInitialWsapiObject(DomainT source) throws NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException, ClassNotFoundException, NoSuchFieldException {
        WsapiT target = null;
        if (getWsapiClass() != null) {
            target = wsapiObjectFactory.getInitialObject(source, getWsapiClass());
        }
        if (target == null) {
            target = (WsapiT) wsapiObjectFactory.getInitialObject(source, findTargetClassFromSourceClass(source.getClass()));
        }
        return target;
    }

    protected DomainT getInitialDomainObject(WsapiT source) throws NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException, ClassNotFoundException, NoSuchFieldException {
        DomainT target = null;
        if (getDomainClass() != null) {
            target = domainObjectFactory.getInitialObject(source, getDomainClass());
        }
        if (target == null) {
            target = (DomainT) domainObjectFactory.getInitialObject(source, findTargetClassFromSourceClass(source.getClass()));
        }
        return target;
    }

    @Override
    public void mapDomainObject(DomainT source, WsapiT target) {
        try {
            mapCommonDomainFields(source, target);
        } catch (ClassNotFoundException e) {
            throw new MappingException(e);
        }
    }

    @Override
    public void mapWsapiObject(WsapiT source, DomainT target) {
        try {
            mapCommonWsapiFields(source, target);
        } catch (ClassNotFoundException e) {
            throw new MappingException(e);
        } catch (NoSuchFieldException e) {
            throw new MappingException(e);
        } catch (IllegalAccessException e) {
            throw new MappingException(e);
        }
    }

    protected void mapCommonDomainFields(DomainT source, WsapiT target) throws ClassNotFoundException {
        try {
            if (source instanceof Collection) {
                if (checkHasField(target.getClass(), "item")) {
                    Field targetField = target.getClass().getDeclaredField("item");
                    ArrayList value = new ArrayList();
                    for (Iterator iterator = ((Collection) source).iterator(); iterator.hasNext();) {
                        Object next = iterator.next();
                        value.add(mapping.d2w(next));
                    }
                    targetField.setAccessible(true);
                    targetField.set(target, value);
                } else if (checkHasField(target.getClass(), "liste")) {
                    Field targetField = target.getClass().getDeclaredField("liste");
                    ArrayList value = new ArrayList();
                    for (Iterator iterator = ((Collection) source).iterator(); iterator.hasNext();) {
                        Object next = iterator.next();
                        value.add(mapping.d2w(next));
                    }
                    targetField.setAccessible(true);
                    targetField.set(target, value);
                }else {
                    throw new MappingException("Antar at det alltid er en felt med navn 'item' eller 'liste' på andre siden av en Collection. Det var visst feil...");
                }
            } else {
                List<Field> sourceFields = new ArrayList<Field>();
                addDeclaredAndInheritedFields(source.getClass(), sourceFields);
                for (int i = 0; i < sourceFields.size(); i++) {
                    Field sourceField = sourceFields.get(i);
                    Field targetField = getFieldWithInheritedFields(target.getClass(), sourceField.getName());
                    if (targetField != null) {
                        targetField.setAccessible(true);
                        sourceField.setAccessible(true);

                        Object source1 = sourceField.get(source);
                        if (source1 instanceof Collection) {
                            //Må bruke accessor-metode for å få sortering riktig.
                            Method method = source.getClass().getMethod("get"+sourceField.getName().substring(0,1).toUpperCase()+sourceField.getName().substring(1,sourceField.getName().length()), (Class<?>[])null);
                            Object sortedSource = method.invoke(source);
                            targetField.set(target, mapping.d2w(sortedSource, targetField.getType()));
                        } else {
                            targetField.set(target, mapping.d2w(source1));
                        }
                    } else {
                        //Kan ikke feile dersom vi ikke finner et felt, da vil ikke subklasser kunne fungere.
                        if(logger.isDebugEnabled())
                            logger.debug("Ignorer feltet: " + sourceField.getName() + ", siden jeg ikke fant et tilsvarende felt i target-klasse");
                    }
                }
            }
        } catch (IllegalAccessException e) {
            throw new MappingException(e);
        } catch (NoSuchFieldException e) {
            throw new MappingException(e);
        } catch (NoSuchMethodException e) {
            throw new MappingException(e);
        } catch (InvocationTargetException e) {
            throw new MappingException(e);
        }
    }

    protected void mapCommonWsapiFields(WsapiT source, DomainT target) throws ClassNotFoundException, NoSuchFieldException, IllegalAccessException {
        try {
            if (checkHasField(source.getClass(), "item")) {
                if (target instanceof Collection) {
                    Collection targetCollection = (Collection) target;

                    Field item = source.getClass().getDeclaredField("item");
                    item.setAccessible(true);
                    Iterator iterator = ((Iterable) item.get(source)).iterator();
                    while (iterator.hasNext()) {
                        Object next = iterator.next();
                        targetCollection.add(mapping.w2d(next));
                    }
                } else {
                    throw new MappingException("Antar at det alltid er en List på den andre siden av en wsapi klasse som har et felt med navn 'item'. Det var visst feil...");
                }
            }else if (checkHasField(source.getClass(), "liste")) {
                if (target instanceof Collection) {
                    Collection targetCollection = (Collection) target;

                    Field item = source.getClass().getDeclaredField("liste");
                    item.setAccessible(true);
                    Iterator iterator = ((Iterable) item.get(source)).iterator();
                    while (iterator.hasNext()) {
                        Object next = iterator.next();
                        targetCollection.add(mapping.w2d(next));
                    }
                } else {
                    throw new MappingException("Antar at det alltid er en List på den andre siden av en wsapi klasse som har et felt med navn 'liste'. Det var visst feil...");
                }
            } else {
                List<Field> sourceFields = new ArrayList<Field>();
                addDeclaredAndInheritedFields(source.getClass(), sourceFields);
                for (int i = 0; i < sourceFields.size(); i++) {
                    Field sourceField = sourceFields.get(i);
                    Field targetField = getFieldWithInheritedFields(target.getClass(), sourceField.getName());
                    if (targetField != null) {
                        targetField.setAccessible(true);
                        sourceField.setAccessible(true);
                        Object source1 = sourceField.get(source);
                        if (source1 instanceof Collection) {
                            targetField.set(target, mapping.w2d(source1, targetField.getType()));
                        } else {
                            targetField.set(target, mapping.w2d(source1));
                        }
                    } else {
                        //Kan ikke feile dersom vi ikke finner et felt, da vil ikke subklasser kunne fungere.
                        if(logger.isDebugEnabled())
                            logger.debug("Ignorer feltet: " + sourceField.getName() + ", siden jeg ikke fant et tilsvarende felt i target-klasse");
                    }
                }
            }
        } catch (IllegalAccessException e) {
            throw new MappingException(e);
        }
    }

    private static void addDeclaredAndInheritedFields(Class<?> c, Collection<Field> fields) {
        fields.addAll(Arrays.asList(c.getDeclaredFields()));
        Class<?> superClass = c.getSuperclass();
        if (superClass != null) {
            addDeclaredAndInheritedFields(superClass, fields);
        }
    }

    private Field getFieldWithInheritedFields(Class<?> c, String fieldname) {
        Collection<Field> fields = new ArrayList<Field>();
        addDeclaredAndInheritedFields(c, fields);
        for (Iterator<Field> iterator = fields.iterator(); iterator.hasNext();) {
            Field next = iterator.next();
            if (next.getName().equals(fieldname)) {
                return next;
            }
        }
        return null;
    }

    private boolean checkHasField(Class clazz, String fieldname){
        try {
            clazz.getDeclaredField(fieldname);
            return true;
        } catch (NoSuchFieldException e) {
            return false;
        }
    }

}