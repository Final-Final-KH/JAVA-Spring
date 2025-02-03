import React, { useEffect, useState } from "react";

// 더미 데이터
const dummyCourses = [
  {
    name: "프로그래밍 기초",
    program: "프론트엔드",
    status: "모집중",
    cost: "무료",
    mode: "온라인",
    duration: "3개월",
    time: "주 3회, 3시간",
  },
  {
    name: "데이터 분석",
    program: "백엔드",
    status: "모집중",
    cost: "500,000원",
    mode: "오프라인",
    duration: "4개월",
    time: "주 2회, 4시간",
  },
  {
    name: "AI 기초",
    program: "AI/ML",
    status: "모집중",
    cost: "300,000원",
    mode: "혼합형",
    duration: "2개월",
    time: "주 2회, 2시간",
  },
  {
    name: "AI 기초",
    program: "AI/ML",
    status: "모집중",
    cost: "300,000원",
    mode: "혼합형",
    duration: "2개월",
    time: "주 2회, 2시간",
  },
  {
    name: "AI 기초",
    program: "AI/ML",
    status: "모집중",
    cost: "300,000원",
    mode: "혼합형",
    duration: "2개월",
    time: "주 2회, 2시간",
  },
  {
    name: "AI 기초",
    program: "AI/ML",
    status: "모집중",
    cost: "300,000원",
    mode: "혼합형",
    duration: "2개월",
    time: "주 2회, 2시간",
  },
  {
    name: "AI 기초",
    program: "AI/ML",
    status: "모집중",
    cost: "300,000원",
    mode: "혼합형",
    duration: "2개월",
    time: "주 2회, 2시간",
  },
  {
    name: "AI 기초",
    program: "AI/ML",
    status: "모집중",
    cost: "300,000원",
    mode: "혼합형",
    duration: "2개월",
    time: "주 2회, 2시간",
  },
];

const EduList = () => {
  const [courses, setCourses] = useState([]); // 기본값을 빈 배열로 설정

  useEffect(() => {
    // 실제 API 요청 대신 더미 데이터로 대체
    setCourses(dummyCourses); // 데이터를 courses 상태에 설정
  }, []);

  return (
    <div className="edu-list-container">
      <h1>교육 과정 목록</h1>
      <div className="table-header">
        <div className="table-row">
          <span>교육과정명</span>
          <span>프로그램 과정</span>
          <span>모집상태</span>
          <span>비용</span>
          <span>온·오프라인</span>
          <span>학습기간</span>
          <span>참여시간</span>
        </div>
      </div>
      <div className="table-body">
        {courses.length === 0 ? (
          <div className="no-data-message">등록된 교육 과정이 없습니다.</div> // 데이터가 없으면 표시되는 메시지
        ) : (
          courses.map((course, index) => (
            <div className="table-row" key={index}>
              <span>{course.name}</span>
              <span>{course.program}</span>
              <span>{course.status}</span>
              <span>{course.cost}</span>
              <span>{course.mode}</span>
              <span>{course.duration}</span>
              <span>{course.time}</span>
            </div>
          ))
        )}
      </div>
      <style jsx>{`
        .edu-list-container {
          margin-top: 90px;
          width: 100%;
          max-width: 1200px;
          margin-left: auto; /* 왼쪽 여백을 자동으로 설정하여 가운데 정렬 */
          margin-right: auto; /* 오른쪽 여백을 자동으로 설정하여 가운데 정렬 */
          padding: 30px;
          background: white;
          border-radius: 10px;
          box-shadow: 0 6px 12px rgba(0, 0, 0, 0.1);
        }

        h1 {
          text-align: center;
          font-size: 2.5rem;
          color: #333;
          margin-bottom: 30px;
          font-weight: bold;
        }

        .table-header {
          background-color: #e0e0e0;
          padding: 0px 0;
          border-radius: 8px 8px 0 0;
          box-shadow: 0 6px 12px rgba(0, 0, 0, 0.1);
        }

        .table-body {
          background-color: white;
          box-shadow: 0 6px 12px rgba(0, 0, 0, 0.1);
        }

        .table-row {
          display: grid;
          grid-template-columns: repeat(7, 1fr);
          gap: 20px;
          padding: 15px 10px;

          align-items: center;
          box-shadow: 0 6px 12px rgba(0, 0, 0, 0.1);
        }

        .table-row span {
          text-align: center;
          font-size: 15px;
          font-weight: 500;
          color: #333;
        }

        .table-row:nth-child(odd) {
          // 홀수
          background-color: #f8f8f8;
        }

        .table-row:last-child {
          border-bottom: none;
        }

        .table-row:hover {
          background-color: #e0e0e0;
          cursor: pointer;
        }

        .no-data-message {
          text-align: center;
          font-size: 1.2rem;
          color: #777;
          margin-top: 30px;
        }
      `}</style>
    </div>
  );
};

export default EduList;
