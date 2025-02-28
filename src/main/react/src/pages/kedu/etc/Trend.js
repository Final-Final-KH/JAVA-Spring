import React, { useState, useEffect, useCallback } from "react";
import { Octokit } from "@octokit/rest";
import { Bar } from "react-chartjs-2";
import {
  Chart as ChartJS,
  CategoryScale,
  LinearScale,
  BarElement,
  Title,
  Tooltip,
  Legend,
} from "chart.js";

ChartJS.register(
  CategoryScale,
  LinearScale,
  BarElement,
  Title,
  Tooltip,
  Legend
);

const octokit = new Octokit({
  auth: process.env.REACT_APP_GITHUB_TOKEN,
});

const Trend = () => {
  const [languageTrends, setLanguageTrends] = useState([]);

  const getLanguageTrends = useCallback(async () => {
    try {
      const result = await octokit.rest.search.repos({
        q: "stars:>1000",
        sort: "stars",
        order: "desc",
        per_page: 100,
      });

      const languageCounts = {};
      result.data.items.forEach((repo) => {
        if (repo.language) {
          languageCounts[repo.language] =
            (languageCounts[repo.language] || 0) + 1;
        }
      });

      const sortedLanguages = Object.entries(languageCounts)
        .sort((a, b) => b[1] - a[1])
        .slice(0, 10);

      setLanguageTrends(sortedLanguages);
    } catch (error) {
      console.error("Error fetching language trends:", error);
    }
  }, []);

  useEffect(() => {
    getLanguageTrends();
  }, [getLanguageTrends]);

  const chartData = {
    labels: languageTrends.map(([lang]) => lang),
    datasets: [
      {
        label: "Repository Count",
        data: languageTrends.map(([, count]) => count),
        backgroundColor: "rgba(75, 192, 192, 0.6)",
      },
    ],
  };

  const options = {
    responsive: true,
    plugins: {
      legend: { display: false },
      title: { display: true, text: "Top 10 Programming Languages" },
    },
    scales: {
      y: { beginAtZero: true },
    },
  };

  return (
    <div className="App">
      <h1>GitHub Language Trends</h1>
      <Bar data={chartData} options={options} />
    </div>
  );
};

export default Trend;
