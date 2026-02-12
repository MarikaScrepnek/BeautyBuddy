import { useEffect, useState } from "react";

export default function AskQuestionModal({
  isOpen,
  onClose,
  onSubmit,
  productName,
}) {
  const [title, setTitle] = useState("");
  const [body, setBody] = useState("");
  const [error, setError] = useState("");

  useEffect(() => {
    if (!isOpen) return;

    // reset when opened
    setTitle("");
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
    const trimmedTitle = title.trim();
    const trimmedBody = body.trim();

    if (trimmedTitle.length < 5) {
      setError("Title must be at least 5 characters.");
      return;
    }
    if (trimmedBody.length < 10) {
      setError("Details must be at least 10 characters.");
      return;
    }

    setError("");
    await onSubmit?.({
      title: trimmedTitle,
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
        aria-label="Leave a review"
        onClick={stop}
      >
        <div className="modal-header">
          <h2 className="modal-title">Submit a review</h2>
          {productName ? (
            <p className="modal-subtitle">On: {productName}</p>
          ) : null}
          <button className="modal-close" onClick={onClose} aria-label="Close">
            ✕
          </button>
        </div>

        <form className="modal-form" onSubmit={handleSubmit}>
          <label className="modal-label">
            Title
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
            Details
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
              Post question
            </button>
          </div>
        </form>
      </div>
    </div>
  );
}
