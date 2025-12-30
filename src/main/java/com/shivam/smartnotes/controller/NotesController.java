package com.shivam.smartnotes.controller;

import com.shivam.smartnotes.dto.NoteCreateRequest;
import com.shivam.smartnotes.dto.NoteResponse;
import com.shivam.smartnotes.service.NotesService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/notes")
public class NotesController {
    private final NotesService notesService;

    public NotesController(NotesService notesService) {
        this.notesService = notesService;
    }

    @PostMapping
    public ResponseEntity<NoteResponse> createNote(
            @RequestParam Long userId,
            @RequestBody NoteCreateRequest request
    ){
        NoteResponse response=notesService.createNote(userId,request);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);

    }

    @GetMapping
    public ResponseEntity<List<NoteResponse>> getNotesByUser(
            @RequestParam Long userId
    ){
        List<NoteResponse> notes=
                notesService.getAllNoteForUser(userId);

        return ResponseEntity
                .ok(notes);
    }

    @GetMapping("/{noteId}")
    public ResponseEntity<NoteResponse> getNoteById(
            @RequestParam Long userId,
            @PathVariable Long noteId
    ){
        NoteResponse response=notesService.getNoteById(userId,noteId);

        return ResponseEntity
                .ok(response);
    }
    @DeleteMapping("/{noteId}")
    public ResponseEntity<Void> deleteNote(
            @RequestParam Long userId,
            @PathVariable Long noteId
    ) {
        notesService.deleteNote(userId, noteId);
        return ResponseEntity.noContent().build();
    }

}
