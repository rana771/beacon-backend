package com.athena.mis.application.service

import com.athena.mis.BaseDomainService
import com.athena.mis.PluginConnector
import com.athena.mis.application.entity.ReservedSystemEntity
import com.athena.mis.integration.arms.ArmsPluginConnector
import com.athena.mis.integration.budget.BudgPluginConnector
import com.athena.mis.integration.exchangehouse.ExchangeHousePluginConnector
import com.athena.mis.integration.inventory.InvPluginConnector
import com.athena.mis.integration.sarb.SarbPluginConnector
import org.apache.log4j.Logger

/**
 *  Service class for basic ReservedSystemEntity CRUD
 *  For details go through Use-Case doc named 'ReservedSystemEntityService'
 */

/**
 * UPDATE THIS DOCUMENT EVERY TIME AFTER CREATING NEW RESERVED SYSTEM ENTITY
 * REGISTER ALL INFORMATION FOR LASTLY CREATED RESERVED SYSTEM ENTITY
 * COUNTER must be in 7 digits. (new decision taken on 11-12-2014)
 * LAST COUNTER - 0000366. so next applicable counter - 0000376
 * RESERVED SYSTEM ENTITY - SYS_ENTITY_TYPE_USER_POINT
 * PLUGIN - E-Learning
 * LAST UPDATED BY - Qutub (13-April-2016)
 */
class ReservedSystemEntityService extends BaseDomainService {

    private Logger log = Logger.getLogger(getClass())

    AppVersionService appVersionService

    @Override
    public void init() {
        domainClass = ReservedSystemEntity.class
    }

    // ALWAYS UPDATE THIS AFTER CREATING NEW ONE
    /*##  LAST_COUNTER_FOR_RESERVED = 0000376 ##*/

    private static final String QUERY_CREATE = """INSERT INTO reserved_system_entity (id, key, type, value, plugin_id)
        VALUES(:id, :key, :type, :value, :pluginId);
    """

    private void create(ReservedSystemEntity reservedSystemEntity) {
        Map qParams = [id: reservedSystemEntity.id, key: reservedSystemEntity.key, type: reservedSystemEntity.type, value: reservedSystemEntity.value, pluginId: reservedSystemEntity.pluginId]
        executeInsertSql(QUERY_CREATE, qParams)
    }

    public List<ReservedSystemEntity> findAllByPluginId(int pluginConnector) {
        List<ReservedSystemEntity> lstReservedSystemEntity = ReservedSystemEntity.findAllByPluginId(pluginConnector, [readOnly: true])
        return lstReservedSystemEntity
    }

    public boolean createDefaultDataForBudget() {
        try {
            int count = appVersionService.countByPluginId(BudgPluginConnector.PLUGIN_ID)
            if (count > 0) {
                return true
            }
            ReservedSystemEntity budgTaskStatusDefined = new ReservedSystemEntity(key: 'Defined', value: 'Defined', type: 3721, pluginId: 3)
            budgTaskStatusDefined.id = 389L
            create(budgTaskStatusDefined)
            ReservedSystemEntity budgTaskStatusInProgress = new ReservedSystemEntity(key: 'In Progress', value: 'In Progress', type: 3721, pluginId: 3)
            budgTaskStatusInProgress.id = 390L
            create(budgTaskStatusInProgress)
            ReservedSystemEntity budgTaskStatusCompleted = new ReservedSystemEntity(key: 'Completed', value: 'Completed', type: 3721, pluginId: 3)
            budgTaskStatusCompleted.id = 391L
            create(budgTaskStatusCompleted)

            return true
        } catch (Exception ex) {
            log.error(ex.getMessage())
            throw new RuntimeException(ex)
        }
    }

    public void createDefaultDataForAccounting() {
        ReservedSystemEntity none = new ReservedSystemEntity(key: 'None', value: 'None', type: 51, pluginId: 2)
        none.id = 21
        create(none)
        ReservedSystemEntity customer = new ReservedSystemEntity(key: 'Customer', value: 'Customer', type: 51, pluginId: 2)
        customer.id = 22
        create(customer)
        ReservedSystemEntity employee = new ReservedSystemEntity(key: 'Employee', value: 'Employee', type: 51, pluginId: 2)
        employee.id = 23
        create(employee)
        ReservedSystemEntity subAccount = new ReservedSystemEntity(key: 'Sub-Account', value: 'Sub-Account', type: 51, pluginId: 2)
        subAccount.id = 24
        create(subAccount)
        ReservedSystemEntity supplier = new ReservedSystemEntity(key: 'Supplier', value: 'Supplier', type: 51, pluginId: 2)
        supplier.id = 25
        create(supplier)
        ReservedSystemEntity item = new ReservedSystemEntity(key: 'Item', value: 'Item', type: 51, pluginId: 2)
        item.id = 26
        create(item)
        ReservedSystemEntity lc = new ReservedSystemEntity(key: 'LC', value: 'LC', type: 51, pluginId: 2)
        lc.id = 27
        create(lc)
        ReservedSystemEntity ipc = new ReservedSystemEntity(key: 'IPC', value: 'IPC', type: 51, pluginId: 2)
        ipc.id = 28
        create(ipc)
        ReservedSystemEntity leaseAccount = new ReservedSystemEntity(key: 'Lease Account', value: 'Lease Account', type: 51, pluginId: 2)
        leaseAccount.id = 29
        create(leaseAccount)

        ReservedSystemEntity pb = new ReservedSystemEntity(key: 'Payment Voucher-Bank', value: 'PB', type: 101, pluginId: 2)
        pb.id = 210
        create(pb)
        ReservedSystemEntity pc = new ReservedSystemEntity(key: 'Payment Voucher-Cash', value: 'PC', type: 101, pluginId: 2)
        pc.id = 211
        create(pc)
        ReservedSystemEntity rb = new ReservedSystemEntity(key: 'Received Voucher-Bank', value: 'RB', type: 101, pluginId: 2)
        rb.id = 212
        create(rb)
        ReservedSystemEntity rc = new ReservedSystemEntity(key: 'Received Voucher-Cash', value: 'RC', type: 101, pluginId: 2)
        rc.id = 213
        create(rc)
        ReservedSystemEntity jr = new ReservedSystemEntity(key: 'Journal', value: 'JR', type: 101, pluginId: 2)
        jr.id = 214
        create(jr)

        ReservedSystemEntity iouTrace = new ReservedSystemEntity(key: 'IOU Trace', value: 'IOU Trace', type: 601, pluginId: 2)
        iouTrace.id = 234
        create(iouTrace)
        ReservedSystemEntity poTrace = new ReservedSystemEntity(key: 'PO Trace', value: 'PO Trace', type: 601, pluginId: 2)
        poTrace.id = 235
        create(poTrace)
    }

    public boolean createDefaultDataForInventory() {
        try {
            int count = appVersionService.countByPluginId(InvPluginConnector.PLUGIN_ID)
            if (count > 0) {
                return true
            }
            ReservedSystemEntity rawMaterial = new ReservedSystemEntity(key: 'Raw Material', value: 'Raw Material', type: 151, pluginId: 4)
            rawMaterial.id = 415
            create(rawMaterial)
            ReservedSystemEntity finishedProduct = new ReservedSystemEntity(key: 'Finished Product', value: 'Finished Product', type: 151, pluginId: 4)
            finishedProduct.id = 416
            create(finishedProduct)

            ReservedSystemEntity invIn = new ReservedSystemEntity(key: 'IN', value: 'IN', type: 301, pluginId: 4)
            invIn.id = 417
            create(invIn)
            ReservedSystemEntity invOut = new ReservedSystemEntity(key: 'OUT', value: 'OUT', type: 301, pluginId: 4)
            invOut.id = 418
            create(invOut)
            ReservedSystemEntity consumption = new ReservedSystemEntity(key: 'Consumption', value: 'Consumption', type: 301, pluginId: 4)
            consumption.id = 419
            create(consumption)
            ReservedSystemEntity production = new ReservedSystemEntity(key: 'Production', value: 'Production', type: 301, pluginId: 4)
            production.id = 420
            create(production)
            ReservedSystemEntity adjustment = new ReservedSystemEntity(key: 'Adjustment', value: 'Adjustment', type: 301, pluginId: 4)
            adjustment.id = 421
            create(adjustment)
            ReservedSystemEntity revAdjustment = new ReservedSystemEntity(key: 'Reverse Adjustment', value: 'Reverse Adjustment', type: 301, pluginId: 4)
            revAdjustment.id = 422
            create(revAdjustment)

            ReservedSystemEntity inventory = new ReservedSystemEntity(key: 'Inventory', value: 'Inventory', type: 351, pluginId: 4)
            inventory.id = 423
            create(inventory)
            ReservedSystemEntity supplier = new ReservedSystemEntity(key: 'Supplier', value: 'Supplier', type: 351, pluginId: 4)
            supplier.id = 424
            create(supplier)
            ReservedSystemEntity none = new ReservedSystemEntity(key: 'None', value: 'None', type: 351, pluginId: 4)
            none.id = 425
            create(none)
            ReservedSystemEntity customer = new ReservedSystemEntity(key: 'Customer', value: 'Customer', type: 351, pluginId: 4)
            customer.id = 426
            create(customer)

            ReservedSystemEntity store = new ReservedSystemEntity(key: 'STORE', value: 'STORE', type: 501, pluginId: 4)
            store.id = 430
            create(store)
            ReservedSystemEntity site = new ReservedSystemEntity(key: 'SITE', value: 'SITE', type: 501, pluginId: 4)
            site.id = 431
            create(site)

            return true
        } catch (Exception ex) {
            log.error(ex.getMessage())
            throw new RuntimeException(ex)
        }
    }

