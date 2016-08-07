package com.athena.mis.application.service

import com.athena.mis.BaseSystemEntityCacheService
import com.athena.mis.PluginConnector
import com.athena.mis.application.entity.SystemEntity
import com.athena.mis.application.entity.SystemEntityType

class AppSystemEntityCacheService extends BaseSystemEntityCacheService {

    SystemEntityTypeService systemEntityTypeService

    // initialize
    @Override
    public void init() {
        List<SystemEntityType> lstSysEntityType = systemEntityTypeService.findAllByPluginId(PluginConnector.PLUGIN_ID)
        super.load(lstSysEntityType, PluginConnector.PLUGIN_ID)
    }

    // type ids of SystemEntityType
    public static final long SYS_ENTITY_TYPE_BENCHMARK = 1733L
    public static final long SYS_ENTITY_TYPE_VENDOR = 1735L
    public static final long SYS_ENTITY_TYPE_NOTE = 1729L
    public static final long SYS_ENTITY_TYPE_OS_VENDOR = 1741L
    public static final long SYS_ENTITY_TYPE_USER_ENTITY = 651L
    public static final long SYS_ENTITY_TYPE_UNIT = 251L
    public static final long SYS_ENTITY_TYPE_SUPPLIER = 704L
    public static final long SYS_ENTITY_TYPE_SCRIPT = 1744L
    public static final long SYS_ENTITY_TYPE_SCHEDULE = 1736L
    public static final long SYS_ENTITY_TYPE_CONTENT_ENTITY = 701L
    public static final long SYS_ENTITY_TYPE_CONTENT = 702L
    public static final long SYS_ENTITY_TYPE_DB_INSTANCE = 1746L
    public static final long SYS_ENTITY_TYPE_DB_OBJECT = 1743L
    public static final long SYS_ENTITY_TYPE_GENDER = 1717L
    public static final long SYS_ENTITY_TYPE_ITEM_CATEGORY = 705L
    public static final long SYS_ENTITY_TYPE_MAIL_CONFIGURATION = 1751L
    public static final long SYS_ENTITY_TYPE_NOTE_ENTITY = 703L
    public static final long SYS_ENTITY_TYPE_OWNER = 551L
    public static final long SYS_ENTITY_TYPE_PAYMENT_METHOD = 1L
    public static final long SYS_ENTITY_TYPE_QUERY = 1745L
    public static final long SYS_ENTITY_TYPE_TRANSACTION_LOG = 1738L
    public static final long SYS_ENTITY_TYPE_VALUATION = 451L
    public static final long SYS_ENTITY_TYPE_TRUSTED_IP_ADDRESS = 1737L
    public static final long SYS_ENTITY_TYPE_MIME_TYPE = 1754L
    public static final long SYS_ENTITY_TYPE_FAQ = 1755L
    public static final long SYS_ENTITY_TYPE_HIERARCHY_LEVEL = 1757L
    public static final long SYS_ENTITY_TYPE_PAGE = 1760L

    // reserved ids for benchmark sample domain type
    public static final long SYS_ENTITY_BENCHMARK_SMALL = 1137L
    public static final long SYS_ENTITY_BENCHMARK_MEDIUM = 1138L
    public static final long SYS_ENTITY_BENCHMARK_LARGE = 1139L

    // reserved ids for vendor
    public static final long SYS_ENTITY_VENDOR_POSTGRES = 1143L
    public static final long SYS_ENTITY_VENDOR_MYSQL = 1144L
    public static final long SYS_ENTITY_VENDOR_ORACLE = 1145L
    public static final long SYS_ENTITY_VENDOR_MSSQL_2008 = 1146L
    public static final long SYS_ENTITY_VENDOR_MSSQL_2012 = 10000305L
    public static final long SYS_ENTITY_VENDOR_AMAZON_REDSHIFT = 1147L
    public static final long SYS_ENTITY_VENDOR_GREEN_PLUM = 10000189L

    // reserved ids for note
    public static final long SYS_ENTITY_NOTE_NONE = 1118L
    public static final long SYS_ENTITY_NOTE_SYSTEM_NOTE = 1119L
    public static final long SYS_ENTITY_NOTE_VERIFIED_AND_APPROVED = 1120L

    // reserved ids for os vendor
    public static final long SYS_ENTITY_OS_VENDOR_LINUX = 10000171L
    public static final long SYS_ENTITY_OS_VENDOR_FREE_BDS = 10000172L

