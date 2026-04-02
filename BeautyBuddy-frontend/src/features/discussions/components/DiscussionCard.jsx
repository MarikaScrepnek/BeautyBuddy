import { createComment, getDiscussions, upvoteDiscussion, removeUpvoteDiscussion, upvoteComment, removeUpvoteComment, reportDiscussion, reportComment, editComment, editDiscussion } from "../api/discussionApi";
import "./DiscussionCard.css";
import { useState, useEffect } from "react";
import AuthModal from "../../auth/modals/AuthModal";
import { getCurrentUser } from "../../auth/api/authApi";
import ReportModal from "../../report/modals/ReportModal";

function highlightText(text, term) {
  if (!term || !text) return text;
  const regex = new RegExp(`(${term.replace(/[.*+?^${}()|[\]\\]/g, '\$&')})`, 'gi');
  return text.split(regex).map((part, i) =>
    regex.test(part)
      ? <mark key={i} style={{ background: '#ffe066', color: '#7b4b27', padding: '0 2px', borderRadius: '3px' }}>{part}</mark>
      : part
  );
}

const formatDateTime = (value) => {
  if (!value) return "";
  const date = new Date(value);
  if (Number.isNaN(date.getTime())) return "";
  return date.toLocaleString(undefined, {
    year: "numeric",
    month: "short",
    day: "numeric",
    hour: "numeric",
    minute: "2-digit",
  });
};