    public boolean createDefaultDataForApplication() {
        try {
            int count = appVersionService.countByPluginId(PluginConnector.PLUGIN_ID)
            if (count > 0) {
                return true
            }

            ReservedSystemEntity fifo = new ReservedSystemEntity(key: 'FIFO', value: 'FIFO', type: 451, pluginId: 1)
            fifo.id = 127
            create(fifo)
            ReservedSystemEntity lifo = new ReservedSystemEntity(key: 'LIFO', value: 'LIFO', type: 451, pluginId: 1)
            lifo.id = 128
            create(lifo)
            ReservedSystemEntity avg = new ReservedSystemEntity(key: 'AVG', value: 'AVG', type: 451, pluginId: 1)
            avg.id = 129
            create(avg)

            ReservedSystemEntity purchased = new ReservedSystemEntity(key: 'Purchased', value: 'Purchased', type: 551, pluginId: 1)
            purchased.id = 132
            create(purchased)
            ReservedSystemEntity rental = new ReservedSystemEntity(key: 'Rental', value: 'Rental', type: 551, pluginId: 1)
            rental.id = 133
            create(rental)

            ReservedSystemEntity customer = new ReservedSystemEntity(key: 'Customer', value: 'Customer', type: 651, pluginId: 1)
            customer.id = 136
            create(customer)
            ReservedSystemEntity bankBranch = new ReservedSystemEntity(key: 'Bank Branch', value: 'Bank Branch', type: 651, pluginId: 1)
            bankBranch.id = 137
            create(bankBranch)
            ReservedSystemEntity project = new ReservedSystemEntity(key: 'Project', value: 'Project', type: 651, pluginId: 1)
            project.id = 138
            create(project)
            ReservedSystemEntity inventory = new ReservedSystemEntity(key: 'Inventory', value: 'Inventory', type: 651, pluginId: 1)
            inventory.id = 139
            create(inventory)
            ReservedSystemEntity group = new ReservedSystemEntity(key: 'Group', value: 'Group', type: 651, pluginId: 1)
            group.id = 140
            create(group)
            ReservedSystemEntity agent = new ReservedSystemEntity(key: 'Agent', value: 'Agent', type: 651, pluginId: 1)
            agent.id = 141
            create(agent)

            ReservedSystemEntity user = new ReservedSystemEntity(key: 'AppUser', value: 'AppUser', type: 701, pluginId: 1)
            user.id = 142
            create(user)
            ReservedSystemEntity company = new ReservedSystemEntity(key: 'Company', value: 'Company', type: 701, pluginId: 1)
            company.id = 143
            create(company)
            ReservedSystemEntity customerExh = new ReservedSystemEntity(key: 'Customer (Exh)', value: 'Customer (Exh)', type: 701, pluginId: 1)
            customerExh.id = 144
            create(customerExh)
            ReservedSystemEntity entityProject = new ReservedSystemEntity(key: 'Project', value: 'Project', type: 701, pluginId: 1)
            entityProject.id = 145
            create(entityProject)
            ReservedSystemEntity entityBudget = new ReservedSystemEntity(key: 'BOQ Line Item', value: 'BOQ Line Item', type: 701, pluginId: 1)
            entityBudget.id = 184
            create(entityBudget)
            ReservedSystemEntity entityBudgSprint = new ReservedSystemEntity(key: 'Sprint', value: 'Sprint', type: 701, pluginId: 1)
            entityBudgSprint.id = 192
            create(entityBudgSprint)
            ReservedSystemEntity entityFinancialYear = new ReservedSystemEntity(key: 'Financial Year', value: 'Financial Year', type: 701, pluginId: 1)
            entityFinancialYear.id = 185
            create(entityFinancialYear)

            ReservedSystemEntity entityMail = new ReservedSystemEntity(key: 'Mail', value: 'Attachment for mail', type: 701, pluginId: 1)
            entityMail.id = 10000322L
            create(entityMail)

            ReservedSystemEntity userContent = new ReservedSystemEntity(key: 'User Document', value: 'Attachment for User Document', type: 701, pluginId: 1)
            userContent.id = 10000363L
            create(userContent)

            ReservedSystemEntity document = new ReservedSystemEntity(key: 'Document', value: 'Document', type: 702, pluginId: 1)
            document.id = 146
            create(document)
            ReservedSystemEntity image = new ReservedSystemEntity(key: 'Image', value: 'Image', type: 702, pluginId: 1)
            image.id = 147
            create(image)

            ReservedSystemEntity task = new ReservedSystemEntity(key: 'EXH TASK', value: 'Note Entity Type Exh Task', type: 703, pluginId: 1)
            task.id = 148
            create(task)
            ReservedSystemEntity customerNote = new ReservedSystemEntity(key: 'CUSTOMER', value: 'Note Entity Type Customer', type: 703, pluginId: 1)
            customerNote.id = 149
            create(customerNote)
            ReservedSystemEntity dbInstanceQuery = new ReservedSystemEntity(key: 'DB INSTANCE QUERY', value: 'Note Entity Type db instance query', type: 703, pluginId: 1)
            dbInstanceQuery.id = 10000183L
            create(dbInstanceQuery)

            ReservedSystemEntity noteForScript = new ReservedSystemEntity(key: 'Script', value: 'Note for script', type: 703, pluginId: 1)
            noteForScript.id = 10000185L
            create(noteForScript)

            ReservedSystemEntity inventoryItem = new ReservedSystemEntity(key: 'Inventory', value: 'Inventory', type: 705, pluginId: 1)
            inventoryItem.id = 150
            create(inventoryItem)
            ReservedSystemEntity nonInventory = new ReservedSystemEntity(key: 'Non Inventory', value: 'Non Inventory', type: 705, pluginId: 1)
            nonInventory.id = 151
            create(nonInventory)
            ReservedSystemEntity fixedAsset = new ReservedSystemEntity(key: 'Fixed Asset', value: 'Fixed Asset', type: 705, pluginId: 1)
            fixedAsset.id = 152
            create(fixedAsset)

            ReservedSystemEntity male = new ReservedSystemEntity(key: 'Male', value: 'M', type: 1717, pluginId: 1)
            male.id = 176
            create(male)
            ReservedSystemEntity female = new ReservedSystemEntity(key: 'Female', value: 'F', type: 1717, pluginId: 1)
            female.id = 177
            create(female)

            ReservedSystemEntity none = new ReservedSystemEntity(key: 'None', value: 'None', type: 1729, pluginId: 1)
            none.id = 1118
            create(none)
            ReservedSystemEntity systemNote = new ReservedSystemEntity(key: 'System Note', value: 'System Note', type: 1729, pluginId: 1)
            systemNote.id = 1119
            create(systemNote)
            ReservedSystemEntity verifiedAndApproved = new ReservedSystemEntity(key: 'Verified And Approved', value: 'Verified And Approved', type: 1729, pluginId: 1)
            verifiedAndApproved.id = 1120
            create(verifiedAndApproved)

            ReservedSystemEntity benchmarkSmall = new ReservedSystemEntity(key: 'Small', value: 'Small', type: 1733, pluginId: 1)
            benchmarkSmall.id = 1137
            create(benchmarkSmall)
            ReservedSystemEntity benchmarkMedium = new ReservedSystemEntity(key: 'Medium', value: 'Medium', type: 1733, pluginId: 1)
            benchmarkMedium.id = 1138
            create(benchmarkMedium)
            ReservedSystemEntity benchmarkLarge = new ReservedSystemEntity(key: 'Large', value: 'Large', type: 1733, pluginId: 1)
            benchmarkLarge.id = 1139
            create(benchmarkLarge)

            ReservedSystemEntity vendorPostgre = new ReservedSystemEntity(key: 'PostgreSQL', value: 'org.postgresql.Driver', type: 1735, pluginId: 1)
            vendorPostgre.id = 1143L
            create(vendorPostgre)
            ReservedSystemEntity vendorMysql = new ReservedSystemEntity(key: 'MySQL', value: 'com.mysql.jdbc.Driver', type: 1735, pluginId: 1)
            vendorMysql.id = 1144L
            create(vendorMysql)
            ReservedSystemEntity vendorOracle = new ReservedSystemEntity(key: 'Oracle', value: 'oracle.jdbc.driver.OracleDriver', type: 1735, pluginId: 1)
            vendorOracle.id = 1145L
            create(vendorOracle)
            ReservedSystemEntity vendorMssql2008 = new ReservedSystemEntity(key: 'MS SQLServer 2008', value: 'net.sourceforge.jtds.jdbc.Driver', type: 1735, pluginId: 1)
            vendorMssql2008.id = 1146L
            create(vendorMssql2008)
            ReservedSystemEntity vendorMssql2012 = new ReservedSystemEntity(key: 'MS SQLServer 2012', value: 'net.sourceforge.jtds.jdbc.Driver', type: 1735, pluginId: 1)
            vendorMssql2012.id = 10000305L
            create(vendorMssql2012)
            ReservedSystemEntity vendorAmazonRedShift = new ReservedSystemEntity(key: 'Amazon RedShift', value: 'com.amazon.redshift.jdbc4.Driver', type: 1735, pluginId: 1)
            vendorAmazonRedShift.id = 1147L
            create(vendorAmazonRedShift)
            ReservedSystemEntity greenPlum = new ReservedSystemEntity(key: 'Greenplum', value: 'org.postgresql.Driver', type: 1735, pluginId: 1)
            greenPlum.id = 10000189L
            create(greenPlum)

            ReservedSystemEntity scheduleTypeSimple = new ReservedSystemEntity(key: 'Simple', value: 'Simple', type: 1736, pluginId: 1)
            scheduleTypeSimple.id = 1148L
            create(scheduleTypeSimple)
            ReservedSystemEntity scheduleTypeCron = new ReservedSystemEntity(key: 'Cron', value: 'Cron', type: 1736, pluginId: 1)
            scheduleTypeCron.id = 1149L
            create(scheduleTypeCron)

            ReservedSystemEntity dataExport = new ReservedSystemEntity(key: 'Data Export', value: 'Data Export', type: 1738, pluginId: 1)
            dataExport.id = 10000151L
            create(dataExport)
            ReservedSystemEntity benchmark = new ReservedSystemEntity(key: 'Benchmark', value: 'Benchmark', type: 1738, pluginId: 1)
            benchmark.id = 10000152L
            create(benchmark)
            ReservedSystemEntity benchmarkStar = new ReservedSystemEntity(key: 'Benchmark Star', value: 'Benchmark Star', type: 1738, pluginId: 1)
            benchmarkStar.id = 10000153L
            create(benchmarkStar)
            ReservedSystemEntity dataImport = new ReservedSystemEntity(key: 'Data Import', value: 'Data Import', type: 1738, pluginId: 1)
            dataImport.id = 10000154L
            create(dataImport)

            ReservedSystemEntity linuxVendor = new ReservedSystemEntity(key: 'Linux', value: 'Linux', type: 1741, pluginId: 1)
            linuxVendor.id = 10000171L
            create(linuxVendor)
            ReservedSystemEntity freeBdsVendor = new ReservedSystemEntity(key: 'Free BDS', value: 'Free BDS', type: 1741, pluginId: 1)
            freeBdsVendor.id = 10000172L
            create(freeBdsVendor)

            ReservedSystemEntity table = new ReservedSystemEntity(key: 'Table', value: 'Table', type: 1743, pluginId: 1)
            table.id = 10000179L
            create(table)
            ReservedSystemEntity view = new ReservedSystemEntity(key: 'View', value: 'View', type: 1743, pluginId: 1)
            view.id = 10000180L
            create(view)
            // script type reserved system entity
            ReservedSystemEntity script = new ReservedSystemEntity(key: 'Shell', value: 'Shell', type: 1744, pluginId: 1)
            script.id = 10000181L
            create(script)
            ReservedSystemEntity sql = new ReservedSystemEntity(key: 'SQL', value: 'SQL', type: 1744, pluginId: 1)
            sql.id = 10000182L
            create(sql)

            ReservedSystemEntity diagnosticQuery = new ReservedSystemEntity(key: 'Diagnostic', value: 'Query for diagnostic report', type: 1745, pluginId: 1)
            diagnosticQuery.id = 10000184L
            create(diagnosticQuery)

            ReservedSystemEntity maintenanceQuery = new ReservedSystemEntity(key: 'Maintenance', value: 'Query for Maintenance SQL Scheduler', type: 1745, pluginId: 1)
            maintenanceQuery.id = 10000186L
            create(maintenanceQuery)

            ReservedSystemEntity sourceDb = new ReservedSystemEntity(key: 'Source DB', value: 'DB Instance Type for Source Database', type: 1746, pluginId: 1)
            sourceDb.id = 10000187L
            create(sourceDb)

            ReservedSystemEntity targetDb = new ReservedSystemEntity(key: 'Target DB', value: 'DB Instance Type for Target Database', type: 1746, pluginId: 1)
            targetDb.id = 10000188L
            create(targetDb)

            ReservedSystemEntity emailFrom = new ReservedSystemEntity(key: 'Email From', value: 'noreply@athena.com.bd', type: 1751, pluginId: 1)
            emailFrom.id = 10000306L
            create(emailFrom)

            ReservedSystemEntity smtpHost = new ReservedSystemEntity(key: 'Smtp Host', value: 'smtp.gmail.com', type: 1751, pluginId: 1)
            smtpHost.id = 10000307L
            create(smtpHost)

            ReservedSystemEntity smtpPort = new ReservedSystemEntity(key: 'Smtp Port', value: '465', type: 1751, pluginId: 1)
            smtpPort.id = 10000308L
            create(smtpPort)

            ReservedSystemEntity smtpEmail = new ReservedSystemEntity(key: 'Smtp Email', value: 'noreply.athenamis@gmail.com', type: 1751, pluginId: 1)
            smtpEmail.id = 10000309L
            create(smtpEmail)

            ReservedSystemEntity smtpPwd = new ReservedSystemEntity(key: 'Smtp Password', value: 'athena@123', type: 1751, pluginId: 1)
            smtpPwd.id = 10000310L
            create(smtpPwd)

            ReservedSystemEntity pdf = new ReservedSystemEntity(key: 'PDF', value: 'application/pdf', type: 1754, pluginId: 1)
            pdf.id = 10000323L
            create(pdf)

            ReservedSystemEntity txt = new ReservedSystemEntity(key: 'TXT', value: 'text/plain', type: 1754, pluginId: 1)
            txt.id = 10000324L
            create(txt)

            ReservedSystemEntity doc = new ReservedSystemEntity(key: 'DOC', value: 'application/msword', type: 1754, pluginId: 1)
            doc.id = 10000325L
            create(doc)

            ReservedSystemEntity docx = new ReservedSystemEntity(key: 'DOCX', value: 'application/msword', type: 1754, pluginId: 1)
            docx.id = 10000326L
            create(docx)

            ReservedSystemEntity xls = new ReservedSystemEntity(key: 'XLS', value: 'application/vnd.ms-excel', type: 1754, pluginId: 1)
            xls.id = 10000327L
            create(xls)

            ReservedSystemEntity xlsx = new ReservedSystemEntity(key: 'XLSX', value: 'application/vnd.ms-excel', type: 1754, pluginId: 1)
            xlsx.id = 10000328L
            create(xlsx)

            ReservedSystemEntity ppt = new ReservedSystemEntity(key: 'PPT', value: 'application/vnd.ms-powerpoint', type: 1754, pluginId: 1)
            ppt.id = 10000329L
            create(ppt)

            ReservedSystemEntity pptx = new ReservedSystemEntity(key: 'PPTX', value: 'application/vnd.ms-powerpoint', type: 1754, pluginId: 1)
            pptx.id = 10000330L
            create(pptx)

            ReservedSystemEntity csv = new ReservedSystemEntity(key: 'CSV', value: 'text/csv', type: 1754, pluginId: 1)
            csv.id = 10000331L
            create(csv)

            ReservedSystemEntity jpj = new ReservedSystemEntity(key: 'JPG', value: 'image/jpeg', type: 1754, pluginId: 1)
            jpj.id = 10000332L
            create(jpj)

            ReservedSystemEntity jpej = new ReservedSystemEntity(key: 'JPEG', value: 'image/jpeg', type: 1754, pluginId: 1)
            jpej.id = 10000333L
            create(jpej)

            ReservedSystemEntity png = new ReservedSystemEntity(key: 'PNG', value: 'image/png', type: 1754, pluginId: 1)
            png.id = 10000334L
            create(png)

            ReservedSystemEntity gif = new ReservedSystemEntity(key: 'GIF', value: 'image/gif', type: 1754, pluginId: 1)
            gif.id = 10000335L
            create(gif)

            ReservedSystemEntity mp3 = new ReservedSystemEntity(key: 'MP3', value: 'audio/mpeg', type: 1754, pluginId: 1)
            mp3.id = 10000336L
            create(mp3)

            ReservedSystemEntity mp4 = new ReservedSystemEntity(key: 'MP4', value: 'video/mp4', type: 1754, pluginId: 1)
            mp4.id = 10000337L
            create(mp4)
            ReservedSystemEntity bmp = new ReservedSystemEntity(key: 'BMP', value: 'image/bmp', type: 1754, pluginId: 1)
            bmp.id = 10000338L
            create(bmp)

            ReservedSystemEntity faqDocDocument = new ReservedSystemEntity(key: 'Doc Document', value: 'Doc Document', type: 1755, pluginId: 1)
            faqDocDocument.id = 10000339L
            create(faqDocDocument)

            ReservedSystemEntity faqDocSubCategore = new ReservedSystemEntity(key: 'Doc Sub Category', value: 'Doc Sub Category', type: 1755, pluginId: 1)
            faqDocSubCategore.id = 10000341L
            create(faqDocSubCategore)

            ReservedSystemEntity courseAttachment = new ReservedSystemEntity(key: 'Course', value: 'Attachment for Course', type: 701, pluginId: 1)
            courseAttachment.id = 10000344L
            create(courseAttachment)

            ReservedSystemEntity lessonAttachment = new ReservedSystemEntity(key: 'Lesson', value: 'Attachment for Lesson', type: 701, pluginId: 1)
            lessonAttachment.id = 10000345L
            create(lessonAttachment)

            ReservedSystemEntity root = new ReservedSystemEntity(key: 'Root', value: 'Root', type: 1757, pluginId: 1)
            root.id = 10000346L
            create(root)

            ReservedSystemEntity child = new ReservedSystemEntity(key: 'Selected node', value: 'Selected node', type: 1757, pluginId: 1)
            child.id = 10000347L
            create(child)

            ReservedSystemEntity blog = new ReservedSystemEntity(key: 'Blog', value: 'Blog', type: 1760, pluginId: 1)
            blog.id = 10000355L
            create(blog)

            ReservedSystemEntity post = new ReservedSystemEntity(key: 'Post', value: 'Post', type: 1760, pluginId: 1)
            post.id = 10000361L
            create(post)

            ReservedSystemEntity noteTypeBlog = new ReservedSystemEntity(key: 'Blog', value: 'Blog', type: 703, pluginId: 1)
            noteTypeBlog.id = 10000356L
            create(noteTypeBlog)

            ReservedSystemEntity noteTypePost = new ReservedSystemEntity(key: 'Post', value: 'Post', type: 703, pluginId: 1)
            noteTypePost.id = 10000362L
            create(noteTypePost)

            return true
        } catch (Exception ex) {
            log.error(ex.getMessage())
            throw new RuntimeException(ex)
        }
    }

