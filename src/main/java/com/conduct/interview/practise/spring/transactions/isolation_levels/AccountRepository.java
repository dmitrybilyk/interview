package com.conduct.interview.practise.spring.transactions.isolation_levels;

import org.springframework.data.jpa.repository.JpaRepository;

public interface AccountRepository extends JpaRepository<Account, Long> {
}
