package server.service;

import java.util.List;

import common.dto.InvoiceDTO;

public interface InvoiceService {
	boolean createInvoice(InvoiceDTO dto);
	InvoiceDTO getById(int id);
	List<InvoiceDTO> getAll();
	double getDailyRevenue(String date);
	double getMonthlyRevenue(int month, int year);
}
