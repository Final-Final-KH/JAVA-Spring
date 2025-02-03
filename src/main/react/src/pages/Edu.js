import React from "react";
import { useNavigate } from "react-router-dom";
import imgLogo1 from "../images/EduPicture.webp";

const Edu = () => {
  const navigate = useNavigate();

  const goToEduList = () => {
    navigate("/course"); // 모집열람 페이지로 이동
  };

  const goToReviewList = () => {
    navigate("/academy"); // 리뷰열람 페이지로 이동
  };

  return (
    <div className="edu-container">
      <img src={imgLogo1} alt="Edu Logo" className="logo-image" />
      <h1>국비교육 정보</h1>
      <div className="button-group">
        <button className="button" onClick={goToEduList}>
          모집열람
        </button>
        <button className="button" onClick={goToReviewList}>
          리뷰열람
        </button>
      </div>
      <style jsx>{`
        .edu-container {
          display: flex;
          flex-direction: column;
          align-items: center;
          justify-content: center;
          height: 100vh;
          background: linear-gradient(to bottom, #f0f0f0, #e0e0e0);
          padding: 20px;
          box-sizing: border-box;
        }

        .logo-image {
          width: 120px;
          height: auto;
          margin-bottom: 20px;
          border-radius: 50%; /* Makes the image circular */
          box-shadow: 0 4px 6px rgba(0, 0, 0, 0.1);
        }

        h1 {
          margin-bottom: 25px;
          font-size: 2.5rem;
          font-weight: bold;
          color: #222;
          text-shadow: 1px 1px 5px rgba(0, 0, 0, 0.1);
        }

        .button-group {
          display: flex;
          gap: 20px;
        }

        .button {
          padding: 12px 25px;
          font-size: 16px;
          color: #fff;
          background: linear-gradient(135deg, #4caf50, #3b8b3b);
          border: none;
          border-radius: 50px;
          cursor: pointer;
          box-shadow: 0 4px 6px rgba(0, 0, 0, 0.1);
          transition: all 0.3s ease;
        }

        .button:hover {
          background: linear-gradient(135deg, #45a049, #367c37);
          transform: translateY(-2px);
          box-shadow: 0 6px 8px rgba(0, 0, 0, 0.15);
        }

        .button:active {
          transform: translateY(1px);
          box-shadow: 0 3px 6px rgba(0, 0, 0, 0.1);
        }
      `}</style>
    </div>
  );
};

export default Edu;
