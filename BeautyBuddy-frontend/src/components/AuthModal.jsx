import { useState } from "react";
import LoginModal from "./LoginModal";
import SignupModal from "./SignupModal";

export default function AuthModal({ onClose }) {
  const [mode, setMode] = useState("login");

  return mode === "login" ? (
    <LoginModal
      onClose={onClose}
      onSwitchToSignup={() => setMode("signup")}
    />
  ) : (
    <SignupModal
      onClose={onClose}
      onSwitchToLogin={() => setMode("login")}
    />
  );
}
