import { useParams } from "react-router-dom";
import AxiosApi2 from "../../../api/AxiosApi2";
import { useEffect, useState } from "react";
import {
  Content,
  Table2,
  TableContainer2,
  TableData2,
  TableRow2,
} from "./style/style2";
import { Card, CardContent, CardTitle } from "../css/Card";
import styled from "styled-components";

// 스타일 추가
const Title = styled.h1`
  font-size: 2rem;
  font-weight: bold;
  color: #2c3e50;
  margin-bottom: 20px;
  text-align: center;
  margin-top: 150px; // 상단에서 150px 만큼 아래로 위치
`;

const SubTitle = styled.h3`
  font-size: 1.5rem;
  color: #34495e;
  margin-bottom: 10px;
  text-align: center;
`;

const ScoreContainer = styled.div`
  background-color: #f9fafb;
  padding: 20px;
  border-radius: 8px;
  box-shadow: 0 4px 6px rgba(0, 0, 0, 0.1);
  margin-bottom: 20px;
  width: 45%;
  display: inline-block;
  margin-right: 10%; // 두 컨테이너 간 간격
`;

const ScoreTitle = styled.h2`
  font-size: 1.8rem;
  color: #e74c3c;
`;

const ScoreCategory = styled.p`
  font-size: 1rem;
  color: #7f8c8d;
`;

const ScoreWrapper = styled.div`
  display: flex;
  justify-content: center; // 두 개의 점수를 가운데 정렬
  gap: 20px; // 두 컨테이너 사이의 간격
`;

const TableContainerWrapper = styled.div`
  margin: 20px 0;
  background-color: #ecf0f1;
  padding: 20px;
  border-radius: 8px;
  box-shadow: 0 4px 6px rgba(0, 0, 0, 0.1);
`;

const SurveyCard = styled(Card)`
  margin-bottom: 20px;
  padding: 15px;
  background-color: #fff;
  border-radius: 8px;
  box-shadow: 0 4px 6px rgba(0, 0, 0, 0.1);
`;

const SurveyContent = styled(CardContent)`
  margin: 10px 0;
`;

const SurveyTitle = styled(CardTitle)`
  font-size: 1.3rem;
  color: #3498db;
`;

const LoadingMessage = styled.p`
  font-size: 1.2rem;
  color: #95a5a6;
  text-align: center;
`;

