import React, { useState, useEffect } from 'react';
import { Plus, Edit2, Trash2, ArrowDownRight, GripVertical } from 'lucide-react';
import './AdminMenu.css';

interface Menu {
    id?: number;
    title: string;
    linkUrl: string;
    sortOrder: number;
    isActive: boolean;
    parentId?: number | null;
}

const emptyMenu: Menu = {
    title: '',
    linkUrl: '',
    sortOrder: 0,
    isActive: true,
    parentId: null
};

interface UrlOption {
    label: string;
    url: string;
}

const STATIC_URLS: UrlOption[] = [
    { label: '홈', url: '/' },
    { label: '회사소개', url: '/about' },
    { label: '오시는길', url: '/location' },
    { label: '상품 목록', url: '/products' },
    { label: '장바구니', url: '/cart' },
    { label: '마이페이지', url: '/mypage' },
    { label: '주문내역', url: '/orders' },
];

const AdminMenu = () => {
    const [menus, setMenus] = useState<Menu[]>([]);
    const [loading, setLoading] = useState(false);
    const [isModalOpen, setIsModalOpen] = useState(false);
    const [currentMenu, setCurrentMenu] = useState<Menu>(emptyMenu);
    const [availableUrls, setAvailableUrls] = useState<UrlOption[]>(STATIC_URLS);

    const fetchMenus = async () => {
        try {
            const res = await fetch(`http://${window.location.hostname}:8080/api/admin/menus`);
            const data = await res.json();
            if (data.menus) {
                setMenus(data.menus);
            }
        } catch (error) {
            console.error('Failed to load menus:', error);
        }
    };

    const fetchBoards = async () => {
        try {
            const res = await fetch(`http://${window.location.hostname}:8080/api/admin/board`);
            if (res.ok) {
                const data = await res.json();
                const boards: UrlOption[] = (data.boards || []).map((b: any) => ({
                    label: `게시판: ${b.boardId}`,
                    url: `/board/${b.boardId}`
                }));
                setAvailableUrls([...STATIC_URLS, ...boards]);
            }
        } catch (error) {
            console.error('Failed to load boards:', error);
        }
    };

    useEffect(() => {
        fetchMenus();
        fetchBoards();
    }, []);

    const handleOpenModal = (menu?: Menu, parentId: number | null = null) => {
        if (menu) {
            setCurrentMenu({ ...menu });
        } else {
            setCurrentMenu({ ...emptyMenu, parentId, sortOrder: menus.length });
        }
        setIsModalOpen(true);
    };

    const handleCloseModal = () => {
        setIsModalOpen(false);
        setCurrentMenu(emptyMenu);
    };

    useEffect(() => {
        const handleEsc = (e: KeyboardEvent) => {
            if (e.key === 'Escape' && isModalOpen) handleCloseModal();
        };
        document.addEventListener('keydown', handleEsc);
        return () => document.removeEventListener('keydown', handleEsc);
    }, [isModalOpen]);

    const handleChange = (e: React.ChangeEvent<HTMLInputElement | HTMLSelectElement>) => {
        const { name, value, type } = e.target;
        if (type === 'checkbox') {
            const checked = (e.target as HTMLInputElement).checked;
            setCurrentMenu(prev => ({ ...prev, [name]: checked }));
        } else {
            setCurrentMenu(prev => ({ ...prev, [name]: value }));
        }
    };

    const handleSubmit = async (e: React.FormEvent) => {
        e.preventDefault();
        setLoading(true);

        try {
            const res = await fetch(`http://${window.location.hostname}:8080/api/admin/menus/save`, {
                method: 'POST',
                headers: { 'Content-Type': 'application/json' },
                body: JSON.stringify(currentMenu)
            });

            if (res.ok) {
                alert('메뉴가 저장되었습니다.');
                fetchMenus();
                handleCloseModal();
            } else {
                alert('저장 실패');
            }
        } catch (error) {
            console.error(error);
            alert('오류 발생');
        } finally {
            setLoading(false);
        }
    };

    const handleDelete = async (id: number) => {
        if (!confirm('정말 삭제하시겠습니까? (서브메뉴가 있는 메인메뉴 삭제 시 서브메뉴가 고아 데이터가 될 수 있습니다)')) return;
        try {
            const res = await fetch(`http://${window.location.hostname}:8080/api/admin/menus/delete?id=${id}`, { method: 'POST' });
            if (res.ok) fetchMenus();
        } catch (error) {
            console.error(error);
        }
    };

    const [draggedMenuId, setDraggedMenuId] = useState<number | null>(null);
    const [dragOverMenuId, setDragOverMenuId] = useState<number | null>(null);

    const handleDragStart = (e: React.DragEvent<HTMLTableRowElement>, menu: Menu) => {
        setDraggedMenuId(menu.id!);
        e.dataTransfer.effectAllowed = 'move';
        // Required for Firefox
        e.dataTransfer.setData('text/plain', menu.id!.toString());
    };

    const handleDragOver = (e: React.DragEvent<HTMLTableRowElement>, menu: Menu) => {
        e.preventDefault(); // Necessary to allow dropping
        if (draggedMenuId === null || draggedMenuId === menu.id) return;

        const draggedMenu = menus.find(m => m.id === draggedMenuId);
        if (!draggedMenu) return;

        // Only allow drop within same depth level (same parentId)
        if (draggedMenu.parentId === menu.parentId) {
            setDragOverMenuId(menu.id!);
            e.dataTransfer.dropEffect = 'move';
        } else {
            e.dataTransfer.dropEffect = 'none';
            setDragOverMenuId(null);
        }
    };

    const handleDragLeave = () => {
        setDragOverMenuId(null);
    };

    const handleDragEnd = () => {
        setDraggedMenuId(null);
        setDragOverMenuId(null);
    };

    const handleDrop = async (e: React.DragEvent<HTMLTableRowElement>, targetMenu: Menu) => {
        e.preventDefault();
        setDragOverMenuId(null);

        if (draggedMenuId === null || draggedMenuId === targetMenu.id) {
            setDraggedMenuId(null);
            return;
        }

        const draggedMenu = menus.find(m => m.id === draggedMenuId);
        if (!draggedMenu || draggedMenu.parentId !== targetMenu.parentId) {
            setDraggedMenuId(null);
            return;
        }

        const siblings = menus.filter(m => m.parentId === draggedMenu.parentId).sort((a, b) => a.sortOrder - b.sortOrder);
        const draggedIndex = siblings.findIndex(m => m.id === draggedMenuId);
        const targetIndex = siblings.findIndex(m => m.id === targetMenu.id);

        const updatedSiblings = [...siblings];
        updatedSiblings.splice(draggedIndex, 1);
        updatedSiblings.splice(targetIndex, 0, draggedMenu);

        updatedSiblings.forEach((sibling, idx) => {
            sibling.sortOrder = idx;
        });

        const newMenus = menus.map(m => {
            const updated = updatedSiblings.find(s => s.id === m.id);
            return updated ? updated : m;
        });

        setMenus(newMenus);

        // Save
        const mainMenusToSave = newMenus.filter(m => !m.parentId).sort((a, b) => a.sortOrder - b.sortOrder);
        const getSubMenusToSave = (parentId: number) => newMenus.filter(m => m.parentId === parentId).sort((a, b) => a.sortOrder - b.sortOrder);

        let flattenedIds: number[] = [];
        mainMenusToSave.forEach(main => {
            flattenedIds.push(main.id!);
            getSubMenusToSave(main.id!).forEach(sub => flattenedIds.push(sub.id!));
        });

        try {
            await fetch(`http://${window.location.hostname}:8080/api/admin/menus/reorder`, {
                method: 'POST',
                headers: { 'Content-Type': 'application/json' },
                body: JSON.stringify(flattenedIds)
            });
            fetchMenus();
        } catch (err) {
            console.error(err);
        }
        setDraggedMenuId(null);
    };

    // Derived states
    const mainMenus = menus.filter(m => !m.parentId).sort((a, b) => a.sortOrder - b.sortOrder);

    return (
        <div className="admin-menu-container">
            <div className="admin-menu-header">
                <div>
                    <h2>메뉴 관리 (GNB)</h2>
                    <p className="header-desc">메인 메뉴와 서브 메뉴(2뎁스) 계층을 설정합니다.</p>
                </div>
                <button className="btn-save" onClick={() => handleOpenModal()} style={{ display: 'flex', alignItems: 'center', gap: '0.4rem' }}>
                    <Plus size={18} /> 새 메인메뉴 등록
                </button>
            </div>

            <div className="admin-menu-list">
                <table className="menu-table">
                    <thead>
                        <tr>
                            <th className="col-drag">순서 조정</th>
                            <th>메뉴 트리</th>
                            <th>링크 (URL)</th>
                            <th className="col-status">상태</th>
                            <th className="col-actions">관리</th>
                        </tr>
                    </thead>
                    <tbody>
                        {mainMenus.map((mainMenu) => {
                            const subMenus = menus.filter(m => m.parentId === mainMenu.id).sort((a, b) => a.sortOrder - b.sortOrder);
                            return (
                                <React.Fragment key={`main-${mainMenu.id}`}>
                                    <tr className={`main-menu-row draggable-row ${draggedMenuId === mainMenu.id ? 'dragging' : ''} ${dragOverMenuId === mainMenu.id ? 'drag-over' : ''}`}
                                        draggable
                                        onDragStart={(e) => handleDragStart(e, mainMenu)}
                                        onDragOver={(e) => handleDragOver(e, mainMenu)}
                                        onDragLeave={handleDragLeave}
                                        onDrop={(e) => handleDrop(e, mainMenu)}
                                        onDragEnd={handleDragEnd}
                                    >
                                        <td className="col-drag">
                                            <div className="drag-handle">
                                                <GripVertical size={18} />
                                            </div>
                                        </td>
                                        <td className="col-title title-main">
                                            {mainMenu.title}
                                        </td>
                                        <td className="col-link">{mainMenu.linkUrl}</td>
                                        <td className="col-status">
                                            <span className={`status-badge ${mainMenu.isActive ? 'active' : 'inactive'}`}>
                                                {mainMenu.isActive ? '표출' : '숨김'}
                                            </span>
                                        </td>
                                        <td className="col-actions">
                                            <button className="btn-action btn-add-sub" onClick={() => handleOpenModal(undefined, mainMenu.id)} title="서브메뉴 추가">
                                                <Plus size={14} /> 서브 추가
                                            </button>
                                            <button className="btn-action btn-edit" onClick={() => handleOpenModal(mainMenu)} title="수정">
                                                <Edit2 size={14} /> 수정
                                            </button>
                                            <button className="btn-action btn-delete" onClick={() => mainMenu.id && handleDelete(mainMenu.id)} title="삭제">
                                                <Trash2 size={14} /> 삭제
                                            </button>
                                        </td>
                                    </tr>

                                    {subMenus.map((subMenu) => (
                                        <tr key={`sub-${subMenu.id}`}
                                            className={`sub-menu-row draggable-row ${draggedMenuId === subMenu.id ? 'dragging' : ''} ${dragOverMenuId === subMenu.id ? 'drag-over' : ''}`}
                                            draggable
                                            onDragStart={(e) => handleDragStart(e, subMenu)}
                                            onDragOver={(e) => handleDragOver(e, subMenu)}
                                            onDragLeave={handleDragLeave}
                                            onDrop={(e) => handleDrop(e, subMenu)}
                                            onDragEnd={handleDragEnd}
                                        >
                                            <td className="col-drag">
                                                <div className="drag-handle sub">
                                                    <GripVertical size={16} />
                                                </div>
                                            </td>
                                            <td className="col-title title-sub">
                                                <ArrowDownRight size={16} className="sub-icon" />
                                                {subMenu.title}
                                            </td>
                                            <td className="col-link sub-link">{subMenu.linkUrl}</td>
                                            <td className="col-status">
                                                <span className={`status-badge ${subMenu.isActive ? 'active' : 'inactive'} small`}>
                                                    {subMenu.isActive ? '표출' : '숨김'}
                                                </span>
                                            </td>
                                            <td className="col-actions">
                                                <button className="btn-action btn-edit" onClick={() => handleOpenModal(subMenu)} title="수정">
                                                    <Edit2 size={14} /> 수정
                                                </button>
                                                <button className="btn-action btn-delete" onClick={() => subMenu.id && handleDelete(subMenu.id)} title="삭제">
                                                    <Trash2 size={14} /> 삭제
                                                </button>
                                            </td>
                                        </tr>
                                    ))}
                                </React.Fragment>
                            );
                        })}
                        {mainMenus.length === 0 && (
                            <tr>
                                <td colSpan={5} style={{ textAlign: 'center', padding: '3rem 0', color: '#64748b' }}>
                                    등록된 메뉴가 없습니다. 새 메인메뉴를 등록해주세요.
                                </td>
                            </tr>
                        )}
                    </tbody>
                </table>
            </div>

            {isModalOpen && (
                <div className="admin-modal-overlay">
                    <div className="admin-modal-content" style={{ maxWidth: '450px' }}>
                        <div className="modal-header">
                            <h3>
                                {currentMenu.id ? '메뉴 수정' : (currentMenu.parentId ? '새 서브메뉴 등록' : '새 메인메뉴 등록')}
                            </h3>
                            <button className="btn-close-modal" onClick={handleCloseModal}>✕</button>
                        </div>
                        <div className="modal-body">
                            {currentMenu.parentId && !currentMenu.id && (
                                <div className="modal-info-box">
                                    <strong>안내:</strong> 부모 메뉴({menus.find(m => m.id === currentMenu.parentId)?.title})의 하위 메뉴로 등록됩니다.
                                </div>
                            )}
                            <form onSubmit={handleSubmit} className="admin-menu-form">
                                <div className="input-group">
                                    <label>메뉴명</label>
                                    <input type="text" name="title" value={currentMenu.title} onChange={handleChange} required placeholder="ex) 회사 소개" />
                                </div>
                                <div className="input-group">
                                    <label>연결 링크 (URL)</label>
                                    <select
                                        value={availableUrls.some(u => u.url === currentMenu.linkUrl) ? currentMenu.linkUrl : '__custom__'}
                                        onChange={(e) => {
                                            if (e.target.value === '__custom__') return;
                                            setCurrentMenu(prev => ({ ...prev, linkUrl: e.target.value }));
                                        }}
                                        style={{ width: '100%', padding: '0.6rem 0.8rem', marginBottom: '0.5rem', background: 'var(--input-bg)', border: '1px solid var(--input-border)', borderRadius: '8px', color: 'var(--text-primary)', fontSize: '0.95rem' }}
                                    >
                                        <option value="__custom__">-- 직접 입력 --</option>
                                        {availableUrls.map(opt => (
                                            <option key={opt.url} value={opt.url}>{opt.label} ({opt.url})</option>
                                        ))}
                                    </select>
                                    <input type="text" name="linkUrl" value={currentMenu.linkUrl} onChange={handleChange} required placeholder="ex) /about 혹은 https://..." />
                                </div>
                                <div className="input-group checkbox-group" style={{ marginTop: '1.5rem', marginBottom: '1.5rem' }}>
                                    <label style={{ cursor: 'pointer', display: 'flex', alignItems: 'center' }}>
                                        <input type="checkbox" name="isActive" checked={currentMenu.isActive} onChange={handleChange} style={{ marginRight: '8px', width: '18px', height: '18px' }} />
                                        <span style={{ fontWeight: 600 }}>사용 (사용자 화면 표출)</span>
                                    </label>
                                </div>
                                <div className="modal-actions" style={{ display: 'flex', gap: '0.8rem' }}>
                                    <button type="button" className="btn-cancel" onClick={handleCloseModal}>
                                        취소
                                    </button>
                                    <button type="submit" className="btn-submit" disabled={loading}>
                                        {loading ? '저장 중...' : '저장 완료'}
                                    </button>
                                </div>
                            </form>
                        </div>
                    </div>
                </div>
            )}
        </div>
    );
};

export default AdminMenu;
