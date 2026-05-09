import { useState, useEffect } from 'react';
import { useLocation } from 'react-router-dom';
import { MapPin, Search, Loader2, AlertCircle } from 'lucide-react';
import ProviderCard from '../components/ProviderCard';
import BookingModal from '../components/BookingModal';
import api from '../api/axios';
import './Providers.css';

export default function Providers() {
  const location = useLocation();
  const passedSpecialty = location.state?.specialty || 'General Physician';

  const [specialty, setSpecialty] = useState(passedSpecialty);
  const [registered, setRegistered] = useState([]);
  const [nonRegistered, setNonRegistered] = useState([]);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState('');
  const [selectedProvider, setSelectedProvider] = useState(null);
  const [searchDone, setSearchDone] = useState(false);

  // Default location: New Delhi (Connaught Place)
  const [coords] = useState({ lat: 28.6139, lng: 77.2090 });

  const searchProviders = async () => {
    setLoading(true);
    setError('');
    setSearchDone(false);

    try {
      const res = await api.post('/providers/search', {
        latitude: coords.lat,
        longitude: coords.lng,
        specialty: specialty,
        radiusKm: 15,
      });

      setRegistered(res.data.registeredProviders || []);
      setNonRegistered(res.data.nonRegisteredProviders || []);
      setSearchDone(true);
    } catch (err) {
      setError(err.response?.data?.message || 'Failed to search providers. Please try again.');
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    searchProviders();
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, []);

  const handleBook = (provider) => {
    setSelectedProvider(provider);
  };

  const totalResults = registered.length + nonRegistered.length;

  return (
    <div className="providers-layout">
      {/* List Panel */}
      <div className="providers-panel">
        <div className="providers-head">
          <h2>Find Providers</h2>
          <p className="text-sm text-secondary mb-3">
            Specialty: <strong style={{ color: 'var(--accent)' }}>{specialty}</strong>
          </p>

          <div className="prov-search-bar">
            <Search size={15} color="var(--text-muted)" />
            <input
              type="text"
              value={specialty}
              onChange={(e) => setSpecialty(e.target.value)}
              placeholder="Specialty (e.g. Cardiologist)"
              onKeyDown={(e) => e.key === 'Enter' && searchProviders()}
            />
            <button className="prov-search-go" onClick={searchProviders} disabled={loading}>
              {loading ? <Loader2 size={14} className="spin-icon" /> : 'Search'}
            </button>
          </div>
        </div>

        <div className="providers-list">
          {loading && (
            <div className="prov-empty">
              <Loader2 size={28} className="spin-icon" color="var(--primary)" />
              <p className="text-secondary">Searching nearby providers…</p>
            </div>
          )}

          {error && (
            <div className="prov-empty">
              <AlertCircle size={28} color="var(--danger)" />
              <p className="text-secondary">{error}</p>
              <button onClick={searchProviders} style={{ fontSize: '0.8rem' }}>Retry</button>
            </div>
          )}

          {!loading && !error && searchDone && totalResults === 0 && (
            <div className="prov-empty">
              <MapPin size={32} color="var(--text-muted)" />
              <p className="text-secondary">No providers found for "{specialty}" in your area.</p>
            </div>
          )}

          {/* Registered providers */}
          {registered.length > 0 && (
            <>
              <div className="prov-section-label">
                DocBot Partners ({registered.length})
              </div>
              {registered.map((p) => (
                <ProviderCard key={p.id} provider={p} isRegistered onBook={() => handleBook(p)} />
              ))}
            </>
          )}

          {/* Non-registered */}
          {nonRegistered.length > 0 && (
            <>
              <div className="prov-section-label" style={{ marginTop: '0.5rem' }}>
                Other Providers ({nonRegistered.length})
              </div>
              {nonRegistered.map((p) => (
                <ProviderCard key={p.id} provider={p} isRegistered={false} />
              ))}
            </>
          )}
        </div>
      </div>

      {/* Map Placeholder */}
      <div className="providers-map hidden-mobile">
        <div className="map-placeholder-content">
          <MapPin size={40} color="var(--primary)" style={{ opacity: 0.4 }} />
          <h3 style={{ color: 'var(--text-muted)' }}>Map View</h3>
          <p className="text-sm text-muted text-center" style={{ maxWidth: 280 }}>
            Google Maps integration will show provider locations based on your proximity.
          </p>
        </div>
      </div>

      {/* Booking Modal */}
      {selectedProvider && (
        <BookingModal
          provider={selectedProvider}
          onClose={() => setSelectedProvider(null)}
          onSuccess={() => {
            // Refresh to update slot availability
            setTimeout(() => searchProviders(), 1000);
          }}
        />
      )}
    </div>
  );
}
