import axios from "axios";
import Commons from "../utils/Common";

// Axios 인스턴스를 생성
const AxiosInstance = axios.create({
  baseURL: Commons.KH_DOMAIN, // 기본 URL 설정 (Commons에서 정의된 도메인을 사용)
});

// 요청 인터셉터 추가
AxiosInstance.interceptors.request.use(
  async (config) => {
    // 액세스 토큰을 가져와 Authorization 헤더에 추가
    const accessToken = Commons.getAccessToken(); // Commons에서 액세스 토큰을 가져옴
    config.headers.Authorization = `Bearer ${accessToken}`; // Authorization 헤더 설정
    console.log("Authorization Header:", config.headers.Authorization); // 디버그용 출력
    return config; // 요청이 성공적으로 설정되면 반환
  },
  (error) => {
    // 요청 구성 중 발생한 에러를 반환
    console.error("요청 인터셉터 중 에러 발생:", error); // 추가 디버그 로그
    return Promise.reject(error); // 에러를 호출한 곳으로 전달
  }
);

// 응답 인터셉터 추가
AxiosInstance.interceptors.response.use(
  (response) => {
    // 성공적인 응답 처리
    return response; // 응답 객체를 그대로 반환
  },
  async (error) => {
    // 응답 에러 처리
    if (error.response && error.response.status === 401) {
      // 401 Unauthorized 에러 발생 시 토큰 갱신 로직 실행
      console.warn("401 오류: 토큰 갱신 시도 중..."); // 경고 로그 추가
      const newToken = await Commons.handleUnauthorized(); // Commons의 갱신 로직 호출
      if (newToken) {
        // 갱신된 토큰을 사용하여 원래 요청을 재시도
        error.config.headers.Authorization = `Bearer ${Commons.getAccessToken()}`;
        return AxiosInstance.request(error.config); // 재시도 요청 실행
      }
    }
    // 다른 에러는 그대로 반환
    console.error("응답 인터셉터 중 에러 발생:", error); // 디버그용 에러 로그 추가
    return Promise.reject(error); // 에러를 호출한 곳으로 전달
  }
);

// 현재 사용자 정보를 가져오는 함수
export const getUserInfo = async () => {
  try {
    // 서버에서 현재 사용자 정보 가져오기
    const rsp = await AxiosInstance.get("/auth/current-user");
    console.log("API Response from /auth/current-user:", rsp.data); // 디버그용 출력
    return rsp.data; // 사용자 데이터 반환
  } catch (error) {
    // 에러 발생 시 null 반환 및 로그 출력
    console.error("사용자 정보를 가져오는데 실패했습니다.", error); // 에러 로그 출력
    return null;
  }
};

export default AxiosInstance; // Axios 인스턴스 기본 내보내기
