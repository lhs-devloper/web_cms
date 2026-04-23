import { useEffect } from 'react';
import { useNavigate, useLocation } from 'react-router-dom';

const LoginSuccess = () => {
    const navigate = useNavigate();
    const location = useLocation();

    useEffect(() => {
        // 백엔드(CustomOAuth2SuccessHandler)에서 보낸 Redirect URL의 파라미터 추출
        const params = new URLSearchParams(location.search);
        const token = params.get('token');

        if (token) {
            localStorage.setItem("accessToken", token);
            navigate('/', { replace: true });
        } else {
            navigate('/login');
        }
    }, [location, navigate]);

    return (
        <div style={{ display: 'flex', justifyContent: 'center', alignItems: 'center', minHeight: '100vh', background: '#0d0f12', color: '#fff' }}>
            <div style={{ textAlign: 'center' }}>
                <h2>로그인 완료 처리중...</h2>
                <p>잠시만 기다려주세요.</p>
            </div>
        </div>
    );
};

export default LoginSuccess;
