package com.smart.bugrca.repository;

import com.smart.bugrca.model.Bug;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BugRepository extends JpaRepository<Bug, Long> {
}
