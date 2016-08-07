grails.servlet.version = "3.0" // Change depending on target container compliance (2.5 or 3.0)
grails.project.class.dir = "target/classes"
grails.project.test.class.dir = "target/test-classes"
grails.project.test.reports.dir = "target/test-reports"
grails.project.work.dir = "target/work"
grails.project.target.level = 1.7
grails.project.source.level = 1.7
grails.server.port.http = 8080
//grails.project.war.file = "target/${appName}-${appVersion}.war"

grails.project.fork = [
        // configure settings for compilation JVM, note that if you alter the Groovy version forked compilation is required
        //compile: [maxMemory: 2048, minMemory: 2048, debug: false, maxPerm: 350, daemon: true],
        // configure settings for the test-app JVM, uses the daemon by default
        //test: [maxMemory: 2G, minMemory: 2G, debug: false, maxPerm: 350, daemon: true],
        // configure settings for the run-app JVM
        //run: [maxMemory: 3072, minMemory: 3072, debug: false, maxPerm: 350, forkReserve: false],
        // configure settings for the run-war JVM
        //war: [maxMemory: 3072, minMemory: 3072, debug: false, maxPerm: 350, forkReserve: false],
        // configure settings for the Console UI JVM
        //console: [maxMemory: 3072, minMemory: 3072, debug: false, maxPerm: 350]
]

grails.project.dependency.resolver = "maven" // maven or ivy
grails.project.dependency.resolution = {
    // inherit Grails' default dependencies
    inherits("global") {
        // specify dependency exclusions here; for example, uncomment this to disable ehcache:
        // excludes 'ehcache'
    }
    log "error" // log level of Ivy resolver, either 'error', 'warn', 'info', 'debug' or 'verbose'
    checksums true // Whether to verify checksums on resolve
    legacyResolve false
    // whether to do a secondary resolve on plugin installation, not advised and here for backwards compatibility

    repositories {
        inherits true // Whether to inherit repository definitions from plugins

        grailsPlugins()
        grailsHome()
        mavenLocal()
        grailsCentral()
        mavenCentral()
        // uncomment these (or add new ones) to enable remote dependency resolution from public Maven repositories
        mavenRepo "http://repository.codehaus.org"
        mavenRepo "http://download.java.net/maven/2/"
        mavenRepo "http://repository.jboss.com/maven2/"
        mavenRepo "http://repo.grails.org/grails/core"
    }
    /*
    * All plugins last update checked on : 05-Feb-15
    */
    plugins {
        // following plugins will be installed on respective target
        // plugins for the compile step
        compile ':spring-security-core:2.0.0'
        compile(':jasper:1.11.0'){ excludes 'itext' }
        compile ':executor:0.3'
        compile(':jcaptcha:1.5.0')
        compile ':cache:1.1.8'
//        compile ':hibernate:3.6.10.18'
//        runtime ":hibernate4:4.3.8.1"
        runtime ":hibernate4:4.3.10"
        compile ':csv:0.3.1'        //for EXH
        compile ":quartz:1.0.2"
        // plugins for the build system only
        build ':tomcat:7.0.55.3'
        compile ":scaffolding:2.1.2"
//        build ":codenarc:0.24.1"
        //runtime ":database-migration:1.3.2"
    }

    dependencies {
        // specify dependencies here under either 'build', 'compile', 'runtime', 'test' or 'provided' scopes eg.
        compile 'org.imgscalr:imgscalr-lib:4.2'
        compile 'net.sf.uadetector:uadetector-resources:2014.10'
        build "com.lowagie:itext:2.1.7"
        runtime 'org.apache.poi:poi-scratchpad:3.10-FINAL'
        runtime 'org.apache.poi:ooxml-schemas:1.1'
        runtime 'org.apache.poi:poi-ooxml:3.10-FINAL'
        runtime 'com.itextpdf:itextpdf:5.5.4'   // for pdf parsing/generation
        runtime 'com.itextpdf.tool:xmlworker:5.5.5'  // for xhtml to pdf generation
        runtime 'org.jsoup:jsoup:1.7.2'
        runtime 'javax.mail:javax.mail-api:1.5.1'
        runtime 'com.sun.mail:javax.mail:1.5.1'
        runtime 'com.jcraft:jsch:0.1.52'
        test "org.grails:grails-datastore-test-support:1.0.2-grails-2.4"
    }
}

grails.plugin.location.'applicationPlugin' = "../plugins/applicationPlugin"
//grails.plugin.location.'exchangeHouse' = "../plugins/exchangeHouse"
//grails.plugin.location.'sarb' = "../plugins/sarb"
//grails.plugin.location.'arms' = "../plugins/arms"
//grails.plugin.location.'document' = "../plugins/document"
//grails.plugin.location.'dataPipeLine' = "../plugins/dataPipeLine"
//grails.plugin.location.'projectTrack' = "../plugins/projectTrack"
//grails.plugin.location.'procurement' = "../plugins/procurement"
//grails.plugin.location.'budget' = "../plugins/budget"
//grails.plugin.location.'accounting' = "../plugins/accounting"
//grails.plugin.location.'inventory' = "../plugins/inventory"
//grails.plugin.location.'qs' = "../plugins/qs"
//grails.plugin.location.'fixedAsset' = "../plugins/fixedAsset"
//grails.plugin.location.'elearning' = "../plugins/elearning"
grails.plugin.location.'beacon' = "../plugins/beacon"