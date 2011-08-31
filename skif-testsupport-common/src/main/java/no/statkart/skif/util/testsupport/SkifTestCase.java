package no.statkart.skif.util.testsupport;

import com.google.inject.Injector;
import com.google.inject.Module;
import no.statkart.skif.SkifUtil;
import no.statkart.skif.config.Configuration;
import no.statkart.skif.config.ConfigurationConstants;
import no.statkart.skif.config.SystemConfiguration;
import no.statkart.skif.module.ModuleBuilder;
import no.statkart.skif.service.LoginUser;
import no.statkart.skif.service.LoginUserHolder;
import no.statkart.skif.service.ServerUrlHolder;
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
 * parameter skal kun påvirke de tester som er designet til valgfritt å kunne kjøres i begge modes. Tester som krever
 * bestemt mode eller eksplisitt tester alle modes skal ikke påvirkes av en slik ekstern parameter. Det skal være mulig
 * selektivt å kjøre tester som tilhører en bestemt testgruppe eller ekskluderer tester som tilhører en bestemt testgruppe.
 * <p/>
 * Videre skal støtte i denne klassen være slik at der er enkelt å kjøre testene fra både fra byggeverktøy (f.eks Gradle)
 * og fra IntelliJ. Spesielt skal det være mulig å velge en enkelt testmetode eller testklasse i IntelliJ, høyre-klikke
 * på metoden og kjøre denne. TestNG støtten i IntelliJ (versjon 10) er slik at ikke alle TestNG annotasjoner umiddelbart
 * gir ønsket oppførsel i IntelliJ. Spesielt bør man unngå å bruke dependsOnGroups i stor stil (i noen få tilfeller kan
 * det med hensikt anvendes mellom testklasser i samme pakke). Annotasjonen dependsOnMethods bør kun brukes innenfor
 * samme testklasse og gjør at man ikke lengere kan kjøre metodene enkeltvis fra IntelliJ og bør derfor ikke brukes
 * i stor utstrekning.
 * <p/>
 * Tanken bak designet av testklassen var opprindelig å bruke  {@code @BeforeSuite} til å gjøre all nødvendig tung
 * initialisering, men det virket dårlig fordi {@code @BeforeSuite} ikke blir kjørt når man bruker grupper
 * ( {@code @BeforeSuite} blir ikke med i grupper som subklassen tilhører) og dermed blir  {@code @BeforeSuite} ikke
 * alltid kjørt når man bruke grupper. Hvis man bruker {@code alwaysRun} kan det også fører til at denne koden  bli
 * kjørt flere ganger. Det vi ønsker er at "BeforeSuite-metoden" alltid blir kjørt nettopp en gang hvis en av testene
 * i en av subklassene kjøres. Dette skal skje uavhengig av hvordan testene blir utvalgt. Det er ikke mulig å få til
 * med {@code @BeforeSuite} annotasjonen. Derfor blir {@code @BeforeClass} og {@code alwaysRun} brukt i stedet. Man må
 * da teste på en statisk variable eller lignende slik at initialiseringen ikke skjer flere ganger.
 * <p/>
 * Det er ønskelig å kunne skjeldne mellom release- og unit-tester da disse vil gå mot forskjellige typer databaser. Unit-
 * tester må ikke få lov å ødelegge releasetestdatabasen da denne kan ta lang tid å gjenetablere.
 * For release-tester er det også ønskelig å kunne skjeldne mellom read- og write-tester siden write-tester krever at
 * man måre gjøre en database-flashback for rask tilbakestilling mellom hver kjøring. Den enkleste løsning her er å ha
 * forskjellige brukere for hver database type og evt en liten sjekk som får unit tester til å feile hvis de
 * forsøker å skrive data til en releasetest database.  Denne basisklassen har dog ikke noen eksplisitt støtte for dette
 * konseptet. Dette kan implementeres i en subklasse.
 * <p/>
 * Klassen har avansert støtte for å gjenbruke konfigurasjon på tvers av testcaser og testmetoder slik at initialisering
 * av tunge ressurser kan reduseres. Default er følgende:
 * <ul>
 * <li>Testklassens injector gjenbrukes på tvers av testmetoder innenfor samme klasse
 * <li>I SingleVm oppsett gjenbrukes samme underliggen serverinjectoren på tvers av alle testcases
 * <li>Tester som bruker samme konfigurasonsoppsett vil normalt dele injector-instans, men kan ha sin egen hvis ønskelig
 * <li>Tester som bruker forskjellig konfigurasjonsoppsett vil aldrig dele injector</li>
 * </ul>
 * Det er mulig å endre oppførslen for en test slik at den alltid kjører i SingleVm eller JEE mode. Det er også mulig å angi
 * at hver enkelt testmetode skal ha sin egen injector eller at testcasen ikke skal dele konfigurasjon med andre testcases.
 * <p/>
 * Klassen støtter automatisk member injection av via @Inject slik testcasens membervariable er satt før test metoden
 * kalles. Member variablene sette hvergang klasse skifter injector.
 * <p/>
 * Klassen har en tom {@link #resetLogin()}-metode som kalles automatisk før hver testmetode.
 *
 * @author Henrik Fredholm
 * @since 2.0
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
    protected boolean reuseInjector = true;

    private Class<? extends Module> moduleClass;

    private Class<? extends Module> singleVmServerModuleClass;

    private String configurationFilename = "skif.properties";

    private String singleVmServerConfigurationFilename = "skif.properties";

    private Boolean singleVm;

    protected boolean isReuseInjector() {
        return reuseInjector;
    }

    protected void setReuseInjector(boolean reuseInjector) {
        this.reuseInjector = reuseInjector;
    }

    protected Class<? extends Module> getModuleClass() {
        return moduleClass;
    }

    protected void setModuleClass(Class<? extends Module> moduleClass) {
        this.moduleClass = moduleClass;
    }

    protected String getModuleClassname() {
        final Class<? extends Module> moduleClass = getModuleClass();
        if (moduleClass!=null) {
            return moduleClass.getName();
        } else {
            return null;
        }
    }

    protected void setModuleClassname(String moduleClassname) {
        setModuleClass((Class<? extends Module>) SkifUtil.classForName(moduleClassname));
    }

    /**
     * Angir filnavn på properties for konfigurasjonen. Default er "skif.properties". Hvis filen ikke
     * finnes eller metoden returnerer null brukes et tomt properties sett.
     *
     * @return
     */
    protected String getConfigurationFilename() {
        return configurationFilename;
    }

    protected void setConfigurationFilename(String configurationFilename) {
        this.configurationFilename = configurationFilename;
    }

    protected Class<? extends Module> getSingleVmServerModuleClass() {
        return singleVmServerModuleClass;
    }

    protected void setSingleVmServerModuleClass(Class<? extends Module> singleVmServerModuleClass) {
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

    protected void setSingleVmServerModuleClassname(String singleVmModuleClassname) {
        setSingleVmServerModuleClass((Class<? extends Module>) SkifUtil.classForName(singleVmModuleClassname));
    }

    /**
     * Angir SingleVm ServerConfigurasjonsklasse. Denne må være satt, enten direkte eller indirekte, for at
     * en SingleVm server skal kunne opprettes
     */
    protected String getSingleVmServerConfigurationFilename() {
        return singleVmServerConfigurationFilename;

    }

    protected void setSingleVmServerConfigurationFilename(String singleVmServerConfigurationFilename) {
        this.singleVmServerConfigurationFilename = singleVmServerConfigurationFilename;
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

    protected void setSingleVm(Boolean singleVm) {
        this.singleVm = singleVm;
    }

    /**
     * Angir om en custom ModuleBuilder skal brukes. Custom ModuleBuilders ikke gjenbrukes på tvers av testcaser.
     * Default implementatsjonen returnerer null, hvilket angir at testcasen skal bruke en reusable ModuleBuilder
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


        if (isSingleVm() != null) {
            builder.setSingleVm(isSingleVm());
        }
        return builder;
    }


    /**
     * Beregner konfigurasjonsnøkkel for testcase på basis av hvilke konfigurasjonsklasser testcasen bruker.
     */
    protected final String calcConfigurationKey() {
        return getModuleClassname() + ":" + getConfigurationFilename() + ":" + getSingleVmServerModuleClassname() + ":" +  getSingleVmServerConfigurationFilename()+ ":" + isSingleVm();
    }

    /**
     * Setter injector for første testmetode kalles, og før hver testmetode hvis {@link #isReuseInjector()} returnerer
     * false. TestNG krever at den ikke er private.
     *
     * @param context leveres at TestNG rammeverket og holder state på tvers av testcases
     */
    @BeforeMethod(alwaysRun = true)
    protected final void beforeMethod(ITestContext context) {
        if (injector == null || !isReuseInjector()) {
            injector = moduleBuilder.buildInjector();
            injector.injectMembers(this);
        }
        resetLogin();
    }

    /**
     * Kalles før hver testmetode og bør brukes til å nullstille pålogget bruker. Overskriv denne metode hvis
     * modulen ikke krever login eller krever annen form for login.
     */
    protected void resetLogin() {
        Configuration configuration = injector.getInstance(Configuration.class);

        String serverUrl = configuration.getString(ConfigurationConstants.SERVER_URL);
        final ServerUrlHolder serverUrlHolder = injector.getInstance(ServerUrlHolder.class);
        serverUrlHolder.set(serverUrl);

        LoginUserHolder userHolder = injector.getInstance(LoginUserHolder.class);
        String username = configuration.getString(ConfigurationConstants.SERVER_USERNAME);
        String password = configuration.getString(ConfigurationConstants.SERVER_PASSWORD);
        userHolder.set(new LoginUser(username, password));

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
