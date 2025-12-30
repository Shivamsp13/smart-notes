package com.shivam.smartnotes.service;


import com.shivam.smartnotes.dto.NoteCreateRequest;
import com.shivam.smartnotes.dto.NoteResponse;

import java.util.List;

public interface NotesService {

    NoteResponse createNote(Long userid, NoteCreateRequest request);
    NoteResponse getNoteById(Long userId,Long noteId);

    List<NoteResponse>  getAllNoteForUser(Long userid);
    void deleteNote(Long userId, Long noteId);
    void deleteExpiredNotes();
}
