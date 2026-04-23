import { useState, useEffect } from 'react';
import './Hero.css';

interface BannerButton {
    text: string;
    linkUrl: string;
    bgColor: string;
    textColor: string;
}

interface Banner {
    id: number;
    subtitle: string;
    title: string;
    description: string;
    imageUrl: string;
    linkUrl: string;
    titleFontSize: string;
    titleColor: string;
    subtitleColor: string;
    textAlignment: string;
    buttonsJson?: string;
    buttons: BannerButton[];
    isActive: boolean;
}

const Hero = () => {
    const [banners, setBanners] = useState<Banner[]>([]);
    const [currentIndex, setCurrentIndex] = useState(0);

    useEffect(() => {
        const fetchBanners = async () => {
            try {
                const res = await fetch(`/api/global/banners`);
                if (res.ok) {
                    const data = await res.json();
                    if (data && data.length > 0) {
                        const mappedData = data.map((b: any) => ({
                            ...b,
                            buttons: b.buttonsJson ? JSON.parse(b.buttonsJson) : []
                        }));
                        setBanners(mappedData);
                    }
                }
            } catch (error) {
                console.error('Failed to load banners:', error);
            }
        };
        fetchBanners();
    }, []);

    useEffect(() => {
        if (banners.length <= 1) return;
        const interval = setInterval(() => {
            setCurrentIndex((prev) => (prev + 1) % banners.length);
        }, 5000);
        return () => clearInterval(interval);
    }, [banners.length]);

    const handlePrev = () => {
        setCurrentIndex((prev) => (prev === 0 ? banners.length - 1 : prev - 1));
    };

    const handleNext = () => {
        setCurrentIndex((prev) => (prev + 1) % banners.length);
    };

    if (banners.length === 0) {
        // Fallback default banner if nothing is returned from server
        return (
            <section className="hero-section">
                <div className="hero-bg">
                    <img
                        src="https://images.unsplash.com/photo-1441986300917-64674bd600d8?q=80&w=2000&auto=format&fit=crop"
                        alt="Default Hero"
                        className="hero-video"
                    />
                    <div className="hero-overlay"></div>
                </div>
                <div className="container hero-content animate-fade-in" style={{ alignItems: 'flex-start', textAlign: 'left' }}>
                    <h1 className="hero-title">
                        ELEVATE YOUR EVERYDAY.
                    </h1>
                </div>
            </section>
        );
    }

    return (
        <section className="hero-section">
            {banners.map((currentBanner, index) => {
                const bgUrl = currentBanner.imageUrl?.startsWith('http') ? currentBanner.imageUrl : `${currentBanner.imageUrl}`;
                const isActive = index === currentIndex;

                return (
                    <div key={currentBanner.id} className={`hero-slide ${isActive ? 'active' : ''}`}>
                        <div className="hero-bg">
                            {currentBanner.imageUrl?.endsWith('.mp4') ? (
                                <video autoPlay loop muted playsInline className="hero-video">
                                    <source src={bgUrl} type="video/mp4" />
                                </video>
                            ) : (
                                <img src={bgUrl} alt={currentBanner.title} className="hero-video" />
                            )}
                            <div className="hero-overlay"></div>
                        </div>

                        <div
                            className="container hero-content"
                            style={{
                                display: 'flex',
                                flexDirection: 'column',
                                alignItems: currentBanner.textAlignment === 'center' ? 'center' : (currentBanner.textAlignment === 'right' ? 'flex-end' : 'flex-start'),
                                textAlign: currentBanner.textAlignment as any,
                                width: '100%'
                            }}
                        >
                            <h1
                                className="hero-title animate-slide-up"
                                style={{
                                    fontSize: currentBanner.titleFontSize,
                                    background: 'none',
                                    WebkitTextFillColor: currentBanner.titleColor,
                                    color: currentBanner.titleColor,
                                    animationDelay: '0.2s'
                                }}
                            >
                                <span
                                    className="hero-subtitle"
                                    style={{ color: currentBanner.subtitleColor }}
                                >
                                    {currentBanner.subtitle}
                                </span><br />
                                <div dangerouslySetInnerHTML={{ __html: (currentBanner.title || '').replace(/\n/g, '<br />') }} />
                            </h1>

                            <div className="hero-description animate-slide-up" style={{ textAlign: currentBanner.textAlignment as any, animationDelay: '0.4s' }}>
                                <div dangerouslySetInnerHTML={{ __html: (currentBanner.description || '').replace(/\n/g, '<br />') }} />
                            </div>

                            {currentBanner.buttons && currentBanner.buttons.length > 0 && (
                                <div className="hero-actions animate-slide-up" style={{ justifyContent: currentBanner.textAlignment === 'center' ? 'center' : (currentBanner.textAlignment === 'right' ? 'flex-end' : 'flex-start'), display: 'flex', gap: '1rem', flexWrap: 'wrap', animationDelay: '0.6s' }}>
                                    {currentBanner.buttons.map((btn, btnIndex) => (
                                        <a key={btnIndex} href={btn.linkUrl || '#'} className="btn btn-primary" style={{ backgroundColor: btn.bgColor, color: btn.textColor }}>
                                            {btn.text}
                                        </a>
                                    ))}
                                </div>
                            )}
                        </div>
                    </div>
                );
            })}

            {banners.length > 1 && (
                <>
                    <button className="slider-nav prev" onClick={handlePrev}>
                        &#10094;
                    </button>
                    <button className="slider-nav next" onClick={handleNext}>
                        &#10095;
                    </button>

                    <div className="slider-dots">
                        {banners.map((_, index) => (
                            <button
                                key={index}
                                className={`slider-dot ${index === currentIndex ? 'active' : ''}`}
                                onClick={() => setCurrentIndex(index)}
                            />
                        ))}
                    </div>
                </>
            )}
        </section>
    );
};

export default Hero;
