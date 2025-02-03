import React, { useEffect, useState } from "react";
import imgLogo1 from "../images/DeveloperMark.jpg";
import imgLogo2 from "../images/EditButton.webp";
import imgLogo3 from "../images/SaveButton.png";
import AxiosApi2 from "../api/AxiosApi2";
import { Button3, Content } from "./kedu/course/style/style2";
import NaverPayButton from "./kedu/etc/NaverPay";
import KakaoPayButton from "./kedu/etc/KaKaoPay";
import PaymentPage from "./kedu/etc/TossPay";
import Modal from "./kedu/etc/Modal2";
import Grass from "./kedu/etc/Grass";

const MyPage = () => {
  const [schoolInfo, setSchoolInfo] = useState("학원 정보 또는 기타 관련 내용");

  const [courses, setCourses] = useState([
    { id: 1, title: "React 기초 강의", progress: "50%" },
  ]);

  const [feeds, setfeeds] = useState([
    { id: 1, title: "피드 1", content: "내가 쓴 피드 내용" },
    { id: 2, title: "피드 2", content: "또 다른 피드 내용" },
  ]);
  const [schoolComments, setSchoolComments] = useState([
    { id: 1, content: "학원 코멘트 1" },
    { id: 2, content: "학원 코멘트 2" },
  ]);
  const [lectureComments, setLectureComments] = useState([
    { id: 1, content: "강의 코멘트 1" },
    { id: 2, content: "강의 코멘트 2" },
  ]);
  const [workDiary, setWorkDiary] = useState([
    { id: 1, date: "2025. 1. 17. 오후 3:39:40", content: "첫 작업일지" },
  ]); // 초기 작업일지

  const [editMode, setEditMode] = useState({}); // 수정 모드 상태
  const [editSchoolMode, setEditSchoolMode] = useState(false); // 학원 정보 수정 모드
  const [editCourseMode, setEditCourseMode] = useState({}); // 강의 수정 모드
  const [newCourseTitle, setNewCourseTitle] = useState("");
  const [newCourseProgress, setNewCourseProgress] = useState("");

  const [newDiaryContent, setNewDiaryContent] = useState(""); // 새 작업일지 내용

  // 수정 모드 토글
  const handleDiaryEdit = (id) => {
    setEditMode((prev) => ({ ...prev, [id]: true }));
  };

  const handleDiarySave = (id) => {
    setEditMode((prev) => ({ ...prev, [id]: false }));
  };

  const handleSchoolEdit = () => {
    setEditSchoolMode(true);
  };

  const handleSchoolSave = () => {
    setEditSchoolMode(false);
  };

  const handleCourseEdit = (id) => {
    setEditCourseMode((prev) => ({ ...prev, [id]: true }));
  };

  const handleCourseSave = (id) => {
    setEditCourseMode((prev) => ({ ...prev, [id]: false }));
  };

  // 작업일지 내용 변경
  const handleDiaryChange = (id, value) => {
    setWorkDiary((prev) =>
      prev.map((entry) =>
        entry.id === id ? { ...entry, content: value } : entry
      )
    );
  };

  // 학원 정보 변경
  const handleSchoolChange = (value) => {
    setSchoolInfo(value);
  };

  // 강의 내용 변경
  const handleCourseChange = (id, field, value) => {
    setCourses((prev) =>
      prev.map((course) =>
        course.id === id ? { ...course, [field]: value } : course
      )
    );
  };

  const handleAddCourse = () => {
    if (newCourseTitle.trim() === "" || newCourseProgress.trim() === "") {
      alert("강의 제목과 진행률을 입력해주세요.");
      return;
    }

    const course = {
      id: Date.now(),
      title: newCourseTitle,
      progress: `${newCourseProgress}%`, // 진행률에 % 추가
    };

    setCourses((prevCourses) => [...prevCourses, course]);
    setNewCourseTitle("");
    setNewCourseProgress("");
  };

  // 새 작업일지 추가
  const handleAddDiary = () => {
    if (newDiaryContent.trim() === "") return;
    const newEntry = {
      id: Date.now(), // 고유 ID
      date: new Date().toLocaleString(), //  오늘 날짜 초 단위까지 나옴
      content: newDiaryContent,
    };
    setWorkDiary((prev) => [...prev, newEntry]);
    setNewDiaryContent(""); // 입력창 초기화
  };

  const [academy, setAcademy] = useState([]);
  const [member, setMember] = useState(null);
  const [course, setCourse] = useState([]);
  const [academyComment, setAcademyComment] = useState([]);
  const [courseComment, setCourseComment] = useState([]);
  const [shortComment, setShortComment] = useState([]);
  const [survey, setSurvey] = useState([]);
  const [modalState, setModalState] = useState(false);
  useEffect(() => {
    const fetchData = async () => {
      const memberData = localStorage.getItem("keduMember");
      if (memberData) {
        const parsedMember = JSON.parse(memberData);
        setMember(parsedMember);
      }
    };
    fetchData();
  }, []);

  useEffect(() => {
    if (member && member.memberId) {
      MyAcademy();
      MyCourse();
      MyAcademyComment();
      MyCourseComment();
    }
  }, [member]); // member가 변경될 때마다 MyAcademy 호출

  const MyAcademy = async () => {
    if (member && member.memberId) {
      try {
        const rsp = await AxiosApi2.myAcademy(member.memberId);
        setAcademy(rsp.data.list);
      } catch (error) {
        console.error("Failed to fetch academy data:", error);
      }
    }
  };

  const MyCourse = async () => {
    const rsp = await AxiosApi2.myCourse(member.memberId);
    setCourse(rsp.data.list);
  };
  const MyAcademyComment = async () => {
    const rsp = await AxiosApi2.myAcademyComment(member.memberId);
    setAcademyComment(rsp.data.list[0]);
  };

  const MyCourseComment = async () => {
    const rsp = await AxiosApi2.MyCourseComment(member.memberId);
    setCourseComment(rsp.data.list[0]);
  };
  const PayButton = () => {
    setModalState(true);
  };
  const closeModal = () => {
    setModalState(false);
  };
  return (
    <Content>
      <div className="mypage-container">
        {/* Left Section from Feed */}
        <div className="profile-section">
          <img src={imgLogo1} alt="프로필 이미지" className="profile-image" />
          <h2>User Name</h2>
          <p>user@email.com</p>
          <p>User Age</p>
          <p>User Location</p>
          <NaverPayButton></NaverPayButton>
          <Button3 onClick={PayButton}>정기구독</Button3>
        </div>

        {/* Right Section */}
        <div className="container mt-4">
          <div className="row">
            {/* My School */}
            <div className="col-md-6">
              <div className="section-container card p-3 mb-4">
                <h2>나의 학원</h2>
                <div>
                  <table className="table table-striped">
                    <tbody>
                      {academy ? (
                        academy.map((academy) => (
                          <tr key={academy.academy_id}>
                            <td>{academy.academy}</td>
                          </tr>
                        ))
                      ) : (
                        <tr>
                          <td>등록된 학원이 없습니다.</td>
                        </tr>
                      )}
                    </tbody>
                  </table>
                </div>
              </div>
            </div>

            {/* My Courses */}
            <div className="col-md-6">
              <div className="section-container card p-3 mb-4">
                <h2>내가 수강한 강의</h2>
                <div>
                  <table className="table table-striped">
                    <tbody>
                      {course ? (
                        course.map((course) => (
                          <tr key={course.list_id}>
                            <td>{course.academy}</td>
                            <td>{course.course}</td>
                          </tr>
                        ))
                      ) : (
                        <tr>
                          <td>등록한 강의가 없습니다.</td>
                        </tr>
                      )}
                    </tbody>
                  </table>
                </div>
              </div>
            </div>
          </div>

          {/* My Feeds */}
          <div className="section-container card p-3 mb-4">
            <h2>내 피드</h2>
            <ul className="list-group">
              {feeds.map((feed) => (
                <li key={feed.id} className="list-group-item">
                  <strong>{feed.title}</strong>
                  <p>{feed.content}</p>
                </li>
              ))}
            </ul>
          </div>

          {/* School Comments */}
          <div className="container mt-4">
            <div className="row">
              {/* School Comments */}
              <div className="col-md-6">
                <div className="section-container card p-3 mb-4">
                  <h3>학원 리뷰</h3>
                  <div>
                    {academyComment ? (
                      <div>
                        <div>
                          <p>
                            취업 도움:{" "}
                            {academyComment.avgJob
                              ? academyComment.avgJob.toFixed(2)
                              : "N/A"}
                          </p>
                          <p>
                            강의 만족도:{" "}
                            {academyComment.avgLecture
                              ? academyComment.avgLecture.toFixed(2)
                              : "N/A"}
                          </p>
                          <p>
                            시설 만족도:{" "}
                            {academyComment.avgFacilities
                              ? academyComment.avgFacilities.toFixed(2)
                              : "N/A"}
                          </p>
                          <p>
                            강사 평가:{" "}
                            {academyComment.avgTeacher
                              ? academyComment.avgTeacher.toFixed(2)
                              : "N/A"}
                          </p>
                          <p>
                            교재 만족도:{" "}
                            {academyComment.avgBooks
                              ? academyComment.avgBooks.toFixed(2)
                              : "N/A"}
                          </p>
                          <p>
                            취업 지원:{" "}
                            {academyComment.avgService
                              ? academyComment.avgService.toFixed(2)
                              : "N/A"}
                          </p>
                        </div>
                      </div>
                    ) : (
                      <p>로딩 중...</p>
                    )}
                  </div>
                </div>
              </div>

              {/* Lecture Comments */}
              <div className="col-md-6">
                <div className="section-container card p-3 mb-4">
                  <h3>강의 리뷰</h3>
                  <div>
                    {courseComment ? (
                      <div>
                        <div>
                          <p>
                            취업 도움:{" "}
                            {courseComment.job !== undefined
                              ? courseComment.job.toFixed(2)
                              : "N/A"}
                          </p>
                          <p>
                            강의 만족도:{" "}
                            {courseComment.lecture !== undefined
                              ? courseComment.lecture.toFixed(2)
                              : "N/A"}
                          </p>
                          <p>
                            강사 만족도:{" "}
                            {courseComment.teacher !== undefined
                              ? courseComment.teacher.toFixed(2)
                              : "N/A"}
                          </p>
                          <p>
                            교재 만족도:{" "}
                            {courseComment.books !== undefined
                              ? courseComment.books.toFixed(2)
                              : "N/A"}
                          </p>
                          <p>
                            기술 만족도:{" "}
                            {courseComment.newTech !== undefined
                              ? courseComment.newTech.toFixed(2)
                              : "N/A"}
                          </p>
                          <p>
                            학업 만족도:{" "}
                            {courseComment.skillUp !== undefined
                              ? courseComment.skillUp.toFixed(2)
                              : "N/A"}
                          </p>
                        </div>
                      </div>
                    ) : (
                      <p>로딩 중...</p>
                    )}
                  </div>
                </div>
              </div>
            </div>
          </div>

          {/* Work Diary */}
          <div className="section-container">
            <h2>나의 작업일지</h2>
            <ul>
              {workDiary.map((entry) =>
                editMode[entry.id] ? (
                  <li key={entry.id}>
                    <strong>{entry.date}</strong>
                    <textarea
                      value={entry.content}
                      onChange={(e) =>
                        handleDiaryChange(entry.id, e.target.value)
                      }
                      className="edit-input"
                    />
                    <button
                      className="save-button"
                      onClick={() => handleDiarySave(entry.id)}
                    >
                      <img
                        src={imgLogo3}
                        alt="저장"
                        style={{ width: "20px", height: "20px" }}
                      />
                    </button>
                  </li>
                ) : (
                  <li key={entry.id}>
                    <strong>{entry.date}</strong>
                    <p>{entry.content}</p>
                    <button
                      className="edit-button"
                      onClick={() => handleDiaryEdit(entry.id)}
                    >
                      <img
                        src={imgLogo2}
                        alt="수정"
                        style={{ width: "20px", height: "20px" }}
                      />
                    </button>
                  </li>
                )
              )}
            </ul>
            <div className="add-diary-container">
              <h3>작업일지 추가</h3>
              <textarea
                value={newDiaryContent}
                onChange={(e) => setNewDiaryContent(e.target.value)}
                placeholder="작업 내용을 작성하세요."
                className="new-diary-input"
              />
              <button className="add-button" onClick={handleAddDiary}>
                추가
              </button>
              <Grass></Grass>
            </div>
          </div>
        </div>
        <Modal open={modalState} close={closeModal} type={true}></Modal>
        {/* Styles */}
        <style jsx>{`
          .mypage-container {
            display: grid;
            grid-template-columns: 1fr 3fr;
            gap: 20px;
            padding: 20px;
            background-color: #f5f6f7;
            min-height: 100vh;
          }
          .profile-section {
            position: sticky;
            top: 90px; /* 네비게이션 바 높이만큼 내려오게 설정 */
            align-self: start;
            max-height: calc(
              100vh - 90px
            ); /* 뷰포트 높이에서 네비게이션 바 높이 제외 */
            overflow-y: auto;
            background: white;
            padding: 20px;
            border-radius: 8px;
            box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
            text-align: center;
          }

          .profile-image {
            width: 150px;
            height: 150px;
            border-radius: 50%;
            margin-bottom: 15px;
          }

          .right-section {
            margin-top: 70px;
            display: flex;
            flex-direction: column;
            gap: 20px;
          }

          .section-container {
            background: white;
            padding: 20px;
            border-radius: 8px;
            box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
          }

          .section-container h2 {
            margin-bottom: 10px;
          }

          .section-container ul {
            list-style: none;
            padding: 0;
          }

          .section-container ul li {
            margin-bottom: 10px;
          }

          .edit-input,
          .new-diary-input {
            width: 100%;
            padding: 8px;
            margin: 10px 0;
            font-size: 14px;
            border: 1px solid #ddd;
            border-radius: 4px;
            resize: none;
          }

          .edit-button,
          .save-button {
            background: none;
            border: none;
            cursor: pointer;
            padding: 5px;
          }

          .add-course-container {
            margin-top: 16px;
          }

          .input-group {
            display: flex;
            align-items: center; /* 세로 가운데 정렬 */
          }

          .course-add-button {
            margin-left: 8px;
            padding: 4px 8px;
            background-color: #4caf50;
            color: white;
            border: none;
            border-radius: 4px;
            cursor: pointer;
          }

          .course-add-button:hover {
            background-color: #45a049;
          }

          .add-button {
            width: 100%;
            display: inline-block;
            margin: 5px;
            padding: 5px 10px;
            background-color: #4caf50;
            color: #fff;
            border: none;
            cursor: pointer;
            border-radius: 4px;
            font-size: 14px;
          }

          .add-button:hover {
            background-color: #45a049;
          }

          .edit-button img,
          .save-button img {
            vertical-align: middle;
            width: 20px;
            height: 20px;
            transition: transform 0.2s ease;
          }

          .edit-button:hover img,
          .save-button:hover img {
            transform: scale(1.1); /* 확대 효과 */
          }

          .add-diary-container {
            margin-top: 20px;
            display: flex;
            flex-direction: column;
            gap: 10px;
          }

          .add-diary-container h3 {
            margin-bottom: 5px;
          }
        `}</style>
      </div>
    </Content>
  );
};

export default MyPage;
