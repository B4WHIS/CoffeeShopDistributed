package server.dao;

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
			// TODO: handle exception
			if (tr != null && tr.isActive())
				tr.rollback();
			e.printStackTrace();
			return false;
		}
		
	}
}
