import { useContext, useEffect, useRef, useState } from "react";
import AxiosApi2 from "../../../../api/AxiosApi2";
import { ModalStyle } from "./ModalStyle2";
import { useParams } from "react-router-dom";
import { toast } from "react-toastify";
import { RegionSearchContext } from "../../../../api/provider/RegionSearchContextProvider2";

const RegAcademyReviewModal = (props) => {
  const { close, open } = props;
  const { academyId } = useParams(); // useParams에서 academyId를 가져옵니다.
  const academyNameRef = useRef("");
  const regionRef = useRef("");
  const { searchKeyword, academyName } = useContext(RegionSearchContext);

  const [member, setMember] = useState(null); // member 상태를 null로 초기화
  const [score, setScore] = useState({
    job: 50,
    service: 50,
    books: 50,
    facilities: 50,
    teacher: 50,
    lecture: 50,
  });

  const [employmentOutcome, setEmploymentOutcome] = useState("no"); // 취업 여부 기본값은 "no"

  useEffect(() => {
    const memberData = localStorage.getItem("keduMember");
    if (memberData) {
      setMember(JSON.parse(memberData)); // member를 객체로 파싱하여 상태에 저장
    }
  }, []);

  useEffect(() => {
    academyNameRef.current = localStorage.getItem("academyName");
    regionRef.current = localStorage.getItem("region");
  }, [searchKeyword, academyName]);

  // 점수 변경 핸들러
  const handleScoreChange = (e, category) => {
    setScore({ ...score, [category]: e.target.value });
  };

  // 취업 여부 변경 핸들러
  const handleEmploymentOutcomeChange = (e) => {
    setEmploymentOutcome(e.target.value);
  };

  // 폼 제출 핸들러
  const handleSubmit = async (e) => {
    e.preventDefault();

    if (
      Object.values(score).some(
        (value) => value === "" || value < 0 || value > 100
      )
    ) {
      toast.error("모든 점수를 0에서 100 사이로 입력해주세요.");
      return;
    }

    const submissionData = {
      ...score,
      employee_outcome: employmentOutcome === "yes", // 취업 여부 처리
    };

    if (member && member.memberId && academyId) {
      try {
        const rsp = await AxiosApi2.regAcademyReview(
          submissionData,
          member.memberId, // member 객체에서 memberId 가져오기
          academyId // academyId는 useParams에서 가져오기
        );
        if (rsp.data === true) {
          toast.success("학원 리뷰 등록에 성공하셨습니다.");
        } else {
          toast.error("리뷰 등록에 실패했습니다.");
        }
      } catch (error) {
        toast.error("오류가 발생했습니다. 다시 시도해 주세요.");
      }
    } else {
      toast.error("회원 정보 또는 학원 ID가 잘못되었습니다.");
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
                  <h2 className="modal-title">학원 리뷰 작성</h2>
                  <button
                    type="button"
                    className="btn-close"
                    aria-label="Close"
                    onClick={() => close()}
                  />
                </header>
                <main className="modal-body">
                  <form onSubmit={handleSubmit}>
                    {/* 직무 연관성 */}
                    <div className="form-group">
                      <label>직무 연관성 (0-100): </label>
                      <input
                        type="number"
                        value={score.job}
                        min="0"
                        max="100"
                        onChange={(e) => handleScoreChange(e, "job")}
                        className="form-control"
                      />
                    </div>

                    {/* 서비스 만족도 */}
                    <div className="form-group">
                      <label>서비스 만족도 (0-100): </label>
                      <input
                        type="number"
                        value={score.service}
                        min="0"
                        max="100"
                        onChange={(e) => handleScoreChange(e, "service")}
                        className="form-control"
                      />
                    </div>

                    {/* 도서 내용 */}
                    <div className="form-group">
                      <label>도서 내용 (0-100): </label>
                      <input
                        type="number"
                        value={score.books}
                        min="0"
                        max="100"
                        onChange={(e) => handleScoreChange(e, "books")}
                        className="form-control"
                      />
                    </div>

                    {/* 시설 만족도 */}
                    <div className="form-group">
                      <label>시설 만족도 (0-100): </label>
                      <input
                        type="number"
                        value={score.facilities}
                        min="0"
                        max="100"
                        onChange={(e) => handleScoreChange(e, "facilities")}
                        className="form-control"
                      />
                    </div>

                    {/* 강사 만족도 */}
                    <div className="form-group">
                      <label>강사 만족도 (0-100): </label>
                      <input
                        type="number"
                        value={score.teacher}
                        min="0"
                        max="100"
                        onChange={(e) => handleScoreChange(e, "teacher")}
                        className="form-control"
                      />
                    </div>

                    {/* 강의 만족도 */}
                    <div className="form-group">
                      <label>강의 만족도 (0-100): </label>
                      <input
                        type="number"
                        value={score.lecture}
                        min="0"
                        max="100"
                        onChange={(e) => handleScoreChange(e, "lecture")}
                        className="form-control"
                      />
                    </div>

                    {/* 취업 여부 */}
                    <div className="form-group">
                      <label>취업 여부: </label>
                      <select
                        value={employmentOutcome}
                        onChange={handleEmploymentOutcomeChange}
                        className="form-control"
                      >
                        <option value="no">No</option>
                        <option value="yes">Yes</option>
                      </select>
                    </div>

                    <button
                      type="submit"
                      className="btn btn-primary w-100 mt-3"
                    >
                      제출
                    </button>
                  </form>
                </main>
                <footer className="modal-footer">
                  <button
                    type="button"
                    className="btn btn-secondary"
                    onClick={() => close()}
                  >
                    취소
                  </button>
                </footer>
              </div>
            </div>
          </section>
        )}
      </div>
    </ModalStyle>
  );
};

export default RegAcademyReviewModal;
