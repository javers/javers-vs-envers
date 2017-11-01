package org.javers.organization.structure

import org.hibernate.envers.Audited
import org.javers.common.string.ToStringBuilder

import javax.persistence.*

@Entity
@Audited
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

    Employee getSubordinate(String name) {
        subordinates.find {it.name.equals(name)}
    }

    void addSubordinate(Employee subordinate) {
        subordinate.boss = this
        this.subordinates.add(subordinate)
    }

    void addSubordinates(Employee... subordinates) {
        subordinates.each {addSubordinate(it)}
    }

    void giveRaise(int raise) {
        salary += raise
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
    String toString() {
        'Employee{ ' +
            name + ' ' + position + ', $'  + salary + ', ' + address?.city +
            ', subordinates:'+ToStringBuilder.join(subordinates.collect{it.name}) +
        ' }'
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
