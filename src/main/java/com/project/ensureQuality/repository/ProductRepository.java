package com.project.ensureQuality.repository;

import com.project.ensureQuality.model.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductRepository extends JpaRepository<Product, Integer> {

    @Query("select p from Product p order by p.id asc limit ?2 offset ?1")
    List<Product> getProductsWithPagination(int offset, int limit);
}
