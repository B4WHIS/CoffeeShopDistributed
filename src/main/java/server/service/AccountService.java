package server.service;

import java.util.List;

import common.dto.AccountDTO;

public interface AccountService {
	AccountDTO login(String userName, String password);
	boolean changePassword(int accountId, String oldPassword, String newPassword);
	List<AccountDTO> getAll();
	boolean add(AccountDTO dt, String password);
}
