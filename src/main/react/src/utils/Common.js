import moment from "moment";
import axios from "axios";
import "moment/locale/ko"; // 한글 로컬라이제이션
import { toast } from "react-toastify";
import "react-toastify/dist/ReactToastify.css";
moment.locale("ko"); // 한글 설정 적용

const Common = {
  KH_DOMAIN: "",
  KH_SOCKET_URL: "ws://localhost:8111/ws/chat",

  timeFromNow: (timestamp) => {
    return moment(timestamp).fromNow();
  },
  formatDate: (timestamp) => {
    const date = new Date(timestamp);
    const year = date.getFullYear();
    const month = ("0" + (date.getMonth() + 1)).slice(-2); // Adds leading 0 if needed
    const day = ("0" + date.getDate()).slice(-2);
    const hour = ("0" + date.getHours()).slice(-2);
    const minute = ("0" + date.getMinutes()).slice(-2);
    return `${year}년 ${month}월 ${day}일 ${hour}시 ${minute}분`;
  },

  getAccessToken: () => {
    return localStorage.getItem("accessToken");
  },
  setAccessToken: (token) => {
    localStorage.setItem("accessToken", token);
  },
  getRefreshToken: () => {
    return localStorage.getItem("refreshToken");
  },
  setRefreshToken: (token) => {
    localStorage.setItem("refreshToken", token);
  },

  // 401 에러 처리 함수
  handleUnauthorized: async () => {
    console.log("handleUnauthorized");
    const refreshToken = Common.getRefreshToken();
    const accessToken = Common.getAccessToken();
    const config = {
      headers: {
        Authorization: `Bearer ${accessToken}`,
      },
    };
    try {
      const res = await axios.post(
        `${Common.KH_DOMAIN}/auth/refresh`,
        refreshToken,
        config
      );
      // 응답 데이터에서 새로운 액세스 및 리프레시 토큰 처리
      const { accessToken: newAccessToken, refreshToken: newRefreshToken } =
        res.data;

      // 새로운 토큰 저장
      Common.setAccessToken(newAccessToken);
      Common.setRefreshToken(newRefreshToken);
      return true;
    } catch (err) {
      console.log(err);
      console.error("토큰 갱신 실패:", err.response?.data || err.message);
      Common.clearTokens();
      Common.redirectToLogin();
      return false;
    }
  },
  clearTokens: () => {
    console.log("모든 토큰 삭제 처리 중...");
    localStorage.removeItem("accessToken");
    localStorage.removeItem("refreshToken");
  },

  redirectToLogin: () => {
    toast.warn("세션이 만료되었습니다. 다시 로그인 해주세요.", {
      position: toast.POSITION.TOP_CENTER,
      autoClose: 3000,
      hideProgressBar: true,
      closeOnClick: true,
      pauseOnHover: false,
      draggable: true,
      theme: "colored",
    });

    // 로그인 페이지로 리디렉션
    setTimeout(() => {
      window.location.href = "/login";
    }, 3500); // 알림 표시 후 3.5초 후 리디렉션
  },
};

export default Common;
