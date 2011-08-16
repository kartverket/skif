package no.statkart.matrikkel.build.utils.parser.sql.model;


/**
 * Felles funksjonalitet for alle noder.
 *
 * @author Leif Lislegård
 * @since 1.1
 */
public abstract class AbstractExpression implements Expression {

    int lineNumber;

    public int getLineNumber() {
        return lineNumber;
    }

    public void setLineNumber(int lineNumber) {
        this.lineNumber = lineNumber;
    }
}
