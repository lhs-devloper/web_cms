import { useState } from 'react';
import { Link, useNavigate } from 'react-router-dom';
import { Mail, Lock, ArrowRight } from 'lucide-react';
import './Login.css';

const Login = () => {
    const [email, setEmail] = useState('');
    const [password, setPassword] = useState('');
    const navigate = useNavigate();

    const handleSubmit = async (e: React.FormEvent) => {
        e.preventDefault();

        try {
            const response = await fetch(`http://${window.location.hostname}:8080/api/auth/login`, {
                method: 'POST',
                headers: { 'Content-Type': 'application/json' },
                body: JSON.stringify({ email, password })
            });

            if (response.ok) {
                const data = await response.json();
                localStorage.setItem('accessToken', data.token);
                console.log('Login success!');
                // 이동 처리
                if (data.user.role === 'ROLE_ADMIN') {
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

    return (
        <div className="login-container">
            <div className="login-box animate-fade-in">
                <div className="login-header">
                    <h2>Lumière</h2>
                    <p>프리미엄 회원을 위한 로그인</p>
                </div>

                <form onSubmit={handleSubmit} className="login-form">
                    <div className="input-group">
                        <div className="input-icon">
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
                    </div>

                    <div className="input-group">
                        <div className="input-icon">
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
                    </div>

                    <div className="login-options">
                        <label className="checkbox-container">
                            <input type="checkbox" />
                            <span className="checkmark"></span>
                            아이디 저장
                        </label>
                        <a href="#" className="forgot-password">비밀번호 찾기</a>
                    </div>

                    <button type="submit" className="login-btn">
                        로그인 <ArrowRight size={18} className="arrow-icon" />
                    </button>

                    <div className="signup-link">
                        계정이 없으신가요? <Link to="/signup">회원가입</Link>
                    </div>

                    <div className="social-login-divider">
                        <span>또는 간편 로그인</span>
                    </div>

                    <div className="social-login-group">
                        <button type="button" className="social-btn kakao" onClick={() => window.location.href = `http://${window.location.hostname}:8080/oauth2/authorization/kakao`}>
                            <img src="/icons/kakao.svg" alt="Kakao" />
                        </button>
                        <button type="button" className="social-btn naver" onClick={() => window.location.href = `http://${window.location.hostname}:8080/oauth2/authorization/naver`}>
                            <img src="/icons/naver.svg" alt="Naver" />
                        </button>
                        <button type="button" className="social-btn google" onClick={() => window.location.href = `http://${window.location.hostname}:8080/oauth2/authorization/google`}>
                            <img src="/icons/google.svg" alt="Google" />
                        </button>
                        <button type="button" className="social-btn github" onClick={() => window.location.href = `http://${window.location.hostname}:8080/oauth2/authorization/github`}>
                            <img src="/icons/github.svg" alt="Github" />
                        </button>
                    </div>
                </form>
            </div>

            {/* 배경 데코레이션 효과 */}
            <div className="deco-circle circle-1"></div>
            <div className="deco-circle circle-2"></div>
        </div>
    );
};

export default Login;
