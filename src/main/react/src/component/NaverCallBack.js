import React, { useEffect, useContext } from "react";
import { useNavigate } from "react-router-dom";
import AxiosInstance from "../axios/AxiosInstanse";
import Common from "../utils/Common";
import { AuthContext } from "../api/context/AuthContext";

const NaverCallback = () => {
  const navigate = useNavigate();
  const { loggedIn } = useContext(AuthContext);

  useEffect(() => {
    const params = new URLSearchParams(window.location.search);
    const code = params.get("code");
    const state = params.get("state");

    const handleNaverLogin = async () => {
      try {
        const response = await AxiosInstance.post("/api/auth/naver", {
          code,
          state,
        });
        const { accessToken, refreshToken } = response.data;

        // 로그인 토큰 저장
        Common.setAccessToken(accessToken);
        Common.setRefreshToken(refreshToken);
        loggedIn();
        navigate("/feed");
      } catch (err) {
        console.error("네이버 로그인 실패:", err);
        alert("네이버 로그인에 실패했습니다. 다시 시도해주세요.");
        navigate("/");
      }
    };

    if (code && state) {
      handleNaverLogin();
    }
  }, [navigate, loggedIn]);

  return <div>네이버 로그인 처리 중...</div>;
};

export default NaverCallback;
