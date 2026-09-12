package server.mapper;

import common.dto.AccountDto;
import common.entity.Account;

public interface AccountMapper {
	public default AccountDto toDto(Account a) {
		if(a == null)
			return null;
		return new AccountDto(a.getId(), a.getUsername(), a.getFullName(),a.getRole() , a.getPhoneNumber() , a.isActive());
	}
	public default Account toEntity(AccountDto aDto) {
		if (aDto == null)
			return null;
		return new Account(aDto.id(), aDto.username(),"", aDto.fullName(),
				aDto.role(), aDto.phoneNumber(), aDto.active());
	}
}
