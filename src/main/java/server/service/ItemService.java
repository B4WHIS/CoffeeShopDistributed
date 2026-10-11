package server.service;

import java.util.List;

import common.dto.ItemDTO;

public interface ItemService {
	List<ItemDTO> getAll();
	List<ItemDTO> getByCategory(int categoryId);
	List<ItemDTO> searchByName(String keyword);
	ItemDTO getById(int id);
	
	boolean add(ItemDTO dto);
	boolean update(ItemDTO dto);
	boolean delete(int id);
}