function PostCard({ post, isDiscussion, currentUser, isLoggedIn, onReport, onUpvote, onRemoveUpvote, onReply, onEdit, upvoteState, replyState, searchTerm }) {
    if (post.isReported) {
      return (
        <>
          <div className={isDiscussion ? "discussion-card" : "comment-card"} style={{
            marginBottom: 16,
            background: isDiscussion ? '#e9e3f5' : '#faf1f4',
            borderRadius: 16,
            border: '1px solid #e7e2dc',
            boxShadow: '0 2px 8px rgba(24,12,3,0.07)',
            padding: '10px 12px',
          }}>
            <div style={{ color: '#b94a48', fontWeight: 600, fontSize: '1.1rem', padding: '12px 0' }}>*You reported this post*</div>
          </div>
          {/* Render replies if any, outside the reported card */}
          {post.replies && post.replies.length > 0 && (
            <ul style={{ listStyle: 'none', padding: 0, margin: '8px 0 0 0' }}>
              {post.replies.map((r, idx) => (
                <li key={r.id || idx}>
                  <PostCard
                    post={r}
                    isDiscussion={false}
                    currentUser={currentUser}
                    isLoggedIn={isLoggedIn}
                    onReport={onReport}
                    onUpvote={onUpvote}
                    onRemoveUpvote={onRemoveUpvote}
                    onEdit={onEdit}
                    upvoteState={upvoteState}
                    replyState={replyState}
                    searchTerm={searchTerm}
                  />
                </li>
              ))}
            </ul>
          )}
        </>
      );
    }
  const isOwn = currentUser && post.authorUsername && currentUser.username === post.authorUsername;
  const canEdit = isDiscussion
    ? isOwn && (!post.replyCount || post.replyCount === 0)
    : isOwn && (!post.replies || post.replies.length === 0);
  // Add card background and border for comments
  // Unified card style for both discussion and comment
  const cardStyle = {
    marginBottom: 16,
    background: isDiscussion ? '#e9e3f5' : '#faf1f4',
    borderRadius: 16,
    border: '1px solid #e7e2dc',
    boxShadow: '0 2px 8px rgba(24,12,3,0.07)',
    padding: '10px 12px',
  };
  const [editMode, setEditMode] = useState(false);
  const [editTitle, setEditTitle] = useState(post.title || "");
  const [editText, setEditText] = useState(post.text || "");
  const [editLoading, setEditLoading] = useState(false);
  const [editError, setEditError] = useState("");

  async function handleEditSubmit(e) {
    e.preventDefault();
    setEditLoading(true);
    setEditError("");
    let success = false;
    if (isDiscussion) {
      success = await editDiscussion(post.id, editTitle, editText);
    } else {
      success = await editComment(post.id, editText);
    }
    setEditLoading(false);
    if (!success) {
      setEditError("Failed to edit. Please try again.");
      return;
    }
    // Update local post state immediately
    if (isDiscussion) {
      post.title = editTitle;
      post.text = editText;
    } else {
      post.text = editText;
    }
    setEditMode(false);
    onEdit && onEdit(post); // trigger parent refresh
  }

  return (
    <div className={isDiscussion ? "discussion-card" : "comment-card"} style={cardStyle}>
      <div style={{ marginBottom: 6, display: 'flex', alignItems: 'center', justifyContent: 'space-between', flexWrap: 'wrap' }}>
        <div style={{ display: 'flex', alignItems: 'center', gap: 12, flex: 1, minWidth: 0 }}>
          <span style={{ fontWeight: 400, color: '#a08b7b', fontSize: '0.92rem', whiteSpace: 'nowrap', overflow: 'hidden', textOverflow: 'ellipsis' }}>
            Posted by {post.authorUsername || 'Anonymous'} on {formatDateTime(post.createdAt)}
          </span>
        </div>
        <div style={{ display: 'flex', alignItems: 'center', gap: 10 }}>
          {!isOwn && !isDiscussion && (
            <>
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
          {!isDiscussion && (
            <span className={isDiscussion ? "discussion-upvotes" : "reply-upvotes"}>
              {post.upvotes || 0} {post.upvotes === 1 ? 'upvote' : 'upvotes'}
            </span>
          )}
        </div>
      </div>
      {editMode ? (
        <form onSubmit={handleEditSubmit} style={{ margin: '8px 0' }}>
          {isDiscussion && (
            <input
              type="text"
              value={editTitle}
              onChange={e => setEditTitle(e.target.value)}
              placeholder="Edit title"
              style={{ width: '100%', borderRadius: 8, border: '1px solid #e7e2dc', padding: 8, fontSize: '1rem', marginBottom: 6 }}
              disabled={editLoading}
            />
          )}
          <textarea
            value={editText}
            onChange={e => setEditText(e.target.value)}
            rows={3}
            maxLength={800}
            placeholder="Edit text"
            style={{ width: '100%', borderRadius: 8, border: '1px solid #e7e2dc', padding: 8, fontSize: '1rem', marginBottom: 6, resize: 'none' }}
            disabled={editLoading}
          />
          {editError && <div style={{ color: '#b94a48', marginBottom: 4 }}>{editError}</div>}
          <button
            type="submit"
            className="discussion-action-btn"
            style={{ background: '#7b4b27', color: '#fff', marginRight: 8 }}
            disabled={editLoading}
          >
            {editLoading ? 'Saving...' : 'Save'}
          </button>
          <button
            type="button"
            className="discussion-action-btn"
            onClick={() => setEditMode(false)}
            disabled={editLoading}
          >
            Cancel
          </button>
        </form>
      ) : (
        <div style={{ display: 'flex', gap: 8 }}>
          <div style={{ flex: 1, minWidth: 0, wordBreak: 'break-word', marginLeft: 8 }}>{highlightText(post.text, searchTerm)}</div>
          {!isDiscussion && (
            <span className="reply-upvotes">
            {post.replyCount || 0} {post.replyCount === 1 ? 'reply' : 'replies'}
          </span>
          )}
        </div>
      )}
      {!editMode && (
        <>
          <div style={{ display: 'flex', gap: 8, marginTop: 6, marginLeft: 0 }}>
            {replyState.replyingTo && replyState.replyingTo.id === post.id && ((isDiscussion && replyState.replyingTo.type === 'discussion') || (!isDiscussion && replyState.replyingTo.type === 'comment')) ? (
              <button
                className="discussion-action-btn"
                style={{ fontSize: '0.75rem' }}
                onClick={() => replyState.setReplyingTo(null)}
                disabled={replyState.replyLoading}
              >
                Cancel
              </button>
            ) : (
              <button
                className="discussion-action-btn"
                style={{ fontSize: '0.75rem' }}
                onClick={() => {
                  if (!isLoggedIn) {
                    replyState.setShowLoginModal(true);
                    return;
                  }
                  replyState.setReplyingTo({
                    type: isDiscussion ? 'discussion' : 'comment',
                    id: post.id
                  });
                  replyState.setReplyText("");
                  replyState.setReplyError("");
                }}
                disabled={replyState.replyLoading}
              >
                Reply
              </button>
            )}
            {isOwn && (
              <button
                className="discussion-action-btn"
                type="button"
                style={{ fontSize: '0.75rem', borderColor: '#7b4b27', color: canEdit ? '#7b4b27' : '#a08b7b', background: canEdit ? undefined : '#f7f7f7', cursor: canEdit ? 'pointer' : 'not-allowed' }}
                disabled={!canEdit}
                title={!canEdit ? 'cant edit post with replies' : undefined}
                onClick={() => { if (canEdit) setEditMode(true); }}
              >
                Edit
              </button>
            )}
            {!isOwn && (
              <button
                className="discussion-action-btn"
                type="button"
                style={{ borderColor: '#b94a48', color: '#b94a48', fontSize: '0.75rem' }}
                onClick={() => onReport(post)}
              >
                Report
              </button>
            )}
          </div>
        </>
      )}
      {/* Render reply form directly below the card being replied to */}
      {replyState.replyingTo &&
        replyState.replyingTo.id === post.id &&
        ((isDiscussion && replyState.replyingTo.type === 'discussion') || (!isDiscussion && replyState.replyingTo.type === 'comment')) && (
        <form
          onSubmit={e => replyState.handleReply(e, {
            text: replyState.replyText,
            setText: replyState.setReplyText,
            setError: replyState.setReplyError,
            setLoading: replyState.setReplyLoading,
            parentId: post.id,
            afterSubmit: () => replyState.setReplyingTo(null)
          })}
          style={{ marginTop: 8 }}
        >
          <textarea
            value={replyState.replyText}
            onChange={e => replyState.setReplyText(e.target.value)}
            rows={2}
            maxLength={800}
            placeholder="Write your reply..."
            style={{ width: '100%', borderRadius: 8, border: '1px solid #e7e2dc', padding: 8, fontSize: '0.75rem', marginBottom: 4, resize: 'none' }}
            disabled={replyState.replyLoading}
          />
          {replyState.replyError && <div style={{ color: '#b94a48', marginBottom: 4 }}>{replyState.replyError}</div>}
          <button
            type="submit"
            className="discussion-action-btn"
            style={{ background: '#7b4b27', color: '#fff', marginTop: 2, fontSize: '0.75rem' }}
            disabled={replyState.replyLoading}
          >
            {replyState.replyLoading ? 'Posting...' : 'Submit reply'}
          </button>
          {/* Cancel button removed from form, handled above */}
        </form>
      )}
    </div>
  );
}

export default function DiscussionCard({ id, createdAt, title, text, authorUsername, upvoteCount, commentCount, comments = [], hasUpvoted, hasReported, searchTerm, sortKey }) {
  const [reportModalOpen, setReportModalOpen] = useState(false);
  const [reportTarget, setReportTarget] = useState(null);
  const [showLeaveComment, setShowLeaveComment] = useState(false);
  const [commentText, setCommentText] = useState("");
  const [commentError, setCommentError] = useState("");
  const [commentLoading, setCommentLoading] = useState(false);
  const [localComments, setLocalComments] = useState(comments);
  // replyingTo: { type: 'discussion'|'comment', id: number } | null
  const [replyingTo, setReplyingTo] = useState(null);
  const [replyText, setReplyText] = useState("");
  const [replyError, setReplyError] = useState("");
  const [replyLoading, setReplyLoading] = useState(false);
  const [isUpvoted, setIsUpvoted] = useState(hasUpvoted);
  const [isReported, setIsReported] = useState(hasReported);
  const [localUpvoteCount, setLocalUpvoteCount] = useState(upvoteCount);
  const [commentUpvoteStates, setCommentUpvoteStates] = useState(() => {
    const map = {};
    (comments || []).forEach(c => { map[c.id] = c.hasUpvoted; });
    return map;
  });
  const [showLoginModal, setShowLoginModal] = useState(false);
  const [isLoggedIn, setIsLoggedIn] = useState(false);
  const [currentUser, setCurrentUser] = useState(null);
  // Add local state for discussion title and text
  const [localTitle, setLocalTitle] = useState(title);
  const [localText, setLocalText] = useState(text);
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
    const discussions = await getDiscussions(0, 10, sortKey);
    const discussion = (Array.isArray(discussions) ? discussions : discussions.content || []).find(d => d.id === id);
    setLocalComments(discussion?.comments || []);
    const map = {};
    (discussion?.comments || []).forEach(c => { map[c.id] = c.hasUpvoted; });
    setCommentUpvoteStates(map);
    if (typeof discussion?.hasUpvoted === 'boolean') setIsUpvoted(discussion.hasUpvoted);
    if (typeof discussion?.hasReported === 'boolean') setIsReported(discussion.hasReported);
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
    // If replying to main discussion, parentId should be null
    const isMainDiscussion = !parentId || (replyingTo && replyingTo.type === 'discussion');
    const success = await createComment(id, isMainDiscussion ? null : parentId, trimmed);
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

  // Filter comment tree recursively by search term
  function filterCommentTree(comments, term) {
    if (!term || !term.trim()) return comments;
    const lowerTerm = term.toLowerCase();
    function matches(comment) {
      return (
        (comment.text && comment.text.toLowerCase().includes(lowerTerm)) ||
        (comment.authorUsername && comment.authorUsername.toLowerCase().includes(lowerTerm))
      );
    }
    function filterRecursive(comment) {
      // Filter replies recursively
      const filteredReplies = (comment.replies || []).map(filterRecursive).filter(Boolean);
      // If this comment matches or any reply matches, keep it
      if (matches(comment) || filteredReplies.length > 0) {
        return { ...comment, replies: filteredReplies };
      }
      return null;
    }
    return comments.map(filterRecursive).filter(Boolean);
  }

  function renderComment(comment, depth = 0) {
    const indent = depth <= 6 ? '25px' : '0px';
    return (
      <li key={comment.id} style={{ marginLeft: indent }}>
        <PostCard
          post={{ ...comment, isReported: comment.hasReported }}
          isDiscussion={false}
          currentUser={currentUser}
          isLoggedIn={isLoggedIn}
          onReport={handleReport}
          onUpvote={handleUpvote}
          onRemoveUpvote={handleRemoveUpvote}
          onEdit={() => {/* TODO: open edit modal for comment */}}
          upvoteState={commentUpvoteStates}
          replyState={{
            setShowLoginModal,
            setReplyingTo,
            setReplyText,
            setReplyError,
            setReplyLoading: () => {}, // dummy function for comments
            replyText,
            replyError,
            replyLoading,
            replyingTo,
            handleReply,
          }}
          searchTerm={searchTerm}
        />
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
            if (success) {
              setIsReported(true); // Immediately mark as reported
            }
          } else if (reportTarget.type === 'comment') {
            success = await reportComment(reportTarget.id, reason);
            if (success) {
              // Remove comment from UI
              setLocalComments(prev => prev.map(c =>
                c.id === reportTarget.id ? { ...c, hasReported: true } : c
              ));
            }
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
        <div style={{ display: 'flex', alignItems: 'center', flexWrap: 'wrap', marginBottom: 12 }}>
          <h2 className="discussion-card__title" style={{margin: 0, textAlign: 'center', width: '100%', textDecoration: 'underline', textDecorationColor: '#d8c8f8', textDecorationThickness: '2px'}}>{highlightText(localTitle, searchTerm)}</h2>
          <div style={{ display: 'flex', alignItems: 'center', gap: 12, marginLeft: 'auto', marginTop: 8 }}>
            <button
              className="discussion-action-btn"
              type="button"
              style={{ fontSize: '0.75rem', padding: '4px 12px' }}
              onClick={() => {
                if (isUpvoted) {
                  handleRemoveUpvote({ id, title: localTitle });
                } else {
                  handleUpvote({ id, title: localTitle });
                }
              }}
            >
              {isUpvoted ? 'Undo' : 'Upvote'}
            </button>
            <span className="discussion-upvotes">
              {localUpvoteCount || 0} {localUpvoteCount === 1 ? 'upvote' : 'upvotes'}
            </span>
            <span className="reply-upvotes">
              {localComments.length || 0} {localComments.length === 1 ? 'reply' : 'replies'}
            </span>
          </div>
        </div>
        {localTitle && localText && (
          <PostCard
            post={{ id, createdAt, title: localTitle, text: localText, authorUsername, upvotes: localUpvoteCount, commentCount, replyCount: localComments.length, isReported: isReported }}
            isDiscussion={true}
            currentUser={currentUser}
            isLoggedIn={isLoggedIn}
            onReport={handleReport}
            onUpvote={handleUpvote}
            onRemoveUpvote={handleRemoveUpvote}
            onEdit={(updatedPost) => {
              // If discussion, update local title/text
              if (updatedPost && updatedPost.title !== undefined && updatedPost.text !== undefined) {
                setLocalTitle(updatedPost.title);
                setLocalText(updatedPost.text);
              }
            }}
            upvoteState={{ [id]: isUpvoted }}
            replyState={{
              setShowLoginModal,
              setReplyingTo,
              setReplyText,
              setReplyError,
              setReplyLoading,
              replyText,
              replyError,
              replyLoading,
              replyingTo,
              handleReply,
            }}
            searchTerm={searchTerm}
          />
        )}
        <ul style={{ listStyle: 'none', padding: 0, margin: '18px 0 0 0' }}>
          {localComments.length > 0 && filterCommentTree(buildCommentTree(localComments), searchTerm).map(c => renderComment(c))}
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
