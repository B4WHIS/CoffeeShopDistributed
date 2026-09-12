package server.mapper;

import common.dto.OrderDetailDto;
import common.entity.Invoice;
import common.entity.Item;
import common.entity.OrderDetail;

public interface OrderDetailMapper {
	public default OrderDetailDto toDto(OrderDetail od) {
		if(od == null)
			return null;
		return new OrderDetailDto(od.getId(), 
				od.getItem() != null ? od.getItem().getId() : 0, 
				od.getItemName(), 
				od.getUnitPrice(), 
				od.getQuantity(),
				od.getSubTotal()
				);
	}
	
	public default OrderDetail toEntity(OrderDetailDto odDto, Invoice invoice, Item item) {
		if(odDto == null)
			return null;
		return new OrderDetail(odDto.id(), 
				odDto.itemName(),
				odDto.unitPrice(), 
				odDto.quantity(),
				odDto.subTotal(), 
				invoice, 
				item);
	}
}
