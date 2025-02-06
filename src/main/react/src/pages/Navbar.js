import React, { useContext } from "react";
import { Link, useNavigate } from "react-router-dom";
import imgLogo1 from "../images/DeveloperMark.jpg";
import { AuthContext } from "../api/context/AuthContext";
import "../css/Navbar.css"; // 별도의 CSS 파일을 import

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
    <nav className="navbar">
      {/* 사이트 로고 */}
      <div>
        <Link to="/feed" className="navbar-logo">
          <img src={imgLogo1} alt="로고" className="navbar-logo-image" />
        </Link>
      </div>

      {/* 네비게이션 메뉴 */}
      <ul className="navbar-menu">
        <li>
          <Link to="/profile" className="navbar-item">
            프로필
          </Link>
        </li>
        <li>
          <Link to="/feed" className="navbar-item">
            피드
          </Link>
        </li>
        <li>
          <Link to="/forum" className="navbar-item">
            포럼
          </Link>
        </li>
        <li>
          <Link to="/news" className="navbar-item">
            뉴스
          </Link>
        </li>
        <li>
          <Link to="/jobpost" className="navbar-item">
            채용공고
          </Link>
        </li>
        <li>
          <Link to="/edu" className="navbar-item">
            국비교육
          </Link>
        </li>
      </ul>

      {/* 로그인/로그아웃 버튼 */}
      <button onClick={handleLoginLogout} className="navbar-button">
        {isLoggedIn ? "로그아웃" : "로그인"}
      </button>
    </nav>
  );
}

export default Navbar;
