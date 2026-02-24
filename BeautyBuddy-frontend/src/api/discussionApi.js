// DiscussionApi.js

const API_BASE_URL = 'http://localhost:8080/api/discussions';

export async function getDiscussions() {
  const res = await fetch(`${API_BASE_URL}/all`, {
    credentials: 'include',
  });
  return res.json();
}

export async function getComments(discussionId) {
  const res = await fetch(`${API_BASE_URL}/${discussionId}/comments`, {
    credentials: 'include',
  });
  return res.json();
}

export async function createComment(discussionId, text, parentCommentId = null) {
  const res = await fetch(`${API_BASE_URL}/${discussionId}/comments`, {
    method: 'POST',
    credentials: 'include',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify({ text, parentCommentId }),
  });
  return res.ok;
}

export async function createDiscussion(title, body) {
  const res = await fetch(`${API_BASE_URL}`, {
    method: 'POST',
    credentials: 'include',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify({ title, body }),
  });
  return res.ok;
}
