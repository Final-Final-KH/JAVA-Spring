import React, { useEffect, useState } from "react";
import { Line } from "react-chartjs-2";

const Trend = () => {
  const [trendData, setTrendData] = useState([]);

  useEffect(() => {
    fetch("http://localhost:5000/trend_keyword")
      .then((res) => res.json())
      .then((data) => {
        if (Array.isArray(data)) {
          const formatDate = (timestamp) => {
            const date = new Date(timestamp);
            return date.toISOString().split("T")[0];
          };

          const formattedData = data.map((item) => ({
            date: formatDate(item.date),
            AI: item.AI,
            "인공 지능": item["인공 지능"],
            빅데이터: item.빅데이터,
            "양자 컴퓨터": item["양자 컴퓨터"],
          }));

          setTrendData(formattedData);
        }
      })
      .catch((error) => console.error("Error fetching trends:", error));
  }, []);

  if (trendData.length === 0) {
    return <p>데이터 로딩 중...</p>;
  }

  const generateChartData = (label, color) => ({
    labels: trendData.map((d) => d.date),
    datasets: [
      {
        label,
        data: trendData.map((d) => d[label]),
        borderColor: color,
        backgroundColor: color.replace("1)", "0.2)"),
        fill: true,
      },
    ],
  });

  return (
    <div
      style={{
        maxWidth: "500px", // 전체 최대 너비 증가
        margin: "0 auto",
      }}
    >
      <h2>IT 키워드 트렌드</h2>
      <div
        style={{
          display: "grid",
          gridTemplateColumns: "repeat(2, 1fr)", // 2열 배치
          gap: "20px",
          justifyItems: "center",
        }}
      >
        <div style={{ width: "100%", maxWidth: "300px" }}>
          <h3>AI</h3>
          <Line data={generateChartData("AI", "rgba(75, 192, 192, 1)")} />
        </div>
        <div style={{ width: "100%", maxWidth: "300px" }}>
          <h3>인공 지능</h3>
          <Line
            data={generateChartData("인공 지능", "rgba(255, 99, 132, 1)")}
          />
        </div>
        <div style={{ width: "100%", maxWidth: "300px" }}>
          <h3>빅데이터</h3>
          <Line data={generateChartData("빅데이터", "rgba(54, 162, 235, 1)")} />
        </div>
        <div style={{ width: "100%", maxWidth: "300px" }}>
          <h3>양자 컴퓨터</h3>
          <Line
            data={generateChartData("양자 컴퓨터", "rgba(255, 206, 86, 1)")}
          />
        </div>
      </div>
    </div>
  );
};

export default Trend;
