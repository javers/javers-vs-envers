package org.javers.organization.structure;

//import org.javers.spring.annotation.JaversSpringDataAuditable;
import org.springframework.data.repository.CrudRepository;

//@JaversSpringDataAuditable
public interface EmployeeRepository extends CrudRepository<Employee, String> {
}
