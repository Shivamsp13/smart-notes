package com.shivam.smartnotes.service;


import com.shivam.smartnotes.dto.NoteCreateRequest;
import com.shivam.smartnotes.dto.NoteResponse;

import java.util.List;

public interface NotesService {

    NoteResponse createNote(String username, NoteCreateRequest request);
    NoteResponse getNoteById(String username,Long noteId);

    List<NoteResponse>  getAllNotesForUser(String username);
    void deleteNote(String username, Long noteId);
    void deleteExpiredNotes();
}
