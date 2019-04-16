package com.sunlands.deskmate.repository;

import com.sunlands.deskmate.domain.MessageDO;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * @author shixiaopeng
 */
@Repository
public interface MessageRepository extends JpaRepository<MessageDO, Integer> {

    MessageDO findAllByUserIdAndBusinessIdAndType(Integer userId, Integer businessId, String type);
}
