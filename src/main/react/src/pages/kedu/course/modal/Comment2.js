import { useParams } from "react-router-dom";
import { ModalStyle, ModalButton } from "./ModalStyle2";
import { useContext, useEffect, useState } from "react";
import AxiosApi2 from "../../../../api/AxiosApi2";
import { toast } from "react-toastify";
import { Button2, CancelButton2, Input2, Select2 } from "../style/style2";
import { MemberInfoContext } from "../../../../api/provider/MemberInfoContextProvider2";

const CommentModal = (props) => {
  const { close, open } = props;
  const { academyId } = useParams();
  const [member1, setMember] = useState([]);
  const [comment, setComment] = useState("");
  const [courseList, setCourseList] = useState([]);
  const [courseId, setCourseId] = useState("");
  const [title, setTitle] = useState("");
  const { member } = useContext(MemberInfoContext);
  // 로그인된 사용자 정보 가져오기
  useEffect(() => {
    const fetchData = () => {
      const memberData = localStorage.getItem("keduMember");
      if (memberData) {
        const parsedMember = JSON.parse(memberData);
        setMember(parsedMember);
      }
    };
    fetchData();
    console.log("멤버 아이디", member);
  }, []);

  // 코멘트 등록
  const RegComment = async () => {
    if (!courseId || !title || !comment) {
      toast.error("모든 필드를 작성해주세요.");
      return;
    }

    try {
      const rsp = await AxiosApi2.regComment(
        title,
        comment,
        member1.memberId,
        member.email,
        academyId,
        courseId
      );
      if (rsp.data === true) {
        toast.success("코멘트 등록에 성공하셨습니다.");
        close(); // 등록 후 모달 닫기
      } else {
        toast.error("코멘트 등록에 실패했습니다.");
      }
    } catch (error) {
      toast.error("오류가 발생했습니다. 다시 시도해 주세요.");
    }
  };

  // 수강한 강의 목록 조회
  const CoursesTaken = async () => {
    try {
      const rsp = await AxiosApi2.coursesTaken(member.memberId, academyId);
      setCourseList(rsp.data.list);
    } catch (error) {
      toast.error("강의 조회에 실패했습니다.");
    }
  };

  return (
    <ModalStyle>
      <div
        className={open ? "modal fade show d-block" : "modal fade"}
        tabIndex="-1"
        aria-labelledby="commentModalLabel"
        aria-hidden="true"
      >
        {open && (
          <section>
            <div className="modal-dialog">
              <div className="modal-content">
                <header className="modal-header">
                  <h2 className="modal-title">한 줄 코멘트 작성</h2>
                  <Button2
                    type="button"
                    className="btn-close"
                    aria-label="Close"
                    onClick={close}
                  />
                </header>
                <main>
                  {/* 강의 선택 */}
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
                    <Button2 onClick={CoursesTaken}>
                      내가 수강한 강의 조회
                    </Button2>
                  )}

                  <br />
                  {/* 카테고리 선택 */}
                  <label>카테고리를 선택하세요</label>
                  <Select2 onChange={(e) => setTitle(e.target.value)}>
                    <option value="">카테고리를 선택하세요</option>
                    <option value="GOOD">좋아요(GOOD)</option>
                    <option value="BAD">별로에요(BAD)</option>
                  </Select2>

                  {/* 코멘트 입력 */}
                  <Input2
                    type="text"
                    value={comment}
                    onChange={(e) => setComment(e.target.value)}
                    placeholder="코멘트를 입력하세요."
                  />

                  {/* 등록 버튼 */}
                  <Button2 onClick={RegComment}>등록</Button2>
                </main>
                <footer>
                  <CancelButton2
                    type="button"
                    className="btn btn-secondary"
                    onClick={close}
                  >
                    취소
                  </CancelButton2>
                </footer>
              </div>
            </div>
          </section>
        )}
      </div>
    </ModalStyle>
  );
};

export default CommentModal;
