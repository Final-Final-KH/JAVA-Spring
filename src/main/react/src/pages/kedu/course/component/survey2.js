import { useContext, useEffect, useRef, useState } from "react";
import AxiosApi2 from "../../../../api/AxiosApi2";
import { useParams } from "react-router-dom";
import { RegionSearchContext } from "../../../../api/provider/RegionSearchContextProvider2";
import { Button2, Content, Select2 } from "../style/style2";
import { Card, CardContent, CardTitle } from "../../css/Card";

const Survey = () => {
  const [survey, setSurvey] = useState("");
  const { academyId } = useParams("");
  const academyNameRef = useRef("");
  const regionRef = useRef("");
  const { searchKeyword, academyName } = useContext(RegionSearchContext); // RegionSearchContext로 변경
  const [lectureList, setLectureList] = useState([]);
  const [courseName, setCourseName] = useState("");
  const [courseId, setCourseId] = useState("");
  useEffect(() => {
    // SurvetList();
  }, []);

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
  const SurvetList = async () => {
    const rsp = await AxiosApi2.surveyList(academyId, courseId);
    setSurvey(rsp.data.list);
  };

  return (
    <Content>
      <h3>설문조사</h3>
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
            <Button2 onClick={SurvetList}>설문조사 리스트</Button2>
          </>
        ) : (
          <p>강의 리스트가 없습니다.</p>
        )}
      </div>
      <div>
        {survey.length > 0 ? (
          survey.map((survey) => (
            <Card>
              <CardTitle>설문 ID: {survey.survey_id}</CardTitle>
              <CardContent>
                <strong>교사:</strong> {survey.teacher}
              </CardContent>
              <CardContent>
                <strong>강의:</strong> {survey.lecture}
              </CardContent>
              <CardContent>
                <strong>시설:</strong> {survey.facilities}
              </CardContent>
              <CardContent>
                <strong>코멘트:</strong> {survey.comment}
              </CardContent>
              <CardContent>
                <strong>회원 ID:</strong> {survey.member_id}
              </CardContent>
              <CardContent>
                <strong>학원 ID:</strong> {survey.academy_id}
              </CardContent>
              <CardContent>
                <strong>강의 ID:</strong> {survey.course_id}
              </CardContent>
            </Card>
          ))
        ) : (
          <p>등록된 설문 조사가 없습니다.</p>
        )}
      </div>
    </Content>
  );
};

export default Survey;
