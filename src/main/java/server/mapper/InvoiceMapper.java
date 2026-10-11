package server.mapper;

import java.util.ArrayList;
import java.util.List;

import common.dto.InvoiceDTO;
import common.dto.OrderDetailDTO;
import common.entity.Account;
import common.entity.Customer;
import common.entity.Invoice;
import common.entity.OrderDetail;

public interface InvoiceMapper {
	
	public default InvoiceDTO toDto(Invoice i) {
		
		if (i == null) {
			return null;
		}
		
		List<OrderDetailDTO> details = new ArrayList<OrderDetailDTO>();
	
		if (i.getOrderDetails() != null) {
			for (OrderDetail od : i.getOrderDetails()) {
				OrderDetailDTO addOd = new OrderDetailDTO(
						od.getId(), 
						od.getItem() != null ? od.getItem().getId() : 0, 
						od.getItemName(), od.getUnitPrice(), 
						od.getQuantity(), od.getSubTotal());
				details.add(addOd);
			}
		}
		

		
		return new InvoiceDTO(i.getId(), i.getOrderNumber(), i.getOrderType(), i.getCreatedAt(), i.getSubTotalAmount(), i.getDiscountAmount(), i.getTotalAmount(), i.getPaymentMethod(), i.getStatus(), 
				i.getAccount() != null ? i.getAccount().getId() : 0, 
				i.getAccount() != null ? i.getAccount().getFullName() : "",
				i.getCustomer() != null ? i.getCustomer().getId() : null, 
				i.getCustomer() != null ? i.getCustomer().getFullName() : "Khách vãng lai", 
				i.getCustomer() != null ? i.getCustomer().getPhoneNumber() : "", 
				details
					
				);
	}
	
	public default Invoice toEntity(InvoiceDTO iDto, Account a, Customer c) {
		if(iDto == null)
			return null;
		
		
		
		return new Invoice(iDto.id(), iDto.orderNumber(), iDto.orderType(),
				iDto.createdAt(), iDto.subTotalAmount(), iDto.discountAmount(),
				iDto.totalAmount(), iDto.paymentMethod(), iDto.status(),
				a, new ArrayList<OrderDetail>(), c);
		
		
	}
	
}
