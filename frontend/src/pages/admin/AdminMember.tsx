import { useState, useEffect } from 'react';
import './AdminMember.css';

interface User {
    id: number;
    name: string;
    email: string;
    picture: string | null;
    role: string;
    provider: string;
}

const AdminMember = () => {
    const [users, setUsers] = useState<User[]>([]);
    const [roles, setRoles] = useState<string[]>([]);
    const [keyword, setKeyword] = useState('');
    const [selectedRole, setSelectedRole] = useState('');
    const [loading, setLoading] = useState(false);

    const fetchUsers = async () => {
        try {
            setLoading(true);
            const params = new URLSearchParams();
            if (keyword) params.append('keyword', keyword);
            if (selectedRole) params.append('role', selectedRole);

            const res = await fetch(`http://${window.location.hostname}:8080/api/admin/user?${params.toString()}`);
            const data = await res.json();
            if (data.users) setUsers(data.users);
            if (data.roles) setRoles(data.roles);
        } catch (error) {
            console.error('Failed to load users:', error);
        } finally {
            setLoading(false);
        }
    };

    useEffect(() => {
        const debounceTimer = setTimeout(() => {
            fetchUsers();
        }, 300);

        return () => clearTimeout(debounceTimer);
        // eslint-disable-next-line react-hooks/exhaustive-deps
    }, [keyword, selectedRole]);

    const handleRoleChange = async (id: number, newRole: string) => {
        if (!confirm('권한을 변경하시겠습니까?')) return;

        try {
            const formData = new URLSearchParams();
            formData.append('id', id.toString());
            formData.append('role', newRole);

            const res = await fetch(`http://${window.location.hostname}:8080/api/admin/user/update-role`, {
                method: 'POST',
                headers: { 'Content-Type': 'application/x-www-form-urlencoded' },
                body: formData.toString()
            });

            if (res.ok) {
                alert('권한이 변경되었습니다.');
                fetchUsers();
            } else {
                alert('변경 실패');
            }
        } catch (error) {
            console.error('Failed to update role', error);
        }
    };

    const handleDelete = async (id: number) => {
        if (!confirm('정말 삭제하시겠습니까? (이 작업은 되돌릴 수 없습니다)')) return;

        try {
            const formData = new URLSearchParams();
            formData.append('id', id.toString());

            const res = await fetch(`http://${window.location.hostname}:8080/api/admin/user/delete`, {
                method: 'POST',
                headers: { 'Content-Type': 'application/x-www-form-urlencoded' },
                body: formData.toString()
            });

            if (res.ok) {
                fetchUsers();
            } else {
                alert('삭제 실패');
            }
        } catch (error) {
            console.error('Failed to delete user', error);
        }
    };

    const getProfileImg = (picture: string | null) => {
        if (!picture) return 'https://via.placeholder.com/150';
        return picture.startsWith('http') ? picture : `http://${window.location.hostname}:8080${picture}`;
    };

    return (
        <div className="admin-member-container">
            <div className="admin-member-header">
                <h2>회원 관리</h2>
            </div>

            <div className="admin-member-filters">
                <input
                    type="text"
                    placeholder="이름 검색..."
                    className="filter-input"
                    value={keyword}
                    onChange={(e) => setKeyword(e.target.value)}
                />
                <select
                    className="filter-input"
                    value={selectedRole}
                    onChange={(e) => setSelectedRole(e.target.value)}
                >
                    <option value="">모든 권한 (전체)</option>
                    {roles.map(r => (
                        <option key={r} value={r}>{r}</option>
                    ))}
                </select>
                {loading && <span className="loading-indicator">로딩 중...</span>}
            </div>

            <div className="admin-member-list">
                <table className="member-table">
                    <thead>
                        <tr>
                            <th>프로필</th>
                            <th>이메일</th>
                            <th>가입 유형</th>
                            <th>권한</th>
                            <th>관리</th>
                        </tr>
                    </thead>
                    <tbody>
                        {users.map((user) => (
                            <tr key={user.id}>
                                <td>
                                    <div className="member-profile-cell">
                                        <img src={getProfileImg(user.picture)} alt="profile" className="member-profile-img" />
                                        <span className="member-name">{user.name}</span>
                                    </div>
                                </td>
                                <td>{user.email}</td>
                                <td>
                                    <span className={`provider-badge ${user.provider}`}>
                                        {user.provider}
                                    </span>
                                </td>
                                <td>
                                    <select
                                        className="role-select"
                                        value={user.role}
                                        onChange={(e) => handleRoleChange(user.id, e.target.value)}
                                    >
                                        {roles.map(r => (
                                            <option key={r} value={r}>{r}</option>
                                        ))}
                                    </select>
                                </td>
                                <td>
                                    <button className="btn-delete-small" onClick={() => handleDelete(user.id)}>삭제</button>
                                </td>
                            </tr>
                        ))}
                        {users.length === 0 && !loading && (
                            <tr>
                                <td colSpan={5} style={{ textAlign: 'center', color: '#999' }}>회원이 존재하지 않습니다.</td>
                            </tr>
                        )}
                    </tbody>
                </table>
            </div>
        </div>
    );
};

export default AdminMember;
