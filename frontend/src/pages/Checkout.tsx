import React, { useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import './Checkout.css';

interface CartItem {
    id: number;
    productId: number;
    productName: string;
    productPrice: number;
    productType: string;
    imageUrl: string;
    quantity: number;
    rentalStartDate: string;
    rentalEndDate: string;
}

const ALL_PAYMENT_METHODS = [
    { id: 'CARD', name: '신용/체크카드', icon: '💳', provider: 'KCP' },
    { id: 'KAKAO_PAY', name: '카카오페이', icon: '🟡', provider: 'KAKAOPAY' },
    { id: 'TOSS_PAY', name: '토스페이', icon: '🔵', provider: 'TOSSPAY' },
    { id: 'BANK_TRANSFER', name: '실시간 계좌이체', icon: '🏦', provider: 'KCP' }
];

const Checkout: React.FC = () => {
    const [items, setItems] = useState<CartItem[]>([]);
    const [selectedMethod, setSelectedMethod] = useState<string>('');
    const [activeProviders, setActiveProviders] = useState<string[]>([]);
    const [isLoading, setIsLoading] = useState(false);
    const navigate = useNavigate();

    useEffect(() => {
        fetchCartItems();
        fetchActiveConfigs();
    }, []);

    const fetchCartItems = async () => {
        try {
            const token = localStorage.getItem('accessToken');
            const res = await fetch('/api/cart', {
                headers: token ? { 'Authorization': `Bearer ${token}` } : {}
            });
            if (res.status === 401) {
                navigate('/login');
                return;
            }
            const data = await res.json();
            setItems(data);
            if (data.length === 0) {
                alert('결제할 상품이 없습니다.');
                navigate('/cart');
            }
        } catch (err) {
            console.error(err);
        }
    };

    const fetchActiveConfigs = async () => {
        try {
            const token = localStorage.getItem('accessToken');
            const res = await fetch('/api/payments/configs', {
                headers: token ? { 'Authorization': `Bearer ${token}` } : {}
            });
            if (res.ok) {
                const configs: { provider: string }[] = await res.json();
                const providers = configs.map(c => c.provider);
                setActiveProviders(providers);

                // 활성 PG 로드 후 최근 결제수단 조회
                await fetchLastPaymentMethod(providers);
            }
        } catch (err) {
            console.error(err);
        }
    };

    const fetchLastPaymentMethod = async (providers: string[]) => {
        try {
            const token = localStorage.getItem('accessToken');
            if (!token) return;
            const res = await fetch('/api/payments/last-method', {
                headers: { 'Authorization': `Bearer ${token}` }
            });
            if (res.ok) {
                const data = await res.json();
                if (data.paymentMethod) {
                    const method = ALL_PAYMENT_METHODS.find(m => m.id === data.paymentMethod);
                    if (method && providers.includes(method.provider)) {
                        setSelectedMethod(data.paymentMethod);
                        return;
                    }
                }
            }
        } catch (err) {
            // 실패 시 아래 fallback
        }
        // fallback: 첫 번째 활성 결제수단 선택
        const first = ALL_PAYMENT_METHODS.find(m => providers.includes(m.provider));
        if (first) setSelectedMethod(first.id);
    };

    const calculateTotal = () => {
        return items.reduce((sum: number, item: CartItem) => sum + (item.productPrice * item.quantity), 0);
    };

    const handlePlaceOrder = async () => {
        if (items.length === 0) return;
        
        setIsLoading(true);

        try {
            const token = localStorage.getItem('accessToken');

            // 1. 주문 생성 API 호출
            const orderRes = await fetch('/api/orders', {
                method: 'POST',
                headers: { 
                    'Content-Type': 'application/json',
                    'Authorization': `Bearer ${token}` 
                },
                body: JSON.stringify({
                    cartItemIds: items.map((item: CartItem) => item.id),
                    paymentMethod: selectedMethod
                })
            });

            if (!orderRes.ok) {
                const err = await orderRes.json();
                throw new Error(err.message || '주문 생성에 실패했습니다.');
            }

            const orderConfig = await orderRes.json(); 
            // orderConfig는 생성된 OrderDto 객체임
            
            // 2. 결제 Ready 상태 변경 API
            const readyRes = await fetch(`/api/payments/${orderConfig.id}/ready`, {
                method: 'POST',
                headers: { 
                    'Content-Type': 'application/json',
                    'Authorization': `Bearer ${token}` 
                },
                body: JSON.stringify({ paymentMethod: selectedMethod })
            });
            
            if (!readyRes.ok) {
                let errMessage = '결제 초기화에 실패했습니다.';
                try {
                    const errData = await readyRes.json();
                    if (errData.message) errMessage += ` 상세: ${errData.message}`;
                } catch (e) {}
                throw new Error(errMessage);
            }
            
            const readyData = await readyRes.json();

            // 3. 결제 수단별 연동 처리
            // 백엔드 order ID 캐싱 (redirect 후 복귀를 위해)
            localStorage.setItem('checkout_internal_order_id', orderConfig.id.toString());

            if (selectedMethod === 'TOSS_PAY') {
                // 토스페이먼츠 클라이언트 SDK 연동
                const configRes = await fetch('/api/payments/configs', {
                    headers: { 'Authorization': `Bearer ${token}` }
                });
                if (configRes.ok) {
                    const configs = await configRes.json();
                    const tossConfig = configs.find((c: any) => c.provider === 'TOSSPAY');

                    if (tossConfig && tossConfig.clientKey) {
                        const tossScript = document.createElement('script');
                        tossScript.src = "https://js.tosspayments.com/v1/payment";
                        tossScript.onload = () => {
                            // @ts-ignore
                            const tossPayments = window.TossPayments(tossConfig.clientKey);
                            tossPayments.requestPayment('카드', {
                                amount: orderConfig.totalPrice,
                                orderId: orderConfig.orderNumber,
                                orderName: items.length > 1 ? `${items[0].productName} 외 ${items.length - 1}건` : items[0].productName,
                                customerName: orderConfig.userName,
                                successUrl: `${window.location.origin}/payment/success`,
                                failUrl: `${window.location.origin}/cart`,
                            }).catch((e: any) => {
                                if (e.code === 'USER_CANCEL') {
                                    alert('결제가 취소되었습니다.');
                                } else {
                                    alert(e.message);
                                }
                                setIsLoading(false);
                            });
                        };
                        document.body.appendChild(tossScript);
                    } else {
                        alert('토스 결제 설정(Client Key)이 반영되지 않았습니다. 관리자 페이지를 확인하세요.');
                        setIsLoading(false);
                    }
                } else {
                    alert('결제 설정 정보를 불러오지 못했습니다.');
                    setIsLoading(false);
                }
            } else if (selectedMethod === 'KAKAO_PAY') {
                // 카카오페이: 백엔드에서 redirectUrl 반환 → 카카오 결제 페이지로 이동
                if (readyData.redirectUrl) {
                    window.location.href = readyData.redirectUrl;
                } else {
                    // 키 미설정 시 mock 콜백으로 approve 처리
                    const approveRes = await fetch(`/api/payments/${orderConfig.id}/approve`, {
                        method: 'POST',
                        headers: { 'Content-Type': 'application/json', 'Authorization': `Bearer ${token}` },
                        body: JSON.stringify({ pgToken: readyData.pgTransactionId })
                    });
                    if (approveRes.ok) {
                        navigate('/payment/success?mock=true');
                    } else {
                        const errData = await approveRes.json();
                        alert('결제 승인 실패: ' + (errData.message || '알 수 없는 오류'));
                    }
                    setIsLoading(false);
                }
            } else {
                // KCP (CARD, BANK_TRANSFER): 백엔드에서 redirectUrl 반환 → KCP 결제창 이동
                if (readyData.redirectUrl && !readyData.redirectUrl.startsWith('/api/')) {
                    window.location.href = readyData.redirectUrl;
                } else {
                    // 키 미설정 시 mock 콜백으로 approve 처리
                    const approveRes = await fetch(`/api/payments/${orderConfig.id}/approve`, {
                        method: 'POST',
                        headers: { 'Content-Type': 'application/json', 'Authorization': `Bearer ${token}` },
                        body: JSON.stringify({ pgToken: readyData.pgTransactionId })
                    });
                    if (approveRes.ok) {
                        navigate('/payment/success?mock=true');
                    } else {
                        const errData = await approveRes.json();
                        alert('결제 승인 실패: ' + (errData.message || '알 수 없는 오류'));
                    }
                    setIsLoading(false);
                }
            }
        } catch (err: any) {
            console.error('Order/Payment Error:', err);
            alert(err.message || '결제 진행 중 오류가 발생했습니다.');
            setIsLoading(false);
        }
    };

    const paymentMethods = ALL_PAYMENT_METHODS.filter(m => activeProviders.includes(m.provider));

    return (
        <div className="container checkout-container">
            <div className="checkout-main">
                <div className="checkout-section">
                    <h3 className="checkout-section-title">주문 상품 정보</h3>
                    <div className="checkout-item-list">
                        {items.map((item: CartItem) => (
                            <div className="checkout-item" key={item.id}>
                                <img src={item.imageUrl || 'https://via.placeholder.com/60'} alt={item.productName} className="checkout-item-img" />
                                <div className="checkout-item-info">
                                    <div className="checkout-item-name">{item.productName}</div>
                                    <div className="checkout-item-meta">
                                        수량: {item.quantity}개 | {item.rentalStartDate ? `대여 (${item.rentalStartDate} ~ ${item.rentalEndDate})` : '구매'}
                                    </div>
                                </div>
                                <div className="checkout-item-price">
                                    {(item.productPrice * item.quantity).toLocaleString()}원
                                </div>
                            </div>
                        ))}
                    </div>
                </div>

                <div className="checkout-section">
                    <h3 className="checkout-section-title">결제 수단 선택</h3>
                    {paymentMethods.length > 0 ? (
                        <div className="payment-methods">
                            {paymentMethods.map(method => (
                                <div
                                    key={method.id}
                                    className={`payment-method-card ${selectedMethod === method.id ? 'selected' : ''}`}
                                    onClick={() => setSelectedMethod(method.id)}
                                >
                                    <div className="payment-method-icon">{method.icon}</div>
                                    <div>{method.name}</div>
                                </div>
                            ))}
                        </div>
                    ) : (
                        <div className="no-payment-methods">
                            현재 이용 가능한 결제 수단이 없습니다. 관리자에게 문의해주세요.
                        </div>
                    )}
                </div>
            </div>

            <div className="checkout-sidebar">
                <h3 className="checkout-section-title">결제 금액</h3>
                <div className="summary-row">
                    <span>총 상품금액</span>
                    <span>{calculateTotal().toLocaleString()}원</span>
                </div>
                <div className="summary-row">
                    <span>배송비</span>
                    <span>0원</span> {/* 임시 */}
                </div>
                <div className="summary-total">
                    <span>최종 결제금액</span>
                    <span>{calculateTotal().toLocaleString()}원</span>
                </div>
                
                <button 
                    className="btn-place-order" 
                    onClick={handlePlaceOrder}
                    disabled={isLoading || items.length === 0 || !selectedMethod}
                >
                    {isLoading ? '결제 처리중...' : '결제하기'}
                </button>
            </div>
        </div>
    );
};

export default Checkout;
