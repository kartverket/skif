package no.statkart.matrikkel.build.utils.gradle.plugins.database.oracle

import org.gradle.api.Project
import no.statkart.matrikkel.build.utils.gradle.plugins.database.Credentials
import org.gradle.api.InvalidUserDataException

/**
 * Convention object for oracle database
 *
 * @author Leif Lislegård
 * @since 1.1
 */
class OracleDatabaseConvention {

    Project project

    /**
     * Prefix for alle tasks for tilknyttet denne konvensjonen
     */
    String prefix

    public Credentials credentials

    /** settes via {@link #config(Closure) config closure} definert i prosjekt */
    public String url
    /** settes via {@link #config(Closure) config closure} definert i prosjekt */
    public String driver = 'oracle.jdbc.OracleDriver'


    OracleDatabaseConvention(Project project, String propertyPrefix) {
        this.project = project
        this.prefix = propertyPrefix

        credentials = new Credentials(project)

        url = project.properties[propertyPrefix + 'db_jdbc_url']
        if (project.hasProperty(propertyPrefix + 'db_jdbc_driver')) {
            driver = project.properties[propertyPrefix + 'db_jdbc_driver']
        }

        /**
         * passord er det samme som brukernavn dersom uspesifisert
         */
        if (!project.hasProperty(propertyPrefix + 'db_password')) {
            project.setProperty(propertyPrefix + 'db_password', project.properties[propertyPrefix + 'db_username'])
            project.logger.info "setting property ${propertyPrefix + 'db_password'} to ${project.properties[propertyPrefix + 'db_password']}"
        }

        /**
         * schema er det samme som brukernavn dersom uspesifisert
         */
        if (!project.hasProperty(propertyPrefix + 'db_schema')) {
            project.setProperty(propertyPrefix + 'db_schema', project.properties[propertyPrefix + 'db_username'])
            project.logger.info "setting property ${propertyPrefix + 'db_schema'} to ${project.properties[propertyPrefix + 'db_schema']}"
        }


        //oradataNN settes enten til standard verdi, eller til hva som er angitt i oradata
        if (!project.hasProperty(propertyPrefix + 'db_oradata01')) {
            project.setProperty(propertyPrefix + 'db_oradata01', project.hasProperty('db_oradata') ? project.db_oradata : 'F:\\Oradata')
        }
        if (!project.hasProperty(propertyPrefix + 'db_oradata02')) {
            project.setProperty(propertyPrefix + 'db_oradata02', project.hasProperty('db_oradata') ? project.db_oradata : 'G:\\Oradata')
        }
        if (!project.hasProperty(propertyPrefix + 'db_oradata03')) {
            project.setProperty(propertyPrefix + 'db_oradata03', project.hasProperty('db_oradata') ? project.db_oradata : 'J:\\Oradata')
        }
        if (!project.hasProperty(propertyPrefix + 'db_oradata04')) {
            project.setProperty(propertyPrefix + 'db_oradata04', project.properties[propertyPrefix + 'db_oradata01'])
        }
        if (!project.hasProperty(propertyPrefix + 'db_oradata05')) {
            project.setProperty(propertyPrefix + 'db_oradata05', project.properties[propertyPrefix + 'db_oradata02'])
        }
        if (!project.hasProperty(propertyPrefix + 'db_oradata06')) {
            project.setProperty(propertyPrefix + 'db_oradata06', project.properties[propertyPrefix + 'db_oradata03'])
        }
        if (!project.hasProperty(propertyPrefix + 'db_oradata07')) {
            project.setProperty(propertyPrefix + 'db_oradata07', project.properties[propertyPrefix + 'db_oradata01'])
        }
        if (!project.hasProperty(propertyPrefix + 'db_oradata08')) {
            project.setProperty(propertyPrefix + 'db_oradata08', project.properties[propertyPrefix + 'db_oradata02'])
        }
        if (!project.hasProperty(propertyPrefix + 'db_oradata09')) {
            project.setProperty(propertyPrefix + 'db_oradata09', project.properties[propertyPrefix + 'db_oradata03'])
        }
    }

    /**
     * Printer viktige definerte proerties
     */
    public void printInfo() {
        80.times {project.print '*'}; project.println ''

        println "${this.class.simpleName} for \"${prefix}\":"
        println "  url -> ${url}"
        println "  driver -> ${driver}"
        if (credentials.hasUsername())
            println "  credentials.username -> ${credentials.username}"
        if (credentials.hasPassword())
            println "  credentials.password -> ${credentials.password}"
        println "  username -> ${username}"
        println "  host -> ${host}"
        println "  port -> ${port}"
        println "  sid -> ${sid}"
        println "  tns -> ${tns}"

        80.times {project.print '*'}; project.println ''
    }


    public OracleDatabaseConvention addTasks(String path) {
        OracleTasks tasks = new OracleTasks(path)
        tasks.init(project, this)
        return this
    }

    public def config(Closure closure) {
        closure.setDelegate(this)
        closure.resolveStrategy = Closure.DELEGATE_FIRST
        closure()
   }


    // oracle attributes by convention... -->

    public String getUsername() {
        return project.property(prefix + 'db_username')
    }
    public String getHost() {
        return project.property(prefix + 'db_host')
    }
    public String getPort() {
        return project.property(prefix + 'db_port')
    }
    public String getSid() {
        return project.property(prefix + 'db_sid')
    }

    public String getTns() {
        String tns;
        if (project.hasProperty('tns')) {
            tns = project.property('tns')
        } else {
            tns = "${host}:${port}/${sid}".toUpperCase()    //defaults to an EZCONNECT connect string
            if (!tns.toUpperCase().endsWith('.STATKART.NO')) {
                tns += '.STATKART.NO'
            }
        }
        return tns
    }
    public String getDirectory() {
        if (project.hasProperty('directory')) {
            return project.property('directory')
        }
        throw new InvalidUserDataException("property 'directory' not set!")
    }
    public Collection<String> getSchemas() {
        return Arrays.asList(getUsername())        //defaults to username
    }

    public Map<String, String> getSchemaMapping() {
        def schemaMapping = [:]
        getSchemas().each { it ->
            schemaMapping[it] = it
        }
        return schemaMapping
    }

    public String getDateString() {
        return new Date().format('yyyy-MM-dd_hhmmss')
    }

    public String getDumpfile() {
        return "${schemas[0]}_${dateString}.DMP"
    }

    public String getLogfileImport(String dumpfile) {
        return "${dumpfile}.import.${dateString}.LOG"
    }

    public String getLogfileExport(String dumpfile) {
        if (dumpfile == null) {
            dumpfile = getDumpfile()
        }
        return "${dumpfile}.export.${dateString}.LOG"
    }

    public Collection<String> getExcludesImport() {
        return 'USER'.split(',')
    }

    public Collection<String> getExcludesExport() {
        return 'STATISTICS,TABLESPACE_QUOTA,SYNONYM,VIEW'.split(',')
    }

    public String getCompression() {
        return 'DATA_ONLY'
    }

    public String getTableExistsAction() {
        return 'REPLACE'
    }

}
