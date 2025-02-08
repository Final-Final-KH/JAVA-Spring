import React from "react";
import TermsOfService from "./TermsOfService";
import "../css/Modal2.css";

const TermsOfServiceModal = ({ isOpen, onClose, onAgree }) => {
  if (!isOpen) return null;

  return (
    <div className="modal-overlay">
      <div className="modal-container">
        <button className="modal-close" onClick={onClose}>
          X
        </button>
        <h1>회원가입 약관</h1>
        <div className="terms-scroll-box">
          <TermsOfService />
        </div>
        <div className="modal-buttons">
          <button className="agree-button" onClick={onAgree}>
            동의
          </button>
          <button className="cancel-button" onClick={onClose}>
            취소
          </button>
        </div>
      </div>
    </div>
  );
};
export default TermsOfServiceModal;
