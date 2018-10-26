package no.statkart.skif.util.testsupport;

import no.statkart.skif.SkifUtil;
import no.statkart.skif.config.*;
import no.statkart.skif.module.ModuleBuilder;
import no.statkart.skif.service.LoginUser;
import no.statkart.skif.service.LoginUserHolder;
import no.statkart.skif.service.ServerUrlHolder;
import org.testng.ITestContext;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

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
public class SkifTestCase extends AbstractSkifTestCase {
    protected void setSingleVmServerModuleClassname(String singleVmModuleClassname) {
        setSingleVmServerModuleClass(SkifUtil.classForName(singleVmModuleClassname));
    }

    protected void setSingleVm(Boolean singleVm) {
        checkModuleBuilderNotCreated();
        this.singleVm = singleVm;
    }

    protected final ModuleBuilder createReusableModuleBuilder() {
        ModuleBuilder builder = new ModuleBuilder(new SystemConfiguration());

        String moduleClassname = getModuleClassname();
        if (moduleClassname != null) {
            builder.setModuleClassname(moduleClassname);
        }

        builder.setConfiguration(getConfiguration());

        String singleVmServerModuleClassname = getSingleVmServerModuleClassname();
        if (singleVmServerModuleClassname != null) {
            builder.setSingleVmServerModuleClassname(singleVmServerModuleClassname);
        }

        builder.setSingleVmServerConfiguration(getSingleVmConfiguration());


        if (isSingleVm() != null) {
            builder.setSingleVm(isSingleVm());
        }
        return builder;
    }

    protected Configuration getSingleVmConfiguration() {
        String[] singleVmServerConfigurationFilenames = getSingleVmServerConfigurationFilenames();
        return singleVmServerConfigurationFilenames != null ? new SkifConfiguration(singleVmServerConfigurationFilenames) : new SkifServerConfiguration();
    }

    protected Configuration getConfiguration() {
        String[] configurationFilenames = getConfigurationFilenames();
        return configurationFilenames != null ? new SkifConfiguration(configurationFilenames) : new SkifClientConfiguration();
    }


    /**
     * Beregner konfigurasjonsnøkkel for testcase på basis av hvilke konfigurasjonsklasser testcasen bruker.
     */
    protected final String calcConfigurationKey() {
        return super.calcConfigurationKey();
    }

    /**
     * Setter injector for første testmetode kalles, og før hver testmetode hvis {@link #isReuseInjector()} returnerer
     * false. TestNG krever at den ikke er private.
     *
     * @param context leveres at TestNG rammeverket og holder state på tvers av testcases
     */
    @BeforeMethod(alwaysRun = true)
    protected void beforeMethod(ITestContext context) {
        if (injector == null || !isReuseInjector()) {
            injector = getModuleBuilder().buildInjector();
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

        String serverUrl = configuration.getString(SkifConfigConstants.SERVER_URL);
        final ServerUrlHolder serverUrlHolder = injector.getInstance(ServerUrlHolder.class);
        serverUrlHolder.set(serverUrl);

        LoginUserHolder userHolder = injector.getInstance(LoginUserHolder.class);
        String username = configuration.getString(SkifConfigConstants.SERVER_USERNAME);
        String password = configuration.getString(SkifConfigConstants.SERVER_PASSWORD);
        userHolder.set(new LoginUser(username, password));

    }

    protected void setLogin(String username, String password) {
        LoginUserHolder userHolder = injector.getInstance(LoginUserHolder.class);
        userHolder.set(new LoginUser(username, password));
    }


}
