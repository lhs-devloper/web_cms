import { useState, useEffect } from 'react';
import { Link, useNavigate } from 'react-router-dom';
import { Mail, Lock, ArrowRight } from 'lucide-react';
import './Login.css';

interface SocialProvider {
    registrationId: string;
    clientName: string;
}

const SOCIAL_META: Record<string, { icon: string; alt: string; className: string }> = {
    kakao: { icon: '/icons/kakao.svg', alt: 'Kakao', className: 'kakao' },
    naver: { icon: '/icons/naver.svg', alt: 'Naver', className: 'naver' },
    google: { icon: '/icons/google.svg', alt: 'Google', className: 'google' },
    github: { icon: '/icons/github.svg', alt: 'Github', className: 'github' },
};

const Login = () => {
    const [email, setEmail] = useState('');
    const [password, setPassword] = useState('');
    const [socialProviders, setSocialProviders] = useState<SocialProvider[]>([]);
    const [siteSetting, setSiteSetting] = useState<any>(null);
    const [showResetModal, setShowResetModal] = useState(false);
    const [resetEmail, setResetEmail] = useState('');
    const [resetResult, setResetResult] = useState<{ type: 'success' | 'error'; message: string; tempPassword?: string } | null>(null);
    const navigate = useNavigate();

    useEffect(() => {
        const fetchSocialProviders = async () => {
            try {
                const res = await fetch(`/api/global/social/active`);
                if (res.ok) {
                    const data = await res.json();
                    setSocialProviders(data);
                }
            } catch (err) {
                console.error('Failed to load social providers:', err);
            }
        };
        const fetchSiteSetting = async () => {
            try {
                const res = await fetch(`/api/global/setting`);
                if (res.ok) setSiteSetting(await res.json());
            } catch (err) {
                console.error('Failed to load site setting:', err);
            }
        };
        fetchSocialProviders();
        fetchSiteSetting();
    }, []);

    const handleSubmit = async (e: React.FormEvent) => {
        e.preventDefault();

        try {
            const response = await fetch(`/api/auth/login`, {
                method: 'POST',
                headers: { 'Content-Type': 'application/json' },
                body: JSON.stringify({ email, password })
            });

            if (response.ok) {
                const data = await response.json();
                localStorage.setItem('accessToken', data.token);
                if (['ROLE_ADMIN', 'ROLE_SUPER_ADMIN', 'ADMIN', 'SUPER_ADMIN'].includes(data.user.role)) {
                    navigate('/admin');
                } else {
                    navigate('/');
                }
            } else {
                const errorData = await response.json();
                alert(errorData.message || '로그인에 실패했습니다.');
            }
        } catch (error) {
            console.error('Login Error:', error);
            alert('서버와의 통신에 실패했습니다.');
        }
    };

    const handleResetPassword = async (e: React.FormEvent) => {
        e.preventDefault();
        setResetResult(null);
        try {
            const res = await fetch('/api/auth/reset-password', {
                method: 'POST',
                headers: { 'Content-Type': 'application/json' },
                body: JSON.stringify({ email: resetEmail })
            });
            const data = await res.json();
            if (res.ok) {
                setResetResult({ type: 'success', message: data.message, tempPassword: data.tempPassword });
            } else {
                setResetResult({ type: 'error', message: data.message || '오류가 발생했습니다.' });
            }
        } catch {
            setResetResult({ type: 'error', message: '서버와의 통신에 실패했습니다.' });
        }
    };

    return (
        <div className="login-container">
            {showResetModal && (
                <div className="reset-modal-overlay" onClick={() => setShowResetModal(false)}>
                    <div className="login-box animate-fade-in" style={{ position: 'relative', zIndex: 10 }} onClick={e => e.stopPropagation()}>
                        <div className="login-header">
                            <h2>비밀번호 찾기</h2>
                            <p>가입한 이메일을 입력하면 임시 비밀번호가 발급됩니다.</p>
                        </div>
                        <form onSubmit={handleResetPassword} className="login-form">
                            <label className="login-input-group">
                                <div className="login-input-icon"><Mail size={18} /></div>
                                <input
                                    type="email"
                                    placeholder="가입한 이메일을 입력해주세요"
                                    value={resetEmail}
                                    onChange={e => setResetEmail(e.target.value)}
                                    required
                                />
                            </label>
                            {resetResult && (
                                <div style={{
                                    padding: '0.8rem', borderRadius: '8px', fontSize: '0.9rem', marginBottom: '0.5rem',
                                    background: resetResult.type === 'success' ? 'rgba(16,185,129,0.1)' : 'rgba(244,63,94,0.1)',
                                    border: `1px solid ${resetResult.type === 'success' ? 'rgba(16,185,129,0.3)' : 'rgba(244,63,94,0.3)'}`,
                                    color: resetResult.type === 'success' ? '#10b981' : '#f43f5e'
                                }}>
                                    <p>{resetResult.message}</p>
                                    {resetResult.tempPassword && (
                                        <p style={{ marginTop: '0.5rem', fontWeight: 'bold', fontSize: '1.1rem', letterSpacing: '2px' }}>
                                            임시 비밀번호: {resetResult.tempPassword}
                                        </p>
                                    )}
                                </div>
                            )}
                            <button type="submit" className="login-btn">임시 비밀번호 발급</button>
                            <button type="button" className="login-btn" style={{ background: 'transparent', border: '1px solid var(--text-muted)', color: 'var(--text-muted)', marginTop: '0.5rem' }} onClick={() => setShowResetModal(false)}>닫기</button>
                        </form>
                    </div>
                </div>
            )}
            <div className="login-box animate-fade-in">
                <div className="login-header">
                    {siteSetting?.logoUrl ? (
                        <img src={siteSetting.logoUrl.startsWith('http') ? siteSetting.logoUrl : `${siteSetting.logoUrl}`} alt={siteSetting?.logoAltText || 'Logo'} style={{ maxHeight: '48px', marginBottom: '0.5rem' }} />
                    ) : (
                        <h2>{siteSetting?.siteName || 'Lumière'}</h2>
                    )}
                    <p>프리미엄 회원을 위한 로그인</p>
                </div>

                <form onSubmit={handleSubmit} className="login-form">
                    <label htmlFor="email" className="login-input-group">
                        <div className="login-input-icon">
                            <Mail size={18} />
                        </div>
                        <input
                            type="email"
                            id="email"
                            placeholder="이메일을 입력해주세요"
                            value={email}
                            onChange={(e) => setEmail(e.target.value)}
                            required
                        />
                    </label>

                    <label htmlFor="password" className="login-input-group">
                        <div className="login-input-icon">
                            <Lock size={18} />
                        </div>
                        <input
                            type="password"
                            id="password"
                            placeholder="비밀번호를 입력해주세요"
                            value={password}
                            onChange={(e) => setPassword(e.target.value)}
                            required
                        />
                    </label>

                    <div className="login-options">
                        <label className="checkbox-container">
                            <input type="checkbox" />
                            <span className="checkmark"></span>
                            아이디 저장
                        </label>
                        <button type="button" className="forgot-password" onClick={() => { setShowResetModal(true); setResetResult(null); setResetEmail(''); }}>비밀번호 찾기</button>
                    </div>

                    <button type="submit" className="login-btn">
                        로그인 <ArrowRight size={18} className="arrow-icon" />
                    </button>

                    <div className="signup-link">
                        계정이 없으신가요? <Link to="/signup">회원가입</Link>
                    </div>

                    {socialProviders.length > 0 && (
                        <>
                            <div className="social-login-divider">
                                <span>또는 간편 로그인</span>
                            </div>

                            <div className="social-login-group">
                                {socialProviders.map(provider => {
                                    const meta = SOCIAL_META[provider.registrationId.toLowerCase()];
                                    if (!meta) return null;
                                    return (
                                        <button
                                            key={provider.registrationId}
                                            type="button"
                                            className={`social-btn ${meta.className}`}
                                            onClick={() => window.location.href = `/oauth2/authorization/${provider.registrationId}`}
                                        >
                                            <img src={meta.icon} alt={meta.alt} />
                                        </button>
                                    );
                                })}
                            </div>
                        </>
                    )}
                </form>
            </div>

            {/* 배경 데코레이션 효과 */}
            <div className="deco-circle circle-1"></div>
            <div className="deco-circle circle-2"></div>
        </div>
    );
};

export default Login;
