import React, { useState, useEffect } from "react";
import { getUserInfo } from "../axios/AxiosInstanse";

function Forum() {
  const [userInfo, setUserInfo] = useState(null); // 사용자 정보를 저장할 상태 변수

  // 컴포넌트가 마운트될 때 유저 정보 요청
  useEffect(() => {
    const fetchUserInfo = async () => {
      const data = await getUserInfo(); // 유저 정보 받아오기
      setUserInfo(data); // 받아온 데이터를 상태에 저장
    };
    fetchUserInfo();
  }, []);

  if (!userInfo) {
    return <div>Loading...</div>; // 유저 정보가 아직 로딩 중이면 'Loading...' 표시
  }

  return (
    <div>
      <h1>Welcome to the Forum page!</h1>
      <p>Member ID: {userInfo.memberId}</p> {/* 맴버 ID 출력 */}
      <p>Name: {userInfo.name}</p> {/* 이름 출력 */}
    </div>
  );
}

export default Forum;
