package org.javers.organization.structure

import javax.persistence.Column
import javax.persistence.Embeddable

@Embeddable
class Address {
    @Column(name = "address_city")
    String city

    @Column(name = "address_street")
    String street

    Address() {
    }

    Address(String city) {
        this.city = city
    }
}
