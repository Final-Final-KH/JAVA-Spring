import { useEffect, useState } from "react";
import AxiosApi2 from "../../../api/AxiosApi2";
import "./CourseList2.css"; // CSS 파일을 import
import PageNavigate from "../common/PageNavigate";
import { useNavigate } from "react-router-dom";

const CourseList = () => {
  const [course, setCourse] = useState([]); // 수업 리스트 상태
  const [currentPage, setCurrentPage] = useState(0); // 현재 페이지 상태
  const [totalPage, setTotalPage] = useState(0); // 총 페이지 수 상태
  const navigate = useNavigate();

  useEffect(() => {
    fetchCourseData(); // 페이지에 해당하는 데이터 가져오기
  }, [currentPage]);

  useEffect(() => {
    const fetchTotalPage = async () => {
      try {
        const res = await AxiosApi2.coursePage(0, 5);
        setTotalPage(res.data.totalPages); // 전체 페이지 수 설정
      } catch (error) {
        console.log(error);
      }
    };
    fetchTotalPage();
  }, []);

  // course 데이터를 가져오는 함수
  const fetchCourseData = async () => {
    try {
      const rsp = await AxiosApi2.course(currentPage, 5); // 페이지에 해당하는 데이터 요청
      setCourse(rsp.data.list); // course 상태 업데이트
    } catch (error) {
      console.log(error);
    }
  };

  const handlePageChange = (pageNumber) => {
    setCurrentPage(pageNumber); // 페이지 번호 업데이트 (1부터 시작)
  };

  return (
    <div className="course-table-container">
      <h2>Course List</h2>
      <div></div>
      <table className="course-table">
        <thead>
          <tr>
            <th>Course Name</th> {/* courseName만 표시 */}
            <th>Academy Name</th>
            <th>Start Date</th>
            <th>End Date</th>
            <th>Region</th>
            <th>Auth</th>
            <th>TR Date</th>
            <th>Total Hour</th>
            <th>Employment Rate</th>
            <th>Price Total</th>
            <th>Self Payment</th>
          </tr>
        </thead>
        <tbody>
          {course && course.length > 0 ? (
            course.map((item) => (
              <tr
                key={item.courseId}
                onClick={() =>
                  navigate(
                    `/detail/${item.region}/${item.academyName}/${item.courseId}`
                  )
                }
              >
                <td>{item.courseName}</td>
                <td>{item.academyName}</td>
                <td>{new Date(item.startDate).toLocaleDateString()}</td>
                <td>{new Date(item.endDate).toLocaleDateString()}</td>
                <td>{item.region}</td>
                <td>{item.auth}</td>
                <td>{item.trDate}</td>
                <td>{item.totalHour}</td>
                <td>
                  {item.employmentRate === 0
                    ? "해당 사항 없음"
                    : item.employmentRate}
                </td>
                <td>{item.priceTotal.toLocaleString()}</td>
                <td>{item.selfPayment.toLocaleString()}</td>
              </tr>
            ))
          ) : (
            <tr>
              <td colSpan="12" className="no-data">
                No data available
              </td>
            </tr>
          )}
        </tbody>
      </table>
      <PageNavigate
        activePage={currentPage}
        itemsCountPerPage={5}
        totalItemsCount={totalPage} // 전체 항목 수 전달
        pageRangeDisplayed={5}
        onChange={handlePageChange} // 페이지 변경 시 호출
      />
    </div>
  );
};

export default CourseList;
