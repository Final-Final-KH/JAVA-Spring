import React, { useState } from "react";
import Slider from "rc-slider";
import "rc-slider/assets/index.css";

const ScoreBar4 = () => {
  const [score, setScore] = useState(50);

  return (
    <div style={{ width: "80%", margin: "20px auto", textAlign: "center" }}>
      <Slider
        min={0}
        max={100}
        value={score}
        onChange={setScore}
        trackStyle={{ backgroundColor: "#4CAF50", height: 8 }}
        railStyle={{ backgroundColor: "#ccc", height: 8 }}
        handleStyle={{
          borderColor: "#4CAF50",
          height: 20,
          width: 20,
          backgroundColor: "#4CAF50",
        }}
      />
      <div>점수: {score}</div>
    </div>
  );
};

export default ScoreBar4;
