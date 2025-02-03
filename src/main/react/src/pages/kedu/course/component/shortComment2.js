import { useContext, useEffect, useRef, useState } from "react";
import AxiosApi2 from "../../../../api/AxiosApi2";
import { useParams } from "react-router-dom";
import { RegionSearchContext } from "../../../../api/provider/RegionSearchContextProvider2";
import {
  Select2,
  Button2,
  TableContainer2,
  Table2,
  TableRow2,
  TableData2,
  Content,
} from "../style/style2";

const ShortComment = () => {
  const { academyId } = useParams("");
  const [list, setList] = useState([]);
  const [courseId, setCourseId] = useState("");
  const [lectureList, setLectureList] = useState([]);
  const { searchKeyword, academyName } = useContext(RegionSearchContext); // RegionSearchContext로 변경
  const academyNameRef = useRef("");
  const regionRef = useRef("");
  const [courseName, setCourseName] = useState("");

  useEffect(() => {
    academyNameRef.current = localStorage.getItem("academyName");
    regionRef.current = localStorage.getItem("region");
  }, [searchKeyword, academyName]); // searchKeyword, academyName 값이 변경될 때마다 재호출

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
  };

  const ShortCommentList = async () => {
    const rsp = await AxiosApi2.shortCommentList(academyId, courseId);
    setList(rsp.data.list);
  };

  return (
    <Content>
      {/* 내가 수강한 강의 조회 버튼 */}
      <Button2 onClick={() => LectureList()}>조회할 강의 찾기</Button2>

      {/* 강의 목록이 있을 때만 표시 */}
      {lectureList.length > 0 && (
        <>
          <div>
            <Select2
              onChange={(e) => {
                const selectedOption = e.target.value;

                // "강의를 선택하세요"를 선택했을 때 아무 작업도 하지 않도록 처리
                if (selectedOption === "") {
                  return;
                }

                // 선택된 강의를 찾기
                const selectedLecture = lectureList.find(
                  (lecture) => lecture.course_name === selectedOption
                );

                // selectedLecture가 존재하는 경우에만 ChoseCourse 호출
                if (selectedLecture) {
                  ChoseCourse(
                    selectedLecture.course_name,
                    selectedLecture.course_id
                  );
                } else {
                  console.error("선택된 강의를 찾을 수 없습니다.");
                }
              }}
            >
              <option value={""}>강의를 선택하세요</option>
              {/* 강의 목록 출력 */}
              {lectureList.map((lecture, index) => (
                <option key={index} value={lecture.course_name}>
                  {lecture.course_name}
                </option>
              ))}
            </Select2>

            <Button2 onClick={() => ShortCommentList()}>검색</Button2>
          </div>

          {/* 한줄 코멘트 목록 테이블 */}
          <TableContainer2>
            <Table2>
              <tbody>
                {list && list.length > 0 ? (
                  list.map((short) => (
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
        </>
      )}
    </Content>
  );
};

export default ShortComment;
