package org.javers.organization.structure.domain

import javax.persistence.Embeddable

@Embeddable
class Address {
    String city
    String street
    String houseNo

    Address() {
    }

    Address(String city) {
        this.city = city
    }
}