    // reserved ids for user entity
    public static final long SYS_ENTITY_USER_ENTITY_CUSTOMER = 136     //exhCustomer
    public static final long SYS_ENTITY_USER_ENTITY_BANK_BRANCH = 137
    public static final long SYS_ENTITY_USER_ENTITY_PROJECT = 138
    public static final long SYS_ENTITY_USER_ENTITY_PT_PROJECT = 1059
    public static final long SYS_ENTITY_USER_ENTITY_INVENTORY = 139
    public static final long SYS_ENTITY_USER_ENTITY_GROUP = 140
    public static final long SYS_ENTITY_USER_ENTITY_AGENT = 141
    public static final long SYS_ENTITY_USER_ENTITY_EXCHANGE_HOUSE = 1186

    // reserved ids for script
    public static final long SYS_ENTITY_SCRIPT_SHELL = 10000181L
    public static final long SYS_ENTITY_SCRIPT_SQL = 10000182L

    // reserved ids for schedule
    public static final long SIMPLE = 1148L
    public static final long CRON = 1149L

    // reserved ids for content entity
    public static final long SYS_ENTITY_CONTENT_ENTITY_APP_USER = 142L
    public static final long SYS_ENTITY_CONTENT_ENTITY_USER_DOCUMENT = 10000363L
    public static final long SYS_ENTITY_CONTENT_ENTITY_COMPANY = 143L
    public static final long SYS_ENTITY_CONTENT_ENTITY_EXH_CUSTOMER = 144L
    public static final long SYS_ENTITY_CONTENT_ENTITY_PROJECT = 145L
    public static final long SYS_ENTITY_CONTENT_ENTITY_PT_BUG = 1058L
    public static final long SYS_ENTITY_CONTENT_ENTITY_BUDGET = 184L
    public static final long SYS_ENTITY_CONTENT_ENTITY_BUDG_SPRINT = 192L
    public static final long SYS_ENTITY_CONTENT_ENTITY_FINANCIAL_YEAR = 185L
    public static final long SYS_ENTITY_CONTENT_ENTITY_PT_BACKLOG = 10000158L
    public static final long SYS_ENTITY_CONTENT_ENTITY_APP_MAIL = 10000322L
    public static final long SYS_ENTITY_CONTENT_ENTITY_EL_ASSIGNMENT = 10000354L

    // reserved ids for content
    public static final long SYS_ENTITY_CONTENT_DOCUMENT = 146L
    public static final long SYS_ENTITY_CONTENT_IMAGE = 147L

    // reserved ids for db instance
    public static final long SYS_ENTITY_DB_INSTANCE_SOURCE = 10000187L
    public static final long SYS_ENTITY_DB_INSTANCE_TARGET = 10000188L

    // reserved ids for db object
    public static final long SYS_ENTITY_DB_OBJECT_TABLE = 10000179
    public static final long SYS_ENTITY_DB_OBJECT_VIEW = 10000180

    // reserved ids for gender
    public static final long SYS_ENTITY_GENDER_MALE = 176L
    public static final long SYS_ENTITY_GENDER_FEMALE = 177L

    // reserved ids for item category
    public static final long SYS_ENTITY_ITEM_CATEGORY_INVENTORY = 150L
    public static final long SYS_ENTITY_ITEM_CATEGORY_NON_INVENTORY = 151L
    public static final long SYS_ENTITY_ITEM_CATEGORY_FIXED_ASSET = 152L

    // reserved ids for mail configuration
    public static final long SYS_ENTITY_MAIL_CONFIG_EMAIL_FROM = 10000306L
    public static final long SYS_ENTITY_MAIL_CONFIG_SMTP_HOST = 10000307L
    public static final long SYS_ENTITY_MAIL_CONFIG_SMTP_PORT = 10000308L
    public static final long SYS_ENTITY_MAIL_CONFIG_SMTP_EMAIL = 10000309L
    public static final long SYS_ENTITY_MAIL_CONFIG_SMTP_PWD = 10000310L

    // reserved ids for note entity
    public static final long SYS_ENTITY_NOTE_TASK = 148L
    public static final long SYS_ENTITY_NOTE_CUSTOMER = 149L
    public static final long SYS_ENTITY_NOTE_PT_TASK = 1094L
    public static final long SYS_ENTITY_NOTE_PT_BUG = 100000169L
    public static final long SYS_ENTITY_NOTE_DB_QUERY = 10000183L
    public static final long SYS_ENTITY_NOTE_SCRIPT = 10000185L
    public static final long SYS_ENTITY_NOTE_RMS_TASK = 1181L
    public static final long SYS_ENTITY_NOTE_DOC_DOCUMENT = 130000162L
    public static final long SYS_ENTITY_NOTE_BLOG = 10000356L
    public static final long SYS_ENTITY_NOTE_POST = 10000362L

