package server.mapper;

import java.util.ArrayList;
import java.util.List;

import common.dto.InvoiceDto;
import common.dto.OrderDetailDto;
import common.entity.Account;
import common.entity.Customer;
import common.entity.Invoice;
import common.entity.OrderDetail;

public interface InvoiceMapper {
	
	public default InvoiceDto toDto(Invoice i) {
		
		if (i == null) {
			return null;
		}
		
		List<OrderDetailDto> details = new ArrayList<OrderDetailDto>();
	
		if (i.getOrderDetails() != null) {
			for (OrderDetail od : i.getOrderDetails()) {
				OrderDetailDto addOd = new OrderDetailDto(
						od.getId(), 
						od.getItem() != null ? od.getItem().getId() : 0, 
						od.getItemName(), od.getUnitPrice(), 
						od.getQuantity(), od.getSubTotal());
				details.add(addOd);
			}
		}
		

		
		return new InvoiceDto(i.getId(), i.getOrderNumber(), i.getOrderType(), i.getCreatedAt(), i.getSubTotalAmount(), i.getDiscountAmount(), i.getTotalAmount(), i.getPaymentMethod(), i.getStatus(), 
				i.getAccount() != null ? i.getAccount().getId() : 0, 
				i.getAccount() != null ? i.getAccount().getFullName() : "",
				i.getCustomer() != null ? i.getCustomer().getId() : null, 
				i.getCustomer() != null ? i.getCustomer().getFullName() : "Khách vãng lai", 
				i.getCustomer() != null ? i.getCustomer().getPhoneNumber() : "", 
				details
					
				);
	}
	
	public default Invoice toEntity(InvoiceDto iDto, Account a, Customer c) {
		if(iDto == null)
			return null;
		
		
		
		return new Invoice(iDto.id(), iDto.orderNumber(), iDto.orderType(),
				iDto.createdAt(), iDto.subTotalAmount(), iDto.discountAmount(),
				iDto.totalAmount(), iDto.paymentMethod(), iDto.status(),
				a, new ArrayList<OrderDetail>(), c);
		
		
	}
	
}
