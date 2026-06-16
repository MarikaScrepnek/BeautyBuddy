import UserSearch from '../user/components/UserSearch';
import { fetchFeed } from './api/feedApi';
import './Feed.css';

import { useEffect, useState } from 'react';

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
                            {/* Render posts from followed users here */}
                        </div>
                    ) : (
                        <p>Follow users to view their activity here!</p>
                    )}
                </>
                }
            </div>

    );
}