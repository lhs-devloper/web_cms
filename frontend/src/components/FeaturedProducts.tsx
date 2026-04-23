import { useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import { ShoppingBag } from 'lucide-react';
import './FeaturedProducts.css';

interface Product {
    id: number;
    name: string;
    price: number;
    imageUrls: string[];
    category: string;
    type: string;
    categoryName: string;
    hasRentalPeriod: boolean;
}

const FeaturedProducts = () => {
    const [products, setProducts] = useState<Product[]>([]);
    const navigate = useNavigate();

    useEffect(() => {
        fetch('/api/products')
            .then(res => res.json())
            .then(data => {
                // Assuming data is an array of products, let's take up to 4 for Featured
                setProducts(data.slice(0, 4));
            })
            .catch(err => console.error(err));
    }, []);

    const handleAddToCart = async (product: Product) => {
        if (product.hasRentalPeriod) {
            alert('대여 상품은 상세 페이지에서 일정을 선택해야 장바구니에 담을 수 있습니다.');
            navigate(`/products/${product.id}`);
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
                    productId: product.id,
                    quantity: 1,
                    rentalStartDate: null,
                    rentalEndDate: null
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
                alert('장바구니 담기에 실패했습니다.');
            }
        } catch (err) {
            console.error(err);
        }
    };

    return (
        <section className="featured-section container" id="featured">
            <div className="section-header">
                <h2 className="section-title">TRENDING NOW</h2>
                <p className="section-subtitle">이번 시즌 가장 주목받는 프리미엄 에디션</p>
            </div>

            <div className="featured-grid">
                {products.length > 0 ? products.map((product) => (
                    <div className="featured-card" key={product.id}>
                        <div className="featured-card-link" onClick={() => navigate(`/products/${product.id}`)}>
                            <div className="featured-image">
                                {product.imageUrls && product.imageUrls.length > 0 ? (
                                    <img src={product.imageUrls[0]} alt={product.name} />
                                ) : (
                                    <div className="featured-no-image">No Image</div>
                                )}
                            </div>
                            <div className="featured-info">
                                <div className="featured-type-badge">
                                    {product.categoryName || product.type}
                                </div>
                                <h3>{product.name}</h3>
                                <p className="featured-price">{product.price.toLocaleString()}원</p>
                            </div>
                        </div>
                        <button className="featured-cart-btn" onClick={() => handleAddToCart(product)}>
                            <ShoppingBag size={16} /> Add to Cart
                        </button>
                    </div>
                )) : (
                    <div style={{ textAlign: 'center', width: '100%', padding: '50px 0', gridColumn: '1 / -1' }}>
                        <p>현재 등록된 프리미엄 에디션 상품이 없습니다.</p>
                    </div>
                )}
            </div>
        </section>
    );
};

export default FeaturedProducts;
