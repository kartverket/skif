package no.statkart.skif.util.testsupport;

import com.google.inject.Injector;
import com.google.inject.spi.InjectionPoint;
import no.statkart.skif.SkifModule;
import no.statkart.skif.config.SkifConfiguration;
import no.statkart.skif.config.SystemConfiguration;
import no.statkart.skif.exception.ImplementationException;
import no.statkart.skif.module.ModuleBuilder;
import no.statkart.skif.service.RunOnServerMethod;
import no.statkart.skif.service.RunOnServerService;
import no.statkart.skif.service.module.client.RunOnRemoteServerBuilder;
import no.statkart.skif.service.module.client.RunOnRemoteServerClientModule;
import no.statkart.skif.service.proxy.ChainedProxyHandler;
import org.testng.IHookCallBack;
import org.testng.IHookable;
import org.testng.ITestResult;

import java.lang.reflect.Field;
import java.lang.reflect.Member;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Set;

/**
 * @author Henrik Fredholm
 * @since 2.1
 */
public class SkifServer3TestCase extends AbstractSkifTestCase implements IHookable {
    private Injector clientInjector;
    private final Class<? extends SkifModule> serverModuleExtClass;
    private final Class<? extends ChainedProxyHandler> ejbServiceChainExtClass;

    public SkifServer3TestCase(Class<? extends SkifModule> serverModuleClass) {
        this(RunOnRemoteServerClientModule.class, serverModuleClass, null, null);
    }

    public SkifServer3TestCase(Class<? extends SkifModule> moduleClass, Class<? extends SkifModule> serverModuleClass, Class<? extends SkifModule> serverModuleExtClass, Class<? extends ChainedProxyHandler> ejbServiceChainExtClass) {
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

    private RunOnServerService getService(TestServerMethodTransactionAttributeType txType) {
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
        final TestServerMethodTransactionAttributeType txType = TestServerMethodTransactionAttributesLookup.getAnnotation(testMethod);
        final RunOnServerService runOnServerService = getService(txType);
        runOnServerService.run(new RunOnServerMethod() {
            @Override
            public Object run() {
                SkifServer3TestCase.this.injector = injector;
                injector.injectMembers(SkifServer3TestCase.this);
                Injector savedClientInjector = clientInjector;
                clientInjector = null;
                try {
                    callBack.runTestMethod(testResult);
                    return null;
                } finally {
                    SkifServer3TestCase.this.injector = null;
                    clientInjector = savedClientInjector;
                    resetInjectedMembers(SkifServer3TestCase.this);
                }
            }

            private void resetInjectedMembers(SkifServer3TestCase testCase) {
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
                        throw new ImplementationException("ServerTestCase understøtter ikke bruk av method injection");
                    }
                }
            }
        });
    }
}
