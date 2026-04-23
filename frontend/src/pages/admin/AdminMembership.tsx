import { useState, useEffect } from 'react';
import { Plus, Edit2, Trash2, Award } from 'lucide-react';
import './AdminMembership.css';

interface Grade {
    id: number;
    code: string;
    name: string;
    description: string;
    minPoints: number;
    pointRate: number;
    benefits: string;
    color: string;
    sortOrder: number;
    isActive: boolean;
}

const defaultFormData: Omit<Grade, 'id'> & { id?: number } = {
    code: '',
    name: '',
    description: '',
    minPoints: 0,
    pointRate: 1,
    benefits: '',
    color: '#4f46e5',
    sortOrder: 0,
    isActive: true
};

const AdminMembership = () => {
    const [grades, setGrades] = useState<Grade[]>([]);
    const [isModalOpen, setIsModalOpen] = useState(false);
    const [editingId, setEditingId] = useState<number | null>(null);
    const [formData, setFormData] = useState<any>(defaultFormData);

    const fetchGrades = async () => {
        try {
            const token = localStorage.getItem('accessToken');
            const headers: any = {};
            if (token) headers['Authorization'] = `Bearer ${token}`;

            const res = await fetch(`http://${window.location.hostname}:8080/api/admin/membership/grades`, { headers });
            if (res.ok) {
                setGrades(await res.json());
            }
        } catch (err) {
            console.error(err);
        }
    };

    useEffect(() => {
        fetchGrades();
    }, []);

    useEffect(() => {
        const handleEsc = (e: KeyboardEvent) => {
            if (e.key === 'Escape' && isModalOpen) closeModal();
        };
        document.addEventListener('keydown', handleEsc);
        return () => document.removeEventListener('keydown', handleEsc);
    }, [isModalOpen]);

    const closeModal = () => {
        setIsModalOpen(false);
        setEditingId(null);
        setFormData({ ...defaultFormData });
    };

    const openCreate = () => {
        setEditingId(null);
        setFormData({ ...defaultFormData });
        setIsModalOpen(true);
    };

    const openEdit = (grade: Grade) => {
        setEditingId(grade.id);
        setFormData({ ...grade });
        setIsModalOpen(true);
    };

    const handleChange = (e: React.ChangeEvent<HTMLInputElement | HTMLTextAreaElement>) => {
        const { name, value, type } = e.target;
        if (type === 'checkbox') {
            setFormData({ ...formData, [name]: (e.target as HTMLInputElement).checked });
        } else if (type === 'number') {
            setFormData({ ...formData, [name]: Number(value) });
        } else {
            setFormData({ ...formData, [name]: value });
        }
    };

    const handleSubmit = async (e: React.FormEvent) => {
        e.preventDefault();
        try {
            const token = localStorage.getItem('accessToken');
            const headers: any = { 'Content-Type': 'application/json' };
            if (token) headers['Authorization'] = `Bearer ${token}`;

            const url = editingId
                ? `http://${window.location.hostname}:8080/api/admin/membership/grades/${editingId}`
                : `http://${window.location.hostname}:8080/api/admin/membership/grades`;

            const res = await fetch(url, {
                method: editingId ? 'PUT' : 'POST',
                headers,
                body: JSON.stringify(formData)
            });

            if (res.ok) {
                closeModal();
                fetchGrades();
            } else {
                alert('저장에 실패했습니다.');
            }
        } catch (err) {
            console.error(err);
            alert('저장 중 오류가 발생했습니다.');
        }
    };

    const handleDelete = async (id: number) => {
        if (!confirm('정말 삭제하시겠습니까?')) return;
        try {
            const token = localStorage.getItem('accessToken');
            const headers: any = {};
            if (token) headers['Authorization'] = `Bearer ${token}`;

            const res = await fetch(`http://${window.location.hostname}:8080/api/admin/membership/grades/${id}`, {
                method: 'DELETE',
                headers
            });
            if (res.ok) {
                fetchGrades();
            } else {
                alert('삭제에 실패했습니다.');
            }
        } catch (err) {
            console.error(err);
        }
    };

    return (
        <div className="admin-membership-container">
            <div className="admin-products-header">
                <div>
                    <h2><Award size={24} style={{ verticalAlign: 'middle', marginRight: '0.5rem' }} />멤버십 등급 관리</h2>
                    <p className="header-desc">멤버십 등급을 생성하고 관리합니다.</p>
                </div>
                <button className="btn-add-product" onClick={openCreate}>
                    <Plus size={18} /> 새 등급 추가
                </button>
            </div>

            <div className="grade-grid">
                {grades.map(grade => (
                    <div className="grade-card" key={grade.id}>
                        <div className="grade-card-header" style={{ background: grade.color || '#4f46e5' }}>
                            {grade.name}
                        </div>
                        <div className="grade-card-body">
                            <p><strong>코드:</strong> {grade.code}</p>
                            <p><strong>최소 포인트:</strong> {grade.minPoints.toLocaleString()}P</p>
                            <p><strong>적립률:</strong> {grade.pointRate}%</p>
                            {grade.description && <p><strong>설명:</strong> {grade.description}</p>}
                            {grade.benefits && (
                                <div style={{ marginTop: '0.5rem' }}>
                                    <strong style={{ fontSize: '0.9rem' }}>혜택:</strong>
                                    <ul style={{ margin: '0.3rem 0 0', paddingLeft: '1.2rem', fontSize: '0.85rem', color: 'var(--text-color)' }}>
                                        {grade.benefits.split('\n').filter(b => b.trim()).map((benefit, idx) => (
                                            <li key={idx}>{benefit.trim()}</li>
                                        ))}
                                    </ul>
                                </div>
                            )}
                            {!grade.isActive && (
                                <p style={{ color: '#ff5252', fontWeight: 600, marginTop: '0.5rem' }}>비활성</p>
                            )}
                        </div>
                        <div className="grade-card-actions">
                            <button className="btn-edit-small" onClick={() => openEdit(grade)}>
                                <Edit2 size={14} /> 수정
                            </button>
                            <button className="btn-delete-small" onClick={() => handleDelete(grade.id)}>
                                <Trash2 size={14} /> 삭제
                            </button>
                        </div>
                    </div>
                ))}
                {grades.length === 0 && (
                    <div style={{ gridColumn: '1 / -1', textAlign: 'center', padding: '3rem', color: 'var(--text-disabled)' }}>
                        등록된 등급이 없습니다. 새 등급을 추가해주세요.
                    </div>
                )}
            </div>

            {isModalOpen && (
                <div className="membership-modal-overlay" onClick={closeModal}>
                    <div className="membership-modal-content" onClick={e => e.stopPropagation()}>
                        <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: '1.5rem' }}>
                            <h3 style={{ margin: 0, fontSize: '1.3rem', fontWeight: 700 }}>
                                {editingId ? '등급 수정' : '새 등급 추가'}
                            </h3>
                            <button
                                onClick={closeModal}
                                style={{ background: 'none', border: 'none', color: 'var(--text-primary)', cursor: 'pointer', fontSize: '1.5rem' }}
                            >
                                &times;
                            </button>
                        </div>
                        <form onSubmit={handleSubmit} style={{ display: 'flex', flexDirection: 'column', gap: '1rem' }}>
                            <div className="form-group">
                                <label>코드</label>
                                <input type="text" name="code" value={formData.code} onChange={handleChange} required />
                            </div>
                            <div className="form-group">
                                <label>이름</label>
                                <input type="text" name="name" value={formData.name} onChange={handleChange} required />
                            </div>
                            <div className="form-group">
                                <label>설명</label>
                                <input type="text" name="description" value={formData.description} onChange={handleChange} />
                            </div>
                            <div className="form-group">
                                <label>최소 포인트</label>
                                <input type="number" name="minPoints" value={formData.minPoints} onChange={handleChange} />
                            </div>
                            <div className="form-group">
                                <label>적립률 (%)</label>
                                <input type="number" name="pointRate" value={formData.pointRate} onChange={handleChange} step="0.1" />
                            </div>
                            <div className="form-group">
                                <label>혜택 (줄바꿈으로 구분)</label>
                                <textarea name="benefits" value={formData.benefits} onChange={handleChange} rows={4}
                                    style={{
                                        width: '100%',
                                        padding: '0.75rem 1rem',
                                        border: '1px solid var(--input-border)',
                                        borderRadius: '6px',
                                        background: 'var(--input-bg)',
                                        color: 'var(--input-text)',
                                        fontSize: '0.95rem',
                                        resize: 'vertical'
                                    }}
                                />
                            </div>
                            <div className="form-group">
                                <label>색상</label>
                                <input type="color" name="color" value={formData.color} onChange={handleChange}
                                    style={{ width: '60px', height: '40px', border: 'none', cursor: 'pointer', background: 'transparent' }}
                                />
                            </div>
                            <div className="form-group">
                                <label>정렬 순서</label>
                                <input type="number" name="sortOrder" value={formData.sortOrder} onChange={handleChange} />
                            </div>
                            <div className="form-group checkbox" style={{ display: 'flex', alignItems: 'center' }}>
                                <label style={{ display: 'flex', alignItems: 'center', gap: '0.5rem' }}>
                                    <input type="checkbox" name="isActive" checked={formData.isActive} onChange={handleChange} />
                                    활성화
                                </label>
                            </div>
                            <div className="form-actions">
                                <button type="submit" className="save-btn">저장</button>
                                <button type="button" onClick={closeModal} className="cancel-btn">취소</button>
                            </div>
                        </form>
                    </div>
                </div>
            )}
        </div>
    );
};

export default AdminMembership;
