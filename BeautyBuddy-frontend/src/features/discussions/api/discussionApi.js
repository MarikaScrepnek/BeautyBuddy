import { API_BASE_URL } from '../../../config/apiBase';

export async function getDiscussions(page = 0, size = 10, sort = null) {
    const params = new URLSearchParams({ page: String(page), size: String(size) });
    if (sort) params.set("sort", sort);
    const res = await fetch(`${API_BASE_URL}/discussions?${params.toString()}`, {
        credentials: 'include'
    });
    return res.json();
}

export async function searchDiscussions(query, sort = null, page = 0, size = 10) {
    const params = new URLSearchParams({ query: String(query), page: String(page), size: String(size) });
    if (sort) params.set("sort", sort);
    const res = await fetch(`${API_BASE_URL}/discussions/search?${params.toString()}`, {
        credentials: 'include'
    });
    return res.json();
}

export async function createDiscussion( title, text ) {
    const response = await fetch(`${API_BASE_URL}/discussions`, {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        credentials: "include",
        body: JSON.stringify({ title, text }),
    });
    return response.ok;
}

export async function editDiscussion(discussionId, title, text) {
    const response = await fetch(`${API_BASE_URL}/discussions/${discussionId}/edit`, {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        credentials: "include",
        body: JSON.stringify({ title, text }),
    });
    return response.ok;
}

export async function createComment(discussionId, parentDiscussionCommentId, text) {
    const response = await fetch(`${API_BASE_URL}/discussions/${discussionId}/comment`, {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        credentials: "include",
        body: JSON.stringify({ parentDiscussionCommentId, text }),
    });
    return response.ok;
}

export async function editComment(commentId, text) {
    const response = await fetch(`${API_BASE_URL}/discussions/comments/${commentId}/edit`, {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        credentials: "include",
        body: JSON.stringify({ parentDiscussionCommentId: null, text }),
    });
    return response.ok;
}

export async function upvoteDiscussion(discussionId) {
    const response = await fetch(`${API_BASE_URL}/discussions/${discussionId}/upvote`, {
        method: "POST",
        credentials: "include",
    });
    return response.ok;
}

export async function removeUpvoteDiscussion(discussionId) {
    const response = await fetch(`${API_BASE_URL}/discussions/${discussionId}/upvote`, {
        method: "DELETE",
        credentials: "include",
    });
    return response.ok;
}

export async function upvoteComment(commentId) {
    const response = await fetch(`${API_BASE_URL}/discussions/comments/${commentId}/upvote`, {
        method: "POST",
        credentials: "include",
    });
    return response.ok;
}

export async function removeUpvoteComment(commentId) {
    const response = await fetch(`${API_BASE_URL}/discussions/comments/${commentId}/upvote`, {
        method: "DELETE",
        credentials: "include",
    });
    return response.ok;
}

export async function reportDiscussion(discussionId, reason) {
    const response = await fetch(`${API_BASE_URL}/discussions/${discussionId}/report`, {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        credentials: "include",
        body: JSON.stringify({ reason }),
    });
    return response.ok;
}

export async function reportComment(commentId, reason) {
    const response = await fetch(`${API_BASE_URL}/discussions/comments/${commentId}/report`, {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        credentials: "include",
        body: JSON.stringify({ reason }),
    });
    return response.ok;
}