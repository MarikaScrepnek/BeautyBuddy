package com.beautybuddy.report.repo;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.beautybuddy.product.entity.Product;
import com.beautybuddy.report.entity.ProductReport;
import com.beautybuddy.user.entity.User;

public interface ProductReportRepository extends JpaRepository<ProductReport, Long> {
    Optional<ProductReport> findByUserAndProduct(User user, Product product);
}
