package com.athena.mis

import com.athena.mis.application.entity.Company

public abstract class BaseBootStrapService {

    public abstract boolean init(Company company)
    public abstract boolean init()

}
