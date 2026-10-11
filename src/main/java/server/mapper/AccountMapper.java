package server.mapper;

import common.dto.AccountDTO;
import common.entity.Account;

public interface AccountMapper {
	public default AccountDTO toDto(Account a) {
		if(a == null)
			return null;
		return new AccountDTO(a.getId(), a.getUsername(), a.getFullName(),a.getRole() , a.getPhoneNumber() , a.isActive());
	}
	public default Account toEntity(AccountDTO aDto) {
		if (aDto == null)
			return null;
		return new Account(aDto.id(), aDto.username(),"", aDto.fullName(),
				aDto.role(), aDto.phoneNumber(), aDto.active());
	}
}
