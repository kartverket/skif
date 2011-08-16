package no.statkart.matrikkel.build.utils.parser.sql.model;

/**
 * @author Leif Lislegård
 * @since 1.1
 */
public abstract class Statement extends AbstractExpression {

    protected String sql;


    public String getSql() {
        return sql;
    }

    public void setSql(String sql) {
        this.sql = sql;
    }
}
