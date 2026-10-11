
package com.placement.placementmanagement.repository;

import com.placement.placementmanagement.entity.Job;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface JobRepository extends JpaRepository<Job, Long> {

    List<Job> findByTitleContainingIgnoreCaseOrCompanyContainingIgnoreCase(
            String title,
            String company
    );

    List<Job> findByMinimumCgpaLessThanEqualAndEligibleBranchesContaining(
            Double cgpa,
            String branch
    );

}

