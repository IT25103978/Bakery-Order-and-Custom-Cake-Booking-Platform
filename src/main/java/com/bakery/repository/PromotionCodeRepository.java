package com.bakery.repository;

import com.bakery.entity.PromotionCode;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PromotionCodeRepository extends JpaRepository<PromotionCode, Long>{
    Optional<PromotionCode> findByPromoCode(String promoCode);
}
