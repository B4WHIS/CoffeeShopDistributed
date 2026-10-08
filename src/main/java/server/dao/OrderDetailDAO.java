package server.dao;

import java.util.List;

import common.entity.OrderDetail;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;
import server.util.JPAUtil;

public class OrderDetailDAO {
	public boolean add(OrderDetail orderDetail) {
		EntityTransaction tr = null;
		
		try (EntityManager em = JPAUtil.getEntityManager()){
			tr = em.getTransaction();
			tr.begin();
			em.persist(orderDetail);
			tr.commit();
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
		
	}
	public OrderDetail findById(int id) {
		try (EntityManager em = JPAUtil.getEntityManager()) {
			return em.find(OrderDetail.class, id);
			
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
	public List<OrderDetail> findByInvoiceId(int invoiceId) {
		
		
		try (EntityManager em = JPAUtil.getEntityManager()) {
			String query = """
					SELECT od
					FROM OrderDetail od
					WHERE od.invoice.id = :invoiceId
					""";
			return em.createQuery(query, OrderDetail.class)
						.setParameter("invoiceId", invoiceId)
						.getResultList()
						;
			
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
}
