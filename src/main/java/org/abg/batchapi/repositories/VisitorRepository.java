package org.abg.batchapi.repositories;


import org.abg.batchapi.entities.Visitor;
import org.springframework.data.jpa.repository.JpaRepository;


public interface VisitorRepository extends JpaRepository<Visitor, Long> {

}
