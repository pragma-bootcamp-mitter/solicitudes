package co.com.pragma.bootcamp.r2dbc.mapper;

import co.com.pragma.bootcamp.model.loantype.LoanType;
import co.com.pragma.bootcamp.r2dbc.entity.LoanTypeEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface LoanTypeMapper {
    @Mapping(source = "loanTypeId", target = "id")
    LoanType toDomain(LoanTypeEntity entity);
}