package org.javers.organization.structure.domain

import static org.javers.organization.structure.domain.Position.*

class HierarchyService {

    Employee initStructure(){

        def gandalf = new Employee('Gandalf', 10_000, CEO, 'Middle-earth')
        def elrond = new Employee('Elrond', 8_000, CFO, 'Rivendell')
        def aragorn = new Employee('Aragorn', 8_000, CTO, 'Minas Tirith')
        def thorin = new Employee('Thorin', 5_000, TEAM_LEAD, 'Lonely Mountain')
        def frodo = new Employee('Frodo', 3_000, DEVELOPER, 'Shire')
        def fili = new Employee('Fili', 3_000, DEVELOPER, 'Lonely Mountain')
        def kili = new Employee('Kili', 3_000, DEVELOPER, 'Lonely Mountain')
        def bifur = new Employee('Bifur', 3_000, DEVELOPER, 'Lonely Mountain')
        def bombur = new Employee('Bombur', 2_000, SCRUM_MASTER, 'Lonely Mountain')

        gandalf.addSubordinates(elrond, aragorn)
        aragorn.addSubordinate(thorin)
        thorin.addSubordinates(frodo, fili, kili, bifur, bombur)

        gandalf
    }
}
