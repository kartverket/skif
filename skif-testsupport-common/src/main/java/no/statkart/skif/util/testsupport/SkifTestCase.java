package no.statkart.skif.util.testsupport;

import com.google.inject.Injector;
import com.google.inject.Module;
import no.statkart.skif.SkifModule;
import no.statkart.skif.config.Configuration;
import no.statkart.skif.config.SystemConfiguration;
import no.statkart.skif.module.ModuleBuilder;
import no.statkart.skif.service.LoginUser;
import no.statkart.skif.service.LoginUserHolder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.Assert;
import org.testng.ITestContext;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Collection;

/**
 * Testcase klasse for skif baserte TestNG tester. Det overordnede ønske med denne klassen er å
 * gjøre det mulig å få tester til å kjøre raskt ved at tung initialisering fortrinsvis skjer en gang per testsuite
 * eller testklasse og ikke for hver testmetode. Samtidig skal det være enkelt å skifte mellom å kjøre testene i
 * JEE og SINGLE_VM mode ved f.eks å endre en parameter i en ekstern konfigurasjonsfil. Endring av en slik ekstern
 * parameter skal kun påvirke de tester som der designet til valgfritt å kunne kjøres i begge modes. Tester som krever
 * bestemt mode eller eksplisitt tester alle modes skal ikke påvirkes av en slik ekstern parameter. Det skal være mulig
 * selektivt å gjøre tester som kun tilhører en bestemt grupper eller ekskluderer tester som tilhører en bestemt gruppe.
 * <p/>
 * Videre skal støtte i denne klassen være slik at der er enkelt å kjøre testene fra både fra byggeverktøy (Ant og Maaven)
 * og fra IntelliJ. Spesielt skal det være mulig å velge en enkelt testmetode eller testklasse i IntelliJ, høyre-klikke
 * og kjøre denne. TestNG støtten i IntelliJ (9 og 10) er slik at ikke alle TestNG annotasjoner gir ønsket oppførsel i IntelliJ.
 * Spesielt bør man unngå å bruke dependsOnGroups i stor stil (i noen få tilfeller kan det men hensikt anvendes mellom
 * testklasser i samme pakke). Annotasjonen dependsOnMethods bør kun brukes innenfor samme testklasse og gjør at man
 * ikke lengere kan kjøre metodene i testen enkeltvis fra IntelliJ - hvilket er uheldig i de fleste tilfeller.
 * <p/>
 * Tanken var opprindelig å bruke  {@code @BeforeSuite} til å gjøre all nødvendig tung initialisering, men det virket dårlig fordi
 * {@code @BeforeSuite} ikke blir kjørt når man bruker grupper ( {@code @BeforeSuite} blir ikke med i grupper som
 * subklassen tilhører) og dermed blir  {@code @BeforeSuite} ikke alltid kjørt når man bruke grupper. Hvis man bruker
 *  {@code alwaysRun} kan det også fører til at denne koden  bli kjørt flere ganger. Det vi ønsker er at
 *  "BeforeSuite-metoden" alltid blir kjørt nettopp en gang hvis en av testene i en av subklassene kjøres.
 *  Dette skal skje uavhengig av hvordan testene blir utvalgt. Det er ikke mulig å få til med {@code @BeforeSuite} annotasjonen.
 * Derfor blir {@code @BeforeClass} og {@code alwaysRun} brukt i stedet. Man må da teste på en statisk variable
 * eller lignende slik at initialiseringen ikke skjer flere ganger.
 * <p/>
 * Det er ønskelig å kunne skjeldne mellom release- og unit-tester da disse går mot forskjellige typer databaser. Unit-
 * tester ikke må få lov å ødelegge releasetestdatabasen da denne kan ta lang tid å gjenetablere.
 * For release-tester er det også ønskelig å kunne skjeldne mellom read- og write-tester siden write-tester krever at
 * man gjøre en database-flashback for rask tilbakestilling mellom hver kjøring. Enkleste løsning her er å ha
 * forskjellige brukere for hver database type og evt en liten sjekk som får unit tester til å stoppe hvis de
 * forsøker å skrive data til en releasetest database.  Denne basisklassen har ikke noen eksplisitt støtte for dette
 * konseptet. Det kan legges på i en subklasse.
 * <p/>
 * Klassen har avansert støtte for å gjenbruke konfigurasjon på tvers av testcaser og testmetoder slik at initialisering
 * av tunge ressurser kan reduseres. Default er følgende:
 * <ul>
 * <li>Injector gjenbrukes på tvers av testmetoder innenfor samme klasse
 * <li>SingleVm client-server oppsett gjenbruker serverinjectoren på tvers av alle testcases
 * <li>Testcaser som bruker forskjellige konfigurasjoner gjenbruker ikke hverandres injector
 * <li>Testcaser som anvender standard innstilling kan konfigureres til enten å kjøre i SingleVm mode eller mot remote server
 * </ul>
 * Det er mulig å endre oppførslen slik at tester alltid kjører med SingleVm=true eller false. Det er også mulig å angi
 * at hver enkelt testmetode skal ha sin egen injector eller at testcasen ikke skal dele konfigurasjon med andre testcases.
 * <p/>
 * Klassen støtter automatisk member injection av via @Inject slik testcasens membervariable er satt før test metoden
 * kalles. Member variablene sette hvergang klasse skifter injector.
 * <p/>
 * Klassen har en tom {@link #resetLogin()}-metode som kalles før hver testmetode.
 *
 * @author Henrik Fredholm
 * @since 0.5
 */
