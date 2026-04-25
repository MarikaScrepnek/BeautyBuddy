import { useState, useEffect } from "react";
import AuthModal from "../../auth/modals/AuthModal";
import { getCurrentUser } from "../../auth/api/authApi";

import "./CreateDiscussionModal.css";

export default function CreateDiscussionModal({ open, onClose, onCreate }) {
  const [title, setTitle] = useState("");
  const [text, setText] = useState("");
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState(null);
  const [showLoginModal, setShowLoginModal] = useState(false);
  const [isLoggedIn, setIsLoggedIn] = useState(false);

  useEffect(() => {
    getCurrentUser()
      .then(() => setIsLoggedIn(true))
      .catch(() => setIsLoggedIn(false));
    const handleAuthLogin = () => setIsLoggedIn(true);
    const handleAuthLogout = () => setIsLoggedIn(false);
    window.addEventListener("auth:login", handleAuthLogin);
    window.addEventListener("auth:logout", handleAuthLogout);
    return () => {
      window.removeEventListener("auth:login", handleAuthLogin);
      window.removeEventListener("auth:logout", handleAuthLogout);
    };
  }, []);

  if (!open) return null;

  const handleSubmit = async (e) => {
    e.preventDefault();
    if (!isLoggedIn) {
      setShowLoginModal(true);
      return;
    }
    setLoading(true);
    setError(null);
    try {
      const success = onCreate ? await onCreate(title, text) : false;
      if (success) {
        setTitle("");
        setText("");
        onClose();
      } else {
        setError("Failed to create discussion");
      }
    } catch (err) {
      setError("Failed to create discussion");
    } finally {
      setLoading(false);
    }
  };

  return (
    <>
      {showLoginModal && (
        <AuthModal
          onClose={() => setShowLoginModal(false)}
          onLoginSuccess={() => {
            setShowLoginModal(false);
            setIsLoggedIn(true);
          }}
        />
      )}
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
    </>
  );
}
