export async function followUser(username) {
    const res = await fetch(`/api/follow/${username}`, {
        method: 'POST',
        credentials: 'include',
    });
    if (!res.ok) {
        throw new Error('Failed to follow user');
    }
    return res.json();
}

export async function unfollowUser(username) {
    const res = await fetch(`/api/follow/${username}/unfollow`, {
        method: 'POST',
        credentials: 'include',
    });
    if (!res.ok) {
        throw new Error('Failed to unfollow user');
    }
    return res.json();
}

export async function getFollowers(username) {
    const res = await fetch(`/api/follow/${username}/followers`, {
        method: 'GET',
        credentials: 'include',
    });
    if (!res.ok) {
        throw new Error('Failed to get followers');
    }
    return res.json();
}

export async function getFollowing(username) {
    const res = await fetch(`/api/follow/${username}/following`, {
        method: 'GET',
        credentials: 'include',
    });
    if (!res.ok) {
        throw new Error('Failed to get following');
    }
    return res.json();
}