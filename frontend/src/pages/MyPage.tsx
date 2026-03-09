import React, { useState, useEffect } from 'react';
import { Camera, Mail, Shield, CreditCard, Package, LogOut } from 'lucide-react';
import './MyPage.css';

const MyPage = () => {
    const [user, setUser] = useState({
        name: '로딩중...',
        email: '로딩중...',
        profileImage: 'https://via.placeholder.com/150',
        grade: '일반',
        points: 0
    });

    const [isEditModalOpen, setIsEditModalOpen] = useState(false);
    const [editName, setEditName] = useState('');
    const [passwords, setPasswords] = useState({
        currentPassword: '',
        newPassword: '',
        confirmPassword: ''
    });

    useEffect(() => {
        const fetchUserProfile = async () => {
            const token = localStorage.getItem('accessToken');
            if (!token) {
                window.location.href = '/login';
                return;
            }

            try {
                const response = await fetch(`http://${window.location.hostname}:8080/api/profile`, {
                    headers: {
                        'Authorization': `Bearer ${token}`
                    }
                });

                if (response.ok) {
                    const data = await response.json();
                    const picture = data.user.picture;
                    const profileImageUrl = picture
                        ? (picture.startsWith('http') ? picture : `http://${window.location.hostname}:8080${picture}`)
                        : 'https://via.placeholder.com/150';

                    setUser({
                        name: data.user.name,
                        email: data.user.email,
                        profileImage: profileImageUrl,
                        grade: data.user.role === 'ROLE_ADMIN' ? '관리자' : '일반',
                        points: 0 // 임시 포인트 (DB에 별도 point 칼럼 추가 시 매핑 가능)
                    });
                } else {
                    console.error('Failed to fetch profile', response.status);
                    if (response.status === 401 || response.status === 403) {
                        localStorage.removeItem('accessToken');
                        window.location.href = '/login';
                    }
                }
            } catch (error) {
                console.error("Error fetching profile", error);
            }
        };

        fetchUserProfile();
    }, []);

    const handleLogout = () => {
        // TODO: 실제 로그아웃 처리 (localStorage 토큰 삭제 및 백엔드 로그아웃 API 호출)
        localStorage.removeItem('accessToken');
        window.location.href = '/';
    };

    const fileInputRef = React.useRef<HTMLInputElement>(null);

    const handleImageUpload = async (e: React.ChangeEvent<HTMLInputElement>) => {
        const file = e.target.files?.[0];
        if (!file) return;

        const formData = new FormData();
        formData.append('file', file);
        formData.append('name', user.name);

        try {
            const token = localStorage.getItem('accessToken');
            const response = await fetch(`http://${window.location.hostname}:8080/api/profile/update`, {
                method: 'POST',
                headers: {
                    'Authorization': `Bearer ${token}`
                },
                body: formData
            });

            if (response.ok) {
                // To fetch the newly assigned uploaded url we just re-fetch profile data or update locally if exact response is given.
                // We will do a full refresh to get the latest updated data
                window.location.reload();
            } else {
                alert('프로필 이미지 변경에 실패했습니다.');
            }
        } catch (error) {
            console.error('Error uploading image', error);
            alert('업로드 중 오류가 발생했습니다.');
        }
    };

    const handleCameraClick = () => {
        fileInputRef.current?.click();
    };

    const handleOpenEditModal = () => {
        setEditName(user.name);
        setPasswords({ currentPassword: '', newPassword: '', confirmPassword: '' });
        setIsEditModalOpen(true);
    };

    const handleProfileUpdate = async (e: React.FormEvent) => {
        e.preventDefault();
        const formData = new FormData();
        formData.append('name', editName);

        try {
            const token = localStorage.getItem('accessToken');
            const response = await fetch(`http://${window.location.hostname}:8080/api/profile/update`, {
                method: 'POST',
                headers: { 'Authorization': `Bearer ${token}` },
                body: formData
            });

            if (response.ok) {
                alert('프로필이 업데이트되었습니다.');
                setUser({ ...user, name: editName });
            } else {
                alert('프로필 업데이트에 실패했습니다.');
            }
        } catch (error) {
            console.error('Update error', error);
        }
    };

    const handlePasswordUpdate = async (e: React.FormEvent) => {
        e.preventDefault();
        if (passwords.newPassword !== passwords.confirmPassword) {
            alert('새 비밀번호가 일치하지 않습니다.');
            return;
        }

        const formData = new FormData();
        formData.append('currentPassword', passwords.currentPassword);
        formData.append('newPassword', passwords.newPassword);
        formData.append('confirmPassword', passwords.confirmPassword);

        try {
            const token = localStorage.getItem('accessToken');
            const response = await fetch('http://localhost:8080/api/profile/password', {
                method: 'POST',
                headers: { 'Authorization': `Bearer ${token}` },
                body: formData
            });

            if (response.ok) {
                alert('비밀번호가 변경되었습니다. 다시 로그인해주세요.');
                handleLogout();
            } else {
                const data = await response.json();
                alert(data.message || '비밀번호 변경에 실패했습니다.');
            }
        } catch (error) {
            console.error('Password update error', error);
        }
    };

    return (
        <div className="mypage-container container animate-fade-in">
            <div className="mypage-header">
                <h2>My Account</h2>
                <p>프리미엄 멤버십 대시보드</p>
            </div>

            <div className="mypage-content">
                {/* 좌측: 프로필 카드 */}
                <aside className="profile-card glass-panel">
                    <div className="profile-image-wrapper">
                        <img src={user.profileImage} alt="Profile" className="profile-image" />
                        <button className="camera-btn" aria-label="Change profile image" onClick={handleCameraClick}>
                            <Camera size={16} />
                        </button>
                        <input
                            type="file"
                            ref={fileInputRef}
                            style={{ display: 'none' }}
                            accept="image/*"
                            onChange={handleImageUpload}
                        />
                    </div>

                    <h3 className="profile-name">{user.name}</h3>
                    <p className="profile-email"><Mail size={14} /> {user.email}</p>

                    <div className="profile-stats">
                        <div className="stat-box">
                            <span className="stat-label">Membership</span>
                            <span className="stat-value grade-vip">{user.grade}</span>
                        </div>
                        <div className="stat-box">
                            <span className="stat-label">Points</span>
                            <span className="stat-value">{user.points.toLocaleString()}P</span>
                        </div>
                    </div>

                    <button className="btn-logout" onClick={handleLogout}>
                        <LogOut size={18} /> 로그아웃
                    </button>
                </aside>

                {/* 우측: 메뉴 및 활동 영역 */}
                <main className="mypage-main">
                    <div className="menu-grid">
                        <div className="menu-card glass-panel cursor-pointer">
                            <div className="menu-icon-wrapper">
                                <Package size={24} />
                            </div>
                            <div className="menu-info">
                                <h4>주문/배송 조회</h4>
                                <p>진행 중인 주문 2건</p>
                            </div>
                        </div>

                        <div className="menu-card glass-panel cursor-pointer">
                            <div className="menu-icon-wrapper">
                                <CreditCard size={24} />
                            </div>
                            <div className="menu-info">
                                <h4>결제 수단 관리</h4>
                                <p>등록된 카드 1개</p>
                            </div>
                        </div>

                        <div className="menu-card glass-panel cursor-pointer" onClick={handleOpenEditModal}>
                            <div className="menu-icon-wrapper">
                                <Shield size={24} />
                            </div>
                            <div className="menu-info">
                                <h4>개인정보 수정</h4>
                                <p>비밀번호 및 연락처 변경</p>
                            </div>
                        </div>
                    </div>

                    <div className="recent-orders glass-panel">
                        <div className="section-title-row">
                            <h3>최근 주문 내역</h3>
                            <button className="btn-text">전체보기</button>
                        </div>

                        <div className="order-list">
                            <div className="order-item">
                                <img src="https://images.unsplash.com/photo-1598300042247-d088f8ab3a91?q=80&w=100&auto=format&fit=crop" alt="product" />
                                <div className="order-details">
                                    <span className="order-date">2026.02.21</span>
                                    <h4>Aero Minimalist Chair</h4>
                                    <p className="order-price">₩320,000</p>
                                </div>
                                <div className="order-status delivering">배송중</div>
                            </div>

                            <div className="order-item">
                                <img src="https://images.unsplash.com/photo-1507473885765-e6ed057f782c?q=80&w=100&auto=format&fit=crop" alt="product" />
                                <div className="order-details">
                                    <span className="order-date">2026.02.15</span>
                                    <h4>Lumina Desk Lamp</h4>
                                    <p className="order-price">₩150,000</p>
                                </div>
                                <div className="order-status completed">배송완료</div>
                            </div>
                        </div>
                    </div>
                </main>
            </div>

            {isEditModalOpen && (
                <div className="modal-overlay">
                    <div className="modal-content glass-panel animate-fade-in">
                        <h3>개인정보 수정</h3>

                        <form onSubmit={handleProfileUpdate} className="edit-form">
                            <h4>프로필 정보 변경</h4>
                            <div className="input-group">
                                <label>이름</label>
                                <input
                                    type="text"
                                    value={editName}
                                    onChange={(e) => setEditName(e.target.value)}
                                />
                            </div>
                            <button type="submit" className="btn-primary">이름 변경</button>
                        </form>

                        <hr className="modal-divider" />

                        <form onSubmit={handlePasswordUpdate} className="edit-form">
                            <h4>비밀번호 변경</h4>
                            <div className="input-group">
                                <label>현재 비밀번호</label>
                                <input
                                    type="password"
                                    value={passwords.currentPassword}
                                    onChange={(e) => setPasswords({ ...passwords, currentPassword: e.target.value })}
                                />
                            </div>
                            <div className="input-group">
                                <label>새 비밀번호</label>
                                <input
                                    type="password"
                                    value={passwords.newPassword}
                                    onChange={(e) => setPasswords({ ...passwords, newPassword: e.target.value })}
                                />
                            </div>
                            <div className="input-group">
                                <label>새 비밀번호 확인</label>
                                <input
                                    type="password"
                                    value={passwords.confirmPassword}
                                    onChange={(e) => setPasswords({ ...passwords, confirmPassword: e.target.value })}
                                />
                            </div>
                            <button type="submit" className="btn-primary">비밀번호 변경</button>
                        </form>

                        <button className="btn-close" onClick={() => setIsEditModalOpen(false)}>닫기</button>
                    </div>
                </div>
            )}
        </div>
    );
};

export default MyPage;
