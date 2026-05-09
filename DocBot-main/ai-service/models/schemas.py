from pydantic import BaseModel, Field
from typing import Optional, List


class ChatMessage(BaseModel):
    """
    A single message in the conversation history.
    Mirrors ChatRequest.ChatMessage from the Spring Boot backend.
    - role: "user" or "assistant"
    - content: the message text
    """
    role: str
    content: str


class AssessRequest(BaseModel):
    """
    Incoming request from Spring Boot's AiAssessmentService.
    - message: the user's latest message
    - history: full conversation history for this session
    """
    message: str
    history: List[ChatMessage] = Field(default_factory=list)


class AssessResponse(BaseModel):
    """
    Structured response returned to Spring Boot.
    Must match the fields expected by ChatService.java:
    - type: FOLLOW_UP | ASSESSMENT | EMERGENCY
    - message: the AI response text
    - recommendedSpecialty: e.g. "General Physician" (only when type=ASSESSMENT)
    - isEmergency: boolean (secondary check on AI side)
    - assessmentReady: boolean
    """
    type: str
    message: str
    recommendedSpecialty: Optional[str] = None
    isEmergency: bool = False
    assessmentReady: bool = False
