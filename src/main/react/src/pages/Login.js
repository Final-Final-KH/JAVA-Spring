// Login.js
import React, { useState, useContext, useEffect } from "react";
import { useNavigate } from "react-router-dom";
import AxiosInstance from "../axios/AxiosInstanse";
import Common from "../utils/Common";
import { AuthContext } from "../api/context/AuthContext";
import { jwtDecode } from "jwt-decode";
import GoogleLoginButton from "../component/GoogleLoginButton";
import NaverLoginButton from "../component/NaverLoginButton";
import KakaoLoginButton from "../component/KakaoLoginButton";
import styled from "styled-components";

const SubmitButton = styled.button`
  width: 100%;
  padding: 10px;
  background-color: #333;
  color: #fff;
  border: none;
  border-radius: 4px;
  cursor: pointer;
  transition: background-color 0.3s;

  &:hover {
    background-color: #555;
  }
`;

function Login() {
  const [email, setEmail] = useState("");
  const [password, setPassword] = useState("");
  const [error, setError] = useState("");
  const navigate = useNavigate();
  const { isLoggedIn, loggedIn } = useContext(AuthContext);

  // 로그인 상태 확인후 자동 리디렉션
  useEffect(() => {
    if (isLoggedIn) {
      navigate("/feed");
    }
  }, [isLoggedIn, navigate]);

  // 일반 로그인 처리
  const handleSubmit = async (e) => {
    e.preventDefault();
    try {
      // 로그인 요청
      const response = await AxiosInstance.post("/auth/login", {
        email,
        password,
      });
      const { accessToken, refreshToken } = response.data;

      // JWT 디코딩하여 사용자 정보 추출
      const decodedToken = jwtDecode(accessToken);
      console.log("Decoded JWT:", decodedToken); // Debug log

      // 토큰을 localStorage에 저장
      Common.setAccessToken(accessToken);
      Common.setRefreshToken(refreshToken);

      // 디코딩된 사용자 정보를 localStorage에 저장
      const userData = {
        memberId: decodedToken.sub, // JWT의 'sub' 필드에서 memberId 가져오기
        email: decodedToken.email,
        name: decodedToken.name,
      };
      console.log("User data to be stored:", userData); // Debug log
      localStorage.setItem("keduMember", JSON.stringify(userData));

      // 로그인 상태 업데이트 및 피드 페이지로 이동
      loggedIn();
      navigate("/feed");
    } catch (err) {
      setError("이메일 또는 비밀번호가 올바르지 않습니다."); // 에러 메시지 처리
    }
  };

  // Google OAuth 로그인 처리
  const handleGoogleLoginSuccess = async (credentialResponse) => {
    if (credentialResponse.credential) {
      const decodedToken = jwtDecode(credentialResponse.credential);
      const { email, name, sub: providerId } = decodedToken;
      const accessToken = credentialResponse.credential;
      Common.setAccessToken(accessToken);
      try {
        const response = await AxiosInstance.post("/api/auth/google", {
          email,
          name,
          provider: "google",
          providerId,
        });
        const { accessToken, refreshToken } = response.data;
        Common.setAccessToken(accessToken);
        Common.setRefreshToken(refreshToken);
        console.log(jwtDecode(accessToken));
        loggedIn();
        navigate("/feed");
      } catch (err) {
        setError("OAuth 로그인 실패");
      }
    }
  };
  const handleGoogleLoginError = (error) => {
    setError("구글 로그인 오류: " + error.message);
  };

  return (
    <div
      style={{
        display: "flex",
        justifyContent: "center",
        alignItems: "center",
        minHeight: "100vh",
        background: "#f5f5f5",
        padding: "20px",
      }}
    >
      <div
        style={{
          background: "#fff",
          padding: "20px",
          borderRadius: "8px",
          boxShadow: "0 4px 8px rgba(0, 0, 0, 0.1)",
          maxWidth: "400px",
          width: "100%",
        }}
      >
        <h2 style={{ textAlign: "center", marginBottom: "20px" }}>로그인</h2>

        {/* 로그인 폼 */}
        <form onSubmit={handleSubmit}>
          <div style={{ marginBottom: "15px" }}>
            <label
              htmlFor="email"
              style={{ display: "block", marginBottom: "5px" }}
            >
              이메일
            </label>
            <input
              type="email"
              id="email"
              value={email}
              onChange={(e) => setEmail(e.target.value)}
              style={{
                width: "100%",
                padding: "10px",
                border: "1px solid #ddd",
                borderRadius: "4px",
              }}
              placeholder="이메일을 입력하세요"
            />
          </div>
          <div style={{ marginBottom: "15px" }}>
            <label
              htmlFor="password"
              style={{ display: "block", marginBottom: "5px" }}
            >
              비밀번호
            </label>
            <input
              type="password"
              id="password"
              value={password}
              onChange={(e) => setPassword(e.target.value)}
              style={{
                width: "100%",
                padding: "10px",
                border: "1px solid #ddd",
                borderRadius: "4px",
              }}
              placeholder="비밀번호를 입력하세요"
            />
          </div>
          {error && (
            <div
              style={{ color: "red", marginBottom: "15px", fontSize: "14px" }}
            >
              {error}
            </div>
          )}
          <SubmitButton type="submit">로그인</SubmitButton>
        </form>

        {/* 제3자 로그인 버튼들 */}
        <div style={{ marginTop: "20px", textAlign: "center" }}>
          <GoogleLoginButton
            onSuccess={handleGoogleLoginSuccess}
            onError={handleGoogleLoginError}
          />
          <NaverLoginButton />
          <KakaoLoginButton />
        </div>

        <div style={{ textAlign: "center", marginTop: "15px" }}>
          <span>회원이 아니신가요?</span>
          <a href="/signup" style={{ marginLeft: "5px", color: "#007bff" }}>
            회원가입
          </a>
        </div>
      </div>
    </div>
  );
}

export default Login;
