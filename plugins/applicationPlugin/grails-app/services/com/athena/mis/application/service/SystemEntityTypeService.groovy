package com.athena.mis.application.service

import com.athena.mis.BaseDomainService
import com.athena.mis.PluginConnector
import com.athena.mis.application.entity.SystemEntityType
import com.athena.mis.integration.accounting.AccPluginConnector
import com.athena.mis.integration.arms.ArmsPluginConnector
import com.athena.mis.integration.budget.BudgPluginConnector
import com.athena.mis.integration.exchangehouse.ExchangeHousePluginConnector
import com.athena.mis.integration.inventory.InvPluginConnector
import com.athena.mis.integration.procurement.ProcPluginConnector
import com.athena.mis.integration.sarb.SarbPluginConnector
import org.apache.log4j.Logger

/**
 *  Service class for basic SystemEntityType CRUD (Create, Update, Delete)
 *  For details go through Use-Case doc named 'SystemEntityTypeService'
 */

/**
 * UPDATE THIS DOCUMENT EVERY TIME AFTER CREATING NEW SYSTEM ENTITY TYPE
 * REGISTER ALL INFORMATION FOR LASTLY CREATED SYSTEM ENTITY TYPE
 * LAST COUNTER - 762. so next applicable counter - 763
 * TYPE - RESULT ENTITY
 * PLUGIN - E-LEARNING
 * LAST UPDATED BY - Qutub (13-Aprl-2016)
 */
class SystemEntityTypeService extends BaseDomainService {

    // ALWAYS UPDATE THIS AFTER CREATING NEW ONE
    // private static final long LAST_COUNTER_FOR_TYPE = 762L

    private Logger log = Logger.getLogger(getClass())

    AppVersionService appVersionService

    @Override
    public void init() {
        domainClass = SystemEntityType.class
    }

    /**
     * @return -list of SystemEntityType
     */
    @Override
    public List list() {
        return SystemEntityType.list(sort: SystemEntityType.DEFAULT_SORT_FIELD, order: ASCENDING_SORT_ORDER, readOnly: true);
    }

    private static final String QUERY_CREATE = """INSERT INTO system_entity_type (id, version, description, name, plugin_id)
        VALUES(:id, :version, :description, :name, :pluginId);
    """

    private void create(SystemEntityType systemEntityType) {
        Map qParams = [id: systemEntityType.id, version: systemEntityType.version, description: systemEntityType.description, name: systemEntityType.name, pluginId: systemEntityType.pluginId]
        executeInsertSql(QUERY_CREATE, qParams)
    }

    /**
     * SQL to update SystemEntityType object in database
     * @param seType -SystemEntityType object
     * @return -int value(updateCount)
     */
    public int update(SystemEntityType seType) {
        String query = """
                    UPDATE system_entity_type SET
                          version=version+1,
                          name=:name,
                          description=:description

                      WHERE
                          id=:id AND
                          version=:version
                          """
        Map queryParams = [
                id         : seType.id,
                version    : seType.version,
                name       : seType.name,
                description: seType.description
        ]

        int updateCount = executeUpdateSql(query, queryParams);

        if (updateCount <= 0) {
            throw new RuntimeException('Error occurred while updating information')
        }
        seType.version = seType.version + 1
        return updateCount;
    }

    private static final String COUNT_QUERY = """
           SELECT COUNT(set.id)
           FROM system_entity_type set
           WHERE set.plugin_id = :pluginId
    """

    public int countSystemEntityType(int pluginId) {
        Map queryParams = [
                pluginId: pluginId
        ]
        List countResults = executeSelectSql(COUNT_QUERY, queryParams)
        int count = countResults[0].count
        return count
    }

    public List<SystemEntityType> findAllByPluginId(long pluginId) {
        return SystemEntityType.findAllByPluginId(pluginId, [readOnly: true])
    }

    public boolean createDefaultDataForBudget() {
        try {
            int count = appVersionService.countByPluginId(BudgPluginConnector.PLUGIN_ID)
            if (count > 0) {
                return true
            }
            SystemEntityType budgTaskStatus = new SystemEntityType(version: 0, name: 'Budget Task Status', description: 'Defined, In Progress, Completed', pluginId: 3)
            budgTaskStatus.id = 3721L
            create(budgTaskStatus)
            return true
        } catch (Exception ex) {
            log.error(ex.getMessage())
            throw new RuntimeException(ex)
        }
    }

