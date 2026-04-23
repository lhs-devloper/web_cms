import { useState, useEffect } from 'react';
import { Link } from 'react-router-dom';
import './Board.css';

interface BoardMeta {
    boardId: string;
    readPermission: string;
    writePermission: string;
}

const BoardIndex = () => {
    const [boards, setBoards] = useState<BoardMeta[]>([]);
    const [loading, setLoading] = useState(true);

    useEffect(() => {
        const fetchBoards = async () => {
            try {
                const res = await fetch(`http://${window.location.hostname}:8080/api/board`);
                if (res.ok) {
                    const data = await res.json();
                    setBoards(data);
                }
            } catch (error) {
                console.error(error);
            } finally {
                setLoading(false);
            }
        };
        fetchBoards();
    }, []);

    if (loading) return <div className="board-page"><div className="board-container">Loading...</div></div>;

    return (
        <div className="board-page">
            <div className="board-container">
                <div className="board-header">
                    <h2 className="board-title">게시판 목록</h2>
                </div>
                {boards.length === 0 ? (
                    <div style={{ textAlign: 'center', padding: '3rem', color: 'var(--text-muted)' }}>
                        등록된 게시판이 없습니다.
                    </div>
                ) : (
                    <div className="board-index-list">
                        {boards.map(board => (
                            <Link to={`/board/${board.boardId}`} key={board.boardId} className="board-index-card">
                                <h3>{board.boardId.toUpperCase()}</h3>
                                <span className="board-index-arrow">&rarr;</span>
                            </Link>
                        ))}
                    </div>
                )}
            </div>
        </div>
    );
};

export default BoardIndex;
