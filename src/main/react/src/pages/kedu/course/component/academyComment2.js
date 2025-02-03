import { useParams } from "react-router-dom";
import AxiosApi2 from "../../../../api/AxiosApi2";
import { useEffect, useState } from "react";
import { Content } from "../style/style2";

const AcademyComment = () => {
  const { academyId } = useParams("");
  const [score, setScore] = useState(null); // 초기값을 null로 설정

  useEffect(() => {
    ScoreList();
  }, []);

  const ScoreList = async () => {
    try {
      const rsp = await AxiosApi2.scoreList(academyId);
      if (rsp.data.sub_total_avg && rsp.data.sub_total_avg.length > 0) {
        // 첫 번째 항목만 가져오기
        setScore(rsp.data.sub_total_avg[0]);
      }
    } catch (error) {
      console.error("Error fetching score data:", error);
    }
  };

  return (
    <Content>
      <h3>학원 리뷰</h3>
      <div>
        {score ? (
          <div>
            {/* 총점 크게 출력 */}
            <h2>총점: {score.totalAvg ? score.totalAvg.toFixed(2) : "N/A"}</h2>

            {/* 카테고리별 점수 출력 */}
            <div>
              <p>취업 도움: {score.avgJob ? score.avgJob.toFixed(2) : "N/A"}</p>
              <p>
                강의 만족도:{" "}
                {score.avgLecture ? score.avgLecture.toFixed(2) : "N/A"}
              </p>
              <p>
                시설 만족도:{" "}
                {score.avgFacilities ? score.avgFacilities.toFixed(2) : "N/A"}
              </p>
              <p>
                강사 평가:{" "}
                {score.avgTeacher ? score.avgTeacher.toFixed(2) : "N/A"}
              </p>
              <p>
                교재 만족도:{" "}
                {score.avgBooks ? score.avgBooks.toFixed(2) : "N/A"}
              </p>
              <p>
                취업 지원:{" "}
                {score.avgService ? score.avgService.toFixed(2) : "N/A"}
              </p>
            </div>
          </div>
        ) : (
          <p>로딩 중...</p> // 데이터가 없으면 로딩 메시지
        )}
      </div>
    </Content>
  );
};

export default AcademyComment;
