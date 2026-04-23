import { useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import { ArrowLeft, Search } from 'lucide-react';
import './AdminProducts.css';

interface ActivityLog {
    id: number;
    ip: string;
    userAgent: string;
    requestUrl: string;
    visitDate: string;
    visitTime: string;
}

interface PageData {
    content: ActivityLog[];
    totalPages: number;
    totalElements: number;
    number: number;
}

const AdminActivityLog = () => {
    const navigate = useNavigate();
    const [logs, setLogs] = useState<ActivityLog[]>([]);
    const [page, setPage] = useState(0);
    const [totalPages, setTotalPages] = useState(0);
    const [startDate, setStartDate] = useState('');
    const [endDate, setEndDate] = useState('');
    const [ip, setIp] = useState('');

    const fetchLogs = async (pageNum: number) => {
        try {
            const token = localStorage.getItem('accessToken');
            const headers: any = {};
            if (token) headers['Authorization'] = `Bearer ${token}`;

            const params = new URLSearchParams();
            params.append('page', pageNum.toString());
            params.append('size', '20');
            if (startDate) params.append('startDate', startDate);
            if (endDate) params.append('endDate', endDate);
            if (ip) params.append('ip', ip);

            const res = await fetch(`http://${window.location.hostname}:8080/api/admin/stats/activity-log?${params.toString()}`, { headers });
            if (res.ok) {
                const data: PageData = await res.json();
                setLogs(data.content || []);
                setTotalPages(data.totalPages || 0);
                setPage(data.number || 0);
            }
        } catch (err) {
            console.error(err);
        }
    };

    useEffect(() => {
        fetchLogs(0);
    }, []);

    const handleSearch = (e: React.FormEvent) => {
        e.preventDefault();
        fetchLogs(0);
    };

    return (
        <div className="admin-products-container">
            <div className="admin-products-header">
                <div style={{ display: 'flex', alignItems: 'center', gap: '1rem' }}>
                    <button
                        onClick={() => navigate('/admin')}
                        style={{
                            background: 'none',
                            border: 'none',
                            color: 'var(--text-primary)',
                            cursor: 'pointer',
                            display: 'flex',
                            alignItems: 'center'
                        }}
                    >
                        <ArrowLeft size={22} />
                    </button>
                    <div>
                        <h2>활동 로그</h2>
                        <p className="header-desc">방문자 활동 로그를 조회합니다.</p>
                    </div>
                </div>
            </div>

            <form onSubmit={handleSearch} style={{ display: 'flex', gap: '1rem', marginBottom: '1.5rem', alignItems: 'flex-end', flexWrap: 'wrap' }}>
                <div style={{ display: 'flex', flexDirection: 'column', gap: '0.3rem' }}>
                    <label style={{ fontSize: '0.85rem', color: 'var(--muted-color)', fontWeight: 600 }}>시작일</label>
                    <input
                        type="date"
                        value={startDate}
                        onChange={e => setStartDate(e.target.value)}
                        style={{
                            padding: '0.6rem 1rem',
                            background: 'var(--input-bg)',
                            border: '1px solid var(--input-border)',
                            borderRadius: '6px',
                            color: 'var(--text-primary)',
                            fontSize: '0.95rem'
                        }}
                    />
                </div>
                <div style={{ display: 'flex', flexDirection: 'column', gap: '0.3rem' }}>
                    <label style={{ fontSize: '0.85rem', color: 'var(--muted-color)', fontWeight: 600 }}>종료일</label>
                    <input
                        type="date"
                        value={endDate}
                        onChange={e => setEndDate(e.target.value)}
                        style={{
                            padding: '0.6rem 1rem',
                            background: 'var(--input-bg)',
                            border: '1px solid var(--input-border)',
                            borderRadius: '6px',
                            color: 'var(--text-primary)',
                            fontSize: '0.95rem'
                        }}
                    />
                </div>
                <div style={{ display: 'flex', flexDirection: 'column', gap: '0.3rem' }}>
                    <label style={{ fontSize: '0.85rem', color: 'var(--muted-color)', fontWeight: 600 }}>IP 주소</label>
                    <input
                        type="text"
                        placeholder="IP 주소 검색"
                        value={ip}
                        onChange={e => setIp(e.target.value)}
                        style={{
                            padding: '0.6rem 1rem',
                            background: 'var(--input-bg)',
                            border: '1px solid var(--input-border)',
                            borderRadius: '6px',
                            color: 'var(--text-primary)',
                            fontSize: '0.95rem'
                        }}
                    />
                </div>
                <button
                    type="submit"
                    className="btn-add-product"
                    style={{ height: 'fit-content' }}
                >
                    <Search size={16} /> 검색
                </button>
            </form>

            <div className="products-list-section">
                <table className="products-table">
                    <thead>
                        <tr>
                            <th>IP</th>
                            <th>User Agent</th>
                            <th>URL</th>
                            <th>날짜</th>
                            <th>시간</th>
                        </tr>
                    </thead>
                    <tbody>
                        {logs.map(log => (
                            <tr key={log.id}>
                                <td style={{ fontWeight: 600 }}>{log.ip}</td>
                                <td style={{ maxWidth: '300px', overflow: 'hidden', textOverflow: 'ellipsis', whiteSpace: 'nowrap' }}>{log.userAgent}</td>
                                <td>{log.requestUrl}</td>
                                <td>{log.visitDate}</td>
                                <td>{log.visitTime}</td>
                            </tr>
                        ))}
                        {logs.length === 0 && (
                            <tr>
                                <td colSpan={5} className="empty-message">활동 로그가 없습니다.</td>
                            </tr>
                        )}
                    </tbody>
                </table>
            </div>

            {totalPages > 0 && (
                <div style={{ display: 'flex', justifyContent: 'center', alignItems: 'center', gap: '1rem', marginTop: '1.5rem' }}>
                    <button
                        onClick={() => fetchLogs(page - 1)}
                        disabled={page === 0}
                        style={{
                            padding: '0.5rem 1rem',
                            background: page === 0 ? 'var(--bg-darker)' : 'var(--primary-color)',
                            color: page === 0 ? 'var(--text-disabled)' : 'var(--bg-black)',
                            border: 'none',
                            borderRadius: '6px',
                            cursor: page === 0 ? 'not-allowed' : 'pointer',
                            fontWeight: 600
                        }}
                    >
                        이전
                    </button>
                    <span style={{ color: 'var(--text-primary)', fontWeight: 600 }}>
                        {page + 1} / {totalPages}
                    </span>
                    <button
                        onClick={() => fetchLogs(page + 1)}
                        disabled={page >= totalPages - 1}
                        style={{
                            padding: '0.5rem 1rem',
                            background: page >= totalPages - 1 ? 'var(--bg-darker)' : 'var(--primary-color)',
                            color: page >= totalPages - 1 ? 'var(--text-disabled)' : 'var(--bg-black)',
                            border: 'none',
                            borderRadius: '6px',
                            cursor: page >= totalPages - 1 ? 'not-allowed' : 'pointer',
                            fontWeight: 600
                        }}
                    >
                        다음
                    </button>
                </div>
            )}
        </div>
    );
};

export default AdminActivityLog;
