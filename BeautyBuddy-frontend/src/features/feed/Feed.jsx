import UserSearch from '../user/components/UserSearch';
import { fetchFeed } from './api/feedApi';
import './Feed.css';
import { useNavigate } from 'react-router-dom';

import { useEffect, useState } from 'react';

function parseActivityPayload(payload) {
    if (payload && typeof payload === 'object') {
        return payload;
    }

    if (typeof payload === 'string') {
        try {
            return JSON.parse(payload);
        } catch {
            return { message: payload };
        }
    }

    return {};
}

function formatActivityLabel(activity) {
    return activity.action?.toLowerCase().replace(/_/g, ' ') ?? 'activity';
}

function normalizeFeedItem(item) {
    return item?.body ?? item ?? {};
}

export default function Feed() {

    const [isSearching, setIsSearching] = useState(false);

    const [feed, setFeed] = useState([]);

    const navigate = useNavigate();

    useEffect(() => {
        handleFetchFeed();
    }, []);

    async function handleFetchFeed() {
        try {
            const feedData = await fetchFeed();
            setFeed(Array.isArray(feedData?.content) ? feedData.content.map(normalizeFeedItem) : []);
        } catch (error) {
            console.error('Error fetching feed:', error);
        }
    }

    return (

            <div className="feed-container">
                <UserSearch isSearching={isSearching} setIsSearching={setIsSearching} placeholder="Search for users..." />
                {!isSearching &&
                <>
                    {feed.length > 0 && !isSearching ? (
                        <div className="feed-posts">
                            {feed.map((activity) => {
                                const payload = parseActivityPayload(activity.payload);
                                const message = payload.message ?? activity.payload ?? 'Activity update';
                                const imageUrl = payload?.media?.imageUrl ?? activity.imageUrl ?? null;
                                const imageAlt = payload?.productName
                                    ? `${payload.productName}${payload?.shadeName ? ` - ${payload.shadeName}` : ''}`
                                    : 'Activity media';
                                const key = activity.id ?? `${activity.type ?? 'activity'}-${activity.timestamp ?? message}`;

                                return (
                                    <article className="feed-card" key={key}>
                                        <div className="feed-card__header">
                                            <time className="feed-card__time" dateTime={activity.timestamp}>
                                                {new Date(activity.timestamp).toLocaleString()}
                                            </time>
                                        </div>
                                        <div style={{ display: 'flex', flexDirection: 'row', gap: '5px', alignItems: 'center' }}>
                                            <div style={{ display: 'flex', flexDirection: 'row', gap: '4px', flex: 1 }}>
                                                <p className="feed-card__actor" onClick={() => navigate(`/users/${activity.actorUsername}`)}>
                                                    @{activity.actorUsername}
                                                </p>
                                                <p className="feed-card__message">{message}</p>
                                            </div>
                                            {imageUrl && (
                                                <div className="feed-card__media">
                                                    <img src={imageUrl} alt={imageAlt} className="feed-card__image" draggable="false" />
                                                </div>
                                            )}
                                        </div>
                                    </article>
                                );
                            })}
                        </div>
                    ) : (
                        <p>Follow users to view their activity here!</p>
                    )}
                </>
                }
            </div>

    );
}