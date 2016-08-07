package com.athena.mis.application.controller

/**
 * Created by Asif on 11/26/2015.
 */

import com.athena.mis.BaseService
import com.athena.mis.application.actions.project.CreateProjectActionService
import com.athena.mis.application.actions.project.DeleteProjectActionService
import com.athena.mis.application.actions.project.ListProjectActionService
import com.athena.mis.application.actions.project.UpdateProjectActionService
import com.athena.mis.application.entity.AppUser
import com.athena.mis.application.entity.Project
import com.athena.mis.application.service.AppUserEntityService
import com.athena.mis.application.service.AppBankBranchService
import com.athena.mis.application.service.ProjectService
import com.athena.mis.application.service.UserRoleService

//import com.athena.mis.application.utility.AppUserEntityTypeCacheUtility
import grails.test.mixin.Mock
import grails.test.mixin.TestFor
import spock.lang.Specification
import com.athena.mis.application.session.AppSessionService

import javax.sql.DataSource

/**
 * See the API for {@link grails.test.mixin.support.GrailsUnitTestMixin} for usage instructions
 */

@TestFor(ProjectController)

@Mock([
        Project,
        AppUser,
        CreateProjectActionService,
        UpdateProjectActionService,
        ListProjectActionService,
        ProjectService,
        BaseService,
        AppSessionService
])

class ProjectControllerSpec extends Specification {


    void setup() {
        AppUser appUser = new AppUser();
        appUser.id = 1;

        controller.createProjectActionService.appSessionService.setAppUser(appUser)
        controller.updateProjectActionService.appSessionService.setAppUser(appUser)
        controller.listProjectActionService.appSessionService.setAppUser(appUser)
    }

    def "Test Show Action"() {
        setup:
        controller.params.oId = "ATHENA_ID"

        when:
        request.method = "POST"
        controller.show()

        then:
        model.oId == "ATHENA_ID"
    }

    def 'test create action'() {

        setup:
        controller.params.code = "testCode"
        controller.params.name = "Rana"
        controller.params.description = "N/A"
        controller.params.companyId = 1
        controller.params.contentCount = 1
        controller.params.startDate = "10/10/2015"
        controller.params.endDate = "10/10/2015"

        controller.params.isApproveInFromSupplier = false
        controller.params.isApproveInFromInventory = false
        controller.params.isApproveInvOut = false
        controller.params.isApproveConsumption = false
        controller.params.isApproveProduction = false

        when:
        request.method = 'POST'
        controller.create()

        then:
        response.redirectUrl == null
        response.json.message == "Project has been saved successfully"
        response.json.isError == false
    }

    def 'test update action'() {
        setup:
        project.id = 2
        controller.params.name = "Updated After Insert"
        controller.params.version = "0"
        controller.params.startDate = "10/10/2015"
        controller.params.endDate = "10/10/2015"
        controller.params.code = "Updated Code"
        controller.params.id = project.id

        when:
        project.save(flush: true, failOnError: true, validate: false);

        request.method = 'POST'
        controller.update()


        then:
        response.redirectUrl == null
        response.json.message == "Project has been updated successfully"
        response.json.isError == false

        where:

        project = new Project(
                code: "Update Test",
                name: "Insert for Update",
                version: 1,
                description: 'N/A',
                createdOn: new Date(),
                createdBy: 1,
                updatedOn: new Date(),
                updatedBy: 1,
                companyId: 1,
                contentCount: 1,
                startDate: new Date(),
                endDate: new Date(),
                isApproveInFromSupplier: false,
                isApproveInFromInventory: false,
                isApproveInvOut: false,
                isApproveConsumption: false,
                isApproveProduction: false
        )

    }

    def 'test list action'() {
        setup:
        Project project = new Project(
                code: "Update Test",
                name: "Insert for Update",
                version: 1,
                description: 'N/A',
                createdOn: new Date(),
                createdBy: 1,
                updatedOn: new Date(),
                updatedBy: 1,
                companyId: 1,
                contentCount: 1,
                startDate: new Date(),
                endDate: new Date(),
                isApproveInFromSupplier: false,
                isApproveInFromInventory: false,
                isApproveInvOut: false,
                isApproveConsumption: false,
                isApproveProduction: false
        )

        project.id = 1
        project.save();

        controller.params.page = 1
        controller.params.pageSize = 10
        controller.params."sort[0]['dir']" = 'asc'
        controller.params."sort[0]['field']" = 'name'
        controller.params.take = 10
        controller.params.skip = 0

        when:
        request.method = 'POST'
        controller.list()

        then:
        response.json.isError == false
        response.json.count >= 0
    }
}