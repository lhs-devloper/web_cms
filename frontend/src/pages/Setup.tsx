import { useState } from 'react';

interface SetupProps {
  onComplete: () => void;
}

export default function Setup({ onComplete }: SetupProps) {
  const [name, setName] = useState('');
  const [email, setEmail] = useState('');
  const [password, setPassword] = useState('');
  const [confirmPassword, setConfirmPassword] = useState('');
  const [message, setMessage] = useState<{ type: 'error' | 'success'; text: string } | null>(null);
  const [loading, setLoading] = useState(false);

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    setMessage(null);

    if (password !== confirmPassword) {
      setMessage({ type: 'error', text: '비밀번호가 일치하지 않습니다.' });
      return;
    }

    setLoading(true);
    try {
      const res = await fetch('/api/setup/init', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({ name, email, password }),
      });
      const data = await res.json();
      if (res.ok) {
        setMessage({ type: 'success', text: '관리자 계정이 생성되었습니다. 잠시 후 사이트로 이동합니다.' });
        setTimeout(() => onComplete(), 2000);
      } else {
        setMessage({ type: 'error', text: data.message || '오류가 발생했습니다.' });
      }
    } catch {
      setMessage({ type: 'error', text: '서버 연결에 실패했습니다.' });
    } finally {
      setLoading(false);
    }
  };

  return (
    <div style={{
      minHeight: '100vh', display: 'flex', justifyContent: 'center', alignItems: 'center',
      background: '#0f172a', fontFamily: "-apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, sans-serif", color: '#e2e8f0',
    }}>
      <div style={{
        background: '#1e293b', border: '1px solid #334155', borderRadius: '16px',
        padding: '3rem', width: '100%', maxWidth: '440px', boxShadow: '0 25px 50px rgba(0,0,0,0.5)',
      }}>
        <h1 style={{ fontSize: '1.8rem', textAlign: 'center', marginBottom: '0.5rem', color: '#f8fafc' }}>
          CMS 초기 설정
        </h1>
        <p style={{ textAlign: 'center', color: '#94a3b8', fontSize: '0.9rem', marginBottom: '2rem' }}>
          최초 관리자 계정을 생성합니다.
        </p>

        {message && (
          <div style={{
            textAlign: 'center', padding: '1rem', borderRadius: '8px', marginBottom: '1rem', fontSize: '0.9rem',
            background: message.type === 'error' ? 'rgba(244,63,94,0.1)' : 'rgba(16,185,129,0.1)',
            border: `1px solid ${message.type === 'error' ? 'rgba(244,63,94,0.3)' : 'rgba(16,185,129,0.3)'}`,
            color: message.type === 'error' ? '#f43f5e' : '#10b981',
          }}>
            {message.text}
          </div>
        )}

        <form onSubmit={handleSubmit}>
          {[
            { label: '관리자 이름', type: 'text', value: name, setter: setName, placeholder: '관리자' },
            { label: '이메일', type: 'email', value: email, setter: setEmail, placeholder: 'admin@example.com' },
            { label: '비밀번호', type: 'password', value: password, setter: setPassword, placeholder: '비밀번호 입력' },
            { label: '비밀번호 확인', type: 'password', value: confirmPassword, setter: setConfirmPassword, placeholder: '비밀번호 다시 입력' },
          ].map((field) => (
            <div key={field.label} style={{ marginBottom: '1.2rem' }}>
              <label style={{ display: 'block', fontSize: '0.85rem', color: '#94a3b8', marginBottom: '0.4rem', fontWeight: 500 }}>
                {field.label}
              </label>
              <input
                type={field.type}
                value={field.value}
                onChange={(e) => field.setter(e.target.value)}
                required
                placeholder={field.placeholder}
                style={{
                  width: '100%', padding: '0.8rem 1rem', background: '#0f172a',
                  border: '1px solid #334155', borderRadius: '8px', color: '#f8fafc', fontSize: '1rem',
                  outline: 'none', boxSizing: 'border-box',
                }}
              />
            </div>
          ))}
          <button
            type="submit"
            disabled={loading}
            style={{
              width: '100%', padding: '0.9rem', background: loading ? '#334155' : '#00d2ff',
              color: loading ? '#64748b' : '#0f172a', border: 'none', borderRadius: '8px',
              fontSize: '1rem', fontWeight: 700, cursor: loading ? 'not-allowed' : 'pointer', marginTop: '0.5rem',
            }}
          >
            {loading ? '생성 중...' : '관리자 계정 생성'}
          </button>
        </form>
      </div>
    </div>
  );
}
