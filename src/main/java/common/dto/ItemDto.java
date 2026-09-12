package common.dto;

import java.io.Serializable;

public record ItemDto(
			int id, 
			String itemName,
			double price,
			boolean isAvailable,
			int categoryId,
			String categoryName
		) implements Serializable {
	
}
