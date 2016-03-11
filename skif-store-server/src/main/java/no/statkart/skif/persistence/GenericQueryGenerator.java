package no.statkart.skif.persistence;

import no.statkart.skif.domain.SelectionPolygon;
import no.statkart.skif.exception.ImplementationException;
import no.statkart.skif.store.BubbleId;
import no.statkart.skif.store.util.StoreJDBCHelper;
import no.statkart.skif.util.JDBCHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

/**
 * Denne klassen generer en sql-spørring ut fra brukerdefinerte parametere og evt subspørringer, som igjen kan være
 * nøstet.  Spørringen bygges ved å angi seleksjon, tabeller og kriterier - samt subspørringer.
 * GenericQueryGenerator generator = new GenericQueryGenerator("m.id, m.class, ...", "matrikkelenhet m");<br>
 * generator.addWhereClause("m.utgatt=", 0);<br>
 * generator.addWhereClause("m.gardsnr=", 133);<br>
 * generator.addSubquery("m.id", GenericQueryGenerator.UNION, Arrays.asList(tegenerator, itegenerator));<br>
 * PreparedStatement pstmt = generator.prepareStatement(connection);<br>
 * Dette vil så gi en tilsvarende sql-setning: <br>
 * select m.id, m.class, ... from matrikkelenhet m where m.utgatt=0 and m.gardsnr=133 and m.id in( select ... union select ...)<br>
 *
 * @author Christian A. Rektorli
 * @author Tor Egil R. Strand
 * @since 2.1
 */
public class GenericQueryGenerator {
    private static Logger logger = LoggerFactory.getLogger(GenericQueryGenerator.class);
    protected Connection connection;

    private int srid;

    private static abstract class TableOrInlineView {

        public boolean isTable() {
            return this instanceof Table;
        }

        public abstract String toTableOrInlineViewString(List<Object> parameters);
    }

    public static class Table extends TableOrInlineView {
        private String table;

        public Table(String table) {
            this.table = table;
        }

        public String toTableOrInlineViewString(List<Object> parameters) {
            return table;
        }
    }

    public static class InlineView extends TableOrInlineView {
        private GenericQueryGenerator inlineView;
        private String alias;

        public InlineView(GenericQueryGenerator inlineView) {
            this(inlineView, null);
        }

        public InlineView(GenericQueryGenerator inlineView, String alias) {
            this.inlineView = inlineView;
            this.alias = alias;
        }

        public String toTableOrInlineViewString(List<Object> parameters) {
            StringBuilder buf = inlineView.createQueryString(parameters);
            buf.insert(0, "(");
            buf.append(")");
            if (alias != null) {
                buf.append(" ").append(alias);
            }
            return buf.toString();
        }
    }


    /**
     * Kolonner som skal med i seleksjon
     */
    private List<String> projection = new ArrayList<>();
    /**
     * Tabeller som skal med i spørringen
     */
    private List<TableOrInlineView> tablesOrInlineViews = new ArrayList<>();
    /**
     * Seleksjonsoperator for seleksjonsbetingelse, default er "and" men det er mulig å angi "or"
     */
    private List<String> selectionOperator = new ArrayList<>();
    /**
     * Første del av en seleksjonsbetingelse
     */
    private List<String> selection1 = new ArrayList<>();
    /**
     * Evt. andre del av en seleksjonsbetingelse
     */
    private List<String> selection2 = new ArrayList<>();
    /**
     * Parametre til seleksjonsbetingelsene
     */
    private List<Object> parameters = new ArrayList<Object>();
    /**
     * Evt ordning av tupler
     */
    private List<String> orderby = new ArrayList<>();
    /**
     * Evt hints for opimizeren
     */
    private List<String> hints = new ArrayList<>();

    /**
     * Tree av subspørringer
     */
    private List<String> subqueryKey = new ArrayList<>();
    private List<GenericQueryGeneratorOperation> subqueryOp = new ArrayList<>();
    private List<List<GenericQueryGenerator>> subquery = new ArrayList<>();