@Test
public class SkifTestCase {
    static final Logger logger = LoggerFactory.getLogger(SkifTestCase.class);
    protected ModuleBuilder moduleBuilder;
    protected Injector injector;


    /**
     * Angir om injector skal gjenanvendes på tvers av testmetoder innenfor samme testcase.
     * Default er true.
     */
    protected boolean resuseInjector() {
        return true;
    }

    protected Class<? extends Module> getModuleClass() {
        return null;
    }

    protected String getModuleClassname() {
        final Class<? extends Module> moduleClass = getModuleClass();
        if (moduleClass!=null) {
            return moduleClass.getName();
        } else {
            return null;
        }
    }


    protected String getConfigurationFilename() {
        return null;
    }

    protected Class<? extends Module> getSingleVmServerModuleClass() {
        return null;
    }

    protected String getSingleVmServerModuleClassname() {
        final Class<? extends Module> moduleClass = getSingleVmServerModuleClass();
        if (moduleClass!=null) {
            return moduleClass.getName();
        } else {
            return null;
        }
    }

    /**
     * Angir SingleVm ServerConfigurasjonsklasse. Tester som ikke setter denne kjører alltid med SingleVm=false
     */
    protected String getSingleVmServerConfigurationFilename() {
        return null;

    }

    /**
     * Angir om testcasen skal kjøres i SingleVm mode. Følgende verdier kan returneres
     * av metoden:
     * <ul>
     * <li>true: Testen avvikles alltid i SingleVm mode.
     * <li>false: Testen avvikles aldri i Singlevm mode.
     * <li>null (default): Verdien styres fra konfigurasjonsfil eller system properties.
     * </ul>
     * Dersom SingleVm mode er satt men ingen SingleVmServerModul eller configurasjon er spesifisert har SingleVm settingen
     * ingen betydning. Dvs moduler som ikke bruker SingelVm berøres ikke
     */
    protected Boolean singleVm() {
        return null;
    }

    /**
     * Angir filnavn på properties for konfigurasjonen. Default er "skif.properties". Hvis filen ikke
     * finne brukes et tomt properties sett.
     *
     * @return
     */
    protected String getSkifPropertiesFilename() {
        return "skif.properties";
    }

    /**
     * Angir om en custom SkifConfigurationBuilder som ikke kan gjenanvendes på tvers av testcases skal brukes.
     * Default implementatsjonen returnerer null, hvilket angir at testcasen kan bruke en reusable configuration
     *
     * @return SkifConfigurationBuilder dersom non reusable configuration skal brukes
     */
    protected ModuleBuilder createModuleBuilder() {
        return null;
    }


    /**
     * Utfører initialisering av Skif konfigurasjon. TestNG krever at den ikke er private.
     *
     * @param context
     */
    @BeforeClass(alwaysRun = true)
    protected final void beforeClass(ITestContext context) {
        moduleBuilder = getModuleBuilder(context);
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


    protected final ModuleBuilder createReusableModuleBuilder() {
        ModuleBuilder builder = new ModuleBuilder(new SystemConfiguration());

        String moduleClassname = getModuleClassname();
        if (moduleClassname!=null) {
            builder.setModuleClassname(moduleClassname);
        }

        String configurationFilename = getConfigurationFilename();
        if (configurationFilename!=null) {
            builder.setConfigurationFilename(configurationFilename);
        }

        String singleVmServerModuleClassname = getSingleVmServerModuleClassname();
        if (singleVmServerModuleClassname!=null) {
            builder.setSingleVmServerModuleClassname(singleVmServerModuleClassname);
        }

        String singleVmServerConfigurationFilename = getSingleVmServerConfigurationFilename();
        if (singleVmServerConfigurationFilename!=null) {
            builder.setSingleVmServerConfigurationFilename(singleVmServerConfigurationFilename);
        }


        if (singleVm() != null) {
            builder.setSingleVm(singleVm());
        }
        return builder;
    }


    /**
     * Beregner konfigurasjonsnøkkel for testcase på basis av hvilke konfigurasjonsklasser testcasen bruker.
     */
    protected final String calcConfigurationKey() {
        return getModuleClassname() + ":" + ":" + getConfigurationFilename() + ":" + getSingleVmServerModuleClassname() + ":" +  getSingleVmServerConfigurationFilename()+ ":" +singleVm();
    }

    /**
     * Setter injector for første testmetode kalles, og før hver testmetode hvis {@link #resuseInjector()} returnerer
     * false. TestNG krever at den ikke er private.
     *
     * @param context leveres at TestNG rammeverket og holder state på tvers av testcases
     */
    @BeforeMethod(alwaysRun = true)
    protected final void beforeMethod(ITestContext context) {
        if (injector == null || !resuseInjector()) {
            injector = moduleBuilder.buildInjector();
            injector.injectMembers(this);
        }
        resetLogin();
    }

    /**
     * Kalles før hver testmetode og bør brukes til å nullstille pålogget bruker
     */
    protected void resetLogin() {
        Configuration configuration = injector.getInstance(Configuration.class);
        LoginUserHolder userHolder = injector.getInstance(LoginUserHolder.class);
        String username = configuration.getString("username");
        String password = configuration.getString("password");
        LoginUser user = new LoginUser(username, password);
        userHolder.set(user);
    }

    /**
     * Sammenligner to objekter ved å bryte ned deres komponenter til primitive og comparable objekter.
     *
     * @param o   fasit-objekt
     * @param o2  objekt vi vil sammenligne mot o
     * @param <T> Klasse for objektene o og o2
     */
    protected <T extends Object> void compareWithAsserts(T o, T o2) {
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
