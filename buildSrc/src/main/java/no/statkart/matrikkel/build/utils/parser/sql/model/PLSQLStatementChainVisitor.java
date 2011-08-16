package no.statkart.matrikkel.build.utils.parser.sql.model;

/**
 * Chained visitor for type.
 *
 * @author Leif Lislegård
 * @since 1.1
 */
public abstract class PLSQLStatementChainVisitor<H extends PLSQLStatement> extends AbstractChainVisitor<H> implements PLSQLStatementVisitor<H> {
    protected PLSQLStatementChainVisitor(ExpressionVisitor<H> successor) {
        super(successor);
    }
}
