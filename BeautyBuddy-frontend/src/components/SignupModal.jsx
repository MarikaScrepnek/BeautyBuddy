import { useEffect } from "react";
import "./SignupModal.css";

import { useState } from "react";
import { registerUser } from "../api/authApi";
import { FaEye, FaEyeSlash } from "react-icons/fa";

export default function SignupModal({ onClose, onSwitchToLogin}) {
  const[email, setEmail] = useState("");
  const[username, setUsername] = useState("");
  const[password, setPassword] = useState("");
  const[showPassword, setShowPassword] = useState(false);
  const[error, setError] = useState("");
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

  const handleSignup = async (e) => {
    e.preventDefault();
    setLoading(true);
    setError("");

    try {
      const result = await registerUser(email, username, password);
      if (result.error) {
        setError(result.error);
      } else {
        onSwitchToLogin();
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
        <h2>Sign Up</h2>

        <form onSubmit={handleSignup}>
          <input
            type="email"
            placeholder="Email"
            value={email}
            onChange={(e) => setEmail(e.target.value)}
            required
          />
          <input
            type="text"
            placeholder="Username"
            value={username}
            onChange={(e) => setUsername(e.target.value)}
            required
          />

          <div className="password-input-container">
            <input
              type={showPassword ? "text" : "password"}
              placeholder="Password"
              value={password}
              onChange={(e) => setPassword(e.target.value)}
              required
            />
            <button
                type="button"
                className="toggle-password-button"
                onClick={() => setShowPassword(!showPassword)}
              >
                {showPassword ? <FaEyeSlash /> : <FaEye />}
            </button>
          </div>

          <button type="submit" className="modal-signup-button" disabled={loading}>
            {loading ? "Signing up..." : "Sign Up"}
          </button>
        </form>

        <div className="modal-login">
            <span className="modal-login-prompt">Already have an account?</span>
            <button
                className="modal-login-link"
                onClick={() => {
                    onSwitchToLogin();
                }}
            >
                Log In
            </button>
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