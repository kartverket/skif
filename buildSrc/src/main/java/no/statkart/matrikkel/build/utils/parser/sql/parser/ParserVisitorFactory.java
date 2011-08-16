package no.statkart.matrikkel.build.utils.parser.sql.parser;

import no.statkart.matrikkel.build.utils.parser.sql.model.*;
import no.statkart.matrikkel.build.utils.parser.sql.model.factory.AbstractParserVisitorFactory;

import java.io.IOException;
import java.io.LineNumberReader;
import java.util.ArrayList;
import java.util.Collections;

/**
 * Konkret implementasjon av parser.
 *
 * Et tenkt tilfelle kan være en spesifik versjon av SOSI, eks "SOSI3_2ParserVisitorFactory".
 *
 * @author Leif Lislegård
 * @since 1.1
 */
public class ParserVisitorFactory extends AbstractParserVisitorFactory {

    /**
     * Factory chain der første element er siste ledd i hht chain of responsibility pattern.
     */
    private ArrayList<AbstractParserVisitorFactory> factories = new ArrayList<AbstractParserVisitorFactory>();

    public ParserVisitorFactory(LineNumberReader reader) {
        super(reader);
        factories.add(new PLSQLStatementFactory(reader));
        factories.add(new PromptStatementFactory(reader));
        factories.add(new DefaultStatementFactory(reader));

        Collections.reverse(factories);
    }



    @Override
    public ExpressionVisitor makeVisitor() {
        return makeVisitorImpl(null);
    }

    @Override
    public ExpressionVisitor makeChainedVisitor(ExpressionVisitor successor) {
        return makeVisitorImpl(successor);
    }

    private ExpressionVisitor makeVisitorImpl(ExpressionVisitor successor) {
        ExpressionVisitor next = successor;
        for (AbstractParserVisitorFactory factory : factories) {
            if (next == null) {
                next = factory.makeVisitor();
            } else {
                next = factory.makeChainedVisitor(next);
            }
        }
        return next;
    }


    private class DefaultStatementFactory extends AbstractParserVisitorFactory {
        private DefaultStatementFactory(LineNumberReader reader) {
            super(reader);
        }

        @Override
        public ExpressionVisitor makeVisitor() {
            return new DefaultStatementVisitor() {

                @Override
                public Object defaultStatementCase(DefaultStatement host, Object param) {
                    host.setLineNumber(reader.getLineNumber());
                    StringBuilder sql = new StringBuilder();

                    int current = 0;
                    do {
                        try {
                            current = reader.read();
                        } catch (IOException e) {
                            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
                        }

                        switch (current) {
                            case -1: {
                                break;
                            }
                            case (int) ';': {
//                                sql.append(';');     //TODO: Denne er kommentert ut da vi fikk feil ved kjøring av sql-filer. Både INSERT statements og CREATE
                                current = -1;
                                break;
                            }
                            case (int) '-': {
                                if (sql.length() != 0 && sql.charAt(sql.length()-1) == '-') {
                                    //kommentarer strippes vekk..
                                    sql = sql.length() > 1 ? new StringBuilder(sql.subSequence(0, sql.length()-2)) : new StringBuilder();
                                    scanner.getInput("\n", false);  //skips the rest of this line
                                    current = (int) ' ';
                                }
                            }
                            default: {
                                sql.append((char) current);
                            }
                        }


                    } while (current != -1);

                    host.setSql(sql.toString());
                    return host;
                }

                @Override
                public Object defaultCase(Expression host, Object param) {
                    if (host instanceof DefaultStatement) {
                        return defaultStatementCase((DefaultStatement) host, param);
                    }
                    throw new IllegalArgumentException("wrong token");
                }
            };
        }

        @Override
        public ExpressionVisitor makeChainedVisitor(ExpressionVisitor successor) {
            return new DefaultStatementChainVisitor(successor) {

                @Override
                public Object defaultStatementCase(DefaultStatement host, Object param) {
                    String sql = scanner.getInput(";", true);
                    host.setSql(sql);
                    return host;
                }

                @Override
                public Object defaultCase(Expression host, Object param) {
                    throw new IllegalArgumentException("wrong token");
                }
            };
        }
    }


    private class PLSQLStatementFactory extends AbstractParserVisitorFactory {
        private PLSQLStatementFactory(LineNumberReader reader) {
            super(reader);
        }

        @Override
        public ExpressionVisitor makeVisitor() {
            return new PromptStatementVisitor() {
                @Override
                public Object promptCase(PromptStatement host, Object param) {
                    throw new RuntimeException("not implemented");
                }

                @Override
                public Object defaultCase(Expression host, Object param) {
                    throw new RuntimeException("shoult not happen!");
                }
            };
        }

        @Override
        public ExpressionVisitor makeChainedVisitor(final ExpressionVisitor successor) {
            return new PLSQLStatementChainVisitor(successor) {
                @Override
                public Object plsqlCase(PLSQLStatement host, Object param) {
                    host.setLineNumber(reader.getLineNumber());
                    String sql = scanner.getInput("/", false);
                    host.setSql(sql);
                    return host;
                }
            };
        }
    }


    private class PromptStatementFactory extends AbstractParserVisitorFactory {
        private PromptStatementFactory(LineNumberReader reader) {
            super(reader);
        }

        @Override
        public ExpressionVisitor makeVisitor() {
            return new PromptStatementVisitor() {
                @Override
                public Object promptCase(PromptStatement host, Object param) {
                    throw new RuntimeException("not implemented");
                }

                @Override
                public Object defaultCase(Expression host, Object param) {
                    throw new RuntimeException("shoult not happen!");
                }
            };
        }

        @Override
        public ExpressionVisitor makeChainedVisitor(final ExpressionVisitor successor) {
            return new PromptStatementChainVisitor(successor) {
                @Override
                public Object promptCase(PromptStatement host, Object param) {
                    host.setLineNumber(reader.getLineNumber());
                    try {
                        String text = reader.readLine();
                        host.setText(text);
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                    return host;
                }
            };
        }
    }
}
