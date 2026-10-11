package server.mapper;

import common.dto.CategoryDTO;
import common.entity.Category;

public interface CategoryMapper {
	public default CategoryDTO toDto(Category c) {
		if(c == null)
			return null;
		return new CategoryDTO(c.getId(), c.getName());
	}
	public default Category toEntity(CategoryDTO cDto) {
		if(cDto == null)
			return null;
		return new Category(cDto.id(), cDto.name());
	}
	
	
}
