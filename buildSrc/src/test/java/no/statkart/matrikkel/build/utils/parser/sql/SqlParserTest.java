package no.statkart.matrikkel.build.utils.parser.sql;

import no.statkart.matrikkel.build.utils.parser.sql.model.Expression;

import java.io.LineNumberReader;
import java.io.StringReader;
import java.util.List;

/**
 * Manuell test for parsing av sql-setninger
 *
 * @author Leif Lislegård
 * @since 1.1
 */
public class SqlParserTest {


    public static void main(String... args) throws Exception {


        String sqlString = "select * from dual;\n\ncreate or  replace function blah;/\nselect * from test;  \n\n create or replace trigger TTTTT dddd; blah; / hehe";

        String plsqlString = "--slett;\nselect --slett;\n * from dual; PROMPT purging tables for schema ${oracle_db_schema}\n" +
                "BEGIN\n" +
                "  FOR i IN (SELECT table_name FROM all_tables where owner = '${oracle_db_schema}')\n" +
                "    LOOP\n" +
                "        EXECUTE IMMEDIATE('DROP TABLE ${oracle_db_schema}.' || i.table_name || ' CASCADE CONSTRAINTS PURGE');\n" +
                "    END LOOP;\n" +
                "END;\n" +
                "/";

        StringReader stringReader = new StringReader(plsqlString);

        LineNumberReader reader = new LineNumberReader(stringReader);


        List<Expression> list = SQLStatementParser.parseExpressions(reader);

        int debuggy = 0;

        //
    }
}
