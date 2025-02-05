import React, { useState, useEffect, useRef } from "react";
import { useProfile } from "../../pages/ProfileContext";
import { ToastContainer, toast } from "react-toastify";
import "react-toastify/dist/ReactToastify.css";
import FeedApi from "../../api/FeedApi";
import { getUserInfo } from "../../axios/AxiosInstanse";

// 이미지 파일 임포트
import imgLogo1 from "../../images/RefreshButton.png";
import imgLogo2 from "../../images/DeveloperMark.jpg";
import imgLogo3 from "../../images/PictureButton.png";

// 스타일 컴포넌트 임포트
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
 * Recursively update a comment (or nested reply) in an array.
 * The third parameter (updatedData) may be an object or a function that returns an update object.
 */
const updateCommentRecursively = (comments, commentId, updatedData) => {
  return comments.map((comment) => {
    if (comment.commentId === commentId) {
      const newData =
        typeof updatedData === "function" ? updatedData(comment) : updatedData;
      return { ...comment, ...newData };
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
 * Merge two feed objects.
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
 * Recursively render nested replies.
 * Note: We now pass down "commentLikeLoading" so that nested replies can disable their like button when needed.
 */
const renderReplies = (
  replies,
  parentFeedId,
  memberId,
  likedComments,
  commentLikeLoading, // new parameter
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
      {(replies || []).map((reply, idx) => (
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
              onClick={() => handleCommentLike(reply.commentId, parentFeedId)}
              disabled={commentLikeLoading[reply.commentId]}
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
              commentLikeLoading, // pass it along
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
 * Helper to find an updated comment by its id from an array of comments.
 */
const findCommentById = (comments, commentId) => {
  for (let comment of comments) {
    if (comment.commentId === commentId) {
      return comment;
    }
    if (comment.replies && comment.replies.length > 0) {
      const found = findCommentById(comment.replies, commentId);
      if (found) return found;
    }
  }
  return null;
};

/**
 * Feed 컴포넌트
 */
function Feed() {
  // Set page background color.
  useEffect(() => {
    document.body.style.backgroundColor = "#f5f6f7";
  }, []);

  // Feed and related state.
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

  // Feed, comment, and like states.
  const [likedPosts, setLikedPosts] = useState({});
  const [likeLoading, setLikeLoading] = useState({});
  const [showCommentInput, setShowCommentInput] = useState({});
  const [commentInputs, setCommentInputs] = useState({});
  const [showRepostInput, setShowRepostInput] = useState({});
  const [repostInputs, setRepostInputs] = useState({});
  const [likedComments, setLikedComments] = useState({});
  const [showReplyInput, setShowReplyInput] = useState({});
  const [replyInputs, setReplyInputs] = useState({});

  // New state for tracking comment/reply like actions.
  const [commentLikeLoading, setCommentLikeLoading] = useState({});

  // Edit mode states.
  const [editingFeedId, setEditingFeedId] = useState(null);
  const [editingFeedContent, setEditingFeedContent] = useState("");
  const [editingCommentId, setEditingCommentId] = useState(null);
  const [editingCommentContent, setEditingCommentContent] = useState("");

  // Profile context.
  const { profileInfo } = useProfile();
  useEffect(() => {
    console.log("Profile 정보:", profileInfo);
  }, [profileInfo]);

  // Fetch current user info.
  const fetchMemberData = async () => {
    try {
      const userInfo = await getUserInfo();
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

  useEffect(() => {
    fetchMemberData();
  }, []);

  // Fetch feed posts.
  const fetchFeedPosts = async () => {
    if (!memberId) return;
    setLoading(true);
    try {
      const data = await FeedApi.fetchFeeds(page, 10, memberId);
      if (data.length === 0) setHasMore(false);
      setPosts((prevPosts) => [...prevPosts, ...data]);
      // Update feed like states.
      setLikedPosts((prev) => {
        const newLiked = { ...prev };
        data.forEach((post) => {
          newLiked[post.feedId] = post.liked || false;
        });
        return newLiked;
      });
      // Update comment like states.
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

  useEffect(() => {
    fetchFeedPosts();
  }, [page, memberId]);

  // Intersection Observer for infinite scroll.
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

  // Create a feed post.
  const handleCreateFeed = async () => {
    if (!newFeed.trim() && !image) return;
    const data = { memberId, content: newFeed, mediaUrl: image };
    try {
      const createdPost = await FeedApi.createFeed(data);
      setPosts((prevPosts) => [...prevPosts, createdPost]);
      setNewFeed("");
      setImage(null);
      toast.success("피드 작성이 완료되었습니다.");
    } catch (error) {
      console.error("❌ 피드 작성 실패:", error);
      toast.error("피드 작성에 실패했습니다.");
    }
  };

  // Save a feed post.
  const handleSaveFeed = async (feedId) => {
    try {
      await FeedApi.saveFeed(feedId);
      toast.success("게시글이 저장되었습니다!");
    } catch (error) {
      console.error("❌ 게시글 저장 실패:", error);
      toast.error("게시글 저장에 실패했습니다.");
    }
  };

  // Refresh feeds.
  const handleRefreshFeeds = () => {
    setRefreshing(true);
    setPage(0);
    setPosts([]);
    setHasMore(true);
    fetchFeedPosts();
    setTimeout(() => setRefreshing(false), 300);
  };

  // Handle image upload.
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

  // Feed post like/unlike (pessimistic approach).
  const handleLike = async (feedId) => {
    if (likeLoading[feedId]) return;
    setLikeLoading((prev) => ({ ...prev, [feedId]: true }));
    try {
      const currentLiked = likedPosts[feedId] || false;
      if (currentLiked) {
        await FeedApi.unlikeFeed(feedId, memberId);
      } else {
        await FeedApi.likeFeed(feedId, memberId);
      }
      const updatedFeed = await FeedApi.getFeedById(feedId, memberId);
      setLikedPosts((prev) => ({ ...prev, [feedId]: updatedFeed.liked }));
      setPosts((prevPosts) =>
        prevPosts.map((post) =>
          post.feedId === feedId ? mergeFeedData(post, updatedFeed) : post
        )
      );
    } catch (error) {
      console.error("❌ Like/Unlike failed", error);
      toast.error("좋아요 처리에 실패했습니다.");
    } finally {
      setLikeLoading((prev) => ({ ...prev, [feedId]: false }));
    }
  };

  // Toggle comment input visibility.
  const toggleCommentInput = (feedId) => {
    setShowCommentInput((prev) => ({
      ...prev,
      [feedId]: !prev[feedId],
    }));
  };

  // Update comment input.
  const handleCommentInputChange = (feedId, value) => {
    setCommentInputs((prev) => ({ ...prev, [feedId]: value }));
  };

  // Submit a new comment.
  const handleCommentSubmit = async (feedId) => {
    const comment = commentInputs[feedId];
    if (!comment || !comment.trim()) return;
    try {
      const newComment = await FeedApi.addComment(feedId, {
        comment,
        memberId,
      });
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
                comments:
                  (post.comments || []).length > 0
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

  // Toggle repost input visibility.
  const toggleRepostInput = (feedId) => {
    setShowRepostInput((prev) => ({ ...prev, [feedId]: !prev[feedId] }));
  };

  // Update repost input.
  const handleRepostInputChange = (feedId, value) => {
    setRepostInputs((prev) => ({ ...prev, [feedId]: value }));
  };

  // Submit a repost.
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

  /**
   * Toggle comment (or reply) like using a pessimistic approach.
   * We re-fetch the entire feed for that post after the like/unlike API call
   * to ensure the UI reflects the backend state.
   * (Requires feedId to be passed along with commentId.)
   */
  const handleCommentLike = async (commentId, feedId) => {
    if (commentLikeLoading[commentId]) return;
    setCommentLikeLoading((prev) => ({ ...prev, [commentId]: true }));
    try {
      if (likedComments[commentId]) {
        await FeedApi.unlikeComment(commentId, memberId);
      } else {
        await FeedApi.likeComment(commentId, memberId);
      }
      // Re-fetch updated feed data for this post.
      const updatedFeed = await FeedApi.getFeedById(feedId, memberId);
      // Update the posts array for the updated feed.
      setPosts((prevPosts) =>
        prevPosts.map((post) =>
          post.feedId === feedId ? mergeFeedData(post, updatedFeed) : post
        )
      );
      // Extract the updated comment from the updated feed.
      const findCommentById = (comments, id) => {
        for (let comment of comments) {
          if (comment.commentId === id) return comment;
          if (comment.replies) {
            const found = findCommentById(comment.replies, id);
            if (found) return found;
          }
        }
        return null;
      };
      const updatedComment = findCommentById(updatedFeed.comments, commentId);
      if (updatedComment) {
        setLikedComments((prev) => ({
          ...prev,
          [commentId]: updatedComment.liked,
        }));
      }
      toast.success("댓글 좋아요 상태가 변경되었습니다.");
    } catch (error) {
      console.error("❌ 댓글 좋아요 처리 오류:", error);
      toast.error("댓글 좋아요 처리에 실패했습니다.");
    } finally {
      setCommentLikeLoading((prev) => ({ ...prev, [commentId]: false }));
    }
  };

  // Toggle reply input visibility.
  const toggleReplyInput = (commentId) => {
    setShowReplyInput((prev) => ({ ...prev, [commentId]: !prev[commentId] }));
  };

  // Submit a reply.
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
              ? { ...c, replies: (c.replies || []).concat(newReply) }
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

  // Start editing a feed post.
  const startEditingFeed = (feed) => {
    if (feed.memberId !== memberId) {
      toast.error("자신의 게시글만 수정할 수 있습니다.");
      return;
    }
    setEditingFeedId(feed.feedId);
    setEditingFeedContent(feed.content);
  };

  // Submit feed edit.
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

  // Start editing a comment.
  const startEditingComment = (comment) => {
    if (!comment || comment.memberId !== memberId) {
      toast.error("자신의 댓글만 수정할 수 있습니다.");
      return;
    }
    setEditingCommentId(comment.commentId);
    setEditingCommentContent(comment.comment);
  };

  // Submit comment edit.
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

  return (
    <LayoutContainer>
      {/* Profile Section */}
      <ProfileSection>
        <ProfileImage src={imgLogo2} alt="프로필 이미지" />
        <p>Email: {profileInfo.email}</p>
        <p>Name: {profileInfo.name}</p>
      </ProfileSection>

      <FeedContainer>
        {/* Create Feed Section */}
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

        {/* Refresh Button */}
        <RefreshButton onClick={handleRefreshFeeds}>
          <RefreshIcon
            className={refreshing ? "refreshing" : ""}
            src={imgLogo1}
            alt="새로고침"
          />
        </RefreshButton>

        {/* Post List */}
        <PostList>
          {(posts || []).map((post, index) => {
            const isLastPost = (posts || []).length === index + 1;
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
                    <AuthorName>{post.authorName || "Unknown"}</AuthorName>
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
                {post.comments && (post.comments || []).length > 0 && (
                  <div style={{ marginTop: "10px" }}>
                    {(post.comments || []).map((comment, idx) => (
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
                            onClick={() =>
                              handleCommentLike(comment.commentId, post.feedId)
                            }
                            disabled={commentLikeLoading[comment.commentId]}
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
                          (comment.replies || []).length > 0 && (
                            <div>
                              {renderReplies(
                                comment.replies,
                                post.feedId,
                                memberId,
                                likedComments,
                                commentLikeLoading, // pass the loading state
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
                            </div>
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

      {/* Friend Suggestions Section */}
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
 * Friend Suggestions Component.
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
    if (memberId) fetchFriends();
  }, [memberId]);

  return (
    <>
      {(friendList || []).map((friend) => (
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
