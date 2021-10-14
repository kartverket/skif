package no.statkart.skif.skiftest.service.txcascade;

import com.google.inject.Inject;
import com.google.inject.Provider;
import no.statkart.skif.exception.ImplementationException;
import no.statkart.skif.exception.ValidationException;
import no.statkart.skif.skiftest.service.txbmt.BeanManagedTxAService;
import no.statkart.skif.skiftest.service.txcmt.ContainerManagedTxAService;
import no.statkart.skif.util.JDBCHelper;

import java.sql.*;

/**
 * @author Henrik Fredholm
 * @since 2.0
 */
public class ContainerManagedTxCMTCascadeServiceImpl implements ContainerManagedTxCMTCascadeService {
    final Provider<Connection> connectionProvider;
    final ContainerManagedTxAService containerBasedCascadeService;
    final BeanManagedTxAService beanBasedCascadeService;

    @Inject
    public ContainerManagedTxCMTCascadeServiceImpl(Provider<Connection> connectionProvider, ContainerManagedTxAService containerBasedCascadeService, BeanManagedTxAService beanBasedCascadeService) {
        this.connectionProvider = connectionProvider;
        this.containerBasedCascadeService = containerBasedCascadeService;
        this.beanBasedCascadeService = beanBasedCascadeService;
    }

    @Override
    public void clear() {
        containerBasedCascadeService.clear();
    }

    @Override
    public String get(String key) {
        String result;
        Connection c = connectionProvider.get();
        PreparedStatement ps = null;
        try {
            ps = c.prepareStatement("select v from TestMap where k=?");
            ps.setString(1, key);
            ResultSet resultSet = ps.executeQuery();
            if (resultSet.next()) {
                result = resultSet.getString(1);
                if (resultSet.next()) {
                    throw new ImplementationException("ResultSet returnerte mer enn en rad");
                }
            } else {
                return null;
            }
        } catch (SQLException e) {
            throw new ImplementationException(e);
        } finally {
            JDBCHelper.close(ps);
        }

        return result;
    }

    @Override
    public String put(String key, String value) {
        if (key == null) {
            throw new ValidationException("key = null er ikke tilladt");
        }

        String oldValue = get(key);
        Connection connection = connectionProvider.get();
        PreparedStatement ps = null;
        try {
            if (oldValue == null) {
                ps = connection.prepareStatement("insert into TestMap values(?,?)");
                ps.setString(1, key);
                ps.setString(2, value);
                int result = ps.executeUpdate();
                if (result != 1) {
                    throw new ImplementationException("Fikk feil update count: " + result);
                }
            } else {
                ps = connection.prepareStatement("update TestMap set v=? where k=?");
                ps.setString(1, value);
                ps.setString(2, key);
                int result = ps.executeUpdate();
                if (result != 1) {
                    throw new ImplementationException("Fikk feil update count: " + result);
                }
            }
        } catch (SQLException e) {
            throw new ImplementationException(e);
        } finally {
            JDBCHelper.close(ps);
        }
        return oldValue;
    }

    /**
     * Tjeneste som har TxRequired som kalder intern put metode direkte og en annen tjeneste som har TxRequired.
     */
    @Override
    public void containerTest1(String key1, String value1, String key2, String value2) {
        put(key1, value1);
        containerBasedCascadeService.put(key2, value2);
    }

    /**
     * TxSupports tjeneste som kalle en annen tjeneste 2 ganger som har TxRequired. Hvert kall skal committes separat     *
     */
    @Override
    public void containerTest2(String key1, String value1, String key2, String value2) {
        containerBasedCascadeService.put(key1, value1);
        containerBasedCascadeService.put(key2, value2);
    }

    /**
     * TxRequired tjeneste som kalle en annen tjeneste som har TxRequired. Kallene skal commites samlet.
     */
    @Override
    public void containerTest3(String key1, String value1, String key2, String value2) {
        containerBasedCascadeService.put(key1, value1);
        containerBasedCascadeService.put(key2, value2);
    }


    /**
     * Tjeneste uten transaction attribute som kalle en annen tjeneste som har TxRequired. Disse 2 kallene skal
     * commites samlet siden TxRequired er default
     */
    @Override
    public void containerTest4(String key1, String value1, String key2, String value2) {
        containerBasedCascadeService.put(key1, value1);
        containerBasedCascadeService.put(key2, value2);
    }

    /**
     * Tjeneste som har TxRequired som kalder intern put metode direkte og en annen tjeneste som har TxRequired.
     */
    @Override
    public void beanTest1(String key1, String value1, String key2, String value2) {
        put(key1, value1);
        beanBasedCascadeService.put(key2, value2);
    }

    /**
     * TxSupports tjeneste som kalle en annen tjeneste 2 ganger som har TxRequired. Hvert kall skal committes separat     *
     */
    @Override
    public void beanTest2(String key1, String value1, String key2, String value2) {
        beanBasedCascadeService.put(key1, value1);
        beanBasedCascadeService.put(key2, value2);
    }

    /**
     * TxRequired tjeneste som kalle en annen tjeneste som har TxRequired. Kallene skal commites samlet.
     */
    @Override
    public void beanTest3(String key1, String value1, String key2, String value2) {
        beanBasedCascadeService.put(key1, value1);
        beanBasedCascadeService.put(key2, value2);
    }

    /**
     * TxRequired tjeneste som tester at indre BMT ejb bruker en egen connection som committes separat. For å teste dette
     * gjøres det to endringer i kallet. Først endring skjer i ytre CMT ejb og andre endring i indre BMT ejb. Når kallet
     * til indre BMT ejb er utført skal ytre CMT ejb kunne lese begge endringene. Dernest kaster ytre CMT en exception som
     * får CMT transaksjonen til å rulle tilbake, men ikke BMT transksjonen som allerede er committet. Det endelige
     * resultatet er at kun endringen fra indre BMT ejb blir igjen i databasen etter kallet.
     */
    @Override
    public void beanTest4(String key1, String value1, String key2, String value2) {
        put(key1, value1);
        beanBasedCascadeService.put(key2, value2);
        if (!value1.equals(get(key1))) {
            throw new ImplementationException("Expected key1=" + value1);
        }
        if (!value2.equals(get(key2))) {
            throw new ImplementationException("Expected key2=" + value2);
        }
        throw new ValidationException("Exception from beanTest4 to force CMT rollback");
    }
}
