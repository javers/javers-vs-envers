package org.javers.organization.structure.domain

import org.junit.runner.RunWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.annotation.Rollback
import spock.lang.Specification

@SpringBootTest
@Rollback(false)
class HierarchyServiceIntegrationSpec extends Specification {

    @Autowired
    HierarchyService hierarchyService

    def "should init and persist organization structure"(){
      given:
      def boss = hierarchyService.initStructure()

      boss.prettyPrint()

      expect:
      boss.name == "Gandalf"
    }
}
