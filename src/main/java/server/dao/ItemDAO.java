package server.dao;

import java.util.List;

import common.entity.Item;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;
import server.util.JPAUtil;

public class ItemDAO {
public boolean add(Item item) {
		
		EntityTransaction tr = null;
		
		try (EntityManager em = JPAUtil.getEntityManager()) {
			tr = em.getTransaction();
			tr.begin();
			em.persist(item);
			tr.commit();
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}
	
	public boolean update (Item item) {
		EntityTransaction tr = null;
		
		try (EntityManager em = JPAUtil.getEntityManager()) {
			tr = em.getTransaction();
			tr.begin();
			em.merge(item);
			tr.commit();
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}
	
	public boolean delete (int id) {
		
		EntityTransaction tr = null;
		try (EntityManager em = JPAUtil.getEntityManager()) {
			tr = em.getTransaction();
			tr.begin();
			 Item item = em.find(Item.class, id);
			
			if(item != null) {
				em.remove(item);
				tr.commit();
				return true;
			}
			return false;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}
	
	public Item findById(int id) {
		try (EntityManager em = JPAUtil.getEntityManager()) {
			return em.find(Item.class, id);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
	
	public List<Item> findAll(){
		try (EntityManager em = JPAUtil.getEntityManager()) {
			return em.createQuery("SELECT i FROM Item i", Item.class).getResultList();
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
	public List<Item> findByCategory(int categoryId){
		try (EntityManager em = JPAUtil.getEntityManager()) {
			String query = """
					SELECT i 
					FROM Item i 
					WHERE i.category.id = :cId
					""";
			return em.createQuery(query, Item.class)
					.setParameter("cId", categoryId)
					.getResultList();
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
	public List<Item> findByName(String keyword){
		try (EntityManager em = JPAUtil.getEntityManager()) {
			String query = """
					SELECT i 
					FROM Item i 
					WHERE i.itemName LIKE :kw
					""";
			return em.createQuery(query, Item.class)
					.setParameter("kw","%" + keyword + "%")
					.getResultList();
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
}
