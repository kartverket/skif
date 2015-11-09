package no.statkart.skif.util.testsupport;

import com.google.inject.Injector;
import com.google.inject.Module;
import com.google.common.base.Preconditions;
import no.statkart.skif.module.ModuleBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.Assert;
import org.testng.ITestContext;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.Collection;

/**
 * @author Henrik Fredholm
 * @since 2.1
 */
public abstract class AbstractSkifTestCase {
    static final Logger logger = LoggerFactory.getLogger(SkifTestCase.class);
    private ModuleBuilder moduleBuilder;
    protected Boolean singleVm;
    private Class<? extends Module> moduleClass;
    private Class<? extends Module> singleVmServerModuleClass;
    private String[] configurationFilenames = null;
    private String[] singleVmServerConfigurationFilenames = null;

    protected Injector injector;

    public ModuleBuilder getModuleBuilder() {
        return moduleBuilder;
    }

    /**
     * Angir om testcasen skal kjøres i SingleVm mode. Følgende verdier kan returneres
     * av metoden:
     * <ul>
     * <li>true: Testen avvikles alltid i SingleVm mode.
     * <li>false: Testen avvikles aldri i Singlevm mode.
     * <li>null (default): Verdien styres fra konfigurasjonsfil eller system properties. Hvis ikke satt brukes false.
     * </ul>
     * Dersom SingleVm mode er satt men ingen SingleVmServerModule er spesifisert har SingleVm settingen
     * ingen betydning. Dvs moduler som ikke bruker SingelVm berøres ikke av hva verdien er satt til.
     */
    protected Boolean isSingleVm() {
        return singleVm;
    }

    protected Class<? extends Module> getModuleClass() {
        return moduleClass;
    }

    protected void setModuleClass(Class<? extends Module> moduleClass) {
        checkModuleBuilderNotCreated();
        this.moduleClass = moduleClass;
    }

    protected String getModuleClassname() {
        return getModuleClass().getName();
    }

    protected Class<? extends Module> getSingleVmServerModuleClass() {
        return singleVmServerModuleClass;
    }

    protected void setSingleVmServerModuleClass(Class<? extends Module> singleVmServerModuleClass) {
        checkModuleBuilderNotCreated();
        this.singleVmServerModuleClass = singleVmServerModuleClass;
    }

    protected String getSingleVmServerModuleClassname() {
        final Class<? extends Module> moduleClass = getSingleVmServerModuleClass();
        if (moduleClass!=null) {
            return moduleClass.getName();
        } else {
            return null;
        }
    }

    protected String[] getConfigurationFilenames() {
        return configurationFilenames;
    }

    protected void setConfigurationFilenames(String[] configurationFilenames) {
        checkModuleBuilderNotCreated();
        this.configurationFilenames = configurationFilenames;
    }


    /**
     * Angir SingleVm ServerConfigurasjonsklasse. Denne må være satt, enten direkte eller indirekte, for at
     * en SingleVm server skal kunne opprettes
     */
    protected String[] getSingleVmServerConfigurationFilenames() {
        return singleVmServerConfigurationFilenames;

    }

    protected void setSingleVmServerConfigurationFilenames(String[] singleVmServerConfigurationFilenames) {
        checkModuleBuilderNotCreated();
        this.singleVmServerConfigurationFilenames = singleVmServerConfigurationFilenames;
    }


    /**
     * Angir om injector skal gjenanvendes på tvers av testmetoder innenfor samme testcase.
     * Default er true.
     */
    protected boolean reuseInjector = true;

    protected boolean isReuseInjector() {
        return reuseInjector;
    }

    protected void setReuseInjector(boolean reuseInjector) {
        checkModuleBuilderNotCreated();
        this.reuseInjector = reuseInjector;
    }

    protected void checkModuleBuilderNotCreated() {
        Preconditions.checkState(moduleBuilder == null, "Kan ikke endre oppsett, ModuleBuilder er allerede opprettet");
    }

    /**
     * Angir om en custom ModuleBuilder skal brukes. Custom ModuleBuilders ikke gjenbrukes på tvers av testcaser.
     * Default implementatsjonen returnerer null, hvilket angir at testcasen skal bruke en reusable ModuleBuilder
     *
     * @return SkifConfigurationBuilder dersom non reusable configuration skal brukes
     */
    protected ModuleBuilder createModuleBuilder() {
        checkModuleBuilderNotCreated();
        return null;
    }

    protected abstract ModuleBuilder createReusableModuleBuilder();

    /**
     * Utfører initialisering av Skif konfigurasjon. TestNG krever at den ikke er private.
     *
     * @param context
     */
    @BeforeClass(alwaysRun = true)
    protected void beforeClass(ITestContext context) {
        checkModuleBuilderNotCreated();
        moduleBuilder = getModuleBuilder(context);
    }

