import grails.util.Environment
import org.codehaus.groovy.grails.commons.GrailsApplication

class ApplicationpluginGrailsPlugin {
    // the plugin version
    def version = "0.1"
    // the version or versions of Grails the plugin is designed for
    def grailsVersion = "1.3.7 > *"
    // the other plugins this plugin depends on
    def dependsOn = [:]
    // resources that are excluded from plugin packaging
    def pluginExcludes = [
            "grails-app/views/error.gsp"
    ]

    // TODO Fill in these fields
    def author = "Your name"
    def authorEmail = ""
    def title = "Plugin summary/headline"
    def description = '''\\
Brief description of the plugin.
'''

    // URL to the plugin's documentation
    def documentation = "http://grails.org/plugin/applicationplugin"

    def doWithWebDescriptor = { xml ->
        // TODO Implement additions to web.xml (optional), this event occurs before 
    }

    def doWithSpring = {
        mergeConfig(application)
        // TODO Implement runtime spring config (optional)
    }

    private void mergeConfig(GrailsApplication app) {
        ConfigObject currentConfig = app.config.applicationConfig
        ConfigSlurper slurper = new ConfigSlurper(Environment.getCurrent().getName());
        ConfigObject ptConfig = slurper.parse(app.classLoader.loadClass("ApplicationPluginConfig"))

        ConfigObject config = new ConfigObject();
        config.putAll(ptConfig.applicationConfig.merge(currentConfig))

        app.config.applicationConfig = config;
    }

    def doWithDynamicMethods = { ctx ->
        // TODO Implement registering dynamic methods to classes (optional)
    }


    def doWithApplicationContext = { applicationContext ->
        // TODO Implement post initialization spring config (optional)

        // loading configuration file of applicationPlugin
//        GroovyClassLoader classLoader = new GroovyClassLoader(getClass().getClassLoader());
//        ConfigObject config;
//        try {
//            config = new ConfigSlurper().parse(classLoader.loadClass('ApplicationPluginConfig'));
//            applicationPluginIntf.setConfig(config);
//        } catch (Exception e) {
//            println("ApplicationPluginConfig.groovy is not loaded");
//        }
//        println("Test Print ---> " + config.applicationPlugin.url);    //applicationPlugin.url

    }

    def onChange = { event ->
        // TODO Implement code that i executed when any artefact that this plugin is
        // watching is modified and reloaded. The event contains: event.source,
        // event.application, event.manager, event.ctx, and event.plugin.
    }

    def onConfigChange = { event ->
        this.mergeConfig(application)
        // TODO Implement code that is executed when the project configuration changes.
        // The event is the same as for 'onChange'.
    }
}
