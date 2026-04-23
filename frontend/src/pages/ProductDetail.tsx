import React, { useState, useEffect } from 'react';
import { useParams, useNavigate } from 'react-router-dom';
import { Minus, Plus, Star } from 'lucide-react';
import './ProductDetail.css';

interface Product {
    id: number;
    name: string;
    price: number;
    description: string;
    type: string;
    categoryId: number;
    categoryName: string;
    hasStock: boolean;
    hasRentalPeriod: boolean;
    imageUrls: string[];
}

const ProductDetail: React.FC = () => {
    const { id } = useParams<{ id: string }>();
    const navigate = useNavigate();
    const [product, setProduct] = useState<Product | null>(null);
    const [quantity, setQuantity] = useState(1);
    const [rentalStartDate, setRentalStartDate] = useState('');
    const [rentalEndDate, setRentalEndDate] = useState('');
    const [mainImage, setMainImage] = useState<string>('');
    const [reviews, setReviews] = useState<any[]>([]);

    useEffect(() => {
        fetch(`/api/products/${id}`)
            .then(res => res.json())
            .then(data => {
                setProduct(data);
                if (data.imageUrls && data.imageUrls.length > 0) {
                    setMainImage(data.imageUrls[0]);
                }
            })
            .catch(err => console.error(err));

        fetch(`/api/reviews/product/${id}`)
            .then(res => res.ok ? res.json() : [])
            .then(data => setReviews(data))
            .catch(() => {});
    }, [id]);

    const handleAddToCart = async () => {
        if (product?.hasRentalPeriod && (!rentalStartDate || !rentalEndDate)) {
            alert('대여 시작일과 종료일을 선택해주세요.');
            return;
        }

        try {
            const res = await fetch('/api/cart/add', {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json',
                    ...(localStorage.getItem('accessToken') ? { 'Authorization': `Bearer ${localStorage.getItem('accessToken')}` } : {})
                },
                body: JSON.stringify({
                    productId: product?.id,
                    quantity,
                    rentalStartDate: rentalStartDate || null,
                    rentalEndDate: rentalEndDate || null
                })
            });

            if (res.status === 401) {
                alert('로그인이 필요합니다.');
                navigate('/login');
                return;
            }

            if (res.ok) {
                if (confirm('장바구니에 담겼습니다. 장바구니로 이동하시겠습니까?')) {
                    navigate('/cart');
                }
            } else {
                const errData = await res.json();
                alert(errData.message || '오류가 발생했습니다.');
            }
        } catch (err) {
            console.error(err);
            alert('오류가 발생했습니다.');
        }
    };

    if (!product) return <div className="container">Loading...</div>;

    return (
        <div className="container product-detail-container">
            <div className="product-detail-hero">
                <div className="product-detail-image-section">
                    <div className="product-detail-image">
                        {mainImage ? (
                            <img src={mainImage} alt={product.name} />
                        ) : (
                            <div className="no-image">No Image</div>
                        )}
                    </div>
                    {product.imageUrls && product.imageUrls.length > 1 && (
                        <div className="product-thumbnails" style={{ display: 'flex', gap: '10px', marginTop: '10px', overflowX: 'auto' }}>
                            {product.imageUrls.map((url, idx) => (
                                <img
                                    key={idx}
                                    src={url}
                                    alt={`${product.name} - ${idx}`}
                                    style={{ width: '80px', height: '80px', objectFit: 'cover', cursor: 'pointer', border: mainImage === url ? '2px solid #000' : '1px solid #ccc' }}
                                    onClick={() => setMainImage(url)}
                                />
                            ))}
                        </div>
                    )}
                </div>

                <div className="product-detail-info">
                    <h2>{product.name}</h2>
                    <div className="product-detail-price">{product.price.toLocaleString()}원</div>

                    <div className="product-detail-options">
                        {product.hasRentalPeriod && (
                            <div className="rental-options">
                                <label>대여 일정</label>
                                <div className="date-inputs">
                                    <input type="date" value={rentalStartDate} onChange={e => setRentalStartDate(e.target.value)} />
                                    <span> ~ </span>
                                    <input type="date" value={rentalEndDate} onChange={e => setRentalEndDate(e.target.value)} />
                                </div>
                            </div>
                        )}

                        <div className="quantity-option">
                            <label>수량</label>
                            <div className="quantity-controls">
                                <button
                                    className="qty-btn qty-minus"
                                    onClick={() => setQuantity(q => Math.max(1, q - 1))}
                                    disabled={quantity <= 1}
                                >
                                    <Minus size={16} />
                                </button>
                                <span className="qty-value">{quantity}</span>
                                <button
                                    className="qty-btn qty-plus"
                                    onClick={() => setQuantity(q => q + 1)}
                                >
                                    <Plus size={16} />
                                </button>
                            </div>
                        </div>

                        <div className="total-price">
                            총 {(product.price * quantity).toLocaleString()}원
                        </div>

                        <div className="action-buttons">
                            <button className="add-cart-btn" onClick={handleAddToCart}>장바구니 담기</button>
                        </div>
                    </div>
                </div>
            </div>

            <div className="product-detail-description">
                <h3>상품 설명</h3>
                <div className="description-content" dangerouslySetInnerHTML={{ __html: product.description }} />
            </div>

            <div className="product-detail-description" style={{ marginTop: '2rem' }}>
                <h3>구매 리뷰 ({reviews.length})</h3>
                {reviews.length === 0 ? (
                    <p style={{ color: 'var(--text-muted)', padding: '1rem 0' }}>아직 작성된 리뷰가 없습니다.</p>
                ) : (
                    <div className="review-list">
                        {reviews.map((review: any) => (
                            <div key={review.id} className="review-item" style={{ borderBottom: '1px solid var(--glass-border)', padding: '1rem 0' }}>
                                <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: '0.5rem' }}>
                                    <div style={{ display: 'flex', alignItems: 'center', gap: '0.5rem' }}>
                                        <strong>{review.userName}</strong>
                                        <div style={{ display: 'flex', gap: '2px' }}>
                                            {[1, 2, 3, 4, 5].map(n => (
                                                <Star key={n} size={14} fill={n <= review.rating ? '#fbbf24' : 'none'} color={n <= review.rating ? '#fbbf24' : '#64748b'} />
                                            ))}
                                        </div>
                                    </div>
                                    <span style={{ fontSize: '0.8rem', color: 'var(--text-muted)' }}>
                                        {new Date(review.createdAt).toLocaleDateString()}
                                    </span>
                                </div>
                                <p style={{ color: 'var(--text-secondary)', lineHeight: '1.6' }}>{review.content}</p>
                            </div>
                        ))}
                    </div>
                )}
            </div>
        </div>
    );
};

export default ProductDetail;
