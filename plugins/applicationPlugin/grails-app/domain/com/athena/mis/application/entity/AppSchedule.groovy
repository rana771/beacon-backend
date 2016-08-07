package com.athena.mis.application.entity
/**
 * <p>
 * <strong>Module:</strong> Application </br>
 * <strong>Purpose:</strong> Create Schedule.
 * AppDbInstance has association with few other domains as listed below.
 * </p>
 *
 * <p><strong>Local Reference:</strong> Has-a relationship with other domains:</p>
 * <ul>
 *     <li>{@link com.athena.mis.application.entity.SystemEntity#id as scheduleTypeId}</li>
 *     <li>{@link com.athena.mis.application.entity.Company#id as companyId}</li>
 *     <li>{@link com.athena.mis.application.entity.AppUser#id as createdBy}</li>
 *     <li>{@link com.athena.mis.application.entity.AppUser#id as updatedBy}</li>
 * </ul>
 *
 */
class AppSchedule {

    long id                         // Primary Key (Auto Generated By it's own sequence)
    long version                    // Entity version in the persistence layer
    String name                     // Unique name of the Schedule within company
    long scheduleTypeId             // SystemEntity.id
    long repeatInterval             // given in ms
    int repeatCount                 // repeat counter
    String cronExpression           // cron expression for schedule
    String jobClassName             // job class name
    boolean enable                  // flag for enable or disable
    String actionName               // action name
    long pluginId                   // plugin id
    long companyId                  // Company.id
    long updatedBy                  // AppUser.id
    Date updatedOn                  // Object updated DateTime

    static mapping = {
        id generator: 'sequence', params: [sequence: 'app_schedule_id_seq']

        // unique index on "name" using AppScheduleService.createDefaultSchema()
        // <domain_name><property_name_1>idx
    }

    static constraints = {
        cronExpression(nullable: true)
        updatedOn(nullable: true)
    }
}
