import React, { createContext, useState, useEffect } from "react";

export const AuthContext = createContext();

export const AuthProvider = ({ children }) => {
  const [isLoggedIn, setIsLoggedIn] = useState(false);

  useEffect(() => {
    // 페이지 로드 시 토큰 확인
    const token = localStorage.getItem("accessToken");
    setIsLoggedIn(!!token);
  }, []);

  useEffect(() => {
    console.log("로그인 상태:", isLoggedIn);
  }, [isLoggedIn]);

  const loggedIn = () => {
    setIsLoggedIn(true);
  };
  const logout = () => {
    try {
      localStorage.removeItem("accessToken");
      localStorage.removeItem("refreshToken");
      setIsLoggedIn(false);
    } catch (error) {
      console.error("로그아웃 중 오류 발생:", error);
    }
  };

  return (
    <AuthContext.Provider value={{ isLoggedIn, loggedIn, logout }}>
      {children}
    </AuthContext.Provider>
  );
};
