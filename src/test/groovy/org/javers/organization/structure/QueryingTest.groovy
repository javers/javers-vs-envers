package org.javers.organization.structure

import org.hibernate.envers.AuditReaderFactory
import org.hibernate.envers.query.AuditEntity
import org.javers.core.Javers
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import spock.lang.Specification

import javax.persistence.EntityManager
import javax.persistence.PersistenceContext
import javax.transaction.Transactional

import static org.hibernate.envers.RevisionType.MOD

@SpringBootTest
class QueryingTest extends Specification{
    @Autowired
    HierarchyService hierarchyService

    @Autowired
    TheCleaner theCleaner

    @Autowired
    Javers javers

    @PersistenceContext
    EntityManager entityManager

    @Transactional
    def "should browse history of objects by type"(){
        given:
        def gandalf = hierarchyService.initStructure()
        def aragorn = gandalf.getSubordinate('Aragorn')
        gandalf.prettyPrint()

        //changes
        hierarchyService.giveRaise(gandalf, 200)
        hierarchyService.updateCity(gandalf, 'Shire')
        hierarchyService.giveRaise(aragorn, 100)
        hierarchyService.updateCity(aragorn, 'Shire')

        when: 'envers query'
        List folks = AuditReaderFactory
                .get( entityManager )
                .createQuery()
                .forRevisionsOfEntity( Employee, false, true )
                .add(AuditEntity.revisionType().eq(MOD))
                .getResultList()

        println 'envers history of Employee:'
        folks.each {
            println 'revision:' + it[1].id + ', entity: '+ it[0]
        }

        then:
        folks.size() == 4
    }

    def setup() {
        theCleaner.cleanDb()
    }
}
