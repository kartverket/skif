package no.statkart.matrikkel.build.utils.parser.sql.model;

/**
 * Visitor interface for type
 *
 * @author Leif Lislegård
 * @since 1.1
 */
public interface PromptStatementVisitor<T extends PromptStatement> extends ExpressionVisitor<T> {
    Object promptCase(T host, Object param);
}
