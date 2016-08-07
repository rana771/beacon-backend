package com.athena.mis

/**
 * Created by Qutub on 10/25/2015.
 */
class DataImportDto {
    long id
    long entityTypeId
    String name
    long dataFeedTypeId
    long dataFeedTypeTxtId
    long dataFeedTypeCsvId
    long companyId
    String url
    String dataFeedPath
    String vendorDir
    List lstTableName
    String columnSeparator
    String escapeCharacter
    String quoteCharacter
    long maxRecordPerFile
    String entityDomainName
    String schemaName
    String exportedObjName
    String greenPlumMountLoc
    String fileExt
    String copyScript
    String dplAwsBaseUrl
    String dplAwsAccessKeyId
    String dplAwsSecretAccessKey
}
