export async function getDiscussions(page = 0, size = 10) {
    const res = await fetch(`http://localhost:8080/api/discussions?page=${page}&size=${size}`, {
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

export async function createComment(discussionId, parentDiscussionCommentId, text) {
    const response = await fetch(`http://localhost:8080/api/discussions/${discussionId}/comment`, {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        credentials: "include",
        body: JSON.stringify({ parentDiscussionCommentId, text }),
    });
    return response.ok;
}