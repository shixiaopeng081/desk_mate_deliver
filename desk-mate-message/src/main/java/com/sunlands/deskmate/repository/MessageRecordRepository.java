package com.sunlands.deskmate.repository;

import com.sunlands.deskmate.domain.MessageRecordDO;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * @author shixiaopeng
 */
@Repository
public interface MessageRecordRepository extends JpaRepository<MessageRecordDO, Integer> {
}
