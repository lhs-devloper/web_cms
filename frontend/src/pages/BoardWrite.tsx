import { useState, useEffect, useRef } from 'react';
import Quill from 'quill';
import 'quill/dist/quill.snow.css';
import { useParams, useNavigate, useLocation } from 'react-router-dom';
import './Board.css';

const BoardWrite = () => {
    const { boardId, id } = useParams(); // id exists if editing
    const navigate = useNavigate();
    const location = useLocation();

    const [title, setTitle] = useState('');
    const [content, setContent] = useState('');
    const [author, setAuthor] = useState('');
    const [password, setPassword] = useState(location.state?.password || '');
    const [loading, setLoading] = useState(false);

    const editorRef = useRef<HTMLDivElement>(null);
    const quillRef = useRef<Quill | null>(null);

    const isLoggedIn = !!localStorage.getItem('accessToken');

    useEffect(() => {
        if (editorRef.current && !quillRef.current) {
            quillRef.current = new Quill(editorRef.current, {
                theme: 'snow',
                placeholder: '내용을 입력하세요 (이미지 크기 조절 등이 가능합니다)',
                modules: {
                    toolbar: [
                        [{ 'header': [1, 2, 3, false] }],
                        ['bold', 'italic', 'underline', 'strike', 'blockquote'],
                        [{ 'color': [] }, { 'background': [] }],
                        [{ 'list': 'ordered' }, { 'list': 'bullet' }, { 'align': [] }],
                        ['link', 'image', 'video'],
                        ['clean']
                    ]
                }
            });

            quillRef.current.on('text-change', () => {
                if (quillRef.current) {
                    setContent(quillRef.current.root.innerHTML);
                }
            });
        }
    }, []);

    useEffect(() => {
        if (id) {
            setLoading(true);
            const token = localStorage.getItem('accessToken');
            const headers: any = {};
            if (token) headers['Authorization'] = `Bearer ${token}`;

            fetch(`/api/board/${boardId}/view/${id}`, { headers })
                .then(res => res.json())
                .then(data => {
                    if (data && data.post) {
                        setTitle(data.post.title || '');
                        setContent(data.post.content || '');
                        setAuthor(data.post.author || '');

                        if (quillRef.current && data.post.content) {
                            quillRef.current.clipboard.dangerouslyPasteHTML(data.post.content);
                        }
                    }
                })
                .catch(err => console.error(err))
                .finally(() => setLoading(false));
        }
    }, [boardId, id]);

    const handleSubmit = async (e: React.FormEvent) => {
        e.preventDefault();

        if (!title.trim() || !content.trim()) {
            alert('제목과 내용을 모두 입력해주세요.');
            return;
        }

        if (!isLoggedIn && (!author.trim() || !password.trim())) {
            alert('비회원은 작성자와 비밀번호를 입력해야 합니다.');
            return;
        }

        const token = localStorage.getItem('accessToken');
        const headers: any = { 'Content-Type': 'application/x-www-form-urlencoded' };
        if (token) headers['Authorization'] = `Bearer ${token}`;

        const params = new URLSearchParams();
        params.append('title', title);
        params.append('content', content);
        if (!isLoggedIn) {
            params.append('author', author);
            params.append('password', password);
        }
        if (id) {
            params.append('id', id);
        }

        const endpoint = id
            ? `/api/board/${boardId}/update`
            : `/api/board/${boardId}/save`;

        try {
            const res = await fetch(endpoint, {
                method: 'POST',
                headers,
                body: params
            });

            if (res.ok) {
                alert(id ? '수정되었습니다.' : '등록되었습니다.');
                navigate(id ? `/board/${boardId}/view/${id}` : `/board/${boardId}`);
            } else {
                const data = await res.json();
                alert(data.message || '저장 중 오류가 발생했습니다.');
            }
        } catch (error) {
            alert('저장 실패');
        }
    };

    if (loading) return <div className="board-page"><div className="board-container">Loading...</div></div>;

    return (
        <div className="board-page">
            <div className="board-container">
                <div className="board-header">
                    <h2 className="board-title">{id ? '게시글 수정' : '게시글 등록'}</h2>
                </div>

                <form onSubmit={handleSubmit} className="write-form">
                    <div className="form-group">
                        <label>제목</label>
                        <input
                            type="text"
                            className="form-control"
                            style={{ background: 'var(--input-bg)', color: 'var(--input-text)' }}
                            value={title}
                            onChange={e => setTitle(e.target.value)}
                            placeholder="제목을 입력하세요"
                        />
                    </div>

                    {!isLoggedIn && (
                        <div style={{ display: 'flex', gap: '1rem' }}>
                            <div className="form-group" style={{ flex: 1 }}>
                                <label>작성자</label>
                                <input
                                    type="text"
                                    className="form-control"
                                    style={{ background: 'var(--input-bg)', color: 'var(--input-text)' }}
                                    value={author}
                                    onChange={e => setAuthor(e.target.value)}
                                    placeholder="익명"
                                />
                            </div>
                            <div className="form-group" style={{ flex: 1 }}>
                                <label>비밀번호</label>
                                <input
                                    type="password"
                                    className="form-control"
                                    style={{ background: 'var(--input-bg)', color: 'var(--input-text)' }}
                                    value={password}
                                    onChange={e => setPassword(e.target.value)}
                                    placeholder={id ? "수정 권한 확인용 비밀번호" : "수정/삭제 시 사용할 비밀번호"}
                                />
                            </div>
                        </div>
                    )}

                    <div className="form-group quill-wrap">
                        <label>내용</label>
                        <div ref={editorRef} style={{ background: 'var(--input-bg)', color: 'var(--input-text)', minHeight: '400px', fontSize: '1rem' }}></div>
                    </div>

                    <div style={{ display: 'flex', justifyContent: 'flex-end', gap: '0.5rem', marginTop: '1rem' }}>
                        <button type="button" className="btn-secondary" onClick={() => navigate(-1)}>취소</button>
                        <button type="submit" className="btn-primary">저장</button>
                    </div>
                </form>
            </div>
        </div>
    );
};

export default BoardWrite;
