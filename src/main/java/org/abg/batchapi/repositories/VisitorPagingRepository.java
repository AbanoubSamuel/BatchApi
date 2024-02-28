package org.abg.batchapi.repositories;


import org.abg.batchapi.entities.Visitor;
import org.springframework.data.repository.PagingAndSortingRepository;

public interface VisitorPagingRepository extends PagingAndSortingRepository<Visitor, Long> {

}
