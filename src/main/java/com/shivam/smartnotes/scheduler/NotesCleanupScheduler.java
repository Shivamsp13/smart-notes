package com.shivam.smartnotes.scheduler;

import com.shivam.smartnotes.service.NotesService;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class NotesCleanupScheduler {

    private final NotesService noteService;

    public NotesCleanupScheduler(NotesService noteService) {
        this.noteService = noteService;
    }

    @Scheduled(cron = "0 0 0 * * *")
    public void deleteExpiredNotes() {
        noteService.deleteExpiredNotes();
    }
}