    public void createDefaultDataForPT() {
        try {
            ReservedSystemEntity project = new ReservedSystemEntity(key: 'Pt Project', value: 'Pt Project', type: 651, pluginId: 10)
            project.id = 1059
            create(project)

            ReservedSystemEntity backlogPriorityHigh = new ReservedSystemEntity(key: 'High', value: 'High', type: 10706, pluginId: 10)
            backlogPriorityHigh.id = 1032L
            create(backlogPriorityHigh)
            ReservedSystemEntity backlogPriorityMedium = new ReservedSystemEntity(key: 'Medium', value: 'Medium', type: 10706, pluginId: 10)
            backlogPriorityMedium.id = 1033L
            create(backlogPriorityMedium)
            ReservedSystemEntity backlogPriorityLow = new ReservedSystemEntity(key: 'Low', value: 'Low', type: 10706, pluginId: 10)
            backlogPriorityLow.id = 1034L
            create(backlogPriorityLow)

            ReservedSystemEntity backlogStatusDefined = new ReservedSystemEntity(key: 'Defined', value: 'Defined', type: 10707, pluginId: 10)
            backlogStatusDefined.id = 1035L
            create(backlogStatusDefined)
            ReservedSystemEntity backlogStatusInProgress = new ReservedSystemEntity(key: 'In Progress', value: 'In Progress', type: 10707, pluginId: 10)
            backlogStatusInProgress.id = 1036L
            create(backlogStatusInProgress)
            ReservedSystemEntity backlogStatusCompleted = new ReservedSystemEntity(key: 'Completed', value: 'Completed', type: 10707, pluginId: 10)
            backlogStatusCompleted.id = 1037L
            create(backlogStatusCompleted)
            ReservedSystemEntity backlogStatusAccepted = new ReservedSystemEntity(key: 'Accepted', value: 'Accepted', type: 10707, pluginId: 10)
            backlogStatusAccepted.id = 1038L
            create(backlogStatusAccepted)

            ReservedSystemEntity sprintStatusDefined = new ReservedSystemEntity(key: 'Defined', value: 'Defined', type: 10708, pluginId: 10)
            sprintStatusDefined.id = 1039L
            create(sprintStatusDefined)
            ReservedSystemEntity sprintStatusInProgress = new ReservedSystemEntity(key: 'In Progress', value: 'In Progress', type: 10708, pluginId: 10)
            sprintStatusInProgress.id = 1040L
            create(sprintStatusInProgress)
            ReservedSystemEntity sprintStatusCompleted = new ReservedSystemEntity(key: 'Completed', value: 'Completed', type: 10708, pluginId: 10)
            sprintStatusCompleted.id = 1041L
            create(sprintStatusCompleted)
            ReservedSystemEntity sprintStatusClosed = new ReservedSystemEntity(key: 'Closed', value: 'Closed', type: 10708, pluginId: 10)
            sprintStatusClosed.id = 1095L
            create(sprintStatusClosed)

            ReservedSystemEntity acceptanceStatusDefined = new ReservedSystemEntity(key: 'Defined', value: 'Defined', type: 10711, pluginId: 10)
            acceptanceStatusDefined.id = 1042L
            create(acceptanceStatusDefined)
            ReservedSystemEntity acceptanceStatusInProgress = new ReservedSystemEntity(key: 'In Progress', value: 'In Progress', type: 10711, pluginId: 10)
            acceptanceStatusInProgress.id = 1043L
            create(acceptanceStatusInProgress)
            ReservedSystemEntity acceptanceStatusCompleted = new ReservedSystemEntity(key: 'Completed', value: 'Completed', type: 10711, pluginId: 10)
            acceptanceStatusCompleted.id = 1044L
            create(acceptanceStatusCompleted)
            ReservedSystemEntity acceptanceStatusBlocked = new ReservedSystemEntity(key: 'Blocked', value: 'Blocked', type: 10711, pluginId: 10)
            acceptanceStatusBlocked.id = 1045L
            create(acceptanceStatusBlocked)

            ReservedSystemEntity bugSeverityHigh = new ReservedSystemEntity(key: 'High', value: 'High', type: 10709, pluginId: 10)
            bugSeverityHigh.id = 1046L
            create(bugSeverityHigh)
            ReservedSystemEntity bugSeverityMedium = new ReservedSystemEntity(key: 'Medium', value: 'Medium', type: 10709, pluginId: 10)
            bugSeverityMedium.id = 1047L
            create(bugSeverityMedium)
            ReservedSystemEntity bugSeverityLow = new ReservedSystemEntity(key: 'Low', value: 'Low', type: 10709, pluginId: 10)
            bugSeverityLow.id = 1048L
            create(bugSeverityLow)

            ReservedSystemEntity bugStatusSubmitted = new ReservedSystemEntity(key: 'Submitted', value: 'Submitted', type: 10710, pluginId: 10)
            bugStatusSubmitted.id = 1049L
            create(bugStatusSubmitted)
            ReservedSystemEntity bugStatusOpen = new ReservedSystemEntity(key: 'Re-opened', value: 'Re-opened', type: 10710, pluginId: 10)
            bugStatusOpen.id = 1050L
            create(bugStatusOpen)
            ReservedSystemEntity bugStatusFixed = new ReservedSystemEntity(key: 'Fixed', value: 'Fixed', type: 10710, pluginId: 10)
            bugStatusFixed.id = 1051L
            create(bugStatusFixed)
            ReservedSystemEntity bugStatusClosed = new ReservedSystemEntity(key: 'Closed', value: 'Closed', type: 10710, pluginId: 10)
            bugStatusClosed.id = 1052L
            create(bugStatusClosed)
            ReservedSystemEntity bugStatusNotADefect = new ReservedSystemEntity(key: 'Not a Defect', value: 'Not a Defect', type: 10710, pluginId: 10)
            bugStatusNotADefect.id = 10000155L
            create(bugStatusNotADefect)
            ReservedSystemEntity bugStatusCanNotReproduce = new ReservedSystemEntity(key: 'Could Not Reproduce', value: 'Could Not Reproduce', type: 10710, pluginId: 10)
            bugStatusCanNotReproduce.id = 10000156L
            create(bugStatusCanNotReproduce)
            ReservedSystemEntity bugStatusDuplicate = new ReservedSystemEntity(key: 'Duplicate', value: 'Duplicate', type: 10710, pluginId: 10)
            bugStatusDuplicate.id = 10000157L
            create(bugStatusDuplicate)
            ReservedSystemEntity bugStatusInProgress = new ReservedSystemEntity(key: 'In Progress', value: 'In Progress', type: 10710, pluginId: 10)
            bugStatusInProgress.id = 100000178L
            create(bugStatusInProgress)

            ReservedSystemEntity bugTypeFunctional = new ReservedSystemEntity(key: 'Functional', value: 'Functional', type: 10712, pluginId: 10)
            bugTypeFunctional.id = 1053L
            create(bugTypeFunctional)
            ReservedSystemEntity bugTypeUserInterface = new ReservedSystemEntity(key: 'User Interface', value: 'User Interface', type: 10712, pluginId: 10)
            bugTypeUserInterface.id = 1054L
            create(bugTypeUserInterface)
            ReservedSystemEntity bugTypeInconsistency = new ReservedSystemEntity(key: 'Inconsistency', value: 'Inconsistency', type: 10712, pluginId: 10)
            bugTypeInconsistency.id = 1055L
            create(bugTypeInconsistency)
            ReservedSystemEntity bugTypePerformance = new ReservedSystemEntity(key: 'Performance', value: 'Performance', type: 10712, pluginId: 10)
            bugTypePerformance.id = 1056L
            create(bugTypePerformance)
            ReservedSystemEntity bugTypeSuggestion = new ReservedSystemEntity(key: 'Suggestion', value: 'Suggestion', type: 10712, pluginId: 10)
            bugTypeSuggestion.id = 1057L
            create(bugTypeSuggestion)

            ReservedSystemEntity acceptanceCriteriaTypePreCondition = new ReservedSystemEntity(key: 'Pre-condition', value: 'Pre-condition', type: 10718, pluginId: 10)
            acceptanceCriteriaTypePreCondition.id = 1078L
            create(acceptanceCriteriaTypePreCondition)
            ReservedSystemEntity acceptanceCriteriaTypeBusinessLogic = new ReservedSystemEntity(key: 'Business Logic', value: 'Business Logic', type: 10718, pluginId: 10)
            acceptanceCriteriaTypeBusinessLogic.id = 1079L
            create(acceptanceCriteriaTypeBusinessLogic)
            ReservedSystemEntity acceptanceCriteriaTypePostCondition = new ReservedSystemEntity(key: 'Post-condition', value: 'Post-condition', type: 10718, pluginId: 10)
            acceptanceCriteriaTypePostCondition.id = 1080L
            create(acceptanceCriteriaTypePostCondition)
            ReservedSystemEntity acceptanceCriteriaTypeOthers = new ReservedSystemEntity(key: 'Others', value: 'Others', type: 10718, pluginId: 10)
            acceptanceCriteriaTypeOthers.id = 1096L
            create(acceptanceCriteriaTypeOthers)

            ReservedSystemEntity bug = new ReservedSystemEntity(key: 'Bug', value: 'Bug', type: 701, pluginId: 10)
            bug.id = 1058L
            create(bug)

            ReservedSystemEntity ptTask = new ReservedSystemEntity(key: 'Task', value: 'Note Entity Type Pt Task', type: 703, pluginId: 10)
            ptTask.id = 1094L
            create(ptTask)
            ReservedSystemEntity ptBug = new ReservedSystemEntity(key: 'Bug', value: 'Note Entity Type Pt Bug', type: 703, pluginId: 10)
            ptBug.id = 100000169L
            create(ptBug)

            ReservedSystemEntity backlogChangeRequestTypeProposed = new ReservedSystemEntity(key: 'Proposed', value: 'Proposed', type: 10734, pluginId: 10)
            backlogChangeRequestTypeProposed.id = 10140L
            create(backlogChangeRequestTypeProposed)
            ReservedSystemEntity backlogChangeRequestTypeApproved = new ReservedSystemEntity(key: 'Approved', value: 'Approved', type: 10734, pluginId: 10)
            backlogChangeRequestTypeApproved.id = 10141L
            create(backlogChangeRequestTypeApproved)

            ReservedSystemEntity entityContentTypeBacklog = new ReservedSystemEntity(key: 'PT Backlog', value: 'PT Backlog', type: 701, pluginId: 10)
            entityContentTypeBacklog.id = 10000158L
            create(entityContentTypeBacklog)

            ReservedSystemEntity entityContentTypeAssignment = new ReservedSystemEntity(key: 'El Assignment', value: 'El Assignment', type: 701, pluginId: 15)
            entityContentTypeAssignment.id = 10000354L
            create(entityContentTypeAssignment)

            ReservedSystemEntity oneToMany = new ReservedSystemEntity(key: 'One-to-many', value: 'One-to-many', type: 10752, pluginId: 10)
            oneToMany.id = 100000311L
            create(oneToMany)

            ReservedSystemEntity manyToOne = new ReservedSystemEntity(key: 'Many-to-one', value: 'Many-to-one', type: 10752, pluginId: 10)
            manyToOne.id = 100000340L
            create(manyToOne)

            ReservedSystemEntity manyToMany = new ReservedSystemEntity(key: 'Many-to-many', value: 'Many-to-many', type: 10752, pluginId: 10)
            manyToMany.id = 100000312L
            create(manyToMany)

            ReservedSystemEntity integerType = new ReservedSystemEntity(key: 'Integer', value: 'Integer', type: 10753L, pluginId: 10)
            integerType.id = 100000313L
            create(integerType)

            ReservedSystemEntity longType = new ReservedSystemEntity(key: 'Long', value: 'Long', type: 10753L, pluginId: 10)
            longType.id = 100000314L
            create(longType)

            ReservedSystemEntity floatType = new ReservedSystemEntity(key: 'Float', value: 'Float', type: 10753L, pluginId: 10)
            floatType.id = 100000315L
            create(floatType)

            ReservedSystemEntity doubleType = new ReservedSystemEntity(key: 'Double', value: 'Double', type: 10753L, pluginId: 10)
            doubleType.id = 100000316L
            create(doubleType)

            ReservedSystemEntity characterType = new ReservedSystemEntity(key: 'Character', value: 'Character', type: 10753L, pluginId: 10)
            characterType.id = 100000317L
            create(characterType)

            ReservedSystemEntity stringType = new ReservedSystemEntity(key: 'String', value: 'String', type: 10753L, pluginId: 10)
            stringType.id = 100000318L
            create(stringType)

            ReservedSystemEntity booleanType = new ReservedSystemEntity(key: 'Boolean', value: 'Boolean', type: 10753L, pluginId: 10)
            booleanType.id = 100000319L
            create(booleanType)

            ReservedSystemEntity dateType = new ReservedSystemEntity(key: 'Date', value: 'Date', type: 10753L, pluginId: 10)
            dateType.id = 100000320L
            create(dateType)

            ReservedSystemEntity byteType = new ReservedSystemEntity(key: 'Byte', value: 'Byte', type: 10753L, pluginId: 10)
            byteType.id = 100000321L
            create(byteType)

        } catch (Exception ex) {
            log.error(ex.getMessage())
            throw new RuntimeException(ex)
        }

    }

