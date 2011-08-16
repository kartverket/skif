package no.statkart.matrikkel.build.utils.gradle.plugins.database.oracle

import org.gradle.api.Project
import no.statkart.matrikkel.build.utils.gradle.plugins.database.SQLTask

/**
 * Har til oppgave å definere opp tasks basert på følgende konvensjon:
 *
 *
 * Det vil bli generert opp en task på bakgrunn av hver sql-fil. Prefiks + filnavn dikterer navn på task.
 *
 * Eks: createTablespace.sql med prefix 'db' får da en task mad navn 'db_createTablespace'
 *
 * @author Leif Lislegård
 * @since 1.1
 */
class OracleTasks {

    private final String relativePath

    OracleTasks(String relativePath) {
        this.relativePath = relativePath
    }


    def init(Project project, OracleDatabaseConvention convention) {

        configureTargets(project, 'Database', convention)

        return this
    }

    private def configureTargets(Project project, String groupString, OracleDatabaseConvention conv) {

        def sourceRoot = new File(project.getProjectDir(), 'src')

        def files = project.fileTree(new File(sourceRoot, relativePath))
        files.include '**/*.sql'
        files.each() {
            File file ->

            project.task([type: SQLTask, dependsOn: ['buildSQL']], conv.prefix + file.name.substring(0, file.name.length() - 4)) {
                group = groupString
                convention = conv
                sqlFile = new File(project.getBuildDir(), file.getAbsolutePath().replace(sourceRoot.getAbsolutePath(), ''))
            }
        }

    }


    public static void addDefaultTools(Project project, String groupString, OracleDatabaseConvention conv) {

        project.task([type: OracleImportTask], "${conv.prefix}Import") {
            group = groupString
            convention = conv
            description = 'Import av dump via Oracles eget verktøy'
        }

        project.task([type: OracleExportTask], "${conv.prefix}Export") {
            group = groupString
            convention = conv
            description = 'Export av dump via Oracles eget verktøy'
        }

        project.task("${conv.prefix}Info") {
            group = groupString
            description = 'Viser gjeldende konfigurasjon'

            doLast {
                conv.printInfo()
            }

        }
    }

}

