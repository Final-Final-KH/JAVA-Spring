import axios from "axios";
import AxiosInstance from "./AxiosInstance";
const KH_DOMAIN = "";

const AxiosApi2 = {
  csvInsert: async () => {
    return await axios.post(KH_DOMAIN + "/csv/insert");
  },
  courseList: async () => {
    return await axios.get(KH_DOMAIN + "/course/list", {
      withCredentials: true, // 쿠키를 보내기 위한 설정
    });
  },
  academy: async () => {
    return await axios.get(KH_DOMAIN + "/course/academy");
  },
  region: async () => {
    return await axios.get(KH_DOMAIN + "/course/region");
  },
  district: async (city) => {
    const params = {
      region_name: city,
    };
    return await axios.get(KH_DOMAIN + "/course/district", { params });
  },
  academyList: async (city_gu) => {
    console.log("api 파트", city_gu);
    const params = {
      region: city_gu,
    };
    return await axios.get(KH_DOMAIN + "/course/academy_list", { params });
  },
  lecture: async (region, academy_name) => {
    const params = {
      region: region,
      academy: academy_name,
    };
    return await axios.get(KH_DOMAIN + "/course/lecture", { params });
  },
  tokenMember: async (accessToken) => {
    try {
      const response = await AxiosInstance.get("/auth/current-user", {
        headers: {
          Authorization: `Bearer ${accessToken}`, // Authorization 헤더에 액세스 토큰 포함
        },
      });
      return response;
    } catch (error) {
      console.error("Error fetching user:", error);
      throw error;
    }
  },
  // 학원 아이디 필요
  regAcademy: async (memberId, academyId, academyName) => {
    console.log("아카데미 아이디", academyId);
    const params = {
      member_id: memberId,
      academy_id: academyId,
      academy_name: academyName,
    };
    return await axios.post(KH_DOMAIN + "/my_course/add_my_academy", params);
  },
  regCourse: async (courseName, courseId, memberId, academyId, academyName) => {
    console.log("api 강의 아이디", courseId);
    console.log("api 학원 아이디", academyId);
    console.log("api 멤버 아이디", memberId);
    console.log("api 강의 명", courseName);
    const params = {
      course: courseName,
      course_id: courseId,
      member_id: memberId,
      academy_id: academyId,
      academy: academyName,
    };
    return await axios.post(KH_DOMAIN + "/my_course/add_my_course", params);
  },
  regAcademyCheck: async (academyId, memberId) => {
    console.log("아카데미 체크", academyId);
    console.log("멤버체크", memberId);

    const params = {
      academy_id: academyId, // academyId와 memberId가 정확히 전달되는지 확인
      member_id: memberId,
    };
    return await axios.get(KH_DOMAIN + "/my_course/check_academy", { params });
  },

  /**
   * 포럼 관련 AxiosApi 처리
   */

  /**
   * 모든 포럼 카테고리 가져오기
   *
   * @returns {Promise<Object[]>} 포럼 카테고리 목록
   * @throws {Error} 카테고리 로드 중 오류 발생 시 예외
   */
  fetchCategories: async () => {
    try {
      const response = await AxiosInstance.get("/api/forums/categories");
      return response.data;
    } catch (error) {
      console.error("포럼 카테고리 가져오기 중 오류:", error);
      throw error;
    }
  },

  /**
   * 특정 카테고리의 게시글 가져오기
   *
   * @param {number} categoryId 카테고리 ID
   * @returns {Promise<Object[]>} 해당 카테고리의 게시글 목록
   * @throws {Error} 게시글 로드 중 오류 발생 시 예외
   */
  fetchPostsByCategory: async (categoryId) => {
    try {
      const response = await AxiosInstance.get(
        `/api/forums/posts?categoryId=${categoryId}`
      );
      return response.data;
    } catch (error) {
      console.error("카테고리 게시글 가져오기 중 오류:", error);
      throw error;
    }
  },

  /**
   * 게시글 생성
   *
   * @param {Object} data 새 게시글 데이터 (제목, 내용 등)
   * @returns {Promise<Object>} 생성된 게시글 데이터
   * @throws {Error} 게시글 생성 중 오류 발생 시 예외
   */
  createPost: async (data) => {
    try {
      const response = await AxiosInstance.post("/api/forums/posts", data);
      return response.data;
    } catch (error) {
      console.error(
        "게시글 생성 중 오류:",
        error.response?.data || error.message
      );
      throw error;
    }
  },

  /**
   * 게시글 수정
   *
   * @param {number} postId 수정할 게시글 ID
   * @param {Object} data 수정할 게시글 데이터 (제목, 내용 등)
   * @returns {Promise<Object>} 수정된 게시글 데이터
   * @throws {Error} 게시글 수정 중 오류 발생 시 예외
   */
  editPost: async (postId, data) => {
    try {
      const response = await AxiosInstance.put(
        `/api/forums/posts/${postId}`,
        data
      );
      return response.data;
    } catch (error) {
      console.error("게시글 수정 중 오류:", error);
      throw error;
    }
  },

  /**
   * 게시글 삭제
   *
   * @param {number} postId 삭제할 게시글 ID
   * @returns {Promise<Object>} 삭제 결과
   * @throws {Error} 게시글 삭제 중 오류 발생 시 예외
   */
  deletePost: async (postId) => {
    try {
      const response = await AxiosInstance.delete(
        `/api/forums/posts/${postId}`
      );
      return response.data;
    } catch (error) {
      console.error("게시글 삭제 중 오류:", error);
      throw error;
    }
  },

  /**
   * 게시글 좋아요 토글
   *
   * @param {number} postId 게시글 ID
   * @returns {Promise<Object>} 좋아요 상태
   * @throws {Error} 좋아요 토글 중 오류 발생 시 예외
   */
  toggleLikePost: async (postId) => {
    try {
      const response = await AxiosInstance.post(
        `/api/forums/posts/${postId}/like`
      );
      return response.data;
    } catch (error) {
      console.error("게시글 좋아요 토글 중 오류:", error);
      throw error;
    }
  },

  /**
   * 댓글 추가
   *
   * @param {number} postId 게시글 ID
   * @param {Object} data 댓글 데이터
   * @returns {Promise<Object>} 생성된 댓글 데이터
   * @throws {Error} 추가 중 오류 발생 시 예외
   */
  addComment: async (postId, data) => {
    try {
      const response = await AxiosInstance.post(
        `/api/forums/posts/${postId}/comments`,
        data
      );
      return response.data;
    } catch (error) {
      console.error("댓글 추가 중 오류:", error);
      throw error;
    }
  },

  /**
   * 댓글 수정
   *
   * @param {number} commentId 수정할 댓글 ID
   * @param {string} newContent 수정할 댓글 내용
   * @returns {Promise<Object>} 수정된 댓글 데이터
   * @throws {Error} 수정 중 오류 발생 시 예외
   */
  editComment: async (commentId, newContent) => {
    try {
      const response = await AxiosInstance.put(
        `/api/forums/comments/${commentId}`,
        { newContent }
      );
      return response.data;
    } catch (error) {
      console.error("댓글 수정 중 오류:", error);
      throw error;
    }
  },

  /**
   * 댓글 삭제
   *
   * @param {number} commentId 댓글 ID
   * @returns {Promise<Object>} 삭제 결과
   * @throws {Error} 삭제 중 오류 발생 시 예외
   */
  deleteComment: async (commentId) => {
    try {
      const response = await AxiosInstance.delete(
        `/api/forums/comments/${commentId}`
      );
      return response.data;
    } catch (error) {
      console.error("댓글 삭제 중 오류:", error);
      throw error;
    }
  },

  /**
   * 댓글 좋아요 토글
   *
   * @param {number} commentId 댓글 ID
   * @returns {Promise<Object>} 좋아요 상태
   * @throws {Error} 좋아요 토글 중 오류 발생 시 예외
   */
  toggleLikeComment: async (commentId) => {
    try {
      const response = await AxiosInstance.post(
        `/api/forums/comments/${commentId}/like`
      );
      return response.data;
    } catch (error) {
      console.error("댓글 좋아요 토글 중 오류:", error);
      throw error;
    }
  },

  /**
   * 신고 처리
   *
   * @param {number} contentId 신고 대상 ID (게시글 또는 댓글)
   * @param {string} type 대상 타입 ("posts" 또는 "comments")
   * @param {string} reason 신고 사유
   * @returns {Promise<Object>} 신고 결과
   * @throws {Error} 신고 처리 중 오류 발생 시 예외
   */
  reportContent: async (contentId, type, reason) => {
    try {
      const response = await AxiosInstance.post(
        `/api/forums/${type}/${contentId}/report`,
        { reason }
      );
      return response.data;
    } catch (error) {
      console.error(`신고 처리 중 오류 (${type}):`, error);
      throw error;
    }
  },
  checkRegCourse: async (courseId, memberId) => {
    console.log("api 멤버확인", memberId);
    const params = {
      course_id: courseId,
      member_id: memberId,
    };
    return await axios.get(KH_DOMAIN + "/my_course/check_course", { params });
  },
  regAcademyReview: async (data, memberId, academyId) => {
    console.log("api 취업 여부 전송 : ", data.employee_outcome);
    const params = {
      job: data.job,
      lecture: data.lecture,
      facilities: data.facilities,
      teacher: data.teacher,
      books: data.books,
      service: data.service,
      employee_outcome: data.employee_outcome,
      member_id: memberId, // memberId를 전송
      academy_id: academyId, // academyId를 전송
    };
    return await axios.post(KH_DOMAIN + "/academy_comment/create", params);
  },
  regCourseReview: async (score, memberId, course_id, academyId) => {
    const params = {
      job: score.job,
      lecture: score.lecture,
      teacher: score.teacher,
      books: score.books,
      newTech: score.newTech,
      skillUp: score.skillUp,
      employee_outcome: score.employmentOutcome,
      member_id: memberId,
      course_id: course_id,
      academy_id: academyId,
    };
    return await axios.post(KH_DOMAIN + "/course_comment/create", params);
  },
  coursesTaken: async (memberId, academyId) => {
    const params = {
      member_id: memberId,
      academy_id: academyId,
    };
    return await axios.get(KH_DOMAIN + "/my_course/my_course", { params });
  },
  regComment: async (title, comment, memberId, id, academyId, courseId) => {
    const params = {
      title: title,
      content: comment,
      member_id: memberId,
      user_id: id,
      academy_id: academyId,
      course_id: courseId,
    };
    return await axios.post(KH_DOMAIN + "/kedu_board/new", params);
  },
  regSurvey: async (data, memberId, id, academyId, courseId) => {
    const params = {
      teacher: data.teacher,
      lecture: data.lecture,
      facilities: data.facilities,
      comment: data.comment,
      member_id: memberId,
      academy_id: academyId,
      course_id: courseId,
    };
    return await axios.post(KH_DOMAIN + "/survey/create", params);
  },
  shortCommentList: async (academyId, courseId) => {
    const params = { academy_id: academyId, course_id: courseId };
    return await axios.get(KH_DOMAIN + "/kedu_board/short_comment", { params });
  },
  scoreList: async (academyId) => {
    const params = { academy_id: academyId };
    return await axios.get(KH_DOMAIN + "/academy_comment/sub_total_avg", {
      params,
    });
  },
  courseScore: async (academyId, courseId) => {
    const params = { academy_id: academyId, course_id: courseId };
    return await axios.get(KH_DOMAIN + "/course_comment/sub_total_avg", {
      params,
    });
  },
  course: async (page, size) => {
    const params = {
      page: page,
      size: size,
    };
    return await axios.get(KH_DOMAIN + "/course/list/page", { params });
  },
  coursePage: async (page, size) => {
    const params = {
      page: page,
      size: size,
    };
    return await axios.get(KH_DOMAIN + "/course/count", { params });
  },
  surveyList: async (academyId, courseId) => {
    const params = {
      course_id: courseId,
      academy_id: academyId,
    };
    return await axios.get(KH_DOMAIN + "/survey/list", { params });
  },
  getAcademyId: async (region, academyName) => {
    const params = {
      region: region,
      academy_name: academyName,
    };
    return await axios.get(KH_DOMAIN + "/course/getId", { params });
  },
  review: async (courseId, academyId) => {
    const params = {
      course_id: courseId,
      academy_id: academyId,
    };
    return await axios.get(KH_DOMAIN + "/course/detail", { params });
  },
  myAcademy: async (memberId) => {
    console.log("멤버 아이디 확인 : ", memberId);
    const params = {
      member_id: memberId,
    };
    return await axios.get(KH_DOMAIN + "/my_course/my_academy", { params });
  },
  myCourse: async (memberId) => {
    const params = {
      member_id: memberId,
    };
    return await axios.get(KH_DOMAIN + "/my_course/search_my_course", {
      params,
    });
  },
  myAcademyComment: async (memberId) => {
    const params = {
      member_id: memberId,
    };
    return await axios.get(KH_DOMAIN + "/my_page/my_academy_comment", {
      params,
    });
  },
  MyCourseComment: async (memberId) => {
    const params = {
      member_id: memberId,
    };
    return await axios.get(KH_DOMAIN + "/my_page/my_course_comment", {
      params,
    });
  },
  academyDelete: async (academyId, memberId) => {
    const params = {
      list_id: academyId,
      member_id: memberId,
    };
    return await axios.post(KH_DOMAIN + "/my_course/delete_my_academy", params);
  },
  courseDelete: async (courseId, memberId) => {
    const params = {
      list_id: courseId,
      member_id: memberId,
    };
    return await axios.post(KH_DOMAIN + "/my_course/delete_my_course", params);
  },
  academyReviewDelete: async (academyReviewId, memberId) => {
    const params = {
      academy_comment_id: academyReviewId,
      member_id: memberId,
    };
    return await axios.post(KH_DOMAIN + "/academy_comment/delete", null, {
      params,
    });
  },
  courseReviewDelete: async (courseReviewId, memberId) => {
    const params = {
      course_comment_id: courseReviewId,
      member_id: memberId,
    };
    return await axios.post(KH_DOMAIN + "/course_comment/delete", null, {
      params,
    });
  },
};

export default AxiosApi2;
