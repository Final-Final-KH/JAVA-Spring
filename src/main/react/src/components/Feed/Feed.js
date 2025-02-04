import React, { useState, useEffect, useRef } from "react";
import { useProfile } from "../../pages/ProfileContext";
import { ToastContainer, toast } from "react-toastify"; // Toast 메시지용
import "react-toastify/dist/ReactToastify.css"; // Toast CSS
import FeedApi from "../../api/FeedApi"; // 피드 API 모듈 추가
import { getUserInfo } from "../../axios/AxiosInstanse"; // 현재 사용자 정보 가져오기

// 반드시 이미지 임포트를 포함합니다.
import imgLogo1 from "../../images/RefreshButton.png";
import imgLogo2 from "../../images/DeveloperMark.jpg";
import imgLogo3 from "../../images/PictureButton.png";

// Import styled components
import {
  LayoutContainer,
  ProfileSection,
  ProfileImage,
  FeedContainer,
  CreateFeedContainer,
  TextareaContainer,
  UploadIconLabel,
  UploadIcon,
  PostList,
  Post,
  PostHeader,
  AuthorImage,
  AuthorDetails,
  AuthorName,
  PostDate,
  UploadedImage,
  RefreshButton,
  RefreshIcon,
  FriendsSection,
  FriendList,
  FriendItem,
  FriendInfo,
  FriendImage,
  FriendDetails,
  FriendRole,
  FriendActions,
  FriendRequestButton,
  MessageButton,
  PostActions,
  ActionButton,
  CommentContainer,
  CommentInput,
  CommentInputContainer,
  CommentSubmitIcon,
  CommentCard,
  RepostContainer,
  RepostInput,
  RepostSubmitButton,
  OriginalPostContainer,
  OriginalPostHeader,
  OriginalPostContent,
  ReplyContainer,
} from "../../styles/FeedStyles";

/**
 * 재귀적으로 comments 배열에서 commentId에 해당하는 댓글을 찾아 업데이트합니다.
 *
 * @param {Array} comments - 댓글 배열 (또는 reply 배열)
 * @param {number} commentId - 업데이트할 댓글 ID
 * @param {object} updatedData - 업데이트된 댓글 데이터 (예: { comment: "새 내용", ... })
 * @returns {Array} - 업데이트된 댓글 배열
 */
const updateCommentRecursively = (comments, commentId, updatedData) => {
  return comments.map((comment) => {
    if (comment.commentId === commentId) {
      return {
        ...comment,
        ...updatedData,
        // 만약 updatedData의 값이 falsy하면 기존 값을 유지
        memberName: updatedData.memberName || comment.memberName,
        currentCompany: updatedData.currentCompany || comment.currentCompany,
        profilePictureUrl:
          updatedData.profilePictureUrl || comment.profilePictureUrl,
        replies: comment.replies || [],
      };
    }
    if (comment.replies && comment.replies.length > 0) {
      return {
        ...comment,
        replies: updateCommentRecursively(
          comment.replies,
          commentId,
          updatedData
        ),
      };
    }
    return comment;
  });
};

/**
 * Feed 데이터 병합 헬퍼 함수
 *
 * API로부터 받은 새로운 피드 데이터(newFeed)와 기존 피드(oldFeed)를 병합합니다.
 *
 * @param {object} oldFeed - 기존 피드 객체
 * @param {object} newFeed - API로부터 받은 업데이트된 피드 객체
 * @returns {object} - 병합된 피드 객체
 */
const mergeFeedData = (oldFeed, newFeed) => {
  return {
    ...oldFeed,
    ...newFeed,
    profilePictureUrl: newFeed.profilePictureUrl || oldFeed.profilePictureUrl,
    originalPoster: newFeed.originalPoster || oldFeed.originalPoster,
  };
};

/**
 * [renderReplies]
 * 재귀적으로 하위 답글들을 렌더링하는 헬퍼 함수입니다.
 *
 * @param {Array} replies - 답글 객체들의 배열
 * @param {number} parentFeedId - 상위 게시글의 feedId (답글 추가 시 사용)
 * @param {number} memberId - 현재 로그인한 사용자 ID
 * @param {object} likedComments - 각 답글의 좋아요 상태 객체
 * @param {function} handleCommentLike - 댓글 좋아요 토글 함수
 * @param {function} toggleReplyInput - 답글 입력 필드 토글 함수
 * @param {object} showReplyInput - 답글 입력 필드 표시 상태 객체
 * @param {object} replyInputs - 답글 입력값 상태 객체
 * @param {function} setReplyInputs - 답글 입력값 업데이트 함수
 * @param {function} handleReplySubmit - 답글 제출 함수
 * @param {function} startEditingComment - 댓글 수정 모드 시작 핸들러
 * @param {number} editingCommentId - 현재 편집 중인 댓글 ID
 * @param {string} editingCommentContent - 편집 중인 댓글 내용
 * @param {function} setEditingCommentContent - 편집 중인 댓글 내용 업데이트 함수
 * @param {function} submitCommentEdit - 댓글 수정 제출 함수
 * @returns {JSX.Element} - 재귀적으로 렌더링된 답글 목록
 */
