package org.javers.organization.structure.domain

import spock.lang.Specification

class HierarchyServiceSpec extends Specification {

    def "should init organization structure"(){
      given:
      def boss = new HierarchyService().initStructure()

      boss.prettyPrint()

      expect:
      boss.name == "Gandalf"
    }
}
