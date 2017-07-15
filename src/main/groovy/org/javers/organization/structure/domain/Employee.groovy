package org.javers.organization.structure.domain

import javax.persistence.AttributeOverride
import javax.persistence.AttributeOverrides
import javax.persistence.CascadeType
import javax.persistence.Column
import javax.persistence.Embedded
import javax.persistence.Entity
import javax.persistence.Id
import javax.persistence.ManyToOne
import javax.persistence.OneToMany

@Entity
class Employee {
    @Id
    String name

    @ManyToOne
    Employee boss

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "boss")
    Set<Employee> subordinates = new HashSet()

    @Embedded
    Address address

    Integer salary
    Position position

    Employee() {
    }

    Employee(String name, Integer salary, Position position, String city) {
        this.name = name
        this.address = new Address(city)
        this.salary = salary
        this.position = position
    }

    void addSubordinate(Employee subordinate) {
        subordinate.boss = this
        this.subordinates.add(subordinate)
    }

    void addSubordinates(Employee... subordinates) {
        subordinates.each {addSubordinate(it)}
    }

    int getLevel() {
        if (boss == null) return 0
        return boss.level + 1
    }

    void prettyPrint() {
        println '--'.multiply(level) + (level == 0 ? '':' ') + name + ' ' + position + ', $'  + salary + ', ' + address.city
        subordinates.each {it.prettyPrint()}
    }

    @Override
    int hashCode() {
        Objects.hashCode(name)
    }

    @Override
    boolean equals(Object obj) {
        obj instanceof Employee && obj.name == this.name
    }
}