const renderReplies = (
  replies,
  parentFeedId,
  memberId,
  likedComments,
  handleCommentLike,
  toggleReplyInput,
  showReplyInput,
  replyInputs,
  setReplyInputs,
  handleReplySubmit,
  startEditingComment,
  editingCommentId,
  editingCommentContent,
  setEditingCommentContent,
  submitCommentEdit
) => {
  return (
    <ReplyContainer>
      {replies.map((reply, idx) => (
        <CommentCard
          key={reply.commentId || idx}
          style={{
            padding: "10px",
            borderBottom: "1px solid #eee",
            display: "flex",
            flexDirection: "column",
            gap: "5px",
          }}
        >
          <div style={{ display: "flex", alignItems: "center" }}>
            <img
              src={reply.profilePictureUrl || imgLogo2}
              alt="답글 작성자 이미지"
              style={{
                width: "25px",
                height: "25px",
                borderRadius: "50%",
                marginRight: "8px",
              }}
            />
            <span style={{ fontWeight: "bold", fontSize: "13px" }}>
              {reply.memberName || "Unknown"}
            </span>
          </div>
          {editingCommentId === reply.commentId ? (
            <div>
              <CommentInput
                type="text"
                value={editingCommentContent}
                onChange={(e) => setEditingCommentContent(e.target.value)}
                placeholder="댓글 수정..."
              />
              <button onClick={submitCommentEdit}>저장</button>
              <button onClick={() => startEditingComment(null)}>취소</button>
            </div>
          ) : (
            <div style={{ fontSize: "13px" }}>{reply.comment}</div>
          )}
          <div style={{ display: "flex", gap: "10px" }}>
            <button
              style={{
                border: "none",
                background: "transparent",
                color: "#0073b1",
                fontSize: "12px",
                cursor: "pointer",
              }}
              onClick={() => handleCommentLike(reply.commentId)}
            >
              {likedComments[reply.commentId] ? "Unlike" : "Like"} (
              {reply.likesCount != null ? reply.likesCount : 0})
            </button>
            <button
              style={{
                border: "none",
                background: "transparent",
                color: "#0073b1",
                fontSize: "12px",
                cursor: "pointer",
              }}
              onClick={() => toggleReplyInput(reply.commentId)}
            >
              Reply
            </button>
            {reply.memberId === memberId && (
              <button
                style={{
                  border: "none",
                  background: "transparent",
                  color: "#0073b1",
                  fontSize: "12px",
                  cursor: "pointer",
                }}
                onClick={() => startEditingComment(reply)}
              >
                수정
              </button>
            )}
          </div>
          {showReplyInput[reply.commentId] && (
            <div
              style={{
                display: "flex",
                alignItems: "center",
                marginTop: "5px",
              }}
            >
              <CommentInput
                type="text"
                placeholder="답글 추가..."
                value={replyInputs[reply.commentId] || ""}
                onChange={(e) =>
                  setReplyInputs((prev) => ({
                    ...prev,
                    [reply.commentId]: e.target.value,
                  }))
                }
                onKeyDown={(e) => {
                  if (e.key === "Enter")
                    handleReplySubmit(reply.commentId, parentFeedId);
                }}
              />
              <button
                style={{ padding: "5px", fontSize: "12px", cursor: "pointer" }}
                onClick={() => handleReplySubmit(reply.commentId, parentFeedId)}
              >
                Send
              </button>
            </div>
          )}
          {reply.replies &&
            reply.replies.length > 0 &&
            renderReplies(
              reply.replies,
              parentFeedId,
              memberId,
              likedComments,
              handleCommentLike,
              toggleReplyInput,
              showReplyInput,
              replyInputs,
              setReplyInputs,
              handleReplySubmit,
              startEditingComment,
              editingCommentId,
              editingCommentContent,
              setEditingCommentContent,
              submitCommentEdit
            )}
        </CommentCard>
      ))}
    </ReplyContainer>
  );
};

