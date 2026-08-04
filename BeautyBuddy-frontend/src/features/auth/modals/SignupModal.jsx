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

          <select
            className="form-select"
            type="pronouns"
            placeholder="Pronouns (optional)"
            value={pronouns}
            onChange={(e) => setPronouns(e.target.value)}
          >
            <option value="">Select Pronouns</option>
            <option value="SHE_HER">She/Her</option>
            <option value="HE_HIM">He/Him</option>
            <option value="THEY_THEM">They/Them</option>
            <option value="OTHER">Other</option>
          </select>

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

          <select
            className="form-select"
            type="country"
            placeholder="Country (optional)"
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

          <select
            className="form-select"
            type="skintype"
            placeholder="Skin Type (optional)"
            value={skintype}
            onChange={(e) => setSkintype(e.target.value)}
          >
            <option value="">Select Skin Type</option>
            <option value="OILY">Oily</option>
            <option value="DRY">Dry</option>
            <option value="COMBINATION">Combination</option>
            <option value="OTHER">Other</option>
          </select>

          <select
            className="form-select"
            type="skincondition"
            placeholder="Skin Condition (optional)"
            value={skincondition}
            onChange={(e) => setSkincondition(e.target.value)}
          >
            <option value="">Select Skin Condition</option>
            <option value="NORMAL">Normal</option>
            <option value="SENSITIVE">Sensitive</option>
            <option value="ACNE_PRONE">Acne Prone</option>
          </select>

          <select
            className="form-select"
            type="hairtype"
            placeholder="Hair Type (optional)"
            value={hairtype}
            onChange={(e) => setHairtype(e.target.value)}
          >
            <option value="">Select Hair Type</option>
            <option value="STRAIGHT">Straight</option>
            <option value="WAVY">Wavy</option>
            <option value="CURLY">Curly</option>
            <option value="COILY">Coily</option>
            <option value="OTHER">Other</option>
          </select>

          <select
            className="form-select"
            type="hairdensity"
            placeholder="Hair Density (optional)"
            value={hairdensity}
            onChange={(e) => setHairdensity(e.target.value)}
          >
            <option value="">Select Hair Density</option>
            <option value="THIN">Thin</option>
            <option value="MEDIUM">Medium</option>
            <option value="THICK">Thick</option>
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