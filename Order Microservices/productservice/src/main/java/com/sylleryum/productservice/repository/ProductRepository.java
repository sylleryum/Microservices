package com.sylleryum.productservice.repository;

import com.sylleryum.productservice.model.Product;
import org.springframework.data.repository.CrudRepository;

public interface ProductRepository extends CrudRepository<Product, Long> {
}
