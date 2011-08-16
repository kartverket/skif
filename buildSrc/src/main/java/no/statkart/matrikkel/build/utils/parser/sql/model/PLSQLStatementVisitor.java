package no.statkart.matrikkel.build.utils.parser.sql.model;

/**
 * Visitor interface for type
 *
 * @author Leif Lislegård
 * @since 1.1
 */
public interface PLSQLStatementVisitor<T extends PLSQLStatement> extends ExpressionVisitor<T> {
    Object plsqlCase(T host, Object param);
}