    public boolean createDefaultDataForArms() {
        try {
            int count = appVersionService.countByPluginId(ArmsPluginConnector.PLUGIN_ID)
            if (count > 0) return true

            ReservedSystemEntity processIssue = new ReservedSystemEntity(key: 'Issue', value: 'Issue', type: 11713, pluginId: 11)
            processIssue.id = 1150L
            create(processIssue)
            ReservedSystemEntity processForward = new ReservedSystemEntity(key: 'Forward', value: 'Forward', type: 11713, pluginId: 11)
            processForward.id = 1151L
            create(processForward)
            ReservedSystemEntity processPurchase = new ReservedSystemEntity(key: 'Purchase', value: 'Purchase', type: 11713, pluginId: 11)
            processPurchase.id = 1152L
            create(processPurchase)

            ReservedSystemEntity instrumentPo = new ReservedSystemEntity(key: 'PO', value: 'PO', type: 11714, pluginId: 11)
            instrumentPo.id = 1153L
            create(instrumentPo)
            ReservedSystemEntity instrumentEft = new ReservedSystemEntity(key: 'EFT', value: 'EFT', type: 11714, pluginId: 11)
            instrumentEft.id = 1154L
            create(instrumentEft)
            ReservedSystemEntity instrumentOnline = new ReservedSystemEntity(key: 'Online', value: 'Online', type: 11714, pluginId: 11)
            instrumentOnline.id = 1155L
            create(instrumentOnline)
            ReservedSystemEntity instrumentCashCollection = new ReservedSystemEntity(key: 'Cash collection', value: 'Cash collection', type: 11714, pluginId: 11)
            instrumentCashCollection.id = 1156L
            create(instrumentCashCollection)
            ReservedSystemEntity instrumentTt = new ReservedSystemEntity(key: 'TT', value: 'TT', type: 11714, pluginId: 11)
            instrumentTt.id = 1157L
            create(instrumentTt)
            ReservedSystemEntity instrumentMt = new ReservedSystemEntity(key: 'MT', value: 'MT', type: 11714, pluginId: 11)
            instrumentMt.id = 1158L
            create(instrumentMt)

            ReservedSystemEntity payMethodBankDeposit = new ReservedSystemEntity(key: 'Bank deposit', value: 'Bank deposit', type: 11715, pluginId: 11)
            payMethodBankDeposit.id = 1160L
            create(payMethodBankDeposit)
            ReservedSystemEntity payMethodCashCollection = new ReservedSystemEntity(key: 'Cash collection', value: 'Cash collection', type: 11715, pluginId: 11)
            payMethodCashCollection.id = 1161L
            create(payMethodCashCollection)

            ReservedSystemEntity taskStatusPendingTask = new ReservedSystemEntity(key: 'Pending task', value: 'Pending  task', type: 11716, pluginId: 11)
            taskStatusPendingTask.id = 1162L
            create(taskStatusPendingTask)
            ReservedSystemEntity taskStatusNewTask = new ReservedSystemEntity(key: 'New task', value: 'New  task', type: 11716, pluginId: 11)
            taskStatusNewTask.id = 1163L
            create(taskStatusNewTask)
            ReservedSystemEntity taskStatusIncludeInLst = new ReservedSystemEntity(key: 'Included in list', value: 'Included in list', type: 11716, pluginId: 11)
            taskStatusIncludeInLst.id = 1164L
            create(taskStatusIncludeInLst)
            ReservedSystemEntity taskStatusDecisionTaken = new ReservedSystemEntity(key: 'Decision taken', value: 'Decision taken', type: 11716, pluginId: 11)
            taskStatusDecisionTaken.id = 1165L
            create(taskStatusDecisionTaken)
            ReservedSystemEntity taskStatusDecisionApproved = new ReservedSystemEntity(key: 'Decision approved', value: 'Decision approved', type: 11716, pluginId: 11)
            taskStatusDecisionApproved.id = 1166L
            create(taskStatusDecisionApproved)
            ReservedSystemEntity taskStatusDisbursed = new ReservedSystemEntity(key: 'Disbursed', value: 'Disbursed', type: 11716, pluginId: 11)
            taskStatusDisbursed.id = 1167L
            create(taskStatusDisbursed)
            ReservedSystemEntity taskStatusCanceled = new ReservedSystemEntity(key: 'Canceled', value: 'Canceled', type: 11716, pluginId: 11)
            taskStatusCanceled.id = 1168L
            create(taskStatusCanceled)
            ReservedSystemEntity rmsTaskNote = new ReservedSystemEntity(key: 'RmsTask', value: 'Note Entity Type RmsTask', type: 703, pluginId: 11)
            rmsTaskNote.id = 1181L
            create(rmsTaskNote)
            ReservedSystemEntity rmsExhUser = new ReservedSystemEntity(key: 'Exchange House', value: 'Exchange House', type: 651, pluginId: 11)
            rmsExhUser.id = 1186L
            create(rmsExhUser)

            return true
        } catch (Exception ex) {
            log.error(ex.getMessage())
            throw new RuntimeException(ex)
        }
    }

    public boolean createDefaultDataForExh() {
        try {
            int count = appVersionService.countByPluginId(ExchangeHousePluginConnector.PLUGIN_ID)
            if (count > 0) return true

            ReservedSystemEntity cash = new ReservedSystemEntity(key: 'Cash', value: 'Cash', type: 2001, pluginId: 9)
            cash.id = 950L
            create(cash)
            ReservedSystemEntity onlineTransfer = new ReservedSystemEntity(key: 'Online Transfer', value: 'Online Transfer', type: 2001, pluginId: 9)
            onlineTransfer.id = 951L
            create(onlineTransfer)
            ReservedSystemEntity cardPaymentDebit = new ReservedSystemEntity(key: 'Card Payment Debit', value: 'Card Payment Debit', type: 2001, pluginId: 9)
            cardPaymentDebit.id = 952L
            create(cardPaymentDebit)

            ReservedSystemEntity cardPaymentCredit = new ReservedSystemEntity(key: 'Card Payment Credit', value: 'Card Payment Credit', type: 2001, pluginId: 9)
            cardPaymentCredit.id = 9121L
            create(cardPaymentCredit)

            ReservedSystemEntity bankDeposit = new ReservedSystemEntity(key: 'Bank Deposit', value: '1', type: 2002, pluginId: 9)
            bankDeposit.id = 953L
            create(bankDeposit)
            ReservedSystemEntity cashCollection = new ReservedSystemEntity(key: 'Cash Collection', value: '2', type: 2002, pluginId: 9)
            cashCollection.id = 954L
            create(cashCollection)

            ReservedSystemEntity cancelled = new ReservedSystemEntity(key: 'Cancelled Task', value: 'Cancelled Task', type: 2003, pluginId: 9)
            cancelled.id = 956L
            create(cancelled)
            ReservedSystemEntity pendingTask = new ReservedSystemEntity(key: 'Pending Task', value: 'Pending Task', type: 2003, pluginId: 9)
            pendingTask.id = 957L
            create(pendingTask)
            ReservedSystemEntity newTask = new ReservedSystemEntity(key: 'New Task', value: 'New Task', type: 2003, pluginId: 9)
            newTask.id = 958L
            create(newTask)
            ReservedSystemEntity sentToBank = new ReservedSystemEntity(key: 'Sent To Bank', value: 'Sent To Bank', type: 2003, pluginId: 9)
            sentToBank.id = 959L
            create(sentToBank)
            ReservedSystemEntity sentToOtherBank = new ReservedSystemEntity(key: 'Sent To Other Bank', value: 'Sent To Other Bank', type: 2003, pluginId: 9)
            sentToOtherBank.id = 960L
            create(sentToOtherBank)
            ReservedSystemEntity paid = new ReservedSystemEntity(key: 'Paid', value: 'Paid', type: 2003, pluginId: 9)
            paid.id = 961L
            create(paid)
            ReservedSystemEntity unapprovedTask = new ReservedSystemEntity(key: 'Unapproved Task', value: 'Unapproved Task', type: 2003, pluginId: 9)
            unapprovedTask.id = 962L
            create(unapprovedTask)

            ReservedSystemEntity refundTask = new ReservedSystemEntity(key: 'Refund Task', value: 'Refund Task', type: 2003, pluginId: 9)
            refundTask.id = 997L
            create(refundTask)

            ReservedSystemEntity exhTask = new ReservedSystemEntity(key: 'Exh Task', value: 'Exh Task', type: 2004, pluginId: 9)
            exhTask.id = 963L
            create(exhTask)
            ReservedSystemEntity agentTask = new ReservedSystemEntity(key: 'Agent Task', value: 'Agent Task', type: 2004, pluginId: 9)
            agentTask.id = 964L
            create(agentTask)
            ReservedSystemEntity customerTask = new ReservedSystemEntity(key: 'Customer Task', value: 'Customer Task', type: 2004, pluginId: 9)
            customerTask.id = 965L
            create(customerTask)

            return true
        } catch (Exception ex) {
            log.error(ex.getMessage())
            throw new RuntimeException(ex)
        }
    }

