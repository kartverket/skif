package no.statkart.matrikkel.build.utils.gradle.plugins.database

import org.gradle.api.DefaultTask
import org.gradle.api.tasks.TaskAction
import org.gradle.api.Project

/**
 * Task som setter username og password dersom ikke allerede angitt som parametere.
 *
 * @author Leif Lislegård
 * @since 1.1
 */
class Credentials {

    private String username
    private String password



    Credentials(Project project) {
        username = project.properties['username']
        password = project.properties['password']
    }

    public boolean hasUsername() {
        return username != null
    }

    public String getUsername() {
        if (!hasUsername()) {
            username = System.console().readLine('Please enter username: ')
        }
        return username
    }

    public boolean hasPassword() {
        return password != null
    }

    public String getPassword() {
        if (!hasPassword()) {
            password = new String(System.console().readPassword('Please enter password for %s (empty defaults to %s): ', username, username))
            if (password.isEmpty()) {
                password = username
            }
        }
        return password
    }

}
