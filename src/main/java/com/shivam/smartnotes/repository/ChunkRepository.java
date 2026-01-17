package com.shivam.smartnotes.repository;

import com.shivam.smartnotes.entity.Chunk;
import com.shivam.smartnotes.entity.Notes;
import com.shivam.smartnotes.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
@Repository
public interface ChunkRepository extends JpaRepository<Chunk,Long> {
//    List<Chunk> findByNoteOrderByOrderIndexAsc(Notes note);

    void deleteByNote(Notes note);
    List<Chunk> findByNote_Owner(User owner);


    List<Chunk> findByNote_NoteIdAndNote_Owner(Long noteId, User user);
}
