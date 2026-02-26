import "./Tooltip.css";

export default function Tooltip({ message, children, position = "top" }) {
  return (
    <span className="custom-tooltip-wrapper">
      {children}
      <span className={`custom-tooltip custom-tooltip--${position}`}>{message}</span>
    </span>
  );
}