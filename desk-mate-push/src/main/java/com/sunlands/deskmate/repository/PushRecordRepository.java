package com.sunlands.deskmate.repository;

import com.sunlands.deskmate.domain.PushRecordDO;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * @author shixiaopeng
 */
@Repository
public interface PushRecordRepository extends JpaRepository<PushRecordDO, Integer> {

}
