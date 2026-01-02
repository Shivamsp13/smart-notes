package com.shivam.smartnotes.serviceimpl;

import com.shivam.smartnotes.dto.NoteCreateRequest;
import com.shivam.smartnotes.dto.NoteResponse;
import com.shivam.smartnotes.entity.Notes;
import com.shivam.smartnotes.entity.User;
import com.shivam.smartnotes.exceptions.AccessDeniedException;
import com.shivam.smartnotes.exceptions.ResourceNotFoundException;
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
        Notes notes=notesRepository.findById(noteId)
                .orElseThrow(()->
                        new ResourceNotFoundException("Note not found with id: " + noteId)
                        );

        if(!notes.getOwner().getUserId().equals(userId)){
            throw new
                    AccessDeniedException("You do not own this note");
        }
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

        Notes notes=notesRepository.findById(noteId)
                .orElseThrow(()->
                        new ResourceNotFoundException("Note not found with id: " + noteId)
                );

        if(!notes.getOwner().getUserId().equals(userId)){
            throw new
                    AccessDeniedException("You do not own this note");
        }

        chunkService.deleteChunksForNote(notes);
        notesRepository.delete(notes);
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
