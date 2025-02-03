// Navbar.js
import React, { useContext } from "react";
import { Link, useNavigate } from "react-router-dom";
import imgLogo1 from "../images/DeveloperMark.jpg";
import { AuthContext } from "../api/context/AuthContext";

function Navbar() {
  const { isLoggedIn, logout } = useContext(AuthContext);
  const navigate = useNavigate();

  const handleLoginLogout = () => {
    if (isLoggedIn) {
      logout(); // 로그아웃 상태 변경
      setTimeout(() => navigate("/"), 0); // 로그아웃 후 비동기적으로 이동
    } else {
      navigate("/login"); // 로그인 페이지로 이동
    }
  };

  return (
    <nav
      style={{
        position: "fixed", // 상단 고정
        top: 0, // 페이지 상단에 배치
        left: 0, // 페이지 왼쪽에 배치
        width: "100%", // 전체 너비 차지
        zIndex: 1000, // 다른 요소 위에 표시
        padding: "10px 20px",
        background: "rgba(51, 51, 51, 0.5)" /* #333 색상에 50% 투명도 */,
        color: "#fff",
        display: "flex",
        justifyContent: "space-between",
        alignItems: "center",
        flexWrap: "wrap",
        boxShadow: "0 2px 5px rgba(0, 0, 0, 0.2)", // 하단 그림자 효과
        boxSizing: "border-box", // 패딩 포함
      }}
    >
      {/* 사이트 로고 */}
      <div>
        <Link to="/feed" style={{ textDecoration: "none" }}>
          <img
            src={imgLogo1}
            alt="로고"
            style={{ width: "50px", height: "50px" }}
          />
        </Link>
      </div>

      {/* 네비게이션 메뉴 */}
      <ul
        style={{
          display: "flex",
          listStyle: "none",
          justifyContent: "center",
          alignItems: "center",
          flexWrap: "wrap",
          margin: 0,
          padding: 0,
          gap: "20px",
        }}
      >
        <li>
          <Link to="/profile" style={{ color: "#fff", textDecoration: "none" }}>
            프로필
          </Link>
        </li>
        <li>
          <Link to="/feed" style={{ color: "#fff", textDecoration: "none" }}>
            피드
          </Link>
        </li>
        <li>
          <Link to="/forum" style={{ color: "#fff", textDecoration: "none" }}>
            포럼
          </Link>
        </li>
        <li>
          <Link to="/news" style={{ color: "#fff", textDecoration: "none" }}>
            뉴스
          </Link>
        </li>
        <li>
          <Link to="/jobpost" style={{ color: "#fff", textDecoration: "none" }}>
            채용공고
          </Link>
        </li>
        <li>
          <Link to="/edu" style={{ color: "#fff", textDecoration: "none" }}>
            국비교육
          </Link>
        </li>
      </ul>

      {/* 로그인/로그아웃 버튼 */}
      <button
        onClick={handleLoginLogout}
        style={{
          padding: "8px 12px",
          backgroundColor: "#ff7f50",
          border: "none",
          color: "#fff",
          borderRadius: "4px",
          cursor: "pointer",
        }}
      >
        {isLoggedIn ? "로그아웃" : "로그인"}
      </button>
    </nav>
  );
}

export default Navbar;
