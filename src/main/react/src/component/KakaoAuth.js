import { useNavigate } from "react-router-dom";
import axios from "axios";
import qs from "qs";
import { useEffect, useContext, useState } from "react";
import {
  REST_API_KEY,
  REDIRECT_URI,
  CLIENT_SECRET,
} from "../utils/KakaoAuthConfig";
import { AuthContext } from "../api/context/AuthContext";

export default function KakaoAuth() {
  const navigate = useNavigate();

  const params = new URL(document.URL).searchParams;
  const code = params.get("code");
  const { loggedIn } = useContext(AuthContext);
  const [error, setError] = useState(null);

  const getAccessToken = async () => {
    const payload = qs.stringify({
      grant_type: "authorization_code",
      client_id: REST_API_KEY,
      redirect_uri: REDIRECT_URI,
      code: code,
      client_secret: CLIENT_SECRET,
    });
    try {
      const res = await axios.post(
        "https://kauth.kakao.com/oauth/token",
        payload
      );
      const kakaoAccessToken = res.data.access_token;
      console.log("카카오 엑세스토큰:", kakaoAccessToken);

      // Kakao SDK 초기화 및 액세스 토큰 설정
      window.Kakao.init(REST_API_KEY);
      window.Kakao.Auth.setAccessToken(res.data.access_token); // access token 설정

      // 사용자 정보 가져오기
      const userInfoResponse = await axios.get(
        "https://kapi.kakao.com/v2/user/me",
        {
          headers: {
            Authorization: `Bearer ${kakaoAccessToken}`,
          },
        }
      );
      console.log("카카오 사용자 정보:", userInfoResponse.data);

      const {
        id: providerId,
        kakao_account,
        properties,
      } = userInfoResponse.data;
      const email = kakao_account.email || null; // 이메일이 null일 경우 null로 설정
      const name =
        properties?.nickname || kakao_account?.profile?.nickname || "Unknown"; // 이름 설정

      // 백엔드로 사용자 정보 전송
      const response = await axios.post("/api/auth/kakao", {
        email,
        name,
        provider: "kakao",
        providerId,
      });

      const { accessToken, refreshToken } = response.data;

      // 로컬에 새로운 액세스 토큰 및 리프레시 토큰 저장
      localStorage.setItem("accessToken", accessToken);
      localStorage.setItem("refreshToken", refreshToken);

      // 카카오 액세스 토큰 제거
      window.Kakao.Auth.setAccessToken(null);

      loggedIn();
      navigate("/feed");
    } catch (err) {
      console.error("카카오 로그인 실패: ", err);
      setError("OAuth 로그인 실패");
    }
  };

  useEffect(() => {
    if (code) {
      getAccessToken();
    }
  }, [code]);

  return <div>{error ? <p>{error}</p> : <p>로그인 중...</p>}</div>;
}
