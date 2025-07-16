package com.mydigibank.my_digibank.repository;

import com.mydigibank.my_digibank.model.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;

public interface TransactionRepository extends JpaRepository<Transaction, String> {

    // ✅ Basic transaction history by account
    List<Transaction> findByAccountId(String accountId);

    // ✅ Advanced filtering query
    @Query("SELECT t FROM Transaction t WHERE " +
            "(:accountId IS NULL OR t.accountId = :accountId) AND " +
            "(:type IS NULL OR LOWER(t.transactionType) = LOWER(:type)) AND " +
            "(:fromDate IS NULL OR t.dateOfTransaction >= :fromDate) AND " +
            "(:toDate IS NULL OR t.dateOfTransaction <= :toDate) AND " +
            "(:minAmount IS NULL OR t.amount >= :minAmount) AND " +
            "(:maxAmount IS NULL OR t.amount <= :maxAmount)")
    List<Transaction> findByFilters(
            @Param("accountId") String accountId,
            @Param("type") String type,
            @Param("fromDate") LocalDate fromDate,
            @Param("toDate") LocalDate toDate,
            @Param("minAmount") Double minAmount,
            @Param("maxAmount") Double maxAmount
    );
}
