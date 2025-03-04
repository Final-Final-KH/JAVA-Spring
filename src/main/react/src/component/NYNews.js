import React, { useEffect, useState } from "react";
import { FaAws } from "react-icons/fa";

const NYNews = () => {
  const [query, setQuery] = useState("");
  const [articles, setArticles] = useState([]);
  const [loading, setLoading] = useState(false);

  useEffect(() => {
    fetchNews();
  }, []);
  const fetchNews = async () => {
    setLoading(true);
    try {
      const response = await fetch(`http://127.0.0.1:5000/ny_news`);
      const data = await response.json();
      console.log("기본 출력", data);
      if (data.response && data.response.docs) {
        setArticles(data.response.docs);
      } else {
        setArticles([]);
      }
    } catch (error) {
      console.error("Error fetching news:", error);
      setArticles([]);
    }
    setLoading(false);
  };

  const searchKeyword = async () => {
    setLoading(true);
    try {
      const response = await fetch(
        `http://127.0.0.1:5000/search_ny_news?search=${encodeURIComponent(
          query
        )}`
      );
      const data = await response.json();
      console.log("검색해서 어떻게 가져오길래......", data);

      if (Array.isArray(data)) {
        setArticles(data); // 배열이므로 바로 설정 가능
      } else if (data.response && Array.isArray(data.response.docs)) {
        setArticles(data.response.docs); // response.docs 내부에 있으면 여기서 설정
      } else {
        setArticles([]);
      }
    } catch (error) {
      console.error("Error fetching news:", error);
      setArticles([]);
    }
    setLoading(false);
  };

  return (
    <div
      style={{ maxWidth: "600px", margin: "50px auto", textAlign: "center" }}
    >
      <h1>NYT 뉴스 검색</h1>
      <input
        type="text"
        value={query}
        onChange={(e) => setQuery(e.target.value)}
        placeholder="검색어 입력"
        style={{ padding: "10px", width: "80%", marginBottom: "10px" }}
      />
      <button onClick={searchKeyword} style={{ padding: "10px 20px" }}>
        검색
      </button>

      {loading && <p>검색 중...</p>}

      <ul style={{ listStyle: "none", padding: 0 }}>
        {articles.map((article, index) => (
          <li
            key={index}
            style={{
              margin: "10px 0",
              padding: "10px",
              borderBottom: "1px solid #ddd",
            }}
          >
            <h3>
              <a
                href={article.web_url}
                target="_blank"
                rel="noopener noreferrer"
              >
                {article.headline.main}
              </a>
            </h3>
            <p>{article.abstract}</p>
          </li>
        ))}
      </ul>
    </div>
  );
};

export default NYNews;
