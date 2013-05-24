package no.statkart.skif.util.testsupport;

import com.google.inject.Injector;
import com.google.inject.spi.InjectionPoint;
import no.statkart.skif.SkifModule;
import no.statkart.skif.config.Configuration;
import no.statkart.skif.config.SkifConfigConstants;
import no.statkart.skif.config.SkifConfiguration;
import no.statkart.skif.exception.ImplementationException;
import no.statkart.skif.module.ModuleBuilder;
import no.statkart.skif.service.LoginUser;
import no.statkart.skif.service.LoginUserHolder;
import no.statkart.skif.service.RunOnServerMethod;
import no.statkart.skif.service.RunOnServerService;
import no.statkart.skif.service.module.client.RunOnRemoteServerClientModule;
import no.statkart.skif.service.proxy.ChainedProxyHandler;
import org.testng.IHookCallBack;
import org.testng.IHookable;
import org.testng.ITestResult;

import java.lang.reflect.Field;
import java.lang.reflect.Member;
import java.lang.reflect.Method;
import java.util.Set;

/**
 * Baseklasse for tester som skal kjøre i tjenermodus. Injiserbare felter blir injisert med tjener-injektoren før hver
 * testmetode, og nullstilt etter hvert kall.
 *
 * @author Henrik Fredholm
 * @author Tor Egil R. Strand
 * @since 2.1
 */
public class SkifServerTestCase extends AbstractSkifTestCase implements IHookable {
    private Injector clientInjector;
    private final Class<? extends SkifModule> serverModuleExtClass;
    private final Class<? extends ChainedProxyHandler> ejbServiceChainExtClass;

    public SkifServerTestCase(Class<? extends SkifModule> serverModuleClass) {
        this(RunOnRemoteServerClientModule.class, serverModuleClass, null, null);
    }

    public SkifServerTestCase(Class<? extends SkifModule> moduleClass, Class<? extends SkifModule> serverModuleClass, Class<? extends SkifModule> serverModuleExtClass, Class<? extends ChainedProxyHandler> ejbServiceChainExtClass) {
        setModuleClass(moduleClass);
        setSingleVmServerModuleClass(serverModuleClass);
        this.serverModuleExtClass = serverModuleExtClass;
        this.ejbServiceChainExtClass = ejbServiceChainExtClass;
    }

    protected final Injector getClientInjector() {
        if (clientInjector==null) {
            clientInjector = getModuleBuilder().buildInjector();
        }
        return clientInjector;
    }

    private RunOnServerService getService(TestTransactionAttributeType txType) {
        return getClientInjector().getInstance(txType.getServiceClass());
    }


    @Override
    protected ModuleBuilder createReusableModuleBuilder() {
        ModuleBuilder builder = new ModuleBuilder();
        builder.setSingleVm(true);
        builder.setUseSharedServer(true);
        builder.setModuleClass(getModuleClass());
        builder.setSingleVmServerModuleClass(getSingleVmServerModuleClass());

        if(serverModuleExtClass!=null) {
            builder.setSingleVmServerModuleExtClass(serverModuleExtClass);
        }
        if (ejbServiceChainExtClass!=null) {
            builder.setSingleVmServerEjbServiceChainExtClass(ejbServiceChainExtClass);
        }

        String[] configurationFilenames = getConfigurationFilenames();
        if (configurationFilenames!=null) {
            builder.setConfiguration(new SkifConfiguration(configurationFilenames));
        } else {
            builder.setConfiguration(new SkifConfiguration());
        }

        String[] singleVmServerConfigurationFilenames = getSingleVmServerConfigurationFilenames();
        if (singleVmServerConfigurationFilenames!=null) {
            builder.setSingleVmServerConfiguration(new SkifConfiguration(singleVmServerConfigurationFilenames));
        } else {
            builder.setSingleVmServerConfiguration(new SkifConfiguration());
        }

        return builder;
    }


    @Override
    public final void run(final IHookCallBack callBack, final ITestResult testResult) {
        Method testMethod = TestNGSupport.getMethod(callBack);
        final TestTransactionAttributeType txType = TestTransactionAttributesLookup.getAnnotation(testMethod);
        final RunOnServerService runOnServerService = getService(txType);
        try {
            resetLogin();
            runOnServerService.run(new RunOnServerMethod() {
                @Override
                public Object run() {
                    SkifServerTestCase.this.injector = injector;
                    injector.injectMembers(SkifServerTestCase.this);
                    Injector savedClientInjector = clientInjector;
                    clientInjector = null;
                    try {
                        callBack.runTestMethod(testResult);
                        Throwable throwable = testResult.getThrowable();
                        if (throwable != null) {
                            throw new SkifServerTestCaseTestException(throwable);
                        }
                        return null;
                    } finally {
                        SkifServerTestCase.this.injector = null;
                        clientInjector = savedClientInjector;
                        resetInjectedMembers(SkifServerTestCase.this);
                    }
                }

                private void resetInjectedMembers(SkifServerTestCase testCase) {
                    final Set<InjectionPoint> injectionPoints = InjectionPoint.forInstanceMethodsAndFields(testCase.getClass());
                    for (InjectionPoint injectionPoint : injectionPoints) {
                        final Member member = injectionPoint.getMember();
                        if (member instanceof Field) {
                            Field field = (Field) member;
                            field.setAccessible(true);
                            try {
                                field.set(testCase, null);
                            } catch (IllegalAccessException e) {
                                throw new ImplementationException(e);
                            }
                        } else {
                            throw new ImplementationException("ServerTestCase does not handle method injection");
                        }
                    }
                }
            });
        } catch (SkifServerTestCaseTestException ignore) {
            // Denne exeption ble kastet bare for å rydde opp på tjenersiden. Skal ikke kastes videre.
        }
    }

    /**
     * Kalles før hver testmetode og bør brukes til å nullstille pålogget bruker. Overskriv denne metode hvis
     * modulen ikke krever login eller krever annen form for login.
     *
     * @since 2.3.0
     */
    protected void resetLogin() {
        LoginUserHolder userHolder = clientInjector.getInstance(LoginUserHolder.class);
        Configuration configuration = clientInjector.getInstance(Configuration.class);
        String username = configuration.getString(SkifConfigConstants.SERVER_USERNAME);
        String password = configuration.getString(SkifConfigConstants.SERVER_PASSWORD);
        userHolder.set(new LoginUser(username, password));
    }
}

/**
 * Exception som kastes fra {@link RunOnServerMethod} dersom testen feilet, slik at eventuelle transaksjoner rulles
 * tilbake fremfor å committes. Denne exception må fanges opp på klientsiden slik at den ikke kommer tilbake til TestNG.
 * TestNG skal nemlig benytte den exception som ligger i {@link ITestResult}.
 */
class SkifServerTestCaseTestException extends RuntimeException {
    SkifServerTestCaseTestException(Throwable t) {
        super(t);
    }
}
