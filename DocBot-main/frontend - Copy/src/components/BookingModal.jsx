import { useState } from 'react';
import { X, CheckCircle, Calendar, Clock, Loader2 } from 'lucide-react';
import { useAuth } from '../context/AuthContext';
import api from '../api/axios';

export default function BookingModal({ provider, onClose, onSuccess }) {
  const { user } = useAuth();
  const [selectedSlot, setSelectedSlot] = useState(null);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState('');
  const [result, setResult] = useState(null);

  const slots = provider.availableSlots || [];

  // Group slots by date
  const slotsByDate = slots.reduce((acc, slot) => {
    if (!acc[slot.date]) acc[slot.date] = [];
    acc[slot.date].push(slot);
    return acc;
  }, {});

  const handleConfirm = async () => {
    if (!selectedSlot) return;
    setLoading(true);
    setError('');

    try {
      const res = await api.post('/appointments/book', {
        userId: user.id,
        providerId: provider.id,
        slotId: selectedSlot.slotId,
      });
      setResult(res.data);
      onSuccess?.(res.data);
    } catch (err) {
      setError(
        err.response?.data?.message || 'Booking failed. The slot may have been taken.'
      );
    } finally {
      setLoading(false);
    }
  };

  // Success state
  if (result) {
    return (
      <div className="modal-overlay animate-fade-in">
        <div className="modal-box" style={{ textAlign: 'center', maxWidth: 400 }}>
          <CheckCircle size={56} color="var(--success)" style={{ margin: '0 auto 1rem' }} />
          <h3 style={{ marginBottom: '0.5rem' }}>Booking Confirmed!</h3>
          <p className="text-secondary text-sm">
            Your appointment with {provider.name} has been confirmed.
          </p>
          <div className="confirm-code">
            {result.confirmationCode}
          </div>
          <p className="text-xs text-muted mt-2">
            {result.appointmentDate} at {result.appointmentTime}
          </p>
          <p className="text-xs text-muted">{result.address}</p>
          <button onClick={onClose} style={{ width: '100%', marginTop: '1.5rem' }}>
            Done
          </button>
        </div>
      </div>
    );
  }

  return (
    <div className="modal-overlay animate-fade-in" onClick={(e) => e.target === e.currentTarget && onClose()}>
      <div className="modal-box">
        <div className="modal-head">
          <h3>Book Appointment</h3>
          <button className="btn-icon" onClick={onClose}><X size={18} /></button>
        </div>

        {/* Provider summary */}
        <div className="modal-provider-summary">
          <h4>{provider.name}</h4>
          <p className="text-sm text-secondary">
            {provider.specialization} • {provider.clinicName}
          </p>
          {provider.consultationPrice != null && (
            <p className="text-sm mt-1" style={{ color: 'var(--accent)', fontWeight: 700 }}>
              Consultation: ₹{Number(provider.consultationPrice).toFixed(0)}
            </p>
          )}
        </div>

        {error && <div className="auth-error" style={{ marginTop: '0.75rem' }}>{error}</div>}

        {/* Slots */}
        {Object.keys(slotsByDate).length === 0 ? (
          <div className="no-slots">
            <Calendar size={32} color="var(--text-muted)" />
            <p className="text-secondary text-sm">No available slots for this provider.</p>
          </div>
        ) : (
          <div className="slots-section">
            {Object.entries(slotsByDate).map(([date, dateSlots]) => (
              <div key={date} className="date-group">
                <div className="date-label">
                  <Calendar size={13} /> {formatDate(date)}
                </div>
                <div className="slot-grid">
                  {dateSlots.map((slot) => (
                    <button
                      key={slot.slotId}
                      className={`slot-chip ${selectedSlot?.slotId === slot.slotId ? 'selected' : ''}`}
                      onClick={() => setSelectedSlot(slot)}
                    >
                      <Clock size={12} /> {slot.timeSlot}
                    </button>
                  ))}
                </div>
              </div>
            ))}
          </div>
        )}

        <button
          className="btn-submit"
          disabled={!selectedSlot || loading}
          onClick={handleConfirm}
          style={{ marginTop: '1rem' }}
        >
          {loading ? <Loader2 size={16} className="spin-icon" /> : 'Confirm Booking'}
        </button>
      </div>
    </div>
  );
}

function formatDate(dateStr) {
  try {
    const d = new Date(dateStr + 'T00:00:00');
    return d.toLocaleDateString('en-IN', { weekday: 'short', month: 'short', day: 'numeric' });
  } catch {
    return dateStr;
  }
}
