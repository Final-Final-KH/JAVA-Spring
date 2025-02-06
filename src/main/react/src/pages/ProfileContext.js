import React, { createContext, useContext, useState, useEffect } from "react";
import AxiosInstance from "../axios/AxiosInstanse";

// 기본 프로필 정보 값
const defaultProfileInfo = {
  memberId: "",
  name: "기본 이름",
  email: "기본 이메일",
  phone: "",
  location: "",
  bio: "",
  skills: "",
  resume: "",
};

const ProfileContext = createContext();

export const useProfile = () => {
  return useContext(ProfileContext);
};

export const ProfileProvider = ({ children }) => {
  const [profileInfo, setProfileInfo] = useState(defaultProfileInfo);
  const [profilePic, setProfilePic] = useState("initialPicUrl.jpg");

  // 페이지 로드 시 localStorage에서 프로필 사진 URL 가져오기
  useEffect(() => {
    const storedProfilePic = localStorage.getItem("profilePic");
    if (storedProfilePic) {
      setProfilePic(storedProfilePic);
    }
  }, []);

  // 프로필 정보 업데이트 함수
  const updateProfile = (newProfileInfo) => {
    setProfileInfo((prevState) => ({
      ...prevState,
      ...newProfileInfo,
    }));
  };

  // 프로필 사진 업데이트 함수
  const handleProfilePicChange = (newPic) => {
    setProfilePic(newPic);
    localStorage.setItem("profilePic", newPic);
  };

  // 프로필 데이터 가져오기 함수 (B: 재호출하여 새 로그인 사용자 정보 반영)
  const fetchProfileData = async () => {
    try {
      const response = await AxiosInstance.get("/api/profile");
      const data = response.data;
      updateProfile({
        memberId: data.memberId,
        name: data.name || "User Name",
        email: data.email || "제 3자 로그인",
        phone: data.phoneNumber || "",
        location: data.location || "",
        bio: data.bio || "",
        skills: data.skills || "",
        resume: data.resumeUrl || "",
      });
      if (data.profilePic) {
        handleProfilePicChange(data.profilePic);
      }
    } catch (error) {
      console.error("Failed to fetch profile data:", error);
    }
  };

  // 프로필 데이터를 한 번만 가져오는 기존 효과 (옵션 C: 만약 토큰이나 auth 상태를 감지한다면 여기에 추가)
  useEffect(() => {
    fetchProfileData();
  }, []);

  // A: 로그아웃 시 프로필 정보 초기화 함수
  const logout = () => {
    setProfileInfo(defaultProfileInfo);
    setProfilePic("initialPicUrl.jpg");
    localStorage.removeItem("profilePic");
  };

  return (
    <ProfileContext.Provider
      value={{
        profileInfo,
        updateProfile,
        profilePic,
        handleProfilePicChange,
        fetchProfileData, // 새 로그인 후 호출할 수 있도록 노출
        logout, // 로그아웃 시 호출할 수 있도록 노출
      }}
    >
      {children}
    </ProfileContext.Provider>
  );
};
