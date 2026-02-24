import { createComment, getDiscussions } from "../../api/DiscussionApi";
import "./DiscussionCard.css";


import { useState } from "react";

export default function DiscussionCard({ id, createdAt, title, text, author, upvoteCount, commentCount, comments = [] }) {
  const [showLeaveComment, setShowLeaveComment] = useState(false);
  const [commentText, setCommentText] = useState("");
  const [commentError, setCommentError] = useState("");
  const [commentLoading, setCommentLoading] = useState(false);
  const [localComments, setLocalComments] = useState(comments);
  const [replyingTo, setReplyingTo] = useState(null); // comment id
  const [replyText, setReplyText] = useState("");
  const [replyError, setReplyError] = useState("");
  const [replyLoading, setReplyLoading] = useState(false);

  // Helper to refresh comments from backend
  const refreshComments = async () => {
    const discussions = await getDiscussions();
    const discussion = (Array.isArray(discussions) ? discussions : discussions.content || []).find(d => d.id === id);
    setLocalComments(discussion?.comments || []);
  };

  const handleReply = async (e, {
    text,
    setText,
    setError,
    setLoading,
    parentId = null,
    afterSubmit = () => {}
  }) => {
    e.preventDefault();
    const trimmed = text.trim();
    if (trimmed.length < 2) {
      setError("Reply must be at least 2 characters.");
      return;
    }
    setError("");
    setLoading(true);
    const success = await createComment(id, parentId, trimmed);
    if (!success) {
      setError("Failed to post reply. Please try again.");
      setLoading(false);
      return;
    }
    await refreshComments();
    setText("");
    afterSubmit();
    setLoading(false);
  };

  // Helper to build nested comment tree
  function buildCommentTree(comments) {
    const map = {};
    comments.forEach(c => { map[c.id] = { ...c, replies: [] }; });
    const roots = [];
    comments.forEach(c => {
      if (c.parentId) {
        if (map[c.parentId]) map[c.parentId].replies.push(map[c.id]);
      } else {
        roots.push(map[c.id]);
      }
    });
    return roots;
  }

  // Recursive render for nested comments
  function renderComment(comment, depth = 0) {
    return (
      <li key={comment.id} style={{
        background: '#fff',
        borderRadius: 8,
        border: '1px solid #e7e2dc',
        marginBottom: 8,
        padding: '10px 12px',
        color: '#3d2b1f',
        fontSize: '0.98rem',
        marginLeft: depth * 32,
        boxShadow: depth > 0 ? '0 2px 8px rgba(24,12,3,0.07)' : undefined,
      }}>
        <div style={{ fontWeight: 600, color: '#7f6b5b', marginBottom: 2 }}>{comment.authorUsername || 'Anonymous'} <span style={{ fontWeight: 400, color: '#a08b7b', fontSize: '0.92rem' }}>{comment.createdAt ? new Date(comment.createdAt).toLocaleString() : ''}</span></div>
        <div>{comment.text}</div>
        <button
          className="discussion-action-btn"
          style={{ marginTop: 6, fontSize: '0.95rem' }}
          onClick={() => {
            setReplyingTo(comment.id);
            setReplyText("");
            setReplyError("");
          }}
        >
          Reply
        </button>
        {replyingTo === comment.id && (
          <form
            onSubmit={e => handleReply(e, {
              text: replyText,
              setText: setReplyText,
              setError: setReplyError,
              setLoading: setReplyLoading,
              parentId: replyingTo,
              afterSubmit: () => setReplyingTo(null)
            })}
            style={{ marginTop: 8 }}
          >
            <textarea
              value={replyText}
              onChange={e => setReplyText(e.target.value)}
              rows={2}
              maxLength={800}
              placeholder="Write your reply..."
              style={{ width: '100%', borderRadius: 8, border: '1px solid #e7e2dc', padding: 8, fontSize: '0.98rem', marginBottom: 4 }}
              disabled={replyLoading}
            />
            {replyError && <div style={{ color: '#b94a48', marginBottom: 4 }}>{replyError}</div>}
            <button
              type="submit"
              className="discussion-action-btn"
              style={{ background: '#7b4b27', color: '#fff', marginTop: 2 }}
              disabled={replyLoading}
            >
              {replyLoading ? 'Posting...' : 'Submit reply'}
            </button>
            <button
              type="button"
              className="discussion-action-btn"
              style={{ marginLeft: 8 }}
              onClick={() => setReplyingTo(null)}
              disabled={replyLoading}
            >
              Cancel
            </button>
          </form>
        )}
        {comment.replies.length > 0 && (
          <ul style={{ listStyle: 'none', padding: 0, margin: '8px 0 0 0' }}>
            {comment.replies.map(r => renderComment(r, depth + 1))}
          </ul>
        )}
      </li>
    );
  }

  return (
    <div className="discussions-container">
    <article className="discussion-card">
      <header className="discussion-card__header">
        <h2 className="discussion-card__title">{title}</h2>
        <span className="discussion-card__meta">
          {author} • {new Date(createdAt).toLocaleDateString()}
        </span>
      </header>
      <div className="discussion-card__body">
        <p> {text} </p>
        <button
          className="discussion-action-btn"
          onClick={() => setShowLeaveComment((v) => !v)}
        >
          {showLeaveComment ? "Cancel" : "Reply"}
        </button>
        {showLeaveComment && (
          <form
            className="discussion-comment-form"
            onSubmit={e => handleReply(e, {
              text: commentText,
              setText: setCommentText,
              setError: setCommentError,
              setLoading: setCommentLoading,
              parentId: null,
              afterSubmit: () => setShowLeaveComment(false)
            })}
            style={{ marginTop: 16 }}
          >
            <textarea
              className="discussion-comment-textarea"
              value={commentText}
              onChange={e => setCommentText(e.target.value)}
              rows={3}
              maxLength={800}
              placeholder="Write your comment..."
              style={{ width: "100%", borderRadius: 8, border: "1px solid #e7e2dc", padding: 10, fontSize: "1rem", marginBottom: 6 }}
              disabled={commentLoading}
            />
            {commentError && <div style={{ color: "#b94a48", marginBottom: 6 }}>{commentError}</div>}
            <button
              type="submit"
              className="discussion-action-btn"
              style={{ background: "#7b4b27", color: "#fff", marginTop: 2 }}
              disabled={commentLoading}
            >
              {commentLoading ? "Posting..." : "Submit comment"}
            </button>
          </form>
        )}
      </div>
      <div style={{ marginTop: 18 }}>
        <strong style={{ color: '#7b4b27', fontSize: '1rem' }}>Comments</strong>
        {localComments.length === 0 ? (
          <div style={{ color: '#a08b7b', margin: '8px 0' }}>No comments yet.</div>
        ) : (
          <ul style={{ listStyle: 'none', padding: 0, margin: '8px 0' }}>
            {buildCommentTree(localComments).map(c => renderComment(c))}
          </ul>
        )}
      </div>
      <footer className="discussion-card__footer">
        <span>{commentCount} replies</span>
      </footer>
    </article>
    </div>
  );
}
