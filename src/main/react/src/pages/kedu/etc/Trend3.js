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
import "./Trend3.css";
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

const languages = [
  "Python",
  "Java",
  "C++",
  "Go",
  "Rust",
  "TypeScript",
  "Swift",
]; // JavaScript 제외

const Trend3 = () => {
  const [languageTrends, setLanguageTrends] = useState({});
  const years = Array.from(
    { length: 10 },
    (_, i) => new Date().getFullYear() - i
  ).reverse(); // 최근 10년

  const getYearlyTrends = useCallback(async () => {
    const trends = {};

    for (const lang of languages) {
      trends[lang] = [];

      for (const year of years) {
        const startDate = `${year}-01-01`;
        const endDate = `${year}-12-31`;

        try {
          await new Promise((resolve) => setTimeout(resolve, 1000)); // 1초 딜레이 추가 (API 제한 방지)

          const result = await octokit.rest.search.repos({
            q: `language:${lang} created:${startDate}..${endDate}`,
            per_page: 1, // total_count만 필요
          });

          trends[lang].push(result.data.total_count);
        } catch (error) {
          console.error(`Error fetching data for ${lang} in ${year}:`, error);
          trends[lang].push(0); // 오류 발생 시 0 처리
        }
      }
    }

    setLanguageTrends(trends);
  }, []);

  useEffect(() => {
    getYearlyTrends();
  }, [getYearlyTrends]);

  return (
    <div className="App">
      <h1>GitHub Language Trends (Last 10 Years)</h1>
      <div className="charts-container">
        {languages.map((lang, index) => (
          <div key={index} className="chart-item">
            <h2>{lang}</h2>
            <Line
              data={{
                labels: years,
                datasets: [
                  {
                    label: `${lang} Repository Count`,
                    data: languageTrends[lang] || [],
                    borderColor: `hsl(${(index * 60) % 360}, 70%, 50%)`,
                    backgroundColor: "rgba(0,0,0,0)",
                    tension: 0.3,
                  },
                ],
              }}
              options={{
                responsive: true,
                plugins: { legend: { display: false } },
                scales: { y: { beginAtZero: true } },
              }}
            />
          </div>
        ))}
      </div>
    </div>
  );
};

export default Trend3;
