package no.statkart.skif.persistence;

import no.statkart.skif.persistence.jdbc.SingleVmTransactionAwareDataSource;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.testng.Assert;
import org.testng.annotations.Test;

import javax.sql.DataSource;
import javax.transaction.*;
import java.sql.Connection;
import java.sql.SQLException;

public class SingleVmTransactionManagerTest {
    @Test
    public void testCommit() throws SQLException, SystemException, NotSupportedException, HeuristicRollbackException, HeuristicMixedException, RollbackException {
        Connection connection = Mockito.mock(Connection.class);

        DataSource dataSource = Mockito.mock(DataSource.class);
        Mockito.when(dataSource.getConnection()).thenReturn(connection);

        SingleVmTransactionManager transactionManager = new SingleVmTransactionManager(dataSource);

        Assert.assertNull(transactionManager.getTransaction(), "Transaction before begin");
        Assert.assertEquals(transactionManager.getStatus(), Status.STATUS_NO_TRANSACTION, "Status before begin");

        transactionManager.begin();
        Assert.assertNotNull(transactionManager.getTransaction(), "Transaction after begin");
        Assert.assertEquals(transactionManager.getStatus(), Status.STATUS_ACTIVE, "Status after begin");

        transactionManager.commit();
        Assert.assertNull(transactionManager.getTransaction(), "Transaction after commit");
        Assert.assertEquals(transactionManager.getStatus(), Status.STATUS_NO_TRANSACTION, "Status after commit");

        Mockito.verify(connection).setAutoCommit(false);
        Mockito.verify(connection).commit();
        Mockito.verify(connection).close();
        Mockito.verifyNoMoreInteractions(connection);
    }

    @Test
    public void testCommitRollbackOnly() throws SQLException, SystemException, NotSupportedException, HeuristicRollbackException, HeuristicMixedException, RollbackException {
        Connection connection = Mockito.mock(Connection.class);

        DataSource dataSource = Mockito.mock(DataSource.class);
        Mockito.when(dataSource.getConnection()).thenReturn(connection);

        SingleVmTransactionManager transactionManager = new SingleVmTransactionManager(dataSource);

        Assert.assertNull(transactionManager.getTransaction(), "Transaction before begin");
        Assert.assertEquals(transactionManager.getStatus(), Status.STATUS_NO_TRANSACTION, "Status before begin");

        transactionManager.begin();
        Assert.assertNotNull(transactionManager.getTransaction(), "Transaction after begin");
        Assert.assertEquals(transactionManager.getStatus(), Status.STATUS_ACTIVE, "Status after begin");

        transactionManager.setRollbackOnly();
        Assert.assertEquals(transactionManager.getStatus(), Status.STATUS_MARKED_ROLLBACK, "Status after setRollbackOnly");

        try {
            transactionManager.commit();
            Assert.fail("Should have failed");
        } catch (RollbackException e) {
            Assert.assertEquals(e.getMessage(), "Transaction marked rollback-only", "Exception message");
            Assert.assertNull(e.getCause(), "Cause");
            Assert.assertEquals(e.getSuppressed(), new Throwable[0], "Suppressed");
        }
        Assert.assertNull(transactionManager.getTransaction(), "Transaction after commit");
        Assert.assertEquals(transactionManager.getStatus(), Status.STATUS_NO_TRANSACTION, "Status after commit");

        Mockito.verify(connection).setAutoCommit(false);
        Mockito.verify(connection).rollback();
        Mockito.verify(connection).close();
        Mockito.verifyNoMoreInteractions(connection);
    }