    public void createDefaultDataForDocument() {
        try {

            ReservedSystemEntity checkBox = new ReservedSystemEntity(key: 'Check Box', value: 'Check Box', type: 13725, pluginId: 13)
            checkBox.id = 13106L
            create(checkBox)
            ReservedSystemEntity radioButton = new ReservedSystemEntity(key: 'Radio Button', value: 'Radio Button', type: 13725, pluginId: 13)
            radioButton.id = 13107L
            create(radioButton)
            ReservedSystemEntity textArea = new ReservedSystemEntity(key: 'Text Area', value: 'Text Area', type: 13725, pluginId: 13)
            textArea.id = 13108L
            create(textArea)
            ReservedSystemEntity textBox = new ReservedSystemEntity(key: 'Text Box', value: 'Text Box', type: 13725, pluginId: 13)
            textBox.id = 130000364L
            create(textBox)

            ReservedSystemEntity dropDown = new ReservedSystemEntity(key: 'Drop Down', value: 'Drop Down', type: 13725, pluginId: 13)
            dropDown.id = 130000365L
            create(dropDown)

            ReservedSystemEntity hard = new ReservedSystemEntity(key: 'Hard', value: 'Hard', type: 13726, pluginId: 13)
            hard.id = 13109L
            create(hard)
            ReservedSystemEntity moderate = new ReservedSystemEntity(key: 'Moderate', value: 'Moderate', type: 13726, pluginId: 13)
            moderate.id = 13110L
            create(moderate)
            ReservedSystemEntity easy = new ReservedSystemEntity(key: 'Easy', value: 'Easy', type: 13726, pluginId: 13)
            easy.id = 13111L
            create(easy)

            ReservedSystemEntity newDocument = new ReservedSystemEntity(key: 'New Document', value: 'New Document', type: 13731, pluginId: 13)
            newDocument.id = 13126L
            create(newDocument)
            ReservedSystemEntity requestIndex = new ReservedSystemEntity(key: 'Request for Index', value: 'Request for Index', type: 13731, pluginId: 13)
            requestIndex.id = 13127L
            create(requestIndex)
            ReservedSystemEntity indexed = new ReservedSystemEntity(key: 'Indexed', value: 'Indexed', type: 13731, pluginId: 13)
            indexed.id = 13128L
            create(indexed)
            ReservedSystemEntity indexedFailed = new ReservedSystemEntity(key: 'Indexed Failed', value: 'Indexed Failed', type: 13731, pluginId: 13)
            indexedFailed.id = 13129L
            create(indexedFailed)
            ReservedSystemEntity submitProcess = new ReservedSystemEntity(key: 'Submitted to Process', value: 'Submitted to Process', type: 13731, pluginId: 13)
            submitProcess.id = 13130L
            create(submitProcess)
            ReservedSystemEntity queueProcess = new ReservedSystemEntity(key: 'Queued to Process', value: 'Queued to Process', type: 13731, pluginId: 13)
            queueProcess.id = 13131L
            create(queueProcess)
            ReservedSystemEntity conversionProcess = new ReservedSystemEntity(key: 'Conversion in process', value: 'Conversion in process', type: 13731, pluginId: 13)
            conversionProcess.id = 13132L
            create(conversionProcess)
            ReservedSystemEntity noCredit = new ReservedSystemEntity(key: 'Not enough Credit', value: 'Not enough Credit', type: 13731, pluginId: 13)
            noCredit.id = 13133L
            create(noCredit)

            ReservedSystemEntity none = new ReservedSystemEntity(key: 'None', value: 'None', type: 13732, pluginId: 13)
            none.id = 13134L
            create(none)
            ReservedSystemEntity application = new ReservedSystemEntity(key: 'Application', value: 'Application', type: 13732, pluginId: 13)
            application.id = 13135L
            create(application)
            ReservedSystemEntity ocr = new ReservedSystemEntity(key: 'OCR', value: 'OCR', type: 13732, pluginId: 13)
            ocr.id = 13136L
            create(ocr)

            ReservedSystemEntity docComment = new ReservedSystemEntity(key: 'Document Comment', value: 'Document Comment', type: 703, pluginId: 13)
            docComment.id = 130000162L

            ReservedSystemEntity article = new ReservedSystemEntity(key: 'Article', value: 'Article', type: 13740, pluginId: 13)
            article.id = 130000163L
            create(article)
            ReservedSystemEntity file = new ReservedSystemEntity(key: 'File', value: 'File', type: 13740, pluginId: 13)
            file.id = 130000164L
            create(file)
            ReservedSystemEntity image = new ReservedSystemEntity(key: 'Image', value: 'Image', type: 13740, pluginId: 13)
            image.id = 130000165L
            create(image)
            ReservedSystemEntity audio = new ReservedSystemEntity(key: 'Audio', value: 'Audio', type: 13740, pluginId: 13)
            audio.id = 130000166L
            create(audio)
            ReservedSystemEntity video = new ReservedSystemEntity(key: 'Video', value: 'Video', type: 13740, pluginId: 13)
            video.id = 130000167L
            create(video)
        } catch (Exception ex) {
            log.error(ex.getMessage())
            throw new RuntimeException(ex)
        }
    }


    public void createDefaultDataForElarning() {
        try {
            ReservedSystemEntity bangla = new ReservedSystemEntity(key: 'Bengali', value: 'Bengali', type: 15756, pluginId: 15)
            bangla.id = 150000342L
            create(bangla)

            ReservedSystemEntity english = new ReservedSystemEntity(key: 'English', value: 'English', type: 15756, pluginId: 15)
            english.id = 150000343L
            create(english)

            ReservedSystemEntity courseStatusRunning = new ReservedSystemEntity(key: 'Running', value: 'Running', type: 15758, pluginId: 15)
            courseStatusRunning.id = 150000348L
            create(courseStatusRunning)

            ReservedSystemEntity courseStatusUpcoming = new ReservedSystemEntity(key: 'Upcoming', value: 'Upcoming', type: 15758, pluginId: 15)
            courseStatusUpcoming.id = 150000349L
            create(courseStatusUpcoming)

            ReservedSystemEntity courseStatusPast = new ReservedSystemEntity(key: 'Past', value: 'Past', type: 15758, pluginId: 15)
            courseStatusPast.id = 150000350L
            create(courseStatusPast)

            ReservedSystemEntity courseStatusWatchlist = new ReservedSystemEntity(key: 'Watchlist', value: 'Watchlist', type: 15758, pluginId: 15)
            courseStatusWatchlist.id = 150000351L
            create(courseStatusWatchlist)

            ReservedSystemEntity individual = new ReservedSystemEntity(key: 'Individual', value: 'Individual', type: 15759, pluginId: 15)
            individual.id = 150000352L
            create(individual)

            ReservedSystemEntity group = new ReservedSystemEntity(key: 'Group', value: 'Group', type: 15759, pluginId: 15)
            group.id = 150000353L
            create(group)

            ReservedSystemEntity resultEntityExam = new ReservedSystemEntity(key: 'Exam', value: 'Exam', type: 15761, pluginId: 15)
            resultEntityExam.id = 150000357L
            create(resultEntityExam)

            ReservedSystemEntity resultEntityQuiz = new ReservedSystemEntity(key: 'Quiz', value: 'Quiz', type: 15761, pluginId: 15)
            resultEntityQuiz.id = 150000358L
            create(resultEntityQuiz)

            ReservedSystemEntity resultEntityAssignment = new ReservedSystemEntity(key: 'Assignment', value: 'Assignment', type: 15761, pluginId: 15)
            resultEntityAssignment.id = 150000359L
            create(resultEntityAssignment)

            ReservedSystemEntity resultEntityActivity = new ReservedSystemEntity(key: 'Activity', value: 'Activity', type: 15761, pluginId: 15)
            resultEntityActivity.id = 150000360L
            create(resultEntityActivity)

            ReservedSystemEntity userPointRegistration = new ReservedSystemEntity(key: 'Registration', value: '1000', type: 15762, pluginId: 15)
            userPointRegistration.id = 150000366L
            create(userPointRegistration)

            ReservedSystemEntity userPointProfileUpdate = new ReservedSystemEntity(key: 'Profile Update', value: '100', type: 15762, pluginId: 15)
            userPointProfileUpdate.id = 150000367L
            create(userPointProfileUpdate)

            ReservedSystemEntity userPointCourseEnroll = new ReservedSystemEntity(key: 'Course Enroll', value: '1000', type: 15762, pluginId: 15)
            userPointCourseEnroll.id = 150000368L
            create(userPointCourseEnroll)

            ReservedSystemEntity userPointContentRead = new ReservedSystemEntity(key: 'Content Read', value: '100', type: 15762, pluginId: 15)
            userPointContentRead.id = 150000369L
            create(userPointContentRead)

            ReservedSystemEntity userPointAssignmentSubmit = new ReservedSystemEntity(key: 'Assignment Submit', value: '200', type: 15762, pluginId: 15)
            userPointAssignmentSubmit.id = 150000370L
            create(userPointAssignmentSubmit)

            ReservedSystemEntity userPointPopQuizParticipate = new ReservedSystemEntity(key: 'Pop Quiz Participate', value: '100', type: 15762, pluginId: 15)
            userPointPopQuizParticipate.id = 150000371L
            create(userPointPopQuizParticipate)

            ReservedSystemEntity userPointExamParticipate = new ReservedSystemEntity(key: 'Exam Participate', value: '200', type: 15762, pluginId: 15)
            userPointExamParticipate.id = 150000372L
            create(userPointExamParticipate)

            ReservedSystemEntity userPointDailyLogin = new ReservedSystemEntity(key: 'Daily Login', value: '100', type: 15762, pluginId: 15)
            userPointDailyLogin.id = 150000373L
            create(userPointDailyLogin)

            ReservedSystemEntity userPointDiscussionWriting = new ReservedSystemEntity(key: 'Discussion Writing', value: '200', type: 15762, pluginId: 15)
            userPointDiscussionWriting.id = 150000374L
            create(userPointDiscussionWriting)

            ReservedSystemEntity userPointBlogComment = new ReservedSystemEntity(key: 'Blog Comment', value: '300', type: 15762, pluginId: 15)
            userPointBlogComment.id = 150000375L
            create(userPointBlogComment)

            ReservedSystemEntity userPointCourseComplete = new ReservedSystemEntity(key: 'Course Completion', value: '1000', type: 15762, pluginId: 15)
            userPointCourseComplete.id = 150000376L
            create(userPointCourseComplete)

        } catch (Exception ex) {
            log.error(ex.getMessage())
            throw new RuntimeException(ex)
        }
    }

