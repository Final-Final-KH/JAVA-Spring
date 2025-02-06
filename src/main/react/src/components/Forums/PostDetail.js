import React, { useEffect, useState } from "react";
import { useParams, useNavigate } from "react-router-dom"; // URL에서 postId 추출
import { getUserInfo } from "../../axios/AxiosInstanse"; // 사용자 정보 가져오기
import ForumApi from "../../api/ForumApi"; // API 호출
import { ToastContainer, toast } from "react-toastify"; // Toastify
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
  InlineBlockContainer,
  ReplyQuoteGlobalStyle, // 전역 스타일 (blockquote, reply-quote 등)
  GlobalKeyframes,
} from "../../styles/PostDetailStyles"; // 스타일 컴포넌트

import { FontAwesomeIcon } from "@fortawesome/react-fontawesome";
import {
  faEdit,
  faThumbsUp,
  faReply,
  faDeleteLeft,
  faCircleExclamation,
  faUndo,
  faSpinner,
} from "@fortawesome/free-solid-svg-icons";
import ConfirmationModal from "./ConfirmationModal";
import DOMPurify from "dompurify";

// 🔽 Tiptap 에디터 관련
import { useEditor, EditorContent } from "@tiptap/react";
import StarterKit from "@tiptap/starter-kit";
import Bold from "@tiptap/extension-bold";
import Italic from "@tiptap/extension-italic";
import Underline from "@tiptap/extension-underline";
import Link from "@tiptap/extension-link";
import TextStyle from "@tiptap/extension-text-style";
import Blockquote from "@tiptap/extension-blockquote"; // 인용 노드

