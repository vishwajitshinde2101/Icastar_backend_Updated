package com.icastar.platform.repository;

import com.icastar.platform.entity.RecruiterCategoryField;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RecruiterCategoryFieldRepository extends JpaRepository<RecruiterCategoryField, Long> {
    
    List<RecruiterCategoryField> findByRecruiterCategoryIdAndIsActiveTrueOrderBySortOrder(Long recruiterCategoryId);
    
    @Query("SELECT rcf FROM RecruiterCategoryField rcf WHERE rcf.recruiterCategory.id = :categoryId AND rcf.isActive = true ORDER BY rcf.sortOrder")
    List<RecruiterCategoryField> findActiveFieldsByCategoryId(@Param("categoryId") Long categoryId);
}
