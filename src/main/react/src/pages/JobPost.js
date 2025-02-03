import React, { useEffect } from "react";
import { Link } from "react-router-dom";
import imgLogo1 from "../images/JobPost001.png";
import imgLogo2 from "../images/JobPost002.png";
import imgLogo3 from "../images/JobPost003.png";
import imgLogo4 from "../images/JobPost004.png";
import imgLogo5 from "../images/JobPost005.png";
import imgLogo6 from "../images/JobPost006.png";
import imgLogo7 from "../images/JobPost007.png";
import imgLogo8 from "../images/JobPost008.png";
import imgLogo9 from "../images/JobPost009.png";
import imgLogo10 from "../images/JobPost010.png";
import imgLogo11 from "../images/JobPost011.png";
import imgLogo12 from "../images/JobPost012.png";

// 예시 구직 공고 데이터
const jobData = [
  {
    id: 1,
    title: "프론트엔드 개발자",
    company: "ABC 테크",
    location: "서울 강남구",
    salary: "연봉 4,000만원 ~ 5,000만원",
    thumbnail: imgLogo1, // 이미지 URL
  },
  {
    id: 2,
    title: "백엔드 개발자",
    company: "XYZ 솔루션",
    location: "부산 해운대구",
    salary: "연봉 5,000만원 ~ 6,000만원",
    thumbnail: imgLogo2,
  },
  {
    id: 3,
    title: "데이터 분석가",
    company: "Data Minds",
    location: "대구 중구",
    salary: "연봉 4,500만원 ~ 5,500만원",
    thumbnail: imgLogo3,
  },
  {
    id: 4,
    title: "UI/UX 디자이너",
    company: "Creative Studio",
    location: "서울 마포구",
    salary: "연봉 3,500만원 ~ 4,500만원",
    thumbnail: imgLogo4,
  },
  {
    id: 5,
    title: "AI 엔지니어",
    company: "AI Labs",
    location: "판교",
    salary: "연봉 7,000만원 이상",
    thumbnail: imgLogo5,
  },
  {
    id: 6,
    title: "풀스택 개발자",
    company: "DevWorks",
    location: "서울 서초구",
    salary: "연봉 6,000만원 이상",
    thumbnail: imgLogo6,
  },
  {
    id: 7,
    title: "시스템 관리자",
    company: "Infra Corp",
    location: "인천",
    salary: "연봉 4,200만원 ~ 5,200만원",
    thumbnail: imgLogo7,
  },
  {
    id: 8,
    title: "프로젝트 매니저",
    company: "PMAgency",
    location: "서울 송파구",
    salary: "연봉 5,500만원 이상",
    thumbnail: imgLogo8,
  },
  {
    id: 9,
    title: "게임 개발자",
    company: "Game Studio",
    location: "서울 강서구",
    salary: "연봉 5,000만원 ~ 6,500만원",
    thumbnail: imgLogo9,
  },
  {
    id: 10,
    title: "디지털 마케팅 전문가",
    company: "MarketPro",
    location: "부산 동래구",
    salary: "연봉 4,200만원 ~ 5,500만원",
    thumbnail: imgLogo10,
  },
  {
    id: 11,
    title: "소프트웨어 엔지니어",
    company: "Tech Innovators",
    location: "서울 강동구",
    salary: "연봉 6,000만원 이상",
    thumbnail: imgLogo11,
  },
  {
    id: 12,
    title: "네트워크 관리자",
    company: "NetGuard",
    location: "서울 성동구",
    salary: "연봉 4,000만원 ~ 5,000만원",
    thumbnail: imgLogo12,
  },
];

function JobPost() {
  useEffect(() => {
    // 페이지 로드 시 body의 배경 색상을 설정
    document.body.style.backgroundColor = "#f5f6f7";
  });

  return (
    <div style={styles.container}>
      <div style={styles.grid}>
        {jobData.map((job) => (
          <div key={job.id} style={styles.card}>
            <Link to={`/job/${job.id}`} style={{ textDecoration: "none" }}>
              <img
                src={job.thumbnail}
                alt={`${job.title} 썸네일`}
                style={styles.thumbnail}
              />
              <h2 style={styles.title}>{job.title}</h2>
              <p style={styles.company}>{job.company}</p>
              <p style={styles.location}>{job.location}</p>
              <p style={styles.salary}>{job.salary}</p>
            </Link>
          </div>
        ))}
      </div>
    </div>
  );
}

const styles = {
  container: {
    padding: "20px",
    fontFamily: "'Arial', sans-serif",
    textAlign: "center",
    marginTop: "50px",
    backgroundColor: "#f5f6f7",
  },
  heading: {
    fontSize: "24px",
    fontWeight: "bold",
    marginBottom: "20px",
  },
  grid: {
    display: "grid",
    gridTemplateColumns: "repeat(4, 1fr)", // 기본 4열
    gap: "20px",
    justifyContent: "center",
    marginTop: "20px",
  },
  card: {
    border: "1px solid #ddd",
    borderRadius: "8px",
    padding: "15px",
    boxShadow: "0 4px 6px rgba(0, 0, 0, 0.1)",
    transition: "transform 0.2s, box-shadow 0.2s",
    backgroundColor: "#fff",
    cursor: "pointer",
  },
  thumbnail: {
    width: "100%",
    height: "200px",
    objectFit: "cover",
    borderRadius: "4px",
    marginBottom: "10px",
  },
  title: {
    fontSize: "18px",
    fontWeight: "bold",
    margin: "10px 0 5px",
  },
  company: {
    fontSize: "14px",
    color: "#555",
    margin: "0 0 5px",
  },
  location: {
    fontSize: "14px",
    color: "#777",
    margin: "0 0 5px",
  },
  salary: {
    fontSize: "14px",
    color: "#009688",
    fontWeight: "bold",
  },
};

export default JobPost;
