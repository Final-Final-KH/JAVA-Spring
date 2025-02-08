import { toast } from "react-toastify";
import AxiosApi2 from "../../../../api/AxiosApi2";
import { ModalStyle } from "./ModalStyle2";
import { useEffect, useState } from "react";
import { useParams } from "react-router-dom";
import { Button2, Input2, Select2 } from "../style/style2";

const RegCourseReviewModal = (props) => {
  const { close, open } = props;
  const { academyId } = useParams();
  const [member, setMember] = useState(null); // member 상태를 null로 초기화
  const [courseList, setCourseList] = useState([]); // 수강한 강의 목록
  const [courseId, setCourseId] = useState(""); // 선택된 강의 ID

  // 점수 상태 정의
  const [score, setScore] = useState({
    job: 50,
    books: 50,
    facilities: 50,
    teacher: 50,
    lecture: 50,
    newTech: 50,
    skillUp: 50,
  });

  const [employmentOutcome, setEmploymentOutcome] = useState("no");

  // 비동기 함수로 회원 정보 로드
  useEffect(() => {
    const fetchData = async () => {
      const memberData = localStorage.getItem("keduMember");
      if (memberData) {
        const parsedMember = JSON.parse(memberData);
        setMember(parsedMember); // member를 객체로 파싱하여 상태에 저장
      }
    };

    fetchData();
  }, []);

  // 점수 변경 핸들러
  const handleScoreChange = (e, category) => {
    setScore({ ...score, [category]: parseInt(e.target.value) });
  };

  // 취업 여부 변경 핸들러
  const handleEmploymentOutcomeChange = (e) => {
    setEmploymentOutcome(e.target.value);
  };

  // 강의 조회
  const CoursesTaken = async () => {
    try {
      const rsp = await AxiosApi2.coursesTaken(member.memberId, academyId);
      setCourseList(rsp.data.list);
    } catch (error) {
      console.error("Error fetching courses:", error);
    }
  };

  // 폼 제출 핸들러
  const handleSubmit = async (e) => {
    e.preventDefault();

    // 점수 유효성 검사
    if (Object.values(score).some((val) => val < 0 || val > 100)) {
      toast.error("모든 점수는 0과 100 사이여야 합니다.");
      return;
    }

    const submissionData = {
      job: score.job,
      lecture: score.lecture,
      teacher: score.teacher,
      books: score.books,
      newTech: score.newTech,
      skillUp: score.skillUp,
      employee_outcome: employmentOutcome === "yes",
    };

    if (member && member.memberId && courseId) {
      const rsp = await AxiosApi2.regCourseReview(
        submissionData,
        member.memberId,
        courseId,
        academyId
      );
      if (rsp.data === true) {
        toast.success("강의 리뷰 등록에 성공하셨습니다.");
      } else {
        toast.warn("강의 리뷰 등록에 실패하였습니다.");
      }
    } else {
      toast.error("회원 정보 또는 강의 ID가 잘못되었습니다.");
    }
  };

  return (
    <ModalStyle>
      <div
        className={open ? "modal fade show d-block" : "modal fade"}
        tabIndex="-1"
        aria-labelledby="exampleModalLabel"
        aria-hidden="true"
      >
        {open && (
          <section>
            <div className="modal-dialog">
              <div className="modal-content">
                <header className="modal-header">
                  <h2 className="modal-title">강의 리뷰 작성</h2>
                  <Button2
                    type="button"
                    className="btn-close"
                    aria-label="Close"
                    onClick={() => close()}
                  />
                </header>
                <main className="modal-body">
                  {courseList.length > 0 ? (
                    <div>
                      <label htmlFor="courseSelect">강의명</label>
                      <Select2
                        id="courseSelect"
                        onChange={(e) => setCourseId(e.target.value)}
                      >
                        <option value="">강의를 선택하세요</option>
                        {courseList.map((course) => (
                          <option
                            key={course.course_id}
                            value={course.course_id}
                          >
                            {course.course}
                          </option>
                        ))}
                      </Select2>
                    </div>
                  ) : (
                    <Button2 onClick={() => CoursesTaken()}>
                      내가 수강한 강의 조회
                    </Button2>
                  )}

                  <form onSubmit={handleSubmit}>
                    {/* 점수 입력 */}
                    {[
                      "job",
                      "newTech",
                      "skillUp",
                      "books",
                      "teacher",
                      "lecture",
                      "facilities",
                    ].map((category) => (
                      <div key={category}>
                        <label>{category} (0-100): </label>
                        <Input2
                          type="number"
                          value={score[category]}
                          min="0"
                          max="100"
                          onChange={(e) => handleScoreChange(e, category)}
                        />
                      </div>
                    ))}

                    {/* 취업 여부 선택 */}
                    <div>
                      <label>취업 여부: </label>
                      <Select2
                        value={employmentOutcome}
                        onChange={handleEmploymentOutcomeChange}
                      >
                        <option value="no">No</option>
                        <option value="yes">Yes</option>
                      </Select2>
                    </div>

                    <Button2 type="submit">제출</Button2>
                  </form>
                </main>
                <footer className="modal-footer">
                  <Button2
                    type="button"
                    className="btn btn-secondary"
                    onClick={() => close()}
                  >
                    취소
                  </Button2>
                </footer>
              </div>
            </div>
          </section>
        )}
      </div>
    </ModalStyle>
  );
};

export default RegCourseReviewModal;
