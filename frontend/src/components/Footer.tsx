import { useSiteSetting } from '../contexts/SiteSettingContext';
import { Link } from 'react-router-dom';
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
                </div>

                <div className="footer-links">
                    <div className="footer-col">
                        <h3>ABOUT US</h3>
                        <ul>
                            <li><Link to="/stories">브랜드 스토리</Link></li>
                            <li><Link to="/about">소개</Link></li>
                            <li><Link to="/location">오시는 길</Link></li>
                        </ul>
                    </div>
                    <div className="footer-col">
                        <h3>SHOPPING</h3>
                        <ul>
                            <li><Link to="/products">전체 상품</Link></li>
                            <li><Link to="/cart">장바구니</Link></li>
                            <li><Link to="/orders">주문 내역</Link></li>
                            <li><Link to="/membership">멤버십</Link></li>
                        </ul>
                    </div>
                    <div className="footer-col">
                        <h3>SUPPORT</h3>
                        <ul>
                            <li><Link to="/board">게시판</Link></li>
                            <li><Link to="/mypage">마이페이지</Link></li>
                        </ul>
                    </div>
                </div>
            </div>

            <div className="footer-bottom">
                <div className="container">
                    <p>{setting?.footerCopyright || `© ${new Date().getFullYear()} ${setting?.siteName || 'CMS'}. All rights reserved.`}</p>
                </div>
            </div>
        </footer>
    );
};

export default Footer;
