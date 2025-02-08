import { useContext, useEffect, useRef, useState } from "react";
import AxiosApi2 from "../../../../api/AxiosApi2";
import { useParams } from "react-router-dom";
import { RegionSearchContext } from "../../../../api/provider/RegionSearchContextProvider2";
import { Button2, Content, Select2 } from "../style/style2";

const CourseComment = () => {
  const { searchKeyword, academyName } = useContext(RegionSearchContext); // RegionSearchContext로 변경
  const [lectureList, setLectureList] = useState([]);
  const [courseName, setCourseName] = useState("");
  const [courseId, setCourseId] = useState("");
  const { academyId } = useParams("");
  const [score, setScore] = useState("");
  const academyNameRef = useRef("");
  const regionRef = useRef("");
  useEffect(() => {
    //CourseScore();
  });
  useEffect(() => {
    academyNameRef.current = localStorage.getItem("academyName");
    regionRef.current = localStorage.getItem("region");
  }, [searchKeyword, academyName]); // searchKeyword, academyName 값이 변경될 때마다 재호출

  const ChoseCourse = (courseName, courseId) => {
    setCourseName(courseName);
    setCourseId(courseId);
  };
  const LectureList = async () => {
    const rsp = await AxiosApi2.lecture(
      regionRef.current,
      academyNameRef.current
    );
    setLectureList(rsp.data.list);
  };

  const CourseScore = async () => {
    if (courseId === "") {
      return;
    }
    const rsp = await AxiosApi2.courseScore(academyId, courseId);
    if (rsp.data.sub_total_avg && rsp.data.sub_total_avg.length > 0) {
      // 첫 번째 항목만 가져오기
      setScore(rsp.data.sub_total_avg[0]);
    }
  };
  return (
    <Content>
      <Button2 onClick={() => LectureList()}>강의 조회</Button2>
      <div>
        {lectureList.length > 0 ? (
          <>
            <Select2
              onChange={(e) => {
                const selectedOption = e.target.value;
                const selectedLecture = lectureList.find(
                  (lecture) => lecture.course_name === selectedOption
                );
                ChoseCourse(
                  selectedLecture.course_name,
                  selectedLecture.course_id
                );
              }}
            >
              <option value={""}>수강 정보를 선택하세요</option>
              {lectureList.map((lecture, index) => (
                <option key={index} value={lecture.course_name}>
                  {lecture.course_name}
                  {/* 추가적으로 강의에 대한 내용들 출력 */}
                </option>
              ))}
            </Select2>

            <Button2 onClick={CourseScore}>강의 리뷰 검색하기</Button2>
          </>
        ) : (
          <p>강의 리스트가 없습니다.</p>
        )}
      </div>
      <div>
        <div>
          {score ? (
            <div>
              {/* 총점 크게 출력 */}
              <h2>
                총점: {score.totalAvg ? score.totalAvg.toFixed(2) : "N/A"}
              </h2>

              {/* 카테고리별 점수 출력 */}
              <div>
                <p>
                  취업 도움:{" "}
                  {score.job !== undefined ? score.job.toFixed(2) : "N/A"}
                </p>
                <p>
                  강의 만족도:{" "}
                  {score.lecture !== undefined
                    ? score.lecture.toFixed(2)
                    : "N/A"}
                </p>
                <p>
                  강사 만족도:{" "}
                  {score.teacher !== undefined
                    ? score.teacher.toFixed(2)
                    : "N/A"}
                </p>
                <p>
                  교재 만족도:{" "}
                  {score.books !== undefined ? score.books.toFixed(2) : "N/A"}
                </p>
                <p>
                  기술 만족도:{" "}
                  {score.newTech !== undefined
                    ? score.newTech.toFixed(2)
                    : "N/A"}
                </p>
                <p>
                  학업 만족도:{" "}
                  {score.skillUp !== undefined
                    ? score.skillUp.toFixed(2)
                    : "N/A"}
                </p>
              </div>
            </div>
          ) : (
            <p>로딩 중...</p> // 데이터가 없으면 로딩 메시지
          )}
        </div>
      </div>
    </Content>
  );
};

export default CourseComment;
