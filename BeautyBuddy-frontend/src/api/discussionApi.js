export async function getDiscussions(page = 0, size = 10) {
    const res = await fetch(`http://localhost:8080/api/discussions?page=${page}&size=${size}`, {
        credentials: 'include'
    });
    return res.json();
}

export async function searchDiscussions(query) {
    const res = await fetch(`http://localhost:8080/api/discussions/search?query=${encodeURIComponent(query)}`, {
        credentials: 'include'
    });
    return res.json();
}

export async function createDiscussion( title, text ) {
    const response = await fetch(`http://localhost:8080/api/discussions`, {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        credentials: "include",
        body: JSON.stringify({ title, text }),
    });
    return response.ok;
}

export async function editDiscussion(discussionId, title, text) {
    const response = await fetch(`http://localhost:8080/api/discussions/${discussionId}/edit`, {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        credentials: "include",
        body: JSON.stringify({ title, text }),
    });
    return response.ok;
}

export async function createComment(discussionId, parentDiscussionCommentId, text) {
    const response = await fetch(`http://localhost:8080/api/discussions/${discussionId}/comment`, {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        credentials: "include",
        body: JSON.stringify({ parentDiscussionCommentId, text }),
    });
    return response.ok;
}

export async function editComment(commentId, text) {
    const response = await fetch(`http://localhost:8080/api/discussions/comments/${commentId}/edit`, {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        credentials: "include",
        body: JSON.stringify({ text }),
    });
    return response.ok;
}

export async function upvoteDiscussion(discussionId) {
    const response = await fetch(`http://localhost:8080/api/discussions/${discussionId}/upvote`, {
        method: "POST",
        credentials: "include",
    });
    return response.ok;
}

export async function removeUpvoteDiscussion(discussionId) {
    const response = await fetch(`http://localhost:8080/api/discussions/${discussionId}/upvote`, {
        method: "DELETE",
        credentials: "include",
    });
    return response.ok;
}

export async function upvoteComment(commentId) {
    const response = await fetch(`http://localhost:8080/api/discussions/comments/${commentId}/upvote`, {
        method: "POST",
        credentials: "include",
    });
    return response.ok;
}

export async function removeUpvoteComment(commentId) {
    const response = await fetch(`http://localhost:8080/api/discussions/comments/${commentId}/upvote`, {
        method: "DELETE",
        credentials: "include",
    });
    return response.ok;
}

export async function reportDiscussion(discussionId, reason) {
    const response = await fetch(`http://localhost:8080/api/discussions/${discussionId}/report`, {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        credentials: "include",
        body: JSON.stringify({ reason }),
    });
    return response.ok;
}

export async function reportComment(commentId, reason) {
    const response = await fetch(`http://localhost:8080/api/discussions/comments/${commentId}/report`, {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        credentials: "include",
        body: JSON.stringify({ reason }),
    });
    return response.ok;
}