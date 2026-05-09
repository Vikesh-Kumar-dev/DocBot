import { Link } from 'react-router-dom';
import { useAuth } from '../context/AuthContext';
import {
  Activity, ShieldAlert, Clock, MapPin, ArrowRight,
  MessageSquare, CheckCircle, Stethoscope, Heart
} from 'lucide-react';
import './Landing.css';

export default function Landing() {
  const { user } = useAuth();

  return (
    <div className="landing">
      {/* Hero */}
      <section className="hero">
        <div className="hero-glow" />
        <div className="hero-content">
          <div className="hero-badge animate-fade-in-up">
            <Heart size={12} fill="var(--danger)" color="var(--danger)" />
            AI-Powered Healthcare for India
          </div>
          <h1 className="hero-title animate-fade-in-up" style={{ animationDelay: '0.05s' }}>
            Your symptoms.<br />
            <span className="gradient-text">Understood instantly.</span>
          </h1>
          <p className="hero-subtitle animate-fade-in-up" style={{ animationDelay: '0.1s' }}>
            Describe how you feel, get an AI-driven preliminary assessment, and book
            an appointment with a verified specialist near you — all in one flow.
          </p>
          <div className="hero-actions animate-fade-in-up" style={{ animationDelay: '0.15s' }}>
            {user ? (
              <Link to="/chat">
                <button className="btn-hero">
                  <MessageSquare size={18} />
                  Start Assessment
                  <ArrowRight size={16} />
                </button>
              </Link>
            ) : (
              <>
                <Link to="/register">
                  <button className="btn-hero">
                    Get Started Free
                    <ArrowRight size={16} />
                  </button>
                </Link>
                <Link to="/login" className="hero-login-link">
                  Already have an account?
                </Link>
              </>
            )}
          </div>
        </div>
      </section>

      {/* How it works */}
      <section className="how-section">
        <div className="container">
          <h2 className="section-title">How DocBot Works</h2>
          <p className="section-subtitle">Three simple steps to better healthcare access</p>
          <div className="steps-grid">
            {[
              {
                icon: <MessageSquare size={24} />,
                color: 'var(--primary)',
                bg: 'var(--primary-muted)',
                step: '01',
                title: 'Describe Symptoms',
                desc: 'Chat naturally with our AI. It asks smart follow-up questions to understand your condition thoroughly.',
              },
              {
                icon: <Stethoscope size={24} />,
                color: 'var(--accent)',
                bg: 'var(--accent-glow)',
                step: '02',
                title: 'Get Assessment',
                desc: 'Receive a preliminary assessment with a recommended specialist type — powered by Google Gemini AI.',
              },
              {
                icon: <MapPin size={24} />,
                color: 'var(--warning)',
                bg: 'var(--warning-bg)',
                step: '03',
                title: 'Book Instantly',
                desc: 'Find verified providers near you sorted by distance and ratings. Book a slot in seconds.',
              },
            ].map((item, i) => (
              <div key={i} className="step-card animate-fade-in-up" style={{ animationDelay: `${i * 0.1}s` }}>
                <div className="step-number">{item.step}</div>
                <div className="step-icon" style={{ background: item.bg, color: item.color }}>
                  {item.icon}
                </div>
                <h3>{item.title}</h3>
                <p>{item.desc}</p>
              </div>
            ))}
          </div>
        </div>
      </section>

      {/* Safety */}
      <section className="safety-section">
        <div className="container">
          <div className="safety-card">
            <div className="safety-icon">
              <ShieldAlert size={28} color="var(--danger)" />
            </div>
            <div>
              <h3>Emergency Detection Built-In</h3>
              <p>
                DocBot automatically detects critical symptoms like chest pain, difficulty breathing,
                or stroke signs. When red flags are found, you're immediately directed to call <strong>112</strong> or
                go to the nearest emergency room. Your safety always comes first.
              </p>
            </div>
          </div>
        </div>
      </section>

      {/* Footer */}
      <footer className="landing-footer">
        <div className="container">
          <div className="disclaimer-bar">
            <CheckCircle size={14} />
            <span>
              <strong>Disclaimer:</strong> DocBot provides preliminary AI assessments only.
              It is not a diagnostic tool. Always consult a qualified healthcare professional.
            </span>
          </div>
          <p className="copyright">© {new Date().getFullYear()} DocBot — AI Healthcare MVP</p>
        </div>
      </footer>
    </div>
  );
}
