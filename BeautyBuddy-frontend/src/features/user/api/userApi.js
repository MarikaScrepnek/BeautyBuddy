export async function searchUsers(query) {
    const res = await fetch(`/api/users/search?q=${encodeURIComponent(query)}`, {
        method: 'GET',
        credentials: 'include',
    });
    if (!res.ok) {
        throw new Error('Failed to search users');
    }
    return res.json();
}