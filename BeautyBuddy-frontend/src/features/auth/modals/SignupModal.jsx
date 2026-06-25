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

          <input type="password" placeholder="Confirm Password" required />

          <select type="pronouns" placeholder="Pronouns (optional)">
            <option value="">Select Pronouns</option>
            <option value="she/her">She/Her</option>
            <option value="he/him">He/Him</option>
            <option value="they/them">They/Them</option>
            <option value="other">Other</option>
          </select>

          <datetime-local type="birthday" placeholder="Birthday (optional)" />

          <select type="country" placeholder="Country (optional)">
            <option value="">Select Country</option>
            <option value="USA">United States</option>
            <option value="Canada">Canada</option>
            <option value="UK">United Kingdom</option>
            <option value="Australia">Australia</option>
            <option value="Other">Other</option>
          </select>

          <select type="skintype" placeholder="Skin Type (optional)">
            <option value="">Select Skin Type</option>
            <option value="oily">Oily</option>
            <option value="dry">Dry</option>
            <option value="combination">Combination</option>
            <option value="sensitive">Sensitive</option>
            <option value="normal">Normal</option>
          </select>

          <select type="hairtype" placeholder="Hair Type (optional)">
            <option value="">Select Hair Type</option>
            <option value="straight">Straight</option>
            <option value="wavy">Wavy</option>
            <option value="curly">Curly</option>
            <option value="coily">Coily</option>
          </select>

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