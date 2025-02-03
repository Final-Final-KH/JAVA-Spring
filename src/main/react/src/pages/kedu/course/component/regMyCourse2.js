import { useContext, useEffect, useRef, useState } from "react";
import { useParams } from "react-router-dom";
import { MemberInfoContext } from "../../../../api/provider/MemberInfoContextProvider2";
import { toast } from "react-toastify"; // react-toastify의 toast 함수를 import 합니다.
import AxiosApi2 from "../../../../api/AxiosApi2";
import { RegionSearchContext } from "../../../../api/provider/RegionSearchContextProvider2";
import { Button2, Content, Select2 } from "../style/style2";

const RegMyCourse = () => {
  const [isMyCourse, setIsMyCourse] = useState(false);
  const [lectureList, setLectureList] = useState([]);
  const { academyId } = useParams();
  const [courseName, setCourseName] = useState("");
  const [courseId, setCourseId] = useState("");
  const { member } = useContext(MemberInfoContext);
  const { searchKeyword, academyName } = useContext(RegionSearchContext); // RegionSearchContext로 변경
  const academyNameRef = useRef("");
  const regionRef = useRef("");

  useEffect(() => {
    academyNameRef.current = localStorage.getItem("academyName");
    regionRef.current = localStorage.getItem("region");
  }, [searchKeyword, academyName]); // searchKeyword, academyName 값이 변경될 때마다 재호출

  // regMyCourse
  const LectureList = async () => {
    const rsp = await AxiosApi2.lecture(
      regionRef.current,
      academyNameRef.current
    );
    setLectureList(rsp.data.list);
  };

  const ChoseCourse = (courseName, courseId) => {
    setCourseName(courseName);
    setCourseId(courseId);
    CheckRegCourse(courseId, member.memberId);
  };
  const CheckRegCourse = async (courseId, memberId) => {
    const rsp = await AxiosApi2.checkRegCourse(courseId, memberId);
    if (rsp.data === true) {
      toast.warn("이미 등록된 강의입니다.");
      setIsMyCourse(true); // 강의가 이미 등록된 경우 상태를 true로 설정
    } else {
      setIsMyCourse(false); // 등록되지 않은 경우 상태를 false로 설정
      toast.success("등록 가능한 강의입니다.");
    }
  };

  const RegCourse = async () => {
    if (courseName === "" || courseId === "") {
      toast.warn("수강 정보를 선택하세요.");
      return; // 강의가 선택되지 않은 경우 더 이상 실행하지 않음
    }

    const isRegistered = await CheckRegCourse(courseId, member.memberId); // 반환값을 확인하고 상태 관리

    if (isRegistered) {
      toast.warn("이미 등록된 강의는 등록할 수 없습니다.");
      return;
    }

    try {
      const rsp = await AxiosApi2.regCourse(
        courseName,
        courseId,
        member.memberId,
        academyId,
        academyNameRef.current
      );
      if (rsp.data === true) {
        toast.success("학원 강의가 성공적으로 등록되었습니다!");
      } else {
        toast.error("학원 강의 등록에 실패했습니다.");
      }
    } catch (error) {
      toast.error("학원 강의 등록에 실패했습니다.");
      console.error("Error registering course:", error);
    }
  };

  return (
    <Content>
      <h4>내가 수강한 강의 등록</h4>
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

            <Button2 onClick={RegCourse}>나의 강의 등록 하기</Button2>
          </>
        ) : (
          <p>강의 리스트가 없습니다.</p>
        )}
      </div>
    </Content>
  );
};

export default RegMyCourse;
