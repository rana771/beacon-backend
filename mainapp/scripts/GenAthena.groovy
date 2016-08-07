import grails.util.GrailsNameUtils
import org.apache.commons.io.FilenameUtils
import org.springframework.core.io.FileSystemResource


//includeTargets << grailsScript("Init")
includeTargets << grailsScript("_GrailsInit")
includeTargets << grailsScript("_GrailsCreateArtifacts")
includeTargets << new File(scaffoldingPluginDir, 'scripts/_GrailsGenerate.groovy')

generateViews = true
generateController = true


target(main: "Generates a CRUD interface (controller + views) for a domain class") {
    depends(checkVersion, parseArguments, packageApp)
    println ""

    String className = null
    String pluginName = null
    String pluginsHome = null
    String pluginHome = null
    String propertyName =null
    if(argsMap.params.size() == 0){
        pluginName = promptForPluginName(type: "Plugin Name")
    }
    if(argsMap.params.size() > 0){
        pluginName = argsMap.params[0]
        argsMap.params.remove(0)
    }

    pluginsHome = getPluginsHome()
    pluginHome = "${pluginsHome}/${pluginName}"

    println "|Plugin Home# ${pluginHome}"
    File file = new File(pluginHome)
    if(!file.exists()){
        println "|Plugin name not spescified : ${pluginName}"
        return
    }

    promptForName(type: "Domain Class")
    println "|Running Athena generation Script"
    println ".........................................."

    try {
        String name = argsMap["params"][0]
        //className = GrailsNameUtils.getNaturalName(name)
        className = getClassName(name)
        generateForName = name
        propertyName = GrailsNameUtils.getPropertyNameRepresentation(className)
        println "|Property Name : ${propertyName}"
        String packagePath = getPackagePath(name)
        String actionPackagePath = getActionPackage(name)


        generateForOne()
        moveController(className, pluginHome, packagePath)
        moveService(className, pluginHome, packagePath)
        moveAllAction(className, pluginHome, actionPackagePath)
        moveAllGsp(className, pluginHome, packagePath)
        cleanUp(className, packagePath)
        println "|Done"
    }
    catch (Exception e) {
        //logError "Error running generate-all", e
        println e.getMessage()
    }
}

promptForPluginName = { Map args = [:] ->
    return grailsConsole.userInput("${args["type"]} not specified. Please enter:")
}

getPluginsHome = {
    return FilenameUtils.getFullPath(basedir) + "plugins"
}

def moveController(String className, String pluginHome, String packagePath) {
    try {
        String dst = "${pluginHome}/grails-app/controllers/" + packagePath+"/controller"
        println("moving controller to dir : ---------> ${dst}")
        if (!new File(dst).exists()) {
            ant.mkdir(dir: dst)
            //println("dir: ${dst} created")
        }

        moveGeneratedFile("${basedir}/grails-app/controllers/${packagePath}/entity/${className}Controller.groovy", dst + "/${className}Controller.groovy")
    }
    catch (Exception e) {
        println("error:" + e)
        exit(1)
    }
}

def moveService(String className, String pluginHome, String packagePath) {
     propertyName = GrailsNameUtils.getPropertyNameRepresentation(className)
    try {
        String dst = "${pluginHome}/grails-app/services/" + packagePath+"/service"
        if (!new File(dst).exists()) {
            ant.mkdir(dir: dst)
            //println("dir: ${dst} created")
        }
        moveGeneratedFile("${basedir}/grails-app/views/${propertyName}/Service.gsp", dst + "/${className}Service.groovy")
    }
    catch (Exception e) {
        println("error:" + e)
        exit(1)
    }
}

def moveAllAction(String className, String pluginHome, String actionPackagePath) {
     propertyName = GrailsNameUtils.getPropertyNameRepresentation(className)
    try {
        String dst = "${pluginHome}/grails-app/services/" + actionPackagePath
        if (!new File(dst).exists()) {
            ant.mkdir(dir: dst)
            //println("dir: ${dst} created")
        }
        moveGeneratedFile("${basedir}/grails-app/views/${propertyName}/CreateAction.gsp", "${dst}/Create${className}ActionService.groovy")
        moveGeneratedFile("${basedir}/grails-app/views/${propertyName}/DeleteAction.gsp", "${dst}/Delete${className}ActionService.groovy")
        moveGeneratedFile("${basedir}/grails-app/views/${propertyName}/ListAction.gsp", "${dst}/List${className}ActionService.groovy")
        moveGeneratedFile("${basedir}/grails-app/views/${propertyName}/UpdateAction.gsp", "${dst}/Update${className}ActionService.groovy")

    }
    catch (Exception e) {
        println("error: " + e)
        exit(1)
    }
}