/**
 * 📌 Feed 컴포넌트
 *
 * 이 컴포넌트는 현재 로그인한 회원의 정보를 기반으로 피드를 불러오며,
 * 무한 스크롤, 피드 생성, 좋아요, 댓글 등의 인터랙션을 처리합니다.
 *
 * 주의: 피드와 친구 추천 API 호출은 유효한 memberId가 있을 때만 실행됩니다.
 */
function Feed() {
  // ─────────────────────────
  // State 선언
  const [posts, setPosts] = useState([]);
  const [loading, setLoading] = useState(false);
  const [page, setPage] = useState(0);
  const [hasMore, setHasMore] = useState(true);
  const [newFeed, setNewFeed] = useState("");
  const [image, setImage] = useState(null);
  const observer = useRef();
  const [refreshing, setRefreshing] = useState(false);
  const [memberId, setMemberId] = useState(null);
  const [memberData, setMemberData] = useState(null);
  const [likedPosts, setLikedPosts] = useState({});
  const [likeLoading, setLikeLoading] = useState({});
  const [showCommentInput, setShowCommentInput] = useState({});
  const [commentInputs, setCommentInputs] = useState({});
  const [showRepostInput, setShowRepostInput] = useState({});
  const [repostInputs, setRepostInputs] = useState({});
  const [likedComments, setLikedComments] = useState({});
  const [showReplyInput, setShowReplyInput] = useState({});
  const [replyInputs, setReplyInputs] = useState({});
  const [editingFeedId, setEditingFeedId] = useState(null);
  const [editingFeedContent, setEditingFeedContent] = useState("");
  const [editingCommentId, setEditingCommentId] = useState(null);
  const [editingCommentContent, setEditingCommentContent] = useState("");
  const { profileInfo } = useProfile();

  // ─────────────────────────
  // 사용자 정보 가져오기
  /**
   * 현재 로그인한 사용자의 정보를 가져와 상태를 업데이트합니다.
   * getUserInfo() API를 호출하여 유효한 회원 정보를 받아오며,
   * memberId와 기타 사용자 정보를 설정합니다.
   */
  const fetchMemberData = async () => {
    try {
      const userInfo = await getUserInfo();
      console.log("Fetched userInfo:", userInfo);
      if (userInfo && userInfo.memberId) {
        setMemberId(userInfo.memberId);
        setMemberData({
          name: userInfo.name,
          currentCompany: userInfo.currentCompany,
          profilePictureUrl: userInfo.profilePictureUrl,
        });
      } else {
        toast.error("로그인이 필요합니다.");
      }
    } catch (error) {
      console.error("사용자 정보를 가져오는 중 오류:", error);
      toast.error("사용자 정보를 확인할 수 없습니다.");
    }
  };

  // ─────────────────────────
  // useEffect: 컴포넌트 마운트 시 사용자 정보 먼저 불러오기
  useEffect(() => {
    fetchMemberData();
  }, []);

  // ─────────────────────────
  // useEffect: memberId가 설정되면 피드를 불러옵니다.
  useEffect(() => {
    if (memberId) {
      console.log("memberId is set:", memberId, "/ page:", page);
      fetchFeedPosts();
    }
  }, [memberId, page]);

  // ─────────────────────────
  // 피드 불러오기 함수
  /**
   * memberId가 유효할 때, page와 size를 이용해 API 호출을 수행하여 피드를 불러옵니다.
   * 반환된 데이터가 없으면 hasMore를 false로 설정합니다.
   */
  const fetchFeedPosts = async () => {
    if (!memberId) return;
    setLoading(true);
    try {
      const data = await FeedApi.fetchFeeds(page, 10, memberId);
      if (data.length === 0) setHasMore(false);
      // 새로운 피드를 기존 목록 앞에 추가
      setPosts((prevPosts) => [...data, ...prevPosts]);
      // 좋아요 상태 업데이트
      setLikedPosts((prev) => {
        const newLiked = { ...prev };
        data.forEach((post) => {
          newLiked[post.feedId] = post.liked || false;
        });
        return newLiked;
      });
      // 댓글 좋아요 상태 업데이트
      setLikedComments((prev) => {
        const newLikedComments = { ...prev };
        data.forEach((post) => {
          if (post.comments && Array.isArray(post.comments)) {
            post.comments.forEach((comment) => {
              newLikedComments[comment.commentId] = comment.liked || false;
              if (comment.replies && Array.isArray(comment.replies)) {
                comment.replies.forEach((reply) => {
                  newLikedComments[reply.commentId] = reply.liked || false;
                });
              }
            });
          }
        });
        return newLikedComments;
      });
    } catch (error) {
      console.error("❌ 피드 가져오기 실패:", error);
      toast.error("피드를 불러오는데 실패했습니다.");
    }
    setLoading(false);
  };

  // ─────────────────────────
  // IntersectionObserver (무한 스크롤)
  /**
   * 마지막 게시글 요소를 관찰하여, 화면에 보이면 page를 증가시켜 추가 피드를 로딩합니다.
   *
   * @param {HTMLElement} node - 관찰 대상 DOM 요소 (마지막 게시글 요소)
   */
  const lastPostElementRef = (node) => {
    if (loading) return;
    if (observer.current) observer.current.disconnect();
    observer.current = new IntersectionObserver((entries) => {
      if (entries[0].isIntersecting && hasMore) {
        setPage((prevPage) => prevPage + 1);
      }
    });
    if (node) observer.current.observe(node);
  };

  // ─────────────────────────
  // 핸들러들 (피드 생성, 좋아요, 댓글 추가, 리포스트, 등)
  const handleCreateFeed = async () => {
    if (!newFeed.trim() && !image) return;
    const data = { memberId, content: newFeed, mediaUrl: image };
    try {
      const createdPost = await FeedApi.createFeed(data);
      setPosts((prevPosts) => [createdPost, ...prevPosts]);
      setNewFeed("");
      setImage(null);
      toast.success("피드 작성이 완료되었습니다.");
    } catch (error) {
      console.error("❌ 피드 작성 실패:", error);
      toast.error("피드 작성에 실패했습니다.");
    }
  };

  const handleSaveFeed = async (feedId) => {
    try {
      await FeedApi.saveFeed(feedId);
      toast.success("게시글이 저장되었습니다!");
    } catch (error) {
      console.error("❌ 게시글 저장 실패:", error);
      toast.error("게시글 저장에 실패했습니다.");
    }
  };

  const handleRefreshFeeds = () => {
    setRefreshing(true);
    setPage(0);
    setPosts([]);
    setHasMore(true);
    fetchFeedPosts();
    setTimeout(() => setRefreshing(false), 300);
  };

  const handleImageChange = (e) => {
    const file = e.target.files[0];
    if (file) {
      const reader = new FileReader();
      reader.onloadend = () => {
        setImage(reader.result);
      };
      reader.readAsDataURL(file);
    }
  };

  const handleLike = async (feedId) => {
    if (likeLoading[feedId]) return;
    setLikeLoading((prev) => ({ ...prev, [feedId]: true }));
    try {
      if (likedPosts[feedId]) {
        await FeedApi.unlikeFeed(feedId, memberId);
      } else {
        await FeedApi.likeFeed(feedId, memberId);
      }
      const updatedFeed = await FeedApi.getFeedById(feedId, memberId);
      setPosts((prevPosts) =>
        prevPosts.map((post) =>
          post.feedId === feedId ? mergeFeedData(post, updatedFeed) : post
        )
      );
      setLikedPosts((prev) => ({
        ...prev,
        [feedId]: updatedFeed.liked,
      }));
    } catch (error) {
      console.error("❌ Like/Unlike failed", error);
      toast.error("좋아요 처리에 실패했습니다.");
    } finally {
      setLikeLoading((prev) => ({ ...prev, [feedId]: false }));
    }
  };

  const toggleCommentInput = (feedId) => {
    setShowCommentInput((prev) => ({
      ...prev,
      [feedId]: !prev[feedId],
    }));
  };

  const handleCommentInputChange = (feedId, value) => {
    setCommentInputs((prev) => ({ ...prev, [feedId]: value }));
  };

  const handleCommentSubmit = async (feedId) => {
    const comment = commentInputs[feedId];
    if (!comment || !comment.trim()) return;
    try {
      const newComment = await FeedApi.addComment(feedId, {
        comment,
        memberId,
      });
      // Set default values if missing
      if (!newComment.memberName || newComment.memberName === "Unknown") {
        newComment.memberName = memberData ? memberData.name : "Unknown";
        newComment.currentCompany = memberData
          ? memberData.currentCompany
          : "미등록 회사";
        newComment.profilePictureUrl = memberData
          ? memberData.profilePictureUrl
          : "";
      }
      setCommentInputs((prev) => ({ ...prev, [feedId]: "" }));
      toast.success("댓글이 등록되었습니다.");
      setPosts((prevPosts) =>
        prevPosts.map((post) =>
          post.feedId === feedId
            ? {
                ...post,
                comments: post.comments
                  ? [newComment, ...post.comments]
                  : [newComment],
              }
            : post
        )
      );
      setLikedComments((prev) => ({
        ...prev,
        [newComment.commentId]: newComment.liked,
      }));
    } catch (error) {
      console.error("❌ 댓글 제출 실패", error.response?.data || error);
      toast.error("댓글 등록에 실패했습니다.");
    }
  };

  const toggleRepostInput = (feedId) => {
    setShowRepostInput((prev) => ({ ...prev, [feedId]: !prev[feedId] }));
  };

  const handleRepostInputChange = (feedId, value) => {
    setRepostInputs((prev) => ({ ...prev, [feedId]: value }));
  };

  const handleRepostSubmit = async (feedId) => {
    const repostComment = repostInputs[feedId] || "";
    try {
      await FeedApi.repostFeed(feedId, memberId, { content: repostComment });
      setRepostInputs((prev) => ({ ...prev, [feedId]: "" }));
      toast.success("리포스트가 완료되었습니다!");
    } catch (error) {
      console.error("❌ Repost failed", error);
      toast.error("리포스트 처리에 실패했습니다.");
    }
  };

  const handleCommentLike = async (commentId) => {
    try {
      if (likedComments[commentId]) {
        await FeedApi.unlikeComment(commentId, memberId);
        setLikedComments((prev) => ({ ...prev, [commentId]: false }));
        setPosts((prevPosts) =>
          prevPosts.map((post) => {
            if (!post.comments) return post;
            const updatedComments = post.comments.map((comment) =>
              comment.commentId === commentId
                ? {
                    ...comment,
                    likesCount: Math.max((comment.likesCount || 1) - 1, 0),
                    liked: false,
                  }
                : comment
            );
            return { ...post, comments: updatedComments };
          })
        );
      } else {
        await FeedApi.likeComment(commentId, memberId);
        setLikedComments((prev) => ({ ...prev, [commentId]: true }));
        setPosts((prevPosts) =>
          prevPosts.map((post) => {
            if (!post.comments) return post;
            const updatedComments = post.comments.map((comment) =>
              comment.commentId === commentId
                ? {
                    ...comment,
                    likesCount: (comment.likesCount || 0) + 1,
                    liked: true,
                  }
                : comment
            );
            return { ...post, comments: updatedComments };
          })
        );
      }
      toast.success("댓글 좋아요 상태가 변경되었습니다.");
    } catch (error) {
      console.error("❌ 댓글 좋아요 처리 오류:", error);
      toast.error("댓글 좋아요 처리에 실패했습니다.");
    }
  };

  const toggleReplyInput = (commentId) => {
    setShowReplyInput((prev) => ({ ...prev, [commentId]: !prev[commentId] }));
  };

  const handleReplySubmit = async (commentId, feedId) => {
    const reply = replyInputs[commentId];
    if (!reply || !reply.trim()) return;
    try {
      const newReply = await FeedApi.addComment(feedId, {
        comment: reply,
        memberId,
        parentCommentId: commentId,
      });
      setReplyInputs((prev) => ({ ...prev, [commentId]: "" }));
      toast.success("답글이 등록되었습니다.");
      setPosts((prevPosts) =>
        prevPosts.map((post) => {
          if (post.feedId !== feedId) return post;
          const updatedComments = post.comments.map((c) =>
            c.commentId === commentId
              ? {
                  ...c,
                  replies: c.replies ? [...c.replies, newReply] : [newReply],
                }
              : c
          );
          return { ...post, comments: updatedComments };
        })
      );
    } catch (error) {
      console.error("❌ 답글 제출 실패", error.response?.data || error);
      toast.error("답글 등록에 실패했습니다.");
    }
  };

  const startEditingFeed = (feed) => {
    if (feed.memberId !== memberId) {
      toast.error("자신의 게시글만 수정할 수 있습니다.");
      return;
    }
    setEditingFeedId(feed.feedId);
    setEditingFeedContent(feed.content);
  };

  const submitFeedEdit = async () => {
    if (!editingFeedContent.trim()) return;
    try {
      const updatedFeed = await FeedApi.editFeed(editingFeedId, {
        memberId,
        content: editingFeedContent,
      });
      setPosts((prevPosts) =>
        prevPosts.map((post) =>
          post.feedId === editingFeedId
            ? mergeFeedData(post, updatedFeed)
            : post
        )
      );
      setEditingFeedId(null);
      setEditingFeedContent("");
      toast.success("게시글이 수정되었습니다.");
    } catch (error) {
      console.error("❌ 피드 수정 실패:", error);
      toast.error("게시글 수정에 실패했습니다.");
    }
  };

  const startEditingComment = (comment) => {
    if (!comment || comment.memberId !== memberId) {
      toast.error("자신의 댓글만 수정할 수 있습니다.");
      return;
    }
    setEditingCommentId(comment.commentId);
    setEditingCommentContent(comment.comment);
  };

  const submitCommentEdit = async () => {
    if (!editingCommentContent.trim()) return;
    try {
      const updatedComment = await FeedApi.editComment(editingCommentId, {
        memberId,
        comment: editingCommentContent,
      });
      setPosts((prevPosts) =>
        prevPosts.map((post) => {
          if (!post.comments) return post;
          return {
            ...post,
            comments: updateCommentRecursively(
              post.comments,
              editingCommentId,
              updatedComment
            ),
          };
        })
      );
      setEditingCommentId(null);
      setEditingCommentContent("");
      toast.success("댓글이 수정되었습니다.");
    } catch (error) {
      console.error("❌ 댓글 수정 실패:", error);
      toast.error("댓글 수정에 실패했습니다.");
    }
  };

  // ─────────────────────────
  // Logging profile info changes (for debugging)
  useEffect(() => {
    console.log("Profile Info updated:", profileInfo);
  }, [profileInfo]);

  return (
    <LayoutContainer>
      <ProfileSection>
        <ProfileImage src={imgLogo2} alt="프로필 이미지" />
        <p>Email: {profileInfo.email}</p>
        <p>Name: {profileInfo.name}</p>
      </ProfileSection>

      <FeedContainer>
        <CreateFeedContainer>
          <TextareaContainer>
            <textarea
              value={newFeed}
              onChange={(e) => setNewFeed(e.target.value)}
              placeholder="새 피드를 작성하세요..."
              rows="3"
            />
            <input
              id="image-upload"
              type="file"
              accept="image/*"
              onChange={handleImageChange}
              style={{ display: "none" }}
            />
            <UploadIconLabel htmlFor="image-upload">
              <UploadIcon src={imgLogo3} alt="이미지 업로드" />
            </UploadIconLabel>
          </TextareaContainer>
          {image && <UploadedImage src={image} alt="업로드된 이미지" />}
          <button onClick={handleCreateFeed}>피드 작성</button>
        </CreateFeedContainer>

        <RefreshButton onClick={handleRefreshFeeds}>
          <RefreshIcon
            className={refreshing ? "refreshing" : ""}
            src={imgLogo1}
            alt="새로고침"
          />
        </RefreshButton>

        <PostList>
          {posts.map((post, index) => {
            const isLastPost = posts.length === index + 1;
            return (
              <Post
                key={post.feedId}
                ref={isLastPost ? lastPostElementRef : null}
              >
                <PostHeader>
                  <AuthorImage
                    src={post.profilePictureUrl || imgLogo2}
                    alt="회원 이미지"
                  />
                  <AuthorDetails>
                    <AuthorName>
                      {post.originalPoster
                        ? post.originalPoster.name
                        : "Unknown"}
                    </AuthorName>
                    <PostDate>{post.createdAt}</PostDate>
                  </AuthorDetails>
                  {post.memberId === memberId && (
                    <button
                      onClick={() => startEditingFeed(post)}
                      style={{ marginLeft: "auto" }}
                    >
                      수정
                    </button>
                  )}
                </PostHeader>
                {post.isRepost && post.repostedFromContent && (
                  <OriginalPostContainer>
                    <OriginalPostHeader>원본 게시글</OriginalPostHeader>
                    <OriginalPostContent>
                      {post.repostedFromContent}
                    </OriginalPostContent>
                  </OriginalPostContainer>
                )}
                {editingFeedId === post.feedId ? (
                  <div>
                    <textarea
                      value={editingFeedContent}
                      onChange={(e) => setEditingFeedContent(e.target.value)}
                      rows="3"
                    />
                    <button onClick={submitFeedEdit}>저장</button>
                    <button onClick={() => setEditingFeedId(null)}>취소</button>
                  </div>
                ) : (
                  <p>{post.content}</p>
                )}
                {post.mediaUrl && (
                  <UploadedImage src={post.mediaUrl} alt="게시글 이미지" />
                )}
                <hr />
                <PostActions>
                  <ActionButton onClick={() => handleLike(post.feedId)}>
                    {likedPosts[post.feedId] ? "Unlike" : "Like"} (
                    {post.likesCount})
                  </ActionButton>
                  <ActionButton onClick={() => toggleCommentInput(post.feedId)}>
                    Comment
                  </ActionButton>
                  <ActionButton onClick={() => toggleRepostInput(post.feedId)}>
                    Repost
                  </ActionButton>
                  <ActionButton onClick={() => handleSaveFeed(post.feedId)}>
                    Save
                  </ActionButton>
                </PostActions>
                {showCommentInput[post.feedId] && (
                  <CommentContainer>
                    <CommentInputContainer>
                      <CommentInput
                        type="text"
                        placeholder="댓글 추가..."
                        value={commentInputs[post.feedId] || ""}
                        onChange={(e) =>
                          handleCommentInputChange(post.feedId, e.target.value)
                        }
                        onKeyDown={(e) => {
                          if (e.key === "Enter")
                            handleCommentSubmit(post.feedId);
                        }}
                      />
                      <CommentSubmitIcon
                        onClick={() => handleCommentSubmit(post.feedId)}
                      >
                        &#10148;
                      </CommentSubmitIcon>
                    </CommentInputContainer>
                  </CommentContainer>
                )}
                {post.comments && post.comments.length > 0 && (
                  <div style={{ marginTop: "10px" }}>
                    {post.comments.map((comment, idx) => (
                      <CommentCard
                        key={
                          comment.commentId
                            ? `${comment.commentId}-${idx}`
                            : `${idx}`
                        }
                      >
                        <div style={{ display: "flex", alignItems: "center" }}>
                          <img
                            src={comment.profilePictureUrl || imgLogo2}
                            alt="댓글 작성자 이미지"
                            style={{
                              width: "30px",
                              height: "30px",
                              borderRadius: "50%",
                              marginRight: "10px",
                            }}
                          />
                          <div
                            style={{ display: "flex", flexDirection: "column" }}
                          >
                            <span
                              style={{ fontWeight: "bold", fontSize: "14px" }}
                            >
                              {comment.memberName || "Unknown"}
                            </span>
                            <span style={{ fontSize: "12px", color: "#888" }}>
                              {comment.currentCompany || "미등록 회사"}
                            </span>
                          </div>
                        </div>
                        {editingCommentId === comment.commentId ? (
                          <div>
                            <CommentInput
                              type="text"
                              value={editingCommentContent}
                              onChange={(e) =>
                                setEditingCommentContent(e.target.value)
                              }
                              placeholder="댓글 수정..."
                            />
                            <button onClick={submitCommentEdit}>저장</button>
                            <button onClick={() => setEditingCommentId(null)}>
                              취소
                            </button>
                          </div>
                        ) : (
                          <div style={{ fontSize: "14px" }}>
                            {comment.comment}
                          </div>
                        )}
                        <div style={{ display: "flex", gap: "10px" }}>
                          <button
                            style={{
                              border: "none",
                              background: "transparent",
                              color: "#0073b1",
                              fontSize: "12px",
                              cursor: "pointer",
                            }}
                            onClick={() => handleCommentLike(comment.commentId)}
                          >
                            {likedComments[comment.commentId]
                              ? "Unlike"
                              : "Like"}{" "}
                            (
                            {comment.likesCount != null
                              ? comment.likesCount
                              : 0}
                            )
                          </button>
                          <button
                            style={{
                              border: "none",
                              background: "transparent",
                              color: "#0073b1",
                              fontSize: "12px",
                              cursor: "pointer",
                            }}
                            onClick={() => toggleReplyInput(comment.commentId)}
                          >
                            Reply
                          </button>
                          {comment.memberId === memberId && (
                            <button
                              onClick={() => startEditingComment(comment)}
                              style={{
                                border: "none",
                                background: "transparent",
                                color: "#0073b1",
                                fontSize: "12px",
                                cursor: "pointer",
                              }}
                            >
                              수정
                            </button>
                          )}
                        </div>
                        {comment.replies &&
                          comment.replies.length > 0 &&
                          renderReplies(
                            comment.replies,
                            post.feedId,
                            memberId,
                            likedComments,
                            handleCommentLike,
                            toggleReplyInput,
                            showReplyInput,
                            replyInputs,
                            setReplyInputs,
                            handleReplySubmit,
                            startEditingComment,
                            editingCommentId,
                            editingCommentContent,
                            setEditingCommentContent,
                            submitCommentEdit
                          )}
                        {showReplyInput[comment.commentId] && (
                          <div
                            style={{
                              display: "flex",
                              alignItems: "center",
                              marginTop: "5px",
                            }}
                          >
                            <CommentInput
                              type="text"
                              placeholder="답글 추가..."
                              value={replyInputs[comment.commentId] || ""}
                              onChange={(e) =>
                                setReplyInputs((prev) => ({
                                  ...prev,
                                  [comment.commentId]: e.target.value,
                                }))
                              }
                              onKeyDown={(e) => {
                                if (e.key === "Enter")
                                  handleReplySubmit(
                                    comment.commentId,
                                    post.feedId
                                  );
                              }}
                            />
                            <button
                              style={{
                                padding: "5px",
                                fontSize: "12px",
                                cursor: "pointer",
                              }}
                              onClick={() =>
                                handleReplySubmit(
                                  comment.commentId,
                                  post.feedId
                                )
                              }
                            >
                              Send
                            </button>
                          </div>
                        )}
                      </CommentCard>
                    ))}
                  </div>
                )}
                {showRepostInput[post.feedId] && (
                  <RepostContainer>
                    <RepostInput
                      type="text"
                      placeholder="리포스트 시 코멘트 추가..."
                      value={repostInputs[post.feedId] || ""}
                      onChange={(e) =>
                        handleRepostInputChange(post.feedId, e.target.value)
                      }
                    />
                    <RepostSubmitButton
                      onClick={() => handleRepostSubmit(post.feedId)}
                    >
                      Repost
                    </RepostSubmitButton>
                  </RepostContainer>
                )}
              </Post>
            );
          })}
        </PostList>
      </FeedContainer>

      <FriendsSection>
        <h2>친구 추천</h2>
        <FriendList>
          <FriendSuggestions memberId={memberId} />
        </FriendList>
      </FriendsSection>

      <ToastContainer position="top-right" autoClose={3000} hideProgressBar />
    </LayoutContainer>
  );
}

