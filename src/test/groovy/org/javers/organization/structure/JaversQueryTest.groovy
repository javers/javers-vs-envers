package org.javers.organization.structure

import org.javers.core.Javers
import org.javers.repository.jql.QueryBuilder
import org.javers.shadow.Shadow
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import spock.lang.Specification

@SpringBootTest
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
