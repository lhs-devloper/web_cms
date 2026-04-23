import { useState, useEffect } from 'react';
import { Link } from 'react-router-dom';
import { ShoppingCart, User, Menu as MenuIcon, X, Sun, Moon } from 'lucide-react';
import { useSiteSetting } from '../contexts/SiteSettingContext';
import { useTheme } from '../contexts/ThemeContext';
import './Navbar.css';

interface Menu {
    id: number;
    title: string;
    linkUrl: string;
    sortOrder: number;
    parentId?: number | null;
}

const Navbar = () => {
    const [scrolled, setScrolled] = useState(false);
    const [menuOpen, setMenuOpen] = useState(false);
    const [menus, setMenus] = useState<Menu[]>([]);
    const { setting } = useSiteSetting();
    const { theme, toggleTheme } = useTheme();

    useEffect(() => {
        const handleScroll = () => {
            setScrolled(window.scrollY > 50);
        };

        window.addEventListener('scroll', handleScroll);
        return () => window.removeEventListener('scroll', handleScroll);
    }, []);

    useEffect(() => {
        const fetchMenus = async () => {
            try {
                const res = await fetch(`/api/global/menus`);
                if (res.ok) {
                    const data = await res.json();
                    setMenus(data);
                }
            } catch (error) {
                console.error("Failed to load menus");
            }
        };
        fetchMenus();
    }, []);

    const getImageUrl = (url?: string) => {
        if (!url) return '';
        return url.startsWith('http') ? url : `${url}`;
    };

    return (
        <nav className={`navbar ${scrolled ? 'scrolled' : ''}`}>
            <div className="container nav-container">
                <div className="nav-brand">
                    <a href="/">
                        {setting?.logoUrl ? (
                            <img src={getImageUrl(setting.logoUrl)} alt={setting.logoAltText || 'Logo'} style={{ maxHeight: '40px' }} />
                        ) : (
                            <span>{setting?.siteName || 'Lumière'}</span>
                        )}
                    </a>
                </div>

                <ul className={`nav-links ${menuOpen ? 'open' : ''}`}>
                    {menus.length > 0 ? (
                        menus.filter(m => !m.parentId).sort((a, b) => a.sortOrder - b.sortOrder).map(menu => {
                            const subMenus = menus.filter(m => m.parentId === menu.id).sort((a, b) => a.sortOrder - b.sortOrder);
                            const hasSub = subMenus.length > 0;
                            return (
                                <li key={menu.id} className={hasSub ? 'has-dropdown' : ''}>
                                    <a href={menu.linkUrl}>{menu.title}</a>
                                    {hasSub && (
                                        <ul className="dropdown-menu">
                                            {subMenus.map(sub => (
                                                <li key={sub.id}>
                                                    <a href={sub.linkUrl}>{sub.title}</a>
                                                </li>
                                            ))}
                                        </ul>
                                    )}
                                </li>
                            );
                        })
                    ) : (
                        <>
                            <li><Link to="/products">전체 상품</Link></li>
                            <li><Link to="/stories">브랜드 스토리</Link></li>
                            <li><Link to="/about">소개</Link></li>
                            <li><Link to="/board">게시판</Link></li>
                        </>
                    )}
                </ul>

                <div className="nav-actions">
                    <button className="icon-btn" onClick={toggleTheme} aria-label="Toggle Theme">
                        {theme === 'dark' ? <Sun size={20} /> : <Moon size={20} />}
                    </button>
                    <Link
                        to={localStorage.getItem('accessToken') ? "/mypage" : "/login"}
                        className="icon-btn"
                        aria-label="Profile"
                    >
                        <User size={20} />
                    </Link>
                    <Link to="/cart" className="icon-btn" aria-label="Cart">
                        <ShoppingCart size={20} />
                    </Link>
                    <button
                        className="mobile-menu-btn icon-btn"
                        onClick={() => setMenuOpen(!menuOpen)}
                        aria-label="Menu"
                    >
                        {menuOpen ? <X size={24} /> : <MenuIcon size={24} />}
                    </button>
                </div>
            </div>
        </nav>
    );
};

export default Navbar;
