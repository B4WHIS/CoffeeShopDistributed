package server.mapper;

import common.dto.CustomerDto;
import common.entity.Customer;

public interface CustomerMapper {
	public default CustomerDto toDto(Customer c) {
		if (c == null)
			return null;
		return new CustomerDto(c.getId(), c.getPhoneNumber(), c.getFullName(), c.getAccumulatedPoints(), c.getMemberRank(), c.getCreatedAt());
	}
	
	public default Customer toEntity(CustomerDto cDto) {
		if(cDto == null)
			return null;
		return new Customer(cDto.id(), cDto.phoneNumber(), cDto.fullName()
				, cDto.accumulatedPoints(), cDto.memberRank(), cDto.createdAt());
		
	}
}
