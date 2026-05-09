import { createContext, useContext, useState, useEffect } from 'react';
import api from '../api/axios';

const AuthContext = createContext(null);

export const useAuth = () => {
  const ctx = useContext(AuthContext);
  if (!ctx) throw new Error('useAuth must be used within AuthProvider');
  return ctx;
};

export const AuthProvider = ({ children }) => {
  const [user, setUser] = useState(null);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    const token = localStorage.getItem('docbot_token');
    const userData = localStorage.getItem('docbot_user');
    if (token && userData) {
      try {
        setUser(JSON.parse(userData));
      } catch {
        localStorage.removeItem('docbot_token');
        localStorage.removeItem('docbot_user');
      }
    }
    setLoading(false);
  }, []);

  const login = async (email, password) => {
    const res = await api.post('/auth/login', { email, password });
    const { accessToken, userId } = res.data;
    localStorage.setItem('docbot_token', accessToken);

    // Fetch profile after login
    const profileRes = await api.get('/user/profile', {
      headers: { Authorization: `Bearer ${accessToken}` },
    });
    const userData = { id: userId, ...profileRes.data };
    setUser(userData);
    localStorage.setItem('docbot_user', JSON.stringify(userData));
    return userData;
  };

  const register = async (name, email, phone, password, consentGiven) => {
    const res = await api.post('/auth/register', {
      name, email, phone, password, consentGiven,
    });
    const { accessToken, userId } = res.data;
    localStorage.setItem('docbot_token', accessToken);

    const userData = { id: userId, name, email, phone, consentGiven };
    setUser(userData);
    localStorage.setItem('docbot_user', JSON.stringify(userData));
    return userData;
  };

  const logout = () => {
    setUser(null);
    localStorage.removeItem('docbot_token');
    localStorage.removeItem('docbot_user');
  };

  return (
    <AuthContext.Provider value={{ user, login, register, logout, loading }}>
      {!loading && children}
    </AuthContext.Provider>
  );
};
