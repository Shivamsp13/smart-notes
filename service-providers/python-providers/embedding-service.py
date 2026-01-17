from sentence_transformers import SentenceTransformer
from fastapi import FastAPI
from pydantic import BaseModel

app = FastAPI()

model = SentenceTransformer("intfloat/e5-base-v2")


class EmbeddingRequest(BaseModel):
    texts: list[str]


@app.post("/embed")
def embed(req: EmbeddingRequest):
    embeddings = model.encode(
        req.texts,
        normalize_embeddings=True
    )
    return {
        "embeddings": embeddings.tolist()
    }

if __name__ == "__main__":
    import uvicorn
    uvicorn.run(app, host="0.0.0.0", port=8000)
