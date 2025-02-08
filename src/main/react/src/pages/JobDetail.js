import React, { useEffect, useState } from "react";
import { useParams } from "react-router-dom";

// 예시 구직 공고 데이터
const jobData = [
  {
    id: 1,
    title: "프론트엔드 개발자",
    company: "ABC 테크",
    location: "서울 강남구",
    salary: "연봉 4,000만원 ~ 5,000만원",
    description:
      "프론트엔드 개발자로서 다양한 웹 어플리케이션을 개발하는 역할입니다.",
    companyInfo:
      "ABC 테크는 혁신적인 기술을 바탕으로 글로벌 시장에 진출한 선도적인 기업입니다.",
    recruitingInfo: "프론트엔드 개발자 모집, React.js, JavaScript 등 사용",
    companyLocation: "서울 강남구 테헤란로 123",
    companyLatLng: { lat: 37.5079, lng: 127.0369 },
  },
  {
    id: 2,
    title: "백엔드 개발자",
    company: "XYZ 솔루션",
    location: "부산 해운대구",
    salary: "연봉 5,000만원 ~ 6,000만원",
    description:
      "백엔드 서버 개발 및 유지보수, 데이터베이스 관리 등의 역할을 맡습니다.",
    companyInfo:
      "XYZ 솔루션은 데이터 처리에 강점을 가진 혁신적인 스타트업입니다.",
    recruitingInfo: "백엔드 개발자 모집, Node.js, MongoDB 등 사용",
    companyLocation: "부산 해운대구 해운대해변로 45",
    companyLatLng: { lat: 35.1569, lng: 129.132 },
  },
  {
    id: 3,
    title: "데이터 분석가",
    company: "Data Minds",
    location: "대구 중구",
    salary: "연봉 4,500만원 ~ 5,500만원",
    description: "데이터 분석 및 통찰력을 제공하여 비즈니스 문제를 해결합니다.",
    companyInfo:
      "Data Minds는 데이터 분석과 머신러닝을 전문으로 하는 회사입니다.",
    recruitingInfo: "데이터 분석가 모집, Python, R, SQL 사용",
    companyLocation: "대구 중구 중앙대로 200",
    companyLatLng: { lat: 35.8684, lng: 128.6006 },
  },
  {
    id: 4,
    title: "UI/UX 디자이너",
    company: "Creative Studio",
    location: "서울 마포구",
    salary: "연봉 3,500만원 ~ 4,500만원",
    description:
      "사용자 경험을 향상시키기 위한 UI/UX 디자인 업무를 담당합니다.",
    companyInfo:
      "Creative Studio는 사용자 중심 디자인으로 유명한 디자인 전문 회사입니다.",
    recruitingInfo: "UI/UX 디자이너 모집, Figma, Adobe XD 사용",
    companyLocation: "서울 마포구 양화로 21",
    companyLatLng: { lat: 37.5492, lng: 126.9138 },
  },
  {
    id: 5,
    title: "AI 엔지니어",
    company: "AI Labs",
    location: "판교",
    salary: "연봉 7,000만원 이상",
    description: "AI 모델 개발 및 최적화를 수행합니다.",
    companyInfo:
      "AI Labs는 인공지능 연구 및 개발을 전문으로 하는 글로벌 회사입니다.",
    recruitingInfo: "AI 엔지니어 모집, TensorFlow, PyTorch 사용",
    companyLocation: "경기도 성남시 판교로 123",
    companyLatLng: { lat: 37.3943, lng: 127.1107 },
  },
  {
    id: 6,
    title: "풀스택 개발자",
    company: "DevWorks",
    location: "서울 서초구",
    salary: "연봉 6,000만원 이상",
    description:
      "프론트엔드와 백엔드를 모두 다루는 풀스택 개발 업무를 맡습니다.",
    companyInfo:
      "DevWorks는 최신 기술을 활용한 웹 및 모바일 솔루션을 제공합니다.",
    recruitingInfo: "풀스택 개발자 모집, MERN 스택 사용",
    companyLocation: "서울 서초구 서초대로 45",
    companyLatLng: { lat: 37.5043, lng: 127.0241 },
  },
  {
    id: 7,
    title: "시스템 관리자",
    company: "Infra Corp",
    location: "인천",
    salary: "연봉 4,200만원 ~ 5,200만원",
    description:
      "시스템 구축 및 네트워크 관리, IT 인프라 유지보수 업무를 맡습니다.",
    companyInfo:
      "Infra Corp는 안정적인 IT 서비스를 제공하는 기업으로, 고객 만족을 우선합니다.",
    recruitingInfo: "시스템 관리자 모집, Linux, AWS, Docker 사용",
    companyLocation: "인천 연수구 송도대로 12",
    companyLatLng: { lat: 37.3902, lng: 126.6521 },
  },
  {
    id: 8,
    title: "프로젝트 매니저",
    company: "PMAgency",
    location: "서울 송파구",
    salary: "연봉 5,500만원 이상",
    description:
      "프로젝트 일정 및 자원을 관리하며 팀의 목표 달성을 지원합니다.",
    companyInfo:
      "PMAgency는 체계적인 프로젝트 관리와 성과 중심의 접근 방식을 지향합니다.",
    recruitingInfo: "프로젝트 매니저 모집, Agile, Scrum 경험 필요",
    companyLocation: "서울 송파구 올림픽로 300",
    companyLatLng: { lat: 37.5131, lng: 127.1002 },
  },
  {
    id: 9,
    title: "게임 개발자",
    company: "Game Studio",
    location: "서울 강서구",
    salary: "연봉 5,000만원 ~ 6,500만원",
    description: "게임 개발자로서 다양한 게임 프로젝트를 개발하는 역할입니다.",
    companyInfo: "Game Studio는 혁신적인 게임 개발을 선도하는 회사입니다.",
    recruitingInfo: "게임 개발자 모집, Unity, C++ 사용",
    companyLocation: "서울 강서구 공항대로 12",
    companyLatLng: { lat: 37.5622, lng: 126.801 },
  },
  {
    id: 10,
    title: "디지털 마케팅 전문가",
    company: "MarketPro",
    location: "부산 동래구",
    salary: "연봉 4,200만원 ~ 5,500만원",
    description: "디지털 마케팅 전략을 수립하고 실행하는 역할입니다.",
    companyInfo:
      "MarketPro는 혁신적인 디지털 마케팅 전략을 제공하는 회사입니다.",
    recruitingInfo: "디지털 마케팅 전문가 모집, SEO, SEM, Google Ads 사용",
    companyLocation: "부산 동래구 온천장로 45",
    companyLatLng: { lat: 35.2257, lng: 129.083 },
  },
  {
    id: 11,
    title: "소프트웨어 엔지니어",
    company: "Tech Innovators",
    location: "서울 강동구",
    salary: "연봉 6,000만원 이상",
    description: "소프트웨어 개발 및 시스템 설계 업무를 맡습니다.",
    companyInfo:
      "Tech Innovators는 혁신적인 기술 개발을 통해 글로벌 시장을 리드하는 회사입니다.",
    recruitingInfo: "소프트웨어 엔지니어 모집, Java, Python 사용",
    companyLocation: "서울 강동구 천호대로 45",
    companyLatLng: { lat: 37.5407, lng: 127.141 },
  },
  {
    id: 12,
    title: "네트워크 관리자",
    company: "NetGuard",
    location: "서울 성동구",
    salary: "연봉 4,000만원 ~ 5,000만원",
    description: "네트워크 시스템의 유지보수 및 보안 관리 업무를 담당합니다.",
    companyInfo:
      "NetGuard는 안전하고 안정적인 네트워크 서비스를 제공하는 기업입니다.",
    recruitingInfo: "네트워크 관리자 모집, Cisco, AWS 사용",
    companyLocation: "서울 성동구 왕십리로 10",
    companyLatLng: { lat: 37.5631, lng: 127.0362 },
  },
  {
    id: 13,
    title: "모바일 앱 개발자",
    company: "MobileTech",
    location: "서울 강남구",
    salary: "연봉 5,500만원 ~ 7,000만원",
    description: "모바일 앱 개발을 담당하는 역할입니다.",
    companyInfo:
      "MobileTech는 최신 모바일 기술을 활용한 앱 개발을 전문으로 하는 회사입니다.",
    recruitingInfo: "모바일 앱 개발자 모집, Kotlin, Swift 사용",
    companyLocation: "서울 강남구 테헤란로 45",
    companyLatLng: { lat: 37.5089, lng: 127.0375 },
  },
  {
    id: 14,
    title: "클라우드 엔지니어",
    company: "CloudWorks",
    location: "대전 유성구",
    salary: "연봉 6,000만원 ~ 7,500만원",
    description: "클라우드 환경에서 서비스 구축 및 운영을 담당합니다.",
    companyInfo:
      "CloudWorks는 클라우드 기반 서비스를 제공하는 선도적인 기업입니다.",
    recruitingInfo: "클라우드 엔지니어 모집, AWS, Azure 사용",
    companyLocation: "대전 유성구 테크노1로 7",
    companyLatLng: { lat: 36.3505, lng: 127.3856 },
  },
  {
    id: 15,
    title: "보안 전문가",
    company: "SecureIT",
    location: "서울 종로구",
    salary: "연봉 6,500만원 이상",
    description: "정보 보안 업무를 담당합니다.",
    companyInfo: "SecureIT는 글로벌 보안 솔루션을 제공하는 기업입니다.",
    recruitingInfo: "보안 전문가 모집, Firewalls, IDS/IPS 사용",
    companyLocation: "서울 종로구 종로 45",
    companyLatLng: { lat: 37.5722, lng: 126.9795 },
  },
  {
    id: 16,
    title: "인공지능 연구원",
    company: "AI Innovations",
    location: "서울 송파구",
    salary: "연봉 8,000만원 이상",
    description: "AI 연구 및 모델 개발을 담당합니다.",
    companyInfo:
      "AI Innovations는 차세대 인공지능 기술을 개발하는 글로벌 기업입니다.",
    recruitingInfo: "인공지능 연구원 모집, TensorFlow, PyTorch 사용",
    companyLocation: "서울 송파구 올림픽로 45",
    companyLatLng: { lat: 37.5104, lng: 127.1001 },
  },
];

