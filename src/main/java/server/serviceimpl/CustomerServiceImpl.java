package server.serviceimpl;

import java.util.List;

import common.dto.CustomerDTO;
import common.entity.Customer;
import server.dao.CustomerDAO;
import server.mapper.CustomerMapper;
import server.service.CustomerService;

public class CustomerServiceImpl implements CustomerService, CustomerMapper {
	private final CustomerDAO customerDao = new CustomerDAO();
	@Override
	public List<CustomerDTO> getAll() {
		return customerDao.findAll().stream().map(item -> toDto(item)).toList();
	}

	@Override
	public CustomerDTO getById(int id) {
		Customer cus = customerDao.findById(id);
		return toDto(cus);
		
	}

	@Override
	public CustomerDTO getByPhoneNumber(String phoneNumber) {
		Customer cus = customerDao.findByPhoneNumber(phoneNumber);
		return toDto(cus);
	}

	@Override
	public boolean add(CustomerDTO dto) {
		if (customerDao.findByPhoneNumber(dto.phoneNumber()) != null) return false;
		Customer cus = toEntity(dto);
		cus.setId(0);
		return customerDao.add(cus);
	}

	@Override
	public boolean update(CustomerDTO dto) {
		if (customerDao.findById(dto.id()) == null) return false;
		return customerDao.update(toEntity(dto));
	}

	@Override
	public boolean accumulatePoints(int customerId, int points) {
		Customer cus = customerDao.findById(customerId);
		if(cus == null) return false;
		int newPoints = cus.getAccumulatedPoints() + points;
		cus.setAccumulatedPoints(newPoints);
		
		if(cus.getAccumulatedPoints() >= 500)
			cus.setMemberRank("Kim cương");
		else if(cus.getAccumulatedPoints() >= 300)
			cus.setMemberRank("Vàng");
		else if(cus.getAccumulatedPoints() >= 100)
			cus.setMemberRank("Bạc");
		else if(cus.getAccumulatedPoints() < 100)
			cus.setMemberRank("Đồng");
		return customerDao.update(cus);
	}

}
