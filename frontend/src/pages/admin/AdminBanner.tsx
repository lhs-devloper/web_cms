import { useState, useEffect, useRef } from 'react';
import './AdminBanner.css';

interface BannerButton {
    text: string;
    linkUrl: string;
    bgColor: string;
    textColor: string;
}

interface Banner {
    id?: number;
    subtitle: string;
    title: string;
    description: string;
    imageUrl?: string;
    linkUrl: string;
    titleFontSize: string;
    titleColor: string;
    subtitleColor: string;
    textAlignment: string;
    buttonsJson?: string;
    buttons: BannerButton[];
    sortOrder: number;
    isActive: boolean;
}

const emptyBanner: Banner = {
    subtitle: 'New Collection',
    title: 'ELEVATE YOUR EVERYDAY.',
    description: '고품격 라이프스타일을 위한 프리미엄 셀렉션.',
    linkUrl: '#',
    titleFontSize: '5.5rem',
    titleColor: '#ffffff',
    subtitleColor: '#00d2ff',
    textAlignment: 'left',
    buttonsJson: '[]',
    buttons: [{ text: 'SHOP NOW', linkUrl: '#', bgColor: '#ffffff', textColor: '#000000' }],
    sortOrder: 0,
    isActive: true
};

const AdminBanner = () => {
    const [banners, setBanners] = useState<Banner[]>([]);
    const [loading, setLoading] = useState(false);
    const [isModalOpen, setIsModalOpen] = useState(false);
    const [currentBanner, setCurrentBanner] = useState<Banner>(emptyBanner);
    const [selectedFile, setSelectedFile] = useState<File | null>(null);
    const fileInputRef = useRef<HTMLInputElement>(null);

    const fetchBanners = async () => {
        try {
            const res = await fetch(`http://${window.location.hostname}:8080/api/admin/banners`);
            const data = await res.json();
            if (data.banners) {
                // Parse buttonsJson to use in frontend
                const mappedBanners = data.banners.map((b: any) => ({
                    ...b,
                    buttons: b.buttonsJson ? JSON.parse(b.buttonsJson) : []
                }));
                setBanners(mappedBanners);
            }
        } catch (error) {
            console.error('Error fetching banners:', error);
        }
    };

    useEffect(() => {
        fetchBanners();
    }, []);

    const handleOpenModal = (banner?: Banner) => {
        if (banner) {
            setCurrentBanner({ ...banner, buttons: banner.buttonsJson ? JSON.parse(banner.buttonsJson) : [] });
        } else {
            setCurrentBanner({ ...emptyBanner, buttons: [{ text: 'SHOP NOW', linkUrl: '#', bgColor: '#ffffff', textColor: '#000000' }] });
        }
        setSelectedFile(null);
        setIsModalOpen(true);
    };

    const handleCloseModal = () => {
        setIsModalOpen(false);
    };

    const handleChange = (e: React.ChangeEvent<HTMLInputElement | HTMLSelectElement | HTMLTextAreaElement>) => {
        const { name, value, type } = e.target;
        const checked = type === 'checkbox' ? (e.target as HTMLInputElement).checked : undefined;
        setCurrentBanner(prev => ({
            ...prev,
            [name]: type === 'checkbox' ? checked : value
        }));
    };

    const handleButtonChange = (index: number, field: keyof BannerButton, value: string) => {
        setCurrentBanner(prev => {
            const newButtons = [...prev.buttons];
            newButtons[index] = { ...newButtons[index], [field]: value };
            return { ...prev, buttons: newButtons };
        });
    };

    const handleAddButton = () => {
        setCurrentBanner(prev => ({
            ...prev,
            buttons: [...prev.buttons, { text: 'NEW BUTTON', linkUrl: '#', bgColor: '#00d2ff', textColor: '#000000' }]
        }));
    };

    const handleRemoveButton = (index: number) => {
        setCurrentBanner(prev => ({
            ...prev,
            buttons: prev.buttons.filter((_, i) => i !== index)
        }));
    };

    const handleFileChange = (e: React.ChangeEvent<HTMLInputElement>) => {
        if (e.target.files && e.target.files.length > 0) {
            setSelectedFile(e.target.files[0]);
        }
    };

    const handleSubmit = async (e: React.FormEvent) => {
        e.preventDefault();
        setLoading(true);

        const formData = new FormData();
        Object.keys(currentBanner).forEach(key => {
            if (key !== 'imageUrl' && key !== 'buttons' && key !== 'buttonsJson') {
                const value = currentBanner[key as keyof Banner];
                if (value !== undefined && value !== null) {
                    formData.append(key, String(value));
                }
            }
        });

        formData.append('buttonsJson', JSON.stringify(currentBanner.buttons));

        if (selectedFile) {
            formData.append('file', selectedFile);
        }

        try {
            const url = `http://${window.location.hostname}:8080/api/admin/banners/save`;
            const res = await fetch(url, {
                method: 'POST',
                body: formData
            });

            if (res.ok) {
                alert('배너가 저장되었습니다.');
                fetchBanners();
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
        if (!confirm('정말 삭제하시겠습니까?')) return;
        try {
            const res = await fetch(`http://${window.location.hostname}:8080/api/admin/banners/delete?id=${id}`, { method: 'POST' });
            if (res.ok) fetchBanners();
        } catch (error) {
            console.error(error);
        }
    };

    return (
        <div className="admin-banner-container">
            <div className="admin-banner-header">
                <h2>메인 배너 관리</h2>
                <button className="btn-save" onClick={() => handleOpenModal()}>+ 새 배너 등록</button>
            </div>

            <div className="admin-banner-list">
                {banners.map(banner => (
                    <div className="banner-card" key={banner.id}>
                        <div className="banner-img">
                            <img src={banner.imageUrl?.startsWith('http') ? banner.imageUrl : `http://${window.location.hostname}:8080${banner.imageUrl}`} alt={banner.title} />
                        </div>
                        <div className="banner-details">
                            <h4>{banner.title}</h4>
                            <p>{banner.subtitle}</p>
                            <span className={`status-badge ${banner.isActive ? 'active' : 'inactive'}`}>
                                {banner.isActive ? '표출중' : '숨김'}
                            </span>
                        </div>
                        <div className="banner-actions">
                            <button className="btn-edit" onClick={() => handleOpenModal(banner)}>수정</button>
                            <button className="btn-delete" onClick={() => banner.id && handleDelete(banner.id)}>삭제</button>
                        </div>
                    </div>
                ))}
            </div>

            {isModalOpen && (
                <div className="admin-modal-overlay">
                    <div className="admin-modal-content modal-wide">
                        <div className="modal-header">
                            <h3>{currentBanner.id ? '배너 수정' : '새 배너 등록'}</h3>
                            <button className="btn-close-modal" onClick={handleCloseModal}>✕</button>
                        </div>

                        <div className="admin-modal-body">
                            {/* Left Side: Form */}
                            <div className="admin-modal-left">
                                <form onSubmit={handleSubmit} className="admin-banner-form">
                                    <div className="form-section">
                                        <h4>기본 정보</h4>
                                        <div className="input-group">
                                            <label>배경 이미지/비디오 파일</label>
                                            <input type="file" ref={fileInputRef} onChange={handleFileChange} accept="image/*,video/*" />
                                            {currentBanner.imageUrl && !selectedFile && (
                                                <small>현재 파일: {currentBanner.imageUrl}</small>
                                            )}
                                        </div>
                                        <div className="input-group">
                                            <label>서브 타이틀</label>
                                            <input type="text" name="subtitle" value={currentBanner.subtitle} onChange={handleChange} required />
                                        </div>
                                        <div className="input-group">
                                            <label>메인 타이틀</label>
                                            <textarea name="title" value={currentBanner.title} onChange={handleChange} required></textarea>
                                        </div>
                                        <div className="input-group">
                                            <label>설명글</label>
                                            <textarea name="description" value={currentBanner.description} onChange={handleChange}></textarea>
                                        </div>
                                    </div>

                                    <div className="form-section">
                                        <h4>스타일 속성 (고급)</h4>
                                        <div className="grid-2-col">
                                            <div className="input-group">
                                                <label>타이틀 폰트 크기</label>
                                                <input type="text" name="titleFontSize" value={currentBanner.titleFontSize} onChange={handleChange} placeholder="ex) 5.5rem or 6vw" />
                                            </div>
                                            <div className="input-group">
                                                <label>텍스트 정렬</label>
                                                <select name="textAlignment" value={currentBanner.textAlignment} onChange={handleChange}>
                                                    <option value="left">Left (좌측)</option>
                                                    <option value="center">Center (중앙)</option>
                                                    <option value="right">Right (우측)</option>
                                                </select>
                                            </div>
                                            <div className="input-group">
                                                <label>타이틀 색상</label>
                                                <input type="color" name="titleColor" value={currentBanner.titleColor} onChange={handleChange} />
                                            </div>
                                            <div className="input-group">
                                                <label>서브타이틀 색상</label>
                                                <input type="color" name="subtitleColor" value={currentBanner.subtitleColor} onChange={handleChange} />
                                            </div>
                                        </div>
                                    </div>

                                    <div className="form-section">
                                        <h4>버튼 설정</h4>
                                        <div style={{ marginBottom: '1rem' }}>
                                            <button type="button" onClick={handleAddButton} className="btn-add-button">+ 버튼 추가</button>
                                        </div>
                                        {currentBanner.buttons.map((btn, index) => (
                                            <div key={index} className="button-editor-row">
                                                <div className="button-editor-header">
                                                    <h5>버튼 {index + 1}</h5>
                                                    <button type="button" onClick={() => handleRemoveButton(index)} className="btn-remove-button">삭제 ✕</button>
                                                </div>
                                                <div className="grid-2-col">
                                                    <div className="input-group">
                                                        <label>텍스트</label>
                                                        <input type="text" value={btn.text} onChange={(e) => handleButtonChange(index, 'text', e.target.value)} />
                                                    </div>
                                                    <div className="input-group">
                                                        <label>링크 (URL)</label>
                                                        <input type="text" value={btn.linkUrl} onChange={(e) => handleButtonChange(index, 'linkUrl', e.target.value)} />
                                                    </div>
                                                    <div className="input-group">
                                                        <label>글자 색상</label>
                                                        <input type="color" value={btn.textColor} onChange={(e) => handleButtonChange(index, 'textColor', e.target.value)} />
                                                    </div>
                                                    <div className="input-group">
                                                        <label>배경 색상</label>
                                                        <input type="color" value={btn.bgColor} onChange={(e) => handleButtonChange(index, 'bgColor', e.target.value)} />
                                                    </div>
                                                </div>
                                            </div>
                                        ))}
                                        {currentBanner.buttons.length === 0 && (
                                            <p style={{ color: '#aaa', fontSize: '0.9rem' }}>현재 추가된 버튼이 없습니다. 배너에 버튼이 없습니다.</p>
                                        )}
                                    </div>

                                    <div className="form-section">
                                        <h4>표출 설정</h4>
                                        <div className="grid-2-col">
                                            <div className="input-group">
                                                <label>전체 링크 (선택사항, 배너 클릭 시 이동)</label>
                                                <input type="text" name="linkUrl" value={currentBanner.linkUrl} onChange={handleChange} placeholder="ex) /shop" />
                                            </div>
                                            <div className="input-group flex-row">
                                                <input type="checkbox" name="isActive" checked={currentBanner.isActive} onChange={handleChange} id="isActiveCheck" />
                                                <label htmlFor="isActiveCheck">활성화 (화면에 표출)</label>
                                            </div>
                                            <div className="input-group">
                                                <label>우선순위 (낮을수록 먼저 노출)</label>
                                                <input type="number" name="sortOrder" value={currentBanner.sortOrder} onChange={handleChange} />
                                            </div>
                                        </div>
                                    </div>

                                    <div className="modal-actions">
                                        <button type="button" className="btn-cancel" onClick={handleCloseModal}>취소</button>
                                        <button type="submit" className="btn-save" disabled={loading}>{loading ? '저장중...' : '저장하기'}</button>
                                    </div>
                                </form>
                            </div>

                            {/* Right Side: Preview */}
                            <div className="admin-modal-right">
                                <div className="preview-section sticky-preview">
                                    <h4>실시간 미리보기</h4>
                                    <div className="preview-container">
                                        <div className="preview-bg">
                                            <img
                                                src={selectedFile ? URL.createObjectURL(selectedFile) : (currentBanner.imageUrl?.startsWith('http') ? currentBanner.imageUrl : `http://${window.location.hostname}:8080${currentBanner.imageUrl || '/placeholder.jpg'}`)}
                                                alt="preview"
                                            />
                                            <div className="preview-overlay"></div>
                                        </div>
                                        <div
                                            className="preview-content"
                                            style={{
                                                display: 'flex',
                                                flexDirection: 'column',
                                                alignItems: currentBanner.textAlignment === 'center' ? 'center' : (currentBanner.textAlignment === 'right' ? 'flex-end' : 'flex-start'),
                                                textAlign: currentBanner.textAlignment as any,
                                            }}
                                        >
                                            <h1 className="preview-title" style={{ fontSize: `calc(${currentBanner.titleFontSize} * 0.4)`, color: currentBanner.titleColor }}>
                                                <span className="preview-subtitle" style={{ color: currentBanner.subtitleColor }}>
                                                    {currentBanner.subtitle || 'Sub Title'}
                                                </span><br />
                                                <div dangerouslySetInnerHTML={{ __html: (currentBanner.title || 'Main Title').replace(/\n/g, '<br />') }} />
                                            </h1>
                                            <div className="preview-desc">
                                                <div dangerouslySetInnerHTML={{ __html: (currentBanner.description || 'Description goes here.').replace(/\n/g, '<br />') }} />
                                            </div>
                                            {currentBanner.buttons.length > 0 && (
                                                <div className="preview-actions" style={{ justifyContent: currentBanner.textAlignment === 'center' ? 'center' : (currentBanner.textAlignment === 'right' ? 'flex-end' : 'flex-start'), display: 'flex', gap: '0.5rem', flexWrap: 'wrap' }}>
                                                    {currentBanner.buttons.map((btn, index) => (
                                                        <div key={index} className="preview-btn" style={{ backgroundColor: btn.bgColor, color: btn.textColor }}>
                                                            {btn.text || 'BUTTON'}
                                                        </div>
                                                    ))}
                                                </div>
                                            )}
                                        </div>
                                    </div>
                                </div>
                            </div>
                        </div>
                    </div>
                </div>
            )}
        </div>
    );
};

export default AdminBanner;
