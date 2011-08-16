package no.statkart.matrikkel.build.utils.gradle.plugins.database.oracle

import org.gradle.api.tasks.TaskAction
import org.gradle.api.DefaultTask
import org.gradle.api.tasks.Optional
import org.gradle.api.tasks.Input

/**
 * Todo: virker pdd ikke
 *
 * Gradle task for kjøring av en sql via Oracle SQLPlus.
 *
 * OBS: Dersom det inneholder feil i sql-fil vil prompten henge på sqlplus.. ingen kjente workarounds for dette.
 *
 * @author Leif Lislegård
 * @since 1.1
 */
class OracleSqlplusTask extends DefaultTask {

    @Optional
    String args = ''

    @Input
    File sqlFile = null



    @TaskAction
    def exec() {
        validateInput()

        def username = project.convention.plugins.db.username
        def password = project.convention.plugins.db.password
        def tns = project.tns


        def sout = new StringBuffer()
        def serr = new StringBuffer()
        String[] command = [scriptFile.name,
                '-L',
                "${username}/${password}@${tns}",
                "@${sqlFile.path.replace('\\', '/')}"
        ]

//        String[] command = ['sqlplus.exe',
//                '-L',
//                "${username}/${password}@skrivdb52.statkart.no:1521/GBUT1DEV.STATKART.NO",
//                "@${sqlFile.name}"
//        ]

//        String[] command = ['sqlplus.exe',
//                '-L',
//                "${username}/${password}@${tns}",
//                "@${sqlFile.name}"
//        ]

        //adding optional params
        if (args) {
            command[command.length] = args;
        }

        def sqlplus = Runtime.runtime.exec(command, null, scriptFile.parentFile)
//        def sqlplus = Runtime.runtime.exec(command as String[], new String[0], sqlFile.getParentFile())


        println ' parent: ' + sqlFile.parentFile
        println 'sqlFile: ' + sqlFile


        //todo: filter password
//        def maskedPassword;
//        password.length().times{maskedPassword += '*'}
//        command[2] = command[2].replace(password, maskedPassword)


        logger.debug('Kaller sqlplus.exe med bruker ' + username + ', tns ' + tns + ' og sql: ' + sqlFile);

        logger.info 'Executing command: ' + command.join(' ')

        def running = true
        def bufferPrinter = {buffer ->
          def lastIndex = 0
          while(running) {
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



        sqlplus.consumeProcessOutput(sout, serr)
        try {
            sqlplus.waitFor()
        }
        finally {
          running = false
        }


        if (serr.toString().contains('ORA-') || sqlplus.exitValue()) {
            println '!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!'
            println 'Feil under kjøring av sqlplus.exe:'
            print sout
            print serr
            println '!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!'
            System.exit(1)
        }


        println '...sql queries OK'
        print sout

    }

    private void validateInput() {
        //todo
    }

}
