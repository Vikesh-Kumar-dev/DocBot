"""
Rule-based emergency symptom detection.
This is the Python-side secondary safety net — mirrors the Java
EmergencySymptomChecker.java so both layers catch emergencies independently.
"""

EMERGENCY_KEYWORDS: list[str] = [
    "chest pain",
    "difficulty breathing",
    "can't breathe",
    "cannot breathe",
    "shortness of breath",
    "severe bleeding",
    "heavy bleeding",
    "unconscious",
    "loss of consciousness",
    "fainted",
    "fainting",
    "seizure",
    "convulsion",
    "stroke",
    "sudden numbness",
    "sudden weakness",
    "paralysis",
    "suicidal",
    "suicide",
    "self harm",
    "self-harm",
    "overdose",
    "poisoning",
    "severe allergic reaction",
    "anaphylaxis",
    "choking",
    "severe head injury",
    "head trauma",
    "severe burn",
    "heart attack",
    "cardiac arrest",
    "coughing blood",
    "vomiting blood",
    "blood in stool",
    "sudden severe headache",
    "sudden vision loss",
    "high fever with rash",
    "difficulty swallowing",
    "severe abdominal pain",
]


def is_emergency(message: str) -> bool:
    """
    Deterministic keyword check against the user's raw message.
    Returns True if any emergency keyword is found.
    """
    if not message or not message.strip():
        return False
    lower_message = message.lower().strip()
    return any(keyword in lower_message for keyword in EMERGENCY_KEYWORDS)


def get_emergency_message() -> str:
    """
    Standard emergency response — identical to the Java version so the user
    sees a consistent message regardless of which layer triggers it.
    """
    return (
        "🚨 EMERGENCY ALERT: Based on the symptoms you've described, "
        "this could be a medical emergency.\n\n"
        "Please take immediate action:\n"
        "• Call Emergency Services: 112 (India)\n"
        "• Call an Ambulance: 108\n"
        "• Go to the nearest hospital emergency room immediately\n\n"
        "Do NOT wait for an online consultation. Your safety is the top priority.\n\n"
        "If you are with someone experiencing these symptoms, "
        "please help them get emergency medical attention right away."
    )
