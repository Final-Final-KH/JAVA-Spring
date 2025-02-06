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

  useEffect(() => {
    // 페이지 로드 시 localStorage에서 프로필 사진 URL을 가져오기
    const storedProfilePic = localStorage.getItem("profilePic");
    if (storedProfilePic) {
      setProfilePic(storedProfilePic);
    }
  }, []);

  const [profilePic, setProfilePic] = useState("initialPicUrl.jpg");

  const updateProfile = (newProfileInfo) => {
    setProfileInfo((prevState) => ({
      ...prevState,
      ...newProfileInfo,
    }));
  };

  const handleProfilePicChange = (newPic) => {
    setProfilePic(newPic);
    localStorage.setItem("profilePic", newPic); // 변경된 URL을 localStorage에 저장
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
      // If the profile pic URL is part of the response, update it
      if (data.profilePic) {
        handleProfilePicChange(data.profilePic);
      }
    } catch (error) {
      console.error("Failed to fetch profile data:", error);
    }
  };

  useEffect(() => {
    fetchProfileData(); // Fetch data when component mounts
  }, []);

  return (
    <ProfileContext.Provider
      value={{ profileInfo, updateProfile, profilePic, handleProfilePicChange }}
    >
      {children}
    </ProfileContext.Provider>
  );
};
