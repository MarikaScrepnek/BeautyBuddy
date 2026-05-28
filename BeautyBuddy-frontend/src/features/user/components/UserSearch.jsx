import { searchUsers } from "../api/userApi";
import { useState, useEffect } from "react";
import Searchbar from "../../../components/ui/Searchbar";
import { useNavigate } from "react-router-dom";
import { followUser } from "../api/followApi";
import { unfollowUser } from "../api/followApi";
import { getCurrentUser } from "../../auth/api/authApi";
import { getFollowing } from "../api/followApi";
import { getFollowers } from "../api/followApi";

import "./UserSearch.css";

export default function UserSearch({ isSearching, setIsSearching }) {

    const navigate = useNavigate();
    const [users, setUsers] = useState([]);
    
    const [following, setFollowing] = useState([]);
    const [followers, setFollowers] = useState([]);
    const [currentUser, setCurrentUser] = useState(null);

    useEffect(() => {
        handleGetCurrentUser();
    }, []);

    useEffect(() => {
        if (currentUser) {
            handleGetFollowing();
            handleGetFollowers();
        }
    }, [currentUser]);

    async function handleSearch(query) {
        try {
            const found = await searchUsers(query);
            setUsers(found);
            setIsSearching(true);
        } catch (error) {
            console.error('Error searching users:', error);
        }
    }

    async function handleGetCurrentUser() {
        try {
            const user = await getCurrentUser();
            console.log('Current user:', user);
            setCurrentUser(user);
            return user;
        } catch (error) {
            console.error('Error fetching current user:', error);
            return null;
        }
    }

    async function handleGetFollowing() {
        if (!currentUser) return;
        try {
            const followingRes = await getFollowing(currentUser.username);
            const followingUsernames = Array.isArray(followingRes)
                ? followingRes.map(u => (u && u.username) ? u.username : u)
                : [];
            console.log('Following:', followingUsernames);
            setFollowing(followingUsernames);
        } catch (error) {
            console.error('Error fetching following:', error);
        }
    }

    async function handleGetFollowers() {
        if (!currentUser) return;
        try {
            const followersRes = await getFollowers(currentUser.username);
            const followersUsernames = Array.isArray(followersRes)
                ? followersRes.map(u => (u && u.username) ? u.username : u)
                : [];
            console.log('Followers:', followersUsernames);
            setFollowers(followersUsernames);
        } catch (error) {
            console.error('Error fetching followers:', error);
        }
    }

    async function handleFollow(username) {
        if (!currentUser) return;
        try {
            await followUser(username);
            console.log(`Followed ${username}`);
            setFollowing(prev => [...prev, username]);
        } catch (error) {
            console.error('Error following user:', error);
        }
    }

    async function handleUnfollow(username) {
        if (!currentUser) return;
        try {
            await unfollowUser(username);
            console.log(`Unfollowed ${username}`);
            setFollowing(prev => prev.filter(u => u !== username));
        } catch (error) {
            console.error('Error unfollowing user:', error);
        }
    }

    return (

            <>
                <Searchbar placeholder="Search for users..." onSearch={handleSearch} />
                {isSearching && (
                    <>
                    {users.length > 0 ? (
                        <div className="user-list">
                            {users.map(user => (
                                <div key={user.username} className="user-card" onClick={() => {
                                    navigate(`/users/${user.username}`);
                                }}>
                                    {user.profilePictureUrl ? (
                                        <img src={user.profilePictureUrl}/>
                                    ) : (
                                        <div className="user-avatar-fallback">
                                            {user.username.charAt(0).toUpperCase()}
                                        </div>
                                    )}
                                    <h3>{user.username}</h3>
                                    {!following.includes(user.username) && !followers.includes(user.username) && (
                                        <button onClick={(e) => {
                                            e.stopPropagation();
                                            handleFollow(user.username);
                                        }}>
                                            Follow
                                        </button>
                                    )}
                                    {!following.includes(user.username) && followers.includes(user.username) && (
                                        <button onClick={(e) => {
                                            e.stopPropagation();
                                            handleFollow(user.username);
                                        }}>
                                            Follow Back
                                        </button>
                                    )}
                                    {following.includes(user.username) && (
                                        <button onClick={(e) => {
                                            e.stopPropagation();
                                            handleUnfollow(user.username);
                                        }}>
                                            Unfollow
                                        </button>
                                    )}
                                </div>
                            ))}
                        </div>
                    ) : (
                        <p>No users found.</p>
                    )}
                </>
                )}
            </>

    );
}