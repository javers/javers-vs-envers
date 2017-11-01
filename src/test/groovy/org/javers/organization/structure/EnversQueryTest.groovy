package org.javers.organization.structure

import org.hibernate.envers.AuditReaderFactory
import org.hibernate.envers.query.AuditEntity
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import spock.lang.Specification
import javax.persistence.EntityManager
import javax.persistence.PersistenceContext
import javax.transaction.Transactional

import static org.hibernate.envers.RevisionType.MOD

@SpringBootTest
class EnversQueryTest extends Specification{
    @Autowired
    HierarchyService hierarchyService

    @Autowired
    TheCleaner theCleaner

    @PersistenceContext
    EntityManager entityManager

    @Transactional
    def "should browse Envers history of objects by type"(){
      given:
        def gandalf = hierarchyService.initStructure()
        def aragorn = gandalf.getSubordinate('Aragorn')
        gandalf.prettyPrint()

        //changes
        hierarchyService.giveRaise(gandalf, 200)
        hierarchyService.updateCity(gandalf, 'Shire')
        hierarchyService.giveRaise(aragorn, 100)
        hierarchyService.updateCity(aragorn, 'Shire')

      when:
        List folks = AuditReaderFactory
                .get(entityManager)
                .createQuery()
                .forRevisionsOfEntity( Employee, false, true )
                .add(AuditEntity.revisionType().eq(MOD)) // filter out initial versions
                .getResultList()

        println 'envers history of Employees:'
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
