package com.shivam.smartnotes.repository;

import com.shivam.smartnotes.entity.MCQ;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MCQRepository extends JpaRepository<MCQ,Long> {
    List<MCQ> findByTopic(String topic);

    List<MCQ> findByTopicAndNotes_User_UserId(String topic, Long userId);
}
