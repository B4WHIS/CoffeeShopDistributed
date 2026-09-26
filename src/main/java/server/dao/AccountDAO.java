package server.dao;

import java.util.List;

import common.entity.Account;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;
import jakarta.persistence.Query;
import server.util.JPAUtil;

public class AccountDAO {
	public boolean add(Account a) {
		
		EntityTransaction tr = null;
		
		try (EntityManager em = JPAUtil.getEntityManager()) {
			tr = em.getTransaction();
			tr.begin();
			em.persist(a);
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
	
	public boolean update (Account a) {
		EntityTransaction tr = null;
		
		try (EntityManager em = JPAUtil.getEntityManager()) {
			tr = em.getTransaction();
			tr.begin();
			em.merge(a);
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
			Account a = em.find(Account.class, id);
			
			if(a != null) {
				em.remove(a);
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
	
	public Account findById(int id) {
		try (EntityManager em = JPAUtil.getEntityManager()) {
			return em.find(Account.class, id);
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			return null;
		}
	}
	
	public List<Account> findByAll(){
		try (EntityManager em = JPAUtil.getEntityManager()) {
			return em.createQuery("SELECT a FROM Account a", Account.class).getResultList();
		} catch (Exception e) {
			e.printStackTrace();
			return null;
			// TODO: handle exception
		}
	}
	
	public Account findByUsername(String username) {
		
		try (EntityManager em = JPAUtil.getEntityManager()) {
			Query query = em.createQuery("SELECT a FROM Account a WHERE a.username = :u", Account.class);
			
			List list = query.setParameter("u", username).getResultList();
			return list.isEmpty() ? null : (Account) list.get(0);
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			return null;
		}
		
	}
	
	public static void main(String[] args) {
		Account a = new Account(0,"admin", "123456","NHTB", "ADMIN","0335445454", true );
		AccountDAO aD = new AccountDAO();
		aD.add(a);
		aD.findByUsername("admin");
		
		
	}
	
}
