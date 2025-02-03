import React, { useEffect, useState } from "react";
import { useParams, useNavigate } from "react-router-dom"; // URL에서 postId를 추출
import { getUserInfo } from "../../axios/AxiosInstanse"; // 사용자 정보 가져오기
import ForumApi from "../../api/ForumApi"; // API 호출
import {
  PostDetailContainer,
  PostHeader,
  PostTitle,
  ContentInfo,
  AuthorInfo,
  ActionButtons,
  CommentSection,
  CommentCard,
  CommentContent,
  CommentInputSection,
  HiddenCommentNotice,
  EditButton,
  AdminEditIndicator,
  DisabledEditButton,
  ReportCountText,
} from "../../styles/PostDetailStyles"; // 스타일 컴포넌트
import { FontAwesomeIcon } from "@fortawesome/react-fontawesome";
import {
  faEdit,
  faThumbsUp,
  faReply,
  faDeleteLeft,
  faCircleExclamation,
} from "@fortawesome/free-solid-svg-icons";
import ReactTooltip from "react-tooltip";

const PostDetail = () => {
  const { postId } = useParams(); // URL에서 postId 추출
  const navigate = useNavigate(); // 페이지 이동을 위한 네비게이트 훅
  const [post, setPost] = useState(null); // 게시글 데이터 상태
  const [comments, setComments] = useState([]); // 댓글 데이터 상태
  const [prevPost, setPrevPost] = useState(null); // 이전 게시글 상태를 저장
  const [prevComments, setPrevComments] = useState([]); // 이전 댓글 상태를 저장
  const [newComment, setNewComment] = useState(""); // 새 댓글 입력 상태
  const [loading, setLoading] = useState(true); // 로딩 상태
  const [memberId, setMemberId] = useState(null); // 로그인 사용자 ID
  const [isAdmin, setIsAdmin] = useState(false); // 관리자 여부 상태

  /**
   * 사용자 정보와 권한을 가져오는 함수
   */
  const fetchMemberData = async () => {
    try {
      const userInfo = await getUserInfo(); // AxiosInstanse에서 사용자 정보 가져오기
      if (userInfo && userInfo.memberId) {
        setMemberId(userInfo.memberId); // 사용자 ID 설정
        setIsAdmin(userInfo.role === "ADMIN"); // 관리자 여부 설정
      } else {
        alert("로그인이 필요합니다."); // 비로그인 사용자 처리
        navigate("/login");
      }
    } catch (error) {
      console.error("사용자 정보를 가져오는 중 오류 발생:", error);
      alert("사용자 정보를 확인할 수 없습니다.");
      navigate("/login");
    }
  };

  useEffect(() => {
    /**
     * 게시글 및 댓글 데이터를 가져오는 함수
     * - 서버에서 게시글 및 댓글 데이터를 가져옵니다.
     * - 가져온 게시글 데이터에 '관리자 수정 여부' 플래그를 추가로 계산합니다.
     */
    const fetchPostData = async () => {
      try {
        await fetchMemberData(); // 사용자 정보 가져오기

        const postData = await ForumApi.getPostById(postId); // 게시글 데이터 가져오기
        const commentData = await ForumApi.getCommentsByPostId(postId); // 댓글 데이터 가져오기

        // 관리자 수정 여부 플래그 계산 (백엔드 필드가 없는 경우 기본값 false 설정)
        const processedPost = {
          ...postData,
          editedByAdminTitle: postData.editedByTitle === "ADMIN",
          editedByAdminContent: postData.editedByContent === "ADMIN",
        };

        console.log("Processed Post (With Admin Flags):", processedPost);
        setPost(processedPost); // 게시글 상태 업데이트
        setComments(commentData); // 댓글 상태 업데이트

        console.log("Processed Post Data:", processedPost); // 디버깅용 로그
      } catch (error) {
        console.error("게시글 데이터를 가져오는 중 오류 발생:", error);
        alert("게시글 데이터를 불러오는 중 오류가 발생했습니다."); // 오류 알림
      } finally {
        setLoading(false); // 로딩 상태 해제
      }
    };

    fetchPostData(); // 함수 호출
  }, [postId]); // postId가 변경될 때마다 실행

  /**
   * 새 댓글 추가 처리
   */
  const handleAddComment = async () => {
    if (!newComment.trim()) {
      return alert("댓글을 입력해 주세요.");
    }
    try {
      const response = await ForumApi.addComment({
        postId: post.id,
        memberId,
        content: newComment,
      });
      setComments((prev) => [...prev, response]);
      setNewComment(""); // 입력 초기화
      alert("댓글이 성공적으로 추가되었습니다.");
    } catch (error) {
      console.error("댓글 추가 중 오류 발생:", error);
      alert("댓글 추가에 실패했습니다.");
    }
  };

  /**
   * 게시글 제목 수정 처리
   */
  const handleEditTitle = async () => {
    const updatedTitle = prompt("Enter new post title:", post.title);
    if (!updatedTitle) return;

    try {
      const updatedPost = await ForumApi.updatePostTitle(
        post.id,
        { title: updatedTitle },
        memberId,
        isAdmin
      );

      setPost((prevPost) => ({
        ...prevPost,
        title: updatedPost.title,
        editedByAdminTitle: updatedPost.editedByTitle === "ADMIN",
      }));

      alert("Post title updated successfully.");
    } catch (error) {
      console.error("게시글 제목 수정 중 오류 발생:", error);
      alert("게시글 제목 수정에 실패했습니다.");
    }
  };

  /**
   * 게시글 내용 수정 처리
   * - 게시글 내용을 업데이트하고 상태를 동기화합니다.
   * - 관리자는 수정 후 `editedByAdmin` 플래그를 활성화하고 잠금 상태를 유지합니다.
   * - 작성자는 수정할 수 있지만 관리자에 의해 수정된 경우 수정이 비활성화됩니다.
   */
  const handleEditPost = async () => {
    const updatedContent = prompt("Enter new post content:", post.content);
    if (!updatedContent) return;

    try {
      const updatedPost = await ForumApi.updatePostContent(
        post.id,
        { content: updatedContent },
        memberId,
        isAdmin
      );

      setPost((prevPost) => ({
        ...prevPost,
        content: updatedPost.content,
        editedByAdminContent: updatedPost.editedByContent === "ADMIN",
      }));

      alert("Post content updated successfully.");
    } catch (error) {
      console.error("게시글 내용 수정 중 오류 발생:", error);
      alert("게시글 내용 수정에 실패했습니다.");
    }
  };

  /**
   * 댓글 수정 처리
   */
  const handleEditComment = async (commentId) => {
    const updatedContent = prompt("새 댓글 내용을 입력하세요:");
    if (!updatedContent) return;

    try {
      const updatedComment = await ForumApi.editComment(
        commentId,
        {
          newContent: updatedContent,
        },
        memberId
      );
      setComments((prevComments) =>
        prevComments.map((comment) =>
          comment.id === commentId ? updatedComment : comment
        )
      );
      alert("댓글이 성공적으로 수정되었습니다.");
    } catch (error) {
      console.error("댓글 수정 중 오류 발생:", error);
      alert("댓글 수정에 실패했습니다.");
    }
  };

  /**
   * 게시글 삭제 처리
   */
  const handleDeletePost = async () => {
    if (window.confirm("게시글을 삭제하시겠습니까?")) {
      try {
        const removedBy =
          memberId === post.memberId ? post.authorName : "ADMIN";
        await ForumApi.deletePost(post.id, memberId, removedBy);
        alert("게시글이 삭제되었습니다.");
        navigate("/forum");
      } catch (error) {
        console.error("게시글 삭제 중 오류 발생:", error);
        alert("게시글 삭제에 실패했습니다.");
      }
    }
  };

  /**
   * 댓글 삭제 처리 함수
   * - 사용자에게 삭제 여부를 확인한 후 백엔드 API를 호출하여 댓글 삭제 요청을 보냄.
   * - 삭제된 댓글은 `[Removed]` 상태로 업데이트되어 UI에 반영.
   * - 관리자와 댓글 작성자만 댓글을 삭제할 수 있음.
   * - 비로그인 상태에서는 로그인 페이지로 리다이렉트.
   */
  const handleDeleteComment = async (commentId) => {
    if (window.confirm("댓글을 삭제하시겠습니까?")) {
      try {
        // 로그인된 사용자 정보가 없는 경우 가져오기
        if (!memberId) {
          await fetchMemberData(); // 사용자 정보 갱신
        }
        if (!memberId) return; // 로그인 실패 시 실행 중단

        // 댓글 삭제 API 호출
        await ForumApi.deleteComment(commentId, memberId);

        // UI 업데이트: 삭제된 댓글을 `[Removed]` 상태로 표시
        setComments((prevComments) =>
          prevComments.map((comment) =>
            comment.id === commentId
              ? { ...comment, content: "[Removed]", hidden: true }
              : comment
          )
        );

        alert("댓글이 삭제되었습니다."); // 성공 메시지 출력
      } catch (error) {
        console.error("댓글 삭제 중 오류 발생:", error); // 오류 로그 출력
        alert("댓글 삭제에 실패했습니다."); // 실패 메시지 출력
      }
    }
  };

  /**
   * 게시글 좋아요 토글 처리 함수
   * - 사용자가 게시글의 좋아요 상태를 토글할 수 있도록 처리.
   * - 백엔드 API 호출을 통해 좋아요 상태를 업데이트한 후 UI에 반영.
   * - 사용자가 로그인하지 않은 경우 로그인 페이지로 리다이렉트.
   */
  const handleLike = async () => {
    try {
      // 로그인된 사용자 ID 및 역할 정보 가져오기
      if (!memberId) {
        await fetchMemberData(); // 사용자 정보 갱신
      }
      if (!memberId) return; // 로그인 실패 시 실행 중단

      const updatedPost = await ForumApi.toggleLikePost(post.id, memberId); // 게시글 좋아요 상태 토글 API 호출

      // 상태 업데이트: likesCount와 liked 상태를 업데이트
      setPost((prevPost) => ({
        ...prevPost,
        likesCount: updatedPost.totalLikes, // 서버에서 반환된 좋아요 수
        liked: updatedPost.liked, // 좋아요 상태
      }));

      console.log("Updated post after like toggle:", updatedPost); // 디버깅용 로그
    } catch (error) {
      console.error("게시글 좋아요 처리 중 오류:", error); // 에러 로그 출력
      alert("좋아요 처리에 실패했습니다."); // 사용자 알림
    }
  };

  /**
   * 댓글 좋아요 토글 처리 함수
   * - 사용자가 댓글의 좋아요 상태를 토글할 수 있도록 처리.
   * - 백엔드 API 호출을 통해 좋아요 상태를 업데이트한 후 UI에 반영.
   * - 사용자가 로그인하지 않은 경우 로그인 페이지로 리다이렉트.
   */
  const handleLikeComment = async (commentId) => {
    try {
      // 로그인된 사용자 ID 및 역할 정보 가져오기
      if (!memberId) {
        await fetchMemberData(); // 사용자 정보 갱신
      }
      if (!memberId) return; // 로그인 실패 시 실행 중단

      const updatedComment = await ForumApi.toggleLikeComment(
        commentId,
        memberId
      ); // 댓글 좋아요 상태 토글 API 호출

      // 상태 업데이트: 특정 댓글의 likesCount와 liked 상태를 업데이트
      setComments((prevComments) =>
        prevComments.map((comment) =>
          comment.id === commentId
            ? {
                ...comment,
                likesCount: updatedComment.totalLikes, // 서버에서 반환된 좋아요 수
                liked: updatedComment.liked, // 좋아요 상태
              }
            : comment
        )
      );

      console.log("Updated comment after like toggle:", updatedComment); // 디버깅용 로그
    } catch (error) {
      console.error("댓글 좋아요 처리 중 오류:", error); // 에러 로그 출력
      alert("댓글 좋아요 처리에 실패했습니다."); // 사용자 알림
    }
  };

  const handleReportPost = async (postId, reporterId) => {
    console.log("Post ID and Reporter ID:", { postId, reporterId }); // Debugging log

    const reason = prompt("게시글 신고 사유를 입력하세요:");
    if (!reason) return;

    try {
      await ForumApi.reportPost(postId, reporterId, reason);
      alert("게시글이 성공적으로 신고되었습니다.");
    } catch (error) {
      console.error("Error reporting post:", error);
      alert(
        error.response?.data?.message ||
          "신고 중 오류가 발생했습니다. 다시 시도해주세요."
      );
    }
  };

  const handleReportComment = async (commentId, reporterId) => {
    console.log("Comment ID and Reporter ID:", { commentId, reporterId }); // Debugging log
    const reason = prompt("댓글 신고 사유를 입력하세요:");
    if (!reason) return;

    try {
      await ForumApi.reportComment(commentId, reporterId, reason);
      alert("댓글이 성공적으로 신고되었습니다.");
    } catch (error) {
      console.error("Error reporting comment:", error);
      alert(
        error.response?.data?.message ||
          "신고 중 오류가 발생했습니다. 다시 시도해주세요."
      );
    }
  };

  useEffect(() => {
    if (post) {
      setPrevPost(post);
    }
  }, [post]);

  /**
   * 댓글 데이터(comments)가 업데이트되면 상태를 동기화합니다.
   * - 기존 댓글 데이터(prevComments)를 업데이트합니다.
   * - 각 댓글의 '관리자 수정 여부' 플래그를 재계산하여 상태를 업데이트합니다.
   */
  useEffect(() => {
    if (
      comments.length > 0 && // 댓글 데이터가 존재하고
      JSON.stringify(prevComments) !== JSON.stringify(comments) // 새로운 댓글 데이터와 기존 데이터가 다른 경우
    ) {
      setPrevComments(comments); // 이전 댓글 상태를 업데이트

      // 각 댓글의 상태를 플래그와 함께 업데이트
      setComments(
        comments.map((comment) => ({
          ...comment,
          editedByAdmin: comment.editedBy === "ADMIN", // 관리자에 의한 수정 여부 플래그 설정
        }))
      );
    }
  }, [comments, prevComments]); // comments 또는 prevComments 변경 시 실행

  if (loading) return <div>로딩 중...</div>;
  if (!post) return <div>게시글을 찾을 수 없습니다.</div>;

  return (
    <PostDetailContainer>
      {/* 게시글 제목 섹션 */}
      <PostTitle>
        {/* 게시글이 숨김 상태일 경우 알림 표시 */}
        {post.hidden ? (
          <HiddenCommentNotice>
            NOTICE: 해당 게시글은 삭제되거나 숨김 처리되었습니다.
          </HiddenCommentNotice>
        ) : (
          <>
            {/* 게시글 제목 및 관리자 수정 표시 */}
            <span>
              {post.title}
              {post.editedByAdminTitle && (
                <AdminEditIndicator>
                  [관리자에 의해 제목 수정됨]
                </AdminEditIndicator>
              )}
            </span>

            {/* 작성자가 수정 가능한 경우 */}
            {!post.editedByAdminTitle &&
              memberId === post.memberId &&
              !isAdmin && (
                <EditButton onClick={handleEditTitle} aria-label="Edit Title">
                  <FontAwesomeIcon icon={faEdit} />
                </EditButton>
              )}

            {/* 관리자가 수정 가능한 경우 (관리자가 OP인 경우 중복 방지) */}
            {isAdmin &&
              (!post.editedByAdminTitle || memberId !== post.memberId) && (
                <EditButton onClick={handleEditTitle} aria-label="Edit Title">
                  <FontAwesomeIcon icon={faEdit} />
                </EditButton>
              )}

            {/* 관리자가 수정한 경우 작성자 버튼 비활성화 */}
            {post.editedByAdminTitle &&
              memberId === post.memberId &&
              !isAdmin && (
                <DisabledEditButton aria-label="Edit Disabled by Admin">
                  <FontAwesomeIcon icon={faEdit} />
                </DisabledEditButton>
              )}
          </>
        )}
      </PostTitle>

      {/* 게시글 헤더 섹션 */}
      <PostHeader>
        {/* 작성자 및 생성일 정보 */}
        <AuthorInfo>
          <p>
            <strong>게시자:</strong> {post.authorName}
          </p>
          <p>
            <strong>생성일:</strong> {new Date(post.createdAt).toLocaleString()}
          </p>
        </AuthorInfo>

        {/* 게시글 내용 섹션 */}
        <ContentInfo>
          {/* 게시글이 숨김 처리된 경우 */}
          {post.hidden ? (
            <HiddenCommentNotice>
              NOTICE: 해당 게시글은 삭제되거나 숨김 처리되었습니다.
            </HiddenCommentNotice>
          ) : (
            <p>
              {post.content}
              {post.editedByAdminContent && (
                <AdminEditIndicator>
                  [관리자에 의해 내용 수정됨]
                </AdminEditIndicator>
              )}
            </p>
          )}

          {/* 액션 버튼 섹션 */}
          <ActionButtons>
            <div className="left">
              {/* 게시글 신고 버튼 */}
              <report-button
                onClick={() => handleReportPost(post.id, memberId)}
              >
                <FontAwesomeIcon icon={faCircleExclamation} />
                {isAdmin && post.reportCount !== undefined && (
                  <ReportCountText>
                    신고 누적 수: {post.reportCount}
                  </ReportCountText>
                )}
              </report-button>

              {/* 작성자 전용 버튼 (관리자 수정 시 비활성화) */}
              {memberId === post.memberId && !isAdmin && (
                <>
                  {!post.editedByAdminContent ? (
                    <>
                      <report-button onClick={handleDeletePost}>
                        <FontAwesomeIcon icon={faDeleteLeft}></FontAwesomeIcon>
                      </report-button>
                      <report-button onClick={handleEditPost}>
                        <FontAwesomeIcon icon={faEdit}></FontAwesomeIcon>
                      </report-button>
                    </>
                  ) : (
                    <>
                      <disabled-button>
                        <FontAwesomeIcon icon={faDeleteLeft}></FontAwesomeIcon>
                      </disabled-button>
                      <disabled-button>
                        <FontAwesomeIcon icon={faEdit}></FontAwesomeIcon>
                      </disabled-button>
                    </>
                  )}
                </>
              )}

              {/* 관리자 전용 버튼 */}
              {isAdmin && (
                <>
                  <admin-button onClick={handleDeletePost}>
                    <FontAwesomeIcon icon={faDeleteLeft}></FontAwesomeIcon>
                  </admin-button>
                  <admin-button onClick={handleEditPost}>
                    <FontAwesomeIcon icon={faEdit}></FontAwesomeIcon>
                  </admin-button>
                </>
              )}
            </div>

            <div className="right">
              {/* 좋아요 버튼 */}
              <button onClick={handleLike}>
                <FontAwesomeIcon icon={faThumbsUp}></FontAwesomeIcon>{" "}
                {post.likesCount} {/* 좋아요 수 */}
              </button>
              {/* 답글 버튼 */}
              <button>
                <FontAwesomeIcon icon={faReply}></FontAwesomeIcon>
              </button>
            </div>
          </ActionButtons>
        </ContentInfo>
      </PostHeader>

      {/* 댓글 섹션 */}
      <CommentSection>
        <h2>댓글</h2>
        {comments.map((comment) => (
          <CommentCard key={comment.id}>
            {/* 댓글 작성자 및 작성일 정보 */}
            <AuthorInfo>
              <p>{comment.authorName}</p>
              <p>{new Date(comment.createdAt).toLocaleString()}</p>
            </AuthorInfo>

            {/* 댓글 내용 섹션 */}
            <CommentContent>
              {comment.hidden ? (
                <HiddenCommentNotice>
                  NOTICE: 해당 댓글은 삭제되거나 숨김 처리되었습니다.
                </HiddenCommentNotice>
              ) : (
                <p>
                  {comment.content}
                  {comment.editedByAdmin && (
                    <AdminEditIndicator>[관리자 수정]</AdminEditIndicator>
                  )}
                </p>
              )}

              {/* 댓글 액션 버튼 */}
              <ActionButtons>
                <div className="left">
                  {/* 댓글 신고 버튼 */}
                  <report-button
                    onClick={() => handleReportComment(comment.id, memberId)}
                  >
                    <FontAwesomeIcon icon={faCircleExclamation} />
                    {isAdmin && comment.reportCount !== undefined && (
                      <ReportCountText>
                        신고 누적 수: {comment.reportCount}
                      </ReportCountText>
                    )}
                  </report-button>

                  {/* 댓글 작성자 전용 삭제/수정 버튼 */}
                  {memberId === comment.memberId && !isAdmin && (
                    <>
                      {!comment.editedByAdmin ? (
                        <>
                          <report-button
                            onClick={() => handleDeleteComment(comment.id)}
                          >
                            <FontAwesomeIcon
                              icon={faDeleteLeft}
                            ></FontAwesomeIcon>
                          </report-button>
                          <report-button
                            onClick={() => handleEditComment(comment.id)}
                          >
                            <FontAwesomeIcon icon={faEdit}></FontAwesomeIcon>
                          </report-button>
                        </>
                      ) : (
                        <>
                          <disabled-button>
                            <FontAwesomeIcon
                              icon={faDeleteLeft}
                            ></FontAwesomeIcon>
                          </disabled-button>
                          <disabled-button>
                            <FontAwesomeIcon icon={faEdit}></FontAwesomeIcon>
                          </disabled-button>
                        </>
                      )}
                    </>
                  )}

                  {/* 관리자 전용 삭제/수정 버튼 */}
                  {isAdmin && memberId !== comment.memberId && (
                    <>
                      <admin-button
                        onClick={() => handleDeleteComment(comment.id)}
                      >
                        <FontAwesomeIcon icon={faDeleteLeft}></FontAwesomeIcon>
                      </admin-button>
                      <admin-button
                        onClick={() => handleEditComment(comment.id)}
                      >
                        <FontAwesomeIcon icon={faEdit}></FontAwesomeIcon>
                      </admin-button>
                    </>
                  )}

                  {/* 관리자와 작성자가 동일한 경우 관리자 버튼만 표시 */}
                  {isAdmin && memberId === comment.memberId && (
                    <>
                      <admin-button
                        onClick={() => handleDeleteComment(comment.id)}
                      >
                        <FontAwesomeIcon icon={faDeleteLeft}></FontAwesomeIcon>
                      </admin-button>
                      <admin-button
                        onClick={() => handleEditComment(comment.id)}
                      >
                        <FontAwesomeIcon icon={faEdit}></FontAwesomeIcon>
                      </admin-button>
                    </>
                  )}
                </div>

                <div className="right">
                  {/* 댓글 좋아요 버튼 */}
                  <button onClick={() => handleLikeComment(comment.id)}>
                    <FontAwesomeIcon icon={faThumbsUp}></FontAwesomeIcon>{" "}
                    {comment.likesCount} {/* 좋아요 수 */}
                  </button>
                  {/* 답글 버튼 */}
                  <button>
                    <FontAwesomeIcon icon={faReply}></FontAwesomeIcon>
                  </button>
                </div>
              </ActionButtons>
            </CommentContent>
          </CommentCard>
        ))}
      </CommentSection>

      {/* 댓글 입력 섹션 */}
      <CommentInputSection>
        <textarea
          placeholder="댓글을 작성하세요..."
          value={newComment}
          onChange={(e) => setNewComment(e.target.value)}
        />
        <button onClick={handleAddComment}>Add Comment</button>
      </CommentInputSection>
    </PostDetailContainer>
  );
};

export default PostDetail;