    public boolean createDefaultDataForSarb() {
        try {
            int count = appVersionService.countByPluginId(SarbPluginConnector.PLUGIN_ID)
            if (count > 0) return true

            ReservedSystemEntity movedForCancel = new ReservedSystemEntity(key: 'Moved for cancel', value: 'Moved for cancel', type: 12723, pluginId: 12)
            movedForCancel.id = 1298L
            create(movedForCancel)
            ReservedSystemEntity movedForReplace = new ReservedSystemEntity(key: 'Moved for replace', value: 'Moved for replace', type: 12723, pluginId: 12)
            movedForReplace.id = 1299L
            create(movedForReplace)

            ReservedSystemEntity regular = new ReservedSystemEntity(key: 'Regular', value: 'Regular', type: 12727, pluginId: 12)
            regular.id = 12112L
            create(regular)
            ReservedSystemEntity cancel = new ReservedSystemEntity(key: 'Cancel', value: 'Cancel', type: 12727, pluginId: 12)
            cancel.id = 12113L
            create(cancel)
            ReservedSystemEntity replace = new ReservedSystemEntity(key: 'Replace', value: 'Replace', type: 12727, pluginId: 12)
            replace.id = 12114L
            create(replace)
            ReservedSystemEntity refund = new ReservedSystemEntity(key: 'Refund', value: 'Refund', type: 12727, pluginId: 12)
            refund.id = 12115L
            create(refund)

            ReservedSystemEntity buy = new ReservedSystemEntity(key: 'Buy', value: 'Buy', type: 12728, pluginId: 12)
            buy.id = 12116L
            create(buy)
            ReservedSystemEntity sell = new ReservedSystemEntity(key: 'Sell', value: 'Sell', type: 12728, pluginId: 12)
            sell.id = 12117L
            create(sell)

            return true
        } catch (Exception ex) {
            log.error(ex.getMessage())
            throw new RuntimeException(ex)
        }
    }

