package com.vedha.repository;

import com.vedha.entity.OrganizationEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrgRepository extends JpaRepository<OrganizationEntity, Long> {
}
