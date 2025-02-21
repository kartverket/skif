package no.statkart.skif.storetest.wsapi.mapping.exception;

import com.google.inject.util.Providers;
import no.statkart.skif.exception.*;
import no.statkart.skif.locker.LockInfo;
import no.statkart.skif.locker.LockKey;
import no.statkart.skif.store.SnapshotVersion;
import no.statkart.skif.storetest.domain.basic.SimpleId;
import no.statkart.skif.storetest.wsapi.exception.*;
import no.statkart.skif.storetest.wsapi.exception.mapping.StoreTestExceptionMapper;
import no.statkart.skif.storetest.wsapi.exception.mapping.StoreTestExceptionMapping;
import no.statkart.skif.storetest.wsapi.mapping.StoreTestMapper;
import no.statkart.skif.storetest.wsapi.mapping.StoreTestMapping;
import org.assertj.core.api.Assertions;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@SuppressWarnings({"ThrowableInstanceNeverThrown", "ThrowableResultOfMethodCallIgnored"})
@Test
public class ExceptionMappingTest {
    public void testImplementationExceptionMapping() {
        StoreTestMapping mapping = new StoreTestMapper(Providers.of(SnapshotVersion.CURRENT)).getMapping();
        StoreTestExceptionMapping exceptionMapping = new StoreTestExceptionMapper(mapping).getMapping();

        ImplementationException domainException = new ImplementationException("Test123");
        ServiceException wsException = (ServiceException) exceptionMapping.d2w(domainException);

        Assert.assertEquals(wsException.getFaultInfo().getClass(), ServiceFaultInfo.class);
        Assert.assertEquals(wsException.getMessage(), domainException.getMessage());

        Throwable mappedException = exceptionMapping.w2d(wsException);

        Assert.assertEquals(mappedException.getClass(), ImplementationException.class);
        Assert.assertEquals(mappedException.getMessage(), domainException.getMessage());
    }

    public void testAttemptDeleteExceptionMapping() {
        StoreTestMapping mapping = new StoreTestMapper(Providers.of(SnapshotVersion.CURRENT)).getMapping();
        StoreTestExceptionMapping exceptionMapping = new StoreTestExceptionMapper(mapping).getMapping();

        AttemptDeleteException domainException = new AttemptDeleteException(new SimpleId(1L), new RuntimeException("test"));
        ServiceException wsException = (ServiceException) exceptionMapping.d2w(domainException);

        Assert.assertEquals(wsException.getFaultInfo().getClass(), AttemptDeleteFaultInfo.class);
        Assert.assertEquals(((AttemptDeleteFaultInfo) wsException.getFaultInfo()).getBubbleId().getClass(), no.statkart.skif.storetest.wsapi.domain.basic.SimpleId.class);

        Throwable mappedException = exceptionMapping.w2d(wsException);

        Assert.assertEquals(mappedException.getClass(), AttemptDeleteException.class);
        Assert.assertEquals(((AttemptDeleteException) mappedException).getBubbleId(), domainException.getBubbleId());
        Assert.assertEquals(mappedException.getMessage(), domainException.getMessage());
        Assert.assertEquals(mappedException.getCause().getMessage(), "-> " + domainException.getCause().getClass().getName() + ": " + domainException.getCause().getMessage());
    }

    public void testLockedExceptionMapping() {
        StoreTestMapping mapping = new StoreTestMapper(Providers.of(SnapshotVersion.CURRENT)).getMapping();
        StoreTestExceptionMapping exceptionMapping = new StoreTestExceptionMapper(mapping).getMapping();

        List<LockInfo<String>> lockInfos = new ArrayList<>();
        lockInfos.add(new LockInfo<>(
                new LockKey<>("Test", "1"),
                "Deg",
                new Timestamp(System.currentTimeMillis() + 100000L),
                false
        ));

        LockedException domainException = new LockedException("Meg", lockInfos);
        ServiceException wsException = (ServiceException) exceptionMapping.d2w(domainException);

        Assert.assertEquals(wsException.getFaultInfo().getClass(), LockedFaultInfo.class);
        Assert.assertEquals(((LockedFaultInfo) wsException.getFaultInfo()).getOwner(), domainException.getOwner());
        Assert.assertEquals(((LockedFaultInfo) wsException.getFaultInfo()).getLocksNotAquired().getItem().size(), 1);

        Throwable mappedException = exceptionMapping.w2d(wsException);

        Assert.assertEquals(mappedException.getClass(), LockedException.class);
        LockedException lockedException = (LockedException) mappedException;
        Assert.assertEquals(lockedException.getMessage(), domainException.getMessage()); // Blir teksten lik, så er nok det meste inni likt også, siden førstnevnte lages ut fra sistnevnte.
    }

