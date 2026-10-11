package server.serviceimpl;

import java.util.List;

import common.dto.AccountDTO;
import common.entity.Account;
import server.dao.AccountDAO;
import server.mapper.AccountMapper;
import server.service.AccountService;

public class AccountServiceImpl implements AccountService, AccountMapper{
	private final AccountDAO accountDao = new AccountDAO();
	
	@Override
	public AccountDTO login(String userName, String password) {
		Account account = accountDao.findByUsername(userName);
		if (account == null || !account.isActive() || !account.getPassword().equals(password)) return null;
		return toDto(account);
	}

	@Override
	public boolean changePassword(int accountId, String oldPassword, String newPassword) {
		Account account = accountDao.findById(accountId);
		if (account == null || !account.getPassword().equals(oldPassword)) return false;
		account.setPassword(newPassword);
		return accountDao.update(account);
	}

	@Override
	public List<AccountDTO> getAll() {
		return accountDao.findAll().stream().map(item -> toDto(item)).toList();
	}

	@Override
	public boolean add(AccountDTO dt, String password) {
		Account account = accountDao.findByUsername(dt.username());
		if(account != null) return false;
		
		Account accountNew = new Account(0,dt.username(),password, dt.fullName(), dt.role(), dt.phoneNumber(), dt.active());
		return accountDao.add(accountNew);
	}
	
}
