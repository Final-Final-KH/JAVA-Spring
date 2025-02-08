import { useEffect } from "react";
import { useLocation } from "react-router-dom";

const ScrollToTop = () => {
  const { pathname } = useLocation();

  useEffect(() => {
    window.scrollTo(0, 0); // 화면을 상단으로 이동
  }, [pathname]); // 경로가 변경될 때 실행

  return null; // 화면에 렌더링하지 않는 컴포넌트
};

export default ScrollToTop;
