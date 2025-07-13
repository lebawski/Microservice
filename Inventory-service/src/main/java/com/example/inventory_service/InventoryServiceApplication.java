package com.example.inventory_service;

import com.example.inventory_service.model.Inventory;
import com.example.inventory_service.repository.InventoryRepo;
import com.example.inventory_service.service.InventoryService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
@EnableDiscoveryClient
public class InventoryServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(InventoryServiceApplication.class, args);
	}
	@Bean
	public CommandLineRunner commandLineRunner(InventoryRepo inventoryRepo) {
		return args -> {
			Inventory inventory = new Inventory();
			inventory.setCode("BMW");
			inventory.setQuantity(100);
			inventoryRepo.save(inventory);

			Inventory inventory2 = new Inventory();
			inventory2.setCode("audi");
			inventory2.setQuantity(0);
			inventoryRepo.save(inventory2);
		};
	}
}
