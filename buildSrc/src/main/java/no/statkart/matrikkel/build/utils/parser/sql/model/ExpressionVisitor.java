package no.statkart.matrikkel.build.utils.parser.sql.model;


/**
 * Visitor interface for type
 *
 * @author Leif Lislegård
 * @since 1.1
 */
public interface ExpressionVisitor<H extends Expression> {

    public Object defaultCase(H host, Object param);

}

