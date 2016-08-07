package com.athena.mis.application.service

import com.athena.mis.BaseDomainService
import com.athena.mis.application.entity.ElBlogNote
import com.athena.mis.utility.DateUtility


class ElBlogNoteService  extends BaseDomainService {

     TestDataModelService testDataModelService

            @Override
            public void init() {
                domainClass = ElBlogNote.class
            }


            /**
             * Pull elBlogNote object
             * @return - list of elBlogNote
             */
            @Override
            public List<ElBlogNote> list() {
              return ElBlogNote.findAllByCompanyId(companyId, [sort: ElBlogNote.DEFAULT_SORT_FIELD, order: ASCENDING_SORT_ORDER, readOnly: true]);
           }

    /**
    * Get list of ElBlogNote by list of ids
    * @param lstElBlogNoteIds - list of AppGroup.id
    * @return - list of ElBlogNote by ids
    */
    public List<ElBlogNote> findAllByIdInList(List<Long> lstElBlogNoteIds) {
        List<ElBlogNote> lstElBlogNote = ElBlogNote.findAllByIdInList(lstElBlogNoteIds, [sort: ElBlogNote.DEFAULT_SORT_FIELD, order: ASCENDING_SORT_ORDER, readOnly: true])
        return lstElBlogNote
    }



    @Override
    public void createDefaultSchema() {
            String nameIndex = "create unique index el_blog_note_name_company_id_idx on elBlogNote(lower(name),company_id);"
            executeSql(nameIndex)
            String codeIndex = "create unique index el_blog_note_code_company_id_idx on elBlogNote(lower(code),company_id);"
            executeSql(codeIndex)
        }

        public int countByCompanyId(long companyId) {
        return ElBlogNote.countByCompanyId(companyId)
    }


    @Override
    public boolean createTestData(long companyId, long userId) {
      //Write your default data insert statement here
        return true
    }


}
