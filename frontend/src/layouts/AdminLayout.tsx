import { Outlet, Link, useLocation } from 'react-router-dom';
import { LayoutDashboard, Users, FileText, Settings, Image, LogOut, Menu as MenuIcon, Sun, Moon, ShoppingBag } from 'lucide-react';
import { useTheme } from '../contexts/ThemeContext';
import './AdminLayout.css';

const AdminLayout = () => {
    const location = useLocation();
    const { theme, toggleTheme } = useTheme();

    return (
        <div className="admin-layout-container">
            <aside className="admin-sidebar">
                <div className="admin-brand">
                    <Link to="/admin">Lumière Admin</Link>
                </div>
                <nav className="admin-nav">
                    <ul>
                        <li><Link to="/admin" className={location.pathname === '/admin' ? 'active' : ''}><LayoutDashboard size={20} /> 대시보드</Link></li>
                        <li><Link to="/admin/banners" className={location.pathname.includes('/admin/banners') ? 'active' : ''}><Image size={20} /> 배너 관리</Link></li>
                        <li><Link to="/admin/menus" className={location.pathname.includes('/admin/menus') ? 'active' : ''}><MenuIcon size={20} /> 메뉴 관리</Link></li>
                        <li><Link to="/admin/members" className={location.pathname.includes('/admin/members') ? 'active' : ''}><Users size={20} /> 회원 관리</Link></li>
                        <li><Link to="/admin/boards" className={location.pathname.includes('/admin/boards') ? 'active' : ''}><FileText size={20} /> 게시판 관리</Link></li>
                        <li><Link to="/admin/products" className={location.pathname.includes('/admin/products') ? 'active' : ''}><ShoppingBag size={20} /> 상품 관리</Link></li>
                        <li className={`nav-group ${location.pathname.includes('/admin/settings') ? 'open' : ''}`}>
                            <div className="nav-group-title"><Settings size={20} /> 환경 설정</div>
                            <ul className="sub-nav">
                                <li><Link to="/admin/settings" className={location.pathname === '/admin/settings' ? 'active' : ''}>SEO & 브랜드 기본</Link></li>
                                <li><Link to="/admin/settings/about" className={location.pathname === '/admin/settings/about' ? 'active' : ''}>회사소개 페이지 설정</Link></li>
                                <li><Link to="/admin/settings/location" className={location.pathname === '/admin/settings/location' ? 'active' : ''}>오시는길 안내 설정</Link></li>
                                <li><Link to="/admin/settings/social" className={location.pathname === '/admin/settings/social' ? 'active' : ''}>간편로그인 설정</Link></li>
                            </ul>
                        </li>
                    </ul>
                </nav>
                <div className="admin-footer-nav">
                    <Link to="/"><LogOut size={20} /> 리테일샵 메인으로</Link>
                </div>
            </aside>
            <main className="admin-main-content">
                <header className="admin-header">
                    <h2>관리자 패널</h2>
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
