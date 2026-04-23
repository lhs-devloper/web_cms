import React, { useState, useEffect } from 'react';
import { Save, AlertCircle, ChevronUp, ChevronDown, MoveVertical, Eye, X, Code } from 'lucide-react';
import Editor from '@monaco-editor/react';
import './AdminSetting.css';
import { LocationContent } from '../Location';

interface SiteSetting {
    id?: string;
    locationMapProvider: string;
    locationKakaoMapIframe: string;
    locationAddress: string;
    locationPhone: string;
    locationEmail: string;
    locationHours: string;
    locationMapIframe: string;
    locationSectionOrder: string;
    locationAdvancedMode: boolean;
    locationCustomHtml: string;
    locationCustomCss: string;
    [key: string]: any;
}

const defaultSetting: SiteSetting = {
    locationMapProvider: 'google',
    locationKakaoMapIframe: '',
    locationAddress: '',
    locationPhone: '',
    locationEmail: '',
    locationHours: '',
    locationMapIframe: '',
    locationSectionOrder: 'hero,map,info,transport',
    locationAdvancedMode: false,
    locationCustomHtml: '',
    locationCustomCss: ''
};

const AdminLocationSetting = () => {
    const [setting, setSetting] = useState<SiteSetting>(defaultSetting);
    const [loading, setLoading] = useState(false);
    const [saving, setSaving] = useState(false);
    const [message, setMessage] = useState<{ type: 'success' | 'error', text: string } | null>(null);
    const [showPreview, setShowPreview] = useState(false);

    const fetchSetting = async () => {
        try {
            setLoading(true);
            const res = await fetch(`http://${window.location.hostname}:8080/api/admin/setting`);
            if (res.ok) {
                const data = await res.json();
                if (data) {
                    setSetting({
                        ...defaultSetting,
                        ...data,
                        locationSectionOrder: data.locationSectionOrder || 'hero,map,info,transport',
                        locationAdvancedMode: data.locationAdvancedMode || false
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
        setSetting(prev => ({ ...prev, locationAdvancedMode: !prev.locationAdvancedMode }));
    }

    const handleSave = async (e: React.FormEvent) => {
        e.preventDefault();
        setMessage(null);
        setSaving(true);

        try {
            const res = await fetch(`http://${window.location.hostname}:8080/api/admin/setting/save`, {
                method: 'POST',
                headers: { 'Content-Type': 'application/json' },
                body: JSON.stringify(setting)
            });

            if (res.ok) {
                setMessage({ type: 'success', text: '오시는길 설정이 성공적으로 저장되었습니다.' });
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

    const sectionOrder = (setting.locationSectionOrder || 'hero,map,info,transport').split(',');

    const moveSection = (index: number, direction: 'up' | 'down') => {
        const newOrder = [...sectionOrder];
        if (direction === 'up' && index > 0) {
            [newOrder[index - 1], newOrder[index]] = [newOrder[index], newOrder[index - 1]];
        } else if (direction === 'down' && index < newOrder.length - 1) {
            [newOrder[index + 1], newOrder[index]] = [newOrder[index], newOrder[index + 1]];
        }
        setSetting(prev => ({ ...prev, locationSectionOrder: newOrder.join(',') }));
    };

    const renderFormSection = (section: string, index: number) => {
        if (section === 'hero') {
            return (
                <section key={section} className="settings-section">
                    <div className="section-reorder-header">
                        <h4 style={{ display: 'flex', alignItems: 'center', gap: '0.5rem' }}>
                            <MoveVertical size={16} color="#94a3b8" /> 메인 타이틀 (Hero Section)
                        </h4>
                        <div className="reorder-controls">
                            <button type="button" className="reorder-btn" onClick={() => moveSection(index, 'up')} disabled={index === 0}><ChevronUp size={16} /></button>
                            <button type="button" className="reorder-btn" onClick={() => moveSection(index, 'down')} disabled={index === sectionOrder.length - 1}><ChevronDown size={16} /></button>
                        </div>
                    </div>
                </section>
            );
        }

        if (section === 'map') {
            return (
                <section key={section} className="settings-section">
                    <div className="section-reorder-header">
                        <h4 style={{ display: 'flex', alignItems: 'center', gap: '0.5rem' }}>
                            <MoveVertical size={16} color="#94a3b8" /> 지도 설정
                        </h4>
                        <div className="reorder-controls">
                            <button type="button" className="reorder-btn" onClick={() => moveSection(index, 'up')} disabled={index === 0}><ChevronUp size={16} /></button>
                            <button type="button" className="reorder-btn" onClick={() => moveSection(index, 'down')} disabled={index === sectionOrder.length - 1}><ChevronDown size={16} /></button>
                        </div>
                    </div>

                    <div className="form-group">
                        <label>지도 제공자</label>
                        <div style={{ display: 'flex', gap: '0.8rem', marginTop: '0.5rem' }}>
                            {[{ id: 'google', name: 'Google Maps' }, { id: 'kakao', name: 'Kakao Map' }].map(p => (
                                <label key={p.id} style={{ display: 'flex', alignItems: 'center', gap: '0.4rem', cursor: 'pointer', fontSize: '0.95rem' }}>
                                    <input
                                        type="radio"
                                        name="locationMapProvider"
                                        value={p.id}
                                        checked={(setting.locationMapProvider || 'google') === p.id}
                                        onChange={handleChange}
                                    />
                                    {p.name}
                                </label>
                            ))}
                        </div>
                    </div>

                    {(setting.locationMapProvider || 'google') === 'google' ? (
                        <>
                            <p className="settings-section-desc">Google Maps에서 발급받은 iframe 소스 코드를 입력합니다.</p>
                            <div className="form-group">
                                <label>Iframe URL (SRC 속성 값 전체를 넣으셔도 됩니다)</label>
                                <textarea name="locationMapIframe" value={setting.locationMapIframe || ''} onChange={handleChange} className="form-control" rows={6} style={{ fontFamily: 'monospace' }}></textarea>
                            </div>
                        </>
                    ) : (
                        <>
                            <p className="settings-section-desc">카카오맵에서 "HTML 태그 복사"로 가져온 iframe 코드를 입력합니다.</p>
                            <div className="form-group">
                                <label>Kakao Map Iframe (HTML 태그 또는 src URL)</label>
                                <textarea name="locationKakaoMapIframe" value={setting.locationKakaoMapIframe || ''} onChange={handleChange} className="form-control" rows={6} style={{ fontFamily: 'monospace' }}></textarea>
                            </div>
                        </>
                    )}
                </section>
            );
        }

        if (section === 'info') {
            return (
                <section key={section} className="settings-section">
                    <div className="section-reorder-header">
                        <h4 style={{ display: 'flex', alignItems: 'center', gap: '0.5rem' }}>
                            <MoveVertical size={16} color="#94a3b8" /> 기본 연락처 및 정보
                        </h4>
                        <div className="reorder-controls">
                            <button type="button" className="reorder-btn" onClick={() => moveSection(index, 'up')} disabled={index === 0}><ChevronUp size={16} /></button>
                            <button type="button" className="reorder-btn" onClick={() => moveSection(index, 'down')} disabled={index === sectionOrder.length - 1}><ChevronDown size={16} /></button>
                        </div>
                    </div>
                    <p className="settings-section-desc">본사 주소, 전화번호, 이메일, 운영시간을 입력합니다.</p>

                    <div className="form-group">
                        <label>본사 주소</label>
                        <input type="text" name="locationAddress" value={setting.locationAddress || ''} onChange={handleChange} className="form-control" />
                    </div>
                    <div className="form-group">
                        <label>전화번호 (복수 입력 시 줄바꿈 권장)</label>
                        <textarea name="locationPhone" value={setting.locationPhone || ''} onChange={handleChange} className="form-control" rows={3}></textarea>
                    </div>
                    <div className="form-group">
                        <label>대표 이메일</label>
                        <textarea name="locationEmail" value={setting.locationEmail || ''} onChange={handleChange} className="form-control" rows={3}></textarea>
                    </div>
                    <div className="form-group">
                        <label>운영 시간안내</label>
                        <textarea name="locationHours" value={setting.locationHours || ''} onChange={handleChange} className="form-control" rows={3}></textarea>
                    </div>
                </section>
            );
        }

        if (section === 'transport') {
            return (
                <section key={section} className="settings-section">
                    <div className="section-reorder-header">
                        <h4 style={{ display: 'flex', alignItems: 'center', gap: '0.5rem' }}>
                            <MoveVertical size={16} color="#94a3b8" /> 대중교통 안내
                        </h4>
                        <div className="reorder-controls">
                            <button type="button" className="reorder-btn" onClick={() => moveSection(index, 'up')} disabled={index === 0}><ChevronUp size={16} /></button>
                            <button type="button" className="reorder-btn" onClick={() => moveSection(index, 'down')} disabled={index === sectionOrder.length - 1}><ChevronDown size={16} /></button>
                        </div>
                    </div>
                    <p className="settings-section-desc">지하철/버스 등 대중교통 안내는 기본 레이아웃으로 제공됩니다.</p>
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
                    <h2>오시는길 관리</h2>
                    <p>사용자단 '/location' 페이지에 노출될 정보를 변경하고 미리보기로 확인하세요.</p>
                </div>
                <div className="header-actions">
                    <button type="button" className="btn-upload" onClick={handleToggleAdvanced} style={{ background: setting.locationAdvancedMode ? 'rgba(234, 179, 8, 0.2)' : '', color: setting.locationAdvancedMode ? '#fbbf24' : '', borderColor: setting.locationAdvancedMode ? '#fbbf24' : '' }}>
                        <Code size={18} /> 고급 모드 (직접 디자인)
                    </button>
                    <button type="button" className="btn-preview" onClick={() => setShowPreview(true)}>
                        <Eye size={18} /> 미리보기
                    </button>
                    <button type="submit" form="locationSettingsForm" className="btn-save" disabled={saving}>
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
                <form id="locationSettingsForm" onSubmit={handleSave} className="settings-grid" style={{ gridTemplateColumns: '1fr' }}>

                    {setting.locationAdvancedMode ? (
                        <div className="settings-section" style={{ border: '1px solid rgba(234, 179, 8, 0.3)', boxShadow: '0 4px 20px rgba(234, 179, 8, 0.1)' }}>
                            <div style={{ marginBottom: '2rem', padding: '1rem', background: 'rgba(234, 179, 8, 0.1)', borderRadius: '8px', color: '#fbbf24' }}>
                                <strong>⚠️ 디자인 직접 제어 (고급 모드) 가 활성화되어 있습니다.</strong><br />
                                기존 레이아웃 및 입력 폼 대신, 아래 입력하신 커스텀 HTML과 CSS 코드가 오시는길 페이지에 바로 렌더링됩니다.
                            </div>

                            <div className="form-group">
                                <label style={{ color: '#fbbf24' }}>Custom HTML</label>
                                <div style={{ border: '1px solid #333', borderRadius: '8px', overflow: 'hidden', marginTop: '0.5rem' }}>
                                    <Editor
                                        height="350px"
                                        defaultLanguage="html"
                                        theme="vs-dark"
                                        value={setting.locationCustomHtml || ''}
                                        onChange={(value) => setSetting(prev => ({ ...prev, locationCustomHtml: value || '' }))}
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
                                        value={setting.locationCustomCss || ''}
                                        onChange={(value) => setSetting(prev => ({ ...prev, locationCustomCss: value || '' }))}
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
                            <h3>오시는길 미리보기 {setting.locationAdvancedMode && <span style={{ fontSize: '0.8rem', marginLeft: '0.5rem', background: '#fbbf24', color: '#000', padding: '2px 8px', borderRadius: '12px' }}>고급 모드</span>}</h3>
                            <button className="btn-close-modal" onClick={() => setShowPreview(false)}>
                                <X size={18} /> 닫기
                            </button>
                        </div>
                        <div className="preview-modal-body">
                            <LocationContent setting={setting} />
                        </div>
                    </div>
                </div>
            )}
        </div>
    );
};

export default AdminLocationSetting;
