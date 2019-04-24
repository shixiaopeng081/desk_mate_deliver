package com.sunlands.deskmate.repository;

import com.sunlands.deskmate.domain.MessageRecordDO;
import com.sunlands.deskmate.domain.MessageSystemDO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * @author shixiaopeng
 */
@Repository
public interface MessageSystemRepository extends JpaRepository<MessageSystemDO, Integer> {


    Page<MessageSystemDO> findAllByUserIdOrderByCreateDateTimeDesc(Integer userId, Pageable pageable);
}
