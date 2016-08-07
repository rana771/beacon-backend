package com.athena.mis.integration.projecttrack

import com.athena.mis.BaseService
import com.athena.mis.PluginConnector
import com.athena.mis.application.entity.Company
import com.athena.mis.application.entity.SysConfiguration
import com.athena.mis.application.entity.SystemEntity

public abstract class PtPluginConnector extends PluginConnector {

    public static final String PLUGIN_NAME = "ProjectTrack";
    public static final int PLUGIN_ID = 10
    public static final String PLUGIN_PREFIX = "PT"

    public abstract int getPluginVersion()

    // init list after create, update and delete
    public abstract void initByType(long typeId)

    // get list of active SystemEntity object
    public abstract List listByIsActive(long typeId, long companyId)

    // read SystemEntity object by reserved id
    public abstract SystemEntity readByReservedId(long reservedId, long typeId, long companyId)

    //init SysConfiguration
    public abstract void initPtSysConfiguration()

    // init default data
	public abstract void bootStrap(Company company)

    //read ptProject object by id
    public abstract Object readPtProject(long id)

    // read object of task(backlog)
    public abstract Object readTask(long id)
       // read object of PtBug
    public abstract Object readBug(long id)

    public abstract SysConfiguration readSysConfiguration(String key)

    // create test data
    public abstract void loadTestData(long companyId, long systemUserId)

    // delete test data
    public abstract void deleteTestData()

    public abstract long getSystemEntityTypeAcceptanceCriteriaStatus()

    public abstract long getSystemEntityTypeAcceptanceCriteriaType()

    public abstract long getSystemEntityTypeBacklogPriority()

    public abstract long getSystemEntityTypeBacklogStatus()

    public abstract long getSystemEntityTypeBugSeverity()

    public abstract long getSystemEntityTypeBugStatus()

    public abstract long getSystemEntityTypeBugType()

    public abstract long getSystemEntityTypeChangeRequestStatus()

    public abstract long getSystemEntityTypeDataType()

    public abstract long getSystemEntityTypeEntityRelationType()

    public abstract long getSystemEntityTypeSprintStatus()

    // sys config list for grid
    public abstract Map sysConfigListForGrid(BaseService baseService, long companyId)

    // sys config search list for grid
    public abstract Map sysConfigSearchListForGrid(BaseService baseService, long companyId)
}
