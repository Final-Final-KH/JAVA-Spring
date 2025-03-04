import { useState } from "react";

export default function Pay() {
  const [year, setYear] = useState("");
  const [result, setResult] = useState(null);
  const [error, setError] = useState(null);

  const fetchSalaryPrediction = async () => {
    if (year === "") {
      setError("연차를 입력하세요.");
      return;
    }
    setError(null);

    try {
      const response = await fetch(
        `http://127.0.0.1:5000/predict_salary?year=${year}`
      );
      const data = await response.json();

      if (response.ok) {
        setResult(data);
      } else {
        setError(data.error || "예측 실패");
      }
    } catch (err) {
      setError("서버 연결 실패");
    }
  };

  return (
    <div className="flex flex-col items-center p-4">
      <h1 className="text-2xl font-bold mb-4">연봉 예측기</h1>
      <input
        type="number"
        value={year}
        onChange={(e) => setYear(e.target.value)}
        className="border p-2 rounded w-48"
        placeholder="연차 입력 (예: 8)"
      />
      <button
        onClick={fetchSalaryPrediction}
        className="mt-2 bg-blue-500 text-gray-200 px-4 py-2 rounded"
      >
        예측하기
      </button>

      {error && <p className="text-red-500 mt-2">{error}</p>}

      {result && (
        <div className="mt-4 p-4 border rounded w-64">
          <h2 className="text-lg font-semibold">예측 결과</h2>
          <p>
            <strong>입력 연차:</strong> {result["입력 연차"]}년
          </p>
          <p>
            <strong>k-NN 회귀:</strong> {result["k-NN 회귀 예상 연봉"]}만원
          </p>
          <p>
            <strong>선형 회귀:</strong> {result["선형 회귀 예상 연봉"]}만원
          </p>
          <p>
            <strong>다항 회귀:</strong> {result["다항 회귀 예상 연봉 (2차)"]}
            만원
          </p>
        </div>
      )}
    </div>
  );
}
