import { useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import './AdminDashboard.css';

interface Summary {
    totalMembers: number;
    todayVisitors: number;
    totalVisitors: number;
}

interface ActivityLog {
    id: number;
    ip: string;
    requestUrl: string;
    visitDate: string;
    visitTime: string;
}

interface DashData {
    latestPosts: any[];
}

const AdminDashboard = () => {
    const navigate = useNavigate();
    const [summary, setSummary] = useState<Summary>({ totalMembers: 0, todayVisitors: 0, totalVisitors: 0 });
    const [activityLogs, setActivityLogs] = useState<ActivityLog[]>([]);
    const [dashData, setDashData] = useState<DashData>({ latestPosts: [] });

    useEffect(() => {
        const fetchDashboardData = async () => {
            try {
                const token = localStorage.getItem('accessToken');
                const headers: any = {};
                if (token) headers['Authorization'] = `Bearer ${token}`;

                // 요약 통계
                const summaryRes = await fetch(`http://${window.location.hostname}:8080/api/admin/stats/summary`, { headers });
                if (summaryRes.ok) setSummary(await summaryRes.json());

                // 대시보드 데이터 (최근 게시글 등)
                const dashRes = await fetch(`http://${window.location.hostname}:8080/api/admin`, { headers });
                if (dashRes.ok) setDashData(await dashRes.json());

                // 최근 활동 로그 (5건)
                const logRes = await fetch(`http://${window.location.hostname}:8080/api/admin/stats/activity-log?page=0&size=5`, { headers });
                if (logRes.ok) {
                    const logData = await logRes.json();
                    setActivityLogs(logData.content || []);
                }
            } catch (err) {
                console.error(err);
            }
        };
        fetchDashboardData();
    }, []);

    return (
        <div className="admin-dashboard">
            <h1 className="dashboard-title">Overview</h1>
            <div className="stats-grid">
                <div className="stat-card">
                    <h3>총 회원 수</h3>
                    <p className="stat-value">{summary.totalMembers.toLocaleString()}</p>
                </div>
                <div className="stat-card">
                    <h3>오늘 방문자</h3>
                    <p className="stat-value">{summary.todayVisitors.toLocaleString()}</p>
                </div>
                <div className="stat-card">
                    <h3>누적 트래픽</h3>
                    <p className="stat-value">{summary.totalVisitors.toLocaleString()}</p>
                </div>
            </div>

            <div className="recent-activity-section">
                <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: '1.5rem' }}>
                    <h3 style={{ margin: 0 }}>최근 활동 로그</h3>
                    <button
                        onClick={() => navigate('/admin/activity-log')}
                        style={{
                            background: 'none',
                            border: '1px solid var(--primary-color)',
                            color: 'var(--primary-color)',
                            padding: '0.4rem 1rem',
                            borderRadius: '6px',
                            cursor: 'pointer',
                            fontWeight: 600,
                            fontSize: '0.85rem'
                        }}
                    >
                        더보기
                    </button>
                </div>
                {activityLogs.length > 0 ? (
                    <table style={{ width: '100%', borderCollapse: 'collapse' }}>
                        <thead>
                            <tr>
                                <th style={{ textAlign: 'left', padding: '0.8rem', borderBottom: '1px solid rgba(var(--glass-rgb), 0.1)', color: 'var(--text-disabled)', fontWeight: 600, fontSize: '0.85rem' }}>IP</th>
                                <th style={{ textAlign: 'left', padding: '0.8rem', borderBottom: '1px solid rgba(var(--glass-rgb), 0.1)', color: 'var(--text-disabled)', fontWeight: 600, fontSize: '0.85rem' }}>URL</th>
                                <th style={{ textAlign: 'left', padding: '0.8rem', borderBottom: '1px solid rgba(var(--glass-rgb), 0.1)', color: 'var(--text-disabled)', fontWeight: 600, fontSize: '0.85rem' }}>날짜</th>
                                <th style={{ textAlign: 'left', padding: '0.8rem', borderBottom: '1px solid rgba(var(--glass-rgb), 0.1)', color: 'var(--text-disabled)', fontWeight: 600, fontSize: '0.85rem' }}>시간</th>
                            </tr>
                        </thead>
                        <tbody>
                            {activityLogs.map(log => (
                                <tr key={log.id}>
                                    <td style={{ padding: '0.8rem', borderBottom: '1px solid rgba(var(--glass-rgb), 0.05)', color: 'var(--text-primary)', fontWeight: 600 }}>{log.ip}</td>
                                    <td style={{ padding: '0.8rem', borderBottom: '1px solid rgba(var(--glass-rgb), 0.05)', color: '#ddd' }}>{log.requestUrl}</td>
                                    <td style={{ padding: '0.8rem', borderBottom: '1px solid rgba(var(--glass-rgb), 0.05)', color: 'var(--text-disabled)' }}>{log.visitDate}</td>
                                    <td style={{ padding: '0.8rem', borderBottom: '1px solid rgba(var(--glass-rgb), 0.05)', color: 'var(--text-disabled)' }}>{log.visitTime}</td>
                                </tr>
                            ))}
                        </tbody>
                    </table>
                ) : (
                    <p style={{ textAlign: 'center', color: 'var(--text-disabled)', padding: '2rem 0' }}>활동 로그가 없습니다.</p>
                )}
            </div>

            {dashData.latestPosts && dashData.latestPosts.length > 0 && (
                <div className="recent-activity-section" style={{ marginTop: '2rem' }}>
                    <h3>최근 게시글</h3>
                    <div className="activity-list">
                        {dashData.latestPosts.map((post: any, idx: number) => (
                            <div className="activity-item" key={idx}>
                                <span className="activity-type post">게시글</span>
                                <p className="activity-desc">
                                    <strong>{post.boardName || '게시판'}</strong> - {post.title}
                                </p>
                                <span className="activity-time">
                                    {post.createdAt ? new Date(post.createdAt).toLocaleDateString() : ''}
                                </span>
                            </div>
                        ))}
                    </div>
                </div>
            )}
        </div>
    );
};

export default AdminDashboard;
