package org.javers.organization.structure

import org.hibernate.envers.AuditReaderFactory
import org.hibernate.envers.query.AuditEntity
import org.javers.core.Javers
import org.javers.repository.jql.QueryBuilder
import org.javers.shadow.Shadow
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

    def "should browse JaVers history of objects by type"(){
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
        List<Shadow<Employee>> shadows = javers.findShadows(
                QueryBuilder.byClass(Employee)
                            .withChildValueObjects()
                            .build())
                .subList(0,4) //no better way to filter out initial versions

        println 'javers history of Employees:'
        shadows.each { shadow ->
            println 'commit:' + shadow.commitMetadata.id + ', entity: '+ shadow.get()
        }

        then:
        shadows.size() == 4
        shadows[0].commitMetadata.id.majorId == 5
        shadows[3].commitMetadata.id.majorId == 2
    }

    def setup() {
        theCleaner.cleanDb()
    }
}
