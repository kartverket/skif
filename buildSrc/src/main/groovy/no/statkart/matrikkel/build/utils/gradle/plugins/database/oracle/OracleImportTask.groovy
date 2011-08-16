package no.statkart.matrikkel.build.utils.gradle.plugins.database.oracle

import org.gradle.api.DefaultTask
import org.gradle.api.InvalidUserDataException
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.Optional
import org.gradle.api.tasks.TaskAction

/**
 * Task for kjøring av import-script for oracle baser
 *
 * Denne task forutsetter følgende prosjek-properties:
 *  <ul>
 *      <li><code>username</code>: brukernavn for database autentisering
 *      <li><code>password</code>: passord for database autentisering
 *      <li><code>tns</code>: tns som definerer databasen
 *
 *      <li><code>directory</code>
 *      <li><code>dumpfile</code>
 *  </ul>
 *
 * Det forutsettes også at Oracle sqlClient er installer og finnes tilgjengelig på path.
 *
 * @author Leif Lislegård
 * @since 1.1
 */
class OracleImportTask extends DefaultTask {

    @Optional
    @Input
    OracleDatabaseConvention convention




    @Optional
    @Input
    String directory

    @Optional
    @Input
    String dumpfile

    @Optional
    @Input
    List<String> schemas

    @Optional
    @Input
    Map<String, String> schemaMapping

    @Optional
    @Input
    String logfile

    @Optional
    @Input
    Collection<String> excludes

    @Optional
    @Input
    String tableExistsAction



    @Optional
    @Input
    String username

    @Optional
    @Input
    String password

    @Optional
    @Input
    String tns


    @TaskAction
    def exec() {
        validateInput()

        def sout = new StringBuffer()
        def serr = new StringBuffer()
        String[] command = ['impdp.exe',
                "${username}/${password}@${tns}",
                "DIRECTORY=${directory}",
                "SCHEMAS=${schemas.join(',')}",
                "REMAP_SCHEMA=${schemaMapping.collect{key, value -> key + ':' + value}.join(',')}",
                "DUMPFILE=${dumpfile}",
                "LOGFILE=${logfile}",
                "TABLE_EXISTS_ACTION=${tableExistsAction}",
                "EXCLUDE=${excludes.join(',')}",
                'TRANSFORM=SEGMENT_ATTRIBUTES:n'
        ]
        def impdb = Runtime.runtime.exec(command, null, getProject().getProjectDir())

        //todo: filter password
//        def maskedPassword;
//        password.length().times{maskedPassword += '*'}
//        command[1] = command[1].replace(password, maskedPassword)


        logger.debug('Kaller impdp.exe med bruker ' + username + ', tns ' + tns);

        logger.info 'Executing command: ' + command.join(' ')

        def running = true
        def bufferPrinter = {buffer ->
            def lastIndex = 0
            while (running) {
                def length = buffer.length()
                if (length > lastIndex) {
                    print buffer.subSequence(lastIndex, length)
                    lastIndex = length
                }
                Thread.sleep(100)
            }
        }
        Thread.start bufferPrinter.curry(sout)
        Thread.start bufferPrinter.curry(serr)

        impdb.consumeProcessOutput(sout, serr)
        try {
            impdb.waitFor()
        }
        finally {
            running = false
        }

        if (serr.toString().contains('ORA-') || impdb.exitValue()) {
            println '!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!'
            println 'Feil under kjøring av impdp.exe:'
            print serr
            println '!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!'
            System.exit(1)
        }


        println '...oracle import OK'
        print sout
    }


    private void validateInput() {


        if (project.hasProperty('dumpfile')) {
            dumpfile = project.property('dumpfile')
        } else {
            throw new InvalidUserDataException("property 'dumpfile' not set!")
        }

        dumpfile = dumpfile.toUpperCase()
        if (!dumpfile.endsWith('.DMP')) {
            dumpfile += '.DMP'
        }

        if (project.hasProperty('directory')) {
            directory = project.property('directory')
        } else if (convention != null) {
            directory = convention.directory
        } else {
            throw new InvalidUserDataException("property 'directory' not set!")
        }


        if (project.hasProperty('schemas')) {
            schemas = project.property('schemas').split(',')
        } else if (convention != null) {
            schemas = convention.schemas
        } else {
            throw new InvalidUserDataException("property 'schemas' not set!")
        }

        if (project.hasProperty('schemaMapping')) {
            schemaMapping = [:]
            project.property('schemaMapping').split(',').each { it ->
                schemaMapping[it.split(':')[0], it.split(':')[1]]
            }
        } else if (convention != null) {
            schemaMapping = convention.schemaMapping
        } else {
            throw new InvalidUserDataException("property 'schemaMapping' not set!")
        }

        if (project.hasProperty('logfile')) {
            logfile = project.property('logfile')
        } else if (convention != null) {
            logfile = convention.getLogfileImport(dumpfile)
        } else {
            throw new InvalidUserDataException("property 'logfile' not set!")
        }


        if (project.hasProperty('exclude')) {
            excludes = project.property('exclude').split(',')
        } else if (convention != null) {
            excludes = convention.excludesImport
        } else {
            throw new InvalidUserDataException("property 'exclude' not set!")
        }


        if (project.hasProperty('tableExistsAction')) {
            tableExistsAction = project.property('tableExistsAction')
        } else if (convention != null) {
            tableExistsAction = convention.tableExistsAction
        } else {
            throw new InvalidUserDataException("property 'tableExistsAction' not set!")
        }





        if (username == null) {
            if (convention != null) {
                username = convention.credentials.username
            } else {
                throw new InvalidUserDataException("property 'username' not set!")
            }
        }

        if (password == null) {
            if (convention != null) {
                password = convention.credentials.password
            } else {
                throw new InvalidUserDataException("property 'password' not set!")
            }
        }

        if (tns == null) {
            if (convention != null) {
                tns = convention.tns
            } else {
                throw new InvalidUserDataException("property 'tns' not set!")
            }
        }

    }


}
