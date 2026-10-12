package server.serviceimpl;

import java.util.ArrayList;
import java.util.List;

import common.dto.InvoiceDTO;
import common.dto.OrderDetailDTO;
import common.entity.Account;
import common.entity.Customer;
import common.entity.Invoice;
import common.entity.Item;
import common.entity.OrderDetail;
import server.dao.AccountDAO;
import server.dao.CustomerDAO;
import server.dao.InvoiceDAO;
import server.dao.ItemDAO;
import server.mapper.InvoiceMapper;
import server.service.InvoiceService;

public class InvoiceServiceImpl implements InvoiceService, InvoiceMapper {
	private final InvoiceDAO invoiceDao = new InvoiceDAO();
	private final AccountDAO accountDao = new AccountDAO();
	private final CustomerDAO customerDao = new CustomerDAO();
	private final ItemDAO itemDao = new ItemDAO();

	@Override
	public boolean createInvoice(InvoiceDTO dto) {
		Account findAcc = accountDao.findById(dto.accountId());
		if (findAcc == null)
			return false;

		Customer findCus = null;
		if (dto.customerId() != null && dto.customerId() > 0) {
			findCus = customerDao.findById(dto.customerId());
		}
		Invoice invoice = toEntity(dto, findAcc, findCus);
		invoice.setId(0);

		List<OrderDetail> details = new ArrayList<>();
		for (OrderDetailDTO odDto : dto.orderDetails()) {
			Item item = itemDao.findById(odDto.itemId());
			OrderDetail od = new OrderDetail(0, odDto.itemName(), odDto.unitPrice(), odDto.quantity(), odDto.subTotal(),
					invoice, item);
			details.add(od);

		}
		invoice.setOrderDetails(details);
		return invoiceDao.add(invoice);
	}

	@Override
	public InvoiceDTO getById(int id) {
		Invoice invoice = invoiceDao.findById(id);
		return toDto(invoice);
	}

	@Override
	public List<InvoiceDTO> getAll() {
		// TODO Auto-generated method stub
		return invoiceDao.findAll().stream().map(item -> toDto(item)).toList();
	}

	@Override
	public double getDailyRevenue(String date) {
		return invoiceDao.getDailyRevenue(date);
	}

	@Override
	public double getMonthlyRevenue(int month, int year) {
		// TODO Auto-generated method stub
		return invoiceDao.getMonthlyRevenue(month, year);
	}

}
