package com.beautybuddy.breakout.repo;

public interface BreakoutListProductRepository extends org.springframework.data.jpa.repository.JpaRepository<com.beautybuddy.breakout.entity.BreakoutListProduct, Long> {
    java.util.Optional<com.beautybuddy.breakout.entity.BreakoutListProduct> findByBreakoutListIdAndProductId(Long breakoutListId, Long productId);
}
