package server.dao;

import java.util.List;

import common.entity.Invoice;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;
import server.util.JPAUtil;

public class InvoiceDAO {
	public boolean add(Invoice in) {
		
		EntityTransaction tr = null;
		
		try (EntityManager em = JPAUtil.getEntityManager()) {
			tr = em.getTransaction();
			tr.begin();
			em.persist(in);
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
	public Invoice findById(int id) {
		try (EntityManager em = JPAUtil.getEntityManager()) {
			return em.find(Invoice.class, id);
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			return null;
		}
	}
	public List<Invoice> findByAll(){
		try (EntityManager em = JPAUtil.getEntityManager()) {
			return em.createQuery("SELECT i FROM Invoice i", Invoice.class).getResultList();
		} catch (Exception e) {
			e.printStackTrace();
			return null;
			// TODO: handle exception
		}
	}
	
	public double getDailyRevenue(String date) {
		try (EntityManager em = JPAUtil.getEntityManager()) {
			String query ="""
					SELECT SUM(i.totalAmount)
					FROM Invoice i
					WHERE i.createdAt LIKE :dt
					""";
			Double result = em.createQuery(query, Double.class)
			.setParameter("dt", date + "%")
			.getSingleResult();
			if(result != null)  return result;
				return 0.0;
		} catch (Exception e) {
			e.printStackTrace();
			return 0.0;
			// TODO: handle exception
		}
	}
	public double getMonthlyRevenue(int month, int year) {
		
		String pattern = String.format("%04d-%02d%%", year, month);
		
		try (EntityManager em = JPAUtil.getEntityManager()) {
			String query ="""
					SELECT SUM(i.totalAmount)
					FROM Invoice i
					WHERE i.createdAt LIKE :dt
					""";
			 Double result = em.createQuery(query, Double.class)
					.setParameter("dt", pattern)
					.getSingleResult();
					
			if(result != null) return result;
				return 0.0;
			
		} catch (Exception e) {
			e.printStackTrace();
			return 0.0;
			// TODO: handle exception
		}
	}
}
