import { useState } from "react";
import NewsList from "../../../component/NewsList";
import NYNews from "../../../component/NYNews";

const NewsTab = () => {
  const [activeTab, setActiveTab] = useState("trend"); // 기본 탭: 'trend'
  return (
    <div>
      {/* 탭 버튼 */}
      <ul style={{ display: "flex", listStyle: "none", padding: 0 }}>
        <li
          style={{
            width: "300px", // 고정된 너비 설정
            textAlign: "center", // 중앙 정렬
            marginRight: "10px",
            cursor: "pointer",
            fontWeight: activeTab === "trend" ? "bold" : "normal",
          }}
          onClick={() => setActiveTab("trend")}
        >
          Naver News
        </li>
        <li
          style={{
            width: "300px",
            textAlign: "center",
            marginRight: "10px",
            cursor: "pointer",
            fontWeight: activeTab === "keyword" ? "bold" : "normal",
          }}
          onClick={() => setActiveTab("keyword")}
        >
          NewYork Times
        </li>
      </ul>

      {/* 선택된 탭에 따라 다른 컴포넌트 렌더링 */}
      {activeTab === "trend" && <NewsList />}
      {activeTab === "keyword" && <NYNews />}
    </div>
  );
};

export default NewsTab;