    private int maxRowCount = -1;
    private String maxRowCountOrderBy;

    public GenericQueryGenerator(String select, String from, String where) {
        this.projection.add(select);
        this.tablesOrInlineViews.add(new Table(from));
        addSelectionImpl(where, null, null);
    }

    public GenericQueryGenerator(String select, String from) {
        this.projection.add(select);
        this.tablesOrInlineViews.add(new Table(from));
    }

    public GenericQueryGenerator(String select, InlineView inlineView) {
        this.projection.add(select);
        this.tablesOrInlineViews.add(inlineView);
    }

    public GenericQueryGenerator(String select) {
        this.projection.add(select);
    }

    public GenericQueryGenerator(Connection connection) {
        this.connection = connection;
    }


    public GenericQueryGenerator(String key, GenericQueryGeneratorOperation operation, GenericQueryGenerator... subqueries) {
        addSubquery(key, operation, subqueries);
    }

    public int getSrid() {
        return srid;
    }

    public void setSrid(int srid) {
        this.srid = srid;
    }

    public void addColumn(String column) {
        this.projection.add(column);
    }

    public void addTable(String table) {
        this.tablesOrInlineViews.add(new Table(table));
    }

    public void addInlineView(InlineView inlineView) {
        this.tablesOrInlineViews.add(inlineView);
    }

    public void addHint(String hint) {
        this.hints.add(hint);
    }

    /**
     * Legger til en tekst som seleksjon.
     *
     * @param string kolonnenavn
     */
    public void addSelection(String string) {
        addSelectionImpl(string, null, null);
    }

    /**
     * Legger til en tekst som seleksjon.
     *
     * @param string    kolonnenavn
     * @param parameter parameter
     */
    public void addSelection(String string, String parameter) {
        addSelectionImpl(string, null, parameter);
    }

    /**
     * Legger til en begynnende og avsluttende tekst som seleksjon.
     *
     * @param string1   kolonnenavn - f.eks <code>upper(</code>
     * @param string2   kolonnenavn - f.eks <code>)</code>
     * @param parameter parameter
     */
    public void addSelection(String string1, String string2, String parameter) {
        addSelectionImpl(string1, string2, parameter);
    }

    /**
     * Legger til et heltall som seleksjon.
     *
     * @param string    kolonnenavn
     * @param parameter parameter
     */
    public void addSelection(String string, Integer parameter) {
        addSelectionImpl(string, null, parameter);
    }

    /**
     * Legger til et heltall som seleksjon.
     *
     * @param string    kolonnenavn
     * @param parameter parameter
     */
    public void addSelection(String string, Long parameter) {
        addSelectionImpl(string, null, parameter);
    }

    /**
     * Legger til et flyttall som seleksjon.
     *
     * @param string    kolonnenavn
     * @param parameter parameter
     */
    public void addSelection(String string, Double parameter) {
        addSelectionImpl(string, null, parameter);
    }

    /**
     * Legger til en dato som seleksjon.
     *
     * @param string kolonnenavn
     * @param date   parameter
     */
    public void addSelection(String string, Date date) {
        addSelectionImpl(string, null, date);
    }

    /**
     * Legger til en bubbleId som seleksjon.
     *
     * @param string    kolonnenavn
     * @param parameter parameter
     */
    public void addSelection(String string, BubbleId parameter) {
        addSelectionImpl(string, null, parameter);
    }

    public void addSelection(String string, List objects) {
        addSelectionImpl(string, null, objects);
    }

    /**
     * Legger til et søkepolygon som seleksjon.
     *
     * @param string
     * @param selectionPolygon
     */
    public void addSelection(String string, SelectionPolygon selectionPolygon) {
       addSelectionImpl(string, null, selectionPolygon);
    }



