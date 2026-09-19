package com.placement.placementmanagement.repository;

import com.placement.placementmanagement.entity.Job;
import org.springframework.data.jpa.repository.JpaRepository;

public interface JobRepository extends JpaRepository<Job, Long> {

}