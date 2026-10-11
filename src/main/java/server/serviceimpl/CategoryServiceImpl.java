package server.serviceimpl;

import java.util.List;

import common.dto.CategoryDTO;
import common.entity.Category;
import server.dao.CategoryDAO;
import server.mapper.CategoryMapper;
import server.service.CategoryService;

public class CategoryServiceImpl implements CategoryService, CategoryMapper{
	
	private final CategoryDAO categoryDao = new CategoryDAO();
	
	@Override
	public List<CategoryDTO> getAll() {
		return categoryDao.findAll().stream().map(item -> toDto(item)).toList();
	}

	@Override
	public boolean add(CategoryDTO dto) {
		return categoryDao.add(new Category(0, dto.name()));
	}

	@Override
	public boolean update(CategoryDTO dto) {
		return categoryDao.update(toEntity(dto));
	}

	@Override
	public boolean delete(int categoryId) {
		return categoryDao.delete(categoryId);
	}
	
}
