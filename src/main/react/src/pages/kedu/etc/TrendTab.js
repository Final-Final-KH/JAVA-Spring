import { useState } from "react";
import Trend from "./Trend"; // IT 키워드 트렌드
import Keyword from "./Keyword"; // 키워드 분석
import StockChart from "./Stock";

const TrendTab = () => {
  const [activeTab, setActiveTab] = useState("trend"); // 기본 탭: 'trend'

  return (
    <div>
      {/* 탭 버튼 */}
      <ul style={{ display: "flex", listStyle: "none", padding: 0 }}>
        <li
          style={{
            width: "200px", // 고정된 너비 설정
            textAlign: "center", // 중앙 정렬
            marginRight: "10px",
            cursor: "pointer",
            fontWeight: activeTab === "trend" ? "bold" : "normal",
          }}
          onClick={() => setActiveTab("trend")}
        >
          🔥 프로그래밍 언어 트렌드
        </li>
        <li
          style={{
            width: "200px",
            textAlign: "center",
            marginRight: "10px",
            cursor: "pointer",
            fontWeight: activeTab === "keyword" ? "bold" : "normal",
          }}
          onClick={() => setActiveTab("keyword")}
        >
          🔍 키워드 분석
        </li>
        <li
          style={{
            width: "200px",
            textAlign: "center",
            cursor: "pointer",
            fontWeight: activeTab === "stock" ? "bold" : "normal",
          }}
          onClick={() => setActiveTab("stock")}
        >
          📈 주식 관련 데이터
        </li>
      </ul>

      {/* 선택된 탭에 따라 다른 컴포넌트 렌더링 */}
      {activeTab === "trend" && <Trend />}
      {activeTab === "keyword" && <Keyword />}
      {activeTab === "stock" && <StockChart />}
    </div>
  );
};

export default TrendTab;