    /**
     * Blanker ut alle felter slik at testklassen ikke holder på mye tilstand etter at testene er kjørt.
     */
    @AfterClass
    protected void afterClass() throws IllegalAccessException {
        injector = null;
        for (Class c = getClass(); !c.equals(AbstractSkifTestCase.class); c = c.getSuperclass()) {
            for (Field field : c.getDeclaredFields()) {
                if ((field.getModifiers() & (Modifier.FINAL | Modifier.STATIC)) == 0 && !field.getType().isPrimitive()) {
                    field.setAccessible(true);
                    field.set(this, null);
                }
            }
        }
    }

    private final ModuleBuilder getModuleBuilder(ITestContext context) {
        ModuleBuilder moduleBuilder = createModuleBuilder();
        if (moduleBuilder == null) {
            // Bruk builder hvis den finnes fra før, elles opprett en
            String key = calcConfigurationKey();
            String keyCreatingClass = key + ":creatingClass";
            moduleBuilder = (ModuleBuilder) context.getAttribute(key);
            if (moduleBuilder == null) {
                logger.debug("Oppretter gjenbrukbar ModuleBuilder for " + getClass().getName());
                moduleBuilder = createReusableModuleBuilder();
                context.setAttribute(key, moduleBuilder);
                context.setAttribute(keyCreatingClass, getClass().getName());
            } else {
                String classname = (String) context.getAttribute(keyCreatingClass);
                logger.debug("Gjenbruker ModuleBuilder for " + getClass().getName() + " opprettet av " + classname);
                context.setAttribute(key, moduleBuilder);
                context.setAttribute(keyCreatingClass, getClass().getName());
            }
        } else {
            logger.debug("Anvender en ikke gjenbrukbar ModuleBuilder for " + getClass().getName());
        }
        return moduleBuilder;
    }

    /**
     * Beregner konfigurasjonsnøkkel for testcase på basis av hvilke konfigurasjonsklasser testcasen bruker.
     */
    protected String calcConfigurationKey() {
        return getModuleClassname() + ":" + Arrays.toString(getConfigurationFilenames()) + ":" + getSingleVmServerModuleClassname() + ":" +  Arrays.toString(getSingleVmServerConfigurationFilenames()) + ":" + isSingleVm();
    }

    /**
     * Sammenligner to objekter ved å bryte ned deres komponenter til primitive og comparable objekter.
     *
     * @param o   fasit-objekt
     * @param o2  objekt vi vil sammenligne mot o
     * @param <T> Klasse for objektene o og o2
     */
    protected <T> void compareWithAsserts(T o, T o2) {
        Method[] methods = o.getClass().getMethods();

        for (Method method : methods) {
            if (method.getName().startsWith("get") && method.getReturnType() != Class.class && method.getParameterTypes().length == 0) {
                try {
                    Object sub1 = method.invoke(o);
                    Object sub2 = method.invoke(o2);

                    if (sub1 == null) {
                        if (sub2 != null) {
                            Assert.fail("Originalt objekt er null, mens mappet objekter er ikke null! Objekttype: " + sub2.getClass().getName() + " metode som ble testet: " + method.getName());
                        }
                    } else if (sub2 == null) {
                        Assert.fail("Mappet objekt er null, mens originalt objekter ikke null! Objekttype: " + sub1.getClass().getName() + " metode som ble testet: " + method.getName());
                    } else if (sub1.getClass().isPrimitive() || sub1 instanceof Comparable) {
                        Assert.assertEquals(sub1, sub2, "Objekter som ikke er like: " + sub1.getClass().getSimpleName() + " navn på funksjon: " + method.getName());
                    } else if (sub1 instanceof Collection) {
                        Collection subColl1 = (Collection) sub1;
                        Collection subColl2 = (Collection) sub2;
                        Assert.assertEquals(subColl1.size(), subColl2.size(), "Collections er av ulik størrelse! Navn på funksjon: " + method.getName());

                        Object[] objects1 = subColl1.toArray();
                        Object[] objects2 = subColl2.toArray();
                        for (int i = 0; i < objects1.length; i++) {
                            compareWithAsserts(objects1[i], objects2[i]);
                        }

                    } else {
                        compareWithAsserts(sub1, sub2);
                    }
                } catch (IllegalAccessException e) {
                    Assert.fail("IllegalAccessException ved kall til funksjon " + method.getName() + " på objekt av typen " + o.getClass());
                } catch (InvocationTargetException e) {
                    Assert.fail("InvocationTargetException ved kall til funksjon " + method.getName() + " på objekt av typen " + o.getClass());
                }
            }
        }
    }
}
