package org.javers.organization.structure

import org.hibernate.envers.AuditReaderFactory
import org.hibernate.envers.query.AuditEntity
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.annotation.Rollback
import spock.lang.Specification
import javax.persistence.EntityManager
import javax.persistence.PersistenceContext
import javax.transaction.Transactional

import static org.hibernate.envers.RevisionType.MOD

@SpringBootTest
@Rollback(false)
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
                .add(AuditEntity.revisionType().eq(MOD)) // without initial versions
                .getResultList()

        println 'envers history of Employees:'
        folks.each {
            println 'revision:' + it[1].id + ', entity: '+ it[0]
        }

      then:
        folks.size() == 4
    }

    @Transactional
    def "should browse Envers history of objects by type with filters"(){
      given:
        def gandalf = hierarchyService.initStructure()
        def aragorn = gandalf.getSubordinate('Aragorn')
        def thorin = aragorn.getSubordinate('Thorin')

        //changes
        [gandalf, aragorn, thorin].each {
            hierarchyService.giveRaise(it, 100)
            hierarchyService.updateCity(it, 'Shire')
        }

      when: 'query with Id filter'
        List aragorns = AuditReaderFactory
              .get(entityManager)
              .createQuery()
              .forRevisionsOfEntity( Employee, false, true )
              .add( AuditEntity.id().eq( 'Aragorn' ) )
              .getResultList()

      then:
        println 'envers history of Aragorn:'
        aragorns.each {
            println 'revision:' + it[1].id + ', entity: '+ it[0]
        }
        aragorns.size() == 3

      when: 'query with Property filter'
      List folks = AuditReaderFactory
              .get(entityManager)
              .createQuery()
              .forRevisionsOfEntity( Employee, false, true )
              .add(AuditEntity.property('salary').hasChanged())
              .add(AuditEntity.revisionType().eq(MOD))
              .getResultList()

      then:
        println 'envers history of salary changes:'
        folks.each {
            println 'revision:' + it[1].id + ', entity: '+ it[0]
        }
        folks.size() == 3
    }

    @Transactional
    def "should reconstruct a full object graph with Envers"(){
      given:
        def gandalf = hierarchyService.initStructure()
        def aragorn = gandalf.getSubordinate('Aragorn')
        def thorin = aragorn.getSubordinate('Thorin')
        def bombur = thorin.getSubordinate("Bombur")

        [gandalf,aragorn, bombur].each {
            hierarchyService.updateSalary(it, 6000)
        }

        hierarchyService.giveRaise(thorin, 1000)
        //this state we want to reconstruct,
        //when all the four guys have salary $6000
        gandalf.prettyPrint()

        [gandalf, aragorn, thorin, bombur].each {
            hierarchyService.giveRaise(it, 500)
        }

      when:
        def start = System.currentTimeMillis()
        List thorins = AuditReaderFactory
              .get(entityManager)
              .createQuery()
              .forRevisionsOfEntity( Employee, false, true )
              .add( AuditEntity.id().eq( 'Thorin' ) )
              .getResultList()

      then:
        def thorinShadow = thorins.collect{it[0]}.find{it.salary == 6000}

        [thorinShadow,
         thorinShadow.getBoss(),
         thorinShadow.getBoss().getBoss(),
         thorinShadow.getSubordinate("Bombur")].each
        {
            println it
            assert it.salary == 6000
        }
        println "Envers query executed in " + (System.currentTimeMillis() - start) + " millis"
    }

    def setup() {
        theCleaner.clean()
    }
}
