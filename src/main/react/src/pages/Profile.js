import React, { useState, useEffect, useRef } from "react";
import imgLogo1 from "../images/DeveloperMark.jpg";
import imgLogo2 from "../images/EditButton.webp";
import imgLogo3 from "../images/SaveButton.png";
import { useNavigate } from "react-router-dom";
import EditableField from "../component/profileComponents/EditableField";
import "../css/profile.css";
import AxiosInstance from "../axios/AxiosInstanse";
import { ref, uploadBytes, getDownloadURL } from "firebase/storage";
import { auth, storage } from "../utils/FirebaseConfig";

const Profile = () => {
  useEffect(() => {
    // 페이지 로드 시 body의 배경 색상을 설정
    document.body.style.backgroundColor = "#f5f6f7";
  }, []);

  const navigate = useNavigate();
  const [loading, setLoading] = useState(true);

  const fileInputRef = useRef(null); // ref로 파일 입력 요소를 참조

  const goToMyPage = () => {
    navigate("/profile/mypage");
  };

  const [profileInfo, setProfileInfo] = useState({
    memberId: "",
    name: "User Name",
    email: "user@email.com",
    phone: "",
    location: "",
    bio: "",
    skills: "",
    resume: "",
  });

  useEffect(() => {
    const fetchProfileData = async () => {
      try {
        const response = await AxiosInstance.get("/api/profile");
        const data = response.data;
        setProfileInfo({
          memberId: data.memberId,
          name: data.name || "User Name",
          email: data.email || "제 3자 로그인",
          phone: data.phoneNumber || "",
          location: data.location || "",
          bio: data.bio || "",
          skills: data.skills || "",
          resume: data.resumeUrl || "",
        });

        // Firebase에서 프로필 이미지 URL을 가져오는 로직
        const imageRef = ref(storage, `profile_images/${data.memberId}`);
        const downloadURL = await getDownloadURL(imageRef);
        setProfileImage(downloadURL);
        console.log("Fetched profile data:", response.data);
      } catch (error) {
        console.error("Failed to fetch profile data:", error);
      } finally {
        setLoading(false); // 데이터 불러오기 완료 후 로딩 상태 해제
      }
    };

    fetchProfileData();
  }, []);

  const upLoadProfileIamge = async (file, memberId) => {
    if (!file) {
      console.error("파일이 없습니다.");
      return null;
    }
    try {
      const imageRef = ref(storage, `profile_images/${memberId}`);
      await uploadBytes(imageRef, file); // 업로드
      const downloadURL = await getDownloadURL(imageRef); // 업로드한 파일의 URL 가져오기
      return downloadURL;
    } catch (error) {
      console.error("이미지 업로드 중 오류 발생:", error);
      return null;
    }
  };

  const updateProfileField = async (field, value) => {
    const fieldMap = {
      name: "/name",
      phone: "/phone",
      location: "/location",
      bio: "/bio",
      skills: "/skills",
      resume: "/resume",
    };

    try {
      const endpoint = `/api/profile${fieldMap[field]}`;
      await AxiosInstance.put(endpoint, null, { params: { [field]: value } });
      setProfileInfo((prev) => ({ ...prev, [field]: value })); // 성공 시 로컬 상태 업데이트
      alert(`${field} updated successfully`);
    } catch (error) {
      console.error(`Failed to update ${field}:`, error);
      alert(`Failed to update ${field}`);
    }
  };

  const placeholder = {
    bio: "자기소개 내용을 입력합니다. 이곳에 자신의 소개글을 작성하세요.",
    skills: "이곳에 전문 분야를 작성합니다. 예: 웹 개발, 데이터 분석 등",
    resume: "이력서 내용을 입력합니다. 경력, 학력 등을 작성하세요.",
  };

  const [editMode, setEditMode] = useState({
    name: false,
    email: false,
    bio: false,
    skills: false,
    resume: false,
    recommendation: false,
  });

  const [profileImage, setProfileImage] = useState(imgLogo1);
  const [editImageMode, setEditImageMode] = useState(false);
  const [imageModified, setImageModified] = useState(false);
  const [imageUploading, setImageUploading] = useState(false);

  // handleImageUpload에서 두 번째 클릭을 방지하기 위한 조건 추가

  const handleImageChange = async (event) => {
    console.log("handleImageChange triggered");

    // 이미지 업로드 중이면 추가 동작을 방지
    if (imageUploading) return;

    const file = event.target.files[0];
    if (file) {
      try {
        console.log("업로드할 파일 선택됨:", file);
        // 업로드 진행 중으로 상태 업데이트
        setImageUploading(true);

        // 이미지 업로드
        const uploadedImageUrl = await upLoadProfileIamge(
          file,
          profileInfo.memberId
        );

        if (uploadedImageUrl) {
          setProfileImage(uploadedImageUrl); // 업로드된 이미지 URL 설정
          console.log("업로드된 이미지 URL:", uploadedImageUrl);
          setImageModified(true); // 이미지가 수정된 상태로 업데이트
        } else {
          console.error("이미지 업로드 오류 발생");
        }
      } catch (error) {
        console.error("이미지 업로드 중 오류 발생:", error);
      } finally {
        setImageUploading(false); // 업로드 완료 후 상태 해제
      }
    }
  };

  const [friendList, setFriendList] = useState([
    { id: 1, name: "홍길동", isFriend: false, requestSent: false },
    { id: 2, name: "김철수", isFriend: false, requestSent: false },
    { id: 3, name: "이영희", isFriend: false, requestSent: false },
  ]);

  const handleSendRequest = (id) => {
    setFriendList((prevList) =>
      prevList.map((friend) =>
        friend.id === id ? { ...friend, requestSent: true } : friend
      )
    );
  };

  const handleCancelRequest = (id) => {
    setFriendList((prevList) =>
      prevList.map((friend) =>
        friend.id === id ? { ...friend, requestSent: false } : friend
      )
    );
  };

  const validatePhoneNumber = (phone) => {
    // 전화번호가 숫자와 하이픈(-)만 포함하는지 확인하는 정규식
    const phoneRegex = /^[0-9-]+$/;
    return phoneRegex.test(phone);
  };

  const handleSavePhone = (value) => {
    if (!validatePhoneNumber(value)) {
      // 유효하지 않은 전화번호일 경우 경고 메시지 표시
      alert("전화번호는 숫자와 하이픈만 포함할 수 있습니다.");
      return; // 유효하지 않으면 저장을 취소하고 함수 종료
    }

    // 유효한 전화번호일 경우 상태 업데이트 및 서버 저장
    updateProfileField("phone", value);
  };

  if (loading) {
    return <div>Loading...</div>; // 로딩 중일 때 표시
  }

  return (
    <div className="layout-container">
      {/* 좌측 섹션: 프로필 및 소개 */}
      <div className="profile-left">
        <div className="profile-mini">
          {/* 프로필 이미지 컨테이너 */}
          <div className="profile-image-container">
            <div className="profile-image-wrapper">
              <img
                src={profileImage}
                alt="프로필 이미지"
                className="profile-image"
              />
              <div className="my-page-button-container">
                <button className="my-page-button" onClick={goToMyPage}>
                  마이페이지
                </button>
              </div>

              <label htmlFor="image-upload" className="edit-button">
                <img
                  src={imgLogo2}
                  alt="이미지 업로드"
                  style={{
                    width: "20px",
                    height: "20px",
                    cursor: "pointer",
                  }}
                />
              </label>
              <input
                id="image-upload"
                type="file"
                accept="image/*"
                ref={fileInputRef} // ref로 파일 입력 참조
                style={{ display: "none" }} // 파일 입력창 숨기기
                onChange={handleImageChange} // 파일 변경 시 처리
              />
            </div>
          </div>

          <EditableField
            content="email"
            value={profileInfo.email}
            isEditable={false}
          />

          <EditableField
            content="name"
            value={profileInfo.name}
            isEditable={true}
            onSave={(value) => updateProfileField("name", value)}
          />

          <EditableField
            content="location"
            value={profileInfo.location}
            isEditable={true}
            onSave={(value) => updateProfileField("location", value)}
          />

          <EditableField
            content="phone"
            value={profileInfo.phone}
            isEditable={true}
            onSave={handleSavePhone}
          />
        </div>

        <EditableField
          label="회원 소개"
          content="bio"
          value={profileInfo.bio}
          placeholder={placeholder.bio}
          isEditable={true}
          onSave={(value) => updateProfileField("bio", value)}
        />

        <EditableField
          label="전문 분야"
          content="skills"
          value={profileInfo.skills}
          placeholder={placeholder.skills}
          isEditable={true}
          onSave={(value) => updateProfileField("skills", value)}
        />

        <EditableField
          label="이력서"
          content="resume"
          value={profileInfo.resume}
          placeholder={placeholder.resume}
          isEditable={true}
          onSave={(value) => updateProfileField("resume", value)}
        />
      </div>

      {/* 우측 섹션: 친구 추천 */}
      <div className="profile-right friends-section">
        <h2>친구 추천</h2>
        <ul className="friend-list">
          {friendList.map((friend) => (
            <li key={friend.id} className="friend-item">
              <div className="friend-info">
                <img
                  src={imgLogo1}
                  alt="친구 이미지"
                  className="friend-image"
                />
                <div className="friend-details">
                  <span>{friend.name}</span>
                  <span className="friend-role">미친구</span>
                </div>
              </div>

              {/* 친구 요청 버튼과 메시지 버튼 */}
              <div className="friend-actions">
                {!friend.isFriend && !friend.requestSent ? (
                  <button
                    className="friend-request-button"
                    onClick={() => handleSendRequest(friend.id)}
                  >
                    친구 요청
                  </button>
                ) : friend.requestSent ? (
                  <button
                    className="friend-request-button"
                    onClick={() => handleCancelRequest(friend.id)}
                  >
                    요청 취소
                  </button>
                ) : (
                  <button className="friend-button disabled" disabled>
                    친구
                  </button>
                )}
                <button className="message-button">메시지</button>
              </div>
            </li>
          ))}
        </ul>
      </div>
    </div>
  );
};

export default Profile;
