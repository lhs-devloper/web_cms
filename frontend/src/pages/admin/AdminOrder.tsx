import { useState, useEffect } from 'react';
import { Package, Truck, X, Search } from 'lucide-react';
import './AdminOrder.css';

interface OrderItem {
    id: number;
    productId: number;
    productName: string;
    imageUrl: string;
    quantity: number;
    price: number;
}

interface Order {
    id: number;
    orderNumber: string;
    userName: string;
    totalPrice: number;
    status: string;
    paymentMethod: string;
    createdAt: string;
    orderItems: OrderItem[];
    paymentStatus: string;
    trackingCompany: string;
    trackingNumber: string;
    trackingMemo: string;
}

const STATUS_OPTIONS = [
    { value: '', label: '전체' },
    { value: 'PENDING', label: '결제대기' },
    { value: 'CONFIRMED', label: '결제완료' },
    { value: 'SHIPPING', label: '배송중' },
    { value: 'DELIVERED', label: '배송완료' },
    { value: 'CANCELLED', label: '취소' },
    { value: 'RETURNED', label: '반품' }
];

const getStatusLabel = (status: string) => {
    const found = STATUS_OPTIONS.find(s => s.value === status);
    return found ? found.label : status;
};

const getStatusClass = (status: string) => {
    return status.toLowerCase();
};

