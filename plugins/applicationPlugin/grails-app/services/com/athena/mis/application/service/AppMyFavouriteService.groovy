package com.athena.mis.application.service

import com.athena.mis.BaseDomainService
import com.athena.mis.application.entity.AppMyFavourite

class AppMyFavouriteService extends BaseDomainService {

    @Override
    public void init() {
        domainClass = AppMyFavourite.class
    }

    @Override
    public void createDefaultSchema() {
        String sqlIndex = "CREATE UNIQUE INDEX app_my_favourite_user_id_url_company_id_idx ON app_my_favourite(lower(url),user_id,company_id);"
        executeSql(sqlIndex)
    }

    @Override
    public boolean createTestData(long companyId, long systemUserId) { return true }

    public AppMyFavourite findByUserIdAndIsDefault(long userId, boolean isDefault) {
        return AppMyFavourite.findByUserIdAndIsDefault(userId, isDefault, [readOnly: true])
    }

    public AppMyFavourite findByUrlAndUserId(String url, long userId) {
        return AppMyFavourite.findByUrlAndUserId(url, userId, [readOnly: true])
    }

    public int countByUrlAndUserId(String url, long userId) {
        return AppMyFavourite.countByUrlAndUserId(url, userId)
    }

    private static final String UPDATE_IS_DIRTY_AND_IS_DEFAULT = """
        UPDATE app_my_favourite
        SET is_dirty = TRUE,
            is_default = FALSE,
            version = version + 1
        WHERE
            url =:url AND
            user_id = :userId
    """

    public int updateIsDirtyAndIsDefault(Map result) {
        String bookMarkUrl = result.bookMarkUrl.toString()
        if (result.bookMarkUrl.toString().indexOf("_") > 0) {
            bookMarkUrl = result.bookMarkUrl.toString().replaceAll("_", "&")
        }
        Map queryParams = [url: bookMarkUrl, userId: super.getAppUser().id]
        int updateCount = executeUpdateSql(UPDATE_IS_DIRTY_AND_IS_DEFAULT, queryParams)
        if (updateCount <= 0) {
            throw new RuntimeException("Failed to update isDirty and isDefault")
        }
        return updateCount
    }

    public void setIsDirtyAndIsDefault(Map resultMap) {
        if (resultMap.isBookMark) {
            updateIsDirtyAndIsDefault(resultMap)
        }
    }

}
