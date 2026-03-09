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
            // TODO: Redux/Zustand 또는 Context 에 유저 정보/토큰 세팅
            console.log("로그인 성공! 발급받은 토큰:", token);
            localStorage.setItem("accessToken", token);

            // 임시 전역 관리 처리 완료 후 메인 홈페이지(혹은 어드민)로 스무스하게 이동
            navigate('/', { replace: true });
        } else {
            console.error("토큰을 찾을 수 없습니다.");
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
