package server.dao;

import java.util.List;

import common.entity.Category;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;
import server.util.JPAUtil;

public class CategoryDAO {
	public boolean add(Category category) {
		EntityTransaction tr = null;
		
		try (EntityManager em = JPAUtil.getEntityManager()){
			tr = em.getTransaction();
			tr.begin();
			em.persist(category);
			tr.commit();
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
		
	}
	
	
	public boolean update(Category category) {
		EntityTransaction tr = null;
				
		try (EntityManager em = JPAUtil.getEntityManager()) {
			tr = em.getTransaction();
			tr.begin();
			em.merge(category);
			tr.commit();
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
		
	}
	
	public boolean delete(int id) {
		EntityTransaction tr = null;
		
		try (EntityManager em = JPAUtil.getEntityManager()) {
			tr = em.getTransaction();
			tr.begin();
			Category c = em.find(Category.class, id);
			
			if (c != null) {
				em.remove(c);
				tr.commit();
				return true;
			}
			
			return false;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}
	
	public Category findById(int id) {
		try (EntityManager em = JPAUtil.getEntityManager()) {
			return em.find(Category.class, id);
			
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
	
	public List<Category> findAll(){
		try (EntityManager em = JPAUtil.getEntityManager()){
			
			return em.createQuery("SELECT c FROM Category c", Category.class).getResultList();
			
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
}
