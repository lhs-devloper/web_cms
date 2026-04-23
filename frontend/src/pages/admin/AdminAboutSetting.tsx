import React, { useState, useEffect } from 'react';
import { Save, AlertCircle, ChevronUp, ChevronDown, MoveVertical, Eye, X, Code } from 'lucide-react';
import Editor from '@monaco-editor/react';
import './AdminSetting.css';
import { AboutContent } from '../About';

interface SiteSetting {
    id?: string;
    aboutHeroTitle: string;
    aboutHeroSubtitle: string;
    aboutVisionTitle: string;
    aboutVisionDesc1: string;
    aboutVisionDesc2: string;
    aboutExperienceYears: string;
    aboutGlobalPartners: string;
    aboutSectionOrder: string;
    aboutAdvancedMode: boolean;
    aboutCustomHtml: string;
    aboutCustomCss: string;
    [key: string]: any;
}

const defaultSetting: SiteSetting = {
    aboutHeroTitle: '',
    aboutHeroSubtitle: '',
    aboutVisionTitle: '',
    aboutVisionDesc1: '',
    aboutVisionDesc2: '',
    aboutExperienceYears: '',
    aboutGlobalPartners: '',
    aboutSectionOrder: 'hero,vision,values',
    aboutAdvancedMode: false,
    aboutCustomHtml: '',
    aboutCustomCss: ''
};

