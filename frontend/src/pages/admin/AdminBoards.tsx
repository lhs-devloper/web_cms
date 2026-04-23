import React, { useState, useEffect } from 'react';
import { Plus, Edit2, Trash2, Eye } from 'lucide-react';
import './AdminBoards.css';

interface BoardMeta {
    id: number;
    boardId: string;
    tableName: string;
    readPermission: string;
    writePermission: string;
    checkUpdate: boolean;
    createdAt?: string;
}

const AdminBoards = () => {
    const [boards, setBoards] = useState<BoardMeta[]>([]);
    const [loading, setLoading] = useState(false);
    const [isEditing, setIsEditing] = useState(false);

    // Form State
    const [boardId, setBoardId] = useState('');
    const [readPermission, setReadPermission] = useState('ALL');
    const [writePermission, setWritePermission] = useState('MEMBER');
    const [checkUpdate, setCheckUpdate] = useState(false);

    const fetchBoards = async () => {
        try {
            setLoading(true);
            const res = await fetch(`/api/admin/board`);
            if (res.ok) {
                const data = await res.json();
                if (data.boards) {
                    setBoards(data.boards);
                }
            }
        } catch (error) {
            console.error('Failed to fetch boards:', error);
        } finally {
            setLoading(false);
        }
    };

    useEffect(() => {
        fetchBoards();
    }, []);

    const resetForm = () => {
        setBoardId('');
        setReadPermission('ALL');
        setWritePermission('MEMBER');
        setCheckUpdate(false);
        setIsEditing(false);
    };

    const handleEditClick = async (id: string) => {
        try {
            const res = await fetch(`/api/admin/board/edit/${id}`);
            if (res.ok) {
                const data = await res.json();
                if (data.board) {
                    setBoardId(data.board.boardId);
                    setReadPermission(data.board.readPermission);
                    setWritePermission(data.board.writePermission);
                    setCheckUpdate(data.board.checkUpdate);
                    setIsEditing(true);
                }
            }
        } catch (error) {
            console.error('Failed to load board details:', error);
        }
    };

    const handleDelete = async (id: string) => {
        if (!confirm(`'${id}' 게시판을 정말 삭제하시겠습니까? 관련 데이터가 모두 삭제됩니다 (되돌릴 수 없습니다).`)) return;

        try {
            const formData = new URLSearchParams();
            formData.append('boardId', id);

            const res = await fetch(`/api/admin/board/delete`, {
                method: 'POST',
                headers: { 'Content-Type': 'application/x-www-form-urlencoded' },
                body: formData.toString()
            });

            if (res.ok) {
                fetchBoards();
                if (isEditing && boardId === id) {
                    resetForm();
                }
            } else {
                alert('삭제 실패');
            }
        } catch (error) {
            console.error('Failed to delete board:', error);
        }
    };

    const handleSubmit = async (e: React.FormEvent) => {
        e.preventDefault();

        if (!boardId.trim()) {
            alert('게시판 ID를 입력해주세요.');
            return;
        }

        try {
            const formData = new URLSearchParams();
            formData.append('boardId', boardId);
            formData.append('readPermission', readPermission);
            formData.append('writePermission', writePermission);
            formData.append('checkUpdate', checkUpdate.toString());

            const endpoint = isEditing ? 'update' : 'create';

            const res = await fetch(`/api/admin/board/${endpoint}`, {
                method: 'POST',
                headers: { 'Content-Type': 'application/x-www-form-urlencoded' },
                body: formData.toString()
            });

            const data = await res.json();
            if (res.ok) {
                alert(data.message || '저장되었습니다.');
                fetchBoards();
                resetForm();
            } else {
                alert(data.message || '저장 실패');
            }
        } catch (error) {
            console.error('Failed to save board:', error);
            alert('저장 중 오류가 발생했습니다.');
        }
    };

    const getBadgeClass = (perm: string) => {
        if (perm === 'ALL') return 'all';
        if (perm === 'MEMBER') return 'member';
        return 'admin';
    };

    return (
        <div className="admin-boards-container">
            <div className="admin-boards-header">
                <div>
                    <h2>게시판 연동 관리</h2>
                    <p className="header-desc">동적으로 생성되는 게시판 DB 테이블과 권한을 통합 관리합니다.</p>
                </div>
                <button className="btn-add-board" onClick={resetForm}>
                    <Plus size={18} /> 새 게시판 생성
                </button>
            </div>

            <div className="boards-content">
                {/* Board List Section */}
                <div className="boards-list-section">
                    <h3>생성된 게시판 목록</h3>
                    {loading && <p className="loading-indicator">데이터를 불러오는 중입니다...</p>}

                    <table className="board-table">
                        <thead>
                            <tr>
                                <th>게시판 ID</th>
                                <th>참조 테이블</th>
                                <th>읽기 권한</th>
                                <th>쓰기 권한</th>
                                <th>업데이트 검증</th>
                                <th>관리</th>
                            </tr>
                        </thead>
                        <tbody>
                            {boards.map(board => (
                                <tr key={board.id}>
                                    <td style={{ fontWeight: '700', color: '#00d2ff' }}>{board.boardId}</td>
                                    <td style={{ fontSize: '0.85rem', color: '#64748b' }}>{board.tableName}</td>
                                    <td><span className={`board-badge ${getBadgeClass(board.readPermission)}`}><Eye size={12} style={{ marginRight: '3px', verticalAlign: 'middle' }} />{board.readPermission}</span></td>
                                    <td><span className={`board-badge ${getBadgeClass(board.writePermission)}`}><Edit2 size={12} style={{ marginRight: '3px', verticalAlign: 'middle' }} />{board.writePermission}</span></td>
                                    <td>
                                        {board.checkUpdate ?
                                            <span style={{ color: '#10b981', fontWeight: 600 }}>활성화</span> :
                                            <span style={{ color: '#64748b' }}>비활성</span>
                                        }
                                    </td>
                                    <td>
                                        <div className="action-btns">
                                            <button className="btn-edit-small" onClick={() => handleEditClick(board.boardId)} title="수정">
                                                <Edit2 size={14} /> 수정
                                            </button>
                                            <button className="btn-delete-small" onClick={() => handleDelete(board.boardId)} title="삭제">
                                                <Trash2 size={14} /> 삭제
                                            </button>
                                        </div>
                                    </td>
                                </tr>
                            ))}
                            {boards.length === 0 && !loading && (
                                <tr>
                                    <td colSpan={6} style={{ textAlign: 'center', padding: '3rem 0', color: '#94a3b8' }}>
                                        현재 생성된 게시판이 없습니다. 우측 폼을 통해 새로 생성해주세요.
                                    </td>
                                </tr>
                            )}
                        </tbody>
                    </table>
                </div>

                {/* Form Section */}
                <div className="board-form-section">
                    <h3>{isEditing ? `'${boardId}' 수정` : '새 게시판 생성 (테이블 자동생성)'}</h3>
                    <form onSubmit={handleSubmit}>
                        <div className="form-group">
                            <label>게시판 고유 ID (영어 소문자)</label>
                            <input
                                type="text"
                                className="form-control"
                                value={boardId}
                                onChange={(e) => setBoardId(e.target.value.toLowerCase().replace(/[^a-z0-9_]/g, ''))}
                                placeholder="ex) notice, qna, free"
                                disabled={isEditing}
                                required
                            />
                            {isEditing && <small style={{ color: '#f59e0b', display: 'block', marginTop: '5px' }}>수정 시 ID는 변경할 수 없습니다.</small>}
                            {!isEditing && <small style={{ color: '#64748b', display: 'block', marginTop: '5px' }}>DB에 'cms_board_[ID]' 테이블이 생성됩니다.</small>}
                        </div>

                        <div className="form-group">
                            <label><Eye size={16} style={{ verticalAlign: 'middle', marginRight: '5px' }} />읽기 권한</label>
                            <select className="form-control" value={readPermission} onChange={(e) => setReadPermission(e.target.value)}>
                                <option value="ALL">ALL (전체 - 비회원 가능)</option>
                                <option value="MEMBER">MEMBER (로그인 회원만)</option>
                                <option value="ADMIN">ADMIN (최고 관리자만)</option>
                            </select>
                        </div>

                        <div className="form-group">
                            <label><Edit2 size={16} style={{ verticalAlign: 'middle', marginRight: '5px' }} />글쓰기 권한</label>
                            <select className="form-control" value={writePermission} onChange={(e) => setWritePermission(e.target.value)}>
                                <option value="ALL">ALL (전체 - 비회원 가능)</option>
                                <option value="MEMBER">MEMBER (로그인 회원만)</option>
                                <option value="ADMIN">ADMIN (최고 관리자만)</option>
                            </select>
                        </div>

                        <div className="form-group">
                            <label className="checkbox-group" style={{ cursor: 'pointer', display: 'flex', alignItems: 'center' }}>
                                <input
                                    type="checkbox"
                                    checked={checkUpdate}
                                    onChange={(e) => setCheckUpdate(e.target.checked)}
                                />
                                <span style={{ fontWeight: 600 }}>기본 쿼리 업데이트 동기화 (CheckUpdate)</span>
                            </label>
                            <small style={{ color: '#64748b', display: 'block', marginTop: '-3px', marginLeft: '24px' }}>
                                체크 시 게시판 테이블 구조 변경이 감지되면 자동으로 ALTER 쿼리를 시도합니다. (신중히 사용)
                            </small>
                        </div>

                        <button type="submit" className="btn-submit">
                            {isEditing ? '변경사항 저장' : '생성 및 테이블 연동'}
                        </button>

                        {isEditing && (
                            <button type="button" className="btn-cancel" onClick={resetForm}>
                                취소
                            </button>
                        )}
                    </form>
                </div>
            </div>
        </div>
    );
};

export default AdminBoards;
