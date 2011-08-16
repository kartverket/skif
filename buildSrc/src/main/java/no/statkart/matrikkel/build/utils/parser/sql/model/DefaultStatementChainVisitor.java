package no.statkart.matrikkel.build.utils.parser.sql.model;

/**
 * Type for kjedet visitor.
 *
 * @author Leif Lislegård
 * @since 1.1
 */
public abstract class DefaultStatementChainVisitor<H extends DefaultStatement> extends AbstractChainVisitor<H> implements DefaultStatementVisitor<H> {

    protected DefaultStatementChainVisitor(ExpressionVisitor<H> successor) {
        super(successor);
    }

}
