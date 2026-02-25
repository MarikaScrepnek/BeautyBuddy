
import { createComment, getDiscussions, upvoteDiscussion, removeUpvoteDiscussion, upvoteComment, removeUpvoteComment, reportDiscussion, reportComment } from "../../api/discussionApi";
import "./DiscussionCard.css";
import { useState, useEffect } from "react";
import AuthModal from "../AuthModal";
import { getCurrentUser } from "../../api/authApi";
import ReportModal from "../ReportModal";

function highlightText(text, term) {
  if (!term || !text) return text;
  const regex = new RegExp(`(${term.replace(/[.*+?^${}()|[\]\\]/g, '\$&')})`, 'gi');
  return text.split(regex).map((part, i) =>
    regex.test(part)
      ? <mark key={i} style={{ background: '#ffe066', color: '#7b4b27', padding: '0 2px', borderRadius: '3px' }}>{part}</mark>
      : part
  );
}

function PostCard({ post, isDiscussion, currentUser, isLoggedIn, onReport, onUpvote, onRemoveUpvote, onReply, onEdit, upvoteState, replyState, searchTerm }) {
  const isOwn = currentUser && post.authorUsername && currentUser.username === post.authorUsername;
  const canEdit = isOwn && (!post.replies || post.replies.length === 0);
  // Add card background and border for comments
  // Unified card style for both discussion and comment
  const cardStyle = {
    marginBottom: 16,
    background: '#fff',
    borderRadius: 16,
    border: '1px solid #e7e2dc',
    boxShadow: '0 2px 8px rgba(24,12,3,0.07)',
    padding: '10px 12px',
  };
  return (
    <div className={isDiscussion ? "discussion-card" : "comment-card"} style={cardStyle}>
      {/* Title moved to parent for discussion */}
      <div style={{ fontWeight: 600, color: '#7f6b5b', marginBottom: 2 }}>
        <span style={{ fontWeight: 400, color: '#a08b7b', fontSize: '0.92rem' }}>
          Posted by {post.authorUsername || 'Anonymous'} on {post.createdAt ? new Date(post.createdAt).toLocaleString() : ''}
        </span>
      </div>
      <div style={{ marginBottom: 8 }}>{highlightText(post.text, searchTerm)}</div>
      <div style={{ display: 'flex', gap: 8, marginTop: 6 }}>
        <button
          className="discussion-action-btn"
          style={{ fontSize: '0.95rem' }}
          onClick={() => {
            if (!isLoggedIn) {
              replyState.setShowLoginModal(true);
              return;
            }
            replyState.setReplyingTo(post.id);
            replyState.setReplyText("");
            replyState.setReplyError("");
          }}
        >
          Reply
        </button>
        {isOwn && (
          <button
            className="discussion-action-btn"
            type="button"
            style={{ fontSize: '0.95rem', borderColor: '#7b4b27', color: canEdit ? '#7b4b27' : '#a08b7b', background: canEdit ? undefined : '#f7f7f7', cursor: canEdit ? 'pointer' : 'not-allowed' }}
            disabled={!canEdit}
            title={!canEdit ? 'cant edit post with replies' : undefined}
            onClick={() => { if (canEdit) onEdit(post); }}
          >
            Edit
          </button>
        )}
        {!isOwn && (
          <>
            <button
              className="discussion-action-btn"
              type="button"
              style={{ borderColor: '#b94a48', color: '#b94a48', fontSize: '0.75rem' }}
              onClick={() => onReport(post)}
            >
              Report
            </button>
            <button
              className="discussion-action-btn"
              type="button"
              style={{ fontSize: '0.75rem' }}
              onClick={() => {
                if (upvoteState[post.id]) {
                  onRemoveUpvote(post);
                } else {
                  onUpvote(post);
                }
              }}
            >
              {upvoteState[post.id] ? 'Undo' : 'Upvote'}
            </button>
          </>
        )}
        <span className={isDiscussion ? "discussion-upvotes" : "reply-upvotes"}>{post.upvotes || 0} upvotes</span>
      </div>
    </div>
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
  const [replyingTo, setReplyingTo] = useState(null);
  const [replyText, setReplyText] = useState("");
  const [replyError, setReplyError] = useState("");
  const [replyLoading, setReplyLoading] = useState(false);
  const [isUpvoted, setIsUpvoted] = useState(hasUpvoted);
  const [localUpvoteCount, setLocalUpvoteCount] = useState(upvoteCount);
  const [commentUpvoteStates, setCommentUpvoteStates] = useState(() => {
    const map = {};
    (comments || []).forEach(c => { map[c.id] = c.hasUpvoted; });
    return map;
  });
  const [showLoginModal, setShowLoginModal] = useState(false);
  const [isLoggedIn, setIsLoggedIn] = useState(false);
  const [currentUser, setCurrentUser] = useState(null);
  useEffect(() => {
    getCurrentUser()
      .then(user => {
        setIsLoggedIn(true);
        setCurrentUser(user);
      })
      .catch(() => {
        setIsLoggedIn(false);
        setCurrentUser(null);
      });
    const handleAuthLogin = () => setIsLoggedIn(true);
    const handleAuthLogout = () => setIsLoggedIn(false);
    window.addEventListener("auth:login", handleAuthLogin);
    window.addEventListener("auth:logout", handleAuthLogout);
    return () => {
      window.removeEventListener("auth:login", handleAuthLogin);
      window.removeEventListener("auth:logout", handleAuthLogout);
    };
  }, []);

  const refreshComments = async () => {
    const discussions = await getDiscussions();
    const discussion = (Array.isArray(discussions) ? discussions : discussions.content || []).find(d => d.id === id);
    setLocalComments(discussion?.comments || []);
    const map = {};
    (discussion?.comments || []).forEach(c => { map[c.id] = c.hasUpvoted; });
    setCommentUpvoteStates(map);
    if (typeof discussion?.hasUpvoted === 'boolean') setIsUpvoted(discussion.hasUpvoted);
  };

  const handleReport = (post) => {
    if (!isLoggedIn) {
      setShowLoginModal(true);
      return;
    }
    if (post.title) {
      setReportTarget({ type: 'discussion', id, subtitle: authorUsername ? `Posted by ${authorUsername}` : '' });
      setReportModalOpen(true);
    } else {
      setReportTarget({ type: 'comment', id: post.id, subtitle: post.authorUsername ? `Posted by ${post.authorUsername}` : '' });
      setReportModalOpen(true);
    }
  };

  const handleUpvote = (post) => {
    if (!isLoggedIn) {
      setShowLoginModal(true);
      return;
    }
    if (post.title) {
      upvoteDiscussion(id).then(success => {
        if (success) {
          setIsUpvoted(true);
          setLocalUpvoteCount(count => count + 1);
          refreshComments();
        } else {
          alert("Failed to upvote discussion.");
        }
      });
    } else {
      upvoteComment(post.id).then(success => {
        if (success) {
          setCommentUpvoteStates(states => ({ ...states, [post.id]: true }));
          refreshComments();
        } else {
          alert("Failed to upvote comment.");
        }
      });
    }
  };

  const handleRemoveUpvote = (post) => {
    if (post.title) {
      removeUpvoteDiscussion(id).then(success => {
        if (success) {
          setIsUpvoted(false);
          setLocalUpvoteCount(count => Math.max(0, count - 1));
          refreshComments();
        } else {
          alert("Failed to remove upvote.");
        }
      });
    } else {
      removeUpvoteComment(post.id).then(success => {
        if (success) {
          setCommentUpvoteStates(states => ({ ...states, [post.id]: false }));
          refreshComments();
        } else {
          alert("Failed to remove upvote from comment.");
        }
      });
    }
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

  function renderComment(comment, depth = 0) {
    const indent = depth <= 6 ? '25px' : '0px';
    return (
      <li key={comment.id} style={{ marginLeft: indent }}>
        <PostCard
          post={comment}
          isDiscussion={false}
          currentUser={currentUser}
          isLoggedIn={isLoggedIn}
          onReport={handleReport}
          onUpvote={handleUpvote}
          onRemoveUpvote={handleRemoveUpvote}
          onReply={() => {
            setReplyingTo(comment.id);
            setReplyText("");
            setReplyError("");
          }}
          onEdit={() => {/* TODO: open edit modal for comment */}}
          upvoteState={commentUpvoteStates}
          replyState={{ setShowLoginModal, setReplyingTo, setReplyText, setReplyError }}
          searchTerm={searchTerm}
        />
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
      <div className="discussions-container" style={{
        background: '#fff',
        borderRadius: 16,
        border: '1px solid #e7e2dc',
        boxShadow: '0 2px 8px rgba(24,12,3,0.07)',
        padding: '18px 18px 8px 18px',
        marginBottom: 24
      }}>
        <h2 className="discussion-card__title" style={{marginTop: 0, marginBottom: 12}}>{highlightText(title, searchTerm)}</h2>
        <PostCard
          post={{ id, createdAt, title, text, authorUsername, upvotes: localUpvoteCount, commentCount }}
          isDiscussion={true}
          currentUser={currentUser}
          isLoggedIn={isLoggedIn}
          onReport={handleReport}
          onUpvote={handleUpvote}
          onRemoveUpvote={handleRemoveUpvote}
          onReply={() => setShowLeaveComment(v => !v)}
          onEdit={() => {/* TODO: open edit modal for discussion */}}
          upvoteState={{ [id]: isUpvoted }}
          replyState={{ setShowLoginModal, setReplyingTo, setReplyText, setReplyError }}
          searchTerm={searchTerm}
        />
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
        <ul style={{ listStyle: 'none', padding: 0, margin: '18px 0 0 0' }}>
          {localComments.length > 0 && buildCommentTree(localComments).map(c => renderComment(c))}
        </ul>
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
