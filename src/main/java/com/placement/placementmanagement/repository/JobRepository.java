
package com.placement.placementmanagement.repository;

import com.placement.placementmanagement.entity.Job;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface JobRepository extends JpaRepository<Job, Long> {

    List<Job> findByTitleContainingIgnoreCaseOrCompanyContainingIgnoreCase(
            String title,
            String company
    );

    @Query("""
            SELECT j FROM Job j
            WHERE j.minimumCgpa <= :cgpa
            AND CONCAT(',', REPLACE(LOWER(j.eligibleBranches), ' ', ''), ',')
                LIKE CONCAT('%,', LOWER(:branch), ',%')
            """)
    List<Job> filterEligibleJobs(
            @Param("cgpa") Double cgpa,
            @Param("branch") String branch
    );

    @Query("""
        SELECT j FROM Job j
        WHERE j.minimumCgpa <= :cgpa
        AND CONCAT(',', REPLACE(LOWER(j.eligibleBranches), ' ', ''), ',')
            LIKE CONCAT('%,', LOWER(:branch), ',%')
        AND (
            LOWER(j.title) LIKE LOWER(CONCAT('%', :keyword, '%'))
            OR LOWER(j.company) LIKE LOWER(CONCAT('%', :keyword, '%'))
        )
        """)
    List<Job> searchAndFilterJobs(
            @Param("keyword") String keyword,
            @Param("cgpa") Double cgpa,
            @Param("branch") String branch
    );

}
