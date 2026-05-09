-- V2__seed_sample_providers.sql
-- Sample provider data for development and testing

INSERT INTO providers (name, specialization, clinic_name, is_registered, google_rating, inapp_rating, consultation_price, contact_phone, contact_website, latitude, longitude, address) VALUES
('Dr. Ananya Sharma', 'General Physician', 'HealthFirst Clinic', true, 4.5, 4.6, 500.00, '+91-9876543210', 'https://healthfirst.in', 28.6139, 77.2090, 'Connaught Place, New Delhi, Delhi 110001'),
('Dr. Rajesh Kumar', 'General Physician', 'City Care Hospital', true, 4.2, 4.3, 600.00, '+91-9876543211', 'https://citycare.in', 28.6280, 77.2180, 'Karol Bagh, New Delhi, Delhi 110005'),
('Dr. Priya Patel', 'Dermatologist', 'SkinGlow Clinic', true, 4.7, 4.8, 800.00, '+91-9876543212', 'https://skinglow.in', 28.6353, 77.2250, 'Rajendra Place, New Delhi, Delhi 110008'),
('Dr. Vikram Singh', 'Cardiologist', 'HeartCare Center', true, 4.8, 4.9, 1200.00, '+91-9876543213', 'https://heartcare.in', 28.5672, 77.2100, 'Saket, New Delhi, Delhi 110017'),
('Dr. Meera Joshi', 'Orthopedist', 'BoneWell Hospital', true, 4.3, 4.4, 700.00, '+91-9876543214', 'https://bonewell.in', 28.6508, 77.2340, 'Patel Nagar, New Delhi, Delhi 110008'),
('Dr. Suresh Reddy', 'General Physician', 'Apollo Clinic', false, 4.6, NULL, NULL, '+91-9876543215', 'https://apollo.in', 28.6100, 77.2300, 'Janpath, New Delhi, Delhi 110001'),
('Dr. Kavita Nair', 'Dermatologist', 'Skin Solutions', false, 4.4, NULL, NULL, '+91-9876543216', 'https://skinsolutions.in', 28.6400, 77.2150, 'Pusa Road, New Delhi, Delhi 110005'),
('Dr. Amit Gupta', 'Cardiologist', 'Max Hospital', false, 4.9, NULL, NULL, '+91-9876543217', 'https://maxhealthcare.in', 28.5690, 77.2070, 'Press Enclave, Saket, New Delhi, Delhi 110017');

-- Sample availability for registered providers
INSERT INTO provider_availability (provider_id, date, time_slot, is_booked) VALUES
(1, CURRENT_DATE + 1, '09:00', false),
(1, CURRENT_DATE + 1, '09:30', false),
(1, CURRENT_DATE + 1, '10:00', false),
(1, CURRENT_DATE + 1, '10:30', false),
(1, CURRENT_DATE + 1, '11:00', false),
(1, CURRENT_DATE + 2, '09:00', false),
(1, CURRENT_DATE + 2, '10:00', false),
(2, CURRENT_DATE + 1, '14:00', false),
(2, CURRENT_DATE + 1, '14:30', false),
(2, CURRENT_DATE + 1, '15:00', false),
(2, CURRENT_DATE + 2, '14:00', false),
(3, CURRENT_DATE + 1, '11:00', false),
(3, CURRENT_DATE + 1, '11:30', false),
(3, CURRENT_DATE + 1, '12:00', false),
(4, CURRENT_DATE + 1, '16:00', false),
(4, CURRENT_DATE + 1, '16:30', false),
(4, CURRENT_DATE + 2, '16:00', false),
(5, CURRENT_DATE + 1, '10:00', false),
(5, CURRENT_DATE + 1, '10:30', false),
(5, CURRENT_DATE + 1, '11:00', false);
