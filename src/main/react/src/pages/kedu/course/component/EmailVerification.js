// EmailVerification.js
import React, { useState, useEffect } from "react";
import AxiosInstance from "../../../../axios/AxiosInstanse";
import Modal03 from "../../../../component/Modal03";
import "../../../../css/EmailVerification.css";

const EmailVerification = ({
  email,
  isEmailValid,
  isEmailAvailable,
  onVerify,
}) => {
  const [verificationCode, setVerificationCode] = useState("");
  const [verificationTimer, setVerificationTimer] = useState(0); // 인증번호 입력 타이머
  const [resendTimer, setResendTimer] = useState(0); // 인증번호 재발송 대기 타이머
  const [isVerified, setIsVerified] = useState(false);
  const [isSent, setIsSent] = useState(false);
  const [modalOpen, setModalOpen] = useState(false);

  useEffect(() => {
    // 인증번호 입력 타이머
    if (verificationTimer > 0) {
      const countdown = setTimeout(
        () => setVerificationTimer(verificationTimer - 1),
        1000
      );
      return () => clearTimeout(countdown);
    }
  }, [verificationTimer]);
  useEffect(() => {
    // 인증번호 재발송 대기 타이머
    if (resendTimer > 0) {
      const countdown = setTimeout(() => setResendTimer(resendTimer - 1), 1000);
      return () => clearTimeout(countdown);
    } else if (isSent) {
      setIsSent(false); // 타이머가 끝나면 재발송 버튼 활성화
    }
  }, [resendTimer, isSent]);

  const handleSendVerification = async (e) => {
    e.preventDefault(); // 폼 제출 방지

    if (isEmailValid && isEmailAvailable) {
      try {
        await AxiosInstance.post("/api/email/send", { email });
        console.log("검증용 이메일을 전송했습니다 :", email);
        setIsSent(true);
        setResendTimer(10); // 10초 대기 타이머 설정 (인증번호 재발송 대기)
        setVerificationTimer(300); // 인증번호 입력 타이머 5분 설정

        setModalOpen(true); // 모달 열기
      } catch (error) {
        console.error("검증용 이메일 전송에 실패했습니다 :", error);
        alert("인증 코드 전송에 실패했습니다.");
      }
    }
  };
  // 인증 코드 검증 요청
  const handleVerifyCode = async (e) => {
    e.preventDefault(); // 폼 제출 방지
    try {
      const response = await AxiosInstance.post("/api/email/verify", {
        email,
        code: verificationCode,
      });
      if (response.data.verified) {
        setIsVerified(true);
        onVerify(true);
        setVerificationTimer(0); // 인증 성공 후 타이머 초기화
        alert("이메일 인증에 성공했습니다!");
      } else {
        alert("인증 코드가 잘못되었습니다.");
      }
    } catch (error) {
      console.error("Error verifying code:", error);
      alert("인증 코드 검증에 실패했습니다.");
    }
  };

  const closeModal = () => setModalOpen(false);

  return (
    <div className="email-verification">
      <button
        onClick={handleSendVerification}
        disabled={!isEmailValid || !isEmailAvailable || isSent}
      >
        {resendTimer > 0
          ? `인증번호 재발송 (${resendTimer % 60}초)`
          : "이메일 인증번호 발송"}
      </button>

      {modalOpen && (
        <Modal03 open={modalOpen} close={closeModal} header="이메일 전송 완료">
          이메일을 전송했습니다. 인증 코드를 확인해주세요.
        </Modal03>
      )}

      {verificationTimer > 0 && (
        <div>
          <input
            type="text"
            placeholder="인증번호 입력"
            value={verificationCode}
            onChange={(e) => setVerificationCode(e.target.value)}
          />
          <button onClick={handleVerifyCode} disabled={isVerified}>
            인증 확인
          </button>
          <div className="timer">
            남은 시간:{" "}
            {Math.floor(verificationTimer / 60)
              .toString()
              .padStart(2, "0")}
            :{(verificationTimer % 60).toString().padStart(2, "0")}
          </div>
        </div>
      )}
      {isVerified && <p>이메일 인증 완료!</p>}
    </div>
  );
};

export default EmailVerification;