    /**
     * Legger til en "or" seleksjon med begyndende og avsluttende tekst.
     *
     * @param string1
     * @param string2   kan være null
     * @param parameter kan være null
     */
    public void addORSelection(String string1, String string2, Object parameter) {
        addSelectionImpl("or", string1, string2, parameter);
    }

    private void addSelectionImpl(String string1, String string2, Object parameter) {
        addSelectionImpl("and", string1, string2, parameter);
    }

    public void addSelectionImpl(String op, String string1, String string2, Object parameter) {
        this.selectionOperator.add(op);
        this.selection1.add(string1);
        this.selection2.add(string2);
        this.parameters.add(parameter);
    }

    /**
     * Legger til en ordning av tupler.
     *
     * @param string
     */
    public void addOrderBy(String string) {
        this.orderby.add(string);
    }


    /**
     * Lager et {@link java.sql.PreparedStatement PreparedStatement} og binder variablene til denne.
     *
     * @param connection connection som skal komilere spørringen
     * @return spørring som skal kjøres
     * @throws java.sql.SQLException ved evt feil i spørringen
     */
    public PreparedStatement prepareStatement(Connection connection) throws SQLException {
        List<Object> parameters = new ArrayList<Object>();
        StringBuilder queryString = createQueryString(parameters);
        PreparedStatement pstmt = connection.prepareStatement(queryString.toString());

        logger.debug(queryString.toString());
        insertParameters(pstmt, parameters);
        return pstmt;
    }

    private StringBuilder createQueryString(List<Object> parameters) {
        StringBuilder buffer = new StringBuilder();
        if (maxRowCount >= 0) {
            buffer.append("select * from ( ");
        }

        if (projection.size() > 0) {
            buffer.append("select");

            // FIRST_ROWS virker noen ganger mot sin hensikt når man ikke bruker count(*)
            //if( maxRowCount > 0 ) {
            //   hints.add("FIRST_ROWS(" + maxRowCount + ")");
            //}

            if (hints.size() > 0) {
                buffer.append(" /*+ ");
                for (String hint : hints) {
                    buffer.append(hint).append(" ");
                }
                buffer.append("*/");
            }
            buffer.append(" ").append(projection.get(0));
            for (int i = 1; i < projection.size(); i++) {
                buffer.append(", ").append(projection.get(i));
            }

            buffer.append(" from ").append(tablesOrInlineViews.get(0).toTableOrInlineViewString(parameters));
            for (int i = 1; i < tablesOrInlineViews.size(); i++) {
                buffer.append(", ").append(tablesOrInlineViews.get(i).toTableOrInlineViewString(parameters));
            }

            if (selection1.size() > 0) {
                buffer.append(" where ");
                if (parameters != null) {
                    appendSelectionClause(buffer, null, selection1.get(0), selection2.get(0), this.parameters.get(0), parameters);
                    for (int i = 1; i < this.selection1.size(); i++) {
                        appendSelectionClause(buffer, selectionOperator.get(i), selection1.get(i), selection2.get(i), this.parameters.get(i), parameters);
                    }
                } else {
                    appendSelectionClause(buffer, null, selection1.get(0), selection2.get(0), this.parameters.get(0));
                    for (int i = 1; i < this.selection1.size(); i++) {
                        appendSelectionClause(buffer, selectionOperator.get(i), selection1.get(i), selection2.get(i), this.parameters.get(i));
                    }
                }
            }


        }

        for (int i = 0; i < subquery.size(); i++) {
            List<GenericQueryGenerator> list = subquery.get(i);
            GenericQueryGeneratorOperation operation = subqueryOp.get(i);
            String key = subqueryKey.get(i);

            if (list != null && list.size() > 0) {
                if (key != null) {
                    if (selection1.size() == 0) {
                        buffer.append(" where ");
                    } else {
                        buffer.append(" and ");
                    }
                    buffer.append(key).append(" in (").append(list.get(0).createQueryString(parameters));
                    for (int j = 1; j < list.size(); j++) {
                        buffer.append(" ").append(operation.getOperation()).append(" ").append(list.get(j).createQueryString(parameters));
                    }
                    buffer.append(")");
                } else {
                    buffer.append(" (").append(list.get(0).createQueryString(parameters));
                    for (int j = 1; j < list.size(); j++) {
                        buffer.append(" ").append(operation.getOperation()).append(" ").append(list.get(j).createQueryString(parameters));
                    }
                    buffer.append(")");
                }
            }
        }

        if (orderby.size() > 0) {
            buffer.append(" order by ").append(orderby.get(0));
            for (int i = 1; i < orderby.size(); i++) {
                buffer.append(", ").append(orderby.get(i));
            }
        }

        if (maxRowCount > 0) {
            // NB: maxRowCount må være en del av sql strengen. Kan gir meget dårlig performance (16 millis vs 2 sek) hvis det er en parameter. Se API-914
            buffer.append(") where rownum <= ").append(maxRowCount);
            if (maxRowCountOrderBy != null) {
                buffer.append(" order by ").append(maxRowCountOrderBy);
            }
        }

        return buffer;
    }