const AdminAboutSetting = () => {
    const [setting, setSetting] = useState<SiteSetting>(defaultSetting);
    const [loading, setLoading] = useState(false);
    const [saving, setSaving] = useState(false);
    const [message, setMessage] = useState<{ type: 'success' | 'error', text: string } | null>(null);
    const [showPreview, setShowPreview] = useState(false);

    const fetchSetting = async () => {
        try {
            setLoading(true);
            const res = await fetch(`/api/admin/setting`);
            if (res.ok) {
                const data = await res.json();
                if (data) {
                    setSetting({
                        ...defaultSetting,
                        ...data,
                        aboutSectionOrder: data.aboutSectionOrder || 'hero,vision,values',
                        aboutAdvancedMode: data.aboutAdvancedMode || false
                    });
                }
            }
        } catch (error) {
            console.error('Failed to load settings:', error);
        } finally {
            setLoading(false);
        }
    };

    useEffect(() => {
        fetchSetting();
    }, []);

    const handleChange = (e: React.ChangeEvent<HTMLInputElement | HTMLTextAreaElement>) => {
        const { name, value } = e.target;
        setSetting(prev => ({ ...prev, [name]: value }));
    };

    const handleToggleAdvanced = () => {
        setSetting(prev => ({ ...prev, aboutAdvancedMode: !prev.aboutAdvancedMode }));
    }

    const handleSave = async (e: React.FormEvent) => {
        e.preventDefault();
        setMessage(null);
        setSaving(true);

        try {
            const res = await fetch(`/api/admin/setting/save`, {
                method: 'POST',
                headers: { 'Content-Type': 'application/json' },
                body: JSON.stringify(setting)
            });

            if (res.ok) {
                setMessage({ type: 'success', text: '회사소개 설정이 성공적으로 저장되었습니다.' });
                setTimeout(() => setMessage(null), 3000);
            } else {
                setMessage({ type: 'error', text: '설정 저장 중 오류가 발생했습니다.' });
            }
        } catch (error) {
            console.error('Failed to save settings:', error);
            setMessage({ type: 'error', text: '새로고침 후 다시 시도해주세요.' });
        } finally {
            setSaving(false);
        }
    };

    const sectionOrder = (setting.aboutSectionOrder || 'hero,vision,values').split(',');

    const moveSection = (index: number, direction: 'up' | 'down') => {
        const newOrder = [...sectionOrder];
        if (direction === 'up' && index > 0) {
            [newOrder[index - 1], newOrder[index]] = [newOrder[index], newOrder[index - 1]];
        } else if (direction === 'down' && index < newOrder.length - 1) {
            [newOrder[index + 1], newOrder[index]] = [newOrder[index], newOrder[index + 1]];
        }
        setSetting(prev => ({ ...prev, aboutSectionOrder: newOrder.join(',') }));
    };

    const renderFormSection = (section: string, index: number) => {
        if (section === 'hero') {
            return (
                <section key={section} className="settings-section">
                    <div className="section-reorder-header">
                        <h4 style={{ display: 'flex', alignItems: 'center', gap: '0.5rem' }}>
                            <MoveVertical size={16} color="#94a3b8" /> 1. 메인 타이틀 영역 (Hero Section)
                        </h4>
                        <div className="reorder-controls">
                            <button type="button" className="reorder-btn" onClick={() => moveSection(index, 'up')} disabled={index === 0}><ChevronUp size={16} /></button>
                            <button type="button" className="reorder-btn" onClick={() => moveSection(index, 'down')} disabled={index === sectionOrder.length - 1}><ChevronDown size={16} /></button>
                        </div>
                    </div>
                    <p className="settings-section-desc">회사 소개 최상단에 보이는 메인 카피</p>
                    <div className="form-group">
                        <label>메인 타이틀</label>
                        <input type="text" name="aboutHeroTitle" value={setting.aboutHeroTitle || ''} onChange={handleChange} className="form-control" />
                    </div>
                    <div className="form-group">
                        <label>메인 서브타이틀</label>
                        <textarea name="aboutHeroSubtitle" value={setting.aboutHeroSubtitle || ''} onChange={handleChange} className="form-control" rows={3}></textarea>
                    </div>
                </section>
            );
        }

        if (section === 'vision') {
            return (
                <section key={section} className="settings-section">
                    <div className="section-reorder-header">
                        <h4 style={{ display: 'flex', alignItems: 'center', gap: '0.5rem' }}>
                            <MoveVertical size={16} color="#94a3b8" /> 2. 비전 및 핵심 성과 (Vision & Metrics)
                        </h4>
                        <div className="reorder-controls">
                            <button type="button" className="reorder-btn" onClick={() => moveSection(index, 'up')} disabled={index === 0}><ChevronUp size={16} /></button>
                            <button type="button" className="reorder-btn" onClick={() => moveSection(index, 'down')} disabled={index === sectionOrder.length - 1}><ChevronDown size={16} /></button>
                        </div>
                    </div>
                    <p className="settings-section-desc">회사의 목표, 소개 본문 및 주요 성과 하이라이트</p>
                    <div className="form-group">
                        <label>비전 타이틀</label>
                        <input type="text" name="aboutVisionTitle" value={setting.aboutVisionTitle || ''} onChange={handleChange} className="form-control" />
                    </div>
                    <div className="form-group">
                        <label>소개 본문 1</label>
                        <textarea name="aboutVisionDesc1" value={setting.aboutVisionDesc1 || ''} onChange={handleChange} className="form-control" rows={4}></textarea>
                    </div>
                    <div className="form-group">
                        <label>소개 본문 2</label>
                        <textarea name="aboutVisionDesc2" value={setting.aboutVisionDesc2 || ''} onChange={handleChange} className="form-control" rows={4}></textarea>
                    </div>
                    <div style={{ marginTop: '1.5rem', marginBottom: '1rem', borderTop: '1px dashed rgba(255,255,255,0.1)', paddingTop: '1.5rem' }}>
                        <h5 style={{ color: '#e2e8f0', marginBottom: '1rem' }}>핵심 성과 지표</h5>
                        <div className="form-group">
                            <label>경력 내용 (예: 10+ Years)</label>
                            <input type="text" name="aboutExperienceYears" value={setting.aboutExperienceYears || ''} onChange={handleChange} className="form-control" />
                        </div>
                        <div className="form-group">
                            <label>글로벌 파트너스 수 (예: 200+)</label>
                            <input type="text" name="aboutGlobalPartners" value={setting.aboutGlobalPartners || ''} onChange={handleChange} className="form-control" />
                        </div>
                    </div>
                </section>
            );
        }

        if (section === 'values') {
            return (
                <section key={section} className="settings-section">
                    <div className="section-reorder-header">
                        <h4 style={{ display: 'flex', alignItems: 'center', gap: '0.5rem' }}>
                            <MoveVertical size={16} color="#94a3b8" /> 3. 핵심 가치 (Core Values)
                        </h4>
                        <div className="reorder-controls">
                            <button type="button" className="reorder-btn" onClick={() => moveSection(index, 'up')} disabled={index === 0}><ChevronUp size={16} /></button>
                            <button type="button" className="reorder-btn" onClick={() => moveSection(index, 'down')} disabled={index === sectionOrder.length - 1}><ChevronDown size={16} /></button>
                        </div>
                    </div>
                    <p className="settings-section-desc">회사 핵심 가치 (Customer First, Innovation 등) 영역입니다. 내용은 고정되어 있으며 배치 순서만 변경할 수 있습니다.</p>
                </section>
            );
        }

        return null;
    };

    if (loading) {
        return <div className="admin-setting-container"><div className="loading-indicator">설정 정보를 불러오는 중...</div></div>;
    }

    return (
        <div className="admin-setting-container">
            <div className="admin-setting-header">
                <div>
                    <h2>회사소개 관련 설정</h2>
                    <p>사용자단 '/about' 페이지에 노출될 구성요소를 편집하고, 미리보기를 통해 배치를 확인하세요.</p>
                </div>
                <div className="header-actions">
                    <button type="button" className="btn-upload" onClick={handleToggleAdvanced} style={{ background: setting.aboutAdvancedMode ? 'rgba(234, 179, 8, 0.2)' : '', color: setting.aboutAdvancedMode ? '#fbbf24' : '', borderColor: setting.aboutAdvancedMode ? '#fbbf24' : '' }}>
                        <Code size={18} /> 고급 모드 (직접 디자인)
                    </button>
                    <button type="button" className="btn-preview" onClick={() => setShowPreview(true)}>
                        <Eye size={18} /> 미리보기
                    </button>
                    <button type="submit" form="aboutSettingsForm" className="btn-save" disabled={saving}>
                        <Save size={18} /> {saving ? '저장 중...' : '변경사항 저장'}
                    </button>
                </div>
            </div>

            {message && (
                <div style={{
                    marginBottom: '2rem', padding: '1rem', borderRadius: '8px', display: 'flex', alignItems: 'center', gap: '0.5rem',
                    background: message.type === 'success' ? 'rgba(16, 185, 129, 0.1)' : 'rgba(244, 63, 94, 0.1)',
                    border: `1px solid ${message.type === 'success' ? 'rgba(16, 185, 129, 0.3)' : 'rgba(244, 63, 94, 0.3)'}`,
                    color: message.type === 'success' ? '#10b981' : '#f43f5e'
                }}>
                    <AlertCircle size={18} /> {message.text}
                </div>
            )}

            <div className="admin-form-pane">
                <form id="aboutSettingsForm" onSubmit={handleSave} className="settings-grid" style={{ gridTemplateColumns: '1fr' }}>

                    {setting.aboutAdvancedMode ? (
                        <div className="settings-section" style={{ border: '1px solid rgba(234, 179, 8, 0.3)', boxShadow: '0 4px 20px rgba(234, 179, 8, 0.1)' }}>
                            <div style={{ marginBottom: '2rem', padding: '1rem', background: 'rgba(234, 179, 8, 0.1)', borderRadius: '8px', color: '#fbbf24' }}>
                                <strong>⚠️ 디자인 직접 제어 (고급 모드) 가 활성화되어 있습니다.</strong><br />
                                기존 레이아웃 및 입력 폼 대신, 아래 입력하신 커스텀 HTML과 CSS 코드가 회사소개 페이지에 바로 렌더링됩니다.
                            </div>

                            <div className="form-group">
                                <label style={{ color: '#fbbf24' }}>Custom HTML</label>
                                <div style={{ border: '1px solid #333', borderRadius: '8px', overflow: 'hidden', marginTop: '0.5rem' }}>
                                    <Editor
                                        height="350px"
                                        defaultLanguage="html"
                                        theme="vs-dark"
                                        value={setting.aboutCustomHtml || ''}
                                        onChange={(value) => setSetting(prev => ({ ...prev, aboutCustomHtml: value || '' }))}
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
                                        value={setting.aboutCustomCss || ''}
                                        onChange={(value) => setSetting(prev => ({ ...prev, aboutCustomCss: value || '' }))}
                                        options={{ minimap: { enabled: false }, fontSize: 13, formatOnPaste: true }}
                                    />
                                </div>
                            </div>
                        </div>
                    ) : (
                        sectionOrder.map((section, idx) => renderFormSection(section, idx))
                    )}

                </form>
            </div>

            {showPreview && (
                <div className="preview-modal-overlay">
                    <div className="preview-modal-content">
                        <div className="preview-modal-header">
                            <h3>회사소개 미리보기 {setting.aboutAdvancedMode && <span style={{ fontSize: '0.8rem', marginLeft: '0.5rem', background: '#fbbf24', color: '#000', padding: '2px 8px', borderRadius: '12px' }}>고급 모드</span>}</h3>
                            <button className="btn-close-modal" onClick={() => setShowPreview(false)}>
                                <X size={18} /> 닫기
                            </button>
                        </div>
                        <div className="preview-modal-body">
                            <AboutContent setting={setting} />
                        </div>
                    </div>
                </div>
            )}
        </div>
    );
};

export default AdminAboutSetting;
