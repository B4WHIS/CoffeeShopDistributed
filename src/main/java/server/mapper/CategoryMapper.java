package server.mapper;

import common.dto.CategoryDto;
import common.entity.Category;

public interface CategoryMapper {
	public default CategoryDto toDto(Category c) {
		if(c == null)
			return null;
		return new CategoryDto(c.getId(), c.getName());
	}
	public default Category toEntity(CategoryDto cDto) {
		if(cDto == null)
			return null;
		return new Category(cDto.id(), cDto.name());
	}
	
	
}
