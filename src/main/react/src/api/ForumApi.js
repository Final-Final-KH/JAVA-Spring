import AxiosInstance from "../axios/AxiosInstanse"; // Axios 인스턴스 불러오기

/**
 * 포럼 관련 API 관리
 */
const ForumApi = {
  /**
   * 모든 포럼 카테고리 가져오기
   * @returns {Promise<Object[]>} 카테고리 목록 반환
   */
  fetchCategories: async () => {
    try {
      const response = await AxiosInstance.get("/api/forums/categories"); // API 호출
      console.log("Fetched Categories:", response.data); // 디버그 로그 출력
      return response.data; // 카테고리 데이터 반환
    } catch (error) {
      console.error("포럼 카테고리 가져오기 중 오류:", error); // 에러 로그 출력
      throw error; // 에러를 호출한 함수로 전달
    }
  },

  incrementViewCount: async (postId) => {
    try {
      // 서버에 해당 게시글 ID의 조회수를 증가시키는 요청을 보냄
      await AxiosInstance.post(`/api/forums/posts/${postId}/increment-view`);
    } catch (error) {
      console.error("조회수 증가 중 오류 발생:", error); // 에러 발생 시 콘솔 출력
    }
  },

  /**
   * 특정 카테고리의 게시글 가져오기
   * @param {number} categoryId - 카테고리 ID
   * @param {number} page - 요청할 페이지 번호 (기본값: 0)
   * @param {number} size - 페이지당 게시글 개수 (기본값: 10)
   * @returns {Promise<Object>} 게시글 데이터 반환
   */
  fetchPostsByCategory: async (categoryId, page = 0, size = 10) => {
    try {
      console.log(
        "Fetching posts for category ID:",
        categoryId,
        "Page:",
        page,
        "Size:",
        size
      );
      const response = await AxiosInstance.get(
        `/api/forums/posts?categoryId=${categoryId}&page=${page}&size=${size}`
      );
      return response.data; // API 응답 데이터 반환
    } catch (error) {
      console.error("카테고리 게시글 가져오기 중 오류:", error);
      throw error; // 오류 전달
    }
  },

  /**
   * 특정 카테고리 ID로 게시글 가져오기 (fetchPostsByCategory의 별칭)
   * @param {number} categoryId - 카테고리 ID
   * @param {number} page - 요청할 페이지 번호 (기본값: 0)
   * @param {number} size - 페이지당 게시글 수 (기본값: 10)
   * @returns {Promise<Object[]>} 게시글 데이터 배열 반환
   */
  getPostsByCategoryId: async (categoryId, page = 0, size = 10) => {
    return ForumApi.fetchPostsByCategory(categoryId, page, size); // 기존 함수 호출
  },

  /**
   * 게시글 상세 정보 가져오기
   * @param {number} postId - 게시글 ID
   * @returns {Promise<Object>} 게시글 상세 데이터 반환
   */
  getPostById: async (postId) => {
    try {
      const response = await AxiosInstance.get(`/api/forums/posts/${postId}`);
      return response.data; // 서버 응답 데이터 반환
    } catch (error) {
      console.error("게시글 데이터 가져오기 중 오류 발생:", error);
      throw error; // 오류 전달
    }
  },

  /**
   * 게시글 생성
   * @param {Object} data - 새 게시글 데이터 (제목, 내용 등)
   * @returns {Promise<Object>} 생성된 게시글 데이터 반환
   */
  createPost: async (data) => {
    try {
      const response = await AxiosInstance.post("/api/forums/posts", data); // 게시글 생성 API 호출
      return response.data; // 생성된 게시글 데이터 반환
    } catch (error) {
      console.error("게시글 생성 중 오류:", error); // 에러 로그 출력
      throw error;
    }
  },

  /**
   * 게시글 제목 수정
   * @param {number} postId - 수정할 게시글 ID
   * @param {Object} data - 수정 요청 데이터 (새로운 제목 포함)
   * @param {number} loggedInMemberId - 로그인된 사용자 ID
   * @param {boolean} isAdmin - 관리자 여부
   * @returns {Promise<Object>} 수정된 게시글 데이터 반환
   */
  updatePostTitle: async (postId, data, loggedInMemberId, isAdmin) => {
    if (!loggedInMemberId) {
      throw new Error("Logged-in Member ID is required.");
    }
    try {
      const response = await AxiosInstance.put(
        `/api/forums/posts/${postId}/title?loggedInMemberId=${loggedInMemberId}&isAdmin=${isAdmin}`,
        data
      );
      return response.data; // 서버 응답 데이터 반환
    } catch (error) {
      console.error("게시글 제목 수정 중 오류 발생:", error);
      throw error;
    }
  },

  /**
   * 게시글 내용 수정
   * @param {number} postId - 수정할 게시글 ID
   * @param {Object} data - 수정 요청 데이터 (새로운 내용 포함)
   * @param {number} loggedInMemberId - 로그인된 사용자 ID
   * @param {boolean} isAdmin - 관리자 여부
   * @returns {Promise<Object>} 수정된 게시글 데이터 반환
   */
  updatePostContent: async (postId, data, loggedInMemberId, isAdmin) => {
    if (!loggedInMemberId) {
      throw new Error("Logged-in Member ID is required.");
    }
    try {
      const response = await AxiosInstance.put(
        `/api/forums/posts/${postId}/content?loggedInMemberId=${loggedInMemberId}&isAdmin=${isAdmin}`,
        data
      );
      return response.data; // 서버 응답 데이터 반환
    } catch (error) {
      console.error("게시글 내용 수정 중 오류 발생:", error);
      throw error;
    }
  },

  /**
   * 게시글 삭제
   * @param {number} postId - 삭제할 게시글 ID
   * @param {number} loggedInMemberId - 로그인된 사용자 ID
   * @param {string} removedBy - 삭제 수행자 (작성자 이름 또는 ADMIN)
   * @returns {Promise<void>} 삭제 결과
   */
  deletePost: async (postId, loggedInMemberId, removedBy, isAdmin) => {
    try {
      const url = `/api/forums/posts/${postId}?loggedInMemberId=${loggedInMemberId}&removedBy=${encodeURIComponent(
        removedBy
      )}&isAdmin=${isAdmin}`;
      console.log(`Request URL: ${url}`); // 디버깅 로그
      await AxiosInstance.delete(url); // DELETE 요청
    } catch (error) {
      console.error("게시글 삭제 중 오류:", error);
      throw error;
    }
  },

  /**
   * 특정 게시글의 댓글 가져오기
   * @param {number} postId - 게시글 ID
   * @returns {Promise<Object[]>} 댓글 데이터 배열 반환
   */
  getCommentsByPostId: async (postId) => {
    try {
      // const response = await AxiosInstance.get(
      //   `/api/forums/posts/${postId}/comments`
      // );
      const response = await AxiosInstance.get(
        `/api/forums/comments/${postId}`
      );
      console.log("Fetched Comments:", response.data); // 디버그 로그
      return response.data; // 서버 응답 데이터 반환
    } catch (error) {
      console.error("댓글 데이터를 가져오는 중 오류 발생:", error); // 에러 로그 출력
      throw error; // 에러 전달
    }
  },

  /**
   * 게시글 상세 정보 가져오기
   * @param {number} postId - 게시글 ID
   * @returns {Promise<Object>} 게시글 상세 데이터 반환
   */ getPostById: async (postId) => {
    try {
      const response = await AxiosInstance.get(`/api/forums/posts/${postId}`);
      return response.data; // 서버 응답 데이터 반환
    } catch (error) {
      console.error("게시글 데이터 가져오기 중 오류 발생:", error);
      throw error; // 오류 전달
    }
  },

  /**
   * 댓글 추가
   * @param {Object} data - 댓글 데이터 (postId, memberId, content 등)
   * @param {string} token - 사용자 액세스 토큰
   * @returns {Promise<Object>} 서버 응답 데이터
   */
  addComment: async (data, token) => {
    try {
      const response = await AxiosInstance.post("/api/forums/comments", data, {
        headers: { Authorization: `Bearer ${token}` }, // 인증 헤더 추가
      }); // 댓글 추가 API 호출
      return response.data; // 응답 데이터 반환
    } catch (error) {
      console.error("댓글 추가 중 오류 발생:", error); // 에러 로그 출력
      throw error; // 에러 전달
    }
  },

  /**
   * 특정 댓글에 대한 답글 (인용) 추가
   * @param {number} commentId - 부모 댓글 ID
   * @param {Object} data - 답글 데이터
   * @param {string} token - 사용자 액세스 토큰
   * @returns {Promise<Object>} 서버 응답 데이터
   */
  replyToComment: async (commentId, data, token) => {
    try {
      const response = await AxiosInstance.post(
        `/api/forums/comments/${commentId}/reply`,
        data,
        {
          headers: { Authorization: `Bearer ${token}` },
        }
      );
      return response.data;
    } catch (error) {
      console.error("답글 추가 중 오류 발생:", error);
      throw error;
    }
  },

  /**
   * 게시글(OP)에 대한 답글 (인용) 추가
   * @param {number} postId - 게시글 ID
   * @param {Object} data - 답글 데이터
   * @param {string} token - 사용자 액세스 토큰
   * @returns {Promise<Object>} 서버 응답 데이터
   */
  replyToPost: async (postId, data, token) => {
    try {
      const response = await AxiosInstance.post(
        `/api/forums/comments/post/${postId}/reply`,
        data,
        {
          headers: { Authorization: `Bearer ${token}` },
        }
      );
      return response.data;
    } catch (error) {
      console.error("게시글 답글 추가 중 오류 발생:", error);
      throw error;
    }
  },

  /**
   * 댓글 수정
   * @param {number} commentId - 수정할 댓글 ID
   * @param {Object} data - 수정 요청 데이터 (새로운 댓글 내용 포함)
   * @param {number} loggedInMemberId - 로그인된 사용자 ID
   * @param {boolean} isAdmin - 관리자 여부
   * @returns {Promise<Object>} 수정된 댓글 데이터 반환
   */
  editComment: async (commentId, data, loggedInMemberId, isAdmin) => {
    try {
      console.log("Edit Comment API Call Params:", {
        commentId,
        data,
        loggedInMemberId,
        isAdmin,
      });
      if (!loggedInMemberId) {
        throw new Error("Logged-in Member ID is required.");
      }
      const response = await AxiosInstance.put(
        `/api/forums/comments/${commentId}?loggedInMemberId=${loggedInMemberId}&isAdmin=${isAdmin}`,
        { content: data.newContent } // Align key with backend expectation
      );
      console.log("Payload sent to editComment API:", {
        content: data.newContent,
        loggedInMemberId,
        isAdmin,
      });
      return response.data; // 서버 응답 반환
    } catch (error) {
      console.error("댓글 수정 중 오류:", error); // 에러 로그 출력
      throw error;
    }
  },

  /**
   * 댓글 삭제
   * @param {number} commentId - 삭제할 댓글 ID
   * @param {number} loggedInMemberId - 로그인된 사용자 ID
   * @returns {Promise<Object>} 삭제 결과 반환
   */
  deleteComment: async (commentId, loggedInMemberId, isAdmin) => {
    if (!loggedInMemberId) {
      console.error("loggedInMemberId is null or undefined");
      throw new Error("Logged-in Member ID is required");
    }
    try {
      const response = await AxiosInstance.delete(
        `/api/forums/comments/${commentId}?loggedInMemberId=${loggedInMemberId}&isAdmin=${isAdmin}`
      );
      return response.data;
    } catch (error) {
      console.error("댓글 삭제 중 오류:", error);
      throw error;
    }
  },

  /**
   * 게시글 좋아요 토글
   * @param {number} postId - 게시글 ID
   * @param {number} loggedInMemberId - 로그인된 사용자 ID
   * @returns {Promise<Object>} 좋아요 상태 반환
   */
  toggleLikePost: async (postId, loggedInMemberId) => {
    try {
      const response = await AxiosInstance.post(
        `/api/forums/posts/${postId}/like`,
        null, // 요청 본문 필요 없음
        {
          params: { loggedInMemberId }, // 사용자 ID를 쿼리 매개변수로 추가
        }
      );
      return response.data; // 서버 응답 반환
    } catch (error) {
      console.error("게시글 좋아요 토글 중 오류 발생:", error); // 에러 로그
      throw error; // 오류 전파
    }
  },

  /**
   * 댓글 좋아요 토글
   * @param {number} commentId - 댓글 ID
   * @param {number} loggedInMemberId - 로그인된 사용자 ID
   * @returns {Promise<Object>} 좋아요 상태 반환
   */
  toggleLikeComment: async (commentId, loggedInMemberId) => {
    try {
      const response = await AxiosInstance.post(
        `/api/forums/comments/${commentId}/like`,
        null, // 요청 본문 필요 없음
        {
          params: { loggedInMemberId }, // 사용자 ID를 쿼리 매개변수로 추가
        }
      );
      return response.data; // 서버 응답 반환
    } catch (error) {
      console.error("댓글 좋아요 토글 중 오류 발생:", error); // 에러 로그
      throw error; // 오류 전파
    }
  },

  /**
   * 게시글 신고
   * @param {number} postId - 신고 대상 게시글 ID
   * @param {number} reporterId - 신고자 ID
   * @param {string} reason - 신고 사유
   * @returns {Promise<Object>} 신고 처리 결과 반환
   */
  reportPost: async (postId, reporterId, reason) => {
    console.log("Post ID and Reporter ID for Reporting Post:", {
      postId,
      reporterId,
      reason,
    }); // Debugging log

    try {
      const response = await AxiosInstance.post(
        `/api/forums/posts/${postId}/report`,
        { reason }, // Wrap reason in an object
        { params: { reporterId } } // Reporter ID as query param
      );
      console.log("Post reported successfully:", response.data);
      return response.data;
    } catch (error) {
      console.error("게시글 신고 중 오류 발생:", error);
      throw error;
    }
  },

  /**
   * 댓글 신고
   * @param {number} commentId - 신고 대상 댓글 ID
   * @param {number} reporterId - 신고자 ID
   * @param {string} reason - 신고 사유
   * @returns {Promise<Object>} 신고 처리 결과 반환
   */
  reportComment: async (commentId, reporterId, reason) => {
    try {
      console.log("Reporting comment:", { commentId, reporterId, reason });
      const response = await AxiosInstance.post(
        `/api/forums/comments/${commentId}/report`,
        { reason }, // Wrap reason in an object
        { params: { reporterId } } // Reporter ID as query param
      );
      console.log("Comment reported successfully:", response.data);
      return response.data;
    } catch (error) {
      console.error("댓글 신고 중 오류 발생:", error);
      throw error;
    }
  },

  /**
   * 신고된 게시글 또는 댓글 상태 확인 (관리자 전용)
   * @param {number} contentId - 게시글 또는 댓글 ID
   * @param {string} type - "posts" 또는 "comments"
   * @returns {Promise<Object>} 신고 상태 데이터 반환
   */
  getReportStatus: async (contentId, type) => {
    try {
      const response = await AxiosInstance.get(
        `/api/forums/${type}/${contentId}/report-status`
      );
      console.log("Report status fetched:", response.data);
      return response.data;
    } catch (error) {
      console.error("신고 상태 확인 중 오류 발생:", error);
      throw error;
    }
  },

  /**
   * 포럼 게시글 복원 API 호출
   * @param {number} postId - 복원할 게시글 ID
   * @param {number} adminId - 관리자 ID
   * @returns {Promise<Object>} 복원된 게시글 데이터 반환
   */
  restorePost: async (postId, adminId) => {
    try {
      const response = await AxiosInstance.post(
        `/api/forums/posts/${postId}/restore`,
        { adminId } // 관리자 ID를 요청 본문에 포함
      );
      console.log("API response for restored post:", response.data);
      return response.data; // 복원된 게시글 데이터 반환
    } catch (error) {
      console.error("Error restoring post:", error.response?.data || error); // 에러 로그 출력
      throw error; // 에러를 호출자에게 전달
    }
  },

  /**
   * 포럼 댓글 복원 API 호출
   * @param {number} commentId - 복원할 댓글 ID
   * @param {number} adminId - 관리자 ID
   * @returns {Promise<Object>} 복원된 댓글 데이터 반환
   */
  restoreComment: async (commentId, adminId) => {
    try {
      const response = await AxiosInstance.post(
        `/api/forums/comments/${commentId}/restore`,
        { adminId } // 관리자 ID를 요청 본문에 포함
      );
      console.log("API response for restored comment:", response.data);
      return response.data; // 복원된 댓글 데이터 반환
    } catch (error) {
      console.error("Error restoring comment:", error.response?.data || error); // 에러 로그 출력
      throw error; // 에러를 호출자에게 전달
    }
  },
};

export default ForumApi; // ForumApi 내보내기
