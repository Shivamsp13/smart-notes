<h1 align="center">📘 Smart Notes</h1>
<p align="center">
  <b>AI-Powered PDF Intelligence Backend</b><br>
  <i>Production-inspired Spring Boot project with RAG, OCR, and MCQ generation</i>
</p>

---

<h2>🚀 What is Smart Notes?</h2>

**Smart Notes** is a **backend-first, production-grade Spring Boot application** that allows users to upload PDFs, extract text (with OCR fallback), and interact with their documents using **Retrieval-Augmented Generation (RAG)** and **MCQ generation** powered by modern LLMs.

This project is intentionally designed to demonstrate **real backend engineering**, **clean architecture**, and **correct AI integration** — not just CRUD APIs or tutorials.

---

<h2>✨ Key Features</h2>

### 🔐 Authentication & Security
- Stateless **JWT-based authentication**
- Password hashing using **BCrypt**
- Strict ownership enforcement (users can only access their own data)
- No sessions, no cookies
- Proper `401 / 403` error handling

### 📄 PDF Upload & Text Extraction
- Upload PDFs via REST APIs
- Text extraction using **Apache PDFBox**
- **Per-page OCR fallback** using Tesseract (only when PDF text is unreadable)
- Garbage text detection
- PDFs are **never stored on disk**

### 🧩 Chunking & Embeddings
- Deterministic, sentence-based chunking
- Keyword extraction
- Vector embeddings using **intfloat/e5-base-v2**
- Cosine similarity for semantic ranking

### ❓ Question Answering (RAG)
- Retrieval-Augmented Generation using user notes
- Uses only **user-owned data**
- Supports:
  - **Specific note selection**
  - **All notes**
- Hallucination-safe fallback when answer is not present

### 📝 MCQ Generation
- Topic-based MCQ generation using LLMs
- Context retrieved via semantic search
- Strict JSON parsing and validation
- MCQs persisted with note & chunk linkage

### 🗑️ Automatic Cleanup
- Scheduled deletion of expired notes
- Cascading deletes for chunks and MCQs
- No orphaned data

---

<h2>🏗️ Architecture</h2>

```
Controller
   ↓
Service (interface)
   ↓
ServiceImpl (business logic)
   ↓
Repository (JPA)
   ↓
PostgreSQL
```

### Architectural Rules
- Controllers contain **zero business logic**
- DTOs are strictly separated from entities
- Ownership checks happen **only in service layer**
- External AI calls isolated in infra layer
- No circular dependencies

---

<h2>🧠 Tech Stack</h2>

### Backend
- Java 17
- Spring Boot 3.x
- Spring Web
- Spring Security
- Spring Data JPA
- Hibernate
- PostgreSQL
- Maven
- Lombok

### AI & External Services
- **OCR Service**: FastAPI + Tesseract + OpenCV
- **Embedding Service**: FastAPI + sentence-transformers
- **Embedding Model**: `intfloat/e5-base-v2`
- **LLM Provider**: Groq (OpenAI-compatible)
- **LLM Model**: `llama3-8b-8192`

### Infrastructure
- Docker (PostgreSQL)
- Python virtualenvs for AI services

---

<h2>🗄️ Data Model</h2>

### Entities
- **User**
- **Note**
- **Chunk**
- **MCQ**

### Relationships
- User → Notes (One-to-Many)
- Note → Chunks (One-to-Many)
- Chunk → MCQs (One-to-Many)

### Cascading Behavior
- Deleting a note deletes its chunks
- Deleting a chunk deletes its MCQs automatically

---

<h2>🔐 Authentication Flow</h2>

1. User registers → password hashed with BCrypt
2. User logs in → credentials validated
3. JWT generated and returned
4. Client sends `Authorization: Bearer <token>`
5. JWT filter validates token and sets `SecurityContext`
6. User identity is derived from context (never from request)

---

<h2>📥 API Overview</h2>

### Auth
- `POST /auth/login`

### Users
- `POST /users/register`
- `GET /users/username/{username}`

### Notes
- `POST /notes` — Upload PDF
- `GET /notes`
- `GET /notes/{noteId}`
- `DELETE /notes/{noteId}`

### Question Answering
- `POST /questions/ask`
  - `noteId = null` → All notes
  - `noteId = value` → Specific note

### MCQs
- `POST /mcqs/generatemcq`
- `GET /mcqs`
- `POST /mcqs/submit`

---

<h2>🧪 OCR & Embeddings</h2>

### OCR
- Per-page OCR fallback
- Triggered only when PDF text is unreadable
- Images processed in memory only

### Embeddings
- Generated via external Python service
- Stored as normalized vectors
- Used for semantic similarity search

---

<h2>🐳 Running the Project</h2>

### Prerequisites
- Java 17
- Docker
- Python 3.10+

### Steps
1. Start PostgreSQL Docker container
2. Start OCR service
3. Start Embedding service
4. Run Spring Boot application

Hibernate automatically manages schema creation.

---

<h2>📌 Design Decisions</h2>

- **Stateless JWT** → scalable & cloud-ready
- **External AI services** → isolation & replaceability
- **Deterministic chunking** → reproducible results
- **PostgreSQL** → production parity
- **Minimal frontend** → backend-focused project

---

<h2>✅ Project Status</h2>

✔ Complete  
✔ Stable  
✔ Resume-ready  
✔ Interview-defensible  
✔ Production-inspired  

Further work would be enhancements, not requirements.

---

<h2>👨‍💻 Author</h2>

**Shivam Pandey**

This project was built to demonstrate **real backend engineering and AI integration**

---

