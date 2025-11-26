package com.icastar.platform.repository;

import com.icastar.platform.entity.RecruiterProfileField;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RecruiterProfileFieldRepository extends JpaRepository<RecruiterProfileField, Long> {
    
    List<RecruiterProfileField> findByRecruiterProfileId(Long recruiterProfileId);
    
    @Query("SELECT rpf FROM RecruiterProfileField rpf WHERE rpf.recruiterProfile.id = :profileId")
    List<RecruiterProfileField> findByRecruiterProfileIdQuery(@Param("profileId") Long profileId);
    
    void deleteByRecruiterProfileId(Long recruiterProfileId);
}
