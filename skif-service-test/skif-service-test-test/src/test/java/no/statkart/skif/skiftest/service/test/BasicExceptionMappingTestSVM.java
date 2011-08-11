package no.statkart.skif.skiftest.service.test;

import com.google.inject.Inject;
import com.google.inject.Module;
import no.statkart.skif.SkifModule;
import no.statkart.skif.config.Configuration;
import no.statkart.skif.config.ConfigurationConstants;
import no.statkart.skif.mapper.MappingException;
import no.statkart.skif.module.ModuleConfiguration;
import no.statkart.skif.module.ModuleStrategyFactory;
import no.statkart.skif.service.LoginUser;
import no.statkart.skif.service.LoginUserHolder;
import no.statkart.skif.service.ServerUrlHolder;
import no.statkart.skif.service.module.ClientModuleStrategyFactory;
import no.statkart.skif.service.module.common.RemoteServerModule;
import no.statkart.skif.service.module.common.RemoteServiceModule;
import no.statkart.skif.skiftest.config.SkifTestGroupExServices;
import no.statkart.skif.skiftest.config.SkifTestServerModule;
import no.statkart.skif.skiftest.exception.SimpleException;
import no.statkart.skif.skiftest.exception.SimpleNonMappedException;
import no.statkart.skif.skiftest.service.testex.TestExService;
import no.statkart.skif.skiftest.wsapi.exception.simple.mapping.SkifTestExceptionMapper2;
import no.statkart.skif.skiftest.wsapi.mapping.SkifTestMapper;
import no.statkart.skif.util.testsupport.SkifTestCase;
import org.testng.annotations.Test;

import javax.xml.ws.soap.SOAPFaultException;
import java.util.ArrayList;
import java.util.Arrays;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertSame;

/**
 * @author Henrik Fredholm
 * @since 2.0
 */
public class BasicExceptionMappingTestSVM extends SkifTestCase {

    @Inject
    private TestExService service;

    public BasicExceptionMappingTestSVM() {
        setModuleClass(SkifTestClientModule.class);
        setSingleVmServerModuleClass(SkifTestServerModule.class);
        setSingleVm(true);
    }

    @Override
    protected void resetLogin() {
        final LoginUserHolder loginUserHolder = injector.getInstance(LoginUserHolder.class);
        loginUserHolder.set(new LoginUser("frehen", "matrikkel2"));
        final ServerUrlHolder serverUrlHolder = injector.getInstance(ServerUrlHolder.class);
        serverUrlHolder.set("https://localhost:7002");
    }

    /**
     * NoTx service kaster checked exception som ikke kan mappes av server. I SingleVm mode anvendes mapping rammeverket
     * ikke og og den manglende exception mappping oppdages derfor ikke. Excetionen sendes derfor uforandret videre til
     * klienten. I JEE mode ville mappringen rammeverket ha kastet en MappingException som JAX-WS så ville ha gjort om
     * til en SOAPFaultException.
     */
    @Test(expectedExceptions = SimpleNonMappedException.class )
    public void testThrowNonMappedExceptionNoTx() throws SimpleException, SimpleNonMappedException {
            service.noTx(SimpleNonMappedException.class.getName(), "abc");
    }
}