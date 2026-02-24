import "./DiscussionCard.css";
import { useState } from "react";
import { createComment } from "../../api/DiscussionApi";

export default function DiscussionCard({ id, title, body, author, createdAt, repliesCount, comments = [] }) {
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
    // This should call getDiscussions and update localComments for this discussion
    // For now, just clear the form
    setCommentText("");
    setReplyText("");
    setReplyingTo(null);
  };

  const handleCommentSubmit = async (e) => {
    e.preventDefault();
    const trimmed = commentText.trim();
    if (trimmed.length < 2) {
      setCommentError("Comment must be at least 2 characters.");
      return;
    }
    setCommentError("");
    setCommentLoading(true);
    const success = await createComment(id, trimmed, null);
    if (!success) {
      setCommentError("Failed to post comment. Please try again.");
      setCommentLoading(false);
      return;
    }
    await refreshComments();
    setCommentLoading(false);
  };

  const handleReplySubmit = async (e) => {
    e.preventDefault();
    const trimmed = replyText.trim();
    if (trimmed.length < 2) {
      setReplyError("Reply must be at least 2 characters.");
      return;
    }
    setReplyError("");
    setReplyLoading(true);
    const success = await createComment(id, trimmed, replyingTo);
    if (!success) {
      setReplyError("Failed to post reply. Please try again.");
      setReplyLoading(false);
      return;
    }
    await refreshComments();
    setReplyLoading(false);
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
      <li key={comment.id} style={{ marginLeft: depth * 32 }}>
        <div>{comment.authorUsername || 'Anonymous'} <span>{comment.createdAt ? new Date(comment.createdAt).toLocaleString() : ''}</span></div>
        <div>{comment.text}</div>
        <button onClick={() => { setReplyingTo(comment.id); setReplyText(""); setReplyError(""); }}>Comment on this</button>
        {replyingTo === comment.id && (
          <form onSubmit={handleReplySubmit}>
            <textarea value={replyText} onChange={e => setReplyText(e.target.value)} rows={2} maxLength={800} placeholder="Write your reply..." disabled={replyLoading} />
            {replyError && <div>{replyError}</div>}
            <button type="submit" disabled={replyLoading}>{replyLoading ? 'Posting...' : 'Submit reply'}</button>
            <button type="button" onClick={() => setReplyingTo(null)} disabled={replyLoading}>Cancel</button>
          </form>
        )}
        {comment.replies.length > 0 && (
          <ul>
            {comment.replies.map(r => renderComment(r, depth + 1))}
          </ul>
        )}
      </li>
    );
  }

  return (
    <article>
      <header>
        <h2>{title}</h2>
        <span>{author} • {new Date(createdAt).toLocaleDateString()}</span>
      </header>
      <div>
        {body}
        <button onClick={() => setShowLeaveComment((v) => !v)}>{showLeaveComment ? "Cancel" : "Comment"}</button>
        {showLeaveComment && (
          <form onSubmit={handleCommentSubmit}>
            <textarea value={commentText} onChange={e => setCommentText(e.target.value)} rows={3} maxLength={800} placeholder="Write your comment..." disabled={commentLoading} />
            {commentError && <div>{commentError}</div>}
            <button type="submit" disabled={commentLoading}>{commentLoading ? "Posting..." : "Submit comment"}</button>
          </form>
        )}
      </div>
      <div>
        <strong>Comments</strong>
        {localComments.length === 0 ? (
          <div>No comments yet.</div>
        ) : (
          <ul>
            {buildCommentTree(localComments).map(c => renderComment(c))}
          </ul>
        )}
      </div>
      <footer>
        <span>{repliesCount} replies</span>
      </footer>
    </article>
  );
}
