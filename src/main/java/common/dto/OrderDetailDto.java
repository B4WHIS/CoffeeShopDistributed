package common.dto;

import java.io.Serializable;

public record OrderDetailDto(
			int id,
			int itemId, 
			String itemName,
			double unitPrice,
			int quantity,
			double subTotal
		) implements Serializable {


}
