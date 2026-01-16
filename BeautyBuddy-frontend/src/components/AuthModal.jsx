import { useState } from "react";
import LoginModal from "./LoginModal";
import SignupModal from "./SignupModal";
import ResetPasswordModal from "./ResetPassModal";

export default function AuthModal({ onClose }) {
  const [mode, setMode] = useState("login");

  return mode === "login" ? (
    <LoginModal
      onClose={onClose}
      onSwitchToSignup={() => setMode("signup")}
      onSwitchToResetPass={() => setMode("reset")}
    />
  ) : mode === "signup" ? (
    <SignupModal
      onClose={onClose}
      onSwitchToLogin={() => setMode("login")}
    />
  ) : (
    <ResetPasswordModal onClose={onClose} />
  );
}
