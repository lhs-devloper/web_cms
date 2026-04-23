import { useState, useEffect } from 'react';
import { Award } from 'lucide-react';
import './Membership.css';

interface Grade {
    id: number;
    code: string;
    name: string;
    description: string;
    minPoints: number;
    pointRate: number;
    benefits: string;
    color: string;
    sortOrder: number;
}

interface MyMembership {
    grade: Grade | null;
    availablePoints: number;
    totalPoints: number;
}

const Membership = () => {
    const [grades, setGrades] = useState<Grade[]>([]);
    const [myMembership, setMyMembership] = useState<MyMembership | null>(null);

    useEffect(() => {
        const fetchGrades = async () => {
            try {
                const res = await fetch(`/api/membership/grades`);
                if (res.ok) {
                    setGrades(await res.json());
                }
            } catch (err) {
                console.error(err);
            }
        };

        const fetchMyMembership = async () => {
            const token = localStorage.getItem('accessToken');
            if (!token) return;
            try {
                const res = await fetch(`/api/membership/my`, {
                    headers: { 'Authorization': `Bearer ${token}` }
                });
                if (res.ok) {
                    setMyMembership(await res.json());
                }
            } catch (err) {
                console.error(err);
            }
        };

        fetchGrades();
        fetchMyMembership();
    }, []);

    return (
        <div className="membership-page">
            <div className="membership-hero">
                <Award size={48} style={{ color: 'var(--primary-color)', marginBottom: '1rem' }} />
                <h1>Membership</h1>
                <p style={{ color: 'var(--muted-color)', marginTop: '0.5rem', fontSize: '1.1rem' }}>
                    등급별 혜택을 확인하고 더 많은 혜택을 누려보세요
                </p>
            </div>

            {myMembership && (
                <div className="my-grade-card">
                    <div
                        className="my-grade-badge"
                        style={{ background: myMembership.grade?.color || '#4f46e5' }}
                    >
                        {myMembership.grade?.name || '일반'}
                    </div>
                    <div className="my-points">
                        {myMembership.availablePoints?.toLocaleString() || '0'}P
                    </div>
                    <p style={{ color: 'var(--muted-color)', marginTop: '0.5rem', fontSize: '0.9rem' }}>
                        보유 포인트
                    </p>
                </div>
            )}

            <div className="grades-grid">
                {grades.map(grade => (
                    <div
                        className={`grade-info-card ${myMembership?.grade?.id === grade.id ? 'current' : ''}`}
                        key={grade.id}
                    >
                        <div className="grade-info-header" style={{ background: grade.color || '#4f46e5' }}>
                            <h3>{grade.name}</h3>
                            {grade.description && <p style={{ margin: '0.3rem 0 0', opacity: 0.9, fontSize: '0.9rem' }}>{grade.description}</p>}
                        </div>
                        <div className="grade-info-body">
                            <div className="benefit-item">
                                <strong>필요 포인트:</strong> {grade.minPoints.toLocaleString()}P
                            </div>
                            <div className="benefit-item">
                                <strong>적립률:</strong> {grade.pointRate}%
                            </div>
                            {grade.benefits && grade.benefits.split('\n').filter(b => b.trim()).map((benefit, idx) => (
                                <div className="benefit-item" key={idx}>
                                    {benefit.trim()}
                                </div>
                            ))}
                        </div>
                        {myMembership?.grade?.id === grade.id && (
                            <div style={{
                                textAlign: 'center',
                                padding: '0.8rem',
                                background: 'rgba(var(--glass-rgb), 0.05)',
                                color: 'var(--primary-color)',
                                fontWeight: 700,
                                fontSize: '0.9rem'
                            }}>
                                현재 내 등급
                            </div>
                        )}
                    </div>
                ))}
                {grades.length === 0 && (
                    <div style={{ gridColumn: '1 / -1', textAlign: 'center', padding: '3rem', color: 'var(--text-disabled)' }}>
                        등록된 멤버십 등급이 없습니다.
                    </div>
                )}
            </div>
        </div>
    );
};

export default Membership;
