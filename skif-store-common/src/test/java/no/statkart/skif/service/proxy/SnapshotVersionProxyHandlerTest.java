package no.statkart.skif.service.proxy;

import no.statkart.skif.store.SnapshotVersion;
import no.statkart.skif.store.SnapshotVersionContext;
import org.testng.annotations.Test;

import static org.testng.Assert.*;

public class SnapshotVersionProxyHandlerTest {
    @Test
    public void testCurrent() {
        SnapshotVersionProxyHandler<TestService> snapshotVersionProxyHandler = new SnapshotVersionProxyHandler<>(SnapshotVersionContext.getInstance());
        snapshotVersionProxyHandler.setChained(new InvokeViaInstanceProxyHandler<>((SnapshotVersion snapshotVersion) -> {
            assertEquals(snapshotVersion, SnapshotVersion.CURRENT);
            assertEquals(SnapshotVersionContext.getInstance().getSnapshotVersion(), SnapshotVersion.CURRENT);
        }));
        TestService testService = snapshotVersionProxyHandler.buildProxy(TestService.class);
        assertEquals(SnapshotVersionContext.getInstance().getSnapshotVersion(), SnapshotVersion.CURRENT);
        testService.test(SnapshotVersion.CURRENT);
        assertEquals(SnapshotVersionContext.getInstance().getSnapshotVersion(), SnapshotVersion.CURRENT);
    }

    @Test
    public void testOld() {
        SnapshotVersionProxyHandler<TestService> snapshotVersionProxyHandler = new SnapshotVersionProxyHandler<>(SnapshotVersionContext.getInstance());
        snapshotVersionProxyHandler.setChained(new InvokeViaInstanceProxyHandler<>((SnapshotVersion snapshotVersion) -> {
            assertEquals(snapshotVersion, SnapshotVersion.OLD);
            assertEquals(SnapshotVersionContext.getInstance().getSnapshotVersion(), SnapshotVersion.OLD);
        }));
        TestService testService = snapshotVersionProxyHandler.buildProxy(TestService.class);
        assertEquals(SnapshotVersionContext.getInstance().getSnapshotVersion(), SnapshotVersion.CURRENT);
        testService.test(SnapshotVersion.OLD);
        assertEquals(SnapshotVersionContext.getInstance().getSnapshotVersion(), SnapshotVersion.CURRENT);
    }

    @Test
    public void testException() {
        SnapshotVersionProxyHandler<TestService> snapshotVersionProxyHandler = new SnapshotVersionProxyHandler<>(SnapshotVersionContext.getInstance());
        snapshotVersionProxyHandler.setChained(new InvokeViaInstanceProxyHandler<>((SnapshotVersion snapshotVersion) -> {
            throw new TestException("Test");
        }));
        TestService testService = snapshotVersionProxyHandler.buildProxy(TestService.class);
        assertEquals(SnapshotVersionContext.getInstance().getSnapshotVersion(), SnapshotVersion.CURRENT);
        try {
            testService.test(SnapshotVersion.OLD);
            fail("Skulle fått exception");
        } catch (TestException ignored) {
        }
        assertEquals(SnapshotVersionContext.getInstance().getSnapshotVersion(), SnapshotVersion.CURRENT);
    }

    public interface TestService {
        void test(SnapshotVersion snapshotVersion);
    }

    private static class TestException extends RuntimeException {
        TestException(String message) {
            super(message);
        }
    }
}