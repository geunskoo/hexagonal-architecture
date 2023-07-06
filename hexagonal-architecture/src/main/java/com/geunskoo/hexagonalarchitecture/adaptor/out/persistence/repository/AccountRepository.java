package com.geunskoo.hexagonalarchitecture.adaptor.out.persistence.repository;

import com.geunskoo.hexagonalarchitecture.adaptor.out.persistence.entity.AccountJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AccountRepository extends JpaRepository<AccountJpaEntity, Long> {

}
