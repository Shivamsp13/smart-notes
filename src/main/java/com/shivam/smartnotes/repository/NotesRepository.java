package com.shivam.smartnotes.repository;

import com.shivam.smartnotes.entity.Notes;
import com.shivam.smartnotes.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface NotesRepository extends JpaRepository<Notes,Long> {
    List<Notes> findAllByOwner(User owner);
    Optional<Notes> findByIdAndOwner(Long notesId,User user);
    List<Notes> findByExpiredAtBefore(LocalDateTime now);
    Optional<Notes> findByNoteIdAndUser_UserId(Long noteId, Long userId);
}
