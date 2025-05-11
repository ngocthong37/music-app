package com.quocluan.kdmaylanh.repository;

import com.quocluan.kdmaylanh.entity.Invoice;
import com.quocluan.kdmaylanh.model.MonthlyRevenue;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Date;
import java.util.List;

@Repository
public interface InvoiceRepository extends JpaRepository<Invoice, Integer> {

    @Query("SELECT SUM(i.amount) FROM Invoice i WHERE DAY(i.transactionDate) = :day " +
            "AND MONTH(i.transactionDate) = :month AND YEAR(i.transactionDate) = :year")
    BigDecimal findRevenueByDate(@Param("day") int day, @Param("month") int month, @Param("year") int year);

    @Query("SELECT YEAR(i.transactionDate), MONTH(i.transactionDate), SUM(i.amount) " +
            "FROM Invoice i " +
            "WHERE i.transactionDate >= :startDate AND i.transactionDate <= :endDate " +
            "GROUP BY YEAR(i.transactionDate), MONTH(i.transactionDate) " +
            "ORDER BY YEAR(i.transactionDate), MONTH(i.transactionDate)")
    List<Object[]> findMonthlyRevenueBetweenDates(@Param("startDate") Date startDate, @Param("endDate") Date endDate);

}
