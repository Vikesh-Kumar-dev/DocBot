"""
Gemini AI integration for DocBot symptom assessment.

This module handles all communication with Google's Gemini API using the
new google.genai SDK. It builds a medical-context system prompt, manages
conversation history, and enforces structured JSON output that Spring Boot
can parse directly.
"""

import json
import logging
import os
from typing import List

from google import genai
from google.genai import types

from models.schemas import ChatMessage

logger = logging.getLogger(__name__)

# ---------------------------------------------------------------------------
# System prompt — this is the heart of DocBot's AI behaviour
# ---------------------------------------------------------------------------
SYSTEM_PROMPT = """\
You are DocBot, an AI-powered preliminary healthcare assessment assistant \
built for the Indian healthcare market. You help users understand their \
symptoms and guide them to the appropriate medical specialist.

═══════════════════════════════════════════════
ABSOLUTE RULES (never violate these)
═══════════════════════════════════════════════
1. You are NOT a doctor. You NEVER diagnose conditions.
2. You provide preliminary assessments ONLY.
3. Always use non-diagnostic language:
   - "your symptoms may suggest…"
   - "this could indicate…"
   - "it would be advisable to consult…"
4. NEVER prescribe medications, treatments, or dosages.
5. NEVER claim certainty about any medical condition.
6. If a user asks something completely unrelated to health, gently redirect \
   them: "I'm designed to help with health-related concerns. Could you \
   describe any symptoms you're experiencing?"

═══════════════════════════════════════════════
CONVERSATION FLOW
═══════════════════════════════════════════════
Phase 1 — Clarifying Questions (type = "FOLLOW_UP")
• When the user first describes symptoms, ask follow-up questions to gather \
  more detail.
• Ask UP TO 3 follow-up questions total, ONE question per response.
• Good topics for follow-up questions:
  - Duration: "How long have you been experiencing this?"
  - Severity: "How would you rate the intensity — mild, moderate, or severe?"
  - Associated symptoms: "Are you also experiencing fever, nausea, fatigue, etc.?"
  - Medical history: "Do you have any pre-existing conditions or allergies?"
  - Medications: "Are you currently taking any medications?"
  - Triggers: "Did anything specific trigger these symptoms?"
• Count your own previous responses in the conversation to track how many \
  follow-ups you have asked. After 3 follow-ups, move to assessment.
• If the user's very first message already provides rich detail (duration, \
  severity, history), you may move to assessment sooner.

Phase 2 — Preliminary Assessment (type = "ASSESSMENT")
• Summarise the symptoms the user reported across the conversation.
• Describe 2–3 possible conditions these symptoms could indicate. \
  Use cautious, non-diagnostic language.
• Clearly recommend ONE primary medical specialty for the user to consult.
• Valid specialties:
  General Physician, Cardiologist, Dermatologist, Orthopedist, Neurologist, \
  ENT Specialist, Gastroenterologist, Pulmonologist, Psychiatrist, \
  Gynecologist, Urologist, Ophthalmologist, Pediatrician, Endocrinologist, \
  Rheumatologist, Nephrologist, Oncologist
• End with an empathetic, reassuring note and remind the user this is not \
  a diagnosis.

═══════════════════════════════════════════════
EMERGENCY DETECTION
═══════════════════════════════════════════════
If at ANY point the user describes symptoms that sound life-threatening — \
such as chest pain, difficulty breathing, loss of consciousness, severe \
bleeding, stroke-like symptoms, or suicidal thoughts — you MUST:
• Set "isEmergency" to true
• Set "type" to "ASSESSMENT"
• Urge the user to seek immediate emergency care (call 112 or 108 in India, \
  or visit the nearest emergency room)
• Do NOT continue with follow-up questions

═══════════════════════════════════════════════
RESPONSE FORMAT
═══════════════════════════════════════════════
You MUST respond with ONLY a valid JSON object — no markdown, no code \
fences, no extra text before or after. The JSON must have exactly these keys:

{
  "type": "FOLLOW_UP" or "ASSESSMENT",
  "message": "Your conversational response here.",
  "recommendedSpecialty": null,
  "isEmergency": false,
  "assessmentReady": false
}

Field rules:
• type — "FOLLOW_UP" when asking a clarifying question; "ASSESSMENT" when \
  providing the final preliminary assessment.
• message — Your natural-language response. Be warm, empathetic, and \
  professional. Use short paragraphs for readability.
• recommendedSpecialty — null during FOLLOW_UP. A specialty name string \
  (from the list above) during ASSESSMENT.
• isEmergency — false unless life-threatening symptoms are detected.
• assessmentReady — false during FOLLOW_UP, true during ASSESSMENT.
"""

