import { useContext, useEffect, useRef, useState } from "react";
import { RegionSearchContext } from "../../../api/provider/RegionSearchContextProvider2";
import { useParams } from "react-router-dom";
import "react-toastify/dist/ReactToastify.css"; // Toast 스타일을 import 합니다
import "../css/kedu2.css";
import RegAcademyReviewModal from "./modal/RegAcademyReview2";
import RegCourseReviewModal from "./modal/RegCourseReview2";
import RegSurveyModal from "./modal/RegSurvey2";
import CommentModal from "./modal/Comment2";
import RegMyCourse from "./component/regMyCourse2";
import RegMyAcademy from "./component/regMyAcademy2";
import ShortComment from "./component/shortComment2";
import AcademyComment from "./component/academyComment2";
import CourseComment from "./component/courseComment2";
import Survey from "./component/survey2";
import styled from "styled-components";
import { Content } from "./style/style2";

const Lecture = () => {
  const { searchKeyword, academyName } = useContext(RegionSearchContext);
  const [activeTab, setActiveTab] = useState("lecture"); // 기본적으로 강의 탭이 활성화된 상태로 설정

  const [academyModal, setAcademyModal] = useState(false);
  const [courseModal, setCourseModal] = useState(false);
  const [surveyModal, setSurveyModal] = useState(false);
  const [commentModal, setCommentModal] = useState(false);

  const academyNameRef = useRef("");
  const regionRef = useRef("");

  useEffect(() => {
    academyNameRef.current = localStorage.getItem("academyName");
    regionRef.current = localStorage.getItem("region");
  }, [searchKeyword, academyName]);

  const closeModal = () => {
    setAcademyModal(false);
    setCourseModal(false);
    setSurveyModal(false);
    setCommentModal(false);
  };

  const handleTabClick = (tab) => {
    setActiveTab(tab);
  };

  const AcademyModalState = () => setAcademyModal(true);
  const CourseModalState = () => setCourseModal(true);
  const CommentModalState = () => setCommentModal(true);
  const SurveyModalState = () => setSurveyModal(true);

  return (
    <Content>
      <div
        className="content"
        style={{
          padding: "20px",
          maxWidth: "1200px",
          margin: "0 auto",
        }}
      >
        <h1>{academyNameRef.current}</h1>

        {/* 카드 스타일로 변경된 탭 메뉴 */}
        <div
          style={{
            display: "flex",
            flexWrap: "wrap",
            justifyContent: "space-between",
            marginBottom: "20px",
          }}
        >
          {[
            "academyComment",
            "lectureComment",
            "board",
            "survey",
            "registerAcademy",
            "registerLecture",
          ].map((tab) => (
            <div
              key={tab}
              style={{
                width: "48%",
                marginBottom: "20px",
                padding: "15px",
                backgroundColor: "#fff",
                border: activeTab === tab ? "2px solid #000" : "1px solid #ddd",
                borderRadius: "10px",
                boxShadow: "0 4px 6px rgba(0,0,0,0.1)",
                cursor: "pointer",
                transition: "all 0.3s",
              }}
              onClick={() => handleTabClick(tab)}
            >
              <h4
                style={{
                  textAlign: "center",
                  fontSize: "18px",
                  fontWeight: activeTab === tab ? "bold" : "normal",
                  color: activeTab === tab ? "#000" : "#555",
                }}
              >
                {tab === "academyComment" && "학원 리뷰"}
                {tab === "lectureComment" && "강의 리뷰"}
                {tab === "board" && "한 줄 코멘트"}
                {tab === "survey" && "설문조사"}
                {tab === "registerAcademy" && "나의 학원 등록하기"}
                {tab === "registerLecture" && "내가 수강한 강의 등록하기"}
              </h4>
            </div>
          ))}
        </div>

        {/* 탭 내용 렌더링 */}
        {activeTab === "academyComment" && (
          <>
            <div>
              <button onClick={() => AcademyModalState()} style={buttonStyle}>
                학원 리뷰 등록하기
              </button>
            </div>
            <div>
              <AcademyComment />
            </div>
          </>
        )}

        {activeTab === "lectureComment" && (
          <>
            <div>
              <button onClick={() => CourseModalState()} style={buttonStyle}>
                강의 리뷰 등록하기
              </button>
            </div>
            <div>
              <CourseComment />
            </div>
          </>
        )}

        {activeTab === "board" && (
          <>
            <div>
              <button onClick={() => CommentModalState()} style={buttonStyle}>
                한 줄 코멘트 등록하기
              </button>
            </div>
            <div>
              <ShortComment />
            </div>
          </>
        )}

        {activeTab === "survey" && (
          <>
            <div>
              <button onClick={() => SurveyModalState()} style={buttonStyle}>
                설문조사 등록하기
              </button>
            </div>
            <Survey />
          </>
        )}

        {activeTab === "registerAcademy" && <RegMyAcademy />}
        {activeTab === "registerLecture" && <RegMyCourse />}
      </div>

      <RegAcademyReviewModal
        open={academyModal}
        close={closeModal}
        type={true}
      />
      <RegCourseReviewModal open={courseModal} close={closeModal} type={true} />
      <RegSurveyModal open={surveyModal} close={closeModal} type={true} />
      <CommentModal open={commentModal} close={closeModal} type={true} />
    </Content>
  );
};

const buttonStyle = {
  padding: "10px 20px",
  backgroundColor: "#4caf50",
  color: "white",
  border: "none",
  borderRadius: "5px",
  cursor: "pointer",
  fontSize: "16px",
  marginBottom: "20px",
  transition: "background-color 0.3s",
};

export default Lecture;