function JobDetail() {
  const { id } = useParams(); // URL 파라미터에서 'id' 값을 가져옵니다.
  const job = jobData.find((job) => job.id === parseInt(id));
  const [map, setMap] = useState(null);

  useEffect(() => {
    // 페이지 로드 시 body의 배경 색상을 설정
    document.body.style.backgroundColor = "#f5f6f7";

    const script = document.createElement("script");
    script.src = `https://maps.googleapis.com/maps/api/js?key=AIzaSyBzgO9et2D2Ejimj7KHsCrrR-rR_mhtSvI&callback=initMap`;
    script.async = true;
    script.defer = true;
    document.head.appendChild(script);

    window.initMap = () => {
      const mapOptions = {
        center: job.companyLatLng,
        zoom: 14,
      };
      const newMap = new window.google.maps.Map(
        document.getElementById("map"),
        mapOptions
      );
      new window.google.maps.Marker({
        position: job.companyLatLng,
        map: newMap,
      });
      setMap(newMap);
    };
  }, [job.companyLatLng]);
  return (
    <div style={styles.container}>
      <div style={styles.section}>
        <h2>{job.company}</h2>
        <p>{job.companyInfo}</p>
        <p>위치: {job.companyLocation}</p>
      </div>
      <div style={styles.section}>
        <h3>모집 요강</h3>
        <p>{job.recruitingInfo}</p>
      </div>
      <div style={styles.section}>
        <h3>회사 위치</h3>
        <div
          id="map"
          style={{
            height: "400px",
            width: "100%",
            borderRadius: "8px",
            boxShadow: "0 4px 6px rgba(0, 0, 0, 0.1)",
          }}
        ></div>
      </div>
    </div>
  );
}

const styles = {
  container: {
    padding: "20px",
    fontFamily: "'Arial', sans-serif",
    textAlign: "center",
    marginTop: "70px",
    backgroundColor: "#f5f6f7",
  },
  section: {
    marginBottom: "40px",
  },
};

export default JobDetail;
