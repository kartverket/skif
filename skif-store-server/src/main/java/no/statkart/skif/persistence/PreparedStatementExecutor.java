package no.statkart.skif.persistence;

import no.statkart.skif.exception.ImplementationException;
import no.statkart.skif.store.BubbleId;
import no.statkart.skif.store.util.StoreJDBCHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

/**
 * Hjelpeklasse for kjøring av prepared statements.
 * <p>
 * Inndata kan typisk være en sql (med parametere) etterfulgt av en valgfri 'in elelemts clause'
 * som beskriver en collection med {@link BubbleId}-er.
 * <p>
 * Ved store collections av id-er, vil denne bli brutt ned i mindre batcher slik at statementet vil bli kjørt flere ganger mot databasen.
 *
 * @author Christian A. Rektorli
 * @author Leif Lislegård
 * @author Tor Egil R. Strand
 * @since 2.1
 */
public abstract class PreparedStatementExecutor {
    private static Logger logger = LoggerFactory.getLogger(PreparedStatementExecutor.class);
    private static Map<Integer, String> parameterlists = new HashMap<>();

    private List<ParameterHelper> customParameters = new ArrayList<>();


    protected abstract void readResult(final ResultSet resultSet) throws SQLException;

    public void execute(Connection connection, String query, final Collection<? extends BubbleId> parameters) {
        execute(connection, query, parameters, "");
    }

    public void execute(Connection connection, String query, final Collection<? extends BubbleId> parameters, String queryEnd) {
        Iterator<? extends BubbleId> iterator = parameters.iterator();

        int size = parameters.size();

        // Ved collection størrelse på 128, 256 eller mer sliter oracle. Deler derfor query opp i biter på 64 eller mindre.
        // For å reduserer antall prepared statements brukes størrelse på 64,32,16,15,...
        for (int i = 6; i >= 0; i--) {
            // Hvis collection har mindre enn 16 elementer tilbake hentes ut alle ved en spørring istedet for å dele opp i mindre biter
            final int length = (size > 0 && size < 16) ? size : (1 << i); //length blir aldri 0 . (1 << 0) == 1

            while (size >= length) {
                size -= length;
                String parameterlist = parameterlists.get(length);
                if (parameterlist == null) {
                    StringBuilder buffer = new StringBuilder("(?");
                    for (int j = 1; j < length; j++) {
                        buffer.append(",?");
                    }
                    buffer.append(")");
                    parameterlist = buffer.toString();
                    parameterlists.put(length, parameterlist);
                }

                String sql = new StringBuilder().append(query).append(parameterlist).append(queryEnd).toString();
                try (PreparedStatement statement = connection.prepareStatement(sql)) {

                    int customParametersSize = 0;
                    for (ParameterHelper parameterHelper : customParameters) {
                        parameterHelper.decorateStatement(++customParametersSize, statement, connection);
                    }

                    for (int index = customParametersSize; iterator.hasNext() && index < length + customParametersSize; index++) {
                        BubbleId bubbleId = iterator.next();
                        StoreJDBCHelper.setBubbleId(statement, index + 1, bubbleId);
                    }

                    try (ResultSet resultSet = statement.executeQuery()) {
                        while (resultSet.next()) {
                            readResult(resultSet);
                        }
                    }
                } catch (SQLException e) {
                    throw new ImplementationException("Query failed: " + e.getMessage(), e, logger);
                }
            }

        }
    }

    public void execute(Connection connection, String query1, String query2, final Collection<? extends BubbleId> parameters, String endQuery) {
        Iterator<? extends BubbleId> iterator = parameters.iterator();

        int size = parameters.size();

        // Ved collection størrelse på 128, 256 eller mer sliter oracle. Deler derfor query opp i biter på 64 eller mindre.
        // For å reduserer antall prepared statements brukes størrelse på 64,32,16,15,...
        for (int i = 6; i >= 0; i--) {
            // Hvis collection har mindre enn 16 elementer tilbake hentes ut alle ved en spørring istedet for å dele opp i mindre biter
            final int length = (size > 0 && size < 16) ? size : (1 << i);

            while (size >= length) {
                size -= length;
                String parameterlist = parameterlists.get(length);
                if (parameterlist == null) {
                    StringBuilder buffer = new StringBuilder("(?");
                    for (int j = 1; j < length; j++) {
                        buffer.append(",?");
                    }
                    buffer.append(")");
                    parameterlist = buffer.toString();
                    parameterlists.put(length, parameterlist);
                }

                String sql = new StringBuilder().append(query1).append(parameterlist).append(query2).append(parameterlist).append(endQuery).toString();
                try (PreparedStatement statement = connection.prepareStatement(sql)) {

                    int customParametersSize = 0;
                    for (ParameterHelper parameterHelper : customParameters) {
                        parameterHelper.decorateStatement(++customParametersSize, statement, connection);
                    }

                    for (int index = customParametersSize; iterator.hasNext() && index < length + customParametersSize; index++) {
                        BubbleId bubbleId = iterator.next();
                        StoreJDBCHelper.setBubbleId(statement, index + 1, bubbleId);
                        StoreJDBCHelper.setBubbleId(statement, index + 1 + length, bubbleId);
                    }

                    try (ResultSet resultSet = statement.executeQuery()) {
                        while (resultSet.next()) {
                            readResult(resultSet);
                        }
                    }
                } catch (SQLException e) {
                    throw new ImplementationException("Query failed: " + e.getMessage(), e, logger);
                }
            }

        }
    }


