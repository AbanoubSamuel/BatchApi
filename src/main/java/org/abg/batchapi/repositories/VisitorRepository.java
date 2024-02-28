package org.abg.batchapi.repositories;


import org.abg.batchapi.entities.Visitor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.math.BigDecimal;

public interface VisitorRepository extends JpaRepository<Visitor, Long> {
    Slice<Visitor> findAllBy(long visitor, Pageable request);

}
