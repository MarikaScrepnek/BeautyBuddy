import { useState } from "react";
import "./CreateDiscussionModal.css";

export default function CreateDiscussionModal({ isOpen, onClose, onSubmit }) {
  const [title, setTitle] = useState("");
  const [body, setBody] = useState("");
  const [error, setError] = useState("");
  const [loading, setLoading] = useState(false);

  if (!isOpen) return null;

  const handleSubmit = async (e) => {
    e.preventDefault();
    if (title.trim().length < 3) {
      setError("Title must be at least 3 characters.");
      return;
    }
    if (body.trim().length < 5) {
      setError("Body must be at least 5 characters.");
      return;
    }
    setError("");
    setLoading(true);
    const success = await onSubmit(title.trim(), body.trim());
    setLoading(false);
    if (success) {
      setTitle("");
      setBody("");
      onClose();
    } else {
      setError("Failed to create discussion. Please try again.");
    }
  };

  return (
    <div className="modal-overlay" onClick={onClose}>
      <div className="modal-card" onClick={e => e.stopPropagation()}>
        <h2>Create Discussion</h2>
        <form onSubmit={handleSubmit}>
          <label>
            Title
            <input
              type="text"
              value={title}
              onChange={e => setTitle(e.target.value)}
              maxLength={120}
              autoFocus
              required
            />
          </label>
          <label>
            Body
            <textarea
              value={body}
              onChange={e => setBody(e.target.value)}
              rows={5}
              maxLength={800}
              required
            />
          </label>
          {error && <div className="modal-error">{error}</div>}
          <div className="modal-actions">
            <button type="button" onClick={onClose} disabled={loading}>Cancel</button>
            <button type="submit" disabled={loading}>{loading ? "Posting..." : "Create"}</button>
          </div>
        </form>
      </div>
    </div>
  );
}
