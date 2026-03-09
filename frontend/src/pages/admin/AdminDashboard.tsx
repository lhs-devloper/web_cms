import './AdminDashboard.css';

const AdminDashboard = () => {
    return (
        <div className="admin-dashboard">
            <h1 className="dashboard-title">Overview</h1>
            <div className="stats-grid">
                <div className="stat-card">
                    <h3>총 회원 수</h3>
                    <p className="stat-value">1,248</p>
                    <span className="stat-change positive">+12% 이번 달</span>
                </div>
                <div className="stat-card">
                    <h3>오늘의 신규 게시글</h3>
                    <p className="stat-value">45</p>
                    <span className="stat-change positive">+5% 어제 대비</span>
                </div>
                <div className="stat-card">
                    <h3>누적 트래픽</h3>
                    <p className="stat-value">84,302</p>
                    <span className="stat-change positive">+18% 이번 주</span>
                </div>
            </div>

            <div className="recent-activity-section">
                <h3>최근 통합 활동 로그</h3>
                <div className="activity-list">
                    <div className="activity-item">
                        <span className="activity-type user">새 회원</span>
                        <p className="activity-desc"><strong>홍길동</strong>님이 가입하셨습니다.</p>
                        <span className="activity-time">10분 전</span>
                    </div>
                    <div className="activity-item">
                        <span className="activity-type post">새 게시글</span>
                        <p className="activity-desc"><strong>Q&A 게시판</strong>에 새 글이 작성되었습니다.</p>
                        <span className="activity-time">32분 전</span>
                    </div>
                    <div className="activity-item">
                        <span className="activity-type warning">신고 접수</span>
                        <p className="activity-desc">자유게시판에 도배성 게시물 신고가 접수되었습니다.</p>
                        <span className="activity-time">1시간 전</span>
                    </div>
                </div>
            </div>
        </div>
    );
};

export default AdminDashboard;
