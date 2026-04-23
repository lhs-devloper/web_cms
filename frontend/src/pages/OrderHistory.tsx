import { useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import { ArrowLeft, Package, X } from 'lucide-react';
import './OrderHistory.css';

const OrderHistory = () => {
    const [orders, setOrders] = useState<any[]>([]);
    const [isLoading, setIsLoading] = useState(true);
    const [selectedOrder, setSelectedOrder] = useState<any>(null);
    const navigate = useNavigate();

    useEffect(() => {
        const fetchOrders = async () => {
            const token = localStorage.getItem('accessToken');
            if (!token) {
                navigate('/login');
                return;
            }
            try {
                const response = await fetch('/api/orders', {
                    headers: { 'Authorization': `Bearer ${token}` }
                });
                if (response.ok) {
                    const data = await response.json();
                    // 최신순 정렬
                    const sortedOrders = data.sort((a: any, b: any) => 
                        new Date(b.createdAt).getTime() - new Date(a.createdAt).getTime()
                    );
                    setOrders(sortedOrders);
                } else {
                    if (response.status === 401) {
                        navigate('/login');
                    }
                }
            } catch (error) {
                console.error("Error fetching orders", error);
            } finally {
                setIsLoading(false);
            }
        };

        fetchOrders();
    }, [navigate]);

    const handleRetryPayment = async (order: any) => {
        const token = localStorage.getItem('accessToken');
        if (!token) { navigate('/login'); return; }

        try {
            // 결제수단 결정 (기존 주문의 결제수단 사용)
            const method = order.paymentMethod || 'CARD';

            // 1. 결제 Ready
            const readyRes = await fetch(`/api/payments/${order.id}/ready`, {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json',
                    'Authorization': `Bearer ${token}`
                },
                body: JSON.stringify({ paymentMethod: method })
            });

            if (!readyRes.ok) {
                const err = await readyRes.json();
                throw new Error(err.message || '결제 초기화에 실패했습니다.');
            }

            // 2. 결제수단별 처리
            if (method === 'TOSS_PAY') {
                localStorage.setItem('checkout_internal_order_id', order.id.toString());

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
                            const orderItems = order.orderItems || [];
                            const orderName = orderItems.length > 1
                                ? `${orderItems[0].productName} 외 ${orderItems.length - 1}건`
                                : orderItems[0]?.productName || '상품';

                            tossPayments.requestPayment('카드', {
                                amount: order.totalPrice,
                                orderId: order.orderNumber,
                                orderName,
                                customerName: order.userName,
                                successUrl: `${window.location.origin}/payment/success`,
                                failUrl: `${window.location.origin}/orders`,
                            }).catch((e: any) => {
                                if (e.code === 'USER_CANCEL') {
                                    alert('결제가 취소되었습니다.');
                                } else {
                                    alert(e.message);
                                }
                            });
                        };
                        document.body.appendChild(tossScript);
                    } else {
                        alert('토스 결제 설정이 반영되지 않았습니다.');
                    }
                }
            } else {
                alert(`[결제 시뮬레이션]\n\n총 금액: ${order.totalPrice.toLocaleString()}원\n결제 수단: ${method}\n\n결제 모듈은 추후 연동될 예정입니다!`);
            }
        } catch (err: any) {
            console.error('Retry Payment Error:', err);
            alert(err.message || '결제 진행 중 오류가 발생했습니다.');
        }
    };

    return (
        <div className="order-history-container container animate-fade-in">
            <div className="order-history-header">
                <button className="back-btn" onClick={() => navigate('/mypage')}>
                    <ArrowLeft size={20} />
                    <span>마이페이지로 돌아가기</span>
                </button>
                <h2>전체 주문 내역</h2>
                <p>지금까지 주문하신 모든 내역을 확인하실 수 있습니다.</p>
            </div>

            <div className="order-history-content">
                {isLoading ? (
                    <div className="loading-state">주문 정보를 불러오는 중입니다...</div>
                ) : orders.length === 0 ? (
                    <div className="empty-state">
                        <Package size={64} color="#94a3b8" />
                        <h3>주문 내역이 없습니다.</h3>
                        <p>새로운 상품을 둘러보고 주문해보세요!</p>
                        <button className="btn-primary" onClick={() => navigate('/products')}>쇼핑하러 가기</button>
                    </div>
                ) : (
                    <div className="full-order-list">
                        {orders.map(order => (
                            <div className="full-order-card glass-panel" key={order.id}>
                                <div className="order-card-header">
                                    <div className="order-date-number">
                                        <span className="order-date">{new Date(order.createdAt).toLocaleDateString()}</span>
                                        <span className="order-number">주문번호: {order.orderNumber || order.id}</span>
                                    </div>
                                    <div className={`order-status-badge ${order.status.toLowerCase()}`}>
                                        {order.status === 'PENDING' ? '결제대기' :
                                         order.status === 'CONFIRMED' ? '결제완료' :
                                         order.status === 'SHIPPED' ? '배송중' :
                                         order.status === 'DELIVERED' ? '배송완료' :
                                         order.status === 'CANCELLED' ? '주문취소' : order.status}
                                    </div>
                                </div>
                                <div className="order-card-body">
                                    <img src={order.orderItems && order.orderItems.length > 0 && order.orderItems[0].imageUrl ? (order.orderItems[0].imageUrl.startsWith('http') ? order.orderItems[0].imageUrl : `http://${window.location.hostname}:8080${order.orderItems[0].imageUrl}`) : "https://via.placeholder.com/150?text=Product"} alt="Order" className="order-image" />
                                    <div className="order-info">
                                        <h4>
                                            {order.orderItems && order.orderItems.length > 0 ? (
                                                order.orderItems.length > 1 ? 
                                                    `${order.orderItems[0].productName} 외 ${order.orderItems.length - 1}건` :
                                                    order.orderItems[0].productName
                                            ) : '상품 정보 없음'}
                                        </h4>
                                        <p className="order-price">결제금액: <strong>₩{order.totalPrice.toLocaleString()}</strong></p>
                                        <p className="order-payment-method">결제수단: {
                                            order.paymentMethod === 'CARD' ? '신용/체크카드' :
                                            order.paymentMethod === 'KAKAO_PAY' ? '카카오페이' :
                                            order.paymentMethod === 'TOSS_PAY' ? '토스페이' :
                                            order.paymentMethod === 'BANK_TRANSFER' ? '계좌이체' : order.paymentMethod
                                        }</p>
                                    </div>
                                    <div className="order-actions">
                                        <button className="btn-secondary" onClick={() => setSelectedOrder(order)}>주문 상세</button>
                                        {order.status === 'PENDING' && <button className="btn-primary-outline" onClick={() => handleRetryPayment(order)}>재결제</button>}
                                        {order.status === 'CONFIRMED' && <button className="btn-danger-outline">주문 취소</button>}
                                        {order.status === 'DELIVERED' && <button className="btn-primary-outline">리뷰 작성</button>}
                                    </div>
                                </div>
                            </div>
                        ))}
                    </div>
                )}
            </div>

            {/* 주문 상세 모달 (MockUp) */}
            {selectedOrder && (
                <div className="order-modal-overlay" onClick={() => setSelectedOrder(null)}>
                    <div className="order-modal-content glass-panel animate-fade-in" onClick={e => e.stopPropagation()}>
                        <div className="order-modal-header">
                            <h3>주문 상세내역</h3>
                            <button className="btn-close-icon" onClick={() => setSelectedOrder(null)}>
                                <X size={24} />
                            </button>
                        </div>
                        <div className="order-modal-body">
                            <div className="detail-section">
                                <h4>주문 정보</h4>
                                <p><strong>주문번호:</strong> {selectedOrder.orderNumber || selectedOrder.id}</p>
                                <p><strong>결제일시:</strong> {new Date(selectedOrder.createdAt).toLocaleString()}</p>
                                <p><strong>주문상태:</strong> <span className={`status-text ${selectedOrder.status.toLowerCase()}`}>{
                                         selectedOrder.status === 'PENDING' ? '결제대기' :
                                         selectedOrder.status === 'CONFIRMED' ? '결제완료' :
                                         selectedOrder.status === 'SHIPPED' ? '배송중' :
                                         selectedOrder.status === 'DELIVERED' ? '배송완료' :
                                         selectedOrder.status === 'CANCELLED' ? '주문취소' : selectedOrder.status
                                }</span></p>
                            </div>
                            
                            <hr className="modal-divider" />

                            <div className="detail-section">
                                <h4>결제 정보</h4>
                                <p><strong>결제수단:</strong> {
                                            selectedOrder.paymentMethod === 'CARD' ? '신용/체크카드' :
                                            selectedOrder.paymentMethod === 'KAKAO_PAY' ? '카카오페이' :
                                            selectedOrder.paymentMethod === 'TOSS_PAY' ? '토스페이' :
                                            selectedOrder.paymentMethod === 'BANK_TRANSFER' ? '계좌이체' : selectedOrder.paymentMethod || '정보없음'
                                }</p>
                                <p><strong>총 결제금액:</strong> <span className="highlight-price">₩{selectedOrder.totalPrice.toLocaleString()}</span></p>
                            </div>

                            <hr className="modal-divider" />

                            <div className="detail-section">
                                <h4>주문 상품 ({selectedOrder.orderItems?.length || 0}개)</h4>
                                <div className="modal-items-list">
                                    {selectedOrder.orderItems?.map((item: any) => (
                                        <div className="modal-item" key={item.id}>
                                            <img src={item.imageUrl ? (item.imageUrl.startsWith('http') ? item.imageUrl : `http://${window.location.hostname}:8080${item.imageUrl}`) : "https://via.placeholder.com/80?text=Product"} alt={item.productName} />
                                            <div className="modal-item-info">
                                                <p className="modal-item-name">{item.productName}</p>
                                                <p className="modal-item-qty">{item.price.toLocaleString()}원 x {item.quantity}개</p>
                                                <p className="modal-item-subtotal">₩{item.subtotal.toLocaleString()}</p>
                                            </div>
                                        </div>
                                    ))}
                                </div>
                            </div>

                            <hr className="modal-divider" />

                            <div className="detail-section mockup-section">
                                <h4>배송지 정보 <span className="mock-badge">Mock</span></h4>
                                <p><strong>수령인:</strong> {selectedOrder.userName}</p>
                                <p><strong>연락처:</strong> 010-1234-5678</p>
                                <p><strong>주소:</strong> 서울특별시 강남구 역삼동 123-45, 프레스티지 빌딩 10층</p>
                                <p><strong>배송메모:</strong> 문 앞에 두고 가주세요.</p>
                            </div>
                        </div>
                        <div className="order-modal-footer">
                            <button className="btn-primary" onClick={() => setSelectedOrder(null)}>확인</button>
                        </div>
                    </div>
                </div>
            )}
        </div>
    );
};

export default OrderHistory;
