import { Star, MapPin, Phone, Globe, BadgeCheck } from 'lucide-react';

export default function ProviderCard({ provider, isRegistered, onBook }) {
  return (
    <div className="prov-card">
      <div className="prov-card-header">
        <div>
          <h3 className="prov-name">{provider.name}</h3>
          <p className="prov-spec">{provider.specialization}</p>
        </div>
        {isRegistered ? (
          <span className="badge badge-success"><BadgeCheck size={11} /> Partner</span>
        ) : (
          <span className="badge badge-neutral">External</span>
        )}
      </div>

      <div className="prov-details">
        {isRegistered && provider.clinicName && (
          <div className="prov-detail-row">
            <MapPin size={13} />
            <span>{provider.clinicName}</span>
          </div>
        )}
        <div className="prov-detail-row">
          <MapPin size={13} />
          <span className="text-sm">{provider.address}</span>
        </div>
        <div className="prov-detail-row">
          <Star size={13} color="var(--warning)" fill="var(--warning)" />
          <span>{provider.googleRating?.toFixed(1) || '—'} Google</span>
          {isRegistered && provider.inAppRating && (
            <span className="text-muted">• {provider.inAppRating.toFixed(1)} In-App</span>
          )}
        </div>
        {!isRegistered && provider.contactPhone && (
          <div className="prov-detail-row">
            <Phone size={13} />
            <span>{provider.contactPhone}</span>
          </div>
        )}
      </div>

      <div className="prov-footer">
        <div className="prov-meta">
          {isRegistered && provider.consultationPrice != null && (
            <span className="prov-price">₹{Number(provider.consultationPrice).toFixed(0)}</span>
          )}
          <span className="prov-dist">{provider.distanceKm} km</span>
        </div>
        {isRegistered ? (
          <button onClick={onBook} className="prov-book-btn">Book Slot</button>
        ) : (
          provider.contactWebsite ? (
            <a href={provider.contactWebsite} target="_blank" rel="noopener noreferrer">
              <button className="btn-secondary" style={{ fontSize: '0.8rem' }}>
                <Globe size={13} /> Visit Website
              </button>
            </a>
          ) : (
            <a href={`tel:${provider.contactPhone}`}>
              <button className="btn-secondary" style={{ fontSize: '0.8rem' }}>
                <Phone size={13} /> Call
              </button>
            </a>
          )
        )}
      </div>
    </div>
  );
}
