import { useState } from 'react';
import { Link, useNavigate } from 'react-router-dom';
import { Mail, Lock, User, ArrowRight } from 'lucide-react';
import './Login.css';

const Signup = () => {
    const [name, setName] = useState('');
    const [email, setEmail] = useState('');
    const [password, setPassword] = useState('');
    const [confirmPassword, setConfirmPassword] = useState('');
    const navigate = useNavigate();

    const handleSubmit = async (e: React.FormEvent) => {
        e.preventDefault();

        if (password !== confirmPassword) {
            alert('비밀번호가 일치하지 않습니다.');
            return;
        }

        if (password.length < 4) {
            alert('비밀번호는 4자 이상이어야 합니다.');
            return;
        }

        try {
            const response = await fetch('/api/auth/signup', {
                method: 'POST',
                headers: { 'Content-Type': 'application/json' },
                body: JSON.stringify({ name, email, password })
            });

            if (response.ok) {
                alert('회원가입이 완료되었습니다. 로그인해주세요.');
                navigate('/login');
            } else {
                const errorData = await response.json();
                alert(errorData.message || '회원가입에 실패했습니다.');
            }
        } catch (error) {
            console.error('Signup Error:', error);
            alert('서버와의 통신에 실패했습니다.');
        }
    };

    return (
        <div className="login-container">
            <div className="login-box animate-fade-in">
                <div className="login-header">
                    <h2>회원가입</h2>
                    <p>새 계정을 만들어주세요</p>
                </div>

                <form onSubmit={handleSubmit} className="login-form">
                    <label htmlFor="name" className="login-input-group">
                        <div className="login-input-icon">
                            <User size={18} />
                        </div>
                        <input
                            type="text"
                            id="name"
                            placeholder="이름을 입력해주세요"
                            value={name}
                            onChange={(e) => setName(e.target.value)}
                            required
                        />
                    </label>

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
                            minLength={4}
                        />
                    </label>

                    <label htmlFor="confirmPassword" className="login-input-group">
                        <div className="login-input-icon">
                            <Lock size={18} />
                        </div>
                        <input
                            type="password"
                            id="confirmPassword"
                            placeholder="비밀번호를 다시 입력해주세요"
                            value={confirmPassword}
                            onChange={(e) => setConfirmPassword(e.target.value)}
                            required
                            minLength={4}
                        />
                    </label>

                    <button type="submit" className="login-btn">
                        회원가입 <ArrowRight size={18} className="arrow-icon" />
                    </button>

                    <div className="signup-link">
                        이미 계정이 있으신가요? <Link to="/login">로그인</Link>
                    </div>
                </form>
            </div>

            <div className="deco-circle circle-1"></div>
            <div className="deco-circle circle-2"></div>
        </div>
    );
};

export default Signup;
