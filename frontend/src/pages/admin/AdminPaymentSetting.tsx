import { useState, useEffect } from 'react';
import { Save, AlertCircle, CreditCard, Eye, EyeOff } from 'lucide-react';
import './AdminSetting.css';

interface PaymentConfig {
    id: number;
    provider: string; // "KCP", "KAKAOPAY", "TOSSPAY"
    clientKey: string;
    secretKey: string;
    cid: string;
    apiUrl: string;
    displayName: string;
    active: boolean; // boolean usually mapped as 'active' (Spring is properties style getter isActive)
}

const getIcon = (provider: string) => {
    switch (provider.toUpperCase()) {
        case 'KAKAOPAY': return <img src="/icons/kakao.svg" alt="KakaoPay" style={{ width: 24, height: 24, borderRadius: '50%' }} />;
        case 'TOSSPAY': return <span style={{ fontSize: '20px', display: 'inline-flex', alignItems: 'center' }}>🔵</span>; // Fallback 토스 블루 모티브
        case 'KCP': return <span style={{ fontSize: '20px', display: 'inline-flex', alignItems: 'center' }}>💳</span>; // NHN KCP
        default: return <CreditCard size={20} color="#00d2ff" />;
    }
};

const getProviderGuide = (provider: string) => {
    switch (provider.toUpperCase()) {
        case 'KAKAOPAY':
            return '카카오페이 개발자 센터에서 앱 키(Admin Key)를 Secret Key로 발급받아 입력하고, 가맹점 코드(CID)를 지정합니다.';
        case 'TOSSPAY':
            return '토스페이먼츠 개발자 센터에서 Client Key(클라이언트 키)와 Secret Key(시크릿 키)를 확인해 입력합니다.';
        case 'KCP':
            return 'NHN KCP 가맹점 관리자 페이지에서 부여받은 사이트코드(Site Cd)를 Client Key로, 사이트키(Site Key)를 Secret Key로 입력합니다.';
        default:
            return 'PG사 발급 키 정보를 입력해 주세요.';
    }
};

