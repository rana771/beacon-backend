package com.athena.mis

import com.athena.mis.application.entity.SystemEntity
import com.athena.mis.application.entity.SystemEntityType

public abstract class BaseSystemEntityCacheService {

    // map of list by type id
    public Map<Long, List> sysEntityMap = new HashMap<Long, List>()

    // initialize
    public abstract void init()

    // put system entity list by type id in map
    public void load(List<SystemEntityType> lstSysEntityType, long pluginId) {
        for (int i = 0; i < lstSysEntityType.size(); i++) {
            sysEntityMap.put(lstSysEntityType[i].id, [])
        }

        List<SystemEntity> lstSysEntity = SystemEntity.findAllByPluginId(pluginId)
        for (int i = 0; i < lstSysEntity.size(); i++) {
            SystemEntity systemEntity = lstSysEntity[i]
            Long sysEntityTypeId = new Long(systemEntity.type)
            List lstSysEntityForType = (List) sysEntityMap.get(sysEntityTypeId)
            lstSysEntityForType << systemEntity
            sysEntityMap.put(sysEntityTypeId, lstSysEntityForType)
        }
    }

    // init list after create, update and delete
    public void initByType(long typeId) {
        List<SystemEntity> lstSysEntity = SystemEntity.findAllByType(typeId)

        List lstSysEntityForType = []
        for (int i = 0; i < lstSysEntity.size(); i++) {
            SystemEntity systemEntity = lstSysEntity[i]
            lstSysEntityForType << systemEntity
            sysEntityMap.put(typeId, lstSysEntityForType)
        }
    }

    // read SystemEntity object by id
    public SystemEntity read(long id, long typeId, long companyId) {
        List lstSysEntity = (List) sysEntityMap.get(typeId.toLong())
        for (int i = 0; i < lstSysEntity.size(); i++) {
            SystemEntity systemEntity = (SystemEntity) lstSysEntity[i]
            if (systemEntity.id == id && systemEntity.companyId == companyId) {
                return systemEntity
            }
        }
        return null
    }

    // read SystemEntity object by reserved id
    public SystemEntity readByReservedId(long reservedId, long typeId, long companyId) {
        List lstSysEntity = (List) sysEntityMap.get(typeId.toLong())
        for (int i = 0; i < lstSysEntity.size(); i++) {
            SystemEntity systemEntity = (SystemEntity) lstSysEntity[i]
            if (systemEntity.reservedId == reservedId && systemEntity.companyId == companyId) {
                return systemEntity
            }
        }
        return null
    }

    // read SystemEntity object by key
    public SystemEntity readByKey(String key, long companyId) {
        List lstSysEntity = (List) sysEntityMap.get(key)
        for (int i = 0; i < lstSysEntity.size(); i++) {
            SystemEntity systemEntity = (SystemEntity) lstSysEntity[i]
            if (systemEntity.key.equalsIgnoreCase(key) && systemEntity.companyId == companyId) {
                return systemEntity
            }
        }
        return null
    }

    // get list of active SystemEntity object
    public List<SystemEntity> listByIsActive(long typeId, long companyId) {
        List lstSysEntity = (List) sysEntityMap.get(typeId.toLong())
        List<SystemEntity> lstActive = []
        for (int i = 0; i < lstSysEntity.size(); i++) {
            SystemEntity systemEntity = (SystemEntity) lstSysEntity[i]
            if (systemEntity.isActive && systemEntity.companyId == companyId) {
                lstActive << systemEntity
            }
        }
        return lstActive
    }

    // get list of SystemEntity object by company id
    public List<SystemEntity> list(long typeId, long companyId) {
        List lstSysEntity = (List) sysEntityMap.get(typeId.toLong())
        List<SystemEntity> lstTemp = []
        for (int i = 0; i < lstSysEntity.size(); i++) {
            SystemEntity systemEntity = (SystemEntity) lstSysEntity[i]
            if (systemEntity.companyId == companyId) {
                lstTemp << systemEntity
            }
        }
        return lstTemp
    }
}
