import React, { useState, useEffect, useRef, useMemo } from 'react';
import ReactQuill from 'react-quill-new';
import 'react-quill-new/dist/quill.snow.css';
import { Upload, X, Plus } from 'lucide-react';
import './AdminProducts.css';

interface Product {
    id: number;
    name: string;
    price: number;
    description: string;
    type: 'NORMAL' | 'RENTAL';
    stockQuantity: number;
    rentalAvailableCount: number;
    active: boolean;
    imageUrls: string[];
}

const AdminProducts: React.FC = () => {
    const [products, setProducts] = useState<Product[]>([]);
    const [isFormOpen, setIsFormOpen] = useState(false);
    const [editingId, setEditingId] = useState<number | null>(null);
    const [selectedFiles, setSelectedFiles] = useState<File[]>([]);
    const [previewUrls, setPreviewUrls] = useState<string[]>([]);
    const [isDragging, setIsDragging] = useState(false);
    const fileInputRef = useRef<HTMLInputElement>(null);

    const [formData, setFormData] = useState<Partial<Product>>({
        name: '',
        price: 0,
        description: '',
        type: 'NORMAL',
        stockQuantity: 0,
        rentalAvailableCount: 0,
        active: true,
        imageUrls: []
    });

    useEffect(() => {
        fetchProducts();
    }, []);

    const fetchProducts = async () => {
        try {
            const res = await fetch('/api/admin/products');
            const data = await res.json();
            setProducts(data);
        } catch (err) {
            console.error(err);
        }
    };

    const handleChange = (e: React.ChangeEvent<HTMLInputElement | HTMLSelectElement | HTMLTextAreaElement>) => {
        const { name, value, type } = e.target;
        if (type === 'checkbox') {
            const checked = (e.target as HTMLInputElement).checked;
            setFormData({ ...formData, [name]: checked });
        } else {
            setFormData({ ...formData, [name]: type === 'number' ? Number(value) : value });
        }
    };

    const handleImageChange = (e: React.ChangeEvent<HTMLInputElement>) => {
        if (!e.target.files || e.target.files.length === 0) return;
        addFiles(Array.from(e.target.files));
    };

    const addFiles = (files: File[]) => {
        const imageFiles = files.filter(file => file.type.startsWith('image/'));
        setSelectedFiles(prev => [...prev, ...imageFiles]);

        const newPreviewUrls = imageFiles.map(file => URL.createObjectURL(file));
        setPreviewUrls(prev => [...prev, ...newPreviewUrls]);
    };

    const handleDragOver = (e: React.DragEvent) => {
        e.preventDefault();
        setIsDragging(true);
    };

    const handleDragLeave = () => {
        setIsDragging(false);
    };

    const handleDrop = (e: React.DragEvent) => {
        e.preventDefault();
        setIsDragging(false);
        if (e.dataTransfer.files && e.dataTransfer.files.length > 0) {
            addFiles(Array.from(e.dataTransfer.files));
        }
    };

    const removeExistingImage = (index: number) => {
        setFormData(prev => ({
            ...prev,
            imageUrls: prev.imageUrls?.filter((_, i) => i !== index)
        }));
    };

    const removeNewImage = (index: number) => {
        setSelectedFiles(prev => prev.filter((_, i) => i !== index));
        setPreviewUrls(prev => prev.filter((_, i) => i !== index));
    };

    const handleSubmit = async (e: React.FormEvent) => {
        e.preventDefault();
        try {
            let finalImageUrls = [...(formData.imageUrls || [])];

            if (selectedFiles.length > 0) {
                // Upload each file
                for (const file of selectedFiles) {
                    const formDataObj = new FormData();
                    formDataObj.append('file', file);
                    const res = await fetch('/api/files/upload', {
                        method: 'POST',
                        body: formDataObj
                    });
                    const data = await res.json();
                    if (res.ok) {
                        finalImageUrls.push(data.url);
                    } else {
                        alert(data.message || '이미지 업로드에 실패했습니다.');
                        return;
                    }
                }
            }

            const payload = { ...formData, imageUrls: finalImageUrls };

            if (editingId) {
                await fetch(`/api/admin/products/${editingId}`, {
                    method: 'PUT',
                    headers: { 'Content-Type': 'application/json' },
                    body: JSON.stringify(payload)
                });
            } else {
                await fetch('/api/admin/products', {
                    method: 'POST',
                    headers: { 'Content-Type': 'application/json' },
                    body: JSON.stringify(payload)
                });
            }
            closeForm();
            fetchProducts();
        } catch (err) {
            console.error(err);
            alert('저장 중 오류가 발생했습니다.');
        }
    };

    const closeForm = () => {
        setIsFormOpen(false);
        setEditingId(null);
        setFormData({ name: '', price: 0, description: '', type: 'NORMAL', stockQuantity: 0, rentalAvailableCount: 0, active: true, imageUrls: [] });
        setSelectedFiles([]);
        setPreviewUrls([]);
    };

    const openFormNew = () => {
        setEditingId(null);
        setFormData({ name: '', price: 0, description: '', type: 'NORMAL', stockQuantity: 0, rentalAvailableCount: 0, active: true, imageUrls: [] });
        setSelectedFiles([]);
        setPreviewUrls([]);
        setIsFormOpen(true);
    };

    const handleEdit = (product: Product) => {
        setEditingId(product.id);
        setFormData({ ...product, imageUrls: product.imageUrls || [] });
        setSelectedFiles([]);
        setPreviewUrls([]);
        setIsFormOpen(true);
    };

    const handleDelete = async (id: number) => {
        if (!confirm('정말 삭제하시겠습니까?')) return;
        try {
            await fetch(`/api/admin/products/${id}`, { method: 'DELETE' });
            fetchProducts();
        } catch (err) {
            console.error(err);
        }
    };

    const imageHandler = () => {
        const input = document.createElement('input');
        input.setAttribute('type', 'file');
        input.setAttribute('accept', 'image/*');
        input.click();

        input.onchange = async () => {
            const file = input.files ? input.files[0] : null;
            if (file) {
                const formDataObj = new FormData();
                formDataObj.append('file', file);

                try {
                    const res = await fetch('/api/files/upload', {
                        method: 'POST',
                        body: formDataObj
                    });
                    const data = await res.json();
                    if (res.ok) {
                        const quill = (document.querySelector('.ql-container') as any)?.__quill;
                        if (quill) {
                            const range = quill.getSelection();
                            quill.insertEmbed(range.index, 'image', data.url);
                        }
                    } else {
                        alert('이미지 업로드에 실패했습니다.');
                    }
                } catch (error) {
                    console.error('Editor image upload error:', error);
                }
            }
        };
    };

    const modules = useMemo(() => ({
        toolbar: {
            container: [
                [{ 'header': [1, 2, 3, false] }],
                ['bold', 'italic', 'underline', 'strike'],
                [{ 'list': 'ordered' }, { 'list': 'bullet' }],
                ['link', 'image'],
                ['clean']
            ],
            handlers: {
                image: imageHandler
            }
        }
    }), []);

    const formats = [
        'header',
        'bold', 'italic', 'underline', 'strike',
        'list', 'bullet',
        'link', 'image'
    ];

    return (
        <div className="admin-products-container">
            <h2>상품 관리</h2>

            <button className="add-btn" onClick={openFormNew}>새 상품 등록</button>
            <table className="products-table">
                <thead>
                    <tr>
                        <th>ID</th>
                        <th>상품명</th>
                        <th>타입</th>
                        <th>가격</th>
                        <th>상태</th>
                        <th>관리</th>
                    </tr>
                </thead>
                <tbody>
                    {products.map(p => (
                        <tr key={p.id}>
                            <td>{p.id}</td>
                            <td>{p.name}</td>
                            <td>{p.type === 'NORMAL' ? '일반상픔' : '대여상품'}</td>
                            <td>{p.price.toLocaleString()}원</td>
                            <td>{p.active ? '활성' : '비활성'}</td>
                            <td>
                                <button onClick={() => handleEdit(p)}>수정</button>
                                <button onClick={() => handleDelete(p.id)} className="delete-btn">삭제</button>
                            </td>
                        </tr>
                    ))}
                </tbody>
            </table>

            {isFormOpen && (
                <div className="product-modal-overlay" onClick={closeForm}>
                    <div className="product-modal-content" onClick={e => e.stopPropagation()}>
                        <div className="product-modal-header">
                            <h3>{editingId ? '상품 수정' : '새 상품 등록'}</h3>
                            <button className="product-modal-close" onClick={closeForm}>&times;</button>
                        </div>
                        <form onSubmit={handleSubmit} className="product-form" style={{ maxWidth: '100%', padding: '0', background: 'none' }}>
                            <div className="form-group">
                                <label>상품명</label>
                                <input type="text" name="name" value={formData.name} onChange={handleChange} required />
                            </div>
                            <div className="form-group">
                                <label>타입</label>
                                <select name="type" value={formData.type} onChange={handleChange}>
                                    <option value="NORMAL">일반상품</option>
                                    <option value="RENTAL">대여상품 (렌터카 등)</option>
                                </select>
                            </div>
                            <div className="form-group">
                                <label>가격</label>
                                <input type="number" name="price" value={formData.price} onChange={handleChange} required />
                            </div>

                            {formData.type === 'NORMAL' && (
                                <div className="form-group">
                                    <label>재고 수량</label>
                                    <input type="number" name="stockQuantity" value={formData.stockQuantity} onChange={handleChange} />
                                </div>
                            )}

                            {formData.type === 'RENTAL' && (
                                <div className="form-group">
                                    <label>대여 가능 수량 (차량 대수 등)</label>
                                    <input type="number" name="rentalAvailableCount" value={formData.rentalAvailableCount} onChange={handleChange} />
                                </div>
                            )}

                            <div className="form-group" style={{ marginBottom: '50px' }}>
                                <label>상세 설명</label>
                                <ReactQuill
                                    theme="snow"
                                    value={formData.description || ''}
                                    onChange={(value) => setFormData({ ...formData, description: value })}
                                    modules={modules}
                                    formats={formats}
                                    style={{ height: '300px' }}
                                />
                            </div>
                            <div className="form-group">
                                <label>상품 이미지</label>
                                <div
                                    className={`image-upload-dropzone ${isDragging ? 'dragging' : ''}`}
                                    onDragOver={handleDragOver}
                                    onDragLeave={handleDragLeave}
                                    onDrop={handleDrop}
                                    onClick={() => fileInputRef.current?.click()}
                                >
                                    <input
                                        type="file"
                                        accept="image/*"
                                        multiple
                                        onChange={handleImageChange}
                                        ref={fileInputRef}
                                        style={{ display: 'none' }}
                                    />
                                    <div className="dropzone-content">
                                        <Upload size={32} className="upload-icon" />
                                        <p>이미지를 드래그하여 놓거나 클릭하여 선택하세요</p>
                                        <span>(다중 선택 가능)</span>
                                    </div>
                                </div>

                                <div className="image-preview-grid">
                                    {formData.imageUrls && formData.imageUrls.map((url, idx) => (
                                        <div key={`existing-${idx}`} className="preview-item">
                                            <img src={url} alt="existing preview" />
                                            <button type="button" className="remove-btn" onClick={(e) => { e.stopPropagation(); removeExistingImage(idx); }}>
                                                <X size={14} />
                                            </button>
                                            <div className="badge existing">기존</div>
                                        </div>
                                    ))}
                                    {previewUrls.map((url, idx) => (
                                        <div key={`new-${idx}`} className="preview-item">
                                            <img src={url} alt="new preview" />
                                            <button type="button" className="remove-btn" onClick={(e) => { e.stopPropagation(); removeNewImage(idx); }}>
                                                <X size={14} />
                                            </button>
                                            <div className="badge new">신규</div>
                                        </div>
                                    ))}
                                    {((formData.imageUrls?.length || 0) + previewUrls.length) > 0 && (
                                        <div className="preview-item add-more" onClick={() => fileInputRef.current?.click()}>
                                            <Plus size={24} />
                                            <span>추가</span>
                                        </div>
                                    )}
                                </div>
                            </div>
                            <div className="form-group checkbox">
                                <label>
                                    <input type="checkbox" name="active" checked={formData.active} onChange={handleChange} />
                                    활성화
                                </label>
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

export default AdminProducts;
