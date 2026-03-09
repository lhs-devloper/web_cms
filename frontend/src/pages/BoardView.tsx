import { useState, useEffect } from 'react';
import { useParams, useNavigate } from 'react-router-dom';
import 'quill/dist/quill.snow.css';
import './Board.css';

const BoardView = () => {
    const { boardId, id } = useParams();
    const navigate = useNavigate();
    const [post, setPost] = useState<any>(null);
    const [loading, setLoading] = useState(true);
    const [passwordPrompt, setPasswordPrompt] = useState<'edit' | 'delete' | null>(null);
    const [passwordInput, setPasswordInput] = useState('');
    const isLoggedIn = !!localStorage.getItem('accessToken');

    useEffect(() => {
        const fetchPost = async () => {
            try {
                const token = localStorage.getItem('accessToken');
                const headers: any = {};
                if (token) headers['Authorization'] = `Bearer ${token}`;

                const res = await fetch(`http://${window.location.hostname}:8080/api/board/${boardId}/view/${id}`, { headers });
                if (res.ok) {
                    const data = await res.json();
                    setPost(data.post);
                } else if (res.status === 403) {
                    alert('접근 권한이 없습니다.');
                    navigate(`/board/${boardId}`);
                }
            } catch (error) {
                console.error(error);
            } finally {
                setLoading(false);
            }
        };
        fetchPost();
    }, [boardId, id, navigate]);

    const handleDelete = async () => {
        if (!window.confirm('정말 삭제하시겠습니까?')) return;

        try {
            const token = localStorage.getItem('accessToken');
            const headers: any = { 'Content-Type': 'application/x-www-form-urlencoded' };
            if (token) headers['Authorization'] = `Bearer ${token}`;

            const res = await fetch(`http://${window.location.hostname}:8080/api/board/${boardId}/delete`, {
                method: 'POST',
                headers,
                body: new URLSearchParams({ id: id as string, password: passwordInput })
            });

            if (res.ok) {
                alert('삭제되었습니다.');
                navigate(`/board/${boardId}`);
            } else {
                const data = await res.json();
                alert(data.message || '삭제 권한이 없거나 비밀번호가 틀립니다.');
            }
        } catch (error) {
            alert('삭제 실패');
        } finally {
            setPasswordPrompt(null);
            setPasswordInput('');
        }
    };

    const handleActionClick = (action: 'edit' | 'delete') => {
        if (isLoggedIn) {
            if (action === 'delete') {
                handleDelete();
            } else {
                navigate(`/board/${boardId}/edit/${id}`);
            }
        } else {
            setPasswordPrompt(action);
        }
    };

    const handlePasswordSubmit = () => {
        if (passwordPrompt === 'delete') {
            handleDelete();
        } else {
            // For editing as guest, we can pre-pass the password via state or prompt again in Write page
            navigate(`/board/${boardId}/edit/${id}`, { state: { password: passwordInput } });
            setPasswordPrompt(null);
        }
    };

    if (loading) return <div className="board-page"><div className="board-container">Loading...</div></div>;
    if (!post) return <div className="board-page"><div className="board-container">게시글을 찾을 수 없습니다.</div></div>;

    return (
        <div className="board-page">
            <div className="board-container">
                <div className="post-header">
                    <h2 className="post-title">{post.title}</h2>
                    <div className="post-meta">
                        <span>작성자: {post.author || '익명'}</span>
                        <span>작성일: {new Date(post.created_at).toLocaleString()}</span>
                        <span>조회수: {post.view_count || 0}</span>
                    </div>
                </div>

                <div
                    className="post-content ql-editor"
                    dangerouslySetInnerHTML={{ __html: post.content }}
                ></div>

                <div className="post-actions">
                    <button className="btn-secondary" onClick={() => navigate(`/board/${boardId}`)}>목록</button>
                    <button className="btn-secondary" onClick={() => handleActionClick('edit')}>수정</button>
                    <button className="btn-danger" onClick={() => handleActionClick('delete')}>삭제</button>
                </div>
            </div>

            {passwordPrompt && (
                <div className="password-modal">
                    <div className="password-box">
                        <h4 style={{ marginBottom: '1rem', color: 'var(--text-color)' }}>
                            비밀번호 확인 ({passwordPrompt === 'edit' ? '수정' : '삭제'})
                        </h4>
                        <input
                            type="password"
                            className="form-control"
                            style={{ marginBottom: '1rem', background: 'var(--input-bg)', color: 'var(--input-text)' }}
                            placeholder="게시글 비밀번호"
                            value={passwordInput}
                            onChange={e => setPasswordInput(e.target.value)}
                        />
                        <div style={{ display: 'flex', gap: '0.5rem', justifyContent: 'flex-end' }}>
                            <button className="btn-secondary" onClick={() => { setPasswordPrompt(null); setPasswordInput(''); }}>취소</button>
                            <button className="btn-primary" onClick={handlePasswordSubmit}>확인</button>
                        </div>
                    </div>
                </div>
            )}
        </div>
    );
};

export default BoardView;
