import { useState } from "react";
import NewsList from "../component/NewsList";
import TrendTab from "./kedu/etc/TrendTab";

function News() {
  const [activeTab, setActiveTab] = useState("news"); // 현재 선택된 탭

  return (
    <div
      style={{
        display: "flex",
        justifyContent: "center",
        alignItems: "center",
        flexDirection: "column",
        padding: "20px",
        marginTop: "70px",
      }}
    >
      <h1>IT 뉴스 페이지에 오신 것을 환영합니다!</h1>

      {/* 탭 메뉴 */}
      <ul
        style={{
          listStyle: "none",
          display: "flex",
          gap: "20px",
          padding: 0,
          marginBottom: "20px",
        }}
      >
        <li
          style={{
            cursor: "pointer",
            padding: "10px",
            borderBottom: activeTab === "news" ? "3px solid blue" : "none",
          }}
          onClick={() => setActiveTab("news")}
        >
          뉴스
        </li>
        <li
          style={{
            cursor: "pointer",
            padding: "10px",
            borderBottom: activeTab === "trend" ? "3px solid blue" : "none",
          }}
          onClick={() => setActiveTab("trend")}
        >
          트렌드
        </li>
      </ul>

      {/* 선택된 탭에 따라 컴포넌트 표시 */}
      {activeTab === "news" ? <NewsList /> : <TrendTab />}
    </div>
  );
}

export default News;
