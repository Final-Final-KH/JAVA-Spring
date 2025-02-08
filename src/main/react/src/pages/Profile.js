import React, { useState, useEffect, useRef, useContext } from "react";
import imgLogo1 from "../images/DeveloperMark.jpg";
import imgLogo2 from "../images/EditButton.webp";
import imgLogo3 from "../images/SaveButton.png";
import { useNavigate } from "react-router-dom";
import EditableField from "../component/profileComponents/EditableField";
import "../css/profile.css";
import AxiosInstance from "../axios/AxiosInstanse";
import { ref, uploadBytes, getDownloadURL } from "firebase/storage";
import { auth, storage } from "../utils/FirebaseConfig";
import { useProfile } from "./ProfileContext";
import { AuthContext } from "../api/context/AuthContext";
import Modal03 from "../component/Modal03";
import FeedApi from "../api/FeedApi"; // KR: 친구 추천 데이터를 가져오기 위한 API 임포트

import { getUserInfo } from "../axios/AxiosInstanse"; // 사용자 정보 가져오기
import { ToastContainer, toast } from "react-toastify"; // Toastify

const Profile = () => {
  // KR: 페이지 로드시 body 배경색 설정
  useEffect(() => {
    document.body.style.backgroundColor = "#f5f6f7";
  }, []);

  const navigate = useNavigate();
  const [loading, setLoading] = useState(true);
  const [showDeleteModal, setShowDeleteModal] = useState(false);
  const { logout } = useContext(AuthContext);
  const [memberId, setMemberId] = useState(null);

  const fileInputRef = useRef(null); // KR: 파일 입력 요소를 참조하기 위한 ref

  const goToMyPage = () => {
    navigate("/profile/mypage");
  };

  // KR: 프로필 관련 로컬 상태 초기화
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

  // KR: 프로필 컨텍스트에서 프로필 사진과 사진 변경 함수를 불러옴
  const { profilePic, handleProfilePicChange } = useProfile();
  const [newPic, setNewPic] = useState("");

  // KR: 프로필 사진 변경 핸들러
  const handleChangePic = () => {
    handleProfilePicChange(newPic);
  };

  // KR: 서버에서 프로필 데이터를 불러오는 useEffect (파이어베이스에서 이미지 URL도 가져옴)
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

        // KR: Firebase에서 프로필 이미지 URL 가져오기
        const imageRef = ref(storage, `profile_images/${data.memberId}`);
        const downloadURL = await getDownloadURL(imageRef);
        setProfileImage(downloadURL);
        console.log("Fetched profile data:", response.data);
      } catch (error) {
        console.error("Failed to fetch profile data:", error);
      } finally {
        setLoading(false); // KR: 데이터 불러오기 완료 후 로딩 상태 해제
      }
    };

    fetchProfileData();
  }, []);

  // KR: 파이어베이스에 이미지 업로드 후 다운로드 URL 반환 함수
  const upLoadProfileImage = async (file, memberId) => {
    if (!file) {
      toast.error("파일이 없습니다.");
      return null;
    }
    try {
      const imageRef = ref(storage, `profile_images/${memberId}`);
      await uploadBytes(imageRef, file); // KR: 파일 업로드
      const downloadURL = await getDownloadURL(imageRef); // KR: 업로드한 파일의 URL 가져오기
      return downloadURL;
    } catch (error) {
      toast.error("이미지 업로드 중 오류 발생:", error);
      return null;
    }
  };

  // KR: 특정 프로필 필드를 서버에 업데이트하는 함수
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
      setProfileInfo((prev) => ({ ...prev, [field]: value })); // KR: 성공 시 로컬 상태 업데이트
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

  // KR: 이미지 변경 이벤트 핸들러 (파일 업로드 진행 중이면 추가 동작 방지)
  const handleImageChange = async (event) => {
    console.log("handleImageChange triggered");
    if (imageUploading) return;
    const file = event.target.files[0];
    if (file) {
      try {
        console.log("업로드할 파일 선택됨:", file);
        setImageUploading(true);
        const uploadedImageUrl = await upLoadProfileImage(
          file,
          profileInfo.memberId
        );
        if (uploadedImageUrl) {
          setProfileImage(uploadedImageUrl);
          console.log("업로드된 이미지 URL:", uploadedImageUrl);
          setImageModified(true);
        } else {
          toast.error("이미지 업로드 오류 발생");
        }
      } catch (error) {
        toast.error("이미지 업로드 중 오류 발생:", error);
      } finally {
        setImageUploading(false);
      }
    }
  };

  // ---------------- 친구 추천 부분 ----------------
  // KR: 기존에 하드코딩된 친구 목록 대신 빈 배열로 초기화
  const [friendList, setFriendList] = useState([]);

  // KR: 프로필 정보에서 memberId가 준비되면 서버에서 친구 추천 데이터를 동적으로 불러옴
  useEffect(() => {
    async function fetchFriends() {
      try {
        if (profileInfo.memberId) {
          const suggestedFriends = await FeedApi.fetchSuggestedFriends(
            profileInfo.memberId
          );
          setFriendList(suggestedFriends);
        }
      } catch (error) {
        toast.error("❌ 친구 추천 불러오기 실패:", error);
      }
    }
    fetchFriends();
  }, [profileInfo.memberId]);

  // KR: 친구 요청 버튼 클릭 시 상태 업데이트 함수
  const handleSendRequest = (id) => {
    setFriendList((prevList) =>
      prevList.map((friend) =>
        friend.memberId === id ? { ...friend, requestSent: true } : friend
      )
    );
  };

  // KR: 친구 요청 취소 버튼 클릭 시 상태 업데이트 함수
  const handleCancelRequest = (id) => {
    setFriendList((prevList) =>
      prevList.map((friend) =>
        friend.memberId === id ? { ...friend, requestSent: false } : friend
      )
    );
  };

  // ======================================================================
  // KR: 로그인 상태 확인
  // 아래 useEffect는 현재 로그인한 사용자 정보를 가져와서, 유효한 사용자가 아니면 로그인 페이지로 리다이렉트합니다.
  useEffect(() => {
    const checkLoggedIn = async () => {
      try {
        const userInfo = await getUserInfo();
        // KR: userInfo가 존재하고 memberId가 있는지 확인합니다.
        if (userInfo && userInfo.memberId) {
          setMemberId(userInfo.memberId);
        } else {
          toast.error("로그인이 필요합니다.");
          setTimeout(() => {
            navigate("/login");
          }, 2500);
        }
      } catch (error) {
        console.error("사용자 정보를 가져오는 중 오류:", error);
        toast.error("사용자 정보를 확인할 수 없습니다.");
        setTimeout(() => {
          navigate("/login");
        }, 2500);
      }
    };

    checkLoggedIn();
  }, [navigate]);

  // ---------------- 기타 유틸리티 함수 ----------------
  const validatePhoneNumber = (phone) => {
    // KR: 전화번호가 숫자와 하이픈만 포함하는지 확인하는 정규식
    const phoneRegex = /^[0-9-]+$/;
    return phoneRegex.test(phone);
  };

  const handleSavePhone = (value) => {
    if (!validatePhoneNumber(value)) {
      alert("전화번호는 숫자와 하이픈만 포함할 수 있습니다.");
      return;
    }
    updateProfileField("phone", value);
  };

  const handleDeleteClick = () => {
    setShowDeleteModal(true);
  };

  const confirmDelete = async () => {
    console.log(`회원 Id값 : ${profileInfo.memberId}`);
    try {
      await AxiosInstance.delete(`/api/members/${profileInfo.memberId}`);
      alert("회원 탈퇴가 완료되었습니다.");
      logout();
      navigate("/");
    } catch (error) {
      console.error("회원 탈퇴 실패:", error);
      alert("회원 탈퇴 중 오류가 발생했습니다.");
    } finally {
      setShowDeleteModal(false);
    }
  };

  if (loading) {
    return <div>Loading...</div>;
  }

  // ---------------- 프로필 화면 렌더링 ----------------
  return (
    <div className="layout-container">
      {/* 좌측 섹션: 프로필 및 소개 */}
      <div className="profile-left">
        <div className="profile-image-container">
          <div className="profile-image-wrapper">
            <img
              src={profileImage}
              alt="프로필 이미지"
              className="profile-image"
            />
            <div className="my-page-button-container">
              <button
                className="my-page-button"
                onClick={() => navigate("/profile/mypage")}
              >
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
              ref={fileInputRef}
              className="hidden-input"
              onChange={handleImageChange}
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
          onSave={(value) => updateProfileField("phone", value)}
        />
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

        {/* 회원 탈퇴 버튼 */}
        <button onClick={handleDeleteClick} className="delete-button">
          회원 탈퇴
        </button>

        {/* 회원 탈퇴 확인 모달 */}
        <Modal03
          open={showDeleteModal}
          confirm={confirmDelete}
          close={() => setShowDeleteModal(false)}
          type="confirm"
          header="회원 탈퇴 확인"
        >
          정말 탈퇴하시겠습니까? 이 작업은 되돌릴 수 없습니다.
        </Modal03>
      </div>

      {/* 우측 섹션: 친구 추천 */}
      <div className="profile-right friends-section">
        <h2>친구 추천</h2>
        <ul className="friend-list">
          {friendList.map((friend) => (
            <li key={friend.memberId} className="friend-item">
              <div className="friend-info">
                <img
                  src={friend.profilePictureUrl || imgLogo2}
                  alt="친구 이미지"
                  className="friend-image"
                />
                <div className="friend-details">
                  <span>{friend.name}</span>
                  <span className="friend-role">
                    {friend.currentCompany || "미등록 회사"}
                  </span>
                </div>
              </div>
              <div className="friend-actions">
                {!friend.isFriend && !friend.requestSent ? (
                  <button
                    className="friend-request-button"
                    onClick={() => handleSendRequest(friend.memberId)}
                  >
                    친구 요청
                  </button>
                ) : friend.requestSent ? (
                  <button
                    className="friend-request-button"
                    onClick={() => handleCancelRequest(friend.memberId)}
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
