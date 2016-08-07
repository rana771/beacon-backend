package com.athena.mis.application.dts

import com.athena.mis.application.dts.impl.*
import com.athena.mis.application.entity.AppDbInstance
import com.athena.mis.application.service.AppSystemEntityCacheService
import groovy.transform.CompileStatic

@CompileStatic
class DataAdapterFactoryService {

    AppSystemEntityCacheService appSystemEntityCacheService

    private Map mySqlDataAdapters = new HashMap<String, DataAdapterService>();
    private Map postgresDataAdapters = new HashMap<String, DataAdapterService>();
    private Map msSql2008DataAdapters = new HashMap<String, DataAdapterService>();
    private Map msSql2012DataAdapters = new HashMap<String, DataAdapterService>();
    private Map amazonRedShiftDataAdapters = new HashMap<String, DataAdapterService>();
    private Map greenPlumDataAdapters = new HashMap<String, DataAdapterService>();

    /**
     * Initialize Adapter for supported vendor
     *
     * @param instance - an object of AppDbInstance
     * @return initialized Adapter object
     * @throws NoSuchAdapterException
     */
    public DataAdapterService createAdapter(AppDbInstance instance) throws NoSuchAdapterException {

        if (instance.reservedVendorId == appSystemEntityCacheService.SYS_ENTITY_VENDOR_MYSQL) {
            DataAdapterService mySqlDataAdapterService = getMySqlDataAdapterService(instance);
            return mySqlDataAdapterService;
        } else if (instance.reservedVendorId == appSystemEntityCacheService.SYS_ENTITY_VENDOR_POSTGRES) {
            DataAdapterService postgresDataAdapterService = getPostgresDataAdapterService(instance);
            return postgresDataAdapterService;
        } else if (instance.reservedVendorId == appSystemEntityCacheService.SYS_ENTITY_VENDOR_AMAZON_REDSHIFT) {
            DataAdapterService amazonRedshiftDataAdapterService = getAmazonRedshiftDataAdapterService(instance);
            return amazonRedshiftDataAdapterService;
        } else if (instance.reservedVendorId == appSystemEntityCacheService.SYS_ENTITY_VENDOR_MSSQL_2008) {
            DataAdapterService mssql2008DataAdapterService = getMSSQL2008DataAdapterService(instance);
            return mssql2008DataAdapterService;
        } else if (instance.reservedVendorId == appSystemEntityCacheService.SYS_ENTITY_VENDOR_MSSQL_2012) {
            DataAdapterService mssql2012DataAdapterService = getMSSQL2012DataAdapterService(instance);
            return mssql2012DataAdapterService;
        } else if (instance.reservedVendorId == appSystemEntityCacheService.SYS_ENTITY_VENDOR_GREEN_PLUM) {
            DataAdapterService greenPlumDataAdapterService = getGreenPlumDataAdapterService(instance);
            return greenPlumDataAdapterService;
        }
        throw new NoSuchAdapterException("No adapter found for : " + instance.getDriver());
    }

    private DataAdapterService getMySqlDataAdapterService(AppDbInstance instance) {
        DataAdapterService mySqlDataAdapterService = (DataAdapterService) mySqlDataAdapters.get(instance.getConnectionString());
        if (mySqlDataAdapterService != null) {
            return mySqlDataAdapterService;
        } else {
            mySqlDataAdapterService = new MySqlDataAdapterService(instance);
            mySqlDataAdapters.put(mySqlDataAdapterService.getConnectionString(), mySqlDataAdapterService);
            return mySqlDataAdapterService;
        }
    }

    private DataAdapterService getPostgresDataAdapterService(AppDbInstance instance) {
        DataAdapterService postgresDataAdapterService = (DataAdapterService) postgresDataAdapters.get(instance.getConnectionString());
        if (postgresDataAdapterService != null) {
            return postgresDataAdapterService;
        } else {
            postgresDataAdapterService = new PostgresDataAdapterService(instance);
            postgresDataAdapters.put(postgresDataAdapterService.getConnectionString(), postgresDataAdapterService);
            return postgresDataAdapterService;
        }
    }

    private DataAdapterService getAmazonRedshiftDataAdapterService(AppDbInstance instance) {
        DataAdapterService amazonRedshiftDataAdapterService = (DataAdapterService) amazonRedShiftDataAdapters.get(instance.getConnectionString());
        if (amazonRedshiftDataAdapterService != null) {
            return amazonRedshiftDataAdapterService;
        } else {
            amazonRedshiftDataAdapterService = new AmazonRedshiftDataAdapterService(instance);
            amazonRedShiftDataAdapters.put(amazonRedshiftDataAdapterService.getConnectionString(), amazonRedshiftDataAdapterService);
            return amazonRedshiftDataAdapterService;
        }
    }

