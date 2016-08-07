package ${packageName.replaceAll(".entity",".controller")}

import ${packageName.replaceAll('.entity','.actions')}.${className.toLowerCase()}.Create${className}ActionService
import ${packageName.replaceAll('.entity','.actions')}.${className.toLowerCase()}.Delete${className}ActionService
import ${packageName.replaceAll('.entity','.actions')}.${className.toLowerCase()}.List${className}ActionService
import ${packageName.replaceAll('.entity','.actions')}.${className.toLowerCase()}.Update${className}ActionService

class ${className}Controller extends BaseController {

    Create${className}ActionService create${className}ActionService
    Update${className}ActionService update${className}ActionService
    List${className}ActionService list${className}ActionService
    Delete${className}ActionService delete${className}ActionService

    static allowedMethods = [
            show: "POST", create: "POST", update: "POST", delete: "POST", list: "POST"
    ]

    /**
     * Show project UI
     */
    def show() {
        render(view: "/application/${className.toLowerCase()}/show", model: [oId: params.oId?params.oId:null])
    }

    /**
     * Create new project
     */
    def create() {
        renderOutput(create${className}ActionService, params)
    }

    /**
     * Update project
     */
    def update() {
        renderOutput(update${className}ActionService, params)
    }

    /**
     * Delete project
     *
     */
    def delete() {
        renderOutput(delete${className}ActionService, params)
    }

    /**
     * List & Search project
     */
    def list() {
        renderOutput(list${className}ActionService, params)
    }
}