    public void testObjectNotFoundExceptionMapping() {
        StoreTestMapping mapping = new StoreTestMapper(Providers.of(SnapshotVersion.CURRENT)).getMapping();
        StoreTestExceptionMapping exceptionMapping = new StoreTestExceptionMapper(mapping).getMapping();

        ObjectNotFoundException domainException = new ObjectNotFoundException(new SimpleId(1L));
        ServiceException wsException = (ServiceException) exceptionMapping.d2w(domainException);

        Assert.assertEquals(wsException.getFaultInfo().getClass(), ObjectsNotFoundFaultInfo.class);
        Assert.assertEquals(((ObjectsNotFoundFaultInfo) wsException.getFaultInfo()).getIdsNotFound().getItem().get(0).getClass(), no.statkart.skif.storetest.wsapi.domain.basic.SimpleId.class);

        Throwable mappedException = exceptionMapping.w2d(wsException);

        Assert.assertEquals(mappedException.getClass(), ObjectNotFoundException.class);
        Assert.assertEquals(((ObjectNotFoundException) mappedException).getNotFoundId(), domainException.getNotFoundId());
        Assert.assertEquals(mappedException.getMessage(), domainException.getMessage());
    }

    public void testObjectsNotFoundExceptionMapping() {
        StoreTestMapping mapping = new StoreTestMapper(Providers.of(SnapshotVersion.CURRENT)).getMapping();
        StoreTestExceptionMapping exceptionMapping = new StoreTestExceptionMapper(mapping).getMapping();

        ObjectsNotFoundException domainException = new ObjectsNotFoundException(Arrays.asList(new SimpleId<>(1L), new SimpleId<>(2L)));
        ServiceException wsException = (ServiceException) exceptionMapping.d2w(domainException);

        Assert.assertEquals(wsException.getFaultInfo().getClass(), ObjectsNotFoundFaultInfo.class);
        Assert.assertEquals(((ObjectsNotFoundFaultInfo) wsException.getFaultInfo()).getIdsNotFound().getItem().get(0).getClass(), no.statkart.skif.storetest.wsapi.domain.basic.SimpleId.class);

        Throwable mappedException = exceptionMapping.w2d(wsException);

        Assert.assertEquals(mappedException.getClass(), ObjectsNotFoundException.class);
        Assert.assertEquals(((ObjectsNotFoundException) mappedException).getIdsNotFound(), domainException.getIdsNotFound());
        Assert.assertEquals(mappedException.getMessage(), domainException.getMessage());
    }

    public void testNested() {
        StoreTestMapping mapping = new StoreTestMapper(Providers.of(SnapshotVersion.CURRENT)).getMapping();
        StoreTestExceptionMapping exceptionMapping = new StoreTestExceptionMapper(mapping).getMapping();

        RuntimeException runtimeException = new RuntimeException("Foo");
        ImplementationException implementationException = new ImplementationException("Wrapping", runtimeException);
        Throwable wsException = exceptionMapping.d2w(implementationException);

        Assertions.assertThat(wsException)
                .hasMessage("Wrapping")
                .isInstanceOf(ServiceException.class);
        ServiceException serviceException = (ServiceException) wsException;
        ServiceFaultInfo faultInfo = serviceException.getFaultInfo();
        ExceptionDetail exceptionDetail1 = faultInfo.getExceptionDetail();
        Assertions.assertThat(exceptionDetail1.getMessage()).isEqualTo("Wrapping");
        Assertions.assertThat(exceptionDetail1.getClassName()).isEqualTo(ImplementationException.class.getName());
        ExceptionDetail exceptionDetail2 = exceptionDetail1.getCause();
        Assertions.assertThat(exceptionDetail2.getMessage()).isEqualTo("Foo");
        Assertions.assertThat(exceptionDetail2.getClassName()).isEqualTo(RuntimeException.class.getName());

        Throwable domainException = exceptionMapping.w2d(serviceException);
        Assertions.assertThat(domainException)
                .hasMessage("Wrapping")
                .isInstanceOf(ImplementationException.class);
        Throwable cause = domainException.getCause();
        Assertions.assertThat(cause)
                .isNotNull()
                .isInstanceOf(ServerException.class)
                .hasMessage("-> java.lang.RuntimeException: Foo");
        Assertions.assertThat(cause.getCause()).isNull();
    }
}
