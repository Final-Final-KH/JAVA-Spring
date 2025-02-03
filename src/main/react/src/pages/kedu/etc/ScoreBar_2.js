import React, { useState, useRef } from "react";

const ScoreBar = () => {
  // 점수를 저장하는 상태 변수 (0에서 100 사이의 점수)
  const [score, setScore] = useState(50); // 초기 값은 50
  const [isDragging, setIsDragging] = useState(false); // 드래그 상태를 추적
  const barRef = useRef(null); // 바의 참조를 저장하기 위한 ref

  // 점수를 바 형태로 표시하기 위한 스타일
  const barStyle = {
    width: `${score}%`,
    height: "30px",
    backgroundColor: score >= 80 ? "green" : score >= 50 ? "yellow" : "red",
    borderRadius: "5px",
    transition: "width 0.1s ease",
  };

  // 점수를 클릭한 위치에 맞춰 변경하는 함수
  const handleMouseMove = (event) => {
    if (isDragging && barRef.current) {
      const barWidth = barRef.current.offsetWidth; // 바의 전체 너비
      const clickPosition = event.nativeEvent.offsetX; // 클릭한 위치 (픽셀 단위)
      const newScore = Math.round((clickPosition / barWidth) * 100); // 클릭 위치에 맞는 점수 계산
      setScore(newScore);
    }
  };

  // 마우스를 클릭했을 때 드래그 시작
  const handleMouseDown = (event) => {
    setIsDragging(true); // 드래그 시작
    handleMouseMove(event); // 마우스 위치에 맞는 점수 설정
  };

  // 마우스를 떼었을 때 드래그 종료
  const handleMouseUp = () => {
    setIsDragging(false); // 드래그 종료
  };

  // 마디 표시 스타일
  const tickStyle = {
    position: "absolute",
    width: "2px",
    height: "10px",
    backgroundColor: "#000",
    top: "50%",
    transform: "translateY(-50%)",
  };

  // 점수 표시 스타일
  const scoreLabelStyle = {
    position: "absolute",
    fontSize: "12px",
    top: "70%", // 마디 아래쪽에 위치하도록 설정
    textAlign: "center",
    width: "100%",
  };

  // 점수 바에 +10점마다 마디 표시 생성
  const ticks = [];
  const labels = [];
  for (let i = 0; i <= 10; i++) {
    const position = i * 10;
    ticks.push(
      <div
        key={`tick-${i}`}
        style={{
          ...tickStyle,
          left: `${position}%`, // 각 마디의 위치를 10% 간격으로 설정
        }}
      />
    );

    labels.push(
      <div
        key={`label-${i}`}
        style={{
          position: "absolute",
          left: `${position}%`,
          transform: "translateX(-50%)", // 가운데 정렬
          fontSize: "12px",
        }}
      >
        {position} {/* 각 마디의 점수 표시 */}
      </div>
    );
  }

  return (
    <div>
      <div
        style={{
          position: "relative",
          width: "100%",
          backgroundColor: "#ccc",
          borderRadius: "5px",
        }}
        ref={barRef}
        onMouseDown={handleMouseDown}
        onMouseMove={handleMouseMove}
        onMouseUp={handleMouseUp}
        onMouseLeave={handleMouseUp} // 마우스가 바를 떠나면 드래그 종료
      >
        <div style={barStyle}></div>
        {ticks} {/* 마디 표시 */}
        {labels} {/* +10점마다 점수 표시 */}
      </div>
      <p>현재 점수: {score}</p>
    </div>
  );
};

export default ScoreBar;
