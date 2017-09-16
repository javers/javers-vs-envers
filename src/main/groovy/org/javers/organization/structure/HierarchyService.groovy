package org.javers.organization.structure

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

import static Position.*

@Service
class HierarchyService {

    @Autowired EmployeeRepository employeeRepository

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

        employeeRepository.save(gandalf)

        gandalf
    }

    void giveRaise(Employee employee, int raise) {
        employee.giveRaise(raise)
        employeeRepository.save(employee)
    }

    Employee findByName(String name) {
        employeeRepository.findOne(name)
    }
}
