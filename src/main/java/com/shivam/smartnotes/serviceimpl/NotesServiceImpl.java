package com.shivam.smartnotes.serviceimpl;

import com.shivam.smartnotes.dto.NoteCreateRequest;
import com.shivam.smartnotes.dto.NoteResponse;
import com.shivam.smartnotes.entity.Notes;
import com.shivam.smartnotes.entity.User;
import com.shivam.smartnotes.exceptions.AccessDeniedException;
import com.shivam.smartnotes.exceptions.ResourceNotFoundException;
import com.shivam.smartnotes.repository.NotesRepository;
import com.shivam.smartnotes.repository.UserRepository;
import com.shivam.smartnotes.service.ChunkService;
import com.shivam.smartnotes.service.NotesService;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class NotesServiceImpl implements NotesService {

    private final NotesRepository notesRepository;
    private final UserRepository userRepository;
    private final ChunkService chunkService;

    public NotesServiceImpl(
            NotesRepository notesRepository,
            UserRepository userRepository,
            ChunkService chunkService
    ) {
        this.notesRepository = notesRepository;
        this.userRepository = userRepository;
        this.chunkService = chunkService;
    }

    @Override
    public NoteResponse createNote(String username, NoteCreateRequest request) {
        User user = getUserByUsername(username);

        LocalDateTime expiry = LocalDateTime.now().plusDays(30);

        Notes notes = new Notes(
                user,
                request.getTitle(),
                request.getContent(),
                expiry,
                LocalDateTime.now()
        );

        Notes savedNote = notesRepository.save(notes);
        chunkService.createChunksForNote(savedNote);

        return mapToResponse(savedNote);
    }

    @Override
    public NoteResponse getNoteById(String username, Long noteId) {

        User user = getUserByUsername(username);

        Notes notes = notesRepository.findById(noteId)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Note not found with id: " + noteId
                        )
                );

        if (!notes.getOwner().equals(user)) {
            throw new AccessDeniedException("You do not own this note");
        }

        return mapToResponse(notes);
    }

    @Override
    public List<NoteResponse> getAllNotesForUser(String username) {

        User user = getUserByUsername(username);

        return notesRepository.findAllByOwner(user)
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    public void deleteNote(String username, Long noteId) {

        User user = getUserByUsername(username);

        Notes notes = notesRepository.findById(noteId)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Note not found with id: " + noteId
                        )
                );

        if (!notes.getOwner().equals(user)) {
            throw new AccessDeniedException("You do not own this note");
        }

        chunkService.deleteChunksForNote(notes);
        notesRepository.delete(notes);
    }

    @Override
    public void deleteExpiredNotes() {

        List<Notes> expiredNotes =
                notesRepository.findByExpiredAtBefore(LocalDateTime.now());

        for (Notes note : expiredNotes) {
            chunkService.deleteChunksForNote(note);
            notesRepository.delete(note);
        }
    }

    private User getUserByUsername(String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "User not found with username: " + username
                        )
                );
    }

    private NoteResponse mapToResponse(Notes notes) {
        return new NoteResponse(
                notes.getNoteId(),
                notes.getOwner().getUsername(),
                notes.getCreatedAt(),
                notes.getExpiredAt(),
                notes.getTitle()
        );
    }
}
