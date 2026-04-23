import { Facebook, Instagram, Twitter, Youtube } from 'lucide-react';
import { useSiteSetting } from '../contexts/SiteSettingContext';
import './Footer.css';

const Footer = () => {
    const { setting } = useSiteSetting();

    return (
        <footer className="footer-section">
            <div className="container footer-content">
                <div className="footer-brand">
                    <h2 className="footer-logo">{setting?.siteName || 'Lumière'}</h2>
                    <p className="footer-desc">
                        프리미엄 라이프스타일의 새로운 기준.<br />
                        변하지 않는 가치를 선사합니다.
                    </p>
                    <div className="footer-socials">
                        <a href="#" aria-label="Instagram"><Instagram size={20} /></a>
                        <a href="#" aria-label="Facebook"><Facebook size={20} /></a>
                        <a href="#" aria-label="Twitter"><Twitter size={20} /></a>
                        <a href="#" aria-label="Youtube"><Youtube size={20} /></a>
                    </div>
                </div>

                <div className="footer-links">
                    <div className="footer-col">
                        <h3>ABOUT US</h3>
                        <ul>
                            <li><a href="#">Brand Story</a></li>
                            <li><a href="#">Sustainability</a></li>
                            <li><a href="#">Careers</a></li>
                            <li><a href="#">Press</a></li>
                        </ul>
                    </div>
                    <div className="footer-col">
                        <h3>CUSTOMER CARE</h3>
                        <ul>
                            <li><a href="#">Contact Us</a></li>
                            <li><a href="#">Shipping & Returns</a></li>
                            <li><a href="#">FAQ</a></li>
                            <li><a href="#">Track Order</a></li>
                        </ul>
                    </div>
                    <div className="footer-col">
                        <h3>LEGAL</h3>
                        <ul>
                            <li><a href="#">Terms of Service</a></li>
                            <li><a href="#">Privacy Policy</a></li>
                            <li><a href="#">Cookie Policy</a></li>
                        </ul>
                    </div>
                </div>
            </div>

            <div className="footer-bottom">
                <div className="container">
                    <p>{setting?.footerCopyright || `© ${new Date().getFullYear()} ${setting?.siteName || 'CMS'}. All rights reserved.`}</p>
                    <div className="payment-icons">
                        {/* Dummy icons for payment methods */}
                        <span className="payment-icon">VISA</span>
                        <span className="payment-icon">MASTER</span>
                        <span className="payment-icon">PAYPAL</span>
                    </div>
                </div>
            </div>
        </footer>
    );
};

export default Footer;
