import React, { useState } from "react";
import "../../pages/ProductDetails.css";
import { editReview, submitReview } from "../../api/reviewApi";
import Toast from "../Toast";

export default function ReviewStars({
  productId,
  shadeName,
  rating = 0,
  reviewId = null,
  disabled = false,
  tooltipText = "Submit a rating",
  ariaLabel = "Rate this product",
  onReviewSubmitted,
}) {
  const [hover, setHover] = useState(null);
  const [isSubmitting, setIsSubmitting] = useState(false);
  const [toastMsg, setToastMsg] = useState("");

  const ratingFill = (index) => {
    const displayedRating = hover !== null ? hover : rating;
    const delta = displayedRating - (index - 1);
    const pct = Math.min(1, Math.max(0, delta)) * 100;
    return `${pct}%`;
  };

  const getStarValueFromEvent = (e, starIndex1to5) => {
    const rect = e.currentTarget.getBoundingClientRect();
    const x = e.clientX - rect.left;
    const isHalf = x < rect.width / 2;
    return isHalf ? starIndex1to5 - 0.5 : starIndex1to5;
  };

  async function handleSubmitReview(ratingValue) {
    let result;
    if (reviewId === null) {
      result = await submitReview(productId, shadeName, ratingValue, "", "", [])
        .then((res) => {
          setToastMsg("Review submitted successfully!");
          return res;
        })
        .catch((error) => {
          setToastMsg("Error submitting review");
          return null;
        });
    } else {
      result = await editReview(reviewId, shadeName, ratingValue, null, null, [])
        .then((res) => {
          setToastMsg("Review updated successfully!");
          return res;
        })
        .catch((error) => {
          setToastMsg("Error updating review");
          return null;
        });
    }
    if (onReviewSubmitted && result) {
      onReviewSubmitted(ratingValue);
    }
  }

  return (
    <>
      {toastMsg && (
        <Toast message={toastMsg} type="info" onClose={() => setToastMsg("")} />
      )}
      <div className="rating-stars" role="radiogroup" aria-label={ariaLabel}>
        {[1, 2, 3, 4, 5].map((i) => (
          <button
            key={i}
            type="button"
            className="rating-star-btn"
            aria-label={`Rate ${i} star${i > 1 ? "s" : ""}`}
            style={{ cursor: disabled ? "default" : "pointer" }}
            onMouseMove={(e) => !disabled && setHover(getStarValueFromEvent(e, i))}
            onMouseEnter={(e) => !disabled && setHover(getStarValueFromEvent(e, i))}
            onMouseLeave={() => !disabled && setHover(null)}
            onClick={(e) => {
              if (!disabled) {
                e.stopPropagation();
                setIsSubmitting(true);
                handleSubmitReview(getStarValueFromEvent(e, i));
                setIsSubmitting(false);
              }
            }}
            disabled={disabled || isSubmitting}
          >
            <span className="rating-star" style={{ "--fill": ratingFill(i) }}>
              <svg viewBox="0 0 24 24" aria-hidden="true">
                <path
                  className="rating-star-outline"
                  d="M12 17.27L18.18 21l-1.64-7.03L22 9.24l-7.19-.61L12 2 9.19 8.63 2 9.24l5.46 4.73L5.82 21z"
                />
                <g className="rating-star-fill">
                  <path d="M12 17.27L18.18 21l-1.64-7.03L22 9.24l-7.19-.61L12 2 9.19 8.63 2 9.24l5.46 4.73L5.82 21z" />
                </g>
              </svg>
            </span>
            {!disabled && tooltipText && (
              <span className="tooltip rating-tooltip">{tooltipText}</span>
            )}
          </button>
        ))}
      </div>
    </>
  );
}