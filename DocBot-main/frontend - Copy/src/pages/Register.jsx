import { useState } from 'react';
import { useNavigate, Link } from 'react-router-dom';
import { useAuth } from '../context/AuthContext';
import { UserPlus } from 'lucide-react';
import './Auth.css';

export default function Register() {
  const [form, setForm] = useState({
    name: '', email: '', phone: '', password: '', consent: false,
  });
  const [error, setError] = useState('');
  const [loading, setLoading] = useState(false);
  const { register } = useAuth();
  const navigate = useNavigate();

  const handleChange = (e) => {
    const val = e.target.type === 'checkbox' ? e.target.checked : e.target.value;
    setForm((f) => ({ ...f, [e.target.name]: val }));
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    if (!form.consent) {
      setError('You must consent to the collection of your health data.');
      return;
    }
    if (form.password.length < 8) {
      setError('Password must be at least 8 characters.');
      return;
    }

    setLoading(true);
    setError('');

    try {
      await register(form.name, form.email, form.phone, form.password, form.consent);
      navigate('/chat');
    } catch (err) {
      const msg =
        err.response?.data?.message ||
        err.response?.data?.fieldErrors
          ? Object.values(err.response?.data?.fieldErrors || {}).join('. ')
          : 'Registration failed. Please try again.';
      setError(msg);
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="auth-page animate-fade-in-up">
      <div className="auth-card">
        <h2>Create Account</h2>
        <p className="auth-subtitle">Sign up to get your health assessment</p>

        {error && <div className="auth-error">{error}</div>}

        <form onSubmit={handleSubmit} className="auth-form" id="register-form">
          <div className="form-group">
            <label htmlFor="reg-name">Full Name</label>
            <input id="reg-name" name="name" type="text" value={form.name}
              onChange={handleChange} placeholder="John Doe" required />
          </div>
          <div className="form-group">
            <label htmlFor="reg-email">Email</label>
            <input id="reg-email" name="email" type="email" value={form.email}
              onChange={handleChange} placeholder="you@example.com" required autoComplete="email" />
          </div>
          <div className="form-group">
            <label htmlFor="reg-phone">Phone Number</label>
            <input id="reg-phone" name="phone" type="tel" value={form.phone}
              onChange={handleChange} placeholder="+91 98765 43210" required />
          </div>
          <div className="form-group">
            <label htmlFor="reg-password">Password</label>
            <input id="reg-password" name="password" type="password" value={form.password}
              onChange={handleChange} placeholder="Min 8 characters" required autoComplete="new-password" />
          </div>

          <div className="consent-row">
            <input type="checkbox" name="consent" id="consent"
              checked={form.consent} onChange={handleChange} />
            <label htmlFor="consent">
              I consent to the collection and processing of my health information
              as described in the Privacy Policy. This data helps DocBot provide
              accurate assessments.
            </label>
          </div>

          <button type="submit" disabled={loading} className="btn-submit" id="register-submit">
            {loading ? <span className="spinner" /> : <><UserPlus size={16} /> Create Account</>}
          </button>
        </form>

        <div className="auth-footer">
          Already have an account? <Link to="/login">Log in</Link>
        </div>
      </div>
    </div>
  );
}
