package no.statkart.matrikkel.build.utils.parser.sql.model;

/**
 * Visitor interface for type
 *
 * @author Leif Lislegård
 * @since 1.1
 */
public interface DefaultStatementVisitor<T extends DefaultStatement> extends ExpressionVisitor<T> {

    Object defaultStatementCase(T host, Object param);

}
