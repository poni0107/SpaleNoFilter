package com.instagram.like_service;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Glavna ulazna tačka Like mikroservisa.
 *
 * Ova klasa:
 * 1. Pokreće Spring Boot aplikaciju
 * 2. Aktivira auto-konfiguraciju
 * 3. Skenira sve komponente u paketu com.instagram.like_service
 * 4. Registruje:
 *    - @Entity klase (Like)
 *    - @Repository (LikeRepository)
 *    - @Service (LikeService)
 *    - @RestController (LikeController)
 * 5. Kreira ApplicationContext (IoC container)
 *
 * Bez ove klase mikroservis NE POSTOJI.
 */
@SpringBootApplication
public class LikeServiceApplication {

	public static void main(String[] args) {

		/**
		 * SpringApplication.run radi OGROMAN posao:
		 *
		 * 1. Kreira Spring ApplicationContext
		 * 2. Pokreće embedded server (Tomcat)
		 * 3. Učitava application.yml / properties
		 * 4. Povezuje bazu (JPA/Hibernate)
		 * 5. Registruje sve bean-ove
		 * 6. Pokreće dependency injection
		 * 7. Mapira REST endpoint-e
		 * 8. Startuje mikroservis na portu (npr. 8083)
		 */
		SpringApplication.run(LikeServiceApplication.class, args);
	}
}

