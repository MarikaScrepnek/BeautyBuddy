
import "./ReviewCard.css";

function highlightText(text, term) {
  if (!term || !text) return text;
  const regex = new RegExp(`(${term.replace(/[.*+?^${}()|[\]\\]/g, '\\$&')})`, "gi");
  return text.split(regex).map((part, i) =>
    regex.test(part) ? <span key={i} style={{ background: "yellow" }}>{part}</span> : part
  );
}

const getRatingNumber = (rating) => {
  const num = Number(rating);
  return Number.isFinite(num) ? num : 0;
};

const StarDisplay = ({ rating }) => {
  const safeRating = getRatingNumber(rating);
  return (
    <div className="review-stars" aria-label={`Rating ${safeRating} out of 5`}>
      {[1, 2, 3, 4, 5].map((index) => {
        const fillPct = Math.min(
          100,
          Math.max(0, (safeRating - (index - 1)) * 100)
        );

        return (
          <span
            key={index}
            className="review-star"
            style={{ "--fill": `${fillPct}%` }}
            aria-hidden="true"
          >
            <svg viewBox="0 0 24 24">
              <path
                className="review-star-outline"
                d="M12 17.27L18.18 21l-1.64-7.03L22 9.24l-7.19-.61L12 2 9.19 8.63 2 9.24l5.46 4.73L5.82 21z"
              />
              <g className="review-star-fill">
                <path d="M12 17.27L18.18 21l-1.64-7.03L22 9.24l-7.19-.61L12 2 9.19 8.63 2 9.24l5.46 4.73L5.82 21z" />
              </g>
            </svg>
          </span>
        );
      })}
      <span className="review-rating-value">{safeRating.toFixed(1)}</span>
    </div>
  );
};

export default function ReviewCard({
  review,
  reviewId,
  reviewerName,
  reviewTitle,
  reviewText,
  reviewDate,
  isOwner,
  avatar,
  isPending,
  isUpvoted,
  upvoteCount,
  onEdit,
  onDelete,
  onUpvote,
  onReport,
  isLoggedIn = true,
  onRequireLogin,
  searchTerm,
}) {
  return (
    <article key={reviewId ?? `${reviewerName}-${reviewDate}`} className="review-card">

      <header className="review-header">
        <div className="reviewer">
          <div className="review-avatar">
            {avatar ? (
              <img src={avatar} alt={reviewerName} />
            ) : (
              <span>{reviewerName ? reviewerName.slice(0, 1).toUpperCase() : "?"}</span>
            )}
          </div>
          <div>
            <div className="reviewer-name">{highlightText(reviewerName, searchTerm)}</div>
            {review?.shadeName ? (
              <div className="review-shade">Shade: {review.shadeName}</div>
            ) : null}
          </div>
        </div>
        <div className="review-meta">
          <StarDisplay rating={review?.rating} />
          {reviewDate ? <span className="review-date">{reviewDate}</span> : null}
        </div>
      </header>

      {reviewTitle ? <h3 className="review-title">{highlightText(reviewTitle, searchTerm)}</h3> : null}
      {reviewText ? <p className="review-text">{highlightText(reviewText, searchTerm)}</p> : null}

      <div className="review-actions">
        {isOwner ? (
          <div className="review-actions-left">
            <button
              type="button"
              className="review-action-btn"
              onClick={() => onEdit?.(review)}
              disabled={isPending}
            >
              Edit
            </button>
            <button
              type="button"
              className="review-action-btn review-action-danger"
              onClick={() => onDelete?.(review)}
              disabled={isPending}
            >
              Delete
            </button>
          </div>
        ) : (
          <>
            <div className="review-actions-left">
              <button
                type="button"
                className="review-action-btn"
                onClick={() => {
                  if (!isLoggedIn) {
                    onRequireLogin?.();
                    return;
                  }
                  onUpvote?.(review);
                }}
                disabled={isPending}
              >
                {isUpvoted ? "Undo" : "Helpful"}
              </button>
              <span className="review-upvote-count">
                {upvoteCount} helpful vote{upvoteCount !== 1 ? "s" : ""}
              </span>
            </div>
            <button
              type="button"
              className="review-action-btn review-action-muted"
              onClick={() => {
                if (!isLoggedIn) {
                  onRequireLogin?.();
                  return;
                }
                onReport?.(review);
              }}
              disabled={isPending}
            >
              Report
            </button>
          </>
        )}
      </div>
    </article>
  );
}
