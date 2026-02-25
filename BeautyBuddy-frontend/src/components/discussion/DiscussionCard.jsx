import { createComment, getDiscussions, upvoteDiscussion, removeUpvoteDiscussion, upvoteComment, removeUpvoteComment, reportDiscussion, reportComment } from "../../api/discussionApi";
import "./DiscussionCard.css";


import { useState, useEffect } from "react";
import AuthModal from "../AuthModal";
import { getCurrentUser } from "../../api/authApi";

import ReportModal from "../ReportModal";

// Helper to highlight search term
function highlightText(text, term) {
  if (!term || !text) return text;
  const regex = new RegExp(`(${term.replace(/[.*+?^${}()|[\]\\]/g, '\\$&')})`, 'gi');
  return text.split(regex).map((part, i) =>
    regex.test(part)
      ? <mark key={i} style={{ background: '#ffe066', color: '#7b4b27', padding: '0 2px', borderRadius: '3px' }}>{part}</mark>
      : part
  );
}

export default function DiscussionCard({ id, createdAt, title, text, authorUsername, upvoteCount, commentCount, comments = [], hasUpvoted, searchTerm }) {
    const [reportModalOpen, setReportModalOpen] = useState(false);
    const [reportTarget, setReportTarget] = useState(null);
  const [showLeaveComment, setShowLeaveComment] = useState(false);
  const [commentText, setCommentText] = useState("");
  const [commentError, setCommentError] = useState("");
  const [commentLoading, setCommentLoading] = useState(false);
  const [localComments, setLocalComments] = useState(comments);
  const [replyingTo, setReplyingTo] = useState(null); // comment id
  const [replyText, setReplyText] = useState("");
  const [replyError, setReplyError] = useState("");
  const [replyLoading, setReplyLoading] = useState(false);
  const [isUpvoted, setIsUpvoted] = useState(hasUpvoted);
  const [localUpvoteCount, setLocalUpvoteCount] = useState(upvoteCount);
  // Track upvote state for comments
  const [commentUpvoteStates, setCommentUpvoteStates] = useState(() => {
    const map = {};
    (comments || []).forEach(c => { map[c.id] = c.hasUpvoted; });
    return map;
  });

  // Auth state
  const [showLoginModal, setShowLoginModal] = useState(false);
  const [isLoggedIn, setIsLoggedIn] = useState(false);
  useEffect(() => {
    getCurrentUser()
      .then(() => setIsLoggedIn(true))
      .catch(() => setIsLoggedIn(false));
    const handleAuthLogin = () => setIsLoggedIn(true);
    const handleAuthLogout = () => setIsLoggedIn(false);
    window.addEventListener("auth:login", handleAuthLogin);
    window.addEventListener("auth:logout", handleAuthLogout);
    return () => {
      window.removeEventListener("auth:login", handleAuthLogin);
      window.removeEventListener("auth:logout", handleAuthLogout);
    };
  }, []);

  // Helper to refresh comments from backend
  const refreshComments = async () => {
    const discussions = await getDiscussions();
    const discussion = (Array.isArray(discussions) ? discussions : discussions.content || []).find(d => d.id === id);
    setLocalComments(discussion?.comments || []);
    // Update upvote states for comments
    const map = {};
    (discussion?.comments || []).forEach(c => { map[c.id] = c.hasUpvoted; });
    setCommentUpvoteStates(map);
    // Update discussion upvote state
    if (typeof discussion?.hasUpvoted === 'boolean') setIsUpvoted(discussion.hasUpvoted);
  };

  const handleReport = async (targetType) => {
    if (!isLoggedIn) {
      setShowLoginModal(true);
      return;
    }
    if (targetType === 'discussion') {
      setReportTarget({ type: 'discussion', id, subtitle: authorUsername ? `Posted by ${authorUsername}` : '' });
      setReportModalOpen(true);
    } else if (targetType.type === 'comment') {
      setReportTarget({ type: 'comment', id: targetType.id, subtitle: targetType.authorUsername ? `Posted by ${targetType.authorUsername}` : '' });
      setReportModalOpen(true);
    }
  }

  const handleUpvote = async (targetType) => {
    if (!isLoggedIn) {
      setShowLoginModal(true);
      return;
    }
    if (targetType.type === 'discussion') {
      const success = await upvoteDiscussion(id);
      if (success) {
        setIsUpvoted(true);
        setLocalUpvoteCount(count => count + 1);
        await refreshComments();
      } else {
        alert("Failed to upvote discussion.");
      }
    } else if (targetType.type === 'comment') {
      const success = await upvoteComment(targetType.id);
      if (success) {
        setCommentUpvoteStates(states => ({ ...states, [targetType.id]: true }));
        await refreshComments();
      } else {
        alert("Failed to upvote comment.");
      }
    }
  }

  const handleRemoveUpvote = async (targetType) => {
    if (targetType.type === 'discussion') {
      const success = await removeUpvoteDiscussion(id);
      if (success) {
        setIsUpvoted(false);
        setLocalUpvoteCount(count => Math.max(0, count - 1));
        await refreshComments();
      } else {
        alert("Failed to remove upvote.");
      }
    } else if (targetType.type === 'comment') {
      const success = await removeUpvoteComment(targetType.id);
      if (success) {
        setCommentUpvoteStates(states => ({ ...states, [targetType.id]: false }));
        await refreshComments();
      } else {
        alert("Failed to remove upvote from comment.");
      }
    }
  }

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
        borderRadius: 16,
        border: '1px solid #e7e2dc',
        marginBottom: 8,
        padding: '10px 12px',
        color: '#000000',
        fontSize: '0.98rem',
        marginLeft: depth * 32,
        boxShadow: depth > 0 ? '0 2px 8px rgba(24,12,3,0.07)' : undefined,
      }}>
        <div className="reply-header">
          <div>{comment.text}</div>
          <div className="reply-top-right">
            <button
              className="discussion-action-btn"
              type="button"
              style={{ borderColor: '#b94a48', color: '#b94a48', fontSize: '0.75rem' }}
              onClick={() => {
                handleReport({ type: 'comment', id: comment.id, authorUsername: comment.authorUsername });
              }}
            >
              Report
            </button>
            <button
              className="discussion-action-btn"
              type="button"
              style={{fontSize: '0.75rem' }}
              onClick={() => {
                if (commentUpvoteStates[comment.id]) {
                  handleRemoveUpvote({ type: 'comment', id: comment.id });
                } else {
                  handleUpvote({ type: 'comment', id: comment.id });
                }
              }}
            >
              {commentUpvoteStates[comment.id] ? 'Undo' : 'Upvote'}
            </button>
            <span className="reply-upvotes">{comment.upvotes || 0} upvotes</span>
          </div>
        </div>
        <div style={{ fontWeight: 600, color: '#7f6b5b', marginBottom: 2 }}>
          <span style={{ fontWeight: 400, color: '#a08b7b', fontSize: '0.92rem' }}>
            Posted by {comment.authorUsername || 'Anonymous'} on {comment.createdAt ? new Date(comment.createdAt).toLocaleString() : ''}
          </span>
        </div>
        <button
          className="discussion-action-btn"
          style={{ marginTop: 6, fontSize: '0.95rem' }}
          onClick={() => {
            if (!isLoggedIn) {
              setShowLoginModal(true);
              return;
            }
            setReplyingTo(comment.id);
            setReplyText("");
            setReplyError("");
          }}
        >
          Reply
        </button>
        <p>{comment.replyCount} comments</p>
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
    <>
      <ReportModal
        isOpen={reportModalOpen}
        onClose={() => { setReportModalOpen(false); setReportTarget(null); }}
        title={reportTarget?.type === 'comment' ? 'Report comment' : 'Report discussion'}
        subtitle={reportTarget?.subtitle}
        placeholder={reportTarget?.type === 'comment' ? 'Tell us why you are reporting this comment...' : 'Tell us why you are reporting this discussion...'}
        onSubmit={async ({ reason }) => {
          if (!reportTarget) return;
          let success = false;
          if (reportTarget.type === 'discussion') {
            success = await reportDiscussion(id, reason);
          } else if (reportTarget.type === 'comment') {
            success = await reportComment(reportTarget.id, reason);
          }
          setReportModalOpen(false);
          setReportTarget(null);
          if (success) {
            alert('Report submitted. Thank you!');
          } else {
            alert('Failed to submit report.');
          }
        }}
      />
      <div className="discussions-container">
        <article className="discussion-card">
          <header className="discussion-card__header">
            <h2 className="discussion-card__title">{highlightText(title, searchTerm)}</h2>
            <span className="discussion-card__meta">
              <button
                className="discussion-action-btn"
                type="button"
                style={{ borderColor: '#b94a48', color: '#b94a48', fontSize: '0.75rem' }}
                onClick={() => {
                  handleReport('discussion');
                }}
              >
                Report
              </button>
              <button
                className="discussion-action-btn"
                type="button"
                style={{fontSize: '0.75rem' }}
                onClick={() => {
                  if (isUpvoted) {
                    handleRemoveUpvote({ type: 'discussion' });
                  } else {
                    handleUpvote({ type: 'discussion' });
                  }
                }}
              >
                {isUpvoted ? 'Undo' : 'Upvote'}
              </button>
              <span className="discussion-upvotes">{localUpvoteCount} upvotes</span>
            </span>
          </header>
          <div className="discussion-card__body">
            <p> {highlightText(text, searchTerm)} </p>
            <p className="discussion-meta">Posted by {authorUsername} on {new Date(createdAt).toLocaleDateString()}</p>
            <button
              className="discussion-action-btn"
              onClick={() => {
                if (!isLoggedIn) {
                  setShowLoginModal(true);
                  return;
                }
                setShowLeaveComment((v) => !v);
              }}
            >
              {showLeaveComment ? "Cancel" : "Reply"}
            </button>
            <span className="discussion-replies">{commentCount} replies</span>
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
            <strong style={{ color: '#7b4b27', fontSize: '1rem' }}>Replies</strong>
            {localComments.length === 0 ? (
              <div style={{ color: '#a08b7b', margin: '8px 0' }}>No replies yet.</div>
            ) : (
              <ul style={{ listStyle: 'none', padding: 0, margin: '8px 0' }}>
                {buildCommentTree(localComments).map(c => renderComment(c))}
              </ul>
            )}
          </div>
          <footer className="discussion-card__footer">
          </footer>
        </article>
      </div>
    {showLoginModal && (
      <AuthModal
        onClose={() => setShowLoginModal(false)}
        onLoginSuccess={() => {
          setShowLoginModal(false);
          setIsLoggedIn(true);
        }}
      />
    )}
    </>
  );
}
