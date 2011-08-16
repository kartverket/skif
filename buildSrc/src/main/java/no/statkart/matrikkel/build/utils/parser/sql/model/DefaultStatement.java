package no.statkart.matrikkel.build.utils.parser.sql.model;


/**
 * Node som enkapsulerer standard sql-setninger (default)
 *
 * @author Leif Lislegård
 * @since 1.1
 */
public class DefaultStatement extends Statement {


    @Override
    public Object execute(ExpressionVisitor<Expression> algorithm, Object param) {
        if (algorithm instanceof DefaultStatementVisitor) {
            return ((DefaultStatementVisitor) algorithm).defaultStatementCase(this, param);
        } else {
            return algorithm.defaultCase(this, param);
        }
    }
}
