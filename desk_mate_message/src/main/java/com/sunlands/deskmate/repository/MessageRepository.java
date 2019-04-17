package com.sunlands.deskmate.repository;

import com.sun.org.apache.xpath.internal.operations.Bool;
import com.sunlands.deskmate.domain.MessageDO;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @author shixiaopeng
 */
@Repository
public interface MessageRepository extends JpaRepository<MessageDO, Integer> {

    MessageDO findAllByUserIdAndBusinessIdAndType(Integer userId, Integer businessId, String type);

    List<MessageDO> findAllByUserIdAndIsReadOrderByMessageDateTimeDesc(Integer userId, Boolean isRead);
}