const AdminOrder = () => {
    const [orders, setOrders] = useState<Order[]>([]);
    const [filterStatus, setFilterStatus] = useState('');
    const [searchKeyword, setSearchKeyword] = useState('');
    const [selectedOrder, setSelectedOrder] = useState<Order | null>(null);
    const [newStatus, setNewStatus] = useState('');
    const [trackingCompany, setTrackingCompany] = useState('');
    const [trackingNumber, setTrackingNumber] = useState('');
    const [trackingMemo, setTrackingMemo] = useState('');

    const fetchOrders = async () => {
        try {
            const token = localStorage.getItem('accessToken');
            const headers: any = {};
            if (token) headers['Authorization'] = `Bearer ${token}`;

            const res = await fetch(`/api/admin/orders`, { headers });
            if (res.ok) {
                setOrders(await res.json());
            }
        } catch (err) {
            console.error(err);
        }
    };

    useEffect(() => {
        fetchOrders();
    }, []);

    useEffect(() => {
        const handleEsc = (e: KeyboardEvent) => {
            if (e.key === 'Escape' && selectedOrder) setSelectedOrder(null);
        };
        document.addEventListener('keydown', handleEsc);
        return () => document.removeEventListener('keydown', handleEsc);
    }, [selectedOrder]);

    const openDetail = (order: Order) => {
        setSelectedOrder(order);
        setNewStatus(order.status);
        setTrackingCompany(order.trackingCompany || '');
        setTrackingNumber(order.trackingNumber || '');
        setTrackingMemo(order.trackingMemo || '');
    };

    const handleStatusChange = async () => {
        if (!selectedOrder || !newStatus) return;
        try {
            const token = localStorage.getItem('accessToken');
            const res = await fetch(`/api/admin/orders/${selectedOrder.id}/status`, {
                method: 'PATCH',
                headers: {
                    'Content-Type': 'application/json',
                    ...(token ? { 'Authorization': `Bearer ${token}` } : {})
                },
                body: JSON.stringify({ status: newStatus })
            });
            if (res.ok) {
                alert('주문 상태가 변경되었습니다.');
                fetchOrders();
                setSelectedOrder({ ...selectedOrder, status: newStatus });
            } else {
                alert('상태 변경에 실패했습니다.');
            }
        } catch (err) {
            console.error(err);
            alert('상태 변경 중 오류가 발생했습니다.');
        }
    };

    const handleTrackingSave = async () => {
        if (!selectedOrder) return;
        try {
            const token = localStorage.getItem('accessToken');
            const res = await fetch(`/api/admin/orders/${selectedOrder.id}/tracking`, {
                method: 'PATCH',
                headers: {
                    'Content-Type': 'application/json',
                    ...(token ? { 'Authorization': `Bearer ${token}` } : {})
                },
                body: JSON.stringify({
                    trackingCompany,
                    trackingNumber,
                    trackingMemo
                })
            });
            if (res.ok) {
                alert('배송 정보가 저장되었습니다.');
                fetchOrders();
            } else {
                alert('배송 정보 저장에 실패했습니다.');
            }
        } catch (err) {
            console.error(err);
            alert('배송 정보 저장 중 오류가 발생했습니다.');
        }
    };

    const filteredOrders = orders.filter(order => {
        const matchStatus = !filterStatus || order.status === filterStatus;
        const matchSearch = !searchKeyword || order.orderNumber.toLowerCase().includes(searchKeyword.toLowerCase());
        return matchStatus && matchSearch;
    });

    return (
        <div className="admin-order-container">
            <div className="admin-products-header">
                <div>
                    <h2><Package size={24} style={{ verticalAlign: 'middle', marginRight: '0.5rem' }} />주문 관리</h2>
                    <p className="header-desc">주문 내역을 조회하고 상태를 관리합니다.</p>
                </div>
            </div>

            <div className="order-filter-bar">
                <select value={filterStatus} onChange={e => setFilterStatus(e.target.value)}>
                    {STATUS_OPTIONS.map(opt => (
                        <option key={opt.value} value={opt.value}>{opt.label}</option>
                    ))}
                </select>
                <div style={{ position: 'relative', display: 'flex', alignItems: 'center' }}>
                    <Search size={16} style={{ position: 'absolute', left: '0.8rem', color: 'var(--muted-color)' }} />
                    <input
                        type="text"
                        placeholder="주문번호 검색"
                        value={searchKeyword}
                        onChange={e => setSearchKeyword(e.target.value)}
                        style={{ paddingLeft: '2.5rem' }}
                    />
                </div>
            </div>

            <div className="products-list-section">
                <table className="order-table">
                    <thead>
                        <tr>
                            <th>주문번호</th>
                            <th>주문자</th>
                            <th>금액</th>
                            <th>상태</th>
                            <th>결제수단</th>
                            <th>날짜</th>
                            <th>관리</th>
                        </tr>
                    </thead>
                    <tbody>
                        {filteredOrders.map(order => (
                            <tr key={order.id}>
                                <td style={{ fontWeight: 700, color: 'var(--primary-color)' }}>{order.orderNumber}</td>
                                <td>{order.userName}</td>
                                <td style={{ fontWeight: 600 }}>{order.totalPrice.toLocaleString()}원</td>
                                <td>
                                    <span className={`order-status-badge ${getStatusClass(order.status)}`}>
                                        {getStatusLabel(order.status)}
                                    </span>
                                </td>
                                <td>{order.paymentMethod || '-'}</td>
                                <td>{new Date(order.createdAt).toLocaleDateString()}</td>
                                <td>
                                    <button className="btn-edit-small" onClick={() => openDetail(order)}>
                                        상세
                                    </button>
                                </td>
                            </tr>
                        ))}
                        {filteredOrders.length === 0 && (
                            <tr>
                                <td colSpan={7} className="empty-message">주문 내역이 없습니다.</td>
                            </tr>
                        )}
                    </tbody>
                </table>
            </div>

            {selectedOrder && (
                <div className="order-modal-overlay" onClick={() => setSelectedOrder(null)}>
                    <div className="order-modal-content" onClick={e => e.stopPropagation()}>
                        <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: '1.5rem' }}>
                            <h3 style={{ margin: 0, fontSize: '1.3rem', fontWeight: 700 }}>주문 상세</h3>
                            <button
                                onClick={() => setSelectedOrder(null)}
                                style={{ background: 'none', border: 'none', color: 'var(--text-primary)', cursor: 'pointer' }}
                            >
                                <X size={22} />
                            </button>
                        </div>

                        <div style={{ marginBottom: '1.5rem' }}>
                            <p style={{ margin: '0.3rem 0', color: 'var(--text-color)' }}>
                                <strong>주문번호:</strong> {selectedOrder.orderNumber}
                            </p>
                            <p style={{ margin: '0.3rem 0', color: 'var(--text-color)' }}>
                                <strong>주문일시:</strong> {new Date(selectedOrder.createdAt).toLocaleString()}
                            </p>
                            <p style={{ margin: '0.3rem 0', color: 'var(--text-color)' }}>
                                <strong>현재 상태:</strong>{' '}
                                <span className={`order-status-badge ${getStatusClass(selectedOrder.status)}`}>
                                    {getStatusLabel(selectedOrder.status)}
                                </span>
                            </p>
                        </div>

                        <div style={{ marginBottom: '1.5rem' }}>
                            <h4 style={{ marginBottom: '0.8rem', color: 'var(--text-primary)' }}>주문 상품</h4>
                            {selectedOrder.orderItems && selectedOrder.orderItems.map(item => (
                                <div key={item.id} style={{
                                    display: 'flex',
                                    alignItems: 'center',
                                    gap: '1rem',
                                    padding: '0.8rem',
                                    background: 'var(--bg-darker)',
                                    borderRadius: '8px',
                                    marginBottom: '0.5rem'
                                }}>
                                    <img
                                        src={item.imageUrl ? (item.imageUrl.startsWith('http') ? item.imageUrl : `${item.imageUrl}`) : 'https://via.placeholder.com/60'}
                                        alt={item.productName}
                                        style={{ width: '60px', height: '60px', objectFit: 'cover', borderRadius: '6px' }}
                                    />
                                    <div style={{ flex: 1 }}>
                                        <p style={{ margin: 0, fontWeight: 600, color: 'var(--text-primary)' }}>{item.productName}</p>
                                        <p style={{ margin: '0.2rem 0 0', fontSize: '0.9rem', color: 'var(--muted-color)' }}>
                                            수량: {item.quantity}개 / {item.price.toLocaleString()}원
                                        </p>
                                    </div>
                                </div>
                            ))}
                        </div>

                        <div style={{ marginBottom: '1.5rem' }}>
                            <h4 style={{ marginBottom: '0.8rem', color: 'var(--text-primary)' }}>결제 정보</h4>
                            <p style={{ margin: '0.3rem 0', color: 'var(--text-color)' }}>
                                <strong>결제수단:</strong> {selectedOrder.paymentMethod || '-'}
                            </p>
                            <p style={{ margin: '0.3rem 0', color: 'var(--text-color)' }}>
                                <strong>결제상태:</strong> {selectedOrder.paymentStatus || '-'}
                            </p>
                            <p style={{ margin: '0.3rem 0', color: 'var(--text-color)' }}>
                                <strong>총 금액:</strong> {selectedOrder.totalPrice.toLocaleString()}원
                            </p>
                        </div>

                        <div style={{ marginBottom: '1.5rem' }}>
                            <h4 style={{ marginBottom: '0.8rem', color: 'var(--text-primary)' }}>상태 변경</h4>
                            <div style={{ display: 'flex', gap: '0.8rem', alignItems: 'center' }}>
                                <select
                                    value={newStatus}
                                    onChange={e => setNewStatus(e.target.value)}
                                    style={{
                                        padding: '0.6rem 1rem',
                                        background: 'var(--input-bg)',
                                        border: '1px solid var(--input-border)',
                                        borderRadius: '6px',
                                        color: 'var(--text-primary)',
                                        fontSize: '0.95rem',
                                        flex: 1
                                    }}
                                >
                                    {STATUS_OPTIONS.filter(s => s.value).map(opt => (
                                        <option key={opt.value} value={opt.value}>{opt.label}</option>
                                    ))}
                                </select>
                                <button
                                    onClick={handleStatusChange}
                                    style={{
                                        padding: '0.6rem 1.2rem',
                                        background: 'var(--primary-color)',
                                        color: 'var(--bg-black)',
                                        border: 'none',
                                        borderRadius: '6px',
                                        fontWeight: 700,
                                        cursor: 'pointer'
                                    }}
                                >
                                    변경
                                </button>
                            </div>
                        </div>

                        <div className="tracking-section">
                            <h4><Truck size={18} /> 배송 정보</h4>
                            <div style={{ display: 'flex', flexDirection: 'column', gap: '0.8rem' }}>
                                <input
                                    type="text"
                                    placeholder="택배사"
                                    value={trackingCompany}
                                    onChange={e => setTrackingCompany(e.target.value)}
                                    style={{
                                        padding: '0.6rem 1rem',
                                        background: 'var(--input-bg)',
                                        border: '1px solid var(--input-border)',
                                        borderRadius: '6px',
                                        color: 'var(--text-primary)',
                                        fontSize: '0.95rem'
                                    }}
                                />
                                <input
                                    type="text"
                                    placeholder="운송장번호"
                                    value={trackingNumber}
                                    onChange={e => setTrackingNumber(e.target.value)}
                                    style={{
                                        padding: '0.6rem 1rem',
                                        background: 'var(--input-bg)',
                                        border: '1px solid var(--input-border)',
                                        borderRadius: '6px',
                                        color: 'var(--text-primary)',
                                        fontSize: '0.95rem'
                                    }}
                                />
                                <textarea
                                    placeholder="메모"
                                    value={trackingMemo}
                                    onChange={e => setTrackingMemo(e.target.value)}
                                    rows={3}
                                    style={{
                                        padding: '0.6rem 1rem',
                                        background: 'var(--input-bg)',
                                        border: '1px solid var(--input-border)',
                                        borderRadius: '6px',
                                        color: 'var(--text-primary)',
                                        fontSize: '0.95rem',
                                        resize: 'vertical'
                                    }}
                                />
                                <button
                                    onClick={handleTrackingSave}
                                    style={{
                                        padding: '0.6rem 1.2rem',
                                        background: '#4f46e5',
                                        color: '#fff',
                                        border: 'none',
                                        borderRadius: '6px',
                                        fontWeight: 700,
                                        cursor: 'pointer',
                                        alignSelf: 'flex-end'
                                    }}
                                >
                                    배송 정보 저장
                                </button>
                            </div>
                        </div>
                    </div>
                </div>
            )}
        </div>
    );
};

export default AdminOrder;
