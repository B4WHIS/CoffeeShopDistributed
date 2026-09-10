package server.util;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;

public class JPAUtil {
	private static final EntityManagerFactory EMF = Persistence.createEntityManagerFactory("CoffeeShopDistributed");

	private JPAUtil() {

	}
	
	public static EntityManager getEntityManager(){
		return EMF.createEntityManager();
	}
	
	public static void close() {
		if (EMF != null) {
			EMF.close();
		}	
	}
	
	public static void main(String[] args) {
		EntityManager em = JPAUtil.getEntityManager();
		System.out.println("Chay thanh cong");
		em.close();
		JPAUtil.close();
		
	}
	
}
