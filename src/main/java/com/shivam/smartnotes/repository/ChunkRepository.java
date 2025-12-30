package com.shivam.smartnotes.repository;

import com.shivam.smartnotes.entity.Chunk;
import com.shivam.smartnotes.entity.Notes;
import com.shivam.smartnotes.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface ChunkRepository extends JpaRepository<Chunk,Long> {
    List<Chunk> findByNotesOrderByOrderIndexAsc(Notes notes);

    List<Chunk> findByNotesAndNoteOwner(Notes notes, User owner);

    void deleteByNotes(Notes notes);

    List<Chunk> findByUserIdAndKeywords(Long userId, String topic);

    List<Chunk> findByNotes_User_UserId(Long userId);
}