/**
 * 친구 추천 컴포넌트
 *
 * 서버에서 랜덤 사용자 정보를 불러와 친구 추천 목록을 표시합니다.
 *
 * @param {object} props
 * @param {number} props.memberId - 현재 로그인한 사용자 ID
 * @component
 */
function FriendSuggestions({ memberId }) {
  const [friendList, setFriendList] = useState([]);

  useEffect(() => {
    async function fetchFriends() {
      try {
        const suggestedFriends = await FeedApi.fetchSuggestedFriends(memberId);
        setFriendList(suggestedFriends);
      } catch (error) {
        console.error("❌ 친구 추천 불러오기 실패:", error);
      }
    }
    fetchFriends();
  }, [memberId]);

  return (
    <>
      {friendList.map((friend) => (
        <div
          key={friend.memberId}
          style={{
            display: "flex",
            justifyContent: "space-between",
            alignItems: "center",
            marginBottom: "15px",
            padding: "10px",
            borderRadius: "8px",
            backgroundColor: "#f9f9f9",
            boxShadow: "0 1px 3px rgba(0, 0, 0, 0.1)",
          }}
        >
          <div style={{ display: "flex", alignItems: "center" }}>
            <img
              src={friend.profilePictureUrl || imgLogo2}
              alt="친구 이미지"
              style={{
                width: "40px",
                height: "40px",
                borderRadius: "50%",
                marginRight: "10px",
              }}
            />
            <div style={{ display: "flex", flexDirection: "column" }}>
              <span>{friend.name}</span>
              <span style={{ fontSize: "12px", color: "#888" }}>
                {friend.currentCompany || "미등록 회사"}
              </span>
            </div>
          </div>
          <div style={{ display: "flex", gap: "5px" }}>
            <button
              style={{
                backgroundColor: "#4caf50",
                color: "white",
                border: "none",
                borderRadius: "5px",
                padding: "5px 10px",
                fontSize: "14px",
                cursor: "pointer",
              }}
            >
              친구 요청
            </button>
            <button
              style={{
                backgroundColor: "#f0f0f0",
                color: "#555",
                border: "none",
                borderRadius: "5px",
                padding: "5px 10px",
                fontSize: "14px",
                cursor: "pointer",
              }}
            >
              메시지
            </button>
          </div>
        </div>
      ))}
    </>
  );
}

export default Feed;
