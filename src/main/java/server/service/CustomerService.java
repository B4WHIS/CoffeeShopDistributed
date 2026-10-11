package server.service;

import java.util.List;

import common.dto.CustomerDTO;

public interface CustomerService {
	List<CustomerDTO> getAll();
	CustomerDTO getById(int id);
	CustomerDTO getByPhoneNumber(String phoneNumber);
	boolean add(CustomerDTO dto);
	boolean update(CustomerDTO dto);
	boolean accumulatePoints(int customerId, int points);
}
