import no.statkart.skif.exception.FinderException;
import no.statkart.skif.exception.ImplementationException;
import no.statkart.skif.exception.PermissionDeniedException;
import no.statkart.skif.skiftest.exception.SimpleException;
import no.statkart.skif.skiftest.wsapi.exception.ServiceException;
import no.statkart.skif.skiftest.wsapi.exception.impl.ServiceFaultInfo;
import no.statkart.skif.skiftest.wsapi.exception.impl.SimpleFaultInfo;
import no.statkart.skif.skiftest.wsapi.exception.impl.mapping.SkifTestExceptionMapper;
import no.statkart.skif.skiftest.wsapi.exception.impl.mapping.SkifTestExceptionMapping;
import org.testng.annotations.Test;

import javax.ejb.EJBAccessException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.testng.Assert.assertEquals;

/**
 * @since 2.2.0
 */
@SuppressWarnings("ThrowableResultOfMethodCallIgnored")
@Test
public class EjbAccessExceptionMappingTest {
    SkifTestExceptionMapping mapping = new SkifTestExceptionMapper().getMapping();

    /**
     * Tester at AbstractExceptionMapper gjør om EJBAccessException til PersmissionDeniedException.
     * Tester også at PersmissionDeniedException konverteres riktig til ServiceException og tilbake igjen.
     */
    public void testEjbAccessException() {
        EJBAccessException domainException = new EJBAccessException("Test");
        Throwable wsapiException = mapping.d2w(domainException);
        assertThat(wsapiException).isInstanceOf(ServiceException.class);
        assertThat(((ServiceException) wsapiException).getFaultInfo().getCategory())
                .isEqualTo(":ServiceException:ApplicationException:AccessException:PermissionDeniedException:");
        assertThat(mapping.w2d(wsapiException)).isInstanceOf(PermissionDeniedException.class);
    }

    public void testSubclassMapping() {
        ImplementationException domainImplementationException = new ImplementationException("Test IE");
        ServiceException wsImplementationException = (ServiceException) mapping.d2w(domainImplementationException);
        assertThat(wsImplementationException.getMessage()).isEqualTo("Test IE");
        assertThat(wsImplementationException.getFaultInfo()).isInstanceOf(ServiceFaultInfo.class);
        Throwable mappedImplementationException = mapping.w2d(wsImplementationException);
        assertThat(mappedImplementationException.getMessage()).isEqualTo("Test IE");
        assertThat(mappedImplementationException).isInstanceOf(ImplementationException.class);

        FinderException domainFinderException = new FinderException("Test FE");
        ServiceException wsFinderException = (ServiceException) mapping.d2w(domainFinderException);
        assertEquals(wsFinderException.getMessage(), domainFinderException.getMessage());
        assertEquals(wsFinderException.getFaultInfo().getClass(), ServiceFaultInfo.class);
        Throwable mappedFinderException = mapping.w2d(wsFinderException);
        assertEquals(mappedFinderException.getMessage(), domainFinderException.getMessage());
        assertEquals(mappedFinderException.getClass(), FinderException.class);

        SimpleException domainSimpleException = new SimpleException("Test SE", "Info");
        ServiceException wsSimpleException = (ServiceException) mapping.d2w(domainSimpleException);
        assertThat(wsSimpleException.getMessage()).isEqualTo("Test SE");
        assertThat(wsSimpleException.getFaultInfo()).isInstanceOf(SimpleFaultInfo.class);
        Throwable mappedSimpleException = mapping.w2d(wsSimpleException);
        assertThat(mappedSimpleException).isInstanceOf(SimpleException.class);
        assertThat(((SimpleException) mappedSimpleException).getInfoField()).isEqualTo("Info");
    }
}
