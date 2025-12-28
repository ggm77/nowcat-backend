package org.nowcat.nowcat.domain.admin.auth.repository;

import org.nowcat.nowcat.domain.admin.auth.entity.AdminLoginAttempt;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AdminLoginAttemptRepository extends JpaRepository<AdminLoginAttempt, Long> {
}
