import React, { useState } from "react";
import { Line } from "react-chartjs-2";
import {
  Chart as ChartJS,
  CategoryScale,
  LinearScale,
  PointElement,
  LineElement,
  Title,
  Tooltip,
  Legend,
} from "chart.js";

ChartJS.register(
  CategoryScale,
  LinearScale,
  PointElement,
  LineElement,
  Title,
  Tooltip,
  Legend
);

const StockChart = () => {
  const [company, setCompany] = useState("");
  const [stockData, setStockData] = useState([]);
  const [error, setError] = useState("");

  const fetchStockData = async () => {
    setError("");
    setStockData([]);

    try {
      const response = await fetch(
        `http://localhost:5000/get_stock?company=${company}`
      );
      const data = await response.json();

      if (response.ok) {
        setStockData(data); // 서버에서 직접 데이터 배열을 반환하므로 data.stock_data가 아닌 data를 사용합니다.
      } else {
        setError(data.error || "데이터를 불러오는 중 오류 발생");
      }
    } catch (err) {
      setError("서버 오류 발생");
    }
  };

  const chartData = {
    labels: stockData.map((item) => item.date),
    datasets: [
      {
        label: "종가",
        data: stockData.map((item) => item.close),
        borderColor: "rgba(75, 192, 192, 1)",
        backgroundColor: "rgba(75, 192, 192, 0.2)",
        fill: true,
      },
    ],
  };

  const chartOptions = {
    responsive: true,
    plugins: {
      legend: {
        position: "top",
      },
      title: {
        display: true,
        text: `${company} 주가 차트`,
      },
    },
  };

  return (
    <div style={{ width: "600px", margin: "0 auto", textAlign: "center" }}>
      <h2>주식 데이터 검색</h2>
      <input
        type="text"
        value={company}
        onChange={(e) => setCompany(e.target.value)}
        placeholder="회사명 입력 (예: 삼성)"
        style={{ padding: "8px", fontSize: "16px" }}
      />
      <button
        onClick={fetchStockData}
        style={{ marginLeft: "10px", padding: "8px 16px" }}
      >
        검색
      </button>

      {error && <p style={{ color: "red" }}>{error}</p>}

      {stockData.length > 0 && (
        <div style={{ marginTop: "20px" }}>
          <h3>{company} 주가</h3>
          <Line data={chartData} options={chartOptions} />
        </div>
      )}
    </div>
  );
};

export default StockChart;
