package no.statkart.skif.storetest.util.testsupport;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.encoder.PatternLayoutEncoder;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.OutputStreamAppender;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayOutputStream;

/**
 * Slå midlertidig på logging for en gitt klasse.
 * All logging som fant sted mellom start() og stop() kan til enhver tid hentes via output().
 */
public class LogListener {

    private final ByteArrayOutputStream baos = new ByteArrayOutputStream();
    private final PatternLayoutEncoder encoder = new PatternLayoutEncoder();
    private final OutputStreamAppender<ILoggingEvent> appender = new OutputStreamAppender<>();

    public LogListener(String classname) {
        LoggerContext context = (LoggerContext) LoggerFactory.getILoggerFactory();
        encoder.setPattern("%d{HH:mm:ss} %-5level %logger{36} - %msg%n");
        encoder.setContext(context);
        appender.setContext(context);
        appender.setEncoder(encoder);
        appender.setOutputStream(baos);

        Logger logger = (Logger) LoggerFactory.getLogger(classname);
        logger.addAppender(appender);
        logger.setLevel(Level.ALL);
        logger.setAdditive(false);
    }

    public void start() {
        encoder.start();
        appender.start();
    }

    public void stop() {
        appender.stop();
        encoder.stop();
    }

    public String output() {
        return baos.toString();
    }

}
