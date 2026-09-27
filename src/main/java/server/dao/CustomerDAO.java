package server.dao;

import java.util.List;

import common.entity.Account;
import common.entity.Customer;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;
import jakarta.persistence.Query;
import server.util.JPAUtil;

public class CustomerDAO {
public boolean add(Customer c) {
		
		EntityTransaction tr = null;
		
		try (EntityManager em = JPAUtil.getEntityManager()) {
			tr = em.getTransaction();
			tr.begin();
			em.persist(c);
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
	
	public boolean update (Customer c) {
		EntityTransaction tr = null;
		
		try (EntityManager em = JPAUtil.getEntityManager()) {
			tr = em.getTransaction();
			tr.begin();
			em.merge(c);
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
	
	public boolean delete (int id) {
		
		EntityTransaction tr = null;
		try (EntityManager em = JPAUtil.getEntityManager()) {
			tr = em.getTransaction();
			tr.begin();
			Customer c = em.find(Customer.class, id);
			
			if(c != null) {
				em.remove(c);
				tr.commit();
				return true;
			}
			return false;
		} catch (Exception e) {
			// TODO: handle exception
			if (tr != null && tr.isActive())
				tr.rollback();
			e.printStackTrace();
			return false;
		}
	}
	
	public Customer findById(int id) {
		try (EntityManager em = JPAUtil.getEntityManager()) {
			return em.find(Customer.class, id);
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			return null;
		}
	}
	
	public List<Customer> findByAll(){
		try (EntityManager em = JPAUtil.getEntityManager()) {
			return em.createQuery("SELECT c FROM Customer c", Customer.class).getResultList();
		} catch (Exception e) {
			e.printStackTrace();
			return null;
			// TODO: handle exception
		}
	}
	
	//
public Customer findByPhoneNumber (String phoneNumber) {
		
		try (EntityManager em = JPAUtil.getEntityManager()) {
			Query query = em.createQuery("SELECT c FROM Customer c WHERE c.phoneNumber = :u", Customer.class);
			
			List list = query.setParameter("u", phoneNumber).getResultList();
			return list.isEmpty() ? null :  (Customer) list.get(0);
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			return null;
		}
		
	}
}
