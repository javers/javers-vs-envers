package org.javers.organization.structure

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.annotation.Rollback
import spock.lang.Specification

@SpringBootTest
@Rollback(false)
class InitHierarchyTest extends Specification {

    @Autowired
    HierarchyService hierarchyService

    @Autowired
    TheCleaner theCleaner

    def "should init and persist organization structure"(){
      given:
      def boss = hierarchyService.initStructure()

      boss.prettyPrint()

      expect:
      boss.name == "Gandalf"
    }

    def setup() {
        theCleaner.clean()
    }
}