    private DataAdapterService getMSSQL2008DataAdapterService(AppDbInstance instance) {
        DataAdapterService msSql2008DataAdapterService = (DataAdapterService) msSql2008DataAdapters.get(instance.getConnectionString());
        if (msSql2008DataAdapterService != null) {
            return msSql2008DataAdapterService;
        } else {
            msSql2008DataAdapterService = new MSSQL2008DataAdapterService(instance);
            msSql2008DataAdapters.put(msSql2008DataAdapterService.getConnectionString(), msSql2008DataAdapterService);
            return msSql2008DataAdapterService;
        }
    }

    private DataAdapterService getMSSQL2012DataAdapterService(AppDbInstance instance) {
        DataAdapterService msSql2012DataAdapterService = (DataAdapterService) msSql2012DataAdapters.get(instance.getConnectionString());
        if (msSql2012DataAdapterService != null) {
            return msSql2012DataAdapterService;
        } else {
            msSql2012DataAdapterService = new MSSQL2012DataAdapterService(instance);
            msSql2012DataAdapters.put(msSql2012DataAdapterService.getConnectionString(), msSql2012DataAdapterService);
            return msSql2012DataAdapterService;
        }
    }

    private DataAdapterService getGreenPlumDataAdapterService(AppDbInstance instance) {
        DataAdapterService greenPlumDataAdapterService = (DataAdapterService) greenPlumDataAdapters.get(instance.getConnectionString());
        if (greenPlumDataAdapterService != null) {
            return greenPlumDataAdapterService;
        } else {
            greenPlumDataAdapterService = new GreenPlumDataAdapterService(instance);
            greenPlumDataAdapters.put(greenPlumDataAdapterService.getConnectionString(), greenPlumDataAdapterService);
            return greenPlumDataAdapterService;
        }
    }

    public void deleteAdapter(AppDbInstance instance) {
        if (instance.reservedVendorId == appSystemEntityCacheService.SYS_ENTITY_VENDOR_MYSQL) {
            deleteMySqlDataAdapterService(instance);
        } else if (instance.reservedVendorId == appSystemEntityCacheService.SYS_ENTITY_VENDOR_POSTGRES) {
            deletePostgresDataAdapterService(instance);
        } else if (instance.reservedVendorId == appSystemEntityCacheService.SYS_ENTITY_VENDOR_AMAZON_REDSHIFT) {
            deleteAmazonRedshiftDataAdapterService(instance);
        } else if (instance.reservedVendorId == appSystemEntityCacheService.SYS_ENTITY_VENDOR_MSSQL_2008) {
            deleteMSSQL2008DataAdapterService(instance);
        } else if (instance.reservedVendorId == appSystemEntityCacheService.SYS_ENTITY_VENDOR_MSSQL_2012) {
            deleteMSSQL2012DataAdapterService(instance);
        } else if (instance.reservedVendorId == appSystemEntityCacheService.SYS_ENTITY_VENDOR_GREEN_PLUM) {
            deleteGreenPlumDataAdapterService(instance);
        }
    }

    private void deleteMySqlDataAdapterService(AppDbInstance instance) {
        DataAdapterService mySqlDataAdapterService = (DataAdapterService) mySqlDataAdapters.get(instance.getConnectionString());
        if (mySqlDataAdapterService != null) {
            mySqlDataAdapters.remove(instance.getConnectionString());
        }
    }

    private void deletePostgresDataAdapterService(AppDbInstance instance) {
        DataAdapterService postgresDataAdapterService = (DataAdapterService) postgresDataAdapters.get(instance.getConnectionString());
        if (postgresDataAdapterService != null) {
            postgresDataAdapters.remove(instance.getConnectionString());
        }
    }

    private void deleteAmazonRedshiftDataAdapterService(AppDbInstance instance) {
        DataAdapterService amazonRedshiftDataAdapterService = (DataAdapterService) amazonRedShiftDataAdapters.get(instance.getConnectionString());
        if (amazonRedshiftDataAdapterService != null) {
            amazonRedShiftDataAdapters.remove(instance.getConnectionString());
        }
    }

    private void deleteMSSQL2008DataAdapterService(AppDbInstance instance) {
        DataAdapterService msSql2008DataAdapterService = (DataAdapterService) msSql2008DataAdapters.get(instance.getConnectionString());
        if (msSql2008DataAdapterService != null) {
            msSql2008DataAdapters.remove(instance.getConnectionString());
        }
    }

    private void deleteMSSQL2012DataAdapterService(AppDbInstance instance) {
        DataAdapterService msSql2012DataAdapterService = (DataAdapterService) msSql2012DataAdapters.get(instance.getConnectionString());
        if (msSql2012DataAdapterService != null) {
            msSql2012DataAdapters.remove(instance.getConnectionString());
        }
    }

    private void deleteGreenPlumDataAdapterService(AppDbInstance instance) {
        DataAdapterService greenPlumDataAdapterService = (DataAdapterService) greenPlumDataAdapters.get(instance.getConnectionString());
        if (greenPlumDataAdapterService != null) {
            greenPlumDataAdapters.remove(instance.getConnectionString());
        }
    }
}
