package org.abg.batchapi.repository;


import org.abg.batchapi.entity.Visitor;
import org.springframework.data.repository.PagingAndSortingRepository;

public interface VisitorPagingRepository extends PagingAndSortingRepository<Visitor, Long> {

}
