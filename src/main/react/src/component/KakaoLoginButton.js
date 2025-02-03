import React from "react";
import { KAKAO_AUTH_URL } from "../utils/KakaoAuthConfig";
import OAuth2Button from "../styles/OAuth2Button";
import KakaoIcon from "../images/kakao_login_large.png";

const KakaoLoginButton = () => {
  return (
    <OAuth2Button
      href={KAKAO_AUTH_URL}
      style={{
        backgroundColor: "#FFEB00", // 카카오 기본 색상
        color: "black",
        height: "40px",
        display: "flex",
        alignItems: "center",
      }}
    >
      <div
        style={{
          height: "100%",
          display: "flex",
          alignItems: "center",
          marginRight: "10px",
        }}
      >
        <img
          src={KakaoIcon}
          alt="카카오 아이콘"
          style={{ height: "100%", width: "auto" }}
        />
      </div>
      <span style={{ flex: 1, textAlign: "center" }}>카카오 로그인하기</span>
    </OAuth2Button>
  );
};

export default KakaoLoginButton;