const PostDetail = () => {
  const { postId } = useParams();
  const navigate = useNavigate();
  // ─────────────────────────
  // State
  const [post, setPost] = useState(null);
  const [comments, setComments] = useState([]);
  const [newComment, setNewComment] = useState("");
  const [prevPost, setPrevPost] = useState(null);
  const [prevComments, setPrevComments] = useState([]);
  const [replyingTo, setReplyingTo] = useState(null);
  // └ 인용 대상(게시글/댓글)의 {id, authorName, content}

  const [memberId, setMemberId] = useState(null);
  const [isAdmin, setIsAdmin] = useState(false);
  const [loading, setLoading] = useState(true);
  const [loadingCommentId, setLoadingCommentId] = useState(null);

  // 모달 관련
  const [isModalOpen, setIsModalOpen] = useState(false);
  const [modalData, setModalData] = useState({
    type: "",
    id: null,
    content: "",
  });

  /**
   * 앵커 클릭 시 스크롤 위치를 조정하는 이벤트 핸들러를 등록합니다.
   * 내부 앵커(#)인 경우, 해당 요소를 부드럽게 스크롤하여 중앙에 위치시킵니다.
   * 만약 외부 링크인 경우 새 탭에서 열도록 처리합니다.
   */
  useEffect(() => {
    /**
     * a.jump-to-original 링크 클릭 시 실행되는 핸들러
     * @param {Event} e 클릭 이벤트 객체
     */
    const handleAnchorClick = (e) => {
      const target = e.target;
      if (target.matches("a.jump-to-original")) {
        e.preventDefault();
        const href = target.getAttribute("href") || "";
        if (href.startsWith("#")) {
          const anchorId = href.slice(1); // 예: "#comment-123" -> "comment-123"
          const anchorEl = document.getElementById(anchorId);
          if (anchorEl) {
            // 부드럽게 중앙으로 스크롤합니다.
            console.log("Before adding class:", anchorEl.classList);
            anchorEl.scrollIntoView({ behavior: "smooth", block: "center" });

            // CommentCard 요소에 highlight 클래스를 추가합니다.
            anchorEl.classList.add("highlighted");
            console.log("After adding class:", anchorEl.classList);

            // 2초 후 highlight 클래스를 제거합니다.
            setTimeout(() => {
              anchorEl.classList.remove("highlighted");
              console.log("Removed highlighted class:", anchorEl.classList);
            }, 2000);
          }
        } else {
          // 외부 링크인 경우 새 탭에서 엽니다.
          window.open(href, "_blank");
        }
      }
    };

    document.addEventListener("click", handleAnchorClick);
    return () => {
      document.removeEventListener("click", handleAnchorClick);
    };
  }, []);

  /**
   * 이미 인용된 <blockquote>를 제거 (중첩 인용 방지)
   */
  const stripNestedQuotes = (html = "") => {
    return html.replace(/<blockquote[\s\S]*?<\/blockquote>/gi, "");
  };

  /**
   * Tiptap 에디터 초기화
   *  - openOnClick: false => 에디터 내부에서 링크 클릭 비활성(편집중)
   */
  const editor = useEditor({
    extensions: [
      StarterKit,
      Bold,
      Italic,
      Underline,
      Link.configure({ openOnClick: false }),
      TextStyle,
      Blockquote,
    ],
    content: "",
    onUpdate: ({ editor }) => {
      setNewComment(editor.getHTML());
    },
  });

  /**
   * 댓글 리스트를 날짜순으로 정렬 (오래된 것 먼저)
   */
  const sortComments = (arr) => {
    return arr.sort((a, b) => new Date(a.createdAt) - new Date(b.createdAt));
  };

  /**
   * 현재 로그인 사용자 정보 로딩 & 관리자 여부 확인
   */
  const fetchMemberData = async () => {
    try {
      const userInfo = await getUserInfo();
      if (userInfo && userInfo.memberId) {
        setMemberId(userInfo.memberId);
        setIsAdmin(userInfo.role === "ADMIN");
      } else {
        toast.error("로그인이 필요합니다.");
        navigate("/login");
      }
    } catch (error) {
      console.error("사용자 정보를 가져오는 중 오류:", error);
      toast.error("사용자 정보를 확인할 수 없습니다.");
      navigate("/login");
    }
  };

  /**
   * postId 기반으로 게시글 & 댓글 데이터 불러오기
   */
  useEffect(() => {
    const fetchPostData = async () => {
      try {
        await fetchMemberData();
        const postData = await ForumApi.getPostById(postId);
        const commentData = await ForumApi.getCommentsByPostId(postId);

        // 관리자 수정 여부 플래그
        const processedPost = {
          ...postData,
          editedByAdminTitle: postData.editedByTitle === "ADMIN",
          editedByAdminContent: postData.editedByContent === "ADMIN",
        };

        console.log("게시글 (관리자 수정 표시 포함):", processedPost);
        setPost(processedPost);
        setComments(sortComments(commentData));
      } catch (error) {
        console.error("게시글 로딩 중 오류:", error);
        toast.error("게시글 데이터를 불러오는 중 오류가 발생했습니다.");
      } finally {
        setLoading(false);
      }
    };

    fetchPostData();
  }, [postId]);

  /**
   * 모달 열기
   */
  const openModal = (type, id, content = "") => {
    setModalData({ type, id, content });
    setIsModalOpen(true);
  };

  /**
   * Link 추가용 (모달 열기)
   */
  const handleAddLink = () => {
    openModal("addLink", null, "");
  };

  /**
   * 모달 Confirm 시 처리
   */
  const handleModalConfirm = async (content) => {
    const { type, id } = modalData;
    try {
      switch (type) {
        case "deletePost":
          {
            const removedBy =
              memberId === post.memberId ? post.authorName : "ADMIN";
            await ForumApi.deletePost(id, memberId, removedBy);
            toast.success("게시글이 삭제되었습니다.");
            navigate("/forum");
          }
          break;

        case "editPostTitle":
          {
            if (!content.trim()) return toast.warning("제목을 입력해 주세요.");
            const updatedTitle = await ForumApi.updatePostTitle(
              id,
              { title: content },
              memberId
            );
            setPost((prev) => ({
              ...prev,
              title: updatedTitle.title,
              editedByAdminTitle: updatedTitle.editedByTitle === "ADMIN",
            }));
            toast.success("게시글 제목이 수정되었습니다.");
          }
          break;

        case "editPostContent":
          {
            if (!content.trim()) return toast.warning("내용을 입력해 주세요.");
            const updatedContent = await ForumApi.updatePostContent(
              id,
              { content },
              memberId
            );
            setPost((prev) => ({
              ...prev,
              content: updatedContent.content,
              editedByAdminContent: updatedContent.editedByContent === "ADMIN",
            }));
            toast.success("게시글 내용이 수정되었습니다.");
          }
          break;

        case "editComment":
          {
            if (!content.trim())
              return toast.warning("댓글 내용을 입력해 주세요.");
            const updatedComment = await ForumApi.editComment(
              id,
              { newContent: content },
              memberId
            );
            setComments((prevComments) =>
              prevComments.map((comment) =>
                comment.id === id
                  ? {
                      ...comment,
                      ...updatedComment,
                      reportCount: comment.reportCount, // 기존 reportCount 보전
                    }
                  : comment
              )
            );
            toast.success("댓글이 성공적으로 수정되었습니다.");
          }
          break;

        case "deleteComment":
          {
            await ForumApi.deleteComment(id, memberId);
            setComments((prev) =>
              prev.map((c) =>
                c.id === id ? { ...c, content: "[Removed]", hidden: true } : c
              )
            );
            toast.success("댓글이 삭제되었습니다.");
          }
          break;

        case "restoreComment":
          {
            await ForumApi.restoreComment(id, memberId);
            setComments((prev) =>
              prev.map((c) =>
                c.id === id
                  ? {
                      ...c,
                      content: c.originalContent,
                      hidden: false,
                      reportCount: c.reportCount,
                    }
                  : c
              )
            );
            toast.success("댓글이 복원되었습니다.");
          }
          break;

        case "reportPost":
          {
            if (!content.trim())
              return toast.warning("신고 사유를 입력해 주세요.");
            const reportedPost = await ForumApi.reportPost(
              id,
              memberId,
              content
            );
            setPost((prev) => ({
              ...prev,
              reportCount: reportedPost.reportCount,
              hasReported: reportedPost.hasReported,
            }));
            toast.success("게시글이 신고되었습니다.");
          }
          break;

        case "reportComment":
          {
            if (!content.trim())
              return toast.warning("신고 사유를 입력해 주세요.");
            const reportedComment = await ForumApi.reportComment(
              id,
              memberId,
              content
            );
            setComments((prev) =>
              prev.map((c) =>
                c.id === id
                  ? {
                      ...c,
                      reportCount: reportedComment.reportCount,
                      hasReported: true,
                    }
                  : c
              )
            );
            toast.success("댓글이 신고되었습니다.");
          }
          break;

        case "restorePost":
          {
            if (!isAdmin) {
              toast.error("권한이 없습니다. 관리자만 복원할 수 있습니다.");
              return;
            }
            const restoredPost = await ForumApi.restorePost(id, memberId);
            setPost((prev) => ({
              ...prev,
              ...restoredPost,
              hidden: false,
            }));
            toast.success("게시글이 성공적으로 복원되었습니다.");
          }
          break;

        case "addLink":
          {
            // '#' -> 내부 앵커, 아니면 http://, https://로 보정
            if (content.trim()) {
              let formattedUrl = content.trim();
              if (!formattedUrl.startsWith("#")) {
                if (
                  !formattedUrl.startsWith("http://") &&
                  !formattedUrl.startsWith("https://")
                ) {
                  formattedUrl = `https://${formattedUrl}`;
                }
              }
              editor
                .chain()
                .focus()
                .extendMarkRange("link")
                .setLink({ href: formattedUrl })
                .run();
              toast.success("링크가 성공적으로 추가되었습니다.");
            } else {
              toast.warning("URL을 입력해주세요.");
            }
          }
          break;

        default:
          toast.error("알 수 없는 작업입니다.");
      }
    } catch (error) {
      console.error(`${type} 처리 중 오류:`, error);
      toast.error("작업 처리 중 오류가 발생했습니다.");
    } finally {
      setIsModalOpen(false);
    }
  };

  /**
   * blockquote 태그 -> reply-quote 치환
   * + "원본↑" 링크 삽입
   */
  function transformBlockquotesToReplyQuote(html, anchorId, type = "comment") {
    // 1) class="reply-quote"
    let replaced = html.replace(
      /<blockquote([^>]*)>/gi,
      `<blockquote class="reply-quote"$1>`
    );

    // 2) post => "#post-123", comment => "#comment-123"
    const jumpTarget =
      type === "post" ? `post-${anchorId}` : `comment-${anchorId}`;
    const jumpLinkHtml = `<a class="jump-to-original" href="#${jumpTarget}" style="margin-left:8px; font-size:0.8rem; color:#444;">[원본↑]</a>`;

    // 3) blockquote 첫 번째 <p>뒤에 jumpLinkHtml 삽입
    replaced = replaced.replace(
      /(<blockquote[^>]*>\s*<p[^>]*>[^<]+<\/p>)/gi,
      `$1 ${jumpLinkHtml}`
    );

    return replaced;
  }

  /**
   * 특정 게시글/댓글에 대한 인용(답글)
   * - reply(인용) 상태 저장
   * - Tiptap에 blockquote 노드 삽입
   */
  const handleReply = (target, type) => {
    setReplyingTo((prev) => [
      ...(prev || []),
      {
        type,
        id: target.id,
        authorName: target.authorName,
        content: target.content,
      },
    ]);

    // 1) 중첩 blockquote 제거
    const cleaned = stripNestedQuotes(target.content);
    // 2) HTML 태그 제거 -> 텍스트만 추출
    const textOnly = cleaned.replace(/<[^>]*>/g, "");

    // 3) 에디터에 blockquote 노드 삽입
    editor
      .chain()
      .focus()
      .insertContent([
        {
          type: "blockquote",
          attrs: { class: "reply-quote" },
          content: [
            {
              type: "paragraph",
              content: [
                {
                  type: "text",
                  text: target.authorName,
                  marks: [{ type: "bold" }],
                },
                {
                  type: "text",
                  text: `: ${textOnly} `,
                },
                {
                  type: "text",
                  text: "[원본↑]",
                  marks: [
                    {
                      type: "link",
                      attrs: {
                        href: `#comment-${target.id}`,
                        class: "jump-to-original",
                      },
                    },
                  ],
                },
              ],
            },
          ],
        },
        { type: "paragraph", content: [] },
      ])
      .run();

    toast.info(`${target.authorName}님의 댓글을 인용합니다.`);
  };

  /**
   * 답글(인용) 상태 초기화
   */
  const resetReplying = () => {
    setReplyingTo([]);
  };

  /**
   * 댓글 추가 처리
   */
  const handleAddComment = async () => {
    const rawHTML = editor?.getHTML();
    console.log("에디터 원본 HTML:", rawHTML);

    if (!rawHTML || rawHTML.trim() === "" || rawHTML === "<p></p>") {
      toast.warning("댓글이 비어있거나 잘못된 형식입니다.");
      return;
    }

    // DOMPurify로 XSS방지, #anchors 등 유지
    const sanitized = DOMPurify.sanitize(rawHTML.trim(), {
      ADD_TAGS: ["a"],
      ADD_ATTR: ["href", "target", "rel"],
      ALLOWED_URI_REGEXP: /^(?:#|https?:|mailto:|tel:|ftp)/,
    });
    console.log("1차 정화된 HTML:", sanitized);

    // 혹시 완전 빈 <p>만 남았나 확인
    if (
      sanitized.includes("<p><p></p></p>") ||
      sanitized.trim() === "<p></p>"
    ) {
      toast.warning("유효하지 않은 내용입니다. 다시 확인해주세요.");
      return;
    }

    // 모든 <a> 링크를 검사
    const linkRegex = /<a\s+href=["']([^"']*)["']/g;
    const allMatches = sanitized.matchAll(linkRegex);
    for (const match of allMatches) {
      const href = match[1];
      // 허용: '#' 시작, 'http://', 'https://'
      if (
        !href.startsWith("#") &&
        !href.startsWith("http://") &&
        !href.startsWith("https://")
      ) {
        toast.warning(
          "URL은 http:// 또는 https:// 로 시작하거나, '#' 내부 앵커를 사용해야 합니다."
        );
        return;
      }
    }

    // 인용(replyingTo)이 있다면 마지막 인용 대상 blockquote로 치환
    let finalHTML = sanitized;
    if (replyingTo && replyingTo.length > 0) {
      const lastRef = replyingTo[replyingTo.length - 1];
      finalHTML = transformBlockquotesToReplyQuote(
        sanitized,
        lastRef.id,
        lastRef.type
      );
    }

    console.log("최종 치환된 HTML:", finalHTML);

    try {
      const response = await ForumApi.addComment({
        postId: post.id,
        memberId,
        content: finalHTML,
        parentCommentId: replyingTo?.parentCommentId || null,
        opAuthorName: replyingTo?.authorName || null,
        opContent: replyingTo?.content || null,
      });

      console.log("서버가 반환한 response:", response);

      const newComment = {
        ...response,
        reportCount: response.reportCount || 0,
      };

      // 댓글 목록 즉시 반영
      setComments((prev) => {
        const sorted = sortComments([...prev, newComment]);
        console.log("새 댓글 목록:", sorted);
        return sorted;
      });

      // 에디터/인용 상태 초기화
      editor?.commands.clearContent();
      resetReplying();
      toast.success("댓글이 성공적으로 추가되었습니다.");
    } catch (error) {
      console.error("댓글 추가 중 오류:", error);
      toast.error("댓글 추가에 실패했습니다.");
    }
  };

  /**
   * 게시글 좋아요 처리
   */
  const handleLike = async () => {
    try {
      if (!memberId) {
        await fetchMemberData();
      }
      if (!memberId) return;

      const updatedPost = await ForumApi.toggleLikePost(post.id, memberId);
      setPost((prev) => ({
        ...prev,
        likesCount: updatedPost.totalLikes,
        liked: updatedPost.liked,
      }));

      toast.success("좋아요 상태가 변경되었습니다.");
    } catch (error) {
      console.error("게시글 좋아요 오류:", error);
      toast.error("좋아요 처리에 실패했습니다.");
    }
  };

  /**
   * 댓글 좋아요 토글 처리
   */
  const handleLikeComment = async (commentId) => {
    try {
      if (!memberId) {
        await fetchMemberData();
      }
      if (!memberId) return;

      const updatedComment = await ForumApi.toggleLikeComment(
        commentId,
        memberId
      );

      setComments((prevComments) =>
        prevComments.map((comment) =>
          comment.id === commentId
            ? {
                ...comment,
                likesCount: updatedComment.totalLikes,
                liked: updatedComment.liked,
              }
            : comment
        )
      );

      toast.success("댓글 좋아요 상태가 변경되었습니다.");
    } catch (error) {
      console.error("댓글 좋아요 오류:", error);
      toast.error("좋아요 처리에 실패했습니다.");
    }
  };

  // post 상태 변화 시 prevPost도 업데이트
  useEffect(() => {
    if (post) {
      setPrevPost(post);
    }
  }, [post]);

  // comments 상태 변화 시 prevComments도 업데이트
  // + 관리자 수정 플래그 세팅
  useEffect(() => {
    if (
      comments.length > 0 &&
      JSON.stringify(prevComments) !== JSON.stringify(comments)
    ) {
      setPrevComments(comments);
      setComments(
        comments.map((comment) => ({
          ...comment,
          editedByAdmin: comment.editedBy === "ADMIN",
        }))
      );
    }
  }, [comments, prevComments]);

  if (loading) return <div>로딩 중...</div>;
  if (!post) return <div>게시글을 찾을 수 없습니다.</div>;

  return (
    <PostDetailContainer>
      {/* 전역 스타일 (blockquote, reply-quote 등) */}
      <ReplyQuoteGlobalStyle />
      <GlobalKeyframes />

      {/* --- 게시글 제목 섹션 --- */}
      <PostTitle>
        {post.hidden ? (
          <HiddenCommentNotice>
            NOTICE: 해당 게시글은 삭제되거나 숨김 처리되었습니다.
          </HiddenCommentNotice>
        ) : (
          <>
            <span>
              {post.title}
              {post.editedByAdminTitle && (
                <AdminEditIndicator>
                  [관리자에 의해 제목 수정됨]
                </AdminEditIndicator>
              )}
            </span>
            {/* 작성자/관리자 Title 수정 로직은 그대로 유지 */}
            {!post.editedByAdminTitle &&
              memberId === post.memberId &&
              !isAdmin && (
                <EditButton
                  onClick={() =>
                    openModal("editPostTitle", post.id, post.title)
                  }
                  aria-label="Edit Title"
                >
                  <FontAwesomeIcon icon={faEdit} />
                </EditButton>
              )}
            {isAdmin &&
              (!post.editedByAdminTitle || memberId !== post.memberId) && (
                <EditButton
                  onClick={() =>
                    openModal("editPostTitle", post.id, post.title)
                  }
                  aria-label="Edit Title"
                >
                  <FontAwesomeIcon icon={faEdit} />
                </EditButton>
              )}
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

      {/* --- 게시글 헤더 (작성자, 날짜, 본문) --- */}
      <PostHeader>
        <AuthorInfo>
          <p>
            <strong>게시자:</strong> {post.authorName}
          </p>
          <p>
            <strong>생성일:</strong> {new Date(post.createdAt).toLocaleString()}
          </p>
        </AuthorInfo>

        <ContentInfo>
          {post.hidden ? (
            <HiddenCommentNotice>
              NOTICE: 해당 게시글은 삭제되거나 숨김 처리되었습니다.
            </HiddenCommentNotice>
          ) : (
            <InlineBlockContainer>
              <span
                dangerouslySetInnerHTML={{
                  __html: post.content,
                }}
              />
              {post.editedByAdminContent && (
                <AdminEditIndicator>
                  [관리자에 의해 내용 수정됨]
                </AdminEditIndicator>
              )}
            </InlineBlockContainer>
          )}

          <ActionButtons>
            <div className="left">
              <report-button
                onClick={() => openModal("reportPost", post.id, post.content)}
                disabled={post.hasReported}
              >
                <FontAwesomeIcon icon={faCircleExclamation} />
                {isAdmin && post.reportCount !== undefined && (
                  <ReportCountText>
                    신고 누적 수: {post.reportCount}
                  </ReportCountText>
                )}
              </report-button>

              {/* 작성자 전용 (게시글 삭제/수정) */}
              {memberId === post.memberId && !isAdmin && (
                <>
                  {!post.editedByAdminContent ? (
                    <>
                      <report-button
                        onClick={() => openModal("deletePost", post.id)}
                      >
                        <FontAwesomeIcon icon={faDeleteLeft} />
                      </report-button>
                      <report-button
                        onClick={() =>
                          openModal("editPostContent", post.id, post.content)
                        }
                      >
                        <FontAwesomeIcon icon={faEdit} />
                      </report-button>
                    </>
                  ) : (
                    <>
                      <disabled-button>
                        <FontAwesomeIcon icon={faDeleteLeft} />
                      </disabled-button>
                      <disabled-button>
                        <FontAwesomeIcon icon={faEdit} />
                      </disabled-button>
                    </>
                  )}
                </>
              )}

              {/* 관리자 전용 (게시글 삭제/수정) */}
              {isAdmin && (
                <>
                  <admin-button
                    onClick={() => openModal("deletePost", post.id)}
                  >
                    <FontAwesomeIcon icon={faDeleteLeft} />
                  </admin-button>
                  <admin-button
                    onClick={() =>
                      openModal("editPostContent", post.id, post.content)
                    }
                  >
                    <FontAwesomeIcon icon={faEdit} />
                  </admin-button>
                </>
              )}
            </div>

            <div className="right">
              <button onClick={handleLike}>
                <FontAwesomeIcon icon={faThumbsUp} /> {post.likesCount}
              </button>
              <button onClick={() => handleReply(post, "post")}>
                <FontAwesomeIcon icon={faReply} />
              </button>
              {isAdmin && post.hidden && (
                <button
                  onClick={() => openModal("restorePost", post.id)}
                  disabled={loading}
                >
                  {loading ? (
                    <FontAwesomeIcon icon={faSpinner} spin />
                  ) : (
                    <>
                      <FontAwesomeIcon icon={faUndo} /> 복원
                    </>
                  )}
                </button>
              )}
            </div>
          </ActionButtons>
        </ContentInfo>
      </PostHeader>

      {/* --- 댓글 섹션 --- */}
      <CommentSection>
        <h2>댓글</h2>
        {comments.map((comment) => (
          <CommentCard key={comment.id} id={`comment-${comment.id}`}>
            <AuthorInfo>
              <p>{comment.authorName}</p>
              <p>{new Date(comment.createdAt).toLocaleString()}</p>
            </AuthorInfo>

            <CommentContent>
              {comment.hidden ? (
                <HiddenCommentNotice>
                  NOTICE: 해당 댓글은 삭제되거나 숨김 처리되었습니다.
                </HiddenCommentNotice>
              ) : (
                <InlineBlockContainer>
                  <div
                    dangerouslySetInnerHTML={{
                      __html: comment.content,
                    }}
                  />
                  {comment.editedByAdmin && (
                    <AdminEditIndicator>
                      [관리자에 의해 댓글 내용 수정]
                    </AdminEditIndicator>
                  )}
                </InlineBlockContainer>
              )}

              <ActionButtons>
                <div className="left">
                  <report-button
                    onClick={() => openModal("reportComment", comment.id, "")}
                    disabled={comment.hasReported}
                  >
                    <FontAwesomeIcon icon={faCircleExclamation} />
                    {isAdmin &&
                      comment.reportCount !== null &&
                      comment.reportCount >= 0 && (
                        <ReportCountText>
                          신고 누적 수: {comment.reportCount}
                        </ReportCountText>
                      )}
                  </report-button>

                  {/* 댓글 작성자 전용 (삭제/수정) */}
                  {memberId === comment.memberId && !isAdmin && (
                    <>
                      {!comment.editedByAdmin ? (
                        <>
                          <report-button
                            onClick={() =>
                              openModal("deleteComment", comment.id)
                            }
                          >
                            <FontAwesomeIcon icon={faDeleteLeft} />
                          </report-button>
                          <report-button
                            onClick={() =>
                              openModal(
                                "editComment",
                                comment.id,
                                comment.content
                              )
                            }
                          >
                            <FontAwesomeIcon icon={faEdit} />
                          </report-button>
                        </>
                      ) : (
                        <>
                          <disabled-button>
                            <FontAwesomeIcon icon={faDeleteLeft} />
                          </disabled-button>
                          <disabled-button>
                            <FontAwesomeIcon icon={faEdit} />
                          </disabled-button>
                        </>
                      )}
                    </>
                  )}

                  {/* 관리자 전용 (삭제/수정) => 작성자와 ID가 다를 때 */}
                  {isAdmin && memberId !== comment.memberId && (
                    <>
                      <admin-button
                        onClick={() => openModal("deleteComment", comment.id)}
                      >
                        <FontAwesomeIcon icon={faDeleteLeft} />
                      </admin-button>
                      <admin-button
                        onClick={() =>
                          openModal("editComment", comment.id, comment.content)
                        }
                      >
                        <FontAwesomeIcon icon={faEdit} />
                      </admin-button>
                    </>
                  )}

                  {/* 관리자 + 작성자 동일 시, 관리자 버튼만 표시 */}
                  {isAdmin && memberId === comment.memberId && (
                    <>
                      <admin-button
                        onClick={() => openModal("deleteComment", comment.id)}
                      >
                        <FontAwesomeIcon icon={faDeleteLeft} />
                      </admin-button>
                      <admin-button
                        onClick={() =>
                          openModal("editComment", comment.id, comment.content)
                        }
                      >
                        <FontAwesomeIcon icon={faEdit} />
                      </admin-button>
                    </>
                  )}
                </div>

                <div className="right">
                  <button onClick={() => handleLikeComment(comment.id)}>
                    <FontAwesomeIcon icon={faThumbsUp} /> {comment.likesCount}
                  </button>
                  <button onClick={() => handleReply(comment, "comment")}>
                    <FontAwesomeIcon icon={faReply} />
                  </button>
                  {isAdmin && comment.hidden && (
                    <button
                      onClick={() => openModal("restoreComment", comment.id)}
                      disabled={loadingCommentId === comment.id}
                    >
                      {loadingCommentId === comment.id ? (
                        <FontAwesomeIcon icon={faSpinner} spin />
                      ) : (
                        <>
                          <FontAwesomeIcon icon={faUndo} /> 복원
                        </>
                      )}
                    </button>
                  )}
                </div>
              </ActionButtons>
            </CommentContent>
          </CommentCard>
        ))}
      </CommentSection>

      <hr />

      {/* --- 댓글 입력 (Tiptap 에디터) 섹션 --- */}
      <CommentInputSection>
        <div className="toolbar">
          <button
            onClick={() => editor.chain().focus().toggleBold().run()}
            className={editor.isActive("bold") ? "active" : ""}
          >
            <strong>B</strong>
          </button>
          <button
            onClick={() => editor.chain().focus().toggleItalic().run()}
            className={editor.isActive("italic") ? "active" : ""}
          >
            <em>I</em>
          </button>
          <button
            onClick={() => editor.chain().focus().toggleUnderline().run()}
            className={editor.isActive("underline") ? "active" : ""}
          >
            <u>U</u>
          </button>
          <button onClick={handleAddLink}>Link</button>
          <button
            onClick={() => editor.chain().focus().unsetLink().run()}
            disabled={!editor.isActive("link")}
          >
            Remove Link
          </button>
        </div>

        {/* Tiptap Editor 컨테이너 */}
        <EditorContent editor={editor} className="editor" />

        {/* 댓글 추가 버튼 */}
        <button onClick={handleAddComment}>댓글 추가</button>
      </CommentInputSection>

      {/* 모달 컴포넌트 */}
      <ConfirmationModal
        isOpen={isModalOpen}
        type={modalData.type}
        content={modalData.content}
        message={"진행 하시겠습니까?"}
        onConfirm={handleModalConfirm}
        onCancel={() => setIsModalOpen(false)}
      />
    </PostDetailContainer>
  );
};

export default PostDetail;
