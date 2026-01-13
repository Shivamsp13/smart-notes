package com.shivam.smartnotes.controller;

import com.shivam.smartnotes.dto.NoteCreateRequest;
import com.shivam.smartnotes.dto.NoteResponse;
import com.shivam.smartnotes.security.SecurityUtil;
import com.shivam.smartnotes.service.NotesService;
import com.shivam.smartnotes.service.TextExtractionService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/notes")
public class NotesController {

    private final NotesService notesService;
    private final TextExtractionService textExtractionService;

    public NotesController(
            NotesService notesService,
            TextExtractionService textExtractionService
    ) {
        this.notesService = notesService;
        this.textExtractionService = textExtractionService;
    }

    @PostMapping(consumes = "multipart/form-data")
    public ResponseEntity<NoteResponse> createNoteFromPDF(
            @RequestParam("file") MultipartFile pdfFile,
            @RequestParam String title
    ) {
        String username = SecurityUtil.getCurrentUsername();

        String extractedText = textExtractionService.extractText(pdfFile);

        NoteCreateRequest request = new NoteCreateRequest();
        request.setTitle(title);
        request.setContent(extractedText);

        NoteResponse response =
                notesService.createNote(username, request);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }

    @GetMapping
    public ResponseEntity<List<NoteResponse>> getNotesByUser() {

        String username = SecurityUtil.getCurrentUsername();

        List<NoteResponse> notes =
                notesService.getAllNotesForUser(username);

        return ResponseEntity.ok(notes);
    }

    @GetMapping("/{noteId}")
    public ResponseEntity<NoteResponse> getNoteById(
            @PathVariable Long noteId
    ) {
        String username = SecurityUtil.getCurrentUsername();

        NoteResponse response =
                notesService.getNoteById(username, noteId);

        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{noteId}")
    public ResponseEntity<Void> deleteNote(
            @PathVariable Long noteId
    ) {
        String username = SecurityUtil.getCurrentUsername();

        notesService.deleteNote(username, noteId);

        return ResponseEntity.noContent().build();
    }
}
