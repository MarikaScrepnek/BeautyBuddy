import "./ReportReviewModal.css";

import { useEffect, useState } from "react";

export default function ReportReviewModal({
  isOpen,
  onClose,
  onSubmit,
  reviewerName,
}) {
  const [reason, setReason] = useState("");
  const [error, setError] = useState("");

  useEffect(() => {
    if (!isOpen) return;

    setReason("");
    setError("");

    const onKeyDown = (e) => {
      if (e.key === "Escape") onClose?.();
    };
    window.addEventListener("keydown", onKeyDown);
    return () => window.removeEventListener("keydown", onKeyDown);
  }, [isOpen, onClose]);

  if (!isOpen) return null;

  const handleSubmit = async (e) => {
    e.preventDefault();
    const trimmedReason = reason.trim();

    if (trimmedReason.length < 5) {
      setError("Please add a brief reason (at least 5 characters).");
      return;
    }

    setError("");
    await onSubmit?.({ reason: trimmedReason });
  };

  const stop = (e) => e.stopPropagation();

  return (
    <div className="modal-overlay" onClick={onClose} role="presentation">
      <div
        className="modal-card"
        role="dialog"
        aria-modal="true"
        aria-label="Report review"
        onClick={stop}
      >
        <div className="modal-header">
          <h2 className="modal-title">Report review</h2>
          {reviewerName ? (
            <p className="modal-subtitle">By: {reviewerName}</p>
          ) : null}
          <button className="modal-close" onClick={onClose} aria-label="Close">
            ✕
          </button>
        </div>

        <form className="modal-form" onSubmit={handleSubmit}>
          <label className="modal-label">
            <textarea
              className="modal-textarea"
              value={reason}
              onChange={(e) => setReason(e.target.value)}
              placeholder="Tell us why you are reporting this review..."
              rows={5}
              maxLength={500}
            />
          </label>

          {error ? <p className="modal-error">{error}</p> : null}

          <div className="modal-actions">
            <button type="button" className="modal-secondary" onClick={onClose}>
              Cancel
            </button>
            <button type="submit" className="modal-primary">
              Submit report
            </button>
          </div>
        </form>
      </div>
    </div>
  );
}