    @Test
    public void testCommitFailed() throws SQLException, SystemException, NotSupportedException, HeuristicRollbackException, HeuristicMixedException, RollbackException {
        Connection connection = Mockito.mock(Connection.class);
        Mockito.doThrow(new SQLException("Commit error")).when(connection).commit();

        DataSource dataSource = Mockito.mock(DataSource.class);
        Mockito.when(dataSource.getConnection()).thenReturn(connection);

        SingleVmTransactionManager transactionManager = new SingleVmTransactionManager(dataSource);

        Assert.assertNull(transactionManager.getTransaction(), "Transaction before begin");
        Assert.assertEquals(transactionManager.getStatus(), Status.STATUS_NO_TRANSACTION, "Status before begin");

        transactionManager.begin();
        Assert.assertNotNull(transactionManager.getTransaction(), "Transaction after begin");
        Assert.assertEquals(transactionManager.getStatus(), Status.STATUS_ACTIVE, "Status after begin");

        try {
            transactionManager.commit();
            Assert.fail("Should have failed");
        } catch (HeuristicMixedException e) {
            Assert.assertEquals(e.getMessage(), "Error during commit", "Exception message");
            Assert.assertNotNull(e.getCause(), "Cause");
            Assert.assertEquals(e.getCause().getClass(), SQLException.class, "Cause class");
            Assert.assertEquals(e.getCause().getMessage(), "Commit error", "Cause message");
            Assert.assertEquals(e.getSuppressed(), new Throwable[0], "Suppressed");
        }
        Assert.assertNull(transactionManager.getTransaction(), "Transaction after commit");
        Assert.assertEquals(transactionManager.getStatus(), Status.STATUS_NO_TRANSACTION, "Status after commit");

        Mockito.verify(connection).setAutoCommit(false);
        Mockito.verify(connection).commit();
        Mockito.verify(connection).rollback();
        Mockito.verify(connection).close();
        Mockito.verifyNoMoreInteractions(connection);
    }

    @Test
    public void testCommitFailedRollbackFailed() throws SQLException, SystemException, NotSupportedException, HeuristicRollbackException, HeuristicMixedException, RollbackException {
        Connection connection = Mockito.mock(Connection.class);
        Mockito.doThrow(new SQLException("Commit error")).when(connection).commit();
        Mockito.doThrow(new SQLException("Rollback error")).when(connection).rollback();

        DataSource dataSource = Mockito.mock(DataSource.class);
        Mockito.when(dataSource.getConnection()).thenReturn(connection);

        SingleVmTransactionManager transactionManager = new SingleVmTransactionManager(dataSource);

        Assert.assertNull(transactionManager.getTransaction(), "Transaction before begin");
        Assert.assertEquals(transactionManager.getStatus(), Status.STATUS_NO_TRANSACTION, "Status before begin");

        transactionManager.begin();
        Assert.assertNotNull(transactionManager.getTransaction(), "Transaction after begin");
        Assert.assertEquals(transactionManager.getStatus(), Status.STATUS_ACTIVE, "Status after begin");

        try {
            transactionManager.commit();
            Assert.fail("Should have failed");
        } catch (HeuristicMixedException e) {
            Assert.assertEquals(e.getMessage(), "Error during commit", "Exception message");
            Assert.assertNotNull(e.getCause(), "Cause");
            Assert.assertEquals(e.getCause().getClass(), SQLException.class, "Cause class");
            Assert.assertEquals(e.getCause().getMessage(), "Commit error", "Cause message");
            Throwable[] suppressed = e.getSuppressed();
            Assert.assertEquals(suppressed.length, 1, "Suppressed");
            Assert.assertEquals(suppressed[0].getClass(), SQLException.class, "Suppressed class");
            Assert.assertEquals(suppressed[0].getMessage(), "Rollback error", "Suppressed message");
        }
        Assert.assertNull(transactionManager.getTransaction(), "Transaction after commit");
        Assert.assertEquals(transactionManager.getStatus(), Status.STATUS_NO_TRANSACTION, "Status after commit");

        Mockito.verify(connection).setAutoCommit(false);
        Mockito.verify(connection).commit();
        Mockito.verify(connection).rollback();
        Mockito.verify(connection).close();
        Mockito.verifyNoMoreInteractions(connection);
    }

    @Test
    public void testRollback() throws SQLException, SystemException, NotSupportedException, HeuristicRollbackException, HeuristicMixedException, RollbackException {
        Connection connection = Mockito.mock(Connection.class);

        DataSource dataSource = Mockito.mock(DataSource.class);
        Mockito.when(dataSource.getConnection()).thenReturn(connection);

        SingleVmTransactionManager transactionManager = new SingleVmTransactionManager(dataSource);

        Assert.assertNull(transactionManager.getTransaction(), "Transaction before begin");
        Assert.assertEquals(transactionManager.getStatus(), Status.STATUS_NO_TRANSACTION, "Status before begin");

        transactionManager.begin();
        Assert.assertNotNull(transactionManager.getTransaction(), "Transaction after begin");
        Assert.assertEquals(transactionManager.getStatus(), Status.STATUS_ACTIVE, "Status after begin");

        transactionManager.rollback();
        Assert.assertNull(transactionManager.getTransaction(), "Transaction after rollback");
        Assert.assertEquals(transactionManager.getStatus(), Status.STATUS_NO_TRANSACTION, "Status after rollback");

        Mockito.verify(connection).setAutoCommit(false);
        Mockito.verify(connection).rollback();
        Mockito.verify(connection).close();
        Mockito.verifyNoMoreInteractions(connection);
    }