    public void setDate(int index, Date value) {
        setValueImpl(index, new DateParameter(value, Calendar.getInstance()));
    }

    public void setBoolean(int index, boolean value) {
        setValueImpl(index, new BooleanParameter(value));
    }

    public void setDouble(int index, double value) {
        setValueImpl(index, new DoubleParameter(value));
    }

    public void setFloat(int index, float value) {
        setValueImpl(index, new FloatParameter(value));
    }

    public void setInt(int index, int value) {
        setValueImpl(index, new IntegerParameter(value));
    }

    public void setObject(int index, Object value, int targetSqlType) {
        setValueImpl(index, new ObjectParameter2(value, targetSqlType));
    }

    public void setObject(int index, Object value) {
        setValueImpl(index, new ObjectParameter(value));
    }

    public void setString(int index, String value) {
        setValueImpl(index, new StringParameter(value));
    }


    private void setValueImpl(int index, ParameterHelper parameter) {
        for (int i = customParameters.size(); i < index; i++) {
            customParameters.add(i, null);
        }
        customParameters.set(index - 1, parameter);
    }

    private abstract class ParameterHelper<T> {
        final T value;

        public ParameterHelper(T value) {
            this.value = value;
        }

        abstract void decorateStatement(int index, PreparedStatement statement, Connection connection) throws SQLException;
    }

    /**
     * Setter <tt><b>int</b></tt>-verdi på statement
     *
     * @see java.sql.PreparedStatement#setInt(int, int)
     */
    final class IntegerParameter extends ParameterHelper<Integer> {
        IntegerParameter(Integer value) {
            super(value);
        }

        void decorateStatement(int index, PreparedStatement statement, Connection connection) throws SQLException {
            statement.setInt(index, value);
        }
    }

    /**
     * Setter <tt><b>boolean</b></tt>-verdi på statement
     *
     * @see java.sql.PreparedStatement#setBoolean(int, boolean)
     */
    final class BooleanParameter extends ParameterHelper<Boolean> {
        BooleanParameter(Boolean value) {
            super(value);
        }

        void decorateStatement(int index, PreparedStatement statement, Connection connection) throws SQLException {
            statement.setBoolean(index, value);
        }
    }

    /**
     * Setter <tt><b>double</b></tt>-verdi på statement
     *
     * @see java.sql.PreparedStatement#setDouble(int, double)
     */
    final class DoubleParameter extends ParameterHelper<Double> {
        DoubleParameter(Double value) {
            super(value);
        }

        void decorateStatement(int index, PreparedStatement statement, Connection connection) throws SQLException {
            statement.setDouble(index, value);
        }
    }

    /**
     * Setter <tt><b>float</b></tt>-verdi på statement
     *
     * @see java.sql.PreparedStatement#setFloat(int, float)
     */
    final class FloatParameter extends ParameterHelper<Float> {
        FloatParameter(Float value) {
            super(value);
        }

        void decorateStatement(int index, PreparedStatement statement, Connection connection) throws SQLException {
            statement.setFloat(index, value);
        }
    }

    /**
     * Setter <tt><b>Object</b></tt>-verdi på statement
     *
     * @see java.sql.PreparedStatement#setObject(int, Object)
     */
    final class ObjectParameter extends ParameterHelper<Object> {
        ObjectParameter(Object value) {
            super(value);
        }

        void decorateStatement(int index, PreparedStatement statement, Connection connection) throws SQLException {
            statement.setObject(index, value);
        }
    }

    /**
     * Setter <tt><b>Date</b></tt>-verdi på statement
     *
     * @see java.sql.PreparedStatement#setDate(int, java.sql.Date)
     */
    final class DateParameter extends ParameterHelper<Date> {
        Calendar calendar;
        java.sql.Date cachedDate = null;

        DateParameter(Date value, Calendar calendar) {
            super(value);
            this.calendar = calendar;
        }

        void decorateStatement(int index, PreparedStatement statement, Connection connection) throws SQLException {
            if (cachedDate == null) {
                cachedDate = new java.sql.Date(value.getTime());
            }
            statement.setDate(index, cachedDate, calendar);
        }
    }

    /**
     * Alternativ måte å sette <tt><b>Object</b></tt>-verdi på statement
     *
     * @see java.sql.PreparedStatement#setObject(int, Object, int)
     */
    final class ObjectParameter2 extends ParameterHelper<Object> {
        private int sqltype;

        ObjectParameter2(Object value, int sqltype) {
            super(value);
            this.sqltype = sqltype;
        }

        void decorateStatement(int index, PreparedStatement statement, Connection connection) throws SQLException {
            statement.setObject(index, value, sqltype);
        }
    }

    /**
     * Setter <tt><b>String</b></tt>-verdi på statement
     *
     * @see java.sql.PreparedStatement#setString(int, String)
     */
    final class StringParameter extends ParameterHelper<String> {
        StringParameter(String value) {
            super(value);
        }

        void decorateStatement(int index, PreparedStatement statement, Connection connection) throws SQLException {
            statement.setString(index, value);
        }
    }
}
