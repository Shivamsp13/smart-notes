package com.shivam.smartnotes.service;

import com.shivam.smartnotes.entity.Notes;

public interface ChunkService {

    void createChunksForNote(Notes notes);
    void deleteChunksForNote(Notes notes);

}
