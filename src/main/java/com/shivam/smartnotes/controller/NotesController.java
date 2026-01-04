package com.shivam.smartnotes.controller;

import com.shivam.smartnotes.dto.NoteCreateRequest;
import com.shivam.smartnotes.dto.NoteResponse;
import com.shivam.smartnotes.service.NotesService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import com.shivam.smartnotes.service.TextExtractionService;


import java.time.LocalDateTime;
import java.util.List;

//Request comes in
// → Validate JWT
// → Accept PDF file
// → Call TextExtractionService
// → Get plain text
// → Call NoteService with String
// → Return response

//User uploads PDF
// → Auth (JWT)
// → Controller
// → TextExtractionService
// → Validation
// → NoteService
//   → ChunkingUtil
//   → ChunkService
// → Storage




@RestController
@RequestMapping("/notes")
public class NotesController {
    private final NotesService notesService;
    private final TextExtractionService textExtractionService;

    public NotesController(NotesService notesService,TextExtractionService textExtractionService) {
        this.notesService = notesService;
        this.textExtractionService=textExtractionService;
    }

    @PostMapping(consumes = "multipart/form-data")
    public ResponseEntity<NoteResponse> createNoteFromPDF(
            @RequestParam Long userId,
            @RequestParam("file") MultipartFile pdfFile,
            @RequestParam String title
    ){
        String extractedText = textExtractionService.extractText(pdfFile);

        NoteCreateRequest request = new NoteCreateRequest();
        request.setTitle(title);
        request.setContent(extractedText);

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
