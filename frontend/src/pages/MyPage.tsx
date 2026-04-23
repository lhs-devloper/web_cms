import React, { useState, useEffect } from 'react';
import ReactDOM from 'react-dom';
import { useNavigate } from 'react-router-dom';
import { Camera, Mail, Shield, CreditCard, Package, LogOut, Award } from 'lucide-react';
import './MyPage.css';

const MyPage = () => {
    const [user, setUser] = useState({
        name: '로딩중...',
        email: '로딩중...',
        profileImage: 'https://via.placeholder.com/150',
        grade: '일반',
        points: 0
    });

    const [isEditModalOpen, setIsEditModalOpen] = useState(false);
    const [editName, setEditName] = useState('');
    const [passwords, setPasswords] = useState({
        currentPassword: '',
        newPassword: '',
        confirmPassword: ''
    });
    const [orders, setOrders] = useState<any[]>([]);
    const [retryOrder, setRetryOrder] = useState<any>(null);
    const [retryMethod, setRetryMethod] = useState<string>('');
    const [activeProviders, setActiveProviders] = useState<string[]>([]);
    const [isRetrying, setIsRetrying] = useState(false);
    const [membership, setMembership] = useState<any>(null);
    const navigate = useNavigate();

    useEffect(() => {
        const fetchUserProfile = async () => {
            const token = localStorage.getItem('accessToken');
            if (!token) {
                window.location.href = '/login';
                return;
            }

            try {
                const response = await fetch(`/api/profile`, {
                    headers: {
                        'Authorization': `Bearer ${token}`
                    }
                });

                if (response.ok) {
                    const data = await response.json();
                    const picture = data.user.picture;
                    const profileImageUrl = picture
                        ? (picture.startsWith('http') ? picture : `${picture}`)
                        : 'https://via.placeholder.com/150';

                    setUser({
                        name: data.user.name,
                        email: data.user.email,
                        profileImage: profileImageUrl,
                        grade: data.user.role === 'ROLE_ADMIN' ? '관리자' : '일반',
                        points: 0 // 임시 포인트 (DB에 별도 point 칼럼 추가 시 매핑 가능)
                    });
                } else {
                    console.error('Failed to fetch profile', response.status);
                    if (response.status === 401 || response.status === 403) {
                        localStorage.removeItem('accessToken');
                        window.location.href = '/login';
                    }
                }
            } catch (error) {
                console.error("Error fetching profile", error);
            }
        };

        const fetchOrders = async () => {
            const token = localStorage.getItem('accessToken');
            if (!token) return;
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
                }
            } catch (error) {
                console.error("Error fetching orders", error);
            }
        };

        const fetchMembership = async () => {
            const token = localStorage.getItem('accessToken');
            if (!token) return;
            try {
                const res = await fetch(`/api/membership/my`, {
                    headers: { 'Authorization': `Bearer ${token}` }
                });
                if (res.ok) setMembership(await res.json());
            } catch (err) {
                console.error(err);
            }
        };

        fetchUserProfile();
        fetchOrders();
        fetchActiveConfigs();
        fetchMembership();
    }, []);

    const ALL_PAYMENT_METHODS = [
        { id: 'CARD', name: '신용/체크카드', icon: '💳', provider: 'KCP' },
        { id: 'KAKAO_PAY', name: '카카오페이', icon: '🟡', provider: 'KAKAOPAY' },
        { id: 'TOSS_PAY', name: '토스페이', icon: '🔵', provider: 'TOSSPAY' },
        { id: 'BANK_TRANSFER', name: '실시간 계좌이체', icon: '🏦', provider: 'KCP' }
    ];

    const fetchActiveConfigs = async () => {
        try {
            const token = localStorage.getItem('accessToken');
            const res = await fetch('/api/payments/configs', {
                headers: token ? { 'Authorization': `Bearer ${token}` } : {}
            });
            if (res.ok) {
                const configs: { provider: string }[] = await res.json();
                setActiveProviders(configs.map(c => c.provider));
            }
        } catch (err) {
            console.error(err);
        }
    };

    const openRetryModal = (order: any) => {
        if (order.status !== 'PENDING') return;
        setRetryOrder(order);
        // 기존 결제수단이 활성 PG에 포함되면 기본 선택, 아니면 첫 번째 활성 수단
        const method = ALL_PAYMENT_METHODS.find(m => m.id === order.paymentMethod && activeProviders.includes(m.provider));
        const fallback = ALL_PAYMENT_METHODS.find(m => activeProviders.includes(m.provider));
        setRetryMethod(method?.id || fallback?.id || '');
    };

    const handleRetryPayment = async () => {
        if (!retryOrder || !retryMethod) return;
        const token = localStorage.getItem('accessToken');
        if (!token) { navigate('/login'); return; }

        setIsRetrying(true);
        try {
            const readyRes = await fetch(`/api/payments/${retryOrder.id}/ready`, {
                method: 'POST',
                headers: { 'Content-Type': 'application/json', 'Authorization': `Bearer ${token}` },
                body: JSON.stringify({ paymentMethod: retryMethod })
            });
            if (!readyRes.ok) {
                const err = await readyRes.json();
                throw new Error(err.message || '결제 초기화에 실패했습니다.');
            }

            if (retryMethod === 'TOSS_PAY') {
                localStorage.setItem('checkout_internal_order_id', retryOrder.id.toString());
                const configRes = await fetch('/api/payments/configs', { headers: { 'Authorization': `Bearer ${token}` } });
                if (configRes.ok) {
                    const configs = await configRes.json();
                    const tossConfig = configs.find((c: any) => c.provider === 'TOSSPAY');
                    if (tossConfig?.clientKey) {
                        const tossScript = document.createElement('script');
                        tossScript.src = 'https://js.tosspayments.com/v1/payment';
                        tossScript.onload = () => {
                            // @ts-ignore
                            const tossPayments = window.TossPayments(tossConfig.clientKey);
                            const items = retryOrder.orderItems || [];
                            const orderName = items.length > 1 ? `${items[0].productName} 외 ${items.length - 1}건` : items[0]?.productName || '상품';
                            tossPayments.requestPayment('카드', {
                                amount: retryOrder.totalPrice,
                                orderId: retryOrder.orderNumber,
                                orderName,
                                customerName: retryOrder.userName,
                                successUrl: `${window.location.origin}/payment/success`,
                                failUrl: `${window.location.origin}/mypage`,
                            }).catch((e: any) => {
                                if (e.code !== 'USER_CANCEL') alert(e.message);
                                setIsRetrying(false);
                            });
                        };
                        document.body.appendChild(tossScript);
                    } else {
                        alert('토스 결제 설정이 반영되지 않았습니다.');
                        setIsRetrying(false);
                    }
                }
            } else if (retryMethod === 'KAKAO_PAY') {
                const readyData = await readyRes.json();
                localStorage.setItem('checkout_internal_order_id', retryOrder.id.toString());
                if (readyData.redirectUrl) {
                    window.location.href = readyData.redirectUrl;
                } else {
                    const approveRes = await fetch(`/api/payments/${retryOrder.id}/approve`, {
                        method: 'POST',
                        headers: { 'Content-Type': 'application/json', 'Authorization': `Bearer ${token}` },
                        body: JSON.stringify({ pgToken: readyData.pgTransactionId })
                    });
                    if (approveRes.ok) {
                        window.location.href = '/payment/success?mock=true';
                    } else {
                        const errData = await approveRes.json();
                        alert('결제 승인 실패: ' + (errData.message || '알 수 없는 오류'));
                        setIsRetrying(false);
                    }
                }
            } else {
                // KCP (CARD, BANK_TRANSFER)
                const readyData = await readyRes.json();
                localStorage.setItem('checkout_internal_order_id', retryOrder.id.toString());
                if (readyData.redirectUrl && !readyData.redirectUrl.startsWith('/api/')) {
                    window.location.href = readyData.redirectUrl;
                } else {
                    const approveRes = await fetch(`/api/payments/${retryOrder.id}/approve`, {
                        method: 'POST',
                        headers: { 'Content-Type': 'application/json', 'Authorization': `Bearer ${token}` },
                        body: JSON.stringify({ pgToken: readyData.pgTransactionId })
                    });
                    if (approveRes.ok) {
                        window.location.href = '/payment/success?mock=true';
                    } else {
                        const errData = await approveRes.json();
                        alert('결제 승인 실패: ' + (errData.message || '알 수 없는 오류'));
                        setIsRetrying(false);
                    }
                }
            }
        } catch (err: any) {
            console.error('Retry Payment Error:', err);
            alert(err.message || '결제 진행 중 오류가 발생했습니다.');
            setIsRetrying(false);
        }
    };

    const handleLogout = () => {
        localStorage.removeItem('accessToken');
        window.location.href = '/';
    };

    const fileInputRef = React.useRef<HTMLInputElement>(null);

    const handleImageUpload = async (e: React.ChangeEvent<HTMLInputElement>) => {
        const file = e.target.files?.[0];
        if (!file) return;

        const formData = new FormData();
        formData.append('file', file);
        formData.append('name', user.name);

        try {
            const token = localStorage.getItem('accessToken');
            const response = await fetch(`/api/profile/update`, {
                method: 'POST',
                headers: {
                    'Authorization': `Bearer ${token}`
                },
                body: formData
            });

            if (response.ok) {
                // To fetch the newly assigned uploaded url we just re-fetch profile data or update locally if exact response is given.
                // We will do a full refresh to get the latest updated data
                window.location.reload();
            } else {
                alert('프로필 이미지 변경에 실패했습니다.');
            }
        } catch (error) {
            console.error('Error uploading image', error);
            alert('업로드 중 오류가 발생했습니다.');
        }
    };

    const handleCameraClick = () => {
        fileInputRef.current?.click();
    };

    useEffect(() => {
        const handleEsc = (e: KeyboardEvent) => {
            if (e.key === 'Escape') {
                if (retryOrder) setRetryOrder(null);
                else if (isEditModalOpen) setIsEditModalOpen(false);
            }
        };
        document.addEventListener('keydown', handleEsc);
        return () => document.removeEventListener('keydown', handleEsc);
    }, [isEditModalOpen, retryOrder]);

    const handleOpenEditModal = () => {
        setEditName(user.name);
        setPasswords({ currentPassword: '', newPassword: '', confirmPassword: '' });
        setIsEditModalOpen(true);
    };

    const handleProfileUpdate = async (e: React.FormEvent) => {
        e.preventDefault();
        const formData = new FormData();
        formData.append('name', editName);

        try {
            const token = localStorage.getItem('accessToken');
            const response = await fetch(`/api/profile/update`, {
                method: 'POST',
                headers: { 'Authorization': `Bearer ${token}` },
                body: formData
            });

            if (response.ok) {
                alert('프로필이 업데이트되었습니다.');
                setUser({ ...user, name: editName });
            } else {
                alert('프로필 업데이트에 실패했습니다.');
            }
        } catch (error) {
            console.error('Update error', error);
        }
    };

    const handlePasswordUpdate = async (e: React.FormEvent) => {
        e.preventDefault();
        if (passwords.newPassword !== passwords.confirmPassword) {
            alert('새 비밀번호가 일치하지 않습니다.');
            return;
        }

        const formData = new FormData();
        formData.append('currentPassword', passwords.currentPassword);
        formData.append('newPassword', passwords.newPassword);
        formData.append('confirmPassword', passwords.confirmPassword);

        try {
            const token = localStorage.getItem('accessToken');
            const response = await fetch('/api/profile/password', {
                method: 'POST',
                headers: { 'Authorization': `Bearer ${token}` },
                body: formData
            });

            if (response.ok) {
                alert('비밀번호가 변경되었습니다. 다시 로그인해주세요.');
                handleLogout();
            } else {
                const data = await response.json();
                alert(data.message || '비밀번호 변경에 실패했습니다.');
            }
        } catch (error) {
            console.error('Password update error', error);
        }
    };

    return (
        <div className="mypage-container container animate-fade-in">
            <div className="mypage-header">
                <h2>My Account</h2>
                <p>프리미엄 멤버십 대시보드</p>
            </div>

            <div className="mypage-content">
                {/* 좌측: 프로필 카드 */}
                <aside className="profile-card glass-panel">
                    <div className="profile-image-wrapper">
                        <img src={user.profileImage} alt="Profile" className="profile-image" />
                        <button className="camera-btn" aria-label="Change profile image" onClick={handleCameraClick}>
                            <Camera size={16} />
                        </button>
                        <input
                            type="file"
                            ref={fileInputRef}
                            style={{ display: 'none' }}
                            accept="image/*"
                            onChange={handleImageUpload}
                        />
                    </div>

                    <h3 className="profile-name">{user.name}</h3>
                    <p className="profile-email"><Mail size={14} /> {user.email}</p>

                    <div className="profile-stats">
                        <div className="stat-box">
                            <span className="stat-label">Membership</span>
                            <span className="stat-value grade-vip">{membership?.grade?.name || user.grade}</span>
                        </div>
                        <div className="stat-box">
                            <span className="stat-label">Points</span>
                            <span className="stat-value">{membership?.availablePoints?.toLocaleString() || user.points.toLocaleString()}P</span>
                        </div>
                    </div>
                    <button
                        className="btn-logout"
                        style={{ background: 'var(--primary-color)', color: 'var(--bg-black)', marginBottom: '0.5rem' }}
                        onClick={() => navigate('/membership')}
                    >
                        <Award size={18} /> 멤버십 혜택 보기
                    </button>

                    <button className="btn-logout" onClick={handleLogout}>
                        <LogOut size={18} /> 로그아웃
                    </button>
                </aside>

                {/* 우측: 메뉴 및 활동 영역 */}
                <main className="mypage-main">
                    <div className="menu-grid">
                        <div className="menu-card glass-panel cursor-pointer">
                            <div className="menu-icon-wrapper">
                                <Package size={24} />
                            </div>
                            <div className="menu-info">
                                <h4>주문/배송 조회</h4>
                                <p>총 주문 {orders.length}건</p>
                            </div>
                        </div>

                        <div className="menu-card glass-panel cursor-pointer">
                            <div className="menu-icon-wrapper">
                                <CreditCard size={24} />
                            </div>
                            <div className="menu-info">
                                <h4>결제 수단 관리</h4>
                                <p>등록된 카드 1개</p>
                            </div>
                        </div>

                        <div className="menu-card glass-panel cursor-pointer" onClick={handleOpenEditModal}>
                            <div className="menu-icon-wrapper">
                                <Shield size={24} />
                            </div>
                            <div className="menu-info">
                                <h4>개인정보 수정</h4>
                                <p>비밀번호 및 연락처 변경</p>
                            </div>
                        </div>
                    </div>

                    <div className="recent-orders glass-panel">
                        <div className="section-title-row">
                            <h3>최근 주문 내역</h3>
                            {orders.length > 0 && <button className="btn-text" onClick={() => navigate('/orders')}>전체보기</button>}
                        </div>

                        <div className="order-list">
                            {orders.length === 0 ? (
                                <div style={{ textAlign: 'center', padding: '3rem', color: '#94a3b8' }}>
                                    최근 주문 내역이 없습니다.
                                </div>
                            ) : (
                                orders.slice(0, 5).map(order => (
                                    <div
                                        className={`order-item ${order.status === 'PENDING' ? 'order-item-pending' : ''}`}
                                        key={order.id}
                                        onClick={() => order.status === 'PENDING' && openRetryModal(order)}
                                    >
                                        <img src={order.orderItems && order.orderItems.length > 0 && order.orderItems[0].imageUrl ? (order.orderItems[0].imageUrl.startsWith('http') ? order.orderItems[0].imageUrl : `${order.orderItems[0].imageUrl}`) : "https://via.placeholder.com/100?text=Order"} alt="product" />
                                        <div className="order-details">
                                            <span className="order-date">{new Date(order.createdAt).toLocaleDateString()}</span>
                                            <h4>
                                                {order.orderItems && order.orderItems.length > 0 ? (
                                                    order.orderItems.length > 1 ?
                                                        `${order.orderItems[0].productName} 외 ${order.orderItems.length - 1}건` :
                                                        order.orderItems[0].productName
                                                ) : '상품 정보 없음'}
                                            </h4>
                                            <p className="order-price">₩{order.totalPrice.toLocaleString()}</p>
                                        </div>
                                        <div className={`order-status ${order.status.toLowerCase()}`}>
                                            {order.status === 'PENDING' ? '결제대기' :
                                             order.status === 'CONFIRMED' ? '결제완료' :
                                             order.status === 'SHIPPED' ? '배송중' :
                                             order.status === 'DELIVERED' ? '배송완료' :
                                             order.status === 'CANCELLED' ? '주문취소' : order.status}
                                        </div>
                                    </div>
                                ))
                            )}
                        </div>
                    </div>
                </main>
            </div>

            {retryOrder && ReactDOM.createPortal(
                <div className="modal-overlay" onClick={() => { setRetryOrder(null); setIsRetrying(false); }}>
                    <div className="modal-content glass-panel animate-fade-in" onClick={e => e.stopPropagation()}>
                        <h3>결제하기</h3>
                        <div className="retry-order-summary">
                            <p className="retry-order-number">주문번호: {retryOrder.orderNumber}</p>
                            <p className="retry-order-total">결제금액: <strong>₩{retryOrder.totalPrice.toLocaleString()}</strong></p>
                        </div>

                        <div className="retry-method-section">
                            <h4>결제 수단 선택</h4>
                            <div className="retry-method-grid">
                                {ALL_PAYMENT_METHODS.filter(m => activeProviders.includes(m.provider)).map(method => (
                                    <div
                                        key={method.id}
                                        className={`retry-method-card ${retryMethod === method.id ? 'selected' : ''}`}
                                        onClick={() => setRetryMethod(method.id)}
                                    >
                                        <span className="retry-method-icon">{method.icon}</span>
                                        <span>{method.name}</span>
                                    </div>
                                ))}
                            </div>
                            {ALL_PAYMENT_METHODS.filter(m => activeProviders.includes(m.provider)).length === 0 && (
                                <p style={{ textAlign: 'center', color: '#94a3b8', padding: '1rem 0' }}>이용 가능한 결제 수단이 없습니다.</p>
                            )}
                        </div>

                        <button
                            className="btn-primary"
                            onClick={handleRetryPayment}
                            disabled={isRetrying || !retryMethod}
                        >
                            {isRetrying ? '결제 처리중...' : '결제하기'}
                        </button>
                        <button className="btn-close" onClick={() => { setRetryOrder(null); setIsRetrying(false); }}>취소</button>
                    </div>
                </div>,
                document.body
            )}

            {isEditModalOpen && ReactDOM.createPortal(
                <div className="modal-overlay">
                    <div className="modal-content glass-panel animate-fade-in">
                        <h3>개인정보 수정</h3>

                        <form onSubmit={handleProfileUpdate} className="edit-form">
                            <h4>프로필 정보 변경</h4>
                            <div className="input-group">
                                <label>이름</label>
                                <input
                                    type="text"
                                    value={editName}
                                    onChange={(e) => setEditName(e.target.value)}
                                />
                            </div>
                            <button type="submit" className="btn-primary">이름 변경</button>
                        </form>

                        <hr className="modal-divider" />

                        <form onSubmit={handlePasswordUpdate} className="edit-form">
                            <h4>비밀번호 변경</h4>
                            <div className="input-group">
                                <label>현재 비밀번호</label>
                                <input
                                    type="password"
                                    value={passwords.currentPassword}
                                    onChange={(e) => setPasswords({ ...passwords, currentPassword: e.target.value })}
                                />
                            </div>
                            <div className="input-group">
                                <label>새 비밀번호</label>
                                <input
                                    type="password"
                                    value={passwords.newPassword}
                                    onChange={(e) => setPasswords({ ...passwords, newPassword: e.target.value })}
                                />
                            </div>
                            <div className="input-group">
                                <label>새 비밀번호 확인</label>
                                <input
                                    type="password"
                                    value={passwords.confirmPassword}
                                    onChange={(e) => setPasswords({ ...passwords, confirmPassword: e.target.value })}
                                />
                            </div>
                            <button type="submit" className="btn-primary">비밀번호 변경</button>
                        </form>

                        <button className="btn-close" onClick={() => setIsEditModalOpen(false)}>닫기</button>
                    </div>
                </div>,
                document.body
            )}
        </div>
    );
};

export default MyPage;