    private void appendSelectionClause(StringBuilder buffer, String operator, String where1, String where2, Object parameter, List<Object> parameters) {
        boolean prefix = (operator != null);

        if (parameter instanceof List) {
            List<?> list = (List<?>) parameter;
            StringBuilder buffer_ = new StringBuilder("(?");
            for (int i = 1; i < list.size(); i++) {
                buffer_.append(",?");
            }
            buffer_.append(")");
            if (prefix) {
                buffer.append(" ").append(operator).append(" ");
            }
            buffer.append(where1).append(buffer_);
            parameters.addAll(list);

        } else if( parameter instanceof SelectionPolygon ) {
            OracleSDOStructBuilder sdoStructBuilder = OracleSDOStructBuilder
                    .from((SelectionPolygon) parameter)
                    .setConnection(connection)
                    .setSrid(srid);

            if (prefix) {
                buffer.append(" ").append(operator).append(" ");
            }
            buffer.append("mdsys.sdo_relate(").append(where1).append(",?,'mask=anyinteract')='TRUE'");
            if (where2 != null) {
                buffer.append(where2);
            }

            Object struct = sdoStructBuilder.build();
            parameters.add(struct);

        } else {

            if (prefix) {
                buffer.append(" ").append(operator).append(" ");
            }
            buffer.append(where1);
            if (parameter != null) {
                buffer.append("?");
                parameters.add(parameter);
            }
            if (where2 != null) {
                buffer.append(where2);
            }
        }
    }

    private void appendSelectionClause(StringBuilder buffer, String operator, String where1, String where2, Object parameter) {
        boolean prefix = (operator != null);
        if (prefix) {
            buffer.append(" ").append(operator).append(" ");
        }
        if (parameter instanceof List) {
            List list = (List) parameter;
            Iterator it = list.iterator();
            buffer.append(where1).append(" (").append(it.next().toString());
            while (it.hasNext()) {
                buffer.append(", ").append(it.next().toString());
            }
            buffer.append(")");

        } else {
            buffer.append(where1);
            if (parameter != null) {
                if (parameter instanceof CharSequence) {
                    buffer.append("'").append(parameter.toString()).append("'");
                } else if (parameter instanceof Date) {
                    Date date = (Date) parameter;
                    buffer.append("'").append(date.toString()).append("'");
                } else {
                    buffer.append(parameter.toString());
                }
            }
            if (where2 != null) {
                buffer.append(where2);
            }
        }
    }

    /**
     * Bygger en sql-spørring.
     *
     * @return sql-sqpørring.
     */
    public String createStatement() {
        return createQueryString(null).toString();
    }