    /**
     * insert accounting module default data into database when application starts with bootstrap
     */
    public boolean createDefaultDataForAcc() {
        try {
            int count = appVersionService.countByPluginId(AccPluginConnector.PLUGIN_ID)
            if (count > 0) {
                return true
            }
            SystemEntityType accSourceType = new SystemEntityType(version: 0, name: 'Accounting Source Type', description: 'None, Customer, Employee, Sub-Account, Supplier, Material, Fixed-Asset, Work', pluginId: 2)
            accSourceType.id = 51L
            create(accSourceType)

            SystemEntityType accVoucherType = new SystemEntityType(version: 0, name: 'Accounting Voucher Type', description: 'Payment-Voucher-Bank, Payment-Voucher-Cash, Received-Voucher-Bank, Received-Voucher-Cash, Journal', pluginId: 2)
            accVoucherType.id = 101L
            create(accVoucherType)

            SystemEntityType accInstrumentType = new SystemEntityType(version: 0, name: 'Accounting Instrument Type', description: 'Used in Voucher Create - IOU_ID, PO_ID', pluginId: 2)
            accInstrumentType.id = 601L
            create(accInstrumentType)
            return true
        } catch (Exception ex) {
            log.error(ex.getMessage())
            throw new RuntimeException(ex)
        }
    }