const AdminPaymentSetting = () => {
    const [payments, setPayments] = useState<PaymentConfig[]>([]);
    const [loading, setLoading] = useState(false);
    const [savingId, setSavingId] = useState<number | null>(null);
    const [message, setMessage] = useState<{ type: 'success' | 'error', text: string } | null>(null);
    const [visibleSecrets, setVisibleSecrets] = useState<Record<string, boolean>>({});

    const toggleSecret = (key: string) => {
        setVisibleSecrets(prev => ({ ...prev, [key]: !prev[key] }));
    };

    const fetchPayments = async () => {
        try {
            setLoading(true);
            const res = await fetch(`/api/admin/payment`);
            if (res.ok) {
                const data = await res.json();
                if (data && data.payments) {
                    setPayments(data.payments);
                }
            }
        } catch (error) {
            console.error('Failed to load payment settings:', error);
        } finally {
            setLoading(false);
        }
    };

    useEffect(() => {
        fetchPayments();
    }, []);

    const handleChange = (id: number, field: string, value: string | boolean) => {
        setPayments(prev => prev.map(p => p.id === id ? { ...p, [field]: value } : p));
    };

    const handleSave = async (payment: PaymentConfig) => {
        setMessage(null);
        setSavingId(payment.id);

        try {
            const payload = {
                ...payment,
                isActive: payment.active
            };
            const pgPath = payment.provider.toLowerCase();
            const res = await fetch(`/api/admin/payment/${pgPath}/save`, {
                method: 'POST',
                headers: { 'Content-Type': 'application/json' },
                body: JSON.stringify(payload)
            });

            if (res.ok) {
                setMessage({ type: 'success', text: `${payment.displayName} 설정이 성공적으로 저장되었습니다.` });
                setTimeout(() => setMessage(null), 3000);
            } else {
                setMessage({ type: 'error', text: '결제 설정 저장 중 오류가 발생했습니다.' });
            }
        } catch (error) {
            console.error('Failed to save payment settings:', error);
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
                    <h2>PG사 (결제 모듈) 연동 설정</h2>
                    <p>토스, 카카오페이, KCP 등 온라인 결제 서비스를 이용하기 위한 인증 키 및 API URL을 관리합니다.</p>
                </div>
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

            <div className="settings-grid" style={{ gridTemplateColumns: 'minmax(300px, 1fr)' }}>
                {payments.map((payment) => (
                    <section key={payment.id} className="settings-section">
                        <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: '1rem' }}>
                            <h4 style={{ display: 'flex', alignItems: 'center', gap: '0.5rem', margin: 0 }}>
                                {getIcon(payment.provider)} {payment.displayName} 연동
                            </h4>
                            <label style={{ display: 'flex', alignItems: 'center', gap: '0.5rem', fontSize: '0.9rem', cursor: 'pointer' }}>
                                <input
                                    type="checkbox"
                                    checked={payment.active || false}
                                    onChange={(e) => handleChange(payment.id, 'active', e.target.checked)}
                                />
                                활성화 (사용)
                            </label>
                        </div>

                        <p className="settings-section-desc">
                            {getProviderGuide(payment.provider)}
                        </p>

                        <div className="form-group">
                            <label>상점구분 (Provider)</label>
                            <input
                                type="text"
                                value={payment.provider}
                                className="form-control"
                                disabled
                                style={{ opacity: 0.7 }}
                            />
                        </div>

                        <div className="form-group">
                            <label>Client Key (클라이언트 키 / 사이트 코드)</label>
                            <input
                                type="text"
                                value={payment.clientKey || ''}
                                onChange={(e) => handleChange(payment.id, 'clientKey', e.target.value)}
                                className="form-control"
                                placeholder="Client Key 입력"
                            />
                        </div>

                        <div className="form-group">
                            <label>Secret Key (시크릿 키 / Admin 키)</label>
                            <div style={{ position: 'relative' }}>
                                <input
                                    type={visibleSecrets[`secret-${payment.id}`] ? 'text' : 'password'}
                                    value={payment.secretKey || ''}
                                    onChange={(e) => handleChange(payment.id, 'secretKey', e.target.value)}
                                    className="form-control"
                                    placeholder="Secret Key 입력"
                                    style={{ paddingRight: '2.5rem' }}
                                />
                                <button type="button" onClick={() => toggleSecret(`secret-${payment.id}`)} style={{ position: 'absolute', right: '0.5rem', top: '50%', transform: 'translateY(-50%)', background: 'none', border: 'none', color: 'var(--text-muted)', cursor: 'pointer', padding: '0.3rem' }}>
                                    {visibleSecrets[`secret-${payment.id}`] ? <EyeOff size={16} /> : <Eye size={16} />}
                                </button>
                            </div>
                        </div>

                        <div className="form-group">
                            <label>가맹점 코드 (CID - 카카오 등)</label>
                            <input
                                type="text"
                                value={payment.cid || ''}
                                onChange={(e) => handleChange(payment.id, 'cid', e.target.value)}
                                className="form-control"
                                placeholder="TC0ONETIME"
                            />
                        </div>
                        
                        <div className="form-group">
                            <label>PG사 API 엔드포인트 URL</label>
                            <input
                                type="text"
                                value={payment.apiUrl || ''}
                                onChange={(e) => handleChange(payment.id, 'apiUrl', e.target.value)}
                                className="form-control"
                                placeholder="https://api.tosspayments.com"
                            />
                        </div>

                        <div style={{ display: 'flex', justifyContent: 'flex-end', marginTop: '1.5rem' }}>
                            <button
                                type="button"
                                className="btn-save"
                                onClick={() => handleSave(payment)}
                                disabled={savingId === payment.id}
                                style={{ padding: '0.5rem 1rem', fontSize: '0.9rem' }}
                            >
                                <Save size={16} /> {savingId === payment.id ? '저장 중...' : '변경사항 저장'}
                            </button>
                        </div>
                    </section>
                ))}
            </div>
            {payments.length === 0 && !loading && (
                <div style={{ textAlign: 'center', padding: '3rem', color: '#94a3b8' }}>
                    등록된 PG 결제 설정이 없습니다. (Data Init을 확인하세요)
                </div>
            )}
        </div>
    );
};

export default AdminPaymentSetting;