    /**
     * Legger til en subspørring.
     *
     * @param key        nøkkel for seleksjonsoperator
     * @param operation  relasjon mellom subspørringer (ved flere)
     * @param generators selve spørringene
     */
    public void addSubquery(String key, GenericQueryGeneratorOperation operation, GenericQueryGenerator... generators) {
        subquery.add(Arrays.asList(generators));
        subqueryOp.add(operation);
        subqueryKey.add(key);
    }

    public void addSubquery(String key, GenericQueryGenerator generator) {
        subquery.add(Arrays.asList(generator));
        subqueryOp.add(GenericQueryGeneratorOperation.NULL);
        subqueryKey.add(key);
    }

    /**
     * Binder parametre til den kompilerte spørringen.
     *
     * @param statement prepared statement
     * @param parameters parameterliste
     * @throws java.sql.SQLException ved feil i samtale med database
     */
    private void insertParameters(PreparedStatement statement, List<Object> parameters) throws SQLException {
        for (int i = 0; i < parameters.size(); i++) {
            Object parameter = parameters.get(i);
            logger.debug("Parameter: " + i + "=" + parameter);

            if (parameter instanceof Integer) {
                statement.setInt(i + 1, (Integer) parameter);
            } else if (parameter instanceof Double) {
                statement.setDouble(i + 1, (Double) parameter);
            } else if (parameter instanceof Float) {
                statement.setFloat(i + 1, (Float) parameter);
            } else if (parameter instanceof String) {
                statement.setString(i + 1, (String) parameter);
            } else if (parameter instanceof StringBuilder) {
                statement.setString(i + 1, parameter.toString());
            } else if (parameter instanceof Long) {
                statement.setLong(i + 1, (Long) parameter);
            } else if (parameter instanceof Date) {
                statement.setDate(i + 1, new java.sql.Date(((Date) parameter).getTime()));
            } else if (parameter instanceof BubbleId) {
                StoreJDBCHelper.setBubbleId(statement, i + 1, (BubbleId) parameter);
            } else if (parameter instanceof java.sql.Struct) {
                statement.setObject(i + 1, parameter, java.sql.Types.STRUCT);
            } else {
                throw new ImplementationException("Can not map parameter of type " + parameter.getClass() + " to bind variable", logger);
            }
        }

    }

    /**
     * Setter det maksimale antallet rekker i resultatsettet
     *
     * @param maxRowCount
     */
    public void setMaxResults(int maxRowCount) {
        this.maxRowCount = maxRowCount;
    }

    public void setMaxResultsOrderBy(String s) {
        this.maxRowCountOrderBy = s;
    }


    /**
     * Helpemetode som kun finner antall returnerte rader utifra generatoren.
     *
     * @param connection database connection
     * @param prepared bestemmer om man skal bruke prepared statement eller dynamisk sql.
     * @return antall rader
     */
    public long findAntall(Connection connection, boolean prepared) {

        GenericQueryGenerator generator = new GenericQueryGenerator("count(*)");
        generator.addTable("");
        generator.addSubquery(null, null, this);

        PreparedStatement pstmt = null;
        Statement stmt = null;
        ResultSet result = null;
        try {
            if (prepared) {
                pstmt = generator.prepareStatement(connection);
                result = pstmt.executeQuery();
            } else {
                stmt = connection.createStatement();
                result = stmt.executeQuery(generator.createStatement());
            }

            result.next();
            return result.getLong(1);

        } catch (SQLException e) {
            String sql = (prepared) ? generator.createQueryString(parameters).toString() : generator.createStatement();
            logger.error("Generic count query failed, SQL was: " + sql, e);
            throw new ImplementationException("Error occured querying database: " + e.getMessage(), e, logger);
        } finally {
            if (prepared) {
                JDBCHelper.close(result, pstmt);
            } else {
                JDBCHelper.close(result, stmt);
            }
        }
    }


}
