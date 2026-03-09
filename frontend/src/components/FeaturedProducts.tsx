import { ShoppingBag, Heart } from 'lucide-react';
import './FeaturedProducts.css';

const products = [
    {
        id: 1,
        name: "Aero Minimalist Chair",
        category: "FURNITURE",
        price: "₩320,000",
        image: "https://images.unsplash.com/photo-1598300042247-d088f8ab3a91?q=80&w=600&auto=format&fit=crop"
    },
    {
        id: 2,
        name: "Lumina Desk Lamp",
        category: "LIGHTING",
        price: "₩150,000",
        image: "https://images.unsplash.com/photo-1507473885765-e6ed057f782c?q=80&w=600&auto=format&fit=crop"
    },
    {
        id: 3,
        name: "Nordic Ceramic Vase",
        category: "DECOR",
        price: "₩85,000",
        image: "https://images.unsplash.com/photo-1581783342308-f792dbdd27c5?q=80&w=600&auto=format&fit=crop"
    },
    {
        id: 4,
        name: "Velvet Lounge Sofa",
        category: "FURNITURE",
        price: "₩1,250,000",
        image: "https://images.unsplash.com/photo-1493663284031-b7e3aefcae8e?q=80&w=600&auto=format&fit=crop"
    }
];

const FeaturedProducts = () => {
    return (
        <section className="featured-section container" id="featured">
            <div className="section-header">
                <h2 className="section-title">TRENDING NOW</h2>
                <p className="section-subtitle">이번 시즌 가장 주목받는 프리미엄 에디션</p>
            </div>

            <div className="product-grid">
                {products.map((product) => (
                    <div className="product-card" key={product.id}>
                        <div className="product-image-wrapper">
                            <img src={product.image} alt={product.name} className="product-image" />
                            <div className="product-overlay">
                                <button className="icon-btn-round" aria-label="Add to wishlist"><Heart size={18} /></button>
                                <button className="btn-add-cart">
                                    <ShoppingBag size={18} /> Add to Cart
                                </button>
                            </div>
                        </div>
                        <div className="product-info">
                            <span className="product-category">{product.category}</span>
                            <h3 className="product-name">{product.name}</h3>
                            <p className="product-price">{product.price}</p>
                        </div>
                    </div>
                ))}
            </div>
        </section>
    );
};

export default FeaturedProducts;
