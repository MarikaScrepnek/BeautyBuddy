import { useEffect, useState } from 'react';
import { useNavigate } from 'react-router-dom';

import { fetchUserActivities } from '../../feed/api/feedApi';
import '../../feed/Feed.css';

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

function normalizeFeedItem(item) {
    return item?.body ?? item ?? {};
}

export default function Profile({ username }) {
    const [activities, setActivities] = useState([]);
    const navigate = useNavigate();

    useEffect(() => {
        handleFetchUserActivities();
    }, []);

    const handleFetchUserActivities = async () => {
        try {
            const data = await fetchUserActivities(username);
            setActivities(Array.isArray(data?.content) ? data.content.map(normalizeFeedItem) : []);
        } catch (error) {
            console.error('Error fetching user activities:', error);
        }
    };
    
    return (
        <div style={{ marginTop: '1rem' }}>
            <h1 style={{ textAlign: 'center' }}>{username}'s Activities</h1>

            {activities.length > 0 ? (
                <div className="feed-posts">
                    {activities.map((activity) => {
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
                                        {activity.timestamp ? new Date(activity.timestamp).toLocaleString() : 'Recently'}
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
                <p style={{ textAlign: 'center', color: '#888' }}>No activities yet.</p>
            )}
        </div>
    );
}