    // reserved ids for owner
    public static final long SYS_ENTITY_OWNER_PURCHASED = 132L
    public static final long SYS_ENTITY_OWNER_RENTAL = 133L

    // reserved ids for query
    public static final long SYS_ENTITY_QUERY_DIAGNOSTIC = 10000184L
    public static final long SYS_ENTITY_QUERY_MAINTENANCE = 10000186L

    // reserved ids for transaction log
    public static final long SYS_ENTITY_TRANSACTION_LOG_DATA_EXPORT = 10000151L
    public static final long SYS_ENTITY_TRANSACTION_LOG_BENCHMARK = 10000152L
    public static final long SYS_ENTITY_TRANSACTION_LOG_BENCHMARK_STAR = 10000153L
    public static final long SYS_ENTITY_TRANSACTION_LOG_DATA_IMPORT = 10000154L

    // reserved ids for valuation
    public final long SYS_ENTITY_VALUATION_FIFO = 127L
    public final long SYS_ENTITY_VALUATION_LIFO = 128L
    public final long SYS_ENTITY_VALUATION_AVG = 129L

    // reserved ids for SYS_ENTITY_TYPE_MIME_TYPE
    public final long SYS_ENTITY_MIME_TYPE_PDF = 10000323L
    public final long SYS_ENTITY_MIME_TYPE_TXT = 10000324L
    public final long SYS_ENTITY_MIME_TYPE_DOC = 10000325L
    public final long SYS_ENTITY_MIME_TYPE_DOCX = 10000326L
    public final long SYS_ENTITY_MIME_TYPE_XLS = 10000327L
    public final long SYS_ENTITY_MIME_TYPE_XLSX = 10000328L
    public final long SYS_ENTITY_MIME_TYPE_PPT = 10000329L
    public final long SYS_ENTITY_MIME_TYPE_PPTX = 10000330L
    public final long SYS_ENTITY_MIME_TYPE_CSV = 10000331L
    public final long SYS_ENTITY_MIME_TYPE_JPG = 10000332L
    public final long SYS_ENTITY_MIME_TYPE_JPEG = 10000333L
    public final long SYS_ENTITY_MIME_TYPE_PNG = 10000334L
    public final long SYS_ENTITY_MIME_TYPE_GIF = 10000335L
    public final long SYS_ENTITY_MIME_TYPE_MP3 = 10000336L
    public final long SYS_ENTITY_MIME_TYPE_MP4 = 10000337L
    public final long SYS_ENTITY_MIME_TYPE_BMP = 10000338L

    // reserved ids for SYS_ENTITY_TYPE_FAQ
    public static final long SYS_ENTITY_FAQ_DOC_DOCUMENT = 10000339L
    public static final long SYS_ENTITY_FAQ_DOC_SUB_CATEGORY = 10000341L

    public static final long SYS_ENTITY_CONTENT_ENTITY_COURSE = 10000344L
    public static final long SYS_ENTITY_CONTENT_ENTITY_LESSON = 10000345L

    // reserved ids for SYS_ENTITY_TYPE_HIERARCHY_LEVEL
    public static final long SYS_ENTITY_HIERARCHY_ROOT = 10000346L
    public static final long SYS_ENTITY_HIERARCHY_CHILD = 10000347L

    // reserved ids for SYS_ENTITY_TYPE_PAGE
    public static final long SYS_ENTITY_PAGE_LESSON_BLOG = 10000355L
    public static final long SYS_ENTITY_PAGE_LESSON_POST = 10000361L

    // check if ip address is in list
    public boolean isIpAddressTrusted(String ipAddress, long companyId) {
        long typeId = SYS_ENTITY_TYPE_TRUSTED_IP_ADDRESS
        List<SystemEntity> lstSysEntity = (List) sysEntityMap.get(typeId.toLong())
        boolean trusted = false
        for (int i = 0; i < lstSysEntity.size(); i++) {
            if ((lstSysEntity[i].value.equals(ipAddress)) && (lstSysEntity[i].isActive) && (lstSysEntity[i].companyId == companyId)) {
                trusted = true
                break
            }
        }
        return trusted
    }
}
