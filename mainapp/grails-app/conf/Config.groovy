import com.octo.captcha.component.image.backgroundgenerator.GradientBackgroundGenerator
import com.octo.captcha.component.image.color.SingleColorGenerator
import com.octo.captcha.component.image.fontgenerator.RandomFontGenerator
import com.octo.captcha.component.image.textpaster.NonLinearTextPaster
import com.octo.captcha.component.image.wordtoimage.ComposedWordToImage
import com.octo.captcha.component.word.wordgenerator.RandomWordGenerator
import com.octo.captcha.engine.GenericCaptchaEngine
import com.octo.captcha.image.gimpy.GimpyFactory
import com.octo.captcha.service.multitype.GenericManageableCaptchaService
import org.apache.log4j.RollingFileAppender

import java.awt.*

grails.project.groupId = appName // change this to alter the default package name and Maven publishing destination

// The ACCEPT header will not be used for content negotiation for user agents containing the following strings (defaults to the 4 major rendering engines)
grails.mime.disable.accept.header.userAgents = ['Gecko', 'WebKit', 'Presto', 'Trident']
grails.mime.types = [ // the first one is the default format
                      all          : '*/*', // 'all' maps to '*' or the first available format in withFormat
                      atom         : 'application/atom+xml',
                      css          : 'text/css',
                      csv          : 'text/csv',
                      form         : 'application/x-www-form-urlencoded',
                      html         : ['text/html', 'application/xhtml+xml'],
                      js           : 'text/javascript',
                      json         : ['application/json', 'text/json'],
                      multipartForm: 'multipart/form-data',
                      rss          : 'application/rss+xml',
                      text         : 'text/plain',
                      hal          : ['application/hal+json', 'application/hal+xml'],
                      xml          : ['text/xml', 'application/xml'],
                      pdf          : 'application/pdf',
                      zip          : 'application/zip'
]

// URL Mapping Cache Max Size, defaults to 5000
//grails.urlmapping.cache.maxsize = 1000

// What URL patterns should be processed by the resources plugin
grails.resources.adhoc.patterns = ['/images/*', '/css/*', '/js/*']
grails.resources.adhoc.excludes = ['**/WEB-INF/**', '**/META-INF/**']

// The default scope for controllers. May be prototype, session or singleton.
// If unspecified, controllers are prototype scoped.
grails.controllers.defaultScope = 'prototype'
grails.converters.encoding = "UTF-8"
grails.converters.json.domain.include.version = true

// GSP settings
grails {
    views {
        gsp {
            encoding = 'UTF-8'
            htmlcodec = 'xml' // use xml escaping instead of HTML4 escaping
            codecs {
                expression = 'none' // escapes values inside ${}
                scriptlet = 'html' // escapes output from scriptlets in GSPs
                taglib = 'none' // escapes output from taglibs
                staticparts = 'none' // escapes output from static template parts
            }
        }
        // escapes all not-encoded output at final stage of outputting
        // filteringCodecForContentType.'text/html' = 'html'
    }
}

grails.app.context = "/"
// enable Sitemesh preprocessing of GSP pages
grails.views.gsp.sitemesh.preprocess = true
// scaffolding templates configuration
grails.scaffolding.templates.domainSuffix = ''

// Set to false to use the new Grails 1.2 JSONBuilder in the render method
grails.json.legacy.builder = false
// enabled native2ascii conversion of i18n properties files
grails.enable.native2ascii = true
// whether to install the java.util.logging bridge for sl4j. Disable for AppEngine!

// packages to include in Spring bean scanning
grails.spring.bean.packages = ['com.athena.mis.*']

// request parameters to mask when logging exceptions
grails.exceptionresolver.params.exclude = ['password']

// configure auto-caching of queries by default (if false you can cache individual queries with 'cache: true')
grails.hibernate.cache.queries = false

grails.views.enable.jsessionid = false


environments {
    production {
        grails.config.locations = ["classpath:app-config.properties"]
        grails.logging.jul.usebridge = false
    }
    development {
        grails.logging.jul.usebridge = false
        application.webUrl = "http://192.168.1.110:8080"
        application.printSql = true
    }
}
grails.plugin.springsecurity.rejectIfNoRule = true

// whether to disable processing of multi part requests
grails.web.disable.multipart = false
// configure passing transaction's read-only attribute to Hibernate session, queries and criterias
// set "singleSession = false" OSIV mode in hibernate configuration after enabling
grails.hibernate.pass.readonly = false
// configure passing read-only to OSIV session by default, requires "singleSession = false" OSIV mode
grails.hibernate.osiv.readonly = false

// log4j configuration for Unix system.
String path = "/tmp/logs/"

