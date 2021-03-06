package org.javers.organization.structure

import org.javers.core.Javers
import org.javers.repository.jql.QueryBuilder
import org.javers.shadow.Shadow
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.annotation.Rollback
import spock.lang.Specification

@SpringBootTest
@Rollback(false)
class JaversQueryTest extends Specification{
    @Autowired
    HierarchyService hierarchyService

    @Autowired
    TheCleaner theCleaner

    @Autowired
    Javers javers

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
                            .withSnapshotTypeUpdate()
                            .build())

        println 'javers history of Employees:'
        shadows.each { shadow ->
            println 'commit:' + shadow.commitMetadata.id + ', entity: '+ shadow.get()
        }

      then:
        shadows.size() == 4
        shadows[0].commitMetadata.id.majorId == 5
        shadows[3].commitMetadata.id.majorId == 2
    }

    def "should browse JaVers history of objects by type with filters"(){
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
        List<Shadow<Employee>> shadows = javers.findShadows(
                QueryBuilder.byInstanceId('Aragorn', Employee).build())

      then:
        println 'javers history of Aragorn:'
        shadows.each { shadow ->
          println 'commit:' + shadow.commitMetadata.id + ', entity: '+ shadow.get()
        }
        shadows.size() == 3

      when: 'query with Property filter'
        shadows = javers.findShadows(
                QueryBuilder.byClass(Employee)
                            .withChangedProperty('salary')
                            .withSnapshotTypeUpdate()
                            .build())

      then:
        println 'javers history of salary changes:'
        shadows.each { shadow ->
            println 'commit:' + shadow.commitMetadata.id + ', entity: '+ shadow.get()
        }
        shadows.size() == 3
    }

    def "should reconstruct a full object graph with JaVers"(){
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
        List<Shadow<Employee>> shadows = javers.findShadows(
                QueryBuilder.byInstanceId('Thorin', Employee)
                            .withScopeDeepPlus()
                            .build())

      then:
        def thorinShadow = shadows.collect{it.get()}.find{it.salary == 6000}
        [thorinShadow,
         thorinShadow.getBoss(),
         thorinShadow.getBoss().getBoss(),
         thorinShadow.getSubordinate("Bombur")].each
        {
            println it
            assert it.salary == 6000
        }
        println "JaVers query executed in " + (System.currentTimeMillis() - start) + " millis"
    }

    def setup() {
        theCleaner.clean()
    }
}
