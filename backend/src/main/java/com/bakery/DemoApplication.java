package com.bakery;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class DemoApplication {

	public static void main(String[] args) {
		SpringApplication.run(DemoApplication.class, args);

		System.out.println("=================================");
		System.out.println("Bakery Project is running!");
		System.out.println("Home Page: http://localhost:8080");
		System.out.println("Index Page: http://localhost:8080/index.html");
		System.out.println("Checkout API: http://localhost:8080/api/checkout");
		System.out.println("=================================");
	}
}