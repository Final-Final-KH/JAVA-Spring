import AxiosInstance from "../axios/AxiosInstanse"; // Axios 인스턴스 불러오기

/**
 * 📌 피드 관련 API 관리
 * - 피드 가져오기, 생성, 좋아요, 댓글, 리포스트 등의 기능 포함
 */
const FeedApi = {
  /**
   * 피드 목록 가져오기 (페이징 처리)
   *
   * @param {number} page - 현재 페이지 번호 (0부터 시작)
   * @param {number} size - 가져올 게시글 수 (기본값: 10)
   * @param {number} currentMemberId - 현재 사용자의 회원 ID (좋아요 상태 판별용)
   * @returns {Promise<Array>} - 백엔드 Page 객체의 content 배열을 반환
   */
  fetchFeeds: async (page, size = 10, currentMemberId) => {
    try {
      const response = await AxiosInstance.get(`/api/feeds`, {
        params: { page, size, currentMemberId },
      });
      return response.data.content;
    } catch (error) {
      console.error("❌ 피드 가져오기 실패:", error);
      return [];
    }
  },

  /**
   * 특정 피드 조회
   *
   * @param {number} feedId - 조회할 피드 ID
   * @param {number} currentMemberId - 현재 사용자의 회원 ID (좋아요 상태 판별용)
   * @returns {Promise<Object>} - 조회된 피드 데이터를 반환
   */
  getFeedById: async (feedId, currentMemberId) => {
    try {
      const response = await AxiosInstance.get(`/api/feeds/${feedId}`, {
        params: { currentMemberId },
      });
      return response.data;
    } catch (error) {
      console.error("❌ 피드 조회 실패:", error);
      throw error;
    }
  },

  /**
   * 새로운 피드 작성
   *
   * @param {Object} data - 피드 데이터 (내용, 이미지 등)
   * @returns {Promise<Object>} - 생성된 피드 데이터를 반환
   */
  createFeed: async (data) => {
    try {
      const response = await AxiosInstance.post("/api/feeds", data);
      return response.data;
    } catch (error) {
      console.error("❌ 피드 작성 실패:", error);
      throw error;
    }
  },

  /**
   * 특정 피드 수정 API
   *
   * @param {number} feedId - 수정할 피드 ID
   * @param {object} data - 수정할 피드 데이터 (예: { content, memberId, mediaUrl })
   * @returns {Promise<object>} - 수정된 피드 데이터를 반환
   */
  editFeed: async (feedId, data) => {
    try {
      const response = await AxiosInstance.put(`/api/feeds/${feedId}`, data);
      return response.data;
    } catch (error) {
      console.error("❌ 피드 수정 실패:", error);
      throw error;
    }
  },

  /**
   * 특정 게시글 좋아요 추가
   *
   * @param {number} feedId - 좋아요를 누를 게시글 ID
   * @param {number} memberId - 좋아요를 누른 사용자 ID
   */
  likeFeed: async (feedId, memberId) => {
    try {
      await AxiosInstance.post(`/api/feeds/${feedId}/like`, null, {
        params: { memberId },
      });
    } catch (error) {
      console.error("❌ 게시글 좋아요 실패:", error);
    }
  },

  /**
   * 특정 게시글 좋아요 취소
   *
   * @param {number} feedId - 좋아요를 취소할 게시글 ID
   * @param {number} memberId - 좋아요를 취소한 사용자 ID
   */
  unlikeFeed: async (feedId, memberId) => {
    try {
      await AxiosInstance.delete(`/api/feeds/${feedId}/like`, {
        params: { memberId },
      });
    } catch (error) {
      console.error("❌ 게시글 좋아요 취소 실패:", error);
    }
  },

  /**
   * 특정 게시글에 댓글 추가
   *
   * @param {number} feedId - 댓글을 추가할 게시글 ID
   * @param {Object} commentData - 댓글 데이터 (예: { comment: "댓글 내용", memberId: ..., parentCommentId: ... })
   * @returns {Promise<Object>} - 추가된 댓글 데이터를 반환
   */
  addComment: async (feedId, commentData) => {
    try {
      const response = await AxiosInstance.post(
        `/api/feeds/${feedId}/comments`,
        commentData,
        { params: { parentCommentId: commentData.parentCommentId || null } }
      );
      return response.data;
    } catch (error) {
      console.error("❌ 댓글 추가 실패:", error.response?.data || error);
      throw error;
    }
  },

  /**
   * 특정 댓글 수정 API
   *
   * @param {number} commentId - 수정할 댓글 ID
   * @param {object} data - 수정할 댓글 데이터 (예: { comment, memberId })
   * @returns {Promise<object>} - 수정된 댓글 데이터를 반환
   */
  editComment: async (commentId, data) => {
    try {
      const response = await AxiosInstance.put(
        `/api/feeds/comments/${commentId}`,
        data
      );
      return response.data;
    } catch (error) {
      console.error("❌ 댓글 수정 실패:", error);
      throw error;
    }
  },

  /**
   * 특정 게시글 리포스트
   *
   * @param {number} feedId - 리포스트할 게시글 ID
   * @param {number} reposterId - 리포스트하는 사용자 ID
   * @param {Object} repostData - 리포스트 데이터
   */
  repostFeed: async (feedId, reposterId, repostData) => {
    try {
      await AxiosInstance.post(`/api/feeds/${feedId}/repost`, repostData, {
        params: { reposterId },
      });
    } catch (error) {
      console.error("❌ 게시글 리포스트 실패:", error);
    }
  },

  /**
   * 특정 게시글 저장
   *
   * @param {number} feedId - 저장할 게시글 ID
   * @returns {Promise<Object>} - 저장된 피드의 응답 데이터를 반환
   */
  saveFeed: async (feedId) => {
    try {
      const response = await AxiosInstance.post(`/api/feeds/${feedId}/save`);
      return response.data;
    } catch (error) {
      console.error("❌ 게시글 저장 실패:", error);
      throw error;
    }
  },

  /**
   * 사용자가 저장한 게시글 목록 조회
   *
   * @param {number} memberId - 저장된 게시글 소유자 회원 ID
   * @param {number} currentMemberId - 현재 사용자의 회원 ID (좋아요 상태 판별용)
   * @returns {Promise<Array>} - 저장된 피드 배열을 반환
   */
  getSavedPosts: async (memberId, currentMemberId) => {
    try {
      const response = await AxiosInstance.get(
        `/api/feeds/members/${memberId}/saved-posts`,
        { params: { currentMemberId } }
      );
      return response.data;
    } catch (error) {
      console.error("❌ 저장된 게시글 가져오기 실패:", error);
      return [];
    }
  },

  /**
   * 댓글 좋아요 API
   *
   * @param {number} commentId - 좋아요할 댓글 ID
   * @param {number} memberId - 좋아요를 누른 사용자 ID
   */
  likeComment: async (commentId, memberId) => {
    try {
      await AxiosInstance.post(`/api/feeds/comments/${commentId}/like`, null, {
        params: { memberId },
      });
    } catch (error) {
      console.error("❌ 댓글 좋아요 실패:", error);
    }
  },

  /**
   * 댓글 좋아요 취소 API
   *
   * @param {number} commentId - 좋아요 취소할 댓글 ID
   * @param {number} memberId - 좋아요 취소한 사용자 ID
   */
  unlikeComment: async (commentId, memberId) => {
    try {
      await AxiosInstance.delete(`/api/feeds/comments/${commentId}/like`, {
        params: { memberId },
      });
    } catch (error) {
      console.error("❌ 댓글 좋아요 취소 실패:", error);
    }
  },

  /**
   * 피드 추천 가져오기 (친구 + 랜덤)
   *
   * @param {number} memberId - 추천 기준이 되는 현재 로그인된 사용자 ID
   * @param {number} currentMemberId - 현재 사용자의 회원 ID (좋아요 상태 판별용)
   * @returns {Promise<Array>} - 추천된 피드 목록을 반환
   */
  fetchSuggestedFeeds: async (memberId, currentMemberId) => {
    try {
      const response = await AxiosInstance.get(`/api/feeds/suggested`, {
        params: { memberId, currentMemberId },
      });
      return response.data;
    } catch (error) {
      console.error("❌ 추천 피드 가져오기 실패:", error);
      return [];
    }
  },

  /**
   * 친구 추천 API
   *
   * @param {number} memberId - 현재 로그인된 사용자 ID
   * @returns {Promise<Array>} - 추천된 사용자 목록을 반환
   */
  fetchSuggestedFriends: async (memberId) => {
    try {
      const response = await AxiosInstance.get(`/api/members/suggested`, {
        params: { memberId },
      });
      return response.data;
    } catch (error) {
      console.error("❌ 친구 추천 가져오기 실패:", error);
      throw error;
    }
  },
};

export default FeedApi;
