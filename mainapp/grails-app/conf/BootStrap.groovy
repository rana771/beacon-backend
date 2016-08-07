import com.athena.mis.application.service.ApplicationSessionService
import com.athena.mis.integration.application.AppBootStrapService
import grails.converters.JSON

class BootStrap {

    ApplicationSessionService applicationSessionService
    AppBootStrapService appBootStrapService

    def init = { servletContext ->
        servletContext.addListener(applicationSessionService)

        JSON.registerObjectMarshaller(Date) {
            if (!it) return null
            if (it.format("mm:ss").equals("00:00")) {
                return it.format("yyyy-MM-dd")
            }
            return it.format("yyyy-MM-dd'T'HH:mm:ss")
        }

        appBootStrapService.init()
    }
}
