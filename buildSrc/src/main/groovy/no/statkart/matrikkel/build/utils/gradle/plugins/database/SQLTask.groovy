package no.statkart.matrikkel.build.utils.gradle.plugins.database

import groovy.sql.Sql
import org.gradle.api.DefaultTask
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.TaskAction
import no.statkart.matrikkel.build.utils.parser.sql.SQLStatementParser
import no.statkart.matrikkel.build.utils.parser.sql.model.Statement

/**
 * Task for executing av statements over JDBC.
 *
 *
 * For at denne tasken kan fungere, må jdbc driveren finnes i classpath og være registrert i kjørende classloader.
 * Registrering av denne i Gradle kan gjøres på følgende måte:
 * <pre><code>
 Class driver = loader.loadClass('oracle.jdbc.OracleDriver')

 // You might need one or both of these as well
 Driver instance = driver.newInstance()
 DriverManager.registerDriver(instance)

 * </code></pre>
 * @author Leif Lislegård
 * @since 1.1
 */
public class SQLTask extends DefaultTask {

    @Input
    def convention

    @Input
    File sqlFile

    @TaskAction
    def exec() {
//        println "creds: ${convention.credentials.username} / ${convention.credentials.password}"

        logger.info("parsing statements from file: ${sqlFile}")

        def sql = Sql.newInstance(convention.url, convention.credentials.username, convention.credentials.password, convention.driver)
        println("connected to database: ${sql.connection.metaData.URL} [${sql.connection.metaData.userName}]")

        SQLStatementParser.parseStatements(sqlFile).each() { Statement statement ->

            logger.info("Executing ${statement.class.simpleName}: \n${statement.sql}")

            sql.execute(statement.sql)
        }


    }




}
