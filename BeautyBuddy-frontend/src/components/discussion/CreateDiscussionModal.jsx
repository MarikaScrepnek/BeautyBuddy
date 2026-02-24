import { useState } from "react";

import { createDiscussion } from "../../api/DiscussionApi";

import "./CreateDiscussionModal.css";

export default function CreateDiscussionModal({ open, onClose, onCreate }) {
  const [title, setTitle] = useState("");
  const [text, setText] = useState("");
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState(null);

  if (!open) return null;

  const handleSubmit = async (e) => {
    e.preventDefault();
    setLoading(true);
    setError(null);
    try {
      const created = await createDiscussion(title, text);
      setTitle("");
      setText("");
      if (onCreate) {
        onCreate(created || { title, text });
      }
      onClose();
    } catch (err) {
      setError("Failed to create discussion");
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="modal-backdrop">
      <div className="modal-content">
        <button className="modal-close" onClick={onClose}>&times;</button>
        <h2>Start a New Discussion</h2>
        <form onSubmit={handleSubmit}>
          <label>
            Title
            <input
              type="text"
              value={title}
              onChange={e => setTitle(e.target.value)}
              required
              maxLength={80}
              placeholder="Enter a discussion title"
            />
          </label>
          <label>
            Body
            <textarea
              value={text}
              onChange={e => setText(e.target.value)}
              required
              rows={5}
              placeholder="What's on your mind?"
            />
          </label>
          {error && <div className="modal-error">{error}</div>}
          <button type="submit" className="modal-submit" disabled={loading}>
            {loading ? "Posting..." : "Create Discussion"}
          </button>
        </form>
      </div>
    </div>
  );
}
