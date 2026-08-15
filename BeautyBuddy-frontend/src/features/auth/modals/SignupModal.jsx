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

  const[pronouns, setPronouns] = useState("");
  const[birthMonth, setBirthMonth] = useState("");
  const[birthDay, setBirthDay] = useState("");
  const[birthYear, setBirthYear] = useState("");
  const[country, setCountry] = useState("");
  const[skintype, setSkintype] = useState("");
  const[skincondition, setSkincondition] = useState("");
  const[hairtype, setHairtype] = useState("");
  const[hairdensity, setHairdensity] = useState("");

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
      const birthday = birthMonth && birthDay && birthYear
        ? `${birthYear}-${birthMonth.padStart(2, "0")}-${birthDay.padStart(2, "0")}`
        : "";

      const result = await registerUser(email, username, password, pronouns, birthday, country, skintype, skincondition, hairtype, hairdensity);
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
          <label className="input-label" htmlFor="signup-email">Email</label>
          <input
            id="signup-email"
            className="form-input"
            type="email"
            placeholder="you@example.com"
            value={email}
            onChange={(e) => setEmail(e.target.value)}
            required
          />
          <label className="input-label" htmlFor="signup-username">Username</label>
          <input
            id="signup-username"
            className="form-input"
            type="text"
            placeholder="Username"
            value={username}
            onChange={(e) => setUsername(e.target.value)}
            required
          />

          <label className="input-label" htmlFor="signup-password">Password</label>
          <div className="password-input-container">
            <input
              id="signup-password"
              className="form-input"
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

          <label className="input-label" htmlFor="signup-password-confirm">Confirm Password</label>
          <input id="signup-password-confirm" className="form-input" type="password" placeholder="Confirm Password" required />

          <label className="input-label">Birthday</label>

          <div className="birth-select-row">
              <select className="form-select birth-select" value={birthMonth} onChange={(e) => setBirthMonth(e.target.value)}>
                <option value="">Month</option>
                <option value="01">January</option>
                <option value="02">February</option>
                <option value="03">March</option>
                <option value="04">April</option>
                <option value="05">May</option>
                <option value="06">June</option>
                <option value="07">July</option>
                <option value="08">August</option>
                <option value="09">September</option>
                <option value="10">October</option>
                <option value="11">November</option>
                <option value="12">December</option>
              </select>

              <select className="form-select birth-select" value={birthDay} onChange={(e) => setBirthDay(e.target.value)}>
                <option value="">Day</option>
                {[...Array(31)].map((_, index) => {
                  const day = String(index + 1).padStart(2, "0");
                  return <option key={day} value={day}>{day}</option>;
                })}
              </select>

              <select className="form-select birth-select" value={birthYear} onChange={(e) => setBirthYear(e.target.value)}>
                <option value="">Year</option>
                {Array.from({ length: 100 }, (_, index) => {
                  const year = new Date().getFullYear() - index;
                  return <option key={year} value={year}>{year}</option>;
                })}
              </select>
          </div>

          <label className="input-label" htmlFor="signup-country">Country</label>
          <select
            id="signup-country"
            className="form-select"
            value={country}
            onChange={(e) => setCountry(e.target.value)}
          >
            <option value="">Select Country</option>
            <option value="USA">United States</option>
            <option value="CANADA">Canada</option>
            <option value="UK">United Kingdom</option>
            <option value="AUSTRALIA">Australia</option>
            <option value="OTHER">Other</option>
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