package server.service;

import java.util.List;

import common.dto.CategoryDTO;

public interface CategoryService {
	List<CategoryDTO> getAll();	
	boolean add(CategoryDTO dto);
	boolean update(CategoryDTO dto);
	boolean delete(int categoryId);
}
