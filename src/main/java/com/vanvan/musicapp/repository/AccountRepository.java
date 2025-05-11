package com.vanvan.musicapp.repository;

import com.vanvan.musicapp.entity.Account;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AccountRepository extends JpaRepository<Account, Integer> {
    @Query(value = "select A FROM Account A where A.roleID = 'EMPLOYEE'")
    List<Account> findAllEmployeeAccount();

    Optional<Account> findByEmail(String email);

    @Query(value = "select A FROM Account A where A.customer.customerID = :customerId")
    List<Account> findAccountByCustomerID(@Param("customerId") Integer customerId);

}
