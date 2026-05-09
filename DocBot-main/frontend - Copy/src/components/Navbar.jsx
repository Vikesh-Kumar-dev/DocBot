import { Link, useNavigate, useLocation } from 'react-router-dom';
import { useAuth } from '../context/AuthContext';
import { Activity, LogOut, User, MessageSquare, CalendarDays } from 'lucide-react';

export default function Navbar() {
  const { user, logout } = useAuth();
  const navigate = useNavigate();
  const location = useLocation();

  const handleLogout = () => {
    logout();
    navigate('/');
  };

  const isActive = (path) => location.pathname === path;

  return (
    <nav className="navbar">
      <Link to="/" className="nav-brand">
        <Activity size={22} color="var(--accent)" strokeWidth={2.5} />
        DocBot
      </Link>

      <div className="nav-links">
        {user ? (
          <>
            <Link
              to="/chat"
              className="nav-link"
              style={isActive('/chat') ? { color: 'var(--text-primary)', background: 'var(--bg-hover)' } : {}}
            >
              <MessageSquare size={15} style={{ marginRight: 4 }} />
              Assessment
            </Link>
            <Link
              to="/appointments"
              className="nav-link"
              style={isActive('/appointments') ? { color: 'var(--text-primary)', background: 'var(--bg-hover)' } : {}}
            >
              <CalendarDays size={15} style={{ marginRight: 4 }} />
              Appointments
            </Link>
            <div className="nav-user">
              <User size={14} />
              <span>{user.name?.split(' ')[0]}</span>
            </div>
            <button className="btn-icon" onClick={handleLogout} title="Logout"
              style={{ color: 'var(--text-muted)' }}>
              <LogOut size={16} />
            </button>
          </>
        ) : (
          <>
            <Link to="/login" className="nav-link">Login</Link>
            <Link to="/register">
              <button style={{ fontSize: '0.8rem', padding: '0.45rem 1rem' }}>Get Started</button>
            </Link>
          </>
        )}
      </div>
    </nav>
  );
}
