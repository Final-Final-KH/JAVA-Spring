import { createContext, useEffect, useState } from "react";
import AxiosInstance from "../../axios/AxiosInstanse";

// 기본값 설정
const defaultValue = {
  member: [], // 사용자 정보를 저장할 상태
  setMember: () => {}, // 사용자 정보를 업데이트할 함수
};

// MemberInfoContext 생성
export const MemberInfoContext = createContext(defaultValue);

// TokenMember 함수 정의
// 로컬 스토리지에서 액세스 토큰을 가져와 API를 통해 사용자 정보를 요청
const TokenMember = async (setMember) => {
  const accessToken = localStorage.getItem("accessToken");

  if (accessToken) {
    try {
      const rsp = await AxiosInstance.get("/auth/current-user", {
        headers: {
          Authorization: `Bearer ${accessToken}`,
        },
      });
      setMember(rsp.data);
      localStorage.setItem("keduMember", JSON.stringify(rsp.data));
    } catch (error) {
      console.error("유저 정보를 가져오는데 실패했습니다.", error);
    }
  } else {
    console.log("액세스 토큰이 없습니다.");
  }
};

// MemberInfoContextProvider 컴포넌트 정의
export const MemberInfoContextProvider = ({ children }) => {
  const [member, setMember] = useState([]); // 사용자 정보를 상태로 관리

  useEffect(() => {
    const fetchData = async () => {
      // 로컬 스토리지에서 사용자 정보 가져오기
      const memberData = localStorage.getItem("keduMember");
      console.log("Retrieved memberData from localStorage:", memberData); // Debug log

      if (memberData) {
        // 로컬 스토리지에서 가져온 데이터를 JSON으로 파싱
        const parsedMember = JSON.parse(memberData);
        console.log("Parsed member data:", parsedMember); // Debug log
        setMember(parsedMember); // 상태 업데이트
      } else {
        // 로컬 스토리지에 정보가 없으면 TokenMember를 호출하여 API에서 사용자 정보 가져오기
        await TokenMember(setMember);
      }
    };

    fetchData();
  }, []); // 컴포넌트가 마운트될 때 한 번 실행

  return (
    // Provider를 통해 전역으로 member와 setMember를 전달
    <MemberInfoContext.Provider value={{ member, setMember }}>
      {children}
    </MemberInfoContext.Provider>
  );
};
