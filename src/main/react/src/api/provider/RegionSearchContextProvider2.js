import { useState, createContext } from "react";

// 초기값 설정
const defaultValue = {
  searchKeyword: "", // 기본값을 빈 문자열로 설정
  setSearchKeyword: () => {},
  academyName: "", // 기본값을 빈 문자열로 설정
  setAcademyName: () => {},
};

export const RegionSearchContext = createContext(defaultValue);

export const RegionContextProvider = ({ children }) => {
  // 상태 초기화
  const [searchKeyword, setSearchKeyword] = useState("");
  const [academyName, setAcademyName] = useState("");

  return (
    <RegionSearchContext.Provider
      value={{ searchKeyword, setSearchKeyword, academyName, setAcademyName }}
    >
      {children}
    </RegionSearchContext.Provider>
  );
};
