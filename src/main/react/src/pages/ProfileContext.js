import React, { createContext, useContext, useState, useEffect } from "react";
import AxiosInstance from "../axios/AxiosInstanse";

const ProfileContext = createContext();

export const useProfile = () => {
  return useContext(ProfileContext);
};

export const ProfileProvider = ({ children }) => {
  const [profileInfo, setProfileInfo] = useState({
    memberId: "",
    name: "기본 이름",
    email: "기본 이메일",
    phone: "",
    location: "",
    bio: "",
    skills: "",
    resume: "",
  });

  const updateProfile = (newProfileInfo) => {
    setProfileInfo((prevState) => ({
      ...prevState,
      ...newProfileInfo,
    }));
  };

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
    } catch (error) {
      console.error("Failed to fetch profile data:", error);
    }
  };

  useEffect(() => {
    fetchProfileData(); // 컴포넌트가 마운트되면 데이터 불러오기
  }, []);

  return (
    <ProfileContext.Provider value={{ profileInfo, updateProfile }}>
      {children}
    </ProfileContext.Provider>
  );
};
