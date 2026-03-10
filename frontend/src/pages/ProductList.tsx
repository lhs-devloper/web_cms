import React, { useState, useEffect } from 'react';
import { Link } from 'react-router-dom';
import './ProductList.css';

interface Product {
    id: number;
    name: string;
    price: number;
    description: string;
    type: string;
    imageUrls: string[];
    active: boolean;
}

const ProductList: React.FC = () => {
    const [products, setProducts] = useState<Product[]>([]);

    useEffect(() => {
        fetch('/api/products')
            .then(res => res.json())
            .then(data => {
                setProducts(data.filter((p: Product) => p.active));
            })
            .catch(err => console.error(err));
    }, []);

    return (
        <div className="container product-container">
            <h2>상품 목록</h2>
            <div className="product-grid">
                {products.map(p => (
                    <div key={p.id} className="product-card">
                        <Link to={`/products/${p.id}`}>
                            <div className="product-image">
                                {p.imageUrls && p.imageUrls.length > 0 ? (
                                    <img src={p.imageUrls[0]} alt={p.name} />
                                ) : (
                                    <div className="no-image">No Image</div>
                                )}
                            </div>
                            <div className="product-info">
                                <h3>{p.name}</h3>
                                <p className="product-price">{p.price.toLocaleString()}원</p>
                                <div className="product-type-badge">
                                    {p.type === 'NORMAL' ? '일반상품' : '대여상품'}
                                </div>
                            </div>
                        </Link>
                    </div>
                ))}
            </div>
        </div>
    );
};

export default ProductList;
