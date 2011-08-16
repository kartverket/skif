package no.statkart.matrikkel.build.utils.parser.sql.model;

/**
 * Node som representerer et PL/SQL script
 *
 * @author Leif Lislegård
 * @since 1.1
 */
public class PLSQLStatement extends Statement {

    @Override
    public Object execute(ExpressionVisitor<Expression> algorithm, Object param) {
        if (algorithm instanceof PLSQLStatementVisitor) {
            return ((PLSQLStatementVisitor) algorithm).plsqlCase(this, param);
        } else {
            return algorithm.defaultCase(this, param);
        }
    }
}
