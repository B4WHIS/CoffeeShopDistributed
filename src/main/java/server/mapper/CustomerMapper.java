package server.mapper;

import common.dto.CustomerDTO;
import common.entity.Customer;

public interface CustomerMapper {
	public default CustomerDTO toDto(Customer c) {
		if (c == null)
			return null;
		return new CustomerDTO(c.getId(), c.getPhoneNumber(), c.getFullName(), c.getAccumulatedPoints(), c.getMemberRank(), c.getCreatedAt());
	}
	
	public default Customer toEntity(CustomerDTO cDto) {
		if(cDto == null)
			return null;
		return new Customer(cDto.id(), cDto.phoneNumber(), cDto.fullName()
				, cDto.accumulatedPoints(), cDto.memberRank(), cDto.createdAt());
		
	}
}
