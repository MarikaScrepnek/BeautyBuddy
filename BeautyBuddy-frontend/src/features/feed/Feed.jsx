import UserSearch from '../user/components/UserSearch';
import { fetchFeed } from './api/feedApi';
import './Feed.css';

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

export default function Feed() {

    const [isSearching, setIsSearching] = useState(false);

    const [feed, setFeed] = useState([]);

    useEffect(() => {
        handleFetchFeed();
    }, []);

    async function handleFetchFeed() {
        try {
            const feedData = await fetchFeed();
            setFeed(feedData);
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

                                return (
                                    <article className="feed-card" key={activity.id}>
                                        <div className="feed-card__header">
                                            <div>
                                                <p className="feed-card__actor">@{activity.actorUsername}</p>
                                                <p className="feed-card__type">{formatActivityLabel(activity)}</p>
                                            </div>
                                            <time className="feed-card__time" dateTime={activity.timestamp}>
                                                {new Date(activity.timestamp).toLocaleString()}
                                            </time>
                                        </div>
                                        <p className="feed-card__message">{message}</p>
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