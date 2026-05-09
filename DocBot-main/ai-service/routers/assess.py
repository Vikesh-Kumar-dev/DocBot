"""
Assessment router — the single endpoint that Spring Boot calls.

POST /assess
  Request:  { "message": "...", "history": [...] }
  Response: { "type", "message", "recommendedSpecialty", "isEmergency", "assessmentReady" }
"""

import logging

from fastapi import APIRouter

from models.schemas import AssessRequest, AssessResponse
from services.emergency_rules import is_emergency, get_emergency_message
from services.gemini_service import assess_symptoms

logger = logging.getLogger(__name__)

router = APIRouter()


@router.post("/assess", response_model=AssessResponse)
async def assess(request: AssessRequest) -> AssessResponse:
    """
    Main assessment endpoint called by Spring Boot's AiAssessmentService.

    Flow:
    1. Run the secondary emergency keyword check on the raw message.
       (Spring Boot already runs its own check, but this is a safety net.)
    2. If no emergency keywords → forward to Gemini via gemini_service.
    3. Return the structured response.
    """

    # ── Step 1: Secondary emergency check ──────────────────────────────
    if is_emergency(request.message):
        logger.warning("EMERGENCY detected (Python-side) in message: %s", request.message)
        return AssessResponse(
            type="EMERGENCY",
            message=get_emergency_message(),
            recommendedSpecialty=None,
            isEmergency=True,
            assessmentReady=False,
        )

    # ── Step 2: Call Gemini ─────────────────────────────────────────────
    logger.info(
        "Processing assessment — message length=%d, history size=%d",
        len(request.message),
        len(request.history),
    )

    result = assess_symptoms(request.message, request.history)

    # ── Step 3: Return structured response ─────────────────────────────
    return AssessResponse(**result)
