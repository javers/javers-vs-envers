package org.javers.organization.structure

import groovy.sql.Sql
import org.javers.core.Javers
import org.javers.repository.api.JaversRepository
import org.javers.repository.sql.JaversSqlRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service

@Service
class TheCleaner {
    private Sql sql

    @Autowired
    JaversSqlRepository javersRepository

    TheCleaner(@Value('${spring.datasource.url}') String dbUrl,
               @Value('${spring.datasource.username}') dbUser,
               @Value('${spring.datasource.password}') dbPass,
               @Value('${spring.datasource.driver-class-name}') dbDriver) {
        sql = Sql.newInstance(dbUrl, dbUser, dbPass, dbDriver)
    }

    void clean() {
        println 'cleaning db ...'
        sql.execute('delete from jv_snapshot')
        sql.execute('delete from jv_global_id')
        sql.execute('delete from jv_commit')
        sql.execute('delete from employee_aud')
        sql.execute('delete from revinfo')
        sql.execute('delete from employee')

        javersRepository.evictCache()
    }
}
