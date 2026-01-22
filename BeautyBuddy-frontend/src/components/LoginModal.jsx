import { useEffect, useState } from "react";
import "./LoginModal.css";
import { loginUser } from "../api/authApi";

export default function LoginModal({ onClose, onSwitchToSignup, onSwitchToResetPass }) {
  const [email, setEmail] = useState("");
  const [password, setPassword] = useState("");
  const [error, setError] = useState("");
  const [loading, setLoading] = useState(false);
  
  useEffect(() => {
    const handleEsc = (e) => {
      if (e.key === "Escape") onClose();
    };

    window.addEventListener("keydown", handleEsc);
    return () => {
      window.removeEventListener("keydown", handleEsc);
    };
  }, [onClose]);

  const handleLogin = async (e) => {
    e.preventDefault();
    setLoading(true);
    setError("");

    try {
      const result = await loginUser(email, password);
      if (result.error) {
        setError(result.error);
      } else {
        localStorage.setItem("user", email);
        onClose();
      }
    } catch (err) {
      setError("An unexpected error occurred. Please try again.");
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="modal-overlay">
        <div className="modal" onClick={(e) => e.stopPropagation()}>
        <h2>Login</h2>

        {error && <p style={{ color: "red" }}>{error}</p>}

        <form onSubmit={handleLogin} className="modal-form">
          <input
            className="modal-input"
            type="email"
            placeholder="Email"
            value={email}
            onChange={(e) => setEmail(e.target.value)}
            required
          />
          <input
            className="modal-input"
            type="password"
            placeholder="Password"
            value={password}
            onChange={(e) => setPassword(e.target.value)}
            required
          />

          <button type="submit" className="modal-login-button" disabled={loading}>
            {loading ? "Logging in..." : "Log In"}
          </button>
        </form>

        <button className='modal-forgot-password-link' onClick={onSwitchToResetPass}>Forgot Password?</button>

        <div className='modal-signup'>
            <h2 className='modal-signup-prompt'>Don't have an account?</h2>
            <button className="modal-signup-link" onClick={onSwitchToSignup}>Sign Up</button>
        </div>

        <button
            className="modal-close"
            onClick={() => onClose()}
        >
            ✕
        </button>
        </div>
    </div>
  );
}