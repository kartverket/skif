package no.statkart.skif.storetest.service.txcascade;

import com.google.inject.Inject;
import com.google.inject.Provider;
import no.statkart.skif.exception.ImplementationException;
import no.statkart.skif.exception.ValidationException;
import no.statkart.skif.storetest.service.txbmt.BeanManagedTxAService;
import no.statkart.skif.storetest.service.txcmt.ContainerManagedTxAService;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

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
        try (PreparedStatement ps = c.prepareStatement("select v from TestMap where k=?")) {
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
        if (oldValue == null) {
            try (PreparedStatement ps = connection.prepareStatement("insert into TestMap values(?,?)")) {
                ps.setString(1, key);
                ps.setString(2, value);
                int result = ps.executeUpdate();
                if (result != 1) {
                    throw new ImplementationException("Fikk feil update count: " + result);
                }
            } catch (SQLException e) {
                throw new ImplementationException(e);
            }
        } else {
            try (PreparedStatement ps = connection.prepareStatement("update TestMap set v=? where k=?")) {
                ps.setString(1, value);
                ps.setString(2, key);
                int result = ps.executeUpdate();
                if (result != 1) {
                    throw new ImplementationException("Fikk feil update count: " + result);
                }
            } catch (SQLException e) {
                throw new ImplementationException(e);
            }
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

}
