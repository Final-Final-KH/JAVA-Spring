import React, { useState } from "react";
import ReactSlider from "react-slider";

const ScoreBar3 = () => {
  const [score, setScore] = useState(50); // 초기 값은 50

  return (
    <div style={{ width: "80%", margin: "20px auto", textAlign: "center" }}>
      <ReactSlider
        min={0}
        max={100}
        value={score}
        onChange={setScore}
        renderTrack={(props, state) => (
          <div
            {...props}
            style={{
              ...props.style,
              height: "8px",
              backgroundColor: "#ccc",
              borderRadius: "5px",
            }}
          />
        )}
        renderThumb={(props) => (
          <div
            {...props}
            style={{
              ...props.style,
              height: "10px",
              width: "10px",
              borderRadius: "50%",
              backgroundColor: "#4CAF50",
            }}
          />
        )}
      />
      <br />
      <div>점수: {score}</div>
    </div>
  );
};

export default ScoreBar3;
