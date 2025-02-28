import React, { useState, useEffect, useCallback } from "react";
import { Octokit } from "@octokit/rest";
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

const octokit = new Octokit({
  auth: process.env.REACT_APP_GITHUB_TOKEN, // GitHub Personal Access Token 필요
});

const Trend2 = () => {
  const [languageTrends, setLanguageTrends] = useState({});

  const getYearlyTrends = useCallback(async () => {
    const years = Array.from(
      { length: 10 },
      (_, i) => new Date().getFullYear() - i
    ); // 최근 10년
    const topLanguages = [
      "JavaScript",
      "Python",
      "Java",
      "C++",
      "Go",
      "Rust",
      "TypeScript",
      "Swift",
    ]; // 관심 있는 언어 지정

    const trends = {};

    for (const lang of topLanguages) {
      trends[lang] = [];

      for (const year of years) {
        try {
          const result = await octokit.rest.search.repos({
            q: `language:${lang} created:${year}`,
            per_page: 1, // 개수만 필요하므로 1개만 요청 (total_count 활용)
          });

          trends[lang].push(result.data.total_count);
        } catch (error) {
          console.error(`Error fetching data for ${lang} in ${year}:`, error);
          trends[lang].push(0); // 실패 시 0 추가
        }
      }
    }

    setLanguageTrends({ years: years.reverse(), trends }); // 연도 순서 반대로 정렬
  }, []);

  useEffect(() => {
    getYearlyTrends();
  }, [getYearlyTrends]);

  const chartData = {
    labels: languageTrends.years || [],
    datasets:
      languageTrends.trends &&
      Object.keys(languageTrends.trends).map((lang, index) => ({
        label: lang,
        data: languageTrends.trends[lang],
        borderColor: `hsl(${(index * 60) % 360}, 70%, 50%)`, // 각 언어별 색상 다르게
        backgroundColor: "rgba(0,0,0,0)",
        tension: 0.3, // 부드러운 곡선 효과
      })),
  };

  const options = {
    responsive: true,
    plugins: {
      legend: { position: "top" },
      title: { display: true, text: "GitHub Language Trends (Last 10 Years)" },
    },
    scales: {
      y: { beginAtZero: true },
    },
  };

  return (
    <div className="App">
      <h1>GitHub Language Trends</h1>
      <Line data={chartData} options={options} />
    </div>
  );
};

export default Trend2;
