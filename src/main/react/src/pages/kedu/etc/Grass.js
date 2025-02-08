import React from "react";
import "./Grass.css";

// 임의의 활동 데이터 (2025년 1월)
const activityData = [
  { date: "2025-01-01", count: 5 },
  { date: "2025-01-02", count: 2 },
  { date: "2025-01-03", count: 0 },
  { date: "2025-01-04", count: 8 },
  { date: "2025-01-05", count: 1 },
  { date: "2025-01-06", count: 6 },
  { date: "2025-01-07", count: 0 },
  { date: "2025-01-08", count: 3 },
  // 계속 데이터 추가
];

const getColor = (count) => {
  if (count === 0) return "#ebedf0"; // 비활동
  if (count <= 2) return "#c6e48b"; // 적은 활동
  if (count <= 5) return "#7bc96f"; // 보통 활동
  if (count <= 10) return "#239a3b"; // 많은 활동
  return "#196127"; // 매우 많은 활동
};

const Grass = () => {
  // 날짜가 일요일부터 시작하도록 그리드 만들기
  const daysOfWeek = ["Sun", "Mon", "Tue", "Wed", "Thu", "Fri", "Sat"];
  const rows = [];
  let currentDate = new Date("2025-01-01"); // 시작 날짜

  // 6주 (행)로 구성된 그리드 (1년 기준)
  for (let i = 0; i < 6; i++) {
    const cells = [];
    for (let j = 0; j < 7; j++) {
      const dateString = currentDate.toISOString().split("T")[0]; // 날짜 포맷 YYYY-MM-DD
      const activity = activityData.find((item) => item.date === dateString);
      const count = activity ? activity.count : 0; // 활동 횟수
      const color = getColor(count);

      cells.push(
        <div
          key={dateString}
          className="grass-cell"
          style={{ backgroundColor: color }}
          title={dateString} // 마우스를 올리면 날짜 표시
        ></div>
      );

      // 날짜 증가
      currentDate.setDate(currentDate.getDate() + 1);
    }
    rows.push(
      <div key={i} className="grass-row">
        {cells}
      </div>
    );
  }

  return (
    <div className="grass-container">
      <div className="grass-header">
        {daysOfWeek.map((day) => (
          <div key={day} className="grass-header-day">
            {day}
          </div>
        ))}
      </div>
      <div className="grass-body">{rows}</div>
    </div>
  );
};

export default Grass;
