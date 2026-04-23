import { useState, useEffect } from 'react';
import { useParams, useNavigate } from 'react-router-dom';
import { ArrowLeft } from 'lucide-react';
import 'react-quill-new/dist/quill.snow.css';
import './Story.css';

interface Story {
    id: number;
    title: string;
    summary: string;
    thumbnailUrl: string;
    content: string;
    visible: boolean;
    sortOrder: number;
    createdAt: string;
}

const StoryDetail = () => {
    const { id } = useParams<{ id: string }>();
    const navigate = useNavigate();
    const [story, setStory] = useState<Story | null>(null);
    const [loading, setLoading] = useState(true);

    useEffect(() => {
        const fetchStory = async () => {
            try {
                const res = await fetch(`http://${window.location.hostname}:8080/api/stories/${id}`);
                if (res.ok) {
                    const data = await res.json();
                    setStory(data);
                } else {
                    alert('스토리를 찾을 수 없습니다.');
                    navigate(-1);
                }
            } catch (err) {
                console.error('Failed to load story:', err);
                alert('스토리를 불러오는 중 오류가 발생했습니다.');
                navigate(-1);
            } finally {
                setLoading(false);
            }
        };
        fetchStory();
    }, [id, navigate]);

    if (loading) {
        return (
            <div className="story-page">
                <div className="story-detail">
                    <p style={{ textAlign: 'center', color: 'var(--text-muted)' }}>불러오는 중...</p>
                </div>
            </div>
        );
    }

    if (!story) return null;

    return (
        <div className="story-page">
            <div className="story-detail">
                <button className="story-back-btn" onClick={() => navigate(-1)}>
                    <ArrowLeft size={18} /> 뒤로가기
                </button>

                <div className="story-detail-header">
                    <h1 className="story-detail-title">{story.title}</h1>
                    <div className="story-detail-date">
                        {new Date(story.createdAt).toLocaleDateString('ko-KR', {
                            year: 'numeric',
                            month: 'long',
                            day: 'numeric'
                        })}
                    </div>
                </div>

                <div
                    className="story-detail-content ql-editor"
                    dangerouslySetInnerHTML={{ __html: story.content || '' }}
                />
            </div>
        </div>
    );
};

export default StoryDetail;
