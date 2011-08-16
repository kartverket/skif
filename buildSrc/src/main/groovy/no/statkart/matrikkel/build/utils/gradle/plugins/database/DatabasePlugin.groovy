package no.statkart.matrikkel.build.utils.gradle.plugins.database

import org.gradle.api.Project
import org.gradle.api.Plugin

import org.apache.commons.lang.NotImplementedException
import org.gradle.api.tasks.Copy
import no.statkart.matrikkel.build.utils.gradle.plugins.database.oracle.OracleTasks
import no.statkart.matrikkel.build.utils.gradle.plugins.database.oracle.OracleDatabaseConvention
import java.sql.Driver
import java.sql.DriverManager
import groovy.text.SimpleTemplateEngine
import org.apache.tools.ant.filters.ReplaceTokens
import org.gradle.api.logging.LogLevel
import org.apache.tools.ant.filters.ExpandProperties

/**
 * Gradle plugin for database-moduler.
 *
 * <p>
 *     <code>apply plugin: no.statkart.matrikkel.build.utils.gradle.plugins.database.DatabasePlugin</code> kobler inn denne pluginen.
 * <p>
 *     En modul kan betjene flere databaser samtidig. Disse blir satt opp via egne *Convention instanser.
 *
 *     @see DatabaseConvention
 */
class DatabasePlugin implements Plugin<Project>  {

    def void apply(Project project) {
        project.convention.plugins.db = new DatabaseConvention(project)
        configureTaskBuildSQL(project, 'buildSQL', 'Filtrerer og bygger *.sql filer')
    }


    private def configureTaskBuildSQL(Project project, String name, String description) {
        project.task([type: Copy, description: description], name) {
//            group = groupString

            from('src') {
                include = '**/sql/**/*.sql'
            }

            destinationDir = project.buildDir

            outputs.upToDateWhen { false }  //skal alltid kjøre denne task uansett!


            // late binding enables use of any conventional properties potentially defined by plugins
            doFirst() {
                Properties props = new Properties()
                project.properties.each {
                    if (it.value instanceof CharSequence || it.value instanceof Number || it.value instanceof Boolean) {
                        props.setProperty(it.key, String.valueOf(it.value))
                    }
                }

                filter([tokens: props, beginToken: '@', endToken: '@'], ReplaceTokens)

                if (logger.isEnabled(LogLevel.DEBUG)) {
                    logger.debug('substitution properties:')
                    props.sort().each {
                        logger.debug(it.key + ' -> ' + it.value)
                    }
                }

//                expand() virker ikke på større filer... kommenterer derfor denne ut her...
//
//                // Substitute property references in files
//                expand(project.properties)
            }
        }
    }

}

/**
 * Konfigurasjon skjer i {@link DatabaseConvention#configureDatabasePlugin(Closure)}
 *
 *
 * Dette kan eksempelvis gjøres via:
 * <pre><code>
 *        useDrivers libraries.ojdbc
 *        useDrivers libraries.db2_jdbc
 * </code></pre>
 */
class DatabaseConvention {
    private Project project;
    private Set<String> loadedDrivers = new HashSet<String>();

    public String username
    public String password

    public final Map<String, ?> env = new HashMap<String, Object>()

    DatabaseConvention(Project project) {
        this.project = project
    }

    /**
     * Configures this plugin by running closure defined in your project.
     *
     * Configuration methods available:
     * <ul>
     *     <li> {@link #useToolset(String, String, String, Closure)}
     *     <li> {@link #useDrivers(Object)}
     * </ul>
     */
    void configureDatabasePlugin(Closure closure) {
        closure.delegate = this
        closure()

    }

    public Map<String, Object> getEnvironments() {
        return env
    }

    def useToolset(String type, String prefix, String path, Closure closure) {

        project.logger.info("Adding ${type} toolset for ${prefix} (${path})...")

        if ('oracle'.equalsIgnoreCase(type)) {
            return addOracleToolset(type, prefix, path, closure)

        } else {
            throw new NotImplementedException("Kun støttet for oracle...")
        }
    }

    /**
     *  For å kunne benytte jdbc funksjonalitet, må jdbc klasser være lastet inn i classloader til gradle/groovy.
     */
    def useDrivers(def dependencies) {
        URLClassLoader loader = GroovyObject.class.classLoader
        dependencies.each {File file ->
            loader.addURL(file.toURL())
        }
    }

    private def addOracleToolset(String type, String prefix, String path, Closure closure) {
        OracleDatabaseConvention convention = env.get(prefix)

        if (convention == null) {
            project.logger.info("Applying oracle convention to ${path} ...")
            convention = new OracleDatabaseConvention(project, prefix)
            env.put(prefix, convention)

            project.logger.info('Adding default tools for oracle...')
            OracleTasks.addDefaultTools(project, 'Database', convention)
        }

        convention.config(closure)

        if (!loadedDrivers.contains(convention.driver)) {
            project.logger.info("Registring jdbc-driver: ${convention.driver}")
            Class driver = GroovyObject.class.classLoader.loadClass(convention.driver)

            // You might need one or both of these as well
            Driver instance = driver.newInstance()
            DriverManager.registerDriver(instance)

            loadedDrivers.add(convention.driver)
        }

        convention.addTasks(path)

        return convention
    }

}
