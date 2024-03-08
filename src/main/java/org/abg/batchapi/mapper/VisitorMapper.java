package org.abg.batchapi.mapper;

import org.abg.batchapi.dto.VisitorDto;
import org.abg.batchapi.entity.Visitor;
import org.mapstruct.*;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE, componentModel = MappingConstants.ComponentModel.SPRING)
public interface VisitorMapper {
    Visitor toEntity(VisitorDto visitorDto);

    VisitorDto toDto(Visitor visitor);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    Visitor partialUpdate(VisitorDto visitorDto, @MappingTarget Visitor visitor);
}