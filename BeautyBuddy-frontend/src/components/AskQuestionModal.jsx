import "./AskQuestionModal.css";

import { useEffect, useState } from "react";

export default function AskQuestionModal({
  isOpen,
  onClose,
  onSubmit,
  productName,
}) {
  const [body, setBody] = useState("");
  const [error, setError] = useState("");

  useEffect(() => {
    if (!isOpen) return;

    // reset when opened
    setBody("");
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
    const trimmedBody = body.trim();

    if (trimmedBody.length < 10) {
      setError("Question must be at least 10 characters.");
      return;
    }

    setError("");
    await onSubmit?.({
      body: trimmedBody,
    });
  };

  const stop = (e) => e.stopPropagation();

  return (
    <div className="modal-overlay" onClick={onClose} role="presentation">
      <div
        className="modal-card"
        role="dialog"
        aria-modal="true"
        aria-label="Ask a question"
        onClick={stop}
      >
        <div className="modal-header">
          <h2 className="modal-title">Ask a question</h2>
          {productName ? (
            <p className="modal-subtitle">About: {productName}</p>
          ) : null}
          <button className="modal-close" onClick={onClose} aria-label="Close">
            ✕
          </button>
        </div>

        <form className="modal-form" onSubmit={handleSubmit}>

          <label className="modal-label">
            <textarea
              className="modal-textarea"
              value={body}
              onChange={(e) => setBody(e.target.value)}
              placeholder="Ask your question here..."
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
              Post question
            </button>
          </div>
        </form>
      </div>
    </div>
  );
}
