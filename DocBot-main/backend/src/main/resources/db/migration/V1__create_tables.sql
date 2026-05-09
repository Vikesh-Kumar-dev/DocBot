-- V1__create_tables.sql
-- DocBot MVP Database Schema

-- 1. Users
CREATE TABLE IF NOT EXISTS users (
    id              BIGSERIAL       PRIMARY KEY,
    name            VARCHAR(255)    NOT NULL,
    email           VARCHAR(255)    NOT NULL UNIQUE,
    phone           VARCHAR(20)     NOT NULL,
    password_hash   VARCHAR(255)    NOT NULL,
    consent_given   BOOLEAN         NOT NULL DEFAULT FALSE,
    created_at      TIMESTAMP       NOT NULL DEFAULT NOW()
);

-- 2. Providers
CREATE TABLE IF NOT EXISTS providers (
    id                  BIGSERIAL       PRIMARY KEY,
    name                VARCHAR(255)    NOT NULL,
    specialization      VARCHAR(255)    NOT NULL,
    clinic_name         VARCHAR(255),
    is_registered       BOOLEAN         NOT NULL DEFAULT FALSE,
    google_rating       DOUBLE PRECISION,
    inapp_rating        DOUBLE PRECISION,
    consultation_price  DECIMAL(10, 2),
    contact_phone       VARCHAR(20),
    contact_website     VARCHAR(500),
    latitude            DOUBLE PRECISION NOT NULL,
    longitude           DOUBLE PRECISION NOT NULL,
    address             TEXT            NOT NULL
);

-- 3. Provider Availability
CREATE TABLE IF NOT EXISTS provider_availability (
    id          BIGSERIAL       PRIMARY KEY,
    provider_id BIGINT          NOT NULL REFERENCES providers(id) ON DELETE CASCADE,
    date        DATE            NOT NULL,
    time_slot   TIME            NOT NULL,
    is_booked   BOOLEAN         NOT NULL DEFAULT FALSE
);

CREATE INDEX idx_availability_provider_date ON provider_availability(provider_id, date);

-- 4. Appointments
CREATE TABLE IF NOT EXISTS appointments (
    id                  BIGSERIAL       PRIMARY KEY,
    user_id             BIGINT          NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    provider_id         BIGINT          NOT NULL REFERENCES providers(id) ON DELETE CASCADE,
    slot_id             BIGINT          NOT NULL REFERENCES provider_availability(id),
    status              VARCHAR(50)     NOT NULL DEFAULT 'CONFIRMED',
    confirmation_code   VARCHAR(50)     NOT NULL UNIQUE,
    created_at          TIMESTAMP       NOT NULL DEFAULT NOW()
);

CREATE INDEX idx_appointments_user ON appointments(user_id);

-- 5. Conversation Logs
CREATE TABLE IF NOT EXISTS conversation_logs (
    id          BIGSERIAL       PRIMARY KEY,
    user_id     BIGINT          NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    session_id  VARCHAR(255)    NOT NULL,
    role        VARCHAR(20)     NOT NULL,
    message     TEXT            NOT NULL,
    timestamp   TIMESTAMP       NOT NULL DEFAULT NOW()
);

CREATE INDEX idx_conversation_session ON conversation_logs(session_id);

-- 6. Symptom Assessments (anonymized — no direct user FK)
CREATE TABLE IF NOT EXISTS symptom_assessments (
    id                      BIGSERIAL       PRIMARY KEY,
    session_id              VARCHAR(255)    NOT NULL,
    symptoms_reported       TEXT            NOT NULL,
    preliminary_assessment  TEXT,
    recommended_specialty   VARCHAR(255),
    is_emergency            BOOLEAN         NOT NULL DEFAULT FALSE,
    timestamp               TIMESTAMP       NOT NULL DEFAULT NOW()
);

-- 7. Consent Records
CREATE TABLE IF NOT EXISTS consent_records (
    id              BIGSERIAL       PRIMARY KEY,
    user_id         BIGINT          NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    consent_type    VARCHAR(100)    NOT NULL,
    agreed          BOOLEAN         NOT NULL DEFAULT FALSE,
    timestamp       TIMESTAMP       NOT NULL DEFAULT NOW()
);