log4j = {
    appenders {
        appender new RollingFileAppender(
                name: "errorLog",
                maxFileSize: 1024,
                file: "${path}error.log".toString(),
                append: true,
                threshold: org.apache.log4j.Level.ERROR,
                layout: pattern(conversionPattern:'%d{dd MMM yyyy HH:mm:ss} %c{1} %M - %m%n')
                )

        // Use if we want to prevent creation of a stacktrace.log file.
//        'null' name: 'stacktrace'

        console name: "stdout",
                layout: pattern
    }

    root {
        additivity = false
        environments {
            production {
                error "errorLog"
            }
        }
    }

    error 'org.codehaus.groovy.grails.web.servlet',        // controllers
            'org.codehaus.groovy.grails.web.pages',          // GSP
            'org.codehaus.groovy.grails.web.sitemesh',       // layouts
            'org.codehaus.groovy.grails.web.mapping.filter', // URL mapping
            'org.codehaus.groovy.grails.web.mapping',        // URL mapping
            'org.codehaus.groovy.grails.commons',            // core / classloading
            'org.codehaus.groovy.grails.plugins',             // plugins
            'org.codehaus.groovy.grails.orm.hibernate',      // hibernate integration
            'org.springframework',
            'org.hibernate',
            'net.sf.ehcache.hibernate'

    error errorLog: 'com.athena.mis'

    all 'grails.app'
}

// Added by the Spring Security Core plugin:
// For Spring Security Core plugin:
grails.plugin.springsecurity.userLookup.userDomainClassName = 'com.athena.mis.application.entity.AppUser'
grails.plugin.springsecurity.userLookup.authorityJoinClassName = 'com.athena.mis.application.entity.UserRole'
grails.plugin.springsecurity.authority.className = 'com.athena.mis.application.entity.Role'
grails.plugin.springsecurity.requestMap.className = 'com.athena.mis.application.entity.RequestMap'
grails.plugin.springsecurity.securityConfigType = grails.plugin.springsecurity.SecurityConfigType.Requestmap
grails.plugin.springsecurity.successHandler.alwaysUseDefault = true
grails.plugin.springsecurity.useSecurityEventListener = true
grails.plugin.springsecurity.userLookup.usernamePropertyName = 'loginId'
grails.plugin.springsecurity.successHandler.defaultTargetUrl = '/login/loginSuccess'
grails.plugin.springsecurity.errors.login.fail = "Invalid ID or Password."
grails.plugin.springsecurity.errors.login.expired = "Your account has expired. Contact with administrator"
grails.plugin.springsecurity.errors.login.passwordExpired = "Your password has expired."
grails.plugin.springsecurity.errors.login.disabled = "Your account is disabled. Contact with administrator"
grails.plugin.springsecurity.errors.login.locked = "Your account is locked. Contact with administrator"
grails.plugin.springsecurity.password.algorithm = 'SHA-256'
grails.plugin.springsecurity.password.hash.iterations = 1
grails.plugin.springsecurity.apf.postOnly = false
grails.plugin.springsecurity.useSwitchUserFilter = true

//grails.plugin.springsecurity.switchUser.switchUserUrl = '/'
//grails.plugin.springsecurity.switchUser.exitUserUrl = '/login/loginSuccess'
grails.plugin.springsecurity.switchUser.targetUrl ='/login/switchUser'
//grails.plugin.springsecurity.switchUser.switchFailureUrl = '/login/loginSuccess'

grails.gorm.validate = false
grails.gorm.deepValidate = false
grails.gorm.failOnError = true
grails.gorm.autoFlush = false

jcaptchas {
    image = new GenericManageableCaptchaService(
            new GenericCaptchaEngine(
                    new GimpyFactory(
                            new RandomWordGenerator(
                                    "abcdefgikmnpqrstwxyz2345689"
                            ),
                            new ComposedWordToImage(
                                    new RandomFontGenerator(
                                            25, // min font size
                                            30, // max font size
                                            [new Font("Tohoma", 0, 14)] as Font[]
                                    ),
                                    new GradientBackgroundGenerator(
                                            214, // width
                                            35, // height
                                            new SingleColorGenerator(new Color(255, 255, 255)),
                                            new SingleColorGenerator(new Color(255, 255, 255))
                                    ),
                                    new NonLinearTextPaster(
                                            5, // minimal length of text
                                            5, // maximal length of text
                                            new Color(0, 0, 0)
                                    )
                            )
                    )
            ),
            180,    // minGuarantedStorageDelayInSeconds
            100000, // maxCaptchaStoreSize
            75000   // captchaStoreLoadBeforeGarbageCollection
    )
}


theme.application = "/plugins/applicationplugin-0.1/theme/application"

// gdoc config
grails.doc.title = "Management Information System"
grails.doc.subtitle = "Usecase Documentation"
grails.doc.authors = "Nahida Sultana, Meherun Nessa"
grails.doc.copyright = "Copyright © 2014 by Athena Software Associates Ltd.<br/>"
grails.doc.images = new File("src/docs/image")

grails.doc.footer = "<br/>All Rights Reserved.  <br/>No part of this document may be reproduced or transmitted in any form " +
        "or by any means, electronic, mechanical, photocopying, recording, or otherwise, " +
        "without prior written permission of Athena Software Associates Ltd. <br/>" +
        "By using this application and it's documentation, you agreed terns and condition of our " +
        "<a href='#'> License Agreement </a> "