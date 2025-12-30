package com.shivam.smartnotes.serviceimpl;

import com.shivam.smartnotes.dto.NoteCreateRequest;
import com.shivam.smartnotes.dto.NoteResponse;
import com.shivam.smartnotes.dto.UserResponse;
import com.shivam.smartnotes.entity.Notes;
import com.shivam.smartnotes.entity.User;
import com.shivam.smartnotes.repository.NotesRepository;
import com.shivam.smartnotes.service.ChunkService;
import com.shivam.smartnotes.service.NotesService;
import com.shivam.smartnotes.service.UserService;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class NotesServiceImpl implements NotesService {

    private final NotesRepository notesRepository;
    private final UserService userService;
    private final ChunkService chunkService;

    public NotesServiceImpl(
            NotesRepository notesRepository,
            UserService userService,
            ChunkService chunkService){

        this.notesRepository=notesRepository;
        this.chunkService=chunkService;
        this.userService=userService;
    }
    @Override
    public NoteResponse createNote(Long userId, NoteCreateRequest request) {
        User user=userService.getUserEntityById(userId);

        LocalDateTime expiry=LocalDateTime.now().plusDays(30);

        Notes notes =new Notes(
                user,
                request.getTitle(),
                request.getContent(),
                expiry,
                LocalDateTime.now()
        );


        Notes savedNote=notesRepository.save(notes);
        chunkService.createChunksForNote(savedNote);

        return mapToResponse(savedNote);
    }

    @Override
    public NoteResponse getNoteById(Long userId, Long noteId) {
        Notes notes = notesRepository
                .findByNoteIdAndUser_UserId(noteId, userId)
                .orElseThrow(() ->
                        new RuntimeException(
                                "Note not found or access denied for noteId: " + noteId
                        )
                );

        return mapToResponse(notes);
    }


    @Override
    public List<NoteResponse> getAllNoteForUser(Long userId) {
        User user=userService.getUserEntityById(userId);

        return notesRepository.findAllByOwner(user)
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    public void deleteNote(Long userId, Long noteId) {
        User user=userService.getUserEntityById(userId);

        Notes note=notesRepository.findByIdAndOwner(noteId,user)
                .orElseThrow(()->new RuntimeException("You don't own this note"));

        chunkService.deleteChunksForNote(note);
        notesRepository.delete(note);
    }

    @Override
    public void deleteExpiredNotes() {
        List<Notes> expiredNotes=notesRepository
                .findByExpiredAtBefore(LocalDateTime.now());

        for(Notes note:expiredNotes){
            chunkService.deleteChunksForNote(note);
            notesRepository.delete(note);
        }
    }

    public NoteResponse mapToResponse(Notes notes){
        return new NoteResponse(
                notes.getNoteId(),
                notes.getOwner().getUsername(),
                notes.getCreatedAt(),
                notes.getExpiredAt(),
                notes.getTitle()
        );
    }
}
