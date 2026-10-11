package server.mapper;

import common.dto.ItemDTO;
import common.entity.Category;
import common.entity.Item;

public interface ItemMapper {
	public default ItemDTO toDto(Item i) {
		if(i == null)
			return null;
		return new ItemDTO(i.getId(), i.getItemName(), i.getPrice(), 
				i.isAvailable(), 
				i.getCategory() != null ? i.getCategory().getId() : 0, 
				i.getCategory() != null ? i.getCategory().getName(): "");
	}
	
	public default Item toEntity(ItemDTO iDto, Category category) {
		if(iDto == null)
			return null;
		return new Item(iDto.id(), iDto.itemName(), iDto.price(), iDto.isAvailable(), category);
	}
}
