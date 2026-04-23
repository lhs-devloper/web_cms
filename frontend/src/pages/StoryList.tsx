import { useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import { useSiteSetting } from '../contexts/SiteSettingContext';
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

export const StoryListContent = ({ setting, stories }: { setting: any; stories: Story[] }) => {
    const navigate = useNavigate();

    if (setting?.storyAdvancedMode) {
        return (
            <div className="story-page story-advanced" style={{ minHeight: '100%', width: '100%' }}>
                <style dangerouslySetInnerHTML={{ __html: setting?.storyCustomCss || '' }} />
                <div dangerouslySetInnerHTML={{ __html: setting?.storyCustomHtml || '' }} />
            </div>
        );
    }

    const getImageUrl = (url: string) => {
        if (!url) return '';
        return url.startsWith('http') ? url : `${url}`;
    };

    return (
        <div className="story-page" style={{ minHeight: '100%', width: '100%' }}>
            <div className="story-hero">
                <h1>{setting?.storyPageTitle || 'Stories'}</h1>
                <p>{setting?.storyPageSubtitle || ''}</p>
            </div>

            {stories.length === 0 ? (
                <div className="story-empty">
                    <p>등록된 스토리가 없습니다.</p>
                </div>
            ) : (
                <div className="story-grid">
                    {stories.map(story => (
                        <div key={story.id} className="story-card" onClick={() => navigate(`/stories/${story.id}`)}>
                            {story.thumbnailUrl ? (
                                <img
                                    src={getImageUrl(story.thumbnailUrl)}
                                    alt={story.title}
                                    className="story-card-thumbnail"
                                />
                            ) : (
                                <div className="story-card-no-thumbnail">No Image</div>
                            )}
                            <div className="story-card-body">
                                <div className="story-card-title">{story.title}</div>
                                {story.summary && <div className="story-card-summary">{story.summary}</div>}
                                <div className="story-card-date">
                                    {new Date(story.createdAt).toLocaleDateString('ko-KR')}
                                </div>
                            </div>
                        </div>
                    ))}
                </div>
            )}
        </div>
    );
};

const StoryList = () => {
    const { setting } = useSiteSetting();
    const [stories, setStories] = useState<Story[]>([]);

    useEffect(() => {
        const fetchStories = async () => {
            try {
                const res = await fetch(`/api/stories`);
                if (res.ok) {
                    const data = await res.json();
                    setStories(data);
                }
            } catch (err) {
                console.error('Failed to load stories:', err);
            }
        };
        fetchStories();
    }, []);

    return <StoryListContent setting={setting} stories={stories} />;
};

export default StoryList;
