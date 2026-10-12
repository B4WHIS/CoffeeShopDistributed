package server.serviceimpl;

import java.util.List;

import common.dto.ItemDTO;
import common.entity.Category;
import common.entity.Item;
import server.dao.CategoryDAO;
import server.dao.ItemDAO;
import server.mapper.ItemMapper;
import server.service.ItemService;

public class ItemServiceImpl implements ItemService, ItemMapper{
	private final ItemDAO itemDao = new ItemDAO();
	private final CategoryDAO categoryDao = new CategoryDAO();
	
	@Override
	public List<ItemDTO> getAll() {
		return itemDao.findAll().stream().map(item -> toDto(item)).toList();
	}

	@Override
	public List<ItemDTO> getByCategory(int categoryId) {
		return itemDao.findByCategory(categoryId).stream().map(item-> toDto(item)).toList();
	}

	@Override
	public List<ItemDTO> searchByName(String keyword) {
		return itemDao.findByName(keyword).stream().map(item-> toDto(item)).toList();
	}

	@Override
	public ItemDTO getById(int id) {
		Item item = itemDao.findById(id);
		return toDto(item);
	}

	@Override
	public boolean add(ItemDTO dto) {
		Category cate = categoryDao.findById(dto.categoryId());
		if (cate == null) return false;
		Item item = toEntity(dto, cate);
		item.setId(0);
		return itemDao.add(item);
		
	}

	@Override
	public boolean update(ItemDTO dto) {
		Category cate = categoryDao.findById(dto.categoryId());
		if(cate == null) return false;
		Item item = toEntity(dto, cate);
		return itemDao.update(item);
	}

	@Override
	public boolean delete(int id) {
		return itemDao.delete(id);
	}

}
