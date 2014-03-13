package nebula.plugin.info.java

import nebula.plugin.info.InfoBrokerPlugin
import nebula.plugin.info.InfoCollectorPlugin
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.plugins.JavaBasePlugin
import org.gradle.api.plugins.JavaPluginConvention

/**
 * Collect Java relevant fields.
 */
class InfoJavaPlugin implements Plugin<Project>, InfoCollectorPlugin {

    // Apache Commons has a standard convention for these variables
    // http://commons.apache.org/releases/prepare.html

    static final String CREATED_PROPERTY = 'Created-By' // E.g. Created-By: 1.5.0_13-119 (Apple Inc.)
    static final String JDK_PROPERTY = 'Build-Java-Version'

    static final String SOURCE_PROPERTY = 'X-Compile-Source-JDK'
    static final String TARGET_PROPERTY = 'X-Compile-Target-JDK'

    void apply(Project project) {
        // This can't change, so we can commit it early
        project.plugins.withType(InfoBrokerPlugin) { InfoBrokerPlugin  manifestPlugin ->
            manifestPlugin.add(CREATED_PROPERTY, "${System.getProperty('java.runtime.version')} (${System.getProperty('java.vm.specification.vendor')})")
            manifestPlugin.add(JDK_PROPERTY, System.getProperty('java.version'))
        }

        // After-evaluating, because we need to give user a chance to effect the extension
        project.afterEvaluate {
            project.plugins.withType(JavaBasePlugin) {
                JavaPluginConvention javaConvention = project.getConvention().getPlugin(JavaPluginConvention)

                project.plugins.withType(InfoBrokerPlugin) { InfoBrokerPlugin manifestPlugin ->
                    manifestPlugin.add(TARGET_PROPERTY, { javaConvention.targetCompatibility } )
                    manifestPlugin.add(SOURCE_PROPERTY, { javaConvention.sourceCompatibility } )
                }
            }
        }
    }
}
