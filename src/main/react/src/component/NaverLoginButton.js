import React from "react";
import { NAVER_AUTH_URL } from "../utils/NaverAuthConfig";
import OAuth2Button from "../styles/OAuth2Button";
import NaverIcon from "../images/naver_login_Icon.png";

const NaverLoginButton = () => {
  const handleLoginClick = () => {
    const state = Math.random().toString(36).substr(2, 15);
    const url = NAVER_AUTH_URL(state);
    window.location.href = url;
  };

  return (
    <OAuth2Button
      onClick={handleLoginClick}
      style={{
        backgroundColor: "#1ec800",
        color: "#fff",
        height: "40px",
        display: "flex",
        alignItems: "center",
        transition: "background-color 0.3s, opacity 0.3s", // 버튼의 hover 효과에 대한 전환 추가
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
          src={NaverIcon}
          alt="네이버 아이콘"
          style={{ height: "100%", width: "auto" }}
        />
      </div>
      <span
        style={{
          flex: 1,
          textAlign: "center",
          opacity: 1, // 글씨를 항상 불투명하게 설정
          transition: "opacity 0.3s", // 글씨의 opacity도 전환 효과를 주어 부드럽게 변하게 설정
        }}
      >
        네이버 로그인
      </span>
    </OAuth2Button>
  );
};

export default NaverLoginButton;
