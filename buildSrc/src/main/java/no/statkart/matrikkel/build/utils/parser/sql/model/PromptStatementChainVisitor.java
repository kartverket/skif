package no.statkart.matrikkel.build.utils.parser.sql.model;

/**
 * Type for kjedet visitor.
 *
 * @author Leif Lislegård
 * @since 1.1
 */
public abstract class PromptStatementChainVisitor<H extends PromptStatement> extends AbstractChainVisitor<H> implements PromptStatementVisitor<H> {

    public PromptStatementChainVisitor(ExpressionVisitor<H> successor) {
        super(successor);
    }
}
