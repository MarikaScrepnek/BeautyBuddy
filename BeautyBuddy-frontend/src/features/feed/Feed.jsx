import Searchbar from '../../components/ui/Searchbar';
import UserSearch from '../user/components/UserSearch';
import './Feed.css';

import { useState } from 'react';

export default function Feed() {

    const [following, setFollowing] = useState([]);
    const [isSearching, setIsSearching] = useState(false);

    return (

            <div className="feed-container">
                <UserSearch isSearching={isSearching} setIsSearching={setIsSearching} placeholder="Search for users..." />
                {!isSearching &&
                <>
                    {following.length > 0 && !isSearching ? (
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