    public void createDefaultDataForDataPipeLine() {
        try {

            ReservedSystemEntity received = new ReservedSystemEntity(key: 'Received', value: 'Received', type: 14739, pluginId: 14)
            received.id = 140000159L
            create(received)
            ReservedSystemEntity preProcess = new ReservedSystemEntity(key: 'Pre-Process', value: 'Pre-Process', type: 14739, pluginId: 14)
            preProcess.id = 140000160L
            create(preProcess)
            ReservedSystemEntity loaded = new ReservedSystemEntity(key: 'Loaded', value: 'Loaded', type: 14739, pluginId: 14)
            loaded.id = 140000161L
            create(loaded)

            ReservedSystemEntity insert = new ReservedSystemEntity(key: 'INSERT', value: 'INSERT', type: 14742, pluginId: 14)
            insert.id = 140000173L
            create(insert)
            ReservedSystemEntity update = new ReservedSystemEntity(key: 'UPDATE', value: 'UPDATE', type: 14742, pluginId: 14)
            update.id = 140000174L
            create(update)
            ReservedSystemEntity delete = new ReservedSystemEntity(key: 'DELETE', value: 'DELETE', type: 14742, pluginId: 14)
            delete.id = 140000175L
            create(delete)
            ReservedSystemEntity replace = new ReservedSystemEntity(key: 'REPLACE', value: 'REPLACE', type: 14742, pluginId: 14)
            replace.id = 140000176L
            create(replace)
            ReservedSystemEntity alter = new ReservedSystemEntity(key: 'ALTER', value: 'ALTER', type: 14742, pluginId: 14)
            alter.id = 140000177L
            create(alter)

            ReservedSystemEntity dataTypeInt = new ReservedSystemEntity(key: 'int', value: 'integer', type: 14747, pluginId: 14)
            dataTypeInt.id = 140000190L
            create(dataTypeInt)

            ReservedSystemEntity dataTypeTinyInt = new ReservedSystemEntity(key: 'tinyint', value: 'smallint', type: 14747, pluginId: 14)
            dataTypeTinyInt.id = 140000191L
            create(dataTypeTinyInt)

            ReservedSystemEntity dataTypeSmallint = new ReservedSystemEntity(key: 'smallint', value: 'smallint', type: 14747, pluginId: 14)
            dataTypeSmallint.id = 140000192L
            create(dataTypeSmallint)

            ReservedSystemEntity dataTypeBigInt = new ReservedSystemEntity(key: 'bigint', value: 'bigint', type: 14747, pluginId: 14)
            dataTypeBigInt.id = 140000193L
            create(dataTypeBigInt)

            ReservedSystemEntity dataTypeMoney = new ReservedSystemEntity(key: 'money', value: 'money', type: 14747, pluginId: 14)
            dataTypeMoney.id = 140000194L
            create(dataTypeMoney)

            ReservedSystemEntity dataTypeSmallMoney = new ReservedSystemEntity(key: 'smallmoney', value: 'money', type: 14747, pluginId: 14)
            dataTypeSmallMoney.id = 140000195L
            create(dataTypeSmallMoney)

            ReservedSystemEntity dataTypeDecimal = new ReservedSystemEntity(key: 'decimal', value: 'numeric', type: 14747, pluginId: 14)
            dataTypeDecimal.id = 140000196L
            create(dataTypeDecimal)

            ReservedSystemEntity dataTypeNumeric = new ReservedSystemEntity(key: 'numeric', value: 'numeric', type: 14747, pluginId: 14)
            dataTypeNumeric.id = 140000197L
            create(dataTypeNumeric)

            ReservedSystemEntity dataTypeFloat = new ReservedSystemEntity(key: 'float', value: 'real', type: 14747, pluginId: 14)
            dataTypeFloat.id = 140000198L
            create(dataTypeFloat)

            ReservedSystemEntity dataTypeReal = new ReservedSystemEntity(key: 'real', value: 'real', type: 14747, pluginId: 14)
            dataTypeReal.id = 140000199L
            create(dataTypeReal)

            ReservedSystemEntity dataTypeDate = new ReservedSystemEntity(key: 'date', value: 'date', type: 14747, pluginId: 14)
            dataTypeDate.id = 140000200L
            create(dataTypeDate)

            ReservedSystemEntity dataTypeDateTime = new ReservedSystemEntity(key: 'datetime', value: 'timestamp without time zone', type: 14747, pluginId: 14)
            dataTypeDateTime.id = 140000201L
            create(dataTypeDateTime)

            ReservedSystemEntity dataTypeDateTime2 = new ReservedSystemEntity(key: 'datetime2', value: 'timestamp without time zone', type: 14747, pluginId: 14)
            dataTypeDateTime2.id = 140000202L
            create(dataTypeDateTime2)

            ReservedSystemEntity dataTypeDateTimeOffset = new ReservedSystemEntity(key: 'datetimeoffset', value: 'timestamp with time zone', type: 14747, pluginId: 14)
            dataTypeDateTimeOffset.id = 140000203L
            create(dataTypeDateTimeOffset)

            ReservedSystemEntity dataTypeSmallDateTime = new ReservedSystemEntity(key: 'smalldatetime', value: 'timestamp without time zone', type: 14747, pluginId: 14)
            dataTypeSmallDateTime.id = 140000204L
            create(dataTypeSmallDateTime)

            ReservedSystemEntity dataTypeTime = new ReservedSystemEntity(key: 'time', value: 'time without time zone', type: 14747, pluginId: 14)
            dataTypeTime.id = 140000205L
            create(dataTypeTime)

            ReservedSystemEntity dataTypeChar = new ReservedSystemEntity(key: 'char', value: 'character varying', type: 14747, pluginId: 14)
            dataTypeChar.id = 140000206L
            create(dataTypeChar)

            ReservedSystemEntity dataTypeNchar = new ReservedSystemEntity(key: 'nchar', value: 'character varying', type: 14747, pluginId: 14)
            dataTypeNchar.id = 140000207L
            create(dataTypeNchar)

            ReservedSystemEntity dataTypeVarChar = new ReservedSystemEntity(key: 'varchar', value: 'character varying', type: 14747, pluginId: 14)
            dataTypeVarChar.id = 140000208L
            create(dataTypeVarChar)

            ReservedSystemEntity dataTypeNvarChar = new ReservedSystemEntity(key: 'nvarchar', value: 'character varying', type: 14747, pluginId: 14)
            dataTypeNvarChar.id = 140000209L
            create(dataTypeNvarChar)

            ReservedSystemEntity dataTypeBit = new ReservedSystemEntity(key: 'bit', value: 'bit varying', type: 14747, pluginId: 14)
            dataTypeBit.id = 140000210L
            create(dataTypeBit)

            ReservedSystemEntity dataTypeBinary = new ReservedSystemEntity(key: 'binary', value: 'bytea', type: 14747, pluginId: 14)
            dataTypeBinary.id = 140000211L
            create(dataTypeBinary)

            ReservedSystemEntity dataTypeVarBinary = new ReservedSystemEntity(key: 'varbinary', value: 'bytea', type: 14747, pluginId: 14)
            dataTypeVarBinary.id = 140000212L
            create(dataTypeVarBinary)

            ReservedSystemEntity dataTypeXml = new ReservedSystemEntity(key: 'xml', value: 'xml', type: 14747, pluginId: 14)
            dataTypeXml.id = 140000213L
            create(dataTypeXml)

            ReservedSystemEntity dataTypeRowVersion = new ReservedSystemEntity(key: 'rowversion', value: 'bigint', type: 14747, pluginId: 14)
            dataTypeRowVersion.id = 140000214L
            create(dataTypeRowVersion)

            ReservedSystemEntity dataTypeUniqueIdentifier = new ReservedSystemEntity(key: 'uniqueidentifier', value: 'smallint', type: 14747, pluginId: 14)
            dataTypeUniqueIdentifier.id = 140000215L
            create(dataTypeUniqueIdentifier)

            ReservedSystemEntity dataTypeText = new ReservedSystemEntity(key: 'text', value: 'text', type: 14747, pluginId: 14)
            dataTypeText.id = 140000216L
            create(dataTypeText)

            ReservedSystemEntity dataTypeNtext = new ReservedSystemEntity(key: 'ntext', value: 'text', type: 14747, pluginId: 14)
            dataTypeNtext.id = 140000217L
            create(dataTypeNtext)

            ReservedSystemEntity dataTypeImage = new ReservedSystemEntity(key: 'image', value: 'bytea', type: 14747, pluginId: 14)
            dataTypeImage.id = 140000218L
            create(dataTypeImage)

            ReservedSystemEntity dataTypeIntForMsSqlToRedShift = new ReservedSystemEntity(key: 'int', value: 'INTEGER', type: 14748, pluginId: 14)
            dataTypeIntForMsSqlToRedShift.id = 140000219L
            create(dataTypeIntForMsSqlToRedShift)

            ReservedSystemEntity dataTypeTinyIntForMsSqlToRedShift = new ReservedSystemEntity(key: 'tinyint', value: 'SMALLINT', type: 14748, pluginId: 14)
            dataTypeTinyIntForMsSqlToRedShift.id = 140000220L
            create(dataTypeTinyIntForMsSqlToRedShift)

            ReservedSystemEntity dataTypeSmallintForMsSqlToRedShift = new ReservedSystemEntity(key: 'smallint', value: 'SMALLINT', type: 14748, pluginId: 14)
            dataTypeSmallintForMsSqlToRedShift.id = 140000221L
            create(dataTypeSmallintForMsSqlToRedShift)

            ReservedSystemEntity dataTypeBigIntForMsSqlToRedShift = new ReservedSystemEntity(key: 'bigint', value: 'BIGINT', type: 14748, pluginId: 14)
            dataTypeBigIntForMsSqlToRedShift.id = 140000222L
            create(dataTypeBigIntForMsSqlToRedShift)

            ReservedSystemEntity dataTypeMoneyForMsSqlToRedShift = new ReservedSystemEntity(key: 'money', value: 'BIGINT', type: 14748, pluginId: 14)
            dataTypeMoneyForMsSqlToRedShift.id = 140000223L
            create(dataTypeMoneyForMsSqlToRedShift)

            ReservedSystemEntity dataTypeSmallMoneyForMsSqlToRedShift = new ReservedSystemEntity(key: 'smallmoney', value: 'INTEGER', type: 14748, pluginId: 14)
            dataTypeSmallMoneyForMsSqlToRedShift.id = 140000224L
            create(dataTypeSmallMoneyForMsSqlToRedShift)

            ReservedSystemEntity dataTypeDecimalForMsSqlToRedShift = new ReservedSystemEntity(key: 'decimal', value: 'DECIMAL', type: 14748, pluginId: 14)
            dataTypeDecimalForMsSqlToRedShift.id = 140000225L
            create(dataTypeDecimalForMsSqlToRedShift)

            ReservedSystemEntity dataTypeNumericForMsSqlToRedShift = new ReservedSystemEntity(key: 'numeric', value: 'DECIMAL', type: 14748, pluginId: 14)
            dataTypeNumericForMsSqlToRedShift.id = 140000226L
            create(dataTypeNumericForMsSqlToRedShift)

            ReservedSystemEntity dataTypeFloatForMsSqlToRedShift = new ReservedSystemEntity(key: 'float', value: 'REAL', type: 14748, pluginId: 14)
            dataTypeFloatForMsSqlToRedShift.id = 140000227L
            create(dataTypeFloatForMsSqlToRedShift)

            ReservedSystemEntity dataTypeRealForMsSqlToRedShift = new ReservedSystemEntity(key: 'real', value: 'REAL', type: 14748, pluginId: 14)
            dataTypeRealForMsSqlToRedShift.id = 140000228L
            create(dataTypeRealForMsSqlToRedShift)

            ReservedSystemEntity dataTypeDateForMsSqlToRedShift = new ReservedSystemEntity(key: 'date', value: 'DATE', type: 14748, pluginId: 14)
            dataTypeDateForMsSqlToRedShift.id = 140000229L
            create(dataTypeDateForMsSqlToRedShift)

            ReservedSystemEntity dataTypeDateTimeForMsSqlToRedShift = new ReservedSystemEntity(key: 'datetime', value: 'TIMESTAMP WITHOUT TIME ZONE', type: 14748, pluginId: 14)
            dataTypeDateTimeForMsSqlToRedShift.id = 140000230L
            create(dataTypeDateTimeForMsSqlToRedShift)

            ReservedSystemEntity dataTypeDateTime2ForMsSqlToRedShift = new ReservedSystemEntity(key: 'datetime2', value: 'TIMESTAMP WITHOUT TIME ZONE', type: 14748, pluginId: 14)
            dataTypeDateTime2ForMsSqlToRedShift.id = 140000231L
            create(dataTypeDateTime2ForMsSqlToRedShift)

            ReservedSystemEntity dataTypeDateTimeOffsetForMsSqlToRedShift = new ReservedSystemEntity(key: 'datetimeoffset', value: 'TIMESTAMP WITHOUT TIME ZONE', type: 14748, pluginId: 14)
            dataTypeDateTimeOffsetForMsSqlToRedShift.id = 140000232L
            create(dataTypeDateTimeOffsetForMsSqlToRedShift)

            ReservedSystemEntity dataTypeSmallDateTimeForMsSqlToRedShift = new ReservedSystemEntity(key: 'smalldatetime', value: 'TIMESTAMP WITHOUT TIME ZONE', type: 14748, pluginId: 14)
            dataTypeSmallDateTimeForMsSqlToRedShift.id = 140000233L
            create(dataTypeSmallDateTimeForMsSqlToRedShift)

            ReservedSystemEntity dataTypeTimeForMsSqlToRedShift = new ReservedSystemEntity(key: 'time', value: '[Unsupported Data Type:time]', type: 14748, pluginId: 14)
            dataTypeTimeForMsSqlToRedShift.id = 140000287L
            create(dataTypeTimeForMsSqlToRedShift)

            ReservedSystemEntity dataTypeCharForMsSqlToRedShift = new ReservedSystemEntity(key: 'char', value: 'VARCHAR', type: 14748, pluginId: 14)
            dataTypeCharForMsSqlToRedShift.id = 140000234L
            create(dataTypeCharForMsSqlToRedShift)

            ReservedSystemEntity dataTypeNcharForMsSqlToRedShift = new ReservedSystemEntity(key: 'nchar', value: 'VARCHAR', type: 14748, pluginId: 14)
            dataTypeNcharForMsSqlToRedShift.id = 140000235L
            create(dataTypeNcharForMsSqlToRedShift)

            ReservedSystemEntity dataTypeVarCharForMsSqlToRedShift = new ReservedSystemEntity(key: 'varchar', value: 'VARCHAR', type: 14748, pluginId: 14)
            dataTypeVarCharForMsSqlToRedShift.id = 140000236L
            create(dataTypeVarCharForMsSqlToRedShift)

            ReservedSystemEntity dataTypeNvarCharForMsSqlToRedShift = new ReservedSystemEntity(key: 'nvarchar', value: 'VARCHAR', type: 14748, pluginId: 14)
            dataTypeNvarCharForMsSqlToRedShift.id = 140000237L
            create(dataTypeNvarCharForMsSqlToRedShift)

            ReservedSystemEntity dataTypeBitForMsSqlToRedShift = new ReservedSystemEntity(key: 'bit', value: '[Unsupported Data Type:bit]', type: 14748, pluginId: 14)
            dataTypeBitForMsSqlToRedShift.id = 140000288L
            create(dataTypeBitForMsSqlToRedShift)

            ReservedSystemEntity dataTypeBinaryForMsSqlToRedShift = new ReservedSystemEntity(key: 'binary', value: '[Unsupported Data Type:binary]', type: 14748, pluginId: 14)
            dataTypeBinaryForMsSqlToRedShift.id = 140000289L
            create(dataTypeBinaryForMsSqlToRedShift)

            ReservedSystemEntity dataTypeVarBinaryForMsSqlToRedShift = new ReservedSystemEntity(key: 'varbinary', value: '[Unsupported Data Type:varbinary]', type: 14748, pluginId: 14)
            dataTypeVarBinaryForMsSqlToRedShift.id = 140000290L
            create(dataTypeVarBinaryForMsSqlToRedShift)

            ReservedSystemEntity dataTypeXmlForMsSqlToRedShift = new ReservedSystemEntity(key: 'xml', value: '[Unsupported Data Type:xml]', type: 14748, pluginId: 14)
            dataTypeXmlForMsSqlToRedShift.id = 140000291L
            create(dataTypeXmlForMsSqlToRedShift)

            ReservedSystemEntity dataTypeRowVersionForMsSqlToRedShift = new ReservedSystemEntity(key: 'rowversion', value: 'BIGINT', type: 14748, pluginId: 14)
            dataTypeRowVersionForMsSqlToRedShift.id = 140000238L
            create(dataTypeRowVersionForMsSqlToRedShift)

            ReservedSystemEntity dataTypeUniqueIdentifierForMsSqlToRedShift = new ReservedSystemEntity(key: 'uniqueidentifier', value: 'SMALLINT', type: 14748, pluginId: 14)
            dataTypeUniqueIdentifierForMsSqlToRedShift.id = 140000239L
            create(dataTypeUniqueIdentifierForMsSqlToRedShift)

            ReservedSystemEntity dataTypeTextForMsSqlToRedShift = new ReservedSystemEntity(key: 'text', value: 'VARCHAR', type: 14748, pluginId: 14)
            dataTypeTextForMsSqlToRedShift.id = 140000240L
            create(dataTypeTextForMsSqlToRedShift)

            ReservedSystemEntity dataTypeNtextForMsSqlToRedShift = new ReservedSystemEntity(key: 'ntext', value: 'VARCHAR', type: 14748, pluginId: 14)
            dataTypeNtextForMsSqlToRedShift.id = 140000241L
            create(dataTypeNtextForMsSqlToRedShift)

            ReservedSystemEntity dataTypeImageForMsSqlToRedShift = new ReservedSystemEntity(key: 'image', value: '[Unsupported Data Type:image]', type: 14748, pluginId: 14)
            dataTypeImageForMsSqlToRedShift.id = 140000292L
            create(dataTypeImageForMsSqlToRedShift)

            ReservedSystemEntity dataTypeIntMySqlToGreenPlum = new ReservedSystemEntity(key: 'INT', value: 'integer', type: 14749, pluginId: 14)
            dataTypeIntMySqlToGreenPlum.id = 140000242L
            create(dataTypeIntMySqlToGreenPlum)

            ReservedSystemEntity dataTypeTinyIntMySqlToGreenPlum = new ReservedSystemEntity(key: 'TINYINT', value: 'smallint', type: 14749, pluginId: 14)
            dataTypeTinyIntMySqlToGreenPlum.id = 140000243L
            create(dataTypeTinyIntMySqlToGreenPlum)

            ReservedSystemEntity dataTypeSmallIntMySqlToGreenPlum = new ReservedSystemEntity(key: 'SMALLINT', value: 'smallint', type: 14749, pluginId: 14)
            dataTypeSmallIntMySqlToGreenPlum.id = 140000244L
            create(dataTypeSmallIntMySqlToGreenPlum)

            ReservedSystemEntity dataTypeMediumIntMySqlToGreenPlum = new ReservedSystemEntity(key: 'MEDIUMINT', value: 'integer', type: 14749, pluginId: 14)
            dataTypeMediumIntMySqlToGreenPlum.id = 140000245L
            create(dataTypeMediumIntMySqlToGreenPlum)

            ReservedSystemEntity dataTypeBigIntMySqlToGreenPlum = new ReservedSystemEntity(key: 'BIGINT', value: 'bigint', type: 14749, pluginId: 14)
            dataTypeBigIntMySqlToGreenPlum.id = 140000246L
            create(dataTypeBigIntMySqlToGreenPlum)

            ReservedSystemEntity dataTypeFloatMySqlToGreenPlum = new ReservedSystemEntity(key: 'FLOAT', value: 'real', type: 14749, pluginId: 14)
            dataTypeFloatMySqlToGreenPlum.id = 140000247L
            create(dataTypeFloatMySqlToGreenPlum)

            ReservedSystemEntity dataTypeDoubleMySqlToGreenPlum = new ReservedSystemEntity(key: 'DOUBLE', value: 'double precision', type: 14749, pluginId: 14)
            dataTypeDoubleMySqlToGreenPlum.id = 140000248L
            create(dataTypeDoubleMySqlToGreenPlum)

            ReservedSystemEntity dataTypeRealMySqlToGreenPlum = new ReservedSystemEntity(key: 'REAL', value: 'double precision', type: 14749, pluginId: 14)
            dataTypeRealMySqlToGreenPlum.id = 140000249L
            create(dataTypeRealMySqlToGreenPlum)

            ReservedSystemEntity dataTypeDecimalMySqlToGreenPlum = new ReservedSystemEntity(key: 'DECIMAL', value: 'decimal', type: 14749, pluginId: 14)
            dataTypeDecimalMySqlToGreenPlum.id = 140000250L
            create(dataTypeDecimalMySqlToGreenPlum)

            ReservedSystemEntity dataTypeNumericMySqlToGreenPlum = new ReservedSystemEntity(key: 'NUMERIC', value: 'decimal', type: 14749, pluginId: 14)
            dataTypeNumericMySqlToGreenPlum.id = 140000251L
            create(dataTypeNumericMySqlToGreenPlum)

            ReservedSystemEntity dataTypeDateMySqlToGreenPlum = new ReservedSystemEntity(key: 'DATE', value: 'date', type: 14749, pluginId: 14)
            dataTypeDateMySqlToGreenPlum.id = 140000252L
            create(dataTypeDateMySqlToGreenPlum)

            ReservedSystemEntity dataTypeDateTimeMySqlToGreenPlum = new ReservedSystemEntity(key: 'DATETIME', value: 'timestamp without time zone', type: 14749, pluginId: 14)
            dataTypeDateTimeMySqlToGreenPlum.id = 140000253L
            create(dataTypeDateTimeMySqlToGreenPlum)

            ReservedSystemEntity dataTypeTimeStampMySqlToGreenPlum = new ReservedSystemEntity(key: 'TIMESTAMP', value: 'timestamp without time zone', type: 14749, pluginId: 14)
            dataTypeTimeStampMySqlToGreenPlum.id = 140000254L
            create(dataTypeTimeStampMySqlToGreenPlum)

            ReservedSystemEntity dataTypeTimeMySqlToGreenPlum = new ReservedSystemEntity(key: 'TIME', value: 'time without time zone', type: 14749, pluginId: 14)
            dataTypeTimeMySqlToGreenPlum.id = 140000255L
            create(dataTypeTimeMySqlToGreenPlum)

            ReservedSystemEntity dataTypeCharMySqlToGreenPlum = new ReservedSystemEntity(key: 'CHAR', value: 'character varying', type: 14749, pluginId: 14)
            dataTypeCharMySqlToGreenPlum.id = 140000256L
            create(dataTypeCharMySqlToGreenPlum)

            ReservedSystemEntity dataTypeVarCharMySqlToGreenPlum = new ReservedSystemEntity(key: 'VARCHAR', value: 'character varying', type: 14749, pluginId: 14)
            dataTypeVarCharMySqlToGreenPlum.id = 140000257L
            create(dataTypeVarCharMySqlToGreenPlum)

            ReservedSystemEntity dataTypeBlobMySqlToGreenPlum = new ReservedSystemEntity(key: 'BLOB', value: 'bytea', type: 14749, pluginId: 14)
            dataTypeBlobMySqlToGreenPlum.id = 140000258L
            create(dataTypeBlobMySqlToGreenPlum)

            ReservedSystemEntity dataTypeTextMySqlToGreenPlum = new ReservedSystemEntity(key: 'TEXT', value: 'text', type: 14749, pluginId: 14)
            dataTypeTextMySqlToGreenPlum.id = 140000259L
            create(dataTypeTextMySqlToGreenPlum)

            ReservedSystemEntity dataTypeTinyBlobMySqlToGreenPlum = new ReservedSystemEntity(key: 'TINYBLOB', value: 'bytea', type: 14749, pluginId: 14)
            dataTypeTinyBlobMySqlToGreenPlum.id = 140000260L
            create(dataTypeTinyBlobMySqlToGreenPlum)

            ReservedSystemEntity dataTypeTinyTextMySqlToGreenPlum = new ReservedSystemEntity(key: 'TINYTEXT', value: 'text', type: 14749, pluginId: 14)
            dataTypeTinyTextMySqlToGreenPlum.id = 140000261L
            create(dataTypeTinyTextMySqlToGreenPlum)

            ReservedSystemEntity dataTypeMediumBlobMySqlToGreenPlum = new ReservedSystemEntity(key: 'MEDIUMBLOB', value: 'bytea', type: 14749, pluginId: 14)
            dataTypeMediumBlobMySqlToGreenPlum.id = 140000262L
            create(dataTypeMediumBlobMySqlToGreenPlum)

            ReservedSystemEntity dataTypeMediumTextMySqlToGreenPlum = new ReservedSystemEntity(key: 'MEDIUMTEXT', value: 'text', type: 14749, pluginId: 14)
            dataTypeMediumTextMySqlToGreenPlum.id = 140000263L
            create(dataTypeMediumTextMySqlToGreenPlum)

            ReservedSystemEntity dataTypeLongBlobMySqlToGreenPlum = new ReservedSystemEntity(key: 'LONGBLOB', value: 'bytea', type: 14749, pluginId: 14)
            dataTypeLongBlobMySqlToGreenPlum.id = 140000264L
            create(dataTypeLongBlobMySqlToGreenPlum)

            ReservedSystemEntity dataTypeLongTextMySqlToGreenPlum = new ReservedSystemEntity(key: 'LONGTEXT', value: 'text', type: 14749, pluginId: 14)
            dataTypeLongTextMySqlToGreenPlum.id = 140000265L
            create(dataTypeLongTextMySqlToGreenPlum)

            ReservedSystemEntity dataTypeYearMySqlToGreenPlum = new ReservedSystemEntity(key: 'YEAR', value: 'smallint', type: 14749, pluginId: 14)
            dataTypeYearMySqlToGreenPlum.id = 140000285L
            create(dataTypeYearMySqlToGreenPlum)

            ReservedSystemEntity dataTypeIntMySqlToRedShift = new ReservedSystemEntity(key: 'INT', value: 'INTEGER', type: 14750, pluginId: 14)
            dataTypeIntMySqlToRedShift.id = 140000266L
            create(dataTypeIntMySqlToRedShift)

            ReservedSystemEntity dataTypeTinyIntMySqlToRedShift = new ReservedSystemEntity(key: 'TINYINT', value: 'SMALLINT', type: 14750, pluginId: 14)
            dataTypeTinyIntMySqlToRedShift.id = 140000267L
            create(dataTypeTinyIntMySqlToRedShift)

            ReservedSystemEntity dataTypeSmallIntMySqlToRedShift = new ReservedSystemEntity(key: 'SMALLINT', value: 'SMALLINT', type: 14750, pluginId: 14)
            dataTypeSmallIntMySqlToRedShift.id = 140000268L
            create(dataTypeSmallIntMySqlToRedShift)

            ReservedSystemEntity dataTypeMediumIntMySqlToRedShift = new ReservedSystemEntity(key: 'MEDIUMINT', value: 'INTEGER', type: 14750, pluginId: 14)
            dataTypeMediumIntMySqlToRedShift.id = 140000269L
            create(dataTypeMediumIntMySqlToRedShift)

            ReservedSystemEntity dataTypeBigIntMySqlToRedShift = new ReservedSystemEntity(key: 'BIGINT', value: 'BIGINT', type: 14750, pluginId: 14)
            dataTypeBigIntMySqlToRedShift.id = 140000270L
            create(dataTypeBigIntMySqlToRedShift)

            ReservedSystemEntity dataTypeFloatMySqlToRedShift = new ReservedSystemEntity(key: 'FLOAT', value: 'REAL', type: 14750, pluginId: 14)
            dataTypeFloatMySqlToRedShift.id = 140000271L
            create(dataTypeFloatMySqlToRedShift)

            ReservedSystemEntity dataTypeDoubleMySqlToRedShift = new ReservedSystemEntity(key: 'DOUBLE', value: 'DOUBLE PRECISION', type: 14750, pluginId: 14)
            dataTypeDoubleMySqlToRedShift.id = 140000272L
            create(dataTypeDoubleMySqlToRedShift)

            ReservedSystemEntity dataTypeRealMySqlToRedShift = new ReservedSystemEntity(key: 'REAL', value: 'DOUBLE PRECISION', type: 14750, pluginId: 14)
            dataTypeRealMySqlToRedShift.id = 140000273L
            create(dataTypeRealMySqlToRedShift)

            ReservedSystemEntity dataTypeDecimalMySqlToRedShift = new ReservedSystemEntity(key: 'DECIMAL', value: 'DECIMAL', type: 14750, pluginId: 14)
            dataTypeDecimalMySqlToRedShift.id = 140000274L
            create(dataTypeDecimalMySqlToRedShift)

            ReservedSystemEntity dataTypeNumericMySqlToRedShift = new ReservedSystemEntity(key: 'NUMERIC', value: 'DECIMAL', type: 14750, pluginId: 14)
            dataTypeNumericMySqlToRedShift.id = 140000275L
            create(dataTypeNumericMySqlToRedShift)

            ReservedSystemEntity dataTypeDateMySqlToRedShift = new ReservedSystemEntity(key: 'DATE', value: 'DATE', type: 14750, pluginId: 14)
            dataTypeDateMySqlToRedShift.id = 140000276L
            create(dataTypeDateMySqlToRedShift)

            ReservedSystemEntity dataTypeDateTimeMySqlToRedShift = new ReservedSystemEntity(key: 'DATETIME', value: 'TIMESTAMP', type: 14750, pluginId: 14)
            dataTypeDateTimeMySqlToRedShift.id = 140000277L
            create(dataTypeDateTimeMySqlToRedShift)

            ReservedSystemEntity dataTypeTimeStampMySqlToRedShift = new ReservedSystemEntity(key: 'TIMESTAMP', value: 'TIMESTAMP', type: 14750, pluginId: 14)
            dataTypeTimeStampMySqlToRedShift.id = 140000278L
            create(dataTypeTimeStampMySqlToRedShift)

            ReservedSystemEntity dataTypeTimeMySqlToRedShift = new ReservedSystemEntity(key: 'TIME', value: '[Unsupported Data Type:TIME]', type: 14750, pluginId: 14)
            dataTypeTimeMySqlToRedShift.id = 140000293L
            create(dataTypeTimeMySqlToRedShift)

            ReservedSystemEntity dataTypeCharMySqlToRedShift = new ReservedSystemEntity(key: 'CHAR', value: 'VARCHAR', type: 14750, pluginId: 14)
            dataTypeCharMySqlToRedShift.id = 140000279L
            create(dataTypeCharMySqlToRedShift)

            ReservedSystemEntity dataTypeVarCharMySqlToRedShift = new ReservedSystemEntity(key: 'VARCHAR', value: 'VARCHAR', type: 14750, pluginId: 14)
            dataTypeVarCharMySqlToRedShift.id = 140000280L
            create(dataTypeVarCharMySqlToRedShift)

            ReservedSystemEntity dataTypeTextMySqlToRedShift = new ReservedSystemEntity(key: 'TEXT', value: 'VARCHAR', type: 14750, pluginId: 14)
            dataTypeTextMySqlToRedShift.id = 140000281L
            create(dataTypeTextMySqlToRedShift)

            ReservedSystemEntity dataTypeTinyTextMySqlToRedShift = new ReservedSystemEntity(key: 'TINYTEXT', value: 'VARCHAR', type: 14750, pluginId: 14)
            dataTypeTinyTextMySqlToRedShift.id = 140000282L
            create(dataTypeTinyTextMySqlToRedShift)

            ReservedSystemEntity dataTypeMediumTextMySqlToRedShift = new ReservedSystemEntity(key: 'MEDIUMTEXT', value: 'VARCHAR', type: 14750, pluginId: 14)
            dataTypeMediumTextMySqlToRedShift.id = 140000283L
            create(dataTypeMediumTextMySqlToRedShift)

            ReservedSystemEntity dataTypeLongTextMySqlToRedShift = new ReservedSystemEntity(key: 'LONGTEXT', value: 'VARCHAR', type: 14750, pluginId: 14)
            dataTypeLongTextMySqlToRedShift.id = 140000284L
            create(dataTypeLongTextMySqlToRedShift)

            ReservedSystemEntity dataTypeYearMySqlToRedShift = new ReservedSystemEntity(key: 'YEAR', value: 'SMALLINT', type: 14750, pluginId: 14)
            dataTypeYearMySqlToRedShift.id = 140000286L
            create(dataTypeYearMySqlToRedShift)

            ReservedSystemEntity dataTypeBlobMySqlToRedShift = new ReservedSystemEntity(key: 'BLOB', value: '[Unsupported Data Type:BLOB]', type: 14750, pluginId: 14)
            dataTypeBlobMySqlToRedShift.id = 140000294L
            create(dataTypeBlobMySqlToRedShift)

            ReservedSystemEntity dataTypeTinyBlobMySqlToRedShift = new ReservedSystemEntity(key: 'TINYBLOB', value: '[Unsupported Data Type:TINYBLOB]', type: 14750, pluginId: 14)
            dataTypeTinyBlobMySqlToRedShift.id = 140000295L
            create(dataTypeTinyBlobMySqlToRedShift)

            ReservedSystemEntity dataTypeMediumBlobMySqlToRedShift = new ReservedSystemEntity(key: 'MEDIUMBLOB', value: '[Unsupported Data Type:MEDIUMBLOB]', type: 14750, pluginId: 14)
            dataTypeMediumBlobMySqlToRedShift.id = 140000296L
            create(dataTypeMediumBlobMySqlToRedShift)

            ReservedSystemEntity dataTypeLongBlobMySqlToRedShift = new ReservedSystemEntity(key: 'LONGBLOB', value: '[Unsupported Data Type:LONGBLOB]', type: 14750, pluginId: 14)
            dataTypeLongBlobMySqlToRedShift.id = 140000297L
            create(dataTypeLongBlobMySqlToRedShift)

            ReservedSystemEntity dataFeedTypeCsv = new ReservedSystemEntity(key: 'CSV', value: 'CSV', type: 14751, pluginId: 14)
            dataFeedTypeCsv.id = 140000298L
            create(dataFeedTypeCsv)

            ReservedSystemEntity dataFeedTypeTxt = new ReservedSystemEntity(key: 'TEXT', value: 'TEXT', type: 14751, pluginId: 14)
            dataFeedTypeTxt.id = 140000299L
            create(dataFeedTypeTxt)

            ReservedSystemEntity colSep = new ReservedSystemEntity(key: 'Column Separator', value: ',', type: 14752, pluginId: 14)
            colSep.id = 140000300L
            create(colSep)

            ReservedSystemEntity quoteChar = new ReservedSystemEntity(key: 'Quote Character', value: '"', type: 14752, pluginId: 14)
            quoteChar.id = 140000301L
            create(quoteChar)

            ReservedSystemEntity escChar = new ReservedSystemEntity(key: 'Escape Character', value: '\\', type: 14752, pluginId: 14)
            escChar.id = 140000302L
            create(escChar)

            ReservedSystemEntity colsSepForText = new ReservedSystemEntity(key: 'Column Separator', value: '|', type: 14753, pluginId: 14)
            colsSepForText.id = 140000303L
            create(colsSepForText)

            ReservedSystemEntity escCharForText = new ReservedSystemEntity(key: 'Escape Character', value: '\\', type: 14753, pluginId: 14)
            escCharForText.id = 140000304L
            create(escCharForText)

        } catch (Exception ex) {
            log.error(ex.getMessage())
            throw new RuntimeException(ex)
        }
    }

    @Override
    public void createDefaultSchema() {
        String sqlIndex = "CREATE UNIQUE INDEX reserved_system_entity_key_type_idx ON reserved_system_entity(lower(key),type);"
        executeSql(sqlIndex)
    }

    @Override
    public boolean createTestData(long companyId, long systemUserId) { return true }
}