    /**
     * insert application module default data into database when application starts with bootstrap
     */
    public boolean createDefaultDataForApp() {
        try {
            int count = appVersionService.countByPluginId(PluginConnector.PLUGIN_ID)
            if (count > 0) {
                return true
            }

            SystemEntityType unit = new SystemEntityType(version: 0, name: 'Unit', description: 'Foot, Ton, Bag, CFT, NOS, K.G., Meter, Liter etc.', pluginId: 1)
            unit.id = 251L
            create(unit)

            SystemEntityType invValuationType = new SystemEntityType(version: 0, name: 'Valuation Type', description: 'FIFO, LIFO, AVG', pluginId: 1)
            invValuationType.id = 451L
            create(invValuationType)

            SystemEntityType ownerType = new SystemEntityType(version: 0, name: 'Owner Type', description: 'Owner Type of a Fixed-Asset - Purchased, Rental', pluginId: 1)
            ownerType.id = 551L
            create(ownerType)

            SystemEntityType userMappingEntityType = new SystemEntityType(version: 0, name: 'App-User Mapping Entity Type', description: 'Customer, Bank-Branch, Project, PtProject, Inventory, Group, Pt Project', pluginId: 1)
            userMappingEntityType.id = 651L
            create(userMappingEntityType)

            SystemEntityType contentEntityType = new SystemEntityType(version: 0, name: 'Content Entity Type', description: 'Project, Company, App User, Bug etc', pluginId: 1)
            contentEntityType.id = 701L
            create(contentEntityType)

            SystemEntityType contentType = new SystemEntityType(version: 0, name: 'Content Type', description: 'Document, Image', pluginId: 1)
            contentType.id = 702L
            create(contentType)

            //703;"e.g. Task, Customer etc.";1800;FALSE;"Entity Type of Comment";1301
            SystemEntityType noteType = new SystemEntityType(version: 0, name: 'Entity Type of Note', description: 'e.g. Task, Customer etc.', pluginId: 1)
            noteType.id = 703L
            create(noteType)

            SystemEntityType supplierType = new SystemEntityType(version: 0, name: 'Supplier Type', description: 'Service provider, Material provider etc', pluginId: 1)
            supplierType.id = 704L
            create(supplierType)

            SystemEntityType itemCategory = new SystemEntityType(version: 0, name: 'Item Category', description: 'Inventory, Non-inventory, Fixed Asset etc.', pluginId: 1)
            itemCategory.id = 705L
            create(itemCategory)

            SystemEntityType gender = new SystemEntityType(version: 0, name: 'Gender', description: 'Male; Female', pluginId: 1)
            gender.id = 1717L
            create(gender)

            SystemEntityType appNoteType = new SystemEntityType(version: 0, name: 'App Note Type', description: 'None, System Note, Verified and Approved', pluginId: 1)
            appNoteType.id = 1729L
            create(appNoteType)

            SystemEntityType benchmarkType = new SystemEntityType(version: 0, name: 'Benchmark Type', description: 'Small, Medium, Large', pluginId: 1)
            benchmarkType.id = 1733L
            create(benchmarkType)

            SystemEntityType dbVendor = new SystemEntityType(version: 0, name: 'Database Vendor', description: 'PostgreSQL, MySQL, Oracle, MS SQL', pluginId: 1)
            dbVendor.id = 1735
            create(dbVendor)

            SystemEntityType scheduleType = new SystemEntityType(version: 0, name: 'Schedule Type', description: 'Simple, Cron', pluginId: 1)
            scheduleType.id = 1736
            create(scheduleType)

            SystemEntityType trustedIpAddress = new SystemEntityType(version: 0, name: 'Trusted IP Address', description: 'Collection of trusted ip address', pluginId: 1)
            trustedIpAddress.id = 1737
            create(trustedIpAddress)

            SystemEntityType transactionLog = new SystemEntityType(version: 0, name: 'Transaction Log Entity Type', description: 'Data Export, Benchmark, Benchmark Star, Data Import', pluginId: 1)
            transactionLog.id = 1738
            create(transactionLog)

            SystemEntityType osVendor = new SystemEntityType(version: 0, name: 'OS Vendor', description: 'Linux, Free BDS', pluginId: 1)
            osVendor.id = 1741
            create(osVendor)

            SystemEntityType dbObjectType = new SystemEntityType(version: 0, name: 'DB Object Type', description: 'Table, View', pluginId: 1)
            dbObjectType.id = 1743
            create(dbObjectType)

            SystemEntityType scriptType = new SystemEntityType(version: 0, name: 'Script Type', description: 'Shell, SQL', pluginId: 1)
            scriptType.id = 1744
            create(scriptType)

            SystemEntityType queryType = new SystemEntityType(version: 0, name: 'Query Type', description: 'Query for diagnostic report etc.', pluginId: 1)
            queryType.id = 1745
            create(queryType)

            SystemEntityType dbInstanceType = new SystemEntityType(version: 0, name: 'DB Instance Type', description: 'DB Instance Type i.e. Source or Target Database', pluginId: 1)
            dbInstanceType.id = 1746
            create(dbInstanceType)

            SystemEntityType mailConfiguration = new SystemEntityType(version: 0, name: 'Mail Configuration', description: 'Configuration for mail e.g. smtp email, password, port, host', pluginId: 1)
            mailConfiguration.id = 1751
            create(mailConfiguration)

            SystemEntityType mimeType = new SystemEntityType(version: 0, name: 'Mime Type', description: 'Supported mime type for attachment ie pdf, doc, xls, jpg etc', pluginId: 1)
            mimeType.id = 1754
            create(mimeType)

            SystemEntityType faqType = new SystemEntityType(version: 0, name: 'FAQ', description: 'e.g. Document', pluginId: 1)
            faqType.id = 1755
            create(faqType)

            SystemEntityType hierarchyLevel = new SystemEntityType(version: 0, name: 'Hierarchy Level', description: 'Hierarchy level of entity eg. sub category', pluginId: 1)
            hierarchyLevel.id = 1757
            create(hierarchyLevel)

            SystemEntityType page = new SystemEntityType(version: 0, name: 'Page Type', description: 'Page Type i.e Blog, Post', pluginId: 1)
            page.id = 1760
            create(page)
            return true
        } catch (Exception ex) {
            log.error(ex.getMessage())
            throw new RuntimeException(ex)
        }
    }

    /**
     * insert inventory module default data into database when application starts with bootstrap
     */
    public boolean createDefaultDataForInv() {
        try {
            int count = appVersionService.countByPluginId(InvPluginConnector.PLUGIN_ID)
            if (count > 0) {
                return true
            }
            SystemEntityType invProdItemType = new SystemEntityType(version: 0, name: 'Inventory Production Item Type', description: 'Raw-Material, Finished-Material', pluginId: 4)
            invProdItemType.id = 151L
            create(invProdItemType)

            SystemEntityType invTransactionType = new SystemEntityType(version: 0, name: 'Inventory Transaction Type', description: 'In, Out, Consumption, Production, Adjustment, Reverse-Adjustment', pluginId: 4)
            invTransactionType.id = 301L
            create(invTransactionType)

            SystemEntityType invTransactionEntityType = new SystemEntityType(version: 0, name: 'Inventory Transaction Entity Type', description: 'None, Inventory, Supplier, Customer', pluginId: 4)
            invTransactionEntityType.id = 351L
            create(invTransactionEntityType)

            SystemEntityType inventoryType = new SystemEntityType(version: 0, name: 'Inventory Type', description: 'Store, Site', pluginId: 4)
            inventoryType.id = 501L
            create(inventoryType)
            return true
        } catch (Exception ex) {
            log.error(ex.getMessage())
            throw new RuntimeException(ex)
        }
    }