# ---------------------------------------------------------------------------
# Fallback response when Gemini is unreachable or returns garbage
# ---------------------------------------------------------------------------
FALLBACK_RESPONSE: dict = {
    "type": "ERROR",
    "message": (
        "I'm sorry, I'm having trouble processing your request right now. "
        "Please try again in a moment. If you're experiencing a medical "
        "emergency, please call 112 or go to your nearest hospital."
    ),
    "recommendedSpecialty": None,
    "isEmergency": False,
    "assessmentReady": False,
}

# ---------------------------------------------------------------------------
# Gemini client initialisation (lazy — runs once on first call)
# ---------------------------------------------------------------------------
_client = None


def _get_client() -> genai.Client:
    """Lazily initialise and cache the Gemini Client."""
    global _client
    if _client is not None:
        return _client

    api_key = os.getenv("GEMINI_API_KEY")
    if not api_key:
        raise RuntimeError(
            "GEMINI_API_KEY environment variable is not set. "
            "Please set it before starting the AI service."
        )

    _client = genai.Client(api_key=api_key)

    logger.info("Gemini client initialised with google.genai SDK")
    return _client


# ---------------------------------------------------------------------------
# History conversion helpers
# ---------------------------------------------------------------------------

def _build_gemini_contents(history: List[ChatMessage], current_message: str) -> list[types.Content]:
    """
    Convert the Spring Boot conversation history + current message into
    the google.genai Content format.
    Spring Boot sends role="user"|"assistant"; Gemini wants role="user"|"model".
    """
    contents = []
    for msg in history:
        role = "model" if msg.role.lower() == "assistant" else "user"
        contents.append(
            types.Content(
                role=role,
                parts=[types.Part.from_text(text=msg.content)],
            )
        )

    # Append the current user message
    contents.append(
        types.Content(
            role="user",
            parts=[types.Part.from_text(text=current_message)],
        )
    )

    return contents


# ---------------------------------------------------------------------------
# Public API
# ---------------------------------------------------------------------------

def assess_symptoms(message: str, history: List[ChatMessage]) -> dict:
    """
    Send the user's message plus full conversation history to Gemini and
    return a structured dict matching the AssessResponse schema.

    Called by routers/assess.py on every POST /assess request.
    """
    try:
        client = _get_client()
        model_name = os.getenv("GEMINI_MODEL", "gemini-2.0-flash")

        # Build the full conversation contents
        contents = _build_gemini_contents(history, message)

        # Configure generation settings
        config = types.GenerateContentConfig(
            system_instruction=SYSTEM_PROMPT,
            response_mime_type="application/json",
            temperature=0.7,
            top_p=0.95,
            max_output_tokens=1024,
        )

        # Call Gemini
        response = client.models.generate_content(
            model=model_name,
            contents=contents,
            config=config,
        )

        # Parse the JSON response from Gemini
        response_text = response.text.strip()
        logger.debug("Raw Gemini response: %s", response_text)

        result = json.loads(response_text)

        # Validate required fields exist with safe defaults
        validated: dict = {
            "type": result.get("type", "FOLLOW_UP"),
            "message": result.get("message", ""),
            "recommendedSpecialty": result.get("recommendedSpecialty"),
            "isEmergency": bool(result.get("isEmergency", False)),
            "assessmentReady": bool(result.get("assessmentReady", False)),
        }

        # Sanity check: if type is ASSESSMENT, assessmentReady must be true
        if validated["type"] == "ASSESSMENT":
            validated["assessmentReady"] = True

        logger.info(
            "Gemini assessment — type=%s, emergency=%s, specialty=%s",
            validated["type"],
            validated["isEmergency"],
            validated["recommendedSpecialty"],
        )

        return validated

    except json.JSONDecodeError as e:
        logger.error("Failed to parse Gemini JSON response: %s", e)
        # Try to salvage a plain-text response from Gemini
        try:
            raw_text = response.text.strip()
            return {
                "type": "FOLLOW_UP",
                "message": raw_text,
                "recommendedSpecialty": None,
                "isEmergency": False,
                "assessmentReady": False,
            }
        except Exception:
            return dict(FALLBACK_RESPONSE)

    except Exception as e:
        logger.error("Gemini API call failed: %s", e, exc_info=True)
        return dict(FALLBACK_RESPONSE)
