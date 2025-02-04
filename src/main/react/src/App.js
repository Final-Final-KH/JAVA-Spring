import React from "react";
import { HashRouter as Router, Routes, Route } from "react-router-dom";
import Navbar from "./pages/Navbar"; // 네비게이터 바 컴포넌트 import
import Feed from "./components/Feed/Feed";
import Forum from "./components/Forums/Forum";
import News from "./pages/News";
import JobPost from "./pages/JobPost";
import JobDetail from "./pages/JobDetail"; // 구직 공고 상세 페이지
import Edu from "./pages/Edu";
import EduList from "./pages/EduList";
import Profile from "./pages/Profile";
import MyPage from "./pages/MyPage";
import Login from "./pages/Login";
import ScrollToTop from "./pages/ScrollToTop";

import { AuthProvider } from "./api/context/AuthContext";
import SignUp from "./pages/SignUp";
import Lecture from "./pages/kedu/course/lecture2";
import { RegionContextProvider } from "./api/provider/RegionSearchContextProvider2";
import Course from "./pages/kedu/course/course2";
import KakaoAuth from "./component/KakaoAuth";
import NaverCallback from "./component/NaverCallBack";
import { ToastContainer } from "react-toastify"; // ToastContainer 컴포넌트 import
import "react-toastify/dist/ReactToastify.css"; // 스타일 impor
import { MemberInfoContextProvider } from "./api/provider/MemberInfoContextProvider2";

import CreatePost from "./components/Forums/CreatePost";
import PostDetail from "./components/Forums/PostDetail";
import ForumPosts from "./components/Forums/ForumPosts";
import CourseList from "./pages/kedu/course/courseList2";
import DetailCourse from "./pages/kedu/course/detailCourse2";
function App() {
  return (
    <AuthProvider>
      <RegionContextProvider>
        <MemberInfoContextProvider>
          <Router>
            <Navbar />
            <ScrollToTop />
            {/* 네비게이터 바 렌더링 */}
            <Routes>
              {/* 메인 화면 */}
              <Route path="/" element={<Login />} />
              {/* 메인 화면 */}
              {/*고태경 회원 가입*/}
              <Route path="/signup" element={<SignUp />} />
              {/*고태경 회원 가입*/}
              {/* 고태경 로그인 */}
              <Route path="/login" element={<Login />} />
              {/* 고태경 로그인 */}
              {/* 고태경 제3자 로그인 */}
              <Route path="/auth/kakao/callback" element={<KakaoAuth />} />
              <Route
                path="/login/oauth2/code/naver"
                element={<NaverCallback />}
              />
              {/* 고태경 제3자 로그인 */}
              {/* 유진기 프로필 */}
              <Route path="/profile" element={<Profile />} />
              <Route path="/profile/mypage" element={<MyPage />} />
              {/* 유진기 프로필 */}
              {/* 유진기 피드 */}
              <Route path="/feed" element={<Feed />} />
              {/* 유진기 피드 */}
              {/* 유진기 채용공고 */}
              <Route path="/jobpost" element={<JobPost />} />
              <Route path="/job/:id" element={<JobDetail />} />
              {/* 유진기 채용공고 */}
              {/* 유진기 국비 교육*/}
              <Route path="/edu" element={<Edu />} />
              <Route path="/edulist" element={<EduList />} />
              {/* 유진기 국비 교육*/}
              {/* 임세호 포럼 */}
              <Route path="/forum" element={<Forum />} />
              {/* 각 게시글 세부 디테일 (실제 포스팅 페이지) */}
              <Route path="/forum/post/:postId" element={<PostDetail />} />
              {/* 포럼 게시글 생성 페이지 */}
              <Route path="/forum/create-post" element={<CreatePost />} />
              {/* 각 카테고리 내의 게시글 */}
              <Route
                path="/forum/category/:categoryId"
                element={<ForumPosts />}
              />
              {/* 임세호 포럼 */}
              {/* 고태경 뉴스  */}
              <Route path="/news" element={<News />} />
              {/* 고태경 뉴스  */}
              {/* 김요한 국비 교육*/}
              <Route path="/edu" element={<Edu />} />
              <Route path="/lecture/:academyId" element={<Lecture />} />
              <Route path="/academy" element={<Course />} />
              <Route path="/course" element={<CourseList />} />
              <Route
                path="/detail/:region/:academyName/:courseId"
                element={<DetailCourse />}
              />
              {/* 김요한 국비 교육*/}
            </Routes>
            {/* 최상위에 ToastContainer 배치 */}
            <ToastContainer
              position="top-right" // 위치 설정 (top-right)
              autoClose={2000} // 자동 닫힘 시간 설정 (5000ms = 5초)
              hideProgressBar={false} // 진행바 숨기기 여부
              newestOnTop={false} // 새 메시지가 위에 표시되도록 설정
              closeOnClick={true} // 클릭 시 닫히도록 설정
              rtl={false} // RTL (오른쪽에서 왼쪽으로) 설정
              pauseOnHover={true} // Hover 시 일시 정지
            />
          </Router>
        </MemberInfoContextProvider>
      </RegionContextProvider>
    </AuthProvider>
  );
}

export default App;