    /**
     * insert procurement module default data into database when application starts with bootstrap
     */
    public boolean createDefaultDataForProcurement() {
        try {
            int count = appVersionService.countByPluginId(ProcPluginConnector.PLUGIN_ID)
            if (count > 0) {
                return true
            }
            SystemEntityType paymentMethodType = new SystemEntityType(version: 0, name: 'Payment Method Type', description: 'Used in Procurement for PO Details - Cash, Cheque, LC', pluginId: 5)
            paymentMethodType.id = 1L
            create(paymentMethodType)
            return true
        } catch (Exception ex) {
            log.error(ex.getMessage())
            throw new RuntimeException(ex)
        }
    }

    /**
     * insert exchangeHouse module default data into database when application starts with bootstrap
     */
    public boolean createDefaultDataForExh() {
        try {
            int count = appVersionService.countByPluginId(ExchangeHousePluginConnector.PLUGIN_ID)
            if (count > 0) return true

            SystemEntityType paidByType = new SystemEntityType(version: 0, name: 'Paid By Type', description: 'Cash, Online, Debit Card, Credit Card', pluginId: 9)
            paidByType.id = 2001L
            create(paidByType)

            SystemEntityType paymentMethodType = new SystemEntityType(version: 0, name: 'Payment Method Type', description: 'Bank Deposit, Cash Collection, Mobile, Remittance Card', pluginId: 9)
            paymentMethodType.id = 2002L
            create(paymentMethodType)

            SystemEntityType taskStatusType = new SystemEntityType(version: 0, name: 'Task Status', description: 'Cancelled, New Task, Sent to bank, Sent to other bank, Paid', pluginId: 9)
            taskStatusType.id = 2003L
            create(taskStatusType)

            SystemEntityType taskType = new SystemEntityType(version: 0, name: 'Task Type', description: 'Type of Task; Task of Exh, Task of Agent, Task of Customer', pluginId: 9)
            taskType.id = 2004L
            create(taskType)

            return true
        } catch (Exception ex) {
            log.error(ex.getMessage())
            throw new RuntimeException(ex)
        }
    }

    public void createDefaultDataForProjectTrack() {
        try {
            SystemEntityType backLogPriorityType = new SystemEntityType(version: 0, name: 'Backlog Priority Type', description: 'Backlog priority i.e. high,medium,low', pluginId: 10)
            backLogPriorityType.id = 10706L
            create(backLogPriorityType)

            SystemEntityType backlogStatusType = new SystemEntityType(version: 0, name: 'Backlog Status Type', description: 'Backlog status i.e. defined,in progress,completed,accepted', pluginId: 10)
            backlogStatusType.id = 10707L
            create(backlogStatusType)

            SystemEntityType acceptanceCriteriaStatusType = new SystemEntityType(version: 0, name: 'Acceptance criteria  status type', description: 'Acceptance criteria status i.e. defined,in progress,completed,blocked', pluginId: 10)
            acceptanceCriteriaStatusType.id = 10711L
            create(acceptanceCriteriaStatusType)

            SystemEntityType sprintStatusType = new SystemEntityType(version: 0, name: 'Sprint Status Type', description: 'defined,in progress,completed', pluginId: 10)
            sprintStatusType.id = 10708L
            create(sprintStatusType)

            SystemEntityType bugSeverity = new SystemEntityType(version: 0, name: 'Bug Severity Type', description: 'high,medium,low', pluginId: 10)
            bugSeverity.id = 10709L
            create(bugSeverity)

            SystemEntityType bugStatus = new SystemEntityType(version: 0, name: 'Bug Status Type', description: 'submitted,re-opened,fixed,closed', pluginId: 10)
            bugStatus.id = 10710L
            create(bugStatus)

            SystemEntityType bugType = new SystemEntityType(version: 0, name: 'Bug Type', description: 'functional,user interface,inconsistency,performance,suggestion', pluginId: 10)
            bugType.id = 10712L
            create(bugType)

            SystemEntityType acceptanceCriteriaType = new SystemEntityType(version: 0, name: 'Acceptance Criteria Type', description: 'pre-condition, business logic, post-condition', pluginId: 10)
            acceptanceCriteriaType.id = 10718L
            create(acceptanceCriteriaType)

            SystemEntityType backlogChangeRequestType = new SystemEntityType(version: 0, name: 'Backlog Change Request Type', description: 'Proposed, Approved, Denied', pluginId: 10)
            backlogChangeRequestType.id = 10734L
            create(backlogChangeRequestType)

            SystemEntityType entityRelationType = new SystemEntityType(version: 0, name: 'Entity Relation Type', description: 'One-to-many, Many-to-many', pluginId: 10)
            entityRelationType.id = 10752L
            create(entityRelationType)

            SystemEntityType dataType = new SystemEntityType(version: 0, name: 'Data Type', description: 'Integer, Long, Float, Double, Character, String, Boolean.', pluginId: 10)
            dataType.id = 10753L
            create(dataType)
        } catch (Exception ex) {
            log.error(ex.getMessage())
            throw new RuntimeException(ex)
        }
    }