def moveAllGsp(String className, String pluginHome, String packagePath){
     propertyName = GrailsNameUtils.getPropertyNameRepresentation(className)
    try {
        String dst = "${pluginHome}/grails-app/views/${propertyName}"
        if (!new File(dst).exists()) {
            ant.mkdir(dir: dst)
            //println("dir: ${dst} created")
        }
        moveGeneratedFile("${basedir}/grails-app/views/${propertyName}/show.gsp", "${dst}/show.gsp")
        moveGeneratedFile("${basedir}/grails-app/views/${propertyName}/_script.gsp", "${dst}/_script.gsp")
        moveGeneratedFile("${basedir}/grails-app/views/${propertyName}/_create.gsp", "${dst}/_create.gsp")
    }
    catch (Exception e) {
        println("Exception Occured while executing command GenAthena: " + e)
        exit(1)
    }
}

def cleanUp(String className, String packagePath){
     propertyName = GrailsNameUtils.getPropertyNameRepresentation(className)
    try {
        ant.delete(file: "${basedir}/grails-app/controllers/${packagePath}/entity/${className}Controller.groovy")
        File f = null
        List<String> folders = packagePath.split("/")
        int folderCount = folders.size()
        String path = null
        folderCount.times {
            path = "${basedir}/grails-app/controllers/${folders.join("/")}"
            f = new File(path)
            if(f.listFiles().length == 0){
                ant.delete(dir: path)
            }
            folders.remove(folders.size() - 1)
        }
        ant.delete(dir: "${basedir}/grails-app/views/${propertyName}")
    }
    catch (Exception e) {
        println("error:" + e)
        exit(1)
    }
}

def getClassName(String packagePath) {
    List<String> words = packagePath.split("\\.")
    return words.get(words.size() - 1)
}

def getPackagePath(def orginalParam) {
    def nameOp = orginalParam
    def rawName = nameOp
    nameOp = nameOp.indexOf('.') > -1 ? nameOp : GrailsNameUtils.getClassNameRepresentation(nameOp)
    nameOp = nameOp.replaceAll('.entity', '')
    propertyName = GrailsNameUtils.getPropertyNameRepresentation(nameOp)
//    className = GrailsNameUtils.getClassNameRepresentation(nameOp)
    String strDomain = "${propertyName}"
    String strDomainInput = strDomain
    if (strDomain.length() > 1) {
        String temp = strDomain.substring(0, 1).toUpperCase()
        strDomainInput = temp + strDomain.substring(1, strDomain.length())
    }
    nameOp = nameOp.replaceAll('.' + strDomainInput, '')
    nameOp = nameOp.replace('.', '/')
    return nameOp
}

def getActionPackage(def orginalParam) {
    def nameOp = orginalParam
    def rawName = nameOp
    nameOp = nameOp.replaceAll('.entity', '.actions')
//    className = GrailsNameUtils.getClassNameRepresentation(nameOp)
    propertyName = GrailsNameUtils.getPropertyNameRepresentation(nameOp)

    String strDomain = "${propertyName}"
    String strDomainInput = strDomain
    if (strDomain.length() > 1) {
        String temp = strDomain.substring(0, 1).toUpperCase()
        strDomainInput = temp + strDomain.substring(1, strDomain.length())
    }

    nameOp = nameOp.replaceAll(strDomainInput, strDomain.toLowerCase())
    nameOp = nameOp.replace('.', '/')
    return nameOp
}

def moveGeneratedFile(String source, String destination) {
    try {
        def actionFileMove = new FileSystemResource(source)
        if (actionFileMove.exists()) {

            if (new File(destination).exists()) {
                if (!confirmInput("${destination}  already exists. Overwrite? [y/n]", "${destination}.overwrite")) {
                    return
                }
            }

            ant.move(file: "${source}", tofile: "${destination}", overwrite: "true")
        }
        else {
            println("${source} is not available")
        }
    }
    catch (Exception e) {
        println(e)
    }

}

setDefaultTarget(main)
