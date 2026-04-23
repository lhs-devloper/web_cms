import React, { useState, useEffect } from 'react';
import { Plus, Edit2, Trash2 } from 'lucide-react';
import './AdminProducts.css';
import './AdminSetting.css';

interface ProductCategory {
    id: number;
    code: string;
    name: string;
    description: string;
    hasStock: boolean;
    hasRentalPeriod: boolean;
    isActive: boolean;
    sortOrder: number;
}

const AdminProductCategory: React.FC = () => {
    const [categories, setCategories] = useState<ProductCategory[]>([]);
    const [isFormOpen, setIsFormOpen] = useState(false);
    const [editingId, setEditingId] = useState<number | null>(null);
    const [formData, setFormData] = useState<Partial<ProductCategory>>({
        code: '',
        name: '',
        description: '',
        hasStock: false,
        hasRentalPeriod: false,
        isActive: true,
        sortOrder: 0
    });

    const API_BASE = `/api/admin/product-categories`;

    useEffect(() => {
        fetchCategories();
    }, []);

    const fetchCategories = async () => {
        try {
            const res = await fetch(API_BASE);
            if (res.ok) {
                const data = await res.json();
                setCategories(data);
            }
        } catch (err) {
            console.error(err);
        }
    };

    const handleChange = (e: React.ChangeEvent<HTMLInputElement | HTMLTextAreaElement>) => {
        const { name, value, type } = e.target;
        if (type === 'checkbox') {
            const checked = (e.target as HTMLInputElement).checked;
            setFormData({ ...formData, [name]: checked });
        } else if (type === 'number') {
            setFormData({ ...formData, [name]: Number(value) });
        } else {
            setFormData({ ...formData, [name]: value });
        }
    };

    const handleSubmit = async (e: React.FormEvent) => {
        e.preventDefault();
        try {
            if (editingId) {
                const res = await fetch(`${API_BASE}/${editingId}`, {
                    method: 'PUT',
                    headers: { 'Content-Type': 'application/json' },
                    body: JSON.stringify(formData)
                });
                if (!res.ok) throw new Error('수정 실패');
            } else {
                const res = await fetch(API_BASE, {
                    method: 'POST',
                    headers: { 'Content-Type': 'application/json' },
                    body: JSON.stringify(formData)
                });
                if (!res.ok) throw new Error('등록 실패');
            }
            closeForm();
            fetchCategories();
        } catch (err) {
            console.error(err);
            alert('저장 중 오류가 발생했습니다.');
        }
    };

    const closeForm = () => {
        setIsFormOpen(false);
        setEditingId(null);
        setFormData({ code: '', name: '', description: '', hasStock: false, hasRentalPeriod: false, isActive: true, sortOrder: 0 });
    };

    const openFormNew = () => {
        setEditingId(null);
        setFormData({ code: '', name: '', description: '', hasStock: false, hasRentalPeriod: false, isActive: true, sortOrder: 0 });
        setIsFormOpen(true);
    };

    const handleEdit = (cat: ProductCategory) => {
        setEditingId(cat.id);
        setFormData({ ...cat });
        setIsFormOpen(true);
    };

    const handleDelete = async (id: number) => {
        if (!confirm('정말 삭제하시겠습니까?')) return;
        try {
            await fetch(`${API_BASE}/${id}`, { method: 'DELETE' });
            fetchCategories();
        } catch (err) {
            console.error(err);
        }
    };

    useEffect(() => {
        const handleEsc = (e: KeyboardEvent) => {
            if (e.key === 'Escape' && isFormOpen) closeForm();
        };
        document.addEventListener('keydown', handleEsc);
        return () => document.removeEventListener('keydown', handleEsc);
    }, [isFormOpen]);

    return (
        <div className="admin-products-container">
            <div className="admin-products-header">
                <div>
                    <h2>상품 타입 관리</h2>
                    <p className="header-desc">상품 카테고리(타입)를 등록하고 관리합니다.</p>
                </div>
                <button className="btn-add-product" onClick={openFormNew}>
                    <Plus size={18} /> 새 타입 등록
                </button>
            </div>

            <div className="products-list-section">
                <h3>등록된 상품 타입 목록</h3>
                <table className="products-table">
                    <thead>
                        <tr>
                            <th>코드</th>
                            <th>이름</th>
                            <th>설명</th>
                            <th>재고관리</th>
                            <th>대여기간관리</th>
                            <th>활성화</th>
                            <th>정렬순서</th>
                            <th>관리</th>
                        </tr>
                    </thead>
                    <tbody>
                        {categories.map(cat => (
                            <tr key={cat.id}>
                                <td style={{ fontWeight: 700, color: 'var(--primary-color)' }}>{cat.code}</td>
                                <td>{cat.name}</td>
                                <td>{cat.description}</td>
                                <td>{cat.hasStock ? 'O' : '-'}</td>
                                <td>{cat.hasRentalPeriod ? 'O' : '-'}</td>
                                <td>
                                    {cat.isActive ?
                                        <span className="product-status-badge active">활성</span> :
                                        <span className="product-status-badge inactive">비활성</span>
                                    }
                                </td>
                                <td>{cat.sortOrder}</td>
                                <td>
                                    <div className="action-btns">
                                        <button onClick={() => handleEdit(cat)} className="btn-edit-small"><Edit2 size={14} /> 수정</button>
                                        <button onClick={() => handleDelete(cat.id)} className="btn-delete-small"><Trash2 size={14} /> 삭제</button>
                                    </div>
                                </td>
                            </tr>
                        ))}
                        {categories.length === 0 && (
                            <tr>
                                <td colSpan={8} className="empty-message">
                                    등록된 상품 타입이 없습니다. 새 타입을 등록해주세요.
                                </td>
                            </tr>
                        )}
                    </tbody>
                </table>
            </div>

            {isFormOpen && (
                <div className="product-modal-overlay" onClick={closeForm}>
                    <div className="product-modal-content" onClick={e => e.stopPropagation()}>
                        <div className="product-modal-header">
                            <h3>{editingId ? '상품 타입 수정' : '새 상품 타입 등록'}</h3>
                            <button className="product-modal-close" onClick={closeForm}>&times;</button>
                        </div>
                        <form onSubmit={handleSubmit} className="product-form" style={{ maxWidth: '100%', padding: '0', background: 'none' }}>
                            <div className="form-group">
                                <label>코드</label>
                                <input type="text" name="code" className="form-control" value={formData.code} onChange={handleChange} required placeholder="예: NORMAL, RENTAL" />
                            </div>
                            <div className="form-group">
                                <label>이름</label>
                                <input type="text" name="name" className="form-control" value={formData.name} onChange={handleChange} required placeholder="예: 일반상품, 대여상품" />
                            </div>
                            <div className="form-group">
                                <label>설명</label>
                                <input type="text" name="description" className="form-control" value={formData.description} onChange={handleChange} placeholder="카테고리 설명" />
                            </div>
                            <div className="form-group checkbox">
                                <label>
                                    <input type="checkbox" name="hasStock" checked={formData.hasStock} onChange={handleChange} />
                                    재고 관리
                                </label>
                            </div>
                            <div className="form-group checkbox">
                                <label>
                                    <input type="checkbox" name="hasRentalPeriod" checked={formData.hasRentalPeriod} onChange={handleChange} />
                                    대여 기간 관리
                                </label>
                            </div>
                            <div className="form-group checkbox">
                                <label>
                                    <input type="checkbox" name="isActive" checked={formData.isActive} onChange={handleChange} />
                                    활성화
                                </label>
                            </div>
                            <div className="form-group">
                                <label>정렬 순서</label>
                                <input type="number" name="sortOrder" className="form-control" value={formData.sortOrder} onChange={handleChange} />
                            </div>
                            <div className="form-actions">
                                <button type="submit" className="save-btn">저장</button>
                                <button type="button" onClick={closeForm} className="cancel-btn">취소</button>
                            </div>
                        </form>
                    </div>
                </div>
            )}
        </div>
    );
};

export default AdminProductCategory;
