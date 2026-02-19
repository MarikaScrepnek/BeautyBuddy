import { useEffect, useState } from "react";
import { useMemo } from "react";

import "./SubmitReviewModal.css"

export default function AskQuestionModal({
  isOpen,
  onClose,
  onSubmit,
  productName,
  shades,
  initialValues,
  modalTitle = "Submit a review",
  submitLabel = "Submit review",
}) {
  const [shadeName, setShadeName] = useState("");
  const [rating, setRating] = useState(0);
  const [hoverRating, setHoverRating] = useState(null);
  const displayedRating = hoverRating ?? rating;
  const [title, setTitle] = useState("");
  const [body, setBody] = useState("");
  const [images, setImages] = useState([]);

  const [error, setError] = useState("");

  useEffect(() => {
    if (!isOpen) return;

    const nextShade = initialValues?.shadeName ?? "";
    const nextRating = initialValues?.rating ?? 0;

    // reset when opened
    setShadeName(nextShade);
    setRating(nextRating);
    setHoverRating(null);
    setTitle(initialValues?.title ?? "");
    setBody(initialValues?.text ?? "");
    setImages(initialValues?.images ?? []);

    setError("");

    const onKeyDown = (e) => {
      if (e.key === "Escape") onClose?.();
    };
    window.addEventListener("keydown", onKeyDown);
    return () => window.removeEventListener("keydown", onKeyDown);
  }, [isOpen, onClose, initialValues, ""]);

  if (!isOpen) return null;

  const handleSubmit = async (e) => {
    e.preventDefault();
    const trimmedTitle = title.trim();
    const trimmedBody = body.trim();

    if (rating <= 0) {
      setError("Please select a rating.");
      return;
    }

    setError("");
    await onSubmit?.({
      shadeName: shadeName || null,
      rating: rating,
      title: trimmedTitle ? trimmedTitle : null,
      text: trimmedBody ? trimmedBody : null,
      images: images
    });
  };

  const stop = (e) => e.stopPropagation();

    const getStarValueFromEvent = (e, starIndex1to5) => {
    const rect = e.currentTarget.getBoundingClientRect();
    const x = e.clientX - rect.left;
    const isHalf = x < rect.width / 2;
    return isHalf ? starIndex1to5 - 0.5 : starIndex1to5;
  };

  const Star = ({ index }) => {
  const fillPct = useMemo(() => {
    const v = displayedRating - (index - 1);
    if (v >= 1) return 100;
    if (v >= 0.5) return 50;
    return 0;
  }, [displayedRating, index]);

    return (
      <button
      type="button"
      className="star-btn"
      aria-label={`Rate ${index} star${index > 1 ? "s" : ""}`}
      onMouseMove={(e) => setHoverRating(getStarValueFromEvent(e, index))}
      onMouseEnter={(e) => setHoverRating(getStarValueFromEvent(e, index))}
      onMouseLeave={() => setHoverRating(null)}
      onClick={(e) => setRating(getStarValueFromEvent(e, index))}
    >
      <span className="star-svg" style={{ "--fill": `${fillPct}%` }}>
        <svg viewBox="0 0 24 24" aria-hidden="true">
          {/* outline */}
          <path
            className="star-outline"
            d="M12 17.27L18.18 21l-1.64-7.03L22 9.24l-7.19-.61L12 2 9.19 8.63 2 9.24l5.46 4.73L5.82 21z"
          />
          {/* filled (clipped) */}
          <g className="star-fill">
            <path d="M12 17.27L18.18 21l-1.64-7.03L22 9.24l-7.19-.61L12 2 9.19 8.63 2 9.24l5.46 4.73L5.82 21z" />
          </g>
        </svg>
      </span>
    </button>
  );
};

  return (
    <div className="modal-overlay" onClick={onClose} role="presentation">
      <div
        className="modal-card"
        role="dialog"
        aria-modal="true"
        aria-label="Leave a review"
        onClick={stop}
      >
        <div className="modal-header">
          <h2 className="modal-title">{modalTitle}</h2>
          {productName ? (
            <p className="modal-subtitle">On: {productName}</p>
          ) : null}
          <button className="modal-close" onClick={onClose} aria-label="Close">
            ✕
          </button>
        </div>

        <form className="modal-form" onSubmit={handleSubmit}>

          <div className="rating-row">
            <span className="rating-label">Rating</span>
            <div className="stars" role="radiogroup" aria-label="Rating">
              {[1, 2, 3, 4, 5].map((i) => (
                <Star key={i} index={i} />
              ))}
            </div>
            <span className="rating-value">{displayedRating ? displayedRating.toFixed(1) : ""}</span>

            {rating > 0 && (
              <button
                type="button"
                className="rating-clear"
                onClick={() => setRating(0)}
              >
                Clear
              </button>
            )}
          </div>

          {Array.isArray(shades) && shades.length > 0 ? (
            <div className="shade-row">
              <label className="shade-label" htmlFor="shade-input">
                Shade (optional) - you may submit one review per shade
              </label>
              <select
                id="shade-input"
                className="shade-select"
                value={shadeName}
                onChange={(e) => setShadeName(e.target.value)}
              >
                <option value="">No shade</option>
                {shades.map((shade) => (
                  <option key={shade.shadeName} value={shade.shadeName}>
                    {shade.shadeName}
                  </option>
                ))}
              </select>
            </div>
          ) : null}
          
          <label className="modal-label">
            Title (optional)
            <input
              className="modal-input"
              value={title}
              onChange={(e) => setTitle(e.target.value)}
              placeholder="Summarize your experience in a few words..."
              maxLength={120}
              autoFocus
            />
          </label>

          <label className="modal-label">
            Details (optional)
            <textarea
              className="modal-textarea"
              value={body}
              onChange={(e) => setBody(e.target.value)}
              placeholder="Add more context (skin type, concerns, shade, etc.)"
              rows={5}
              maxLength={800}
            />
          </label>

          {error ? <p className="modal-error">{error}</p> : null}

          <div className="modal-actions">
            <button type="button" className="modal-secondary" onClick={onClose}>
              Cancel
            </button>
            <button type="submit" className="modal-primary">
              {submitLabel}
            </button>
          </div>
        </form>
      </div>
    </div>
  );
}
