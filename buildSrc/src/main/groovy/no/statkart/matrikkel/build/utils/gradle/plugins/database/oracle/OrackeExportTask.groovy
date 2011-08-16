package no.statkart.matrikkel.build.utils.gradle.plugins.database.oracle

import org.gradle.api.DefaultTask
import org.gradle.api.InvalidUserDataException
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.Optional
import org.gradle.api.tasks.TaskAction

/**
 * Task for kjøring av export-script for oracle baser
 *
 * Denne task forutsetter følgende prosjek-properties:
 *  <ul>
 *      <li><code>username</code>: brukernavn for database autentisering
 *      <li><code>password</code>: passord for database autentisering
 *      <li><code>tns</code>: tns som definerer databasen
 *
 *      <li><code>directory</code>
 *      <li><code>dumpfile</code>
 *
 *  </ul>
 *
 * Det forutsettes også at Oracle sqlClient er installer og finnes tilgjengelig på path.
 *
 * @author Leif Lislegård
 * @since 1.1
 */
class OracleExportTask extends DefaultTask {

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
    Collection<String> schemas

    @Optional
    @Input
    String logfile

    @Optional
    @Input
    Collection<String> exclude

    @Optional
    @Input
    String compression



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
        String[] command = ['expdp.exe',
                "USERID=${username}/${password}@${tns}",
                "DIRECTORY=${directory}",
                "SCHEMAS=${schemas.join(',')}",
                "DUMPFILE=${dumpfile}",
                "LOGFILE=${logfile}",
                "EXCLUDE=${exclude.join(',')}",
                "COMPRESSION=${compression}"
        ]
        def impdb = Runtime.runtime.exec(command, null, getProject().getProjectDir())

        //todo: filter password
//        def maskedPassword;
//        password.length().times{maskedPassword += '*'}
//        command[1] = command[1].replace(password, maskedPassword)


        logger.debug('Kaller impdp.exe med bruker ' + username + ', tns ' + tns);

        logger.info 'Executing command: \n' + command.join(' ')

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
            println 'Feil under kjøring av expdp.exe:'
            print serr
            println '!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!'
            System.exit(1)
        }


        println '...oracle export OK'
        print sout
    }


    private void validateInput() {


        if (project.hasProperty('directory')) {
            directory = project.property('directory')
        } else if (convention != null) {
            directory = convention.directory
        } else {
            throw new InvalidUserDataException("property 'directory' not set!")
        }


        if (project.hasProperty('dumpfile')) {
            dumpfile = project.property('dumpfile')
        } else if (convention != null) {
            dumpfile = convention.dumpfile
        } else {
            throw new InvalidUserDataException("property 'dumpfile' not set!")
        }

        dumpfile = dumpfile.toUpperCase()
        if (!dumpfile.endsWith('.DMP')) {
            dumpfile += '.DMP'
        }


        if (project.hasProperty('schemas')) {
            schemas = project.property('schemas').split(',')
        } else if (convention != null) {
            schemas = convention.schemas
        } else {
            throw new InvalidUserDataException("property 'schemas' not set!")
        }

        if (project.hasProperty('logfile')) {
            logfile = project.property('logfile')
        } else if (convention != null) {
            logfile = convention.getLogfileExport(dumpfile)
        } else {
            throw new InvalidUserDataException("property 'logfile' not set!")
        }


        if (project.hasProperty('exclude')) {
            exclude = project.property('exclude').split(',')
        } else if (convention != null) {
            exclude = convention.excludesExport
        } else {
            throw new InvalidUserDataException("property 'exclude' not set!")
        }

        if (project.hasProperty('compression')) {
            compression = project.property('compression')
        } else if (convention != null) {
            compression = convention.compression
        } else {
            throw new InvalidUserDataException("property 'compression' not set!")
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

