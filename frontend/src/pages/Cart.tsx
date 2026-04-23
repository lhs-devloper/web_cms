import React, { useState, useEffect } from 'react';
import { Link, useNavigate } from 'react-router-dom';
import { Trash2, Plus, Minus, CreditCard, ShoppingBag, Calendar, ShieldCheck } from 'lucide-react';
import './Cart.css';

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

const Cart: React.FC = () => {
    const [items, setItems] = useState<CartItem[]>([]);
    const [isLoading, setIsLoading] = useState(true);
    const navigate = useNavigate();

    useEffect(() => {
        fetchCartItems();
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
        } catch (err) {
            console.error(err);
        } finally {
            setIsLoading(false);
        }
    };

    const updateQuantity = async (id: number, quantity: number) => {
        if (quantity < 1) return;
        try {
            await fetch(`/api/cart/${id}`, {
                method: 'PUT',
                headers: {
                    'Content-Type': 'application/json',
                    ...(localStorage.getItem('accessToken') ? { 'Authorization': `Bearer ${localStorage.getItem('accessToken')}` } : {})
                },
                body: JSON.stringify({ quantity })
            });
            fetchCartItems();
        } catch (err) {
            console.error(err);
        }
    };

    const removeItem = async (id: number) => {
        if (!confirm('장바구니에서 삭제하시겠습니까?')) return;
        try {
            await fetch(`/api/cart/${id}`, {
                method: 'DELETE',
                headers: localStorage.getItem('accessToken') ? { 'Authorization': `Bearer ${localStorage.getItem('accessToken')}` } : {}
            });
            fetchCartItems();
        } catch (err) {
            console.error(err);
        }
    };

    const calculateTotal = () => {
        return items.reduce((sum, item) => sum + (item.productPrice * item.quantity), 0);
    };

    if (isLoading) {
        return <div className="container" style={{ padding: '40px 20px', textAlign: 'center' }}>불러오는 중...</div>;
    }

    return (
        <div className="container cart-container">
            <div className="cart-header">
                <h2>나의 장바구니</h2>
            </div>

            {items.length === 0 ? (
                <div className="empty-cart">
                    <ShoppingBag size={64} className="empty-cart-icon" />
                    <p>장바구니에 담긴 상품이 없습니다.</p>
                    <Link to="/products" className="btn-continue">
                        쇼핑 계속하기
                    </Link>
                </div>
            ) : (
                <div className="cart-layout">
                    <div className="cart-main">
                        {items.map(item => (
                            <div className="cart-item-card" key={item.id}>
                                <img src={item.imageUrl || 'https://via.placeholder.com/200'} alt={item.productName} className="cart-item-image" />
                                
                                <div className="cart-item-details">
                                    <div className="cart-item-header">
                                        <div>
                                            <span className={`badge ${item.productType === 'RENTAL' ? 'rental' : ''}`}>
                                                {item.productType}
                                            </span>
                                            <Link to={`/products/${item.productId}`} className="cart-item-name">
                                                {item.productName}
                                            </Link>
                                        </div>
                                        <button onClick={() => removeItem(item.id)} className="btn-delete-item" aria-label="삭제">
                                            <Trash2 size={20} />
                                        </button>
                                    </div>
                                    
                                    <div className="cart-item-meta">
                                        {item.rentalStartDate && item.rentalEndDate && (
                                            <div className="rental-dates">
                                                <Calendar size={16} /> 
                                                <span>{item.rentalStartDate} ~ {item.rentalEndDate}</span>
                                            </div>
                                        )}
                                    </div>

                                    <div className="cart-item-actions">
                                        <div className="quantity-control">
                                            <button 
                                                className="quantity-btn" 
                                                onClick={() => updateQuantity(item.id, item.quantity - 1)}
                                                disabled={item.quantity <= 1}
                                            >
                                                <Minus size={16} />
                                            </button>
                                            <span className="quantity-display">{item.quantity}</span>
                                            <button 
                                                className="quantity-btn" 
                                                onClick={() => updateQuantity(item.id, item.quantity + 1)}
                                            >
                                                <Plus size={16} />
                                            </button>
                                        </div>

                                        <div className="cart-item-price-wrapper">
                                            {item.quantity > 1 && (
                                                <div className="cart-item-unit-price">
                                                    단일가 {item.productPrice.toLocaleString()}원
                                                </div>
                                            )}
                                            <div className="cart-item-total-price">
                                                {(item.productPrice * item.quantity).toLocaleString()}원
                                            </div>
                                        </div>
                                    </div>
                                </div>
                            </div>
                        ))}
                    </div>

                    <div className="cart-sidebar">
                        <h3 className="summary-title">주문 요약</h3>
                        
                        <div className="summary-details">
                            <div className="summary-row">
                                <span>총 상품금액</span>
                                <span>{calculateTotal().toLocaleString()}원</span>
                            </div>
                            <div className="summary-row muted">
                                <span>배송비</span>
                                <span>0원</span>
                            </div>
                            <div className="summary-divider"></div>
                        </div>

                        <div className="summary-total">
                            <span className="summary-total-label">총 결제금액</span>
                            <span className="summary-total-amount">{calculateTotal().toLocaleString()}원</span>
                        </div>

                        <button className="btn-checkout" onClick={() => navigate('/checkout')}>
                            <CreditCard size={20} />
                            결제하기 ({items.length}개)
                        </button>

                        <div className="secure-checkout-notice">
                            <ShieldCheck size={16} />
                            <span>안전한 결제 환경이 보장됩니다</span>
                        </div>
                    </div>
                </div>
            )}
        </div>
    );
};

export default Cart;
