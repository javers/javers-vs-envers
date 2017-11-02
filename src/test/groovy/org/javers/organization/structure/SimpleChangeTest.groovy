package org.javers.organization.structure

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.annotation.Rollback
import spock.lang.Specification

@SpringBootTest
@Rollback(false)
class SimpleChangeTest extends Specification {

    @Autowired
    HierarchyService hierarchyService

    @Autowired
    TheCleaner theCleaner

    def "should persist Employee's property change"(){
        given:
        def boss = hierarchyService.initStructure()

        hierarchyService.giveRaise(boss, 200)

        expect:
        hierarchyService.findByName("Gandalf").salary == 10200
    }

    def setup() {
        theCleaner.clean()
    }
}
