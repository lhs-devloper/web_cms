import React, { useState, useEffect, useRef } from 'react';
import { Save, Upload, Info, AlertCircle } from 'lucide-react';
import './AdminSetting.css';

interface SiteSetting {
    id?: string;
    siteName: string;
    metaTitle: string;
    metaDescription: string;
    metaKeywords: string;
    ogTitle: string;
    ogDescription: string;
    ogImage: string;
    logoUrl: string;
    logoAltText: string;
    footerCopyright: string;
    canonicalUrl: string;
    googleAnalyticsId: string;
    robotsTxt: string;
}

const defaultSetting: SiteSetting = {
    siteName: '',
    metaTitle: '',
    metaDescription: '',
    metaKeywords: '',
    ogTitle: '',
    ogDescription: '',
    ogImage: '',
    logoUrl: '',
    logoAltText: '',
    footerCopyright: '',
    canonicalUrl: '',
    googleAnalyticsId: '',
    robotsTxt: ''
};

const AdminSetting = () => {
    const [setting, setSetting] = useState<SiteSetting>(defaultSetting);
    const [loading, setLoading] = useState(false);
    const [saving, setSaving] = useState(false);
    const [message, setMessage] = useState<{ type: 'success' | 'error', text: string } | null>(null);

    const fileInputRef = useRef<HTMLInputElement>(null);
    const [activeUploadField, setActiveUploadField] = useState<'ogImage' | 'logoUrl' | null>(null);

    const fetchSetting = async () => {
        try {
            setLoading(true);
            const res = await fetch(`http://${window.location.hostname}:8080/api/admin/setting`);
            if (res.ok) {
                const data = await res.json();
                if (data) {
                    setSetting(data);
                }
            }
        } catch (error) {
            console.error('Failed to load site settings:', error);
        } finally {
            setLoading(false);
        }
    };

    useEffect(() => {
        fetchSetting();
        // eslint-disable-next-line react-hooks/exhaustive-deps
    }, []);

    const handleChange = (e: React.ChangeEvent<HTMLInputElement | HTMLTextAreaElement>) => {
        const { name, value } = e.target;
        setSetting(prev => ({ ...prev, [name]: value }));
    };

    const handleSave = async (e: React.FormEvent) => {
        e.preventDefault();
        setMessage(null);
        setSaving(true);

        try {
            const formData = new FormData();
            Object.entries(setting).forEach(([key, value]) => {
                if (value !== undefined && value !== null) {
                    formData.append(key, value.toString());
                }
            });

            // Make sure the API exists to save (e.g. POST /api/admin/setting/save)
            const res = await fetch(`http://${window.location.hostname}:8080/api/admin/setting/save`, {
                method: 'POST',
                // Spring Boot form data or JSON depending on backend implementation.
                // If it expects form-data:
                // body: formData 
                // BUT it might need JSON instead. Usually @RequestBody needs JSON. Let's use JSON for API
                headers: { 'Content-Type': 'application/json' },
                body: JSON.stringify(setting)
            });

            if (res.ok) {
                setMessage({ type: 'success', text: '설정이 성공적으로 저장되었습니다.' });
                // auto-hide message
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

    const triggerUpload = (field: 'ogImage' | 'logoUrl') => {
        setActiveUploadField(field);
        if (fileInputRef.current) {
            fileInputRef.current.click();
        }
    };

    const handleFileUpload = async (e: React.ChangeEvent<HTMLInputElement>) => {
        const file = e.target.files?.[0];
        if (!file || !activeUploadField) return;

        try {
            const formData = new FormData();
            formData.append('file', file);

            const res = await fetch(`http://${window.location.hostname}:8080/api/files/upload`, {
                method: 'POST',
                body: formData
            });

            if (res.ok) {
                const data = await res.json();
                if (data.url) {
                    setSetting(prev => ({ ...prev, [activeUploadField]: data.url }));
                } else {
                    alert('Upload failed: ' + (data.message || 'Unknown error'));
                }
            } else {
                alert('파일 업로드 에러가 발생했습니다.');
            }
        } catch (error) {
            console.error('Upload Error:', error);
            alert('파일 업로드 중 문제가 발생했습니다.');
        } finally {
            if (fileInputRef.current) {
                fileInputRef.current.value = '';
            }
            setActiveUploadField(null);
        }
    };

    const getImageUrl = (url: string) => {
        if (!url) return '';
        return url.startsWith('http') ? url : `http://${window.location.hostname}:8080${url}`;
    };

    if (loading) {
        return <div className="admin-setting-container"><div className="loading-indicator">설정 정보를 불러오는 중...</div></div>;
    }

    return (
        <div className="admin-setting-container">
            <div className="admin-setting-header">
                <div>
                    <h2>환경 설정 (SEO & 사이트 정보)</h2>
                    <p>검색 엔진 결과와 소셜 미디어 공유 시 나타날 사이트 기본 정보를 구성합니다.</p>
                </div>
                <button type="submit" form="settingsForm" className="btn-save" disabled={saving}>
                    <Save size={18} /> {saving ? '저장 중...' : '변경사항 저장'}
                </button>
            </div>

            {message && (
                <div style={{
                    marginBottom: '2rem', padding: '1rem', borderRadius: '8px', display: 'flex', alignItems: 'center', gap: '0.5rem',
                    background: message.type === 'success' ? 'rgba(0, 210, 255, 0.1)' : 'rgba(255, 82, 82, 0.1)',
                    border: `1px solid ${message.type === 'success' ? 'rgba(0, 210, 255, 0.3)' : 'rgba(255, 82, 82, 0.3)'}`,
                    color: message.type === 'success' ? '#00d2ff' : '#ff5252'
                }}>
                    <AlertCircle size={18} /> {message.text}
                </div>
            )}

            <form id="settingsForm" onSubmit={handleSave} className="settings-grid">

                {/* Section 1: Meta Tags */}
                <section className="settings-section">
                    <h4>1. 기본 설정 (SEO)</h4>
                    <p className="settings-section-desc">브라우저 탭 이름과 검색 엔진 노출 텍스트 설정</p>

                    <div className="form-group">
                        <label>사이트 이름 (내부 관리용)</label>
                        <input type="text" name="siteName" value={setting.siteName} onChange={handleChange} className="form-control" placeholder="예: 무신사 스토어" />
                    </div>
                    <div className="form-group">
                        <label>메타 타이틀 (Meta Title)</label>
                        <input type="text" name="metaTitle" value={setting.metaTitle} onChange={handleChange} className="form-control" placeholder="탭에 표시될 공식 타이틀" />
                    </div>
                    <div className="form-group">
                        <label>메타 설명 (Meta Description)</label>
                        <textarea name="metaDescription" value={setting.metaDescription} onChange={handleChange} className="form-control" rows={3}></textarea>
                    </div>
                    <div className="form-group">
                        <label>메타 키워드 (Meta Keywords)</label>
                        <input type="text" name="metaKeywords" value={setting.metaKeywords} onChange={handleChange} className="form-control" placeholder="ex: 쇼핑, 의류, 남성복 (쉼표로 구분)" />
                    </div>
                </section>

                {/* Section 2: Open Graph */}
                <section className="settings-section">
                    <h4>2. 소셜 공유 정보 (Open Graph)</h4>
                    <p className="settings-section-desc">카카오톡, 페이스북 등에 링크 공유 시 나타나는 카드 뷰 형식 지정</p>

                    <div className="form-group">
                        <label>공유용 제목 (OG Title)</label>
                        <input type="text" name="ogTitle" value={setting.ogTitle} onChange={handleChange} className="form-control" />
                    </div>
                    <div className="form-group">
                        <label>공유용 설명 (OG Description)</label>
                        <textarea name="ogDescription" value={setting.ogDescription} onChange={handleChange} className="form-control" rows={3}></textarea>
                    </div>
                    <div className="form-group">
                        <label>공유용 이미지 (OG Image)</label>
                        <div className="img-upload-group">
                            <input type="text" name="ogImage" value={setting.ogImage} onChange={handleChange} className="form-control" placeholder="https://..." />
                            <button type="button" className="btn-upload" onClick={() => triggerUpload('ogImage')}>
                                <Upload size={16} /> 업로드
                            </button>
                        </div>
                        <p className="form-hint"><Info size={14} /> 권장 해상도: 1200 x 630 픽셀</p>
                        {setting.ogImage && (
                            <div className="image-preview-container">
                                <img src={getImageUrl(setting.ogImage)} alt="OG Preview" />
                            </div>
                        )}
                    </div>
                </section>

                {/* Section 3: Branding */}
                <section className="settings-section">
                    <h4>3. 로고 & 하단 정보 (Branding)</h4>
                    <p className="settings-section-desc">쇼핑몰 상단 로고 이미지와 최하단 카피라이트 설정</p>

                    <div className="form-group">
                        <label>사이트 로고 (Logo URL)</label>
                        <div className="img-upload-group">
                            <input type="text" name="logoUrl" value={setting.logoUrl} onChange={handleChange} className="form-control" />
                            <button type="button" className="btn-upload" onClick={() => triggerUpload('logoUrl')}>
                                <Upload size={16} /> 업로드
                            </button>
                        </div>
                        {setting.logoUrl && (
                            <div className="image-preview-container">
                                <img src={getImageUrl(setting.logoUrl)} alt="Logo Preview" />
                            </div>
                        )}
                    </div>
                    <div className="form-group">
                        <label>로고 대체 텍스트 (Alt Text)</label>
                        <input type="text" name="logoAltText" value={setting.logoAltText} onChange={handleChange} className="form-control" />
                    </div>
                    <div className="form-group">
                        <label>하단 카피라이트 (Footer Copyright)</label>
                        <input type="text" name="footerCopyright" value={setting.footerCopyright} onChange={handleChange} className="form-control" placeholder="© 2026 YourCompany. All rights reserved." />
                    </div>
                </section>

                {/* Section 4: Advanced */}
                <section className="settings-section">
                    <h4>4. 고급 / 스크립트 연결 (Advanced)</h4>
                    <p className="settings-section-desc">통계 도구 및 로봇 설정</p>

                    <div className="form-group">
                        <label>대표 URL (Canonical URL)</label>
                        <input type="text" name="canonicalUrl" value={setting.canonicalUrl} onChange={handleChange} className="form-control" placeholder="https://www.yourdomain.com" />
                    </div>
                    <div className="form-group">
                        <label>구글 애널리틱스 ID (GA)</label>
                        <input type="text" name="googleAnalyticsId" value={setting.googleAnalyticsId} onChange={handleChange} className="form-control" placeholder="G-XXXXXXXXXX" />
                    </div>
                    <div className="form-group">
                        <label>Robots.txt 내용</label>
                        <textarea name="robotsTxt" value={setting.robotsTxt} onChange={handleChange} className="form-control" rows={4} style={{ fontFamily: 'monospace' }}></textarea>
                    </div>
                </section>
            </form>

            <input
                type="file"
                ref={fileInputRef}
                style={{ display: 'none' }}
                accept="image/*"
                onChange={handleFileUpload}
            />
        </div>
    );
};

export default AdminSetting;
