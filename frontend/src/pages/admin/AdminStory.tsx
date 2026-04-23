import { useState, useEffect, useMemo, useRef } from 'react';
import { Plus, Edit2, Trash2, Save, Eye, Code, X, AlertCircle } from 'lucide-react';
import ReactQuill from 'react-quill-new';
import 'react-quill-new/dist/quill.snow.css';
import Editor from '@monaco-editor/react';
import './AdminStory.css';
import '../admin/AdminSetting.css';
import { StoryListContent } from '../StoryList';

interface Story {
    id: number;
    title: string;
    summary: string;
    thumbnailUrl: string;
    content: string;
    visible: boolean;
    sortOrder: number;
    createdAt: string;
}

interface PageSetting {
    storyPageTitle: string;
    storyPageSubtitle: string;
    storyAdvancedMode: boolean;
    storyCustomHtml: string;
    storyCustomCss: string;
    [key: string]: any;
}

const defaultPageSetting: PageSetting = {
    storyPageTitle: '',
    storyPageSubtitle: '',
    storyAdvancedMode: false,
    storyCustomHtml: '',
    storyCustomCss: ''
};

const AdminStory = () => {
    // Page design settings
    const [pageSetting, setPageSetting] = useState<PageSetting>(defaultPageSetting);
    const [settingLoading, setSettingLoading] = useState(false);
    const [settingSaving, setSettingSaving] = useState(false);
    const [settingMessage, setSettingMessage] = useState<{ type: 'success' | 'error'; text: string } | null>(null);
    const [showPreview, setShowPreview] = useState(false);

    // Stories CRUD
    const [stories, setStories] = useState<Story[]>([]);
    const [isFormOpen, setIsFormOpen] = useState(false);
    const [editingId, setEditingId] = useState<number | null>(null);
    const [thumbnailFile, setThumbnailFile] = useState<File | null>(null);
    const [thumbnailPreview, setThumbnailPreview] = useState<string>('');
    const fileInputRef = useRef<HTMLInputElement>(null);
    const quillRef = useRef<ReactQuill>(null);

    const [formData, setFormData] = useState({
        title: '',
        summary: '',
        thumbnailUrl: '',
        content: '',
        visible: true,
        sortOrder: 0
    });

    // ── Fetch setting ──
    const fetchSetting = async () => {
        try {
            setSettingLoading(true);
            const res = await fetch(`/api/admin/setting`);
            if (res.ok) {
                const data = await res.json();
                if (data) {
                    setPageSetting({
                        ...defaultPageSetting,
                        ...data,
                        storyAdvancedMode: data.storyAdvancedMode || false
                    });
                }
            }
        } catch (error) {
            console.error('Failed to load settings:', error);
        } finally {
            setSettingLoading(false);
        }
    };

    // ── Fetch stories ──
    const fetchStories = async () => {
        try {
            const res = await fetch(`/api/admin/stories`);
            if (res.ok) {
                const data = await res.json();
                setStories(data);
            }
        } catch (err) {
            console.error('Failed to load stories:', err);
        }
    };

    useEffect(() => {
        fetchSetting();
        fetchStories();
    }, []);

    // ── Setting handlers ──
    const handleSettingChange = (e: React.ChangeEvent<HTMLInputElement | HTMLTextAreaElement>) => {
        const { name, value } = e.target;
        setPageSetting(prev => ({ ...prev, [name]: value }));
    };

    const handleToggleAdvanced = () => {
        setPageSetting(prev => ({ ...prev, storyAdvancedMode: !prev.storyAdvancedMode }));
    };

    const handleSaveSetting = async (e: React.FormEvent) => {
        e.preventDefault();
        setSettingMessage(null);
        setSettingSaving(true);

        try {
            const res = await fetch(`/api/admin/setting/save`, {
                method: 'POST',
                headers: { 'Content-Type': 'application/json' },
                body: JSON.stringify(pageSetting)
            });

            if (res.ok) {
                setSettingMessage({ type: 'success', text: '스토리 페이지 설정이 저장되었습니다.' });
                setTimeout(() => setSettingMessage(null), 3000);
            } else {
                setSettingMessage({ type: 'error', text: '설정 저장 중 오류가 발생했습니다.' });
            }
        } catch (error) {
            console.error('Failed to save settings:', error);
            setSettingMessage({ type: 'error', text: '새로고침 후 다시 시도해주세요.' });
        } finally {
            setSettingSaving(false);
        }
    };

    // ── Story CRUD handlers ──
    const handleChange = (e: React.ChangeEvent<HTMLInputElement | HTMLTextAreaElement>) => {
        const { name, value, type } = e.target;
        if (type === 'checkbox') {
            const checked = (e.target as HTMLInputElement).checked;
            setFormData(prev => ({ ...prev, [name]: checked }));
        } else {
            setFormData(prev => ({ ...prev, [name]: type === 'number' ? Number(value) : value }));
        }
    };

    const handleThumbnailChange = (e: React.ChangeEvent<HTMLInputElement>) => {
        const file = e.target.files?.[0];
        if (!file) return;
        setThumbnailFile(file);
        setThumbnailPreview(URL.createObjectURL(file));
    };

    const removeThumbnail = () => {
        setThumbnailFile(null);
        setThumbnailPreview('');
        setFormData(prev => ({ ...prev, thumbnailUrl: '' }));
    };

    const getImageUrl = (url: string) => {
        if (!url) return '';
        return url.startsWith('http') ? url : `${url}`;
    };

    const openFormNew = () => {
        setEditingId(null);
        setFormData({ title: '', summary: '', thumbnailUrl: '', content: '', visible: true, sortOrder: 0 });
        setThumbnailFile(null);
        setThumbnailPreview('');
        setIsFormOpen(true);
    };

    const handleEdit = (story: Story) => {
        setEditingId(story.id);
        setFormData({
            title: story.title,
            summary: story.summary || '',
            thumbnailUrl: story.thumbnailUrl || '',
            content: story.content || '',
            visible: story.visible,
            sortOrder: story.sortOrder || 0
        });
        setThumbnailFile(null);
        setThumbnailPreview(story.thumbnailUrl ? getImageUrl(story.thumbnailUrl) : '');
        setIsFormOpen(true);
    };

    const closeForm = () => {
        setIsFormOpen(false);
        setEditingId(null);
        setFormData({ title: '', summary: '', thumbnailUrl: '', content: '', visible: true, sortOrder: 0 });
        setThumbnailFile(null);
        setThumbnailPreview('');
    };

    const handleSubmit = async (e: React.FormEvent) => {
        e.preventDefault();
        try {
            let finalThumbnailUrl = formData.thumbnailUrl;

            if (thumbnailFile) {
                const fd = new FormData();
                fd.append('file', thumbnailFile);
                const uploadRes = await fetch(`/api/files/upload`, {
                    method: 'POST',
                    body: fd
                });
                const uploadData = await uploadRes.json();
                if (uploadRes.ok) {
                    finalThumbnailUrl = uploadData.url;
                } else {
                    alert(uploadData.message || '썸네일 업로드에 실패했습니다.');
                    return;
                }
            }

            const payload = { ...formData, thumbnailUrl: finalThumbnailUrl };

            if (editingId) {
                const res = await fetch(`/api/admin/stories/${editingId}`, {
                    method: 'PUT',
                    headers: { 'Content-Type': 'application/json' },
                    body: JSON.stringify(payload)
                });
                if (!res.ok) throw new Error('수정 실패');
            } else {
                const res = await fetch(`/api/admin/stories`, {
                    method: 'POST',
                    headers: { 'Content-Type': 'application/json' },
                    body: JSON.stringify(payload)
                });
                if (!res.ok) throw new Error('등록 실패');
            }

            closeForm();
            fetchStories();
        } catch (err) {
            console.error(err);
            alert('저장 중 오류가 발생했습니다.');
        }
    };

    const handleDelete = async (id: number) => {
        if (!confirm('정말 삭제하시겠습니까?')) return;
        try {
            await fetch(`/api/admin/stories/${id}`, { method: 'DELETE' });
            fetchStories();
        } catch (err) {
            console.error(err);
        }
    };

    // ESC key to close modal
    useEffect(() => {
        const handleEsc = (e: KeyboardEvent) => {
            if (e.key === 'Escape' && isFormOpen) closeForm();
        };
        document.addEventListener('keydown', handleEsc);
        return () => document.removeEventListener('keydown', handleEsc);
    }, [isFormOpen]);

    // ── ReactQuill image handler ──
    const imageHandler = () => {
        const input = document.createElement('input');
        input.setAttribute('type', 'file');
        input.setAttribute('accept', 'image/*');
        input.click();

        input.onchange = async () => {
            const file = input.files ? input.files[0] : null;
            if (file) {
                const fd = new FormData();
                fd.append('file', file);

                try {
                    const res = await fetch(`/api/files/upload`, {
                        method: 'POST',
                        body: fd
                    });
                    const data = await res.json();
                    if (res.ok) {
                        const quill = quillRef.current?.getEditor();
                        if (quill) {
                            const range = quill.getSelection(true);
                            if (range) {
                                quill.insertEmbed(range.index, 'image', data.url);
                            } else {
                                const len = quill.getLength();
                                quill.insertEmbed(len, 'image', data.url);
                            }
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
        'list',
        'link', 'image'
    ];

    if (settingLoading) {
        return <div className="admin-story-container"><p>설정 정보를 불러오는 중...</p></div>;
    }

    return (
        <div className="admin-story-container">
            {/* ── Header ── */}
            <div className="admin-story-header">
                <div>
                    <h2>스토리 관리</h2>
                    <p className="header-desc">스토리 페이지 디자인 설정 및 스토리 게시글을 관리합니다.</p>
                </div>
                <div className="header-actions">
                    <button type="button" className="btn-upload" onClick={handleToggleAdvanced}
                        style={{
                            background: pageSetting.storyAdvancedMode ? 'rgba(234, 179, 8, 0.2)' : '',
                            color: pageSetting.storyAdvancedMode ? '#fbbf24' : '',
                            borderColor: pageSetting.storyAdvancedMode ? '#fbbf24' : ''
                        }}>
                        <Code size={18} /> 고급 모드 (직접 디자인)
                    </button>
                    <button type="button" className="btn-preview" onClick={() => setShowPreview(true)}>
                        <Eye size={18} /> 미리보기
                    </button>
                    <button type="submit" form="storySettingsForm" className="btn-save" disabled={settingSaving}>
                        <Save size={18} /> {settingSaving ? '저장 중...' : '설정 저장'}
                    </button>
                </div>
            </div>

            {/* ── Setting message ── */}
            {settingMessage && (
                <div style={{
                    marginBottom: '2rem', padding: '1rem', borderRadius: '8px', display: 'flex', alignItems: 'center', gap: '0.5rem',
                    background: settingMessage.type === 'success' ? 'rgba(16, 185, 129, 0.1)' : 'rgba(244, 63, 94, 0.1)',
                    border: `1px solid ${settingMessage.type === 'success' ? 'rgba(16, 185, 129, 0.3)' : 'rgba(244, 63, 94, 0.3)'}`,
                    color: settingMessage.type === 'success' ? '#10b981' : '#f43f5e'
                }}>
                    <AlertCircle size={18} /> {settingMessage.text}
                </div>
            )}

            {/* ── Page Design Settings ── */}
            <div className="story-settings-section">
                <h3>페이지 디자인 설정</h3>
                <form id="storySettingsForm" onSubmit={handleSaveSetting}>
                    {pageSetting.storyAdvancedMode ? (
                        <div style={{ border: '1px solid rgba(234, 179, 8, 0.3)', borderRadius: '12px', padding: '1.5rem', boxShadow: '0 4px 20px rgba(234, 179, 8, 0.1)' }}>
                            <div style={{ marginBottom: '2rem', padding: '1rem', background: 'rgba(234, 179, 8, 0.1)', borderRadius: '8px', color: '#fbbf24' }}>
                                <strong>고급 모드가 활성화되어 있습니다.</strong><br />
                                아래 커스텀 HTML과 CSS 코드가 스토리 목록 페이지에 바로 렌더링됩니다.
                            </div>

                            <div className="form-group">
                                <label style={{ color: '#fbbf24' }}>Custom HTML</label>
                                <div style={{ border: '1px solid #333', borderRadius: '8px', overflow: 'hidden', marginTop: '0.5rem' }}>
                                    <Editor
                                        height="350px"
                                        defaultLanguage="html"
                                        theme="vs-dark"
                                        value={pageSetting.storyCustomHtml || ''}
                                        onChange={(value) => setPageSetting(prev => ({ ...prev, storyCustomHtml: value || '' }))}
                                        options={{ minimap: { enabled: false }, fontSize: 13, formatOnPaste: true }}
                                    />
                                </div>
                            </div>

                            <div className="form-group">
                                <label style={{ color: '#38bdf8' }}>Custom CSS</label>
                                <div style={{ border: '1px solid #333', borderRadius: '8px', overflow: 'hidden', marginTop: '0.5rem' }}>
                                    <Editor
                                        height="250px"
                                        defaultLanguage="css"
                                        theme="vs-dark"
                                        value={pageSetting.storyCustomCss || ''}
                                        onChange={(value) => setPageSetting(prev => ({ ...prev, storyCustomCss: value || '' }))}
                                        options={{ minimap: { enabled: false }, fontSize: 13, formatOnPaste: true }}
                                    />
                                </div>
                            </div>
                        </div>
                    ) : (
                        <>
                            <div className="form-group">
                                <label>페이지 타이틀</label>
                                <input type="text" name="storyPageTitle" value={pageSetting.storyPageTitle || ''} onChange={handleSettingChange} className="form-control" placeholder="예: Stories" />
                            </div>
                            <div className="form-group">
                                <label>페이지 서브타이틀</label>
                                <input type="text" name="storyPageSubtitle" value={pageSetting.storyPageSubtitle || ''} onChange={handleSettingChange} className="form-control" placeholder="예: 우리의 이야기를 들어보세요" />
                            </div>
                        </>
                    )}
                </form>
            </div>

            {/* ── Stories List ── */}
            <div className="stories-list-section">
                <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: '1.5rem', borderBottom: '1px solid rgba(var(--glass-rgb), 0.1)', paddingBottom: '0.75rem' }}>
                    <h3 style={{ margin: 0, border: 'none', padding: 0 }}>등록된 스토리 목록</h3>
                    <button className="btn-add-product" onClick={openFormNew}>
                        <Plus size={18} /> 새 스토리 등록
                    </button>
                </div>

                <table className="story-table">
                    <thead>
                        <tr>
                            <th>썸네일</th>
                            <th>제목</th>
                            <th>요약</th>
                            <th>공개</th>
                            <th>정렬</th>
                            <th>날짜</th>
                            <th>관리</th>
                        </tr>
                    </thead>
                    <tbody>
                        {stories.map(story => (
                            <tr key={story.id}>
                                <td>
                                    {story.thumbnailUrl ? (
                                        <img src={getImageUrl(story.thumbnailUrl)} alt="" className="story-thumbnail" />
                                    ) : (
                                        <div className="story-thumbnail-placeholder">-</div>
                                    )}
                                </td>
                                <td style={{ fontWeight: 600 }}>{story.title}</td>
                                <td style={{ maxWidth: '200px', overflow: 'hidden', textOverflow: 'ellipsis', whiteSpace: 'nowrap' }}>{story.summary}</td>
                                <td>
                                    {story.visible ? (
                                        <span className="story-status-badge active">공개</span>
                                    ) : (
                                        <span className="story-status-badge inactive">비공개</span>
                                    )}
                                </td>
                                <td>{story.sortOrder}</td>
                                <td style={{ whiteSpace: 'nowrap' }}>{new Date(story.createdAt).toLocaleDateString('ko-KR')}</td>
                                <td>
                                    <div className="action-btns">
                                        <button onClick={() => handleEdit(story)} className="btn-edit-small"><Edit2 size={14} /> 수정</button>
                                        <button onClick={() => handleDelete(story.id)} className="btn-delete-small"><Trash2 size={14} /> 삭제</button>
                                    </div>
                                </td>
                            </tr>
                        ))}
                        {stories.length === 0 && (
                            <tr>
                                <td colSpan={7} className="story-empty-message">
                                    등록된 스토리가 없습니다. 새 스토리를 등록해주세요.
                                </td>
                            </tr>
                        )}
                    </tbody>
                </table>
            </div>

            {/* ── Create/Edit Modal ── */}
            {isFormOpen && (
                <div className="story-modal-overlay" onClick={closeForm}>
                    <div className="story-modal-content" onClick={e => e.stopPropagation()}>
                        <div className="story-modal-header">
                            <h3>{editingId ? '스토리 수정' : '새 스토리 등록'}</h3>
                            <button className="story-modal-close" onClick={closeForm}><X size={20} /></button>
                        </div>
                        <form onSubmit={handleSubmit}>
                            <div className="form-group">
                                <label>제목</label>
                                <input type="text" name="title" value={formData.title} onChange={handleChange} required className="form-control" />
                            </div>

                            <div className="form-group">
                                <label>요약</label>
                                <textarea name="summary" value={formData.summary} onChange={handleChange} className="form-control" rows={3} placeholder="카드에 표시될 요약 내용" />
                            </div>

                            <div className="form-group">
                                <label>썸네일 이미지</label>
                                {thumbnailPreview ? (
                                    <div className="story-thumbnail-preview">
                                        <img src={thumbnailPreview} alt="thumbnail preview" />
                                        <button type="button" className="remove-thumb" onClick={removeThumbnail}><X size={14} /></button>
                                    </div>
                                ) : (
                                    <div className="story-thumbnail-upload" onClick={() => fileInputRef.current?.click()}>
                                        <p style={{ margin: 0, color: 'var(--text-muted)' }}>클릭하여 썸네일 이미지를 선택하세요</p>
                                    </div>
                                )}
                                <input
                                    type="file"
                                    accept="image/*"
                                    onChange={handleThumbnailChange}
                                    ref={fileInputRef}
                                    style={{ display: 'none' }}
                                />
                            </div>

                            <div className="form-group" style={{ marginBottom: '50px' }}>
                                <label>본문</label>
                                <ReactQuill
                                    ref={quillRef}
                                    theme="snow"
                                    value={formData.content}
                                    onChange={(value) => setFormData(prev => ({ ...prev, content: value }))}
                                    modules={modules}
                                    formats={formats}
                                    style={{ height: '300px' }}
                                />
                            </div>

                            <div className="form-group">
                                <label>정렬 순서</label>
                                <input type="number" name="sortOrder" value={formData.sortOrder} onChange={handleChange} className="form-control" />
                            </div>

                            <div className="form-group checkbox">
                                <label>
                                    <input type="checkbox" name="visible" checked={formData.visible} onChange={handleChange} />
                                    공개
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

            {/* ── Preview Modal ── */}
            {showPreview && (
                <div className="preview-modal-overlay">
                    <div className="preview-modal-content">
                        <div className="preview-modal-header">
                            <h3>스토리 페이지 미리보기 {pageSetting.storyAdvancedMode && <span style={{ fontSize: '0.8rem', marginLeft: '0.5rem', background: '#fbbf24', color: '#000', padding: '2px 8px', borderRadius: '12px' }}>고급 모드</span>}</h3>
                            <button className="btn-close-modal" onClick={() => setShowPreview(false)}>
                                <X size={18} /> 닫기
                            </button>
                        </div>
                        <div className="preview-modal-body">
                            <StoryListContent setting={pageSetting} stories={stories} />
                        </div>
                    </div>
                </div>
            )}
        </div>
    );
};

export default AdminStory;