    public boolean createDefaultDataForArms() {
        try {
            int count = appVersionService.countByPluginId(ArmsPluginConnector.PLUGIN_ID)
            if (count > 0) return true

            SystemEntityType rmsProcessType = new SystemEntityType(version: 0, name: 'Process Type', description: 'Process i.e. Issue, Forward, Purchase', pluginId: 11)
            rmsProcessType.id = 11713
            create(rmsProcessType)

            SystemEntityType rmsInstrumentType = new SystemEntityType(version: 0, name: 'Instrument Type', description: 'Instrument i.e. PO, EFT, Online, Cash Collection', pluginId: 11)
            rmsInstrumentType.id = 11714
            create(rmsInstrumentType)

            SystemEntityType rmsPaymentMethod = new SystemEntityType(version: 0, name: 'Payment Method', description: 'Payment Method i.e. Bank deposit, Cash collection', pluginId: 11)
            rmsPaymentMethod.id = 11715
            create(rmsPaymentMethod)

            SystemEntityType rmsTaskStatus = new SystemEntityType(version: 0, name: 'Task Status', description: 'Task Status i.e. New task, Included in list, Decision taken etc.', pluginId: 11)
            rmsTaskStatus.id = 11716
            create(rmsTaskStatus)

            return true
        } catch (Exception ex) {
            log.error(ex.getMessage())
            throw new RuntimeException(ex)
        }
    }

    public void createDefaultDataForDocument() {
        try {
            SystemEntityType answerType = new SystemEntityType(version: 0, name: 'Answer Type', description: 'Check Box, Radio Button, Text Area', pluginId: 13)
            answerType.id = 13725
            create(answerType)

            SystemEntityType difficultyLevel = new SystemEntityType(version: 0, name: 'Difficulty Level', description: 'Hard, Moderate, Easy', pluginId: 13)
            difficultyLevel.id = 13726
            create(difficultyLevel)

            SystemEntityType documentStatus = new SystemEntityType(version: 0, name: 'Document Status', description: 'New Document, Request for index, Indexed etc.', pluginId: 13)
            documentStatus.id = 13731
            create(documentStatus)

            SystemEntityType documentIndexType = new SystemEntityType(version: 0, name: 'Document Index Type', description: 'None, Application, OCR', pluginId: 13)
            documentIndexType.id = 13732
            create(documentIndexType)

            SystemEntityType documentCategoryType = new SystemEntityType(version: 0, name: 'Document Category Type', description: 'Article, File, Image, Audio, Video', pluginId: 13)
            documentCategoryType.id = 13740
            create(documentCategoryType)
        } catch (Exception ex) {
            log.error(ex.getMessage())
            throw new RuntimeException(ex)
        }
    }
    
    public void createDefaultDataForElearning() {
        try {
            SystemEntityType languageType = new SystemEntityType(version: 0, name: 'Language Type', description: 'Bengali, English', pluginId: 15)
            languageType.id = 15756
            create(languageType)

            SystemEntityType courseStatus = new SystemEntityType(version: 0, name: 'Course Status', description: 'Running, Upcoming, Past, Watchlist', pluginId: 15)
            courseStatus.id = 15758
            create(courseStatus)

            SystemEntityType assignmentType = new SystemEntityType(version: 0, name: 'Assignment Type', description: 'Individual, Group', pluginId: 15)
            assignmentType.id = 15759
            create(assignmentType)

            SystemEntityType resultEntity = new SystemEntityType(version: 0, name: 'Result Entity', description: 'Exam, Quiz, Assignment, Activity', pluginId: 15)
            resultEntity.id = 15761
            create(resultEntity)

            SystemEntityType userPointType = new SystemEntityType(version: 0, name: 'User Point Type', description: 'Registration, Profile Update, Daily Login, Discussion Writing', pluginId: 15)
            userPointType.id = 15762
            create(userPointType)
        } catch (Exception ex) {
            log.error(ex.getMessage())
            throw new RuntimeException(ex)
        }
    }

