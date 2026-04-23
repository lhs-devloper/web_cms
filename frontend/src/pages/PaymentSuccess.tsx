import { useEffect, useState, useRef } from 'react';
import { useSearchParams, useNavigate } from 'react-router-dom';
import { CheckCircle, AlertCircle } from 'lucide-react';
import './Checkout.css'; // Reusing checkout container styles

const PaymentSuccess = () => {
    const [searchParams] = useSearchParams();
    const navigate = useNavigate();
    const [status, setStatus] = useState<'processing' | 'success' | 'fail'>('processing');
    const [message, setMessage] = useState('결제를 승인하고 있습니다...');

    const paymentKey = searchParams.get('paymentKey');
    const orderId = searchParams.get('orderId');
    const amount = searchParams.get('amount');
    const isMock = searchParams.get('mock');

    const requestSent = useRef(false);

    useEffect(() => {
        const approvePayment = async () => {
            if (requestSent.current) return;
            requestSent.current = true;

            // Mock 결제 (키 미설정 시 approve가 이미 처리된 경우)
            if (isMock) {
                localStorage.removeItem('checkout_internal_order_id');
                setStatus('success');
                setMessage('결제가 성공적으로 완료되었습니다.');
                return;
            }

            const internalOrderId = localStorage.getItem('checkout_internal_order_id');
            const token = localStorage.getItem('accessToken');

            if (!internalOrderId || !paymentKey || !amount) {
                setStatus('fail');
                setMessage('결제 승인에 필요한 정보가 없거나 이미 결제가 처리되었습니다.');
                return;
            }

            // [중요] React Strict Mode 중복 호출 방지를 위해 인증 직전 로컬스토리지에서 orderId 즉시 삭제
            localStorage.removeItem('checkout_internal_order_id');

            try {
                const res = await fetch(`/api/payments/${internalOrderId}/approve`, {
                    method: 'POST',
                    headers: {
                        'Content-Type': 'application/json',
                        'Authorization': `Bearer ${token}`
                    },
                    body: JSON.stringify({
                        paymentKey: paymentKey,
                        orderId: orderId,
                        amount: parseInt(amount, 10)
                    })
                });

                if (res.ok) {
                    setStatus('success');
                    setMessage('결제가 성공적으로 완료되었습니다.');
                } else {
                    const data = await res.json();
                    setStatus('fail');
                    setMessage(`결제 승인 실패: ${data.message || '알 수 없는 오류'}`);
                }
            } catch (err: any) {
                setStatus('fail');
                setMessage('서버와의 통신 중 오류가 발생했습니다.');
            }
        };

        if (status === 'processing') {
            approvePayment();
        }
    }, [paymentKey, orderId, amount, isMock, status]);

    return (
        <div className="container" style={{ padding: 'var(--nav-height) 2rem 5rem 2rem', minHeight: '60vh', display: 'flex', flexDirection: 'column', alignItems: 'center', justifyContent: 'center' }}>
            {status === 'processing' && (
                <div style={{ textAlign: 'center' }}>
                    <div className="loading-indicator">결제 정보 확인 중...</div>
                    <p style={{ marginTop: '1rem', color: '#64748b' }}>{message}</p>
                </div>
            )}
            
            {status === 'success' && (
                <div style={{ textAlign: 'center', maxWidth: '400px' }}>
                    <CheckCircle size={64} color="#00d2ff" style={{ margin: '0 auto 1.5rem auto' }} />
                    <h2 style={{ fontSize: '1.8rem', marginBottom: '1rem', fontWeight: 600 }}>주문 완료!</h2>
                    <p style={{ color: '#64748b', marginBottom: '2rem', lineHeight: '1.6' }}>{message}</p>
                    <button 
                        className="btn-place-order" 
                        onClick={() => navigate('/mypage')}
                    >
                        주문 내역 확인하기
                    </button>
                    <button 
                        style={{ marginTop: '1rem', background: 'transparent', color: '#00d2ff', border: '1px solid #00d2ff' }}
                        className="btn-place-order" 
                        onClick={() => navigate('/')}
                    >
                        메인으로 돌아가기
                    </button>
                </div>
            )}

            {status === 'fail' && (
                <div style={{ textAlign: 'center', maxWidth: '400px' }}>
                    <AlertCircle size={64} color="#ff5252" style={{ margin: '0 auto 1.5rem auto' }} />
                    <h2 style={{ fontSize: '1.8rem', marginBottom: '1rem', fontWeight: 600 }}>결제 실패</h2>
                    <p style={{ color: '#ff5252', marginBottom: '2rem', lineHeight: '1.6' }}>{message}</p>
                    <button 
                        className="btn-place-order" 
                        onClick={() => navigate('/cart')}
                    >
                        장바구니로 돌아가기
                    </button>
                </div>
            )}
        </div>
    );
};

export default PaymentSuccess;
