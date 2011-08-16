package no.statkart.matrikkel.build.utils.parser.sql.model;

/**
 * Interface for alle noder i AST treet
 *
 * @author Leif Lislegård
 * @since 1.1
 */
public interface Expression {

    Object execute(ExpressionVisitor<Expression> algorithm, Object param);

    public int getLineNumber();
}
