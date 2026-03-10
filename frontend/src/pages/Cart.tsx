import React, { useState, useEffect } from 'react';
import { Link, useNavigate } from 'react-router-dom';
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
    const navigate = useNavigate();

    useEffect(() => {
        fetchCartItems();
    }, []);

    const fetchCartItems = async () => {
        try {
            const res = await fetch('/api/cart');
            if (res.status === 401) {
                navigate('/login');
                return;
            }
            const data = await res.json();
            setItems(data);
        } catch (err) {
            console.error(err);
        }
    };

    const updateQuantity = async (id: number, quantity: number) => {
        if (quantity < 1) return;
        try {
            await fetch(`/api/cart/${id}`, {
                method: 'PUT',
                headers: {
                    'Content-Type': 'application/json'
                },
                body: JSON.stringify({ quantity })
            });
            fetchCartItems();
        } catch (err) {
            console.error(err);
        }
    };

    const removeItem = async (id: number) => {
        try {
            await fetch(`/api/cart/${id}`, {
                method: 'DELETE'
            });
            fetchCartItems();
        } catch (err) {
            console.error(err);
        }
    };

    const calculateTotal = () => {
        return items.reduce((sum, item) => sum + (item.productPrice * item.quantity), 0);
    };

    return (
        <div className="container cart-container">
            <h2>장바구니</h2>

            {items.length === 0 ? (
                <div className="empty-cart">
                    <p>장바구니가 비어있습니다.</p>
                    <Link to="/products" className="btn-continue">쇼핑 계속하기</Link>
                </div>
            ) : (
                <div className="cart-content">
                    <table className="cart-table">
                        <thead>
                            <tr>
                                <th>상품정보</th>
                                <th>수량/일정</th>
                                <th>금액</th>
                                <th>관리</th>
                            </tr>
                        </thead>
                        <tbody>
                            {items.map(item => (
                                <tr key={item.id}>
                                    <td className="cart-item-info">
                                        <img src={item.imageUrl || 'https://via.placeholder.com/80'} alt={item.productName} />
                                        <div>
                                            <span className="badge">{item.productType === 'NORMAL' ? '일반' : '대여'}</span>
                                            <Link to={`/products/${item.productId}`} className="item-name">{item.productName}</Link>
                                        </div>
                                    </td>
                                    <td className="cart-item-details">
                                        <div className="quantity-control">
                                            <button onClick={() => updateQuantity(item.id, item.quantity - 1)}>-</button>
                                            <span>{item.quantity}</span>
                                            <button onClick={() => updateQuantity(item.id, item.quantity + 1)}>+</button>
                                        </div>
                                        {item.productType === 'RENTAL' && item.rentalStartDate && (
                                            <div className="rental-dates">
                                                {item.rentalStartDate} ~ {item.rentalEndDate}
                                            </div>
                                        )}
                                    </td>
                                    <td className="cart-item-price">
                                        {(item.productPrice * item.quantity).toLocaleString()}원
                                    </td>
                                    <td>
                                        <button onClick={() => removeItem(item.id)} className="btn-delete">삭제</button>
                                    </td>
                                </tr>
                            ))}
                        </tbody>
                    </table>

                    <div className="cart-summary">
                        <div className="summary-row">
                            <span>총 상품금액</span>
                            <span>{calculateTotal().toLocaleString()}원</span>
                        </div>
                        <div className="summary-total">
                            <span>결제예정금액</span>
                            <span>{calculateTotal().toLocaleString()}원</span>
                        </div>
                        <button className="btn-checkout" onClick={() => alert('결제 시스템은 추후 연동 예정입니다.')}>주문하기</button>
                    </div>
                </div>
            )}
        </div>
    );
};

export default Cart;
