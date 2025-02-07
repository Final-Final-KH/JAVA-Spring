import React, { useState, useEffect } from "react";
import axios from "axios";
import Common from "../utils/Common";
import "../css/NewsList.css";
import { FaSearch, FaArrowLeft, FaArrowRight } from "react-icons/fa"; // 아이콘 불러오기
import AxiosInstance from "../axios/AxiosInstanse";

const NewsList = () => {
  const [news, setNews] = useState([]);
  const [category, setCategory] = useState("IT");
  const [search, setSearch] = useState("");
  const [page, setPage] = useState(1);

  const baseUrl = Common.KH_DOMAIN + "/api/news";

  const fetchNews = () => {
    AxiosInstance.get(baseUrl, {
      params: {
        category: category,
        search: search,
        page: page,
      },
    })
      .then((res) => {
        setNews(res.data.items);
      })
      .catch((err) => {
        console.log("뉴스 불러오기 실패", err);
      });
  };

  // 검색버튼을 눌렀을 때만 뉴스 데이터를 요청하도록 수정
  const handleSearchClick = () => {
    setPage(1); // 검색 후 첫 페이지부터 다시 시작하도록 설정
    fetchNews();
  };

  useEffect(() => {
    fetchNews(); // 최초 페이지 로딩 시에도 데이터를 불러옴
  }, [category, page]); // category와 page에 의존, search는 의존하지 않음

  return (
    <div>
      <div className="search-container">
        <input
          type="text"
          placeholder="Search..."
          value={search}
          onChange={(e) => setSearch(e.target.value)}
        />
        <button className="search-button" onClick={handleSearchClick}>
          <FaSearch />
        </button>
      </div>

      {/* 페이지 네비게이션 상단 */}
      <div className="pagination-buttons">
        <button
          className="pagination-button"
          onClick={() => setPage((prev) => (prev > 1 ? prev - 1 : 1))}
        >
          <FaArrowLeft />
        </button>
        <button
          className="pagination-button"
          onClick={() => setPage((prev) => prev + 1)}
        >
          <FaArrowRight />
        </button>
      </div>

      <div className="news-container">
        {news.map((item, index) => (
          <div className="news-card" key={index}>
            <a href={item.link} target="_blank" rel="noopener noreferrer">
              <h3
                className="news-title"
                dangerouslySetInnerHTML={{ __html: item.title }}
              />
            </a>
            <p
              className="news-description"
              dangerouslySetInnerHTML={{ __html: item.description }}
            />
            <div className="news-footer">
              <span className="news-source">{item.originallink}</span>
              <span className="news-date">
                {new Date(item.pubDate).toLocaleString()}
              </span>
            </div>
          </div>
        ))}
      </div>

      {/* 페이지 네비게이션 하단 */}
      <div className="pagination-buttons">
        <button
          className="pagination-button"
          onClick={() => setPage((prev) => (prev > 1 ? prev - 1 : 1))}
        >
          <FaArrowLeft />
        </button>
        <button
          className="pagination-button"
          onClick={() => setPage((prev) => prev + 1)}
        >
          <FaArrowRight />
        </button>
      </div>
    </div>
  );
};

export default NewsList;
