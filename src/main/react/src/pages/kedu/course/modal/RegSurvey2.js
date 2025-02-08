import { useParams } from "react-router-dom";
import { ModalStyle } from "./ModalStyle2";
import { useEffect, useState } from "react";
import AxiosApi2 from "../../../../api/AxiosApi2";
import { toast } from "react-toastify";
import { Button2, CancelButton2, Input2, Select2 } from "../style/style2";

// 입력 필드 재사용을 위한 컴포넌트
const InputField = ({ label, value, onChange, placeholder, type = "text" }) => (
  <div>
    <label>{label}:</label>
    <input
      type={type}
      value={value}
      onChange={onChange}
      placeholder={placeholder}
    />
  </div>
);

const RegSurveyModal = (props) => {
  const { close, open } = props;
  const { academyId } = useParams();
  const [member, setMember] = useState(null);
  const [courseList, setCourseList] = useState([]);
  const [courseId, setCourseId] = useState("");
  const [teacher, setTeacher] = useState("");
  const [lecture, setLecture] = useState("");
  const [facilities, setFacilities] = useState("");
  const [comment, setComment] = useState("");

  // useEffect 내에서 비동기 함수 호출
  useEffect(() => {
    const fetchData = async () => {
      const memberData = localStorage.getItem("keduMember");
      if (memberData) {
        const parsedMember = JSON.parse(memberData);
        setMember(parsedMember);
      }
    };

    fetchData();
  }, []);

  // 강의 조회 함수
  const CoursesTaken = async () => {
    console.log("멤버 정보 가져오는지 확인 : ", member);
    if (member && member.memberId) {
      try {
        const rsp = await AxiosApi2.coursesTaken(member.memberId, academyId);
        setCourseList(rsp.data.list);
      } catch (error) {
        console.error("Error fetching courses:", error);
      }
    }
  };

  // 폼 제출 핸들러
  const handleSubmit = async (e) => {
    e.preventDefault();

    if (!courseId) {
      toast.error("강의를 선택해주세요.");
      return;
    }

    const formData = {
      teacher,
      lecture,
      facilities,
      comment,
    };

    try {
      const rsp = await AxiosApi2.regSurvey(
        formData,
        member.memberId,
        member.email,
        academyId,
        courseId
      );

      if (rsp.data === true) {
        toast.success("설문조사 등록에 성공하였습니다.");
      } else {
        toast.warn("설문조사 등록에 실패하였습니다.");
      }

      // 제출 후 입력란 비우기
      setTeacher("");
      setLecture("");
      setFacilities("");
      setComment("");
      setCourseId(""); // 선택한 강의 초기화
    } catch (error) {
      toast.error("설문조사 등록에 문제가 발생했습니다.");
      console.error(error);
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
                  <h2 className="modal-title">설문 조사 작성</h2>
                  <Button2
                    type="button"
                    className="btn-close"
                    aria-label="Close"
                    onClick={() => close()}
                  />
                </header>
                <main className="modal-body">
                  {/* 강의 선택 */}
                  {courseList.length > 0 ? (
                    <div>
                      <label htmlFor="courseSelect">강의명</label>
                      <Select2
                        id="courseSelect"
                        value={courseList.course_id}
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
                    {/* 입력 필드들 */}
                    <Input2
                      label="강사"
                      value={teacher}
                      onChange={(e) => setTeacher(e.target.value)}
                      placeholder="강사님에 대한 정보를 입력해주세요"
                    />
                    <Input2
                      label="강의명"
                      value={lecture}
                      onChange={(e) => setLecture(e.target.value)}
                      placeholder="강의에 대해서 정보를 입력해주세요"
                    />
                    <Input2
                      label="시설"
                      value={facilities}
                      onChange={(e) => setFacilities(e.target.value)}
                      placeholder="시설에 대해 입력하세요"
                    />
                    <Input2
                      label="수업 난이도"
                      value={comment}
                      onChange={(e) => setComment(e.target.value)}
                      placeholder="수업 난이도를 입력하세요"
                    />

                    <Button2 type="submit">제출</Button2>
                  </form>
                </main>
                <footer className="modal-footer">
                  <CancelButton2
                    type="button"
                    className="btn btn-secondary"
                    onClick={() => close()}
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

export default RegSurveyModal;
