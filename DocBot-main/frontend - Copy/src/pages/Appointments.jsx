import { useState, useEffect } from 'react';
import { Calendar, Clock, MapPin, CheckCircle, Loader2, CalendarX2, RefreshCw } from 'lucide-react';
import api from '../api/axios';
import './Appointments.css';

export default function Appointments() {
  const [appointments, setAppointments] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');

  const fetchAppointments = async () => {
    setLoading(true);
    setError('');
    try {
      const res = await api.get('/user/appointments');
      setAppointments(res.data);
    } catch (err) {
      setError(err.response?.data?.message || 'Failed to load appointments.');
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    fetchAppointments();
  }, []);

  const getStatusBadge = (status) => {
    switch (status?.toUpperCase()) {
      case 'CONFIRMED':
        return <span className="badge badge-success"><CheckCircle size={10} /> Confirmed</span>;
      case 'CANCELLED':
        return <span className="badge badge-danger">Cancelled</span>;
      default:
        return <span className="badge badge-primary">{status}</span>;
    }
  };

  return (
    <div className="appts-container animate-fade-in-up">
      <div className="appts-header">
        <div>
          <h1>Your Appointments</h1>
          <p className="text-secondary text-sm">Manage your upcoming and past consultations.</p>
        </div>
        <button className="btn-secondary" onClick={fetchAppointments} disabled={loading}
          style={{ fontSize: '0.8rem' }}>
          <RefreshCw size={14} /> Refresh
        </button>
      </div>

      {loading && (
        <div className="appts-empty">
          <Loader2 size={28} className="spin-icon" color="var(--primary)" />
          <p className="text-secondary">Loading appointments…</p>
        </div>
      )}

      {error && (
        <div className="appts-empty">
          <p className="text-secondary">{error}</p>
          <button onClick={fetchAppointments} style={{ fontSize: '0.8rem' }}>Retry</button>
        </div>
      )}

      {!loading && !error && appointments.length === 0 && (
        <div className="appts-empty">
          <CalendarX2 size={40} color="var(--text-muted)" />
          <h3 style={{ color: 'var(--text-secondary)' }}>No Appointments</h3>
          <p className="text-muted text-sm">
            You don't have any appointments yet. Start a symptom assessment to find and book a provider.
          </p>
        </div>
      )}

      <div className="appts-list">
        {appointments.map((apt, i) => (
          <div key={apt.appointmentId || i} className="appt-card animate-fade-in-up"
            style={{ animationDelay: `${i * 0.05}s` }}>
            <div className="appt-top">
              <span className="appt-code font-mono">{apt.confirmationCode}</span>
              {getStatusBadge(apt.status)}
            </div>

            <div className="appt-body">
              <h3>{apt.providerName}</h3>
              {apt.clinicName && <p className="text-secondary text-sm">{apt.clinicName}</p>}

              <div className="appt-meta">
                <div className="meta-chip">
                  <Calendar size={14} />
                  {formatDate(apt.appointmentDate)}
                </div>
                <div className="meta-chip">
                  <Clock size={14} />
                  {apt.appointmentTime}
                </div>
                {apt.address && (
                  <div className="meta-chip">
                    <MapPin size={14} />
                    <span className="meta-addr">{apt.address}</span>
                  </div>
                )}
              </div>
            </div>

            {apt.message && (
              <p className="appt-message text-xs">{apt.message}</p>
            )}
          </div>
        ))}
      </div>
    </div>
  );
}

function formatDate(dateStr) {
  if (!dateStr) return '—';
  try {
    const d = new Date(dateStr + 'T00:00:00');
    return d.toLocaleDateString('en-IN', { weekday: 'short', month: 'short', day: 'numeric', year: 'numeric' });
  } catch {
    return dateStr;
  }
}
