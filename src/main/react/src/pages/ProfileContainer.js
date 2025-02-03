import React, { useState } from "react";
import Profile from "./Profile"; // Profile 컴포넌트
import Feed from "./Feed"; // Feed 컴포넌트

const ProfileContainer = () => {
  // 프로필 정보 상태
  const [profileInfo, setProfileInfo] = useState({
    name: "User Name",
    email: "user@email.com",
    age: "User Age",
    location: "User Location",
    introduction: "소개 내용",
    expertise: "전문 분야 내용",
    resume: "이력서 내용",
    recommendation: "추천서 내용",
  });

  // 프로필 수정 핸들러
  const handleProfileUpdate = (field, value) => {
    setProfileInfo((prev) => ({
      ...prev,
      [field]: value,
    }));
  };

  return (
    <div className="profile-container">
      <Profile
        profileInfo={profileInfo}
        onProfileUpdate={handleProfileUpdate}
      />
      <Feed profileInfo={profileInfo} />
    </div>
  );
};

export default ProfileContainer;
