import React, { useState, useEffect } from "react";
import { useNavigate } from "react-router-dom";
import AxiosInstance from "../axios/AxiosInstanse";
import TermsOfServiceModal from "../component/TermsOfServiceModal";
import "../css/Mordal.css";
import "../css/SignUp.css";
import EmailVerification from "./kedu/course/component/EmailVerification";
import Modal03 from "../component/Modal03";

function SignUp() {
  const navigate = useNavigate();
  const [email, setEmail] = useState("");
  const [password, setPassword] = useState("");
  const [confirmPassword, setConfirmPassword] = useState("");
  const [name, setName] = useState("");
  const [phoneNumber, setPhoneNumber] = useState("");
  const [currentCompany, setCurrentCompany] = useState("");
  const [isAgreed, setIsAgreed] = useState(false);
  const [isEmailValid, setIsEmailValid] = useState(true);
  const [isEmailAvailable, setIsEmailAvailable] = useState(false);
  const [isPasswordValid, setIsPasswordValid] = useState(false);
  const [isPasswordMatch, setIsPasswordMatch] = useState(false);
  const [isVerified, setIsVerified] = useState(false); // 이메일 인증 완료 여부
  const [isNameFilled, setIsNameFilled] = useState(false);
  const [showModal, setShowModal] = useState(false);
  const [isWarningModalOpen, setIsWarningModalOpen] = useState(false);
  const [isPhoneNumberAvailable, setIsPhoneNumberAvailable] = useState(null); // 전화번호 중복 확인 (null = 미확인 상태)

  useEffect(() => {
    // 이메일 인증, 비밀번호 일치, 이름 입력, 이메일 유효성, 약관 동의 체크
    if (
      isVerified &&
      isPasswordMatch &&
      isNameFilled &&
      isEmailValid &&
      isEmailAvailable &&
      isAgreed
    ) {
      document.getElementById("signup-btn").disabled = false;
    } else {
      document.getElementById("signup-btn").disabled = true;
    }
  }, [
    isVerified,
    isPasswordMatch,
    isNameFilled,
    isEmailValid,
    isEmailAvailable,
    isAgreed,
  ]);

  const handleOpenModal = () => {
    setShowModal(true);
  };

  const handleCloseModal = () => {
    setShowModal(false);
  };

  const validateEmail = (email) => {
    const regex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
    return regex.test(email);
  };

  const validatePassword = (password) => {
    const regex = /^(?=.*[A-Za-z])(?=.*\d)(?=.*[!@#$%^&*]).{8,}$/;
    return regex.test(password);
  };

  const handleEmailChange = async (e) => {
    const emailValue = e.target.value;
    setEmail(emailValue);

    if (emailValue && validateEmail(emailValue)) {
      setIsEmailValid(true);
      try {
        const response = await AxiosInstance.post("/auth/check-email", {
          email: emailValue,
        });
        setIsEmailAvailable(response.data.isAvailable);
      } catch (error) {
        setIsEmailAvailable(false);
      }
    } else {
      setIsEmailValid(false);
      setIsEmailAvailable(false);
    }
  };

  const handlePasswordChange = (e) => {
    const passwordValue = e.target.value;
    setPassword(passwordValue);
    setIsPasswordValid(validatePassword(passwordValue));
  };

  const handleConfirmPasswordChange = (e) => {
    const confirmPasswordValue = e.target.value;
    setConfirmPassword(confirmPasswordValue);
    setIsPasswordMatch(password === confirmPasswordValue);
  };

  const handleNameChange = (e) => {
    const nameValue = e.target.value;
    setName(nameValue);
    setIsNameFilled(nameValue.trim().length > 0); // 이름이 비어 있지 않으면 true
  };

  const handlePhoneNumberChange = async (e) => {
    const phoneInput = e.target.value;
    if (/[^0-9-]/.test(phoneInput)) {
      setIsWarningModalOpen(true);
      return;
    }
    setPhoneNumber(phoneInput);

    if (phoneInput.length >= 10) {
      try {
        const response = await AxiosInstance.post("/auth/check-phone", {
          phoneNumber: phoneInput.replace(/-/g, ""),
        });
        setIsPhoneNumberAvailable(response.data.isAvailable);
      } catch (error) {
        setIsPhoneNumberAvailable(false);
      }
    } else {
      setIsPhoneNumberAvailable(null); // 입력이 짧으면 상태 초기화
    }
  };

  const closeWarningModal = () => {
    setIsWarningModalOpen(false);
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    if (!isAgreed) {
      alert("약관에 동의해야 회원가입을 할 수 있습니다.");
      return;
    }

    if (!isEmailValid || !isEmailAvailable) {
      alert("유효하지 않은 이메일입니다.");
      return;
    }

    if (!isPasswordValid) {
      alert("비밀번호 조건을 만족하지 않습니다.");
      return;
    }

    if (!isPasswordMatch) {
      alert("비밀번호가 일치하지 않습니다.");
      return;
    }

    try {
      const sanitizedPhoneNumber = phoneNumber.replace(/-/g, "");
      const response = await AxiosInstance.post("/auth/signup", {
        email,
        password,
        name,
        phoneNumber: sanitizedPhoneNumber,
        currentCompany,
        showCompany: true,
      });
      console.log("회원가입 성공:", response.data);
      navigate("/login");
    } catch (err) {
      console.error("회원가입 실패:", err);
    }
  };

  const handleEmailVerification = () => {
    setIsVerified(true); // 이메일 인증이 완료되면 상태 변경
  };

  return (
    <>
      <div className="signup-container">
        <div className="signup-box">
          <h2>회원가입</h2>
          <form onSubmit={handleSubmit}>
            <div>
              <label>이메일</label>
              <input
                type="email"
                value={email}
                onChange={handleEmailChange}
                required
                placeholder="example@example.com"
              />
              <EmailVerification
                email={email}
                isEmailValid={isEmailValid}
                isEmailAvailable={isEmailAvailable}
                onVerify={handleEmailVerification} // 인증 버튼 클릭 시 이메일 인증 완료 처리
              />
              <br />
              {email && !isEmailValid && (
                <span className="error-message">
                  ❌ 이메일 형식에 맞춰 입력해 주십시오.
                </span>
              )}
              {email && isEmailValid && (
                <span
                  className={`message ${
                    isEmailAvailable ? "success" : "error"
                  }`}
                >
                  {isEmailAvailable
                    ? "✅ 회원가입 가능한 이메일입니다."
                    : "❌ 이미 존재하는 회원의 이메일입니다."}
                </span>
              )}
            </div>
            <div>
              <label>비밀번호</label>
              <input
                type="password"
                value={password}
                onChange={handlePasswordChange}
                required
                placeholder="8자리 이상, 특수문자 포함"
              />
              <br />
              {password && (
                <span
                  className={`message ${isPasswordValid ? "success" : "error"}`}
                >
                  {isPasswordValid
                    ? "✅ 사용 가능한 비밀번호입니다."
                    : "❌ 비밀번호는 8자리 이상 및 특수문자와 숫자를 포함해야 합니다."}
                </span>
              )}
            </div>
            <div>
              <label>비밀번호 확인</label>
              <input
                type="password"
                value={confirmPassword}
                onChange={handleConfirmPasswordChange}
                required
              />
              <br />
              {confirmPassword && (
                <span
                  className={`message ${isPasswordMatch ? "success" : "error"}`}
                >
                  {isPasswordMatch
                    ? "✅ 비밀번호가 일치합니다."
                    : "❌ 비밀번호가 일치하지 않습니다."}
                </span>
              )}
            </div>
            <div>
              <label>이름</label>
              <input
                type="text"
                value={name}
                onChange={handleNameChange}
                required
              />
            </div>
            <div>
              <label>전화번호</label>
              <input
                type="tel"
                value={phoneNumber}
                onChange={handlePhoneNumberChange}
                placeholder="010-1234-5678"
              />
              {phoneNumber && (
                <span
                  className={`message ${
                    isPhoneNumberAvailable ? "success" : "error"
                  }`}
                >
                  {isPhoneNumberAvailable
                    ? "✅ 사용 가능한 전화번호입니다."
                    : "❌ 이미 존재하는 전화번호입니다."}
                </span>
              )}
            </div>
            <div>
              <label>현재 회사</label>
              <input
                type="text"
                value={currentCompany}
                onChange={(e) => setCurrentCompany(e.target.value)}
                placeholder="재직 중인 회사가 없다면 공란으로 비워주세요."
              />
            </div>
            <div className="terms-checkbox">
              <button type="button" onClick={handleOpenModal}>
                <h3>회원가입 약관 (자세히보기)</h3>
              </button>
              <label>
                약관 동의
                <input
                  type="checkbox"
                  checked={isAgreed}
                  onChange={() => setIsAgreed(!isAgreed)}
                />
              </label>
            </div>
            <button id="signup-btn" type="submit" disabled>
              회원가입
            </button>
          </form>
        </div>
      </div>
      <TermsOfServiceModal
        isOpen={showModal}
        onClose={() => setShowModal(false)}
        onAgree={() => {
          setIsAgreed(true);
          setShowModal(false);
        }}
      />
      <Modal03
        open={isWarningModalOpen}
        close={closeWarningModal}
        header="입력 오류"
      >
        숫자와 "-"만 입력할 수 있습니다.
      </Modal03>
    </>
  );
}

export default SignUp;