    @Test
    public void testRollbackRollbackOnly() throws SQLException, SystemException, NotSupportedException, HeuristicRollbackException, HeuristicMixedException, RollbackException {
        Connection connection = Mockito.mock(Connection.class);

        DataSource dataSource = Mockito.mock(DataSource.class);
        Mockito.when(dataSource.getConnection()).thenReturn(connection);

        SingleVmTransactionManager transactionManager = new SingleVmTransactionManager(dataSource);

        Assert.assertNull(transactionManager.getTransaction(), "Transaction before begin");
        Assert.assertEquals(transactionManager.getStatus(), Status.STATUS_NO_TRANSACTION, "Status before begin");

        transactionManager.begin();
        Assert.assertNotNull(transactionManager.getTransaction(), "Transaction after begin");
        Assert.assertEquals(transactionManager.getStatus(), Status.STATUS_ACTIVE, "Status after begin");

        transactionManager.setRollbackOnly();
        Assert.assertEquals(transactionManager.getStatus(), Status.STATUS_MARKED_ROLLBACK, "Status after setRollbackOnly");

        transactionManager.rollback();
        Assert.assertNull(transactionManager.getTransaction(), "Transaction after rollback");
        Assert.assertEquals(transactionManager.getStatus(), Status.STATUS_NO_TRANSACTION, "Status after rollback");

        Mockito.verify(connection).setAutoCommit(false);
        Mockito.verify(connection).rollback();
        Mockito.verify(connection).close();
        Mockito.verifyNoMoreInteractions(connection);
    }

    @Test
    public void testTransactionAwareDataSource() throws SQLException, SystemException, NotSupportedException {
        final boolean[] autoCommit = new boolean[]{true};
        final boolean[] closed = new boolean[]{false};
        Connection connection = Mockito.mock(Connection.class);
        Mockito.when(connection.getAutoCommit()).then(new Answer<Boolean>() {
            @Override
            public Boolean answer(InvocationOnMock invocation) throws Throwable {
                return autoCommit[0];
            }
        });
        Mockito.doAnswer(new Answer<Void>() {
            @Override
            public Void answer(InvocationOnMock invocation) throws Throwable {
                autoCommit[0] = (boolean) invocation.getArguments()[0];
                return null;
            }
        }).when(connection).setAutoCommit(Mockito.anyBoolean());
        Mockito.when(connection.isClosed()).then(new Answer<Boolean>() {
            @Override
            public Boolean answer(InvocationOnMock invocation) throws Throwable {
                return closed[0];
            }
        });
        Mockito.doAnswer(new Answer<Void>() {
            @Override
            public Void answer(InvocationOnMock invocation) throws Throwable {
                closed[0] = true;
                return null;
            }
        }).when(connection).close();

        DataSource dataSource = Mockito.mock(DataSource.class);
        Mockito.when(dataSource.getConnection()).thenReturn(connection);

        SingleVmTransactionManager transactionManager = new SingleVmTransactionManager(dataSource);
        SingleVmTransactionAwareDataSource transactionAwareDataSource = new SingleVmTransactionAwareDataSource(transactionManager);

        transactionManager.begin();

        Connection transactionAwareConnection = transactionAwareDataSource.getConnection();
        Mockito.verify(connection).setAutoCommit(false);
        Assert.assertFalse(transactionAwareConnection.getAutoCommit());
        Mockito.verify(connection).getAutoCommit();

        transactionManager.rollback();
        Assert.assertTrue(transactionAwareConnection.isClosed());

        Mockito.verify(connection).rollback();
        Mockito.verify(connection).close();
        Mockito.verify(connection).isClosed();
        Mockito.verifyNoMoreInteractions(connection);
    }
}
