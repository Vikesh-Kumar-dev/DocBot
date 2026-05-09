"""
DocBot AI Service — FastAPI entry point.

This service receives symptom messages from the Spring Boot backend,
processes them through Google Gemini, and returns structured assessment
responses. It runs on port 8000 inside Docker alongside the backend.
"""

import logging
import os

from dotenv import load_dotenv
from fastapi import FastAPI
from fastapi.middleware.cors import CORSMiddleware

from routers.assess import router as assess_router

# Load .env file for local development (ignored in production/Docker
# where env vars are set via docker-compose or secrets manager)
load_dotenv()

# ---------------------------------------------------------------------------
# Logging configuration
# ---------------------------------------------------------------------------
logging.basicConfig(
    level=logging.INFO,
    format="%(asctime)s | %(levelname)-8s | %(name)s | %(message)s",
    datefmt="%Y-%m-%d %H:%M:%S",
)
logger = logging.getLogger(__name__)

# ---------------------------------------------------------------------------
# FastAPI app
# ---------------------------------------------------------------------------
app = FastAPI(
    title="DocBot AI Service",
    description="Gemini-powered symptom assessment API for DocBot",
    version="1.0.0",
)

# ---------------------------------------------------------------------------
# CORS — accept requests from Spring Boot (and the React frontend in dev)
# ---------------------------------------------------------------------------
allowed_origins = os.getenv(
    "CORS_ORIGINS",
    "http://localhost:8080,http://backend:8080,http://localhost:5173",
).split(",")

app.add_middleware(
    CORSMiddleware,
    allow_origins=allowed_origins,
    allow_credentials=True,
    allow_methods=["*"],
    allow_headers=["*"],
)

# ---------------------------------------------------------------------------
# Routes
# ---------------------------------------------------------------------------
app.include_router(assess_router)


@app.get("/health")
async def health_check():
    """Simple health endpoint for Docker health checks and readiness probes."""
    return {"status": "ok", "service": "docbot-ai"}


# ---------------------------------------------------------------------------
# Startup log
# ---------------------------------------------------------------------------
@app.on_event("startup")
async def on_startup():
    gemini_key_set = bool(os.getenv("GEMINI_API_KEY"))
    model_name = os.getenv("GEMINI_MODEL", "gemini-2.0-flash")
    logger.info("╔══════════════════════════════════════════╗")
    logger.info("║       DocBot AI Service starting         ║")
    logger.info("╠══════════════════════════════════════════╣")
    logger.info("║  GEMINI_API_KEY set: %-20s ║", "✓ Yes" if gemini_key_set else "✗ NO")
    logger.info("║  Model:             %-20s ║", model_name)
    logger.info("╚══════════════════════════════════════════╝")
    if not gemini_key_set:
        logger.warning(
            "GEMINI_API_KEY is not set! The /assess endpoint will return "
            "fallback error responses until a valid key is provided."
        )
