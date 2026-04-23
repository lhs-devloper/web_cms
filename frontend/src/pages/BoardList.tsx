import { useState, useEffect } from 'react';
import { useParams, Link, useNavigate } from 'react-router-dom';
import './Board.css';

interface Post {
    id: number;
    title: string;
    author: string;
    author_id: string;
    view_count: number;
    created_at: string;
    is_notice?: boolean;
}

const BoardList = () => {
    const { boardId } = useParams();
    const navigate = useNavigate();
    const [posts, setPosts] = useState<Post[]>([]);
    // @ts-ignore
    const [boardInfo, setBoardInfo] = useState<any>(null);
    const [loading, setLoading] = useState(true);

    useEffect(() => {
        const fetchPosts = async () => {
            try {
                const token = localStorage.getItem('accessToken');
                const headers: any = {};
                if (token) headers['Authorization'] = `Bearer ${token}`;

                const res = await fetch(`http://${window.location.hostname}:8080/api/board/${boardId}`, { headers });
                if (res.ok) {
                    const data = await res.json();
                    setBoardInfo(data.board);
                    setPosts(data.posts || []);
                } else if (res.status === 404) {
                    alert('존재하지 않는 게시판입니다.');
                    navigate(-1);
                    return;
                } else if (res.status === 403) {
                    alert('게시판 접근 권한이 없습니다.');
                    navigate(-1);
                    return;
                }
            } catch (error) {
                console.error(error);
            } finally {
                setLoading(false);
            }
        };

        fetchPosts();
    }, [boardId, navigate]);

    if (loading) return <div className="board-page"><div className="board-container">Loading...</div></div>;

    const formatDate = (dateString: string) => {
        const date = new Date(dateString);
        return `${date.getFullYear()}-${String(date.getMonth() + 1).padStart(2, '0')}-${String(date.getDate()).padStart(2, '0')}`;
    };

    return (
        <div className="board-page">
            <div className="board-container">
                <div className="board-header">
                    <h2 className="board-title">{boardId?.toUpperCase()} 게시판</h2>
                    <button className="btn-primary" onClick={() => navigate(`/board/${boardId}/write`)}>글쓰기</button>
                </div>

                <table className="board-table">
                    <thead>
                        <tr>
                            <th style={{ width: '80px' }}>번호</th>
                            <th>제목</th>
                            <th style={{ width: '120px' }}>작성자</th>
                            <th style={{ width: '120px' }}>작성일</th>
                            <th style={{ width: '80px' }}>조회수</th>
                        </tr>
                    </thead>
                    <tbody>
                        {posts.length === 0 ? (
                            <tr>
                                <td colSpan={5}>등록된 게시글이 없습니다.</td>
                            </tr>
                        ) : (
                            posts.map((post) => (
                                <tr key={post.id}>
                                    <td>{post.is_notice ? <span style={{ color: 'var(--primary-color)', fontWeight: 'bold' }}>공지</span> : post.id}</td>
                                    <td className="col-title">
                                        <Link to={`/board/${boardId}/view/${post.id}`}>{post.title}</Link>
                                    </td>
                                    <td>{post.author || '익명'}</td>
                                    <td>{formatDate(post.created_at)}</td>
                                    <td>{post.view_count || 0}</td>
                                </tr>
                            ))
                        )}
                    </tbody>
                </table>
            </div>
        </div>
    );
};

export default BoardList;