const DetailCourse = () => {
  const { region, academyName, courseId } = useParams();
  const [academy, setAcademy] = useState("");
  const [surveyList, setSurveyList] = useState([]);
  const [courseScore, setcourseScore] = useState("");
  const [academyScoure, setAcademyScore] = useState("");
  const [shortCommentList, setShortCommentList] = useState("");
  const [survey, setSurvey] = useState("");

  useEffect(() => {
    const fetchData = async () => {
      const academyResponse = await getAcademyId();
      if (academyResponse) {
        await getReview(courseId, academyResponse);
        CourseScore(academyResponse);
        ScoreList(academyResponse);
        ShortCommentList(academyResponse);
        SurvetList(academyResponse);
      }
    };

    fetchData();
  }, [region, academyName, courseId]);

  const getAcademyId = async () => {
    try {
      const rsp = await AxiosApi2.getAcademyId(region, academyName);
      setAcademy(rsp.data.id);
      return rsp.data.id;
    } catch (error) {
      console.error("Failed to fetch academy ID:", error);
      return null;
    }
  };

  const getReview = async (courseId, academyResponse) => {
    try {
      const rsp = await AxiosApi2.review(courseId, academyResponse);
      setSurveyList(rsp.data.list);
    } catch (error) {
      console.error("Failed to fetch reviews:", error);
    }
  };

  const CourseScore = async (academy) => {
    if (academy === "") return;
    const rsp = await AxiosApi2.courseScore(academy, courseId);
    if (rsp.data.sub_total_avg && rsp.data.sub_total_avg.length > 0) {
      setcourseScore(rsp.data.sub_total_avg[0]);
    }
  };

  const ScoreList = async (academy) => {
    try {
      const rsp = await AxiosApi2.scoreList(academy);
      if (rsp.data.sub_total_avg && rsp.data.sub_total_avg.length > 0) {
        setAcademyScore(rsp.data.sub_total_avg[0]);
      }
    } catch (error) {
      console.error("Error fetching score data:", error);
    }
  };

  const ShortCommentList = async (academy) => {
    const rsp = await AxiosApi2.shortCommentList(academy, courseId);
    setShortCommentList(rsp.data.list);
  };

  const SurvetList = async (academy) => {
    const rsp = await AxiosApi2.surveyList(academy, courseId);
    setSurvey(rsp.data.list);
  };

  return (
    <Content>
      <div>
        <Title>Academy: {academyName}</Title>
        <SubTitle>학원 리뷰 & & 강의 리뷰</SubTitle>

        {/* 학원 점수와 강의 점수 나란히 배치 */}
        <ScoreWrapper>
          {/* 학원 점수 */}
          <ScoreContainer>
            {academyScoure ? (
              <div>
                <ScoreTitle>
                  총점:{" "}
                  {academyScoure.totalAvg
                    ? academyScoure.totalAvg.toFixed(2)
                    : "N/A"}
                </ScoreTitle>
                <div>
                  <ScoreCategory>
                    취업 도움:{" "}
                    {academyScoure.avgJob
                      ? academyScoure.avgJob.toFixed(2)
                      : "N/A"}
                  </ScoreCategory>
                  <ScoreCategory>
                    강의 만족도:{" "}
                    {academyScoure.avgLecture
                      ? academyScoure.avgLecture.toFixed(2)
                      : "N/A"}
                  </ScoreCategory>
                  <ScoreCategory>
                    시설 만족도:{" "}
                    {academyScoure.avgFacilities
                      ? academyScoure.avgFacilities.toFixed(2)
                      : "N/A"}
                  </ScoreCategory>
                  <ScoreCategory>
                    강사 평가:{" "}
                    {academyScoure.avgTeacher
                      ? academyScoure.avgTeacher.toFixed(2)
                      : "N/A"}
                  </ScoreCategory>
                  <ScoreCategory>
                    교재 만족도:{" "}
                    {academyScoure.avgBooks
                      ? academyScoure.avgBooks.toFixed(2)
                      : "N/A"}
                  </ScoreCategory>
                  <ScoreCategory>
                    취업 지원:{" "}
                    {academyScoure.avgService
                      ? academyScoure.avgService.toFixed(2)
                      : "N/A"}
                  </ScoreCategory>
                </div>
              </div>
            ) : (
              <LoadingMessage>로딩 중...</LoadingMessage>
            )}
          </ScoreContainer>

          {/* 강의 점수 */}
          <ScoreContainer>
            {courseScore ? (
              <div>
                <ScoreTitle>
                  총점:{" "}
                  {courseScore.totalAvg
                    ? courseScore.totalAvg.toFixed(2)
                    : "N/A"}
                </ScoreTitle>
                <div>
                  <ScoreCategory>
                    취업 도움:{" "}
                    {courseScore.job !== undefined
                      ? courseScore.job.toFixed(2)
                      : "N/A"}
                  </ScoreCategory>
                  <ScoreCategory>
                    강의 만족도:{" "}
                    {courseScore.lecture !== undefined
                      ? courseScore.lecture.toFixed(2)
                      : "N/A"}
                  </ScoreCategory>
                  <ScoreCategory>
                    강사 만족도:{" "}
                    {courseScore.teacher !== undefined
                      ? courseScore.teacher.toFixed(2)
                      : "N/A"}
                  </ScoreCategory>
                  <ScoreCategory>
                    교재 만족도:{" "}
                    {courseScore.books !== undefined
                      ? courseScore.books.toFixed(2)
                      : "N/A"}
                  </ScoreCategory>
                  <ScoreCategory>
                    기술 만족도:{" "}
                    {courseScore.newTech !== undefined
                      ? courseScore.newTech.toFixed(2)
                      : "N/A"}
                  </ScoreCategory>
                  <ScoreCategory>
                    학업 만족도:{" "}
                    {courseScore.skillUp !== undefined
                      ? courseScore.skillUp.toFixed(2)
                      : "N/A"}
                  </ScoreCategory>
                </div>
              </div>
            ) : (
              <LoadingMessage>로딩 중...</LoadingMessage>
            )}
          </ScoreContainer>
        </ScoreWrapper>

        {/* 코멘트 리스트 */}
        <TableContainerWrapper>
          <TableContainer2>
            <Table2>
              <tbody>
                {shortCommentList && shortCommentList.length > 0 ? (
                  shortCommentList.map((short) => (
                    <TableRow2 key={short.list_id}>
                      <TableData2>{short.title}</TableData2>
                      <TableData2>{short.content}</TableData2>
                      <TableData2>{short.user_id}</TableData2>
                      <TableData2>{short.regDate}</TableData2>
                    </TableRow2>
                  ))
                ) : (
                  <TableRow2>
                    <TableData2 colSpan="4">
                      등록된 코멘트가 없습니다.
                    </TableData2>
                  </TableRow2>
                )}
              </tbody>
            </Table2>
          </TableContainer2>
        </TableContainerWrapper>

        {/* 설문 조사 */}
        <div>
          {survey.length > 0 ? (
            survey.map((surveyItem) => (
              <SurveyCard key={surveyItem.survey_id}>
                <SurveyTitle>설문 ID: {surveyItem.survey_id}</SurveyTitle>
                <SurveyContent>
                  <strong>교사:</strong> {surveyItem.teacher}
                </SurveyContent>
                <SurveyContent>
                  <strong>강의:</strong> {surveyItem.lecture}
                </SurveyContent>
                <SurveyContent>
                  <strong>시설:</strong> {surveyItem.facilities}
                </SurveyContent>
                <SurveyContent>
                  <strong>코멘트:</strong> {surveyItem.comment}
                </SurveyContent>
                <SurveyContent>
                  <strong>회원 ID:</strong> {surveyItem.member_id}
                </SurveyContent>
                <SurveyContent>
                  <strong>학원 ID:</strong> {surveyItem.academy_id}
                </SurveyContent>
                <SurveyContent>
                  <strong>강의 ID:</strong> {surveyItem.course_id}
                </SurveyContent>
              </SurveyCard>
            ))
          ) : (
            <LoadingMessage>등록된 설문 조사가 없습니다.</LoadingMessage>
          )}
        </div>
      </div>
    </Content>
  );
};

export default DetailCourse;
