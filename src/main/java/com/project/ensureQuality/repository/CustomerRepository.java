package com.project.ensureQuality.repository;

import com.project.ensureQuality.model.Customer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CustomerRepository extends JpaRepository<Customer, Integer> {

    Boolean existsByPhoneNumber(String phoneNumber);

    @Query("select c from Customer c order by c.id asc limit ?2 offset ?1")
    List<Customer> getCustomersWithPagination(int offset, int limit);

    @Query("select c from Customer c where c.username like %:q% or c.phoneNumber like %:q%")
    List<Customer> getListCusByValueSearch(String q);
}
