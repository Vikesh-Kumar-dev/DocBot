import { useState, useRef, useEffect, useCallback } from 'react';
import { Send, AlertTriangle, MapPin, Activity, RotateCcw } from 'lucide-react';
import { useNavigate } from 'react-router-dom';
import { useAuth } from '../context/AuthContext';
import api from '../api/axios';
import './Chat.css';

function generateSessionId() {
  return 'sess_' + Date.now() + '_' + Math.random().toString(36).slice(2, 9);
}

export default function Chat() {
  const { user } = useAuth();
  const navigate = useNavigate();
  const [sessionId] = useState(generateSessionId);
  const [messages, setMessages] = useState([
    {
      id: 'welcome',
      role: 'assistant',
      content: `Hello${user?.name ? ', ' + user.name.split(' ')[0] : ''}! I'm DocBot, your AI healthcare assistant.\n\nPlease describe your symptoms in detail — what you're feeling, how long it's been happening, and any other relevant information.`,
      type: 'FOLLOW_UP',
    },
  ]);
  const [input, setInput] = useState('');
  const [isTyping, setIsTyping] = useState(false);
  const [assessment, setAssessment] = useState(null);
  const [isEmergency, setIsEmergency] = useState(false);
  const messagesEndRef = useRef(null);
  const inputRef = useRef(null);

  const scrollToBottom = useCallback(() => {
    messagesEndRef.current?.scrollIntoView({ behavior: 'smooth' });
  }, []);

  useEffect(() => {
    scrollToBottom();
  }, [messages, isTyping, scrollToBottom]);

  useEffect(() => {
    inputRef.current?.focus();
  }, []);

  const handleSend = async (e) => {
    e?.preventDefault();
    const text = input.trim();
    if (!text || isTyping || assessment) return;

    // Add user message
    const userMsg = { id: Date.now(), role: 'user', content: text };
    setMessages((prev) => [...prev, userMsg]);
    setInput('');
    setIsTyping(true);

    try {
      const res = await api.post('/chat/message', {
        sessionId,
        message: text,
      });

      const data = res.data;

      // Build assistant message
      const assistantMsg = {
        id: Date.now() + 1,
        role: 'assistant',
        content: data.message,
        type: data.type,
        isEmergency: data.isEmergency,
      };

      setMessages((prev) => [...prev, assistantMsg]);

      if (data.isEmergency) {
        setIsEmergency(true);
      }

      if (data.assessmentReady) {
        setAssessment({
          specialty: data.recommendedSpecialty,
          disclaimer: data.disclaimer,
        });
      }
    } catch (err) {
      const errorMsg = {
        id: Date.now() + 1,
        role: 'assistant',
        content:
          "I'm sorry, I'm having trouble processing your request right now. Please try again in a moment. If you're experiencing a medical emergency, please call 112.",
        type: 'ERROR',
      };
      setMessages((prev) => [...prev, errorMsg]);
    } finally {
      setIsTyping(false);
    }
  };

  const handleNewSession = () => {
    window.location.reload();
  };

  const chatDisabled = !!assessment || isEmergency;

  return (
    <div className="chat-layout">
      {/* Sidebar */}
      <aside className="chat-sidebar hidden-mobile">
        <div className="sidebar-block">
          <h4 className="sidebar-label">Session</h4>
          <p className="text-xs text-muted font-mono">{sessionId.slice(0, 20)}…</p>
        </div>

        <button className="btn-secondary" onClick={handleNewSession}
          style={{ fontSize: '0.8rem', width: '100%' }}>
          <RotateCcw size={14} /> New Assessment
        </button>

        <div className="sidebar-disclaimer">
          <AlertTriangle size={14} color="var(--warning)" />
          <div>
            <strong>Not a Doctor</strong>
            <p>This is an AI-powered preliminary assessment tool. Do not use for emergencies.</p>
          </div>
        </div>
      </aside>

      {/* Main Chat Area */}
      <div className="chat-main">
        <div className="messages-area">
          {messages.map((msg) => (
            <div key={msg.id} className={`msg-row ${msg.role}`}>
              {msg.role === 'assistant' && (
                <div className={`msg-avatar ${msg.type === 'EMERGENCY' ? 'avatar-danger' : ''}`}>
                  <Activity size={14} />
                </div>
              )}
              <div className={`msg-bubble ${
                msg.type === 'EMERGENCY' ? 'bubble-emergency' :
                msg.type === 'ERROR' ? 'bubble-error' : ''
              }`}>
                {msg.content.split('\n').map((line, i) => (
                  <span key={i}>{line}<br /></span>
                ))}
              </div>
            </div>
          ))}

          {isTyping && (
            <div className="msg-row assistant">
              <div className="msg-avatar"><Activity size={14} /></div>
              <div className="msg-bubble typing-dots">
                <span /><span /><span />
              </div>
            </div>
          )}

          {/* Assessment Card */}
          {assessment && (
            <div className="assessment-card animate-fade-in-up">
              <h4>✅ Assessment Complete</h4>
              <p className="text-sm text-secondary mt-2">
                Based on your symptoms, we recommend consulting a specialist.
              </p>
              <div className="specialty-pill">{assessment.specialty || 'General Physician'}</div>
              <button
                className="btn-find-providers"
                onClick={() => navigate('/providers', {
                  state: { specialty: assessment.specialty || 'General Physician' }
                })}
              >
                <MapPin size={16} /> Find Nearby Providers
              </button>
              {assessment.disclaimer && (
                <p className="text-xs text-muted mt-3" style={{ lineHeight: 1.5 }}>
                  {assessment.disclaimer}
                </p>
              )}
            </div>
          )}

          <div ref={messagesEndRef} />
        </div>

        {/* Input */}
        <div className="chat-input-area">
          <form onSubmit={handleSend} className="chat-input-form" id="chat-form">
            <input
              ref={inputRef}
              type="text"
              value={input}
              onChange={(e) => setInput(e.target.value)}
              placeholder={chatDisabled ? 'Assessment complete' : 'Describe your symptoms…'}
              disabled={chatDisabled || isTyping}
              id="chat-input"
            />
            <button
              type="submit"
              disabled={!input.trim() || chatDisabled || isTyping}
              className="send-btn"
              id="chat-send"
            >
              <Send size={16} />
            </button>
          </form>
          <p className="input-disclaimer">
            DocBot can make mistakes. Always verify important medical information with a professional.
          </p>
        </div>
      </div>
    </div>
  );
}
