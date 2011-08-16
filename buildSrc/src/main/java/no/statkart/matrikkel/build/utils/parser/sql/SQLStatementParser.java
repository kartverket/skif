package no.statkart.matrikkel.build.utils.parser.sql;

import no.statkart.matrikkel.build.utils.parser.sql.model.*;
import no.statkart.matrikkel.build.utils.parser.sql.parser.ParserVisitorFactory;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Statiske funksjoner for parsing av sammensatte sql-filer.
 *
 * Innhold til en sql fil blir parset etter følgende definisjon på LL(*) form
 *
 * <ul>
 *     <li>Expression :: DefaultStatement | PLSQLStatement | Comment </li>
 *     <li>DefaultStatement :: {@code <text>}; </li>
 *
 *     <li>PLSQLStatement :: PLSQLStatement1 | PLSQLStatement2 </li>
 *     <li>PLSQLStatement1 :: "CREATE (OR REPLACE) TRIGGER|FUNCTION|PACKAGE" {@code <text>}/ </li>
 *     <li>PLSQLStatement1 :: "DECLARE|BEGIN" {@code <text>}/ </li>
 *
 *     <li>Comment :: PromptStatement</li>
 *     <li>PromptStatement :: "PROMPT " {@code <text>}\n</li>
 * </ul>
 *
 * @author Leif Lislegård
 * @since 1.1
 */
public class SQLStatementParser {

    public static List<Expression> parseExpressions(LineNumberReader reader) throws IOException {

        reader.setLineNumber(1);    //begynner på linje 1 ...

        if (!reader.markSupported()) {
            throw new RuntimeException("markSupported a requisite for this parser!");
        }

        ArrayList<Expression> expressions = new ArrayList<Expression>();
        ParserVisitorFactory visitorFactory = new ParserVisitorFactory(reader);

        boolean eof = false;

        while (!eof) {
            ExpressionVisitor visitor = visitorFactory.makeVisitor();
            Expression newExpression;


            String nextLine;
            do {
                reader.mark(1024*8);
                nextLine = reader.readLine();
                if (nextLine != null) {
                    if (nextLine.matches("^\\s*--.*")) {  //ignorerer kommentar-linjer
                        nextLine = "";
                    }
                    nextLine = nextLine.trim().toUpperCase();
                }

            } while (nextLine != null && nextLine.isEmpty());   //ignorerer tomme linjer...

            if (nextLine == null) {
                eof = true; continue;
            }

            reader.reset();

            if (nextLine.matches("CREATE(\\s+OR\\s+REPLACE)?\\s+(TRIGGER|FUNCTION|PACKAGE).*")) {
                newExpression = new PLSQLStatement();
            } else if (nextLine.matches("(DECLARE|BEGIN).*")) {
                newExpression = new PLSQLStatement();
            } else if (nextLine.matches("PROMPT .*")) {
                newExpression = new PromptStatement();
            } else {
                newExpression = new DefaultStatement();
            }

            Object o = newExpression.execute(visitor, null);

            if (o != null) {
                expressions.add((Expression) o);
            }

            { //end of file??
                reader.mark(16);
                eof = (reader.read() == -1);
                reader.reset();
            }

        }

        return expressions;
    }


    public static List<Statement> parseStatements(String sqlString) throws IOException {
        LineNumberReader reader = new LineNumberReader(new StringReader(sqlString));

        List<Statement> expressions = new ArrayList<Statement>();
        for (Expression expression : parseExpressions(reader)) {
            if (expression instanceof Statement) {
                expressions.add((Statement) expression);
            }
        }
        return expressions;
    }


    public static List<Statement> parseStatements(File file) throws IOException {
        LineNumberReader reader = new LineNumberReader(new FileReader(file));
        List<Statement> expressions = new ArrayList<Statement>();
        for (Expression expression : parseExpressions(reader)) {
            if (expression instanceof Statement) {
                expressions.add((Statement) expression);
            }
        }
        return expressions;
    }

}
