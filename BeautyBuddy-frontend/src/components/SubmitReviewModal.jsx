import { useEffect, useMemo, useRef, useState } from "react";

import "./SubmitReviewModal.css"

export default function AskQuestionModal({
  isOpen,
  onClose,
  onSubmit,
  onDelete,
  onToast,
  productName,
  shades,
  initialValues,
  existingReviewsByShade,
  selectedShadeName,
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
  const [isShadeOpen, setIsShadeOpen] = useState(false);
  const shadeMenuRef = useRef(null);

  const [error, setError] = useState("");

  const reviewKey = shadeName || "";
  const existingReview = existingReviewsByShade?.[reviewKey] ?? null;
  const existingReviewId = existingReview?.reviewId ?? existingReview?.id ?? null;

  useEffect(() => {
    if (!isOpen) return;

    const nextShade = initialValues?.shadeName ?? selectedShadeName ?? "";
    const reviewKey = nextShade || "";
    const existingReview = existingReviewsByShade?.[reviewKey] ?? null;
    const nextRating = existingReview?.rating ?? initialValues?.rating ?? 0;

    // reset when opened
    setShadeName(nextShade);
    setRating(nextRating);
    setHoverRating(null);
    setTitle(existingReview?.reviewTitle ?? existingReview?.title ?? initialValues?.title ?? "");
    setBody(existingReview?.reviewText ?? existingReview?.text ?? initialValues?.text ?? "");
    setImages(existingReview?.imageLinks ?? initialValues?.images ?? []);

    setError("");

    const onKeyDown = (e) => {
      if (e.key === "Escape") onClose?.();
    };
    window.addEventListener("keydown", onKeyDown);
    return () => window.removeEventListener("keydown", onKeyDown);
  }, [isOpen, onClose, initialValues, selectedShadeName, existingReviewsByShade]);

  useEffect(() => {
    if (!isOpen) return;

    const handleClickOutside = (event) => {
      if (!shadeMenuRef.current) return;
      if (shadeMenuRef.current.contains(event.target)) return;
      setIsShadeOpen(false);
    };

    document.addEventListener("mousedown", handleClickOutside);
    return () => document.removeEventListener("mousedown", handleClickOutside);
  }, [isOpen]);

  useEffect(() => {
    if (!isOpen) return;
    const reviewKey = shadeName || "";
    const existingReview = existingReviewsByShade?.[reviewKey] ?? null;
    if (existingReview) {
      setRating(existingReview?.rating ?? 0);
      setTitle(existingReview?.reviewTitle ?? existingReview?.title ?? "");
      setBody(existingReview?.reviewText ?? existingReview?.text ?? "");
      setImages(existingReview?.imageLinks ?? []);
    } else {
      setTitle("");
      setBody("");
      setImages([]);
    }
    setHoverRating(null);
  }, [shadeName, existingReviewsByShade, isOpen]);

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
      images: images,
      existingReviewId,
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
          <h2 className="modal-title">
            {existingReviewId ? "Edit your review" : modalTitle}
          </h2>
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
              <div
                className={`modal-shade-dropdown ${isShadeOpen ? "is-open" : ""}`}
                ref={shadeMenuRef}
              >
                <button
                  type="button"
                  className="modal-shade-trigger"
                  aria-haspopup="listbox"
                  aria-expanded={isShadeOpen}
                  onClick={() => setIsShadeOpen((open) => !open)}
                >
                  <span
                    className="modal-shade-swatch"
                    style={{ backgroundColor: shades.find((s) => s.shadeName === shadeName)?.hexCode ?? "#eee" }}
                    aria-hidden="true"
                  />
                  <span className="modal-shade-label-text">
                    {shadeName || "Select shade"}
                  </span>
                  <span className="modal-shade-caret">▼</span>
                </button>
                {isShadeOpen && (
                  <div className="modal-shade-menu" role="listbox" aria-label="Shades">
                    {shades.map((shade) => (
                      <button
                        key={shade.shadeName}
                        type="button"
                        className="modal-shade-option"
                        role="option"
                        aria-selected={shade.shadeName === shadeName}
                        onClick={() => {
                          const nextShade = shade.shadeName;
                          const reviewKey = nextShade || "";
                          const existingReview = existingReviewsByShade?.[reviewKey] ?? null;
                          if (existingReview) {
                            onToast?.("Fetching your review", "info");
                          }
                          setShadeName(nextShade);
                          setIsShadeOpen(false);
                        }}
                      >
                        <span
                          className="modal-shade-swatch"
                          style={{ backgroundColor: shade?.hexCode ?? "#eee" }}
                          aria-hidden="true"
                        />
                        <span className="modal-shade-option-text">{shade.shadeName}</span>
                      </button>
                    ))}
                  </div>
                )}
              </div>
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
            <div className="modal-actions-right">
              {existingReviewId ? (
                <button
                  type="button"
                  className="modal-secondary modal-danger"
                  onClick={() => onDelete?.(existingReviewId)}
                >
                  Delete review
                </button>
              ) : null}
              <button type="submit" className="modal-primary">
                {existingReviewId ? "Save changes" : submitLabel}
              </button>
            </div>
          </div>
        </form>
      </div>
    </div>
  );
}