    public boolean createDefaultDataForSarb() {
        try {
            int count = appVersionService.countByPluginId(SarbPluginConnector.PLUGIN_ID)
            if (count > 0) return true

            SystemEntityType sarbTaskReviseStatus = new SystemEntityType(version: 0, name: 'Sarb task revise status', description: 'Moved for Cancel, Replace', pluginId: 12)
            sarbTaskReviseStatus.id = 12723
            create(sarbTaskReviseStatus)

            SystemEntityType sarbTaskCurrentStatus = new SystemEntityType(version: 0, name: 'Sarb task current status', description: 'Regular, Cancel, Replace, Refund', pluginId: 12)
            sarbTaskCurrentStatus.id = 12727
            create(sarbTaskCurrentStatus)

            SystemEntityType sarbCurrencyConversionType = new SystemEntityType(version: 0, name: 'Sarb Currency Conversion Type', description: 'Buy, Sell', pluginId: 12)
            sarbCurrencyConversionType.id = 12728
            create(sarbCurrencyConversionType)

            return true
        } catch (Exception ex) {
            log.error(ex.getMessage())
            throw new RuntimeException(ex)
        }
    }

    public void createDefaultDataForDataPipeLine() {
        try {

            SystemEntityType offlineDataFeedStatus = new SystemEntityType(version: 0, name: 'Offline Data Feed Status', description: 'Received, Pre-Process, Loaded', pluginId: 14)
            offlineDataFeedStatus.id = 14739
            create(offlineDataFeedStatus)

            SystemEntityType dataTypeMapping = new SystemEntityType(version: 0, name: 'Data Type Mapping for MsSql to GreenPlum', description: 'Data Type mapping of MS SQL Server 2008 to Green Plum', pluginId: 14)
            dataTypeMapping.id = 14747
            create(dataTypeMapping)

            SystemEntityType dataTypeMappingMsSqlToRedShift = new SystemEntityType(version: 0, name: 'Data Type Mapping for MsSql to RedShift', description: 'Data Type mapping of MS SQL Server 2008 to RedShift', pluginId: 14)
            dataTypeMappingMsSqlToRedShift.id = 14748
            create(dataTypeMappingMsSqlToRedShift)

            SystemEntityType dataTypeMappingMySqlToGreenPlum = new SystemEntityType(version: 0, name: 'Data Type Mapping for MySql to GreenPlum', description: 'Data Type mapping of MY SQL Server to Green Plum', pluginId: 14)
            dataTypeMappingMySqlToGreenPlum.id = 14749
            create(dataTypeMappingMySqlToGreenPlum)

            SystemEntityType dataTypeMappingMySqlToRedShift = new SystemEntityType(version: 0, name: 'Data Type Mapping for MySql to RedShift', description: 'Data Type mapping of MY SQL Server to RedShift', pluginId: 14)
            dataTypeMappingMySqlToRedShift.id = 14750
            create(dataTypeMappingMySqlToRedShift)

            SystemEntityType dataFeedType = new SystemEntityType(version: 0, name: 'Data Feed Type', description: 'Data feed type for data export', pluginId: 14)
            dataFeedType.id = 14751
            create(dataFeedType)

            SystemEntityType dataFeedTypeCsv = new SystemEntityType(version: 0, name: 'CSV Data Feed', description: 'Data feed in csv format', pluginId: 14)
            dataFeedTypeCsv.id = 14752
            create(dataFeedTypeCsv)

            SystemEntityType dataFeedTypeTxt = new SystemEntityType(version: 0, name: 'TEXT Data Feed', description: 'Data feed in text format', pluginId: 14)
            dataFeedTypeTxt.id = 14753
            create(dataFeedTypeTxt)
        } catch (Exception ex) {
            log.error(ex.getMessage())
            throw new RuntimeException(ex)
        }

    }

    @Override
    public void createDefaultSchema() {
        String sqlIndex = "CREATE UNIQUE INDEX system_entity_type_name_plugin_id_idx ON system_entity_type(lower(name),plugin_id);"
        executeSql(sqlIndex)
    }

    @Override
    public boolean createTestData(long companyId, long systemUserId) { return true }
}
