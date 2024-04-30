package com.muralis.minhasfinancas.model.repository;

import com.muralis.minhasfinancas.model.enums.StatusEntry;
import com.muralis.minhasfinancas.model.enums.TypeEntry;
import org.springframework.data.jpa.repository.JpaRepository;

import com.muralis.minhasfinancas.model.entity.Entry;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.util.List;

public interface EntryRepository extends JpaRepository<Entry, Long>{

    @Query(value = "SELECT SUM(l.value) FROM Entry l JOIN l.user u WHERE u.id = :idUser AND l.type = :type AND l.status = :status GROUP BY u")
    BigDecimal getBalanceByIdAndType(@Param("idUser") Long idUser, @Param("type") TypeEntry type, @Param("status") StatusEntry status);

    @Query(value = "SELECT SUM(l.value) FROM Entry l JOIN l.user u WHERE u.id = :idUser AND l.type = :type AND l.status = :status AND l.month = :month GROUP BY u")
    BigDecimal getBalanceByIdAndTypeAndMonth(@Param("idUser") Long idUser, @Param("type") TypeEntry type, @Param("status") StatusEntry status, @Param("month") Number month);

    @Query(value = "SELECT e.* FROM entry e " +
            "LEFT JOIN categories_entries ce ON e.id = ce.entry_id " +
            "LEFT JOIN category c ON ce.category_id = c.id " +
            "WHERE CAST(e.id_user AS VARCHAR) LIKE :idUser " +
            "AND CAST(ce.category_id AS VARCHAR) LIKE :categoryId " +
            "AND e.description LIKE CONCAT('%', :description, '%') " +
            "AND CAST(e.month AS VARCHAR) LIKE :month " +
            "AND CAST(e.year AS VARCHAR) LIKE :year " +
            "AND e.type LIKE CONCAT('%', :type, '%') " +
            "AND e.status LIKE CONCAT('%', :status, '%') " +
            "AND CAST(e.id AS VARCHAR) LIKE :id", nativeQuery = true)
    List<Entry> findByFilters(
            @Param("id") String id,
            @Param("description") String description,
            @Param("month") String month,
            @Param("year") String year,
            @Param("categoryId") String categoryId,
            @Param("type") String type,
            @Param("status") String status,
            @Param("idUser") String idUser
    );

    @Query(value = "SELECT * FROM entry e " +
            "WHERE CAST(e.id_user AS VARCHAR) LIKE :idUser " +
            "AND e.description LIKE CONCAT('%', :description, '%') " +
            "AND CAST(e.month AS VARCHAR) LIKE :month " +
            "AND CAST(e.year AS VARCHAR) LIKE :year " +
            "AND e.type LIKE CONCAT('%', :type, '%') " +
            "AND e.status LIKE CONCAT('%', :status, '%') " +
            "AND CAST(e.id AS VARCHAR) LIKE :id", nativeQuery = true)
    List<Entry> findAllNoCategoryFilter(
            @Param("id") String id,
            @Param("description") String description,
            @Param("month") String month,
            @Param("year") String year,
            @Param("type") String type,
            @Param("status") String status,
            @Param("idUser") String idUser
    );
}
