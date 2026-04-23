import { useState } from 'react';
import { Outlet, Link, useLocation } from 'react-router-dom';
import { LayoutDashboard, Users, FileText, Settings, Image, LogOut, Menu as MenuIcon, Sun, Moon, ShoppingBag, BookOpen, X, Tag, Package, Award } from 'lucide-react';
import { useTheme } from '../contexts/ThemeContext';
import { useSiteSetting } from '../contexts/SiteSettingContext';
import './AdminLayout.css';

const AdminLayout = () => {
    const location = useLocation();
    const { theme, toggleTheme } = useTheme();
    const { setting } = useSiteSetting();
    const [sidebarOpen, setSidebarOpen] = useState(false);

    return (
        <div className="admin-layout-container">
            {sidebarOpen && <div className="sidebar-overlay" onClick={() => setSidebarOpen(false)} />}
            <aside className={`admin-sidebar ${sidebarOpen ? 'open' : ''}`}>
                <div className="admin-brand">
                    <Link to="/admin">{setting?.siteName || 'CMS'} Admin</Link>
                    <button className="sidebar-close-btn" onClick={() => setSidebarOpen(false)} aria-label="Close sidebar">
                        <X size={20} />
                    </button>
                </div>
                <nav className="admin-nav" onClick={() => setSidebarOpen(false)}>
                    <ul>
                        <li><Link to="/admin" className={location.pathname === '/admin' ? 'active' : ''}><LayoutDashboard size={20} /> 대시보드</Link></li>
                        <li><Link to="/admin/banners" className={location.pathname.includes('/admin/banners') ? 'active' : ''}><Image size={20} /> 배너 관리</Link></li>
                        <li><Link to="/admin/menus" className={location.pathname.includes('/admin/menus') ? 'active' : ''}><MenuIcon size={20} /> 메뉴 관리</Link></li>
                        <li><Link to="/admin/members" className={location.pathname.includes('/admin/members') ? 'active' : ''}><Users size={20} /> 회원 관리</Link></li>
                        <li><Link to="/admin/boards" className={location.pathname.includes('/admin/boards') ? 'active' : ''}><FileText size={20} /> 게시판 관리</Link></li>
                        <li><Link to="/admin/products" className={location.pathname === '/admin/products' ? 'active' : ''}><ShoppingBag size={20} /> 상품 관리</Link></li>
                        <li><Link to="/admin/product-categories" className={location.pathname.includes('/admin/product-categories') ? 'active' : ''}><Tag size={20} /> 상품 타입 관리</Link></li>
                        <li><Link to="/admin/stories" className={location.pathname.includes('/admin/stories') ? 'active' : ''}><BookOpen size={20} /> 스토리 관리</Link></li>
                        <li><Link to="/admin/orders" className={location.pathname.includes('/admin/orders') ? 'active' : ''}><Package size={20} /> 주문 관리</Link></li>
                        <li><Link to="/admin/membership" className={location.pathname.includes('/admin/membership') ? 'active' : ''}><Award size={20} /> 멤버십 관리</Link></li>
                        <li className={`nav-group ${location.pathname.includes('/admin/settings') ? 'open' : ''}`}>
                            <div className="nav-group-title"><Settings size={20} /> 환경 설정</div>
                            <ul className="sub-nav">
                                <li><Link to="/admin/settings" className={location.pathname === '/admin/settings' ? 'active' : ''}>SEO & 브랜드 기본</Link></li>
                                <li><Link to="/admin/settings/about" className={location.pathname === '/admin/settings/about' ? 'active' : ''}>회사소개 페이지 설정</Link></li>
                                <li><Link to="/admin/settings/location" className={location.pathname === '/admin/settings/location' ? 'active' : ''}>오시는길 안내 설정</Link></li>
                                <li><Link to="/admin/settings/social" className={location.pathname === '/admin/settings/social' ? 'active' : ''}>간편로그인 설정</Link></li>
                                <li><Link to="/admin/settings/payment" className={location.pathname === '/admin/settings/payment' ? 'active' : ''}>결제(PG) 연동 설정</Link></li>
                            </ul>
                        </li>
                    </ul>
                </nav>
                <div className="admin-footer-nav">
                    <Link to="/"><LogOut size={20} /> {setting?.siteName || '메인'} 사이트로</Link>
                </div>
            </aside>
            <main className="admin-main-content">
                <header className="admin-header">
                    <div style={{ display: 'flex', alignItems: 'center', gap: '1rem' }}>
                        <button className="sidebar-toggle-btn" onClick={() => setSidebarOpen(true)} aria-label="Open sidebar">
                            <MenuIcon size={22} />
                        </button>
                        <h2>관리자 패널</h2>
                    </div>
                    <div style={{ display: 'flex', alignItems: 'center', gap: '1rem' }}>
                        <button onClick={toggleTheme} style={{ background: 'none', border: 'none', color: 'inherit', cursor: 'pointer', display: 'flex' }} aria-label="Toggle Theme">
                            {theme === 'dark' ? <Sun size={20} /> : <Moon size={20} />}
                        </button>
                        <div className="admin-user-info">
                            최고관리자 (admin)님
                        </div>
                    </div>
                </header>
                <div className="admin-content-wrapper">
                    <Outlet />
                </div>
            </main>
        </div>
    );
};

export default AdminLayout;
