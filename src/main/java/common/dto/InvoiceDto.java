package common.dto;

import java.io.Serializable;
import java.util.List;

public record InvoiceDto(
			int id,
			int orderNumber,
			String orderType,
			String createdAt,
			double subTotalAmount,
			double discountAmount,
			double totalAmount,
			String paymentMethod,
			String status,
			int accountId,
			String cashierName,
			Integer customerId,
			String customerName,
			String customerPhone,
			List<OrderDetailDto> orderDetails
		) implements Serializable {

}
