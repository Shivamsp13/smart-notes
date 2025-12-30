package com.shivam.smartnotes.serviceimpl;

import com.shivam.smartnotes.entity.Chunk;
import com.shivam.smartnotes.entity.Notes;
import com.shivam.smartnotes.repository.ChunkRepository;
import com.shivam.smartnotes.service.ChunkService;
import com.shivam.smartnotes.service.EmbeddingService;
import com.shivam.smartnotes.utils.ChunkData;
import com.shivam.smartnotes.utils.ChunkingUtil;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ChunkServiceImpl implements ChunkService {
    private final ChunkRepository chunkRepository;
    private final EmbeddingService embeddingService;

    public ChunkServiceImpl(ChunkRepository chunkRepository,EmbeddingService embeddingService) {
        this.chunkRepository = chunkRepository;
        this.embeddingService=embeddingService;
    }

    @Override
    public void createChunksForNote(Notes notes){

        //if there is duplicate of it, it will be deleted,ensures no duplicacy.
        chunkRepository.deleteByNotes(notes);
        List<ChunkData> chunkDataList=
                ChunkingUtil.chunkText(notes.getContent());

        for (ChunkData data : chunkDataList) {

            String embedding =
                    embeddingService.generateEmbedding(data.getChunkText());

            String keywords =
                    String.join(",", data.getKeywords());

            Chunk chunk = new Chunk(
                    notes,
                    data.getChunkText(),
                    (long)data.getOrderIndex(),
                    keywords,
                    embedding
            );
            chunkRepository.save(chunk);
        }


    }

    @Override
    public void deleteChunksForNote(Notes notes) {
        chunkRepository.deleteByNotes(notes);

    }
}
