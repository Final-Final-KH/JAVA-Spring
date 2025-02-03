import { createContext, useEffect, useState } from "react";
import TokenMember from "../AxiosApi2";

const defaultValue = {
  member: [],
  setMember: [],
};

export const MemberInfoContext = createContext(defaultValue);

export const MemberInfoContextProvider = ({ children }) => {
  const [member, setMember] = useState([]);

  // useEffect 내에서 비동기 함수 호출
  // localStorage에 저장된 memberId를 MemberInfoContext의 상태로 관리하여 애플리케이션 전역에서 접근 가능하도록 설정
  // 이 상태는 CreatePost와 같은 컴포넌트에서 직접 활용 가능.
  useEffect(() => {
    const fetchData = async () => {
      const memberData = localStorage.getItem("keduMember");
      console.log("Retrieved memberData from localStorage:", memberData); // Debug log
      if (memberData) {
        const parsedMember = JSON.parse(memberData);
        console.log("Parsed member data:", parsedMember); // Debug log
        setMember(parsedMember);
      } else {
        // 로컬 스토리지에 정보가 없으면 TokenMember를 호출하여 API에서 사용자 정보 가져오기
        await TokenMember(setMember);
      }
    };

    fetchData();
  }, []);

  // 점
  return (
    <MemberInfoContext.Provider value={{ member, setMember }}>
      {children}
    </MemberInfoContext.Provider>
  );
};
