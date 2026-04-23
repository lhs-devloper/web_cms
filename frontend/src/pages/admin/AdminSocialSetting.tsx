import { useState, useEffect } from 'react';
import { Save, AlertCircle, Key } from 'lucide-react';
import './AdminSetting.css';

interface SocialConfig {
    id: number;
    registrationId: string;
    clientId: string;
    clientSecret: string;
    clientAuthenticationMethod: string;
    authorizationGrantType: string;
    redirectUri: string;
    scopes: string;
    clientName: string;
    authorizationUri: string;
    tokenUri: string;
    userInfoUri: string;
    userNameAttributeName: string;
    jwkSetUri: string;
    issuerUri: string;
    active: boolean; // boolean usually mapped as 'active' (Spring is properties style getter isActive)
}

const getIcon = (registrationId: string) => {
    switch (registrationId.toLowerCase()) {
        case 'kakao': return <img src="/icons/kakao.svg" alt="Kakao" style={{ width: 24, height: 24, borderRadius: '50%' }} />;
        case 'naver': return <img src="/icons/naver.svg" alt="Naver" style={{ width: 24, height: 24, borderRadius: '50%' }} />;
        case 'google': return <img src="/icons/google.svg" alt="Google" style={{ width: 24, height: 24, borderRadius: '50%' }} />;
        case 'github': return <img src="/icons/github.svg" alt="GitHub" style={{ width: 24, height: 24, borderRadius: '50%' }} />;
        default: return <Key size={20} color="#00d2ff" />;
    }
};

const AdminSocialSetting = () => {
    const [socials, setSocials] = useState<SocialConfig[]>([]);
    const [loading, setLoading] = useState(false);
    const [savingId, setSavingId] = useState<number | null>(null);
    const [message, setMessage] = useState<{ type: 'success' | 'error', text: string } | null>(null);

    const fetchSocials = async () => {
        try {
            setLoading(true);
            const res = await fetch(`http://${window.location.hostname}:8080/api/admin/social`);
            if (res.ok) {
                const data = await res.json();
                if (data && data.socials) {
                    setSocials(data.socials);
                }
            }
        } catch (error) {
            console.error('Failed to load social settings:', error);
        } finally {
            setLoading(false);
        }
    };

    useEffect(() => {
        fetchSocials();
    }, []);

    const handleChange = (id: number, field: string, value: string | boolean) => {
        setSocials(prev => prev.map(s => s.id === id ? { ...s, [field]: value } : s));
    };

    const handleSave = async (social: SocialConfig) => {
        setMessage(null);
        setSavingId(social.id);

        try {
            const payload = {
                ...social,
                isActive: social.active
            };

            const res = await fetch(`http://${window.location.hostname}:8080/api/admin/social/save`, {
                method: 'POST',
                headers: { 'Content-Type': 'application/json' },
                body: JSON.stringify(payload)
            });

            if (res.ok) {
                setMessage({ type: 'success', text: `${social.clientName} 설정이 성공적으로 저장되었습니다.` });
                setTimeout(() => setMessage(null), 3000);
            } else {
                setMessage({ type: 'error', text: '설정 저장 중 오류가 발생했습니다.' });
            }
        } catch (error) {
            console.error('Failed to save social settings:', error);
            setMessage({ type: 'error', text: '새로고침 후 다시 시도해주세요.' });
        } finally {
            setSavingId(null);
        }
    };

    if (loading) {
        return <div className="admin-setting-container"><div className="loading-indicator">설정 정보를 불러오는 중...</div></div>;
    }

    return (
        <div className="admin-setting-container">
            <div className="admin-setting-header">
                <div>
                    <h2>간편로그인(소셜) 설정</h2>
                    <p>Google, Naver, Kakao 등 간편로그인 연동에 필요한 클라이언트 키와 시크릿 키를 설정합니다.</p>
                </div>
            </div>

            {message && (
                <div className={`social-message ${message.type}`}>
                    <AlertCircle size={18} /> {message.text}
                </div>
            )}

            <div className="settings-grid" style={{ gridTemplateColumns: 'minmax(300px, 1fr)' }}>
                {socials.map((social) => (
                    <section key={social.id} className="settings-section">
                        <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: '1rem' }}>
                            <h4 style={{ display: 'flex', alignItems: 'center', gap: '0.5rem', margin: 0 }}>
                                {getIcon(social.registrationId)} {social.clientName} 로그인
                            </h4>
                            <label style={{ display: 'flex', alignItems: 'center', gap: '0.5rem', fontSize: '0.9rem', cursor: 'pointer' }}>
                                <input
                                    type="checkbox"
                                    checked={social.active || false}
                                    onChange={(e) => handleChange(social.id, 'active', e.target.checked)}
                                />
                                활성화 (사용)
                            </label>
                        </div>

                        <p className="settings-section-desc">
                            {social.clientName} 개발자 센터에서 발급받은 키를 입력하세요. (Registration ID: {social.registrationId})
                        </p>

                        <div className="form-group">
                            <label>Client ID (클라이언트 키)</label>
                            <input
                                type="text"
                                value={social.clientId || ''}
                                onChange={(e) => handleChange(social.id, 'clientId', e.target.value)}
                                className="form-control"
                                placeholder="Client ID 입력"
                            />
                        </div>

                        <div className="form-group">
                            <label>Client Secret (시크릿 키)</label>
                            <input
                                type="text"
                                value={social.clientSecret || ''}
                                onChange={(e) => handleChange(social.id, 'clientSecret', e.target.value)}
                                className="form-control"
                                placeholder="Client Secret 입력"
                            />
                        </div>

                        <div className="form-group">
                            <label>Redirect URI</label>
                            <input
                                type="text"
                                value={social.redirectUri || ''}
                                onChange={(e) => handleChange(social.id, 'redirectUri', e.target.value)}
                                className="form-control"
                                placeholder="{baseUrl}/{action}/oauth2/code/{registrationId}"
                            />
                        </div>

                        <div style={{ display: 'flex', justifyContent: 'flex-end', marginTop: '1.5rem' }}>
                            <button
                                type="button"
                                className="btn-save"
                                onClick={() => handleSave(social)}
                                disabled={savingId === social.id}
                                style={{ padding: '0.5rem 1rem', fontSize: '0.9rem' }}
                            >
                                <Save size={16} /> {savingId === social.id ? '저장 중...' : '저장'}
                            </button>
                        </div>
                    </section>
                ))}
            </div>
            {socials.length === 0 && !loading && (
                <div className="social-empty">
                    등록된 간편로그인 설정이 없습니다.
                </div>
            )}
        </div>
    );
};

export default AdminSocialSetting;
