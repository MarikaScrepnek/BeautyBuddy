package com.beautybuddy.product;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ProductRepository extends JpaRepository<Product, Long> {
    @Query(
        value = "SELECT p.* " +
                "FROM product p " +
                "JOIN brand b ON p.brand_id = b.brand_id " +
                "WHERE replace(unaccent(lower(p.name)), '''', '') LIKE replace(unaccent(lower(CONCAT('%', :query, '%'))), '''', '') " +
                "   OR replace(unaccent(lower(b.name)), '''', '') LIKE replace(unaccent(lower(CONCAT('%', :query, '%'))), '''', '')",
        nativeQuery = true
    )
    List<Product> searchByProductOrBrand(@Param("query") String query);
}