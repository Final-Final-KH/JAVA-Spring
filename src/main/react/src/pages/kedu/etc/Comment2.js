import React, { useState } from "react";

const commentsData = [
  {
    id: 1,
    content: "이 코멘트는 정말 유용하네요!",
    author: "User1",
    replies: [
      {
        id: 2,
        content: "이 코멘트 정말 도움이 됐어요!",
        author: "User2",
        replies: [
          {
            id: 3,
            content: "저도 그렇게 생각해요!",
            author: "User3",
          },
        ],
      },
      {
        id: 4,
        content: "감사합니다!",
        author: "User4",
        replies: [],
      },
    ],
  },
  // 다른 코멘트들...
];

const CommentSection = () => {
  const [comments, setComments] = useState(commentsData);
  const [newComment, setNewComment] = useState("");
  const [newReply, setNewReply] = useState({});
  const [commentId, setCommentId] = useState(null);

  // 댓글 제출 처리
  const handleCommentSubmit = (e) => {
    e.preventDefault();
    if (!newComment) return;

    const newCommentData = {
      id: comments.length + 1,
      content: newComment,
      author: "User" + (comments.length + 1),
      replies: [],
    };

    setComments([...comments, newCommentData]);
    setNewComment(""); // 댓글 작성 후 입력란 비우기
  };

  // 대댓글 제출 처리
  const handleReplySubmit = (e, commentId) => {
    e.preventDefault();
    const replyContent = newReply[commentId];

    if (!replyContent) return;

    const updatedComments = comments.map((comment) => {
      if (comment.id === commentId) {
        const newReplyData = {
          id: comment.replies.length + 1,
          content: replyContent,
          author: "User" + (comment.replies.length + 1),
        };
        return {
          ...comment,
          replies: [...comment.replies, newReplyData],
        };
      }
      return comment;
    });

    setComments(updatedComments);
    setNewReply({ ...newReply, [commentId]: "" }); // 대댓글 입력란 비우기
  };

  return (
    <div>
      <h2>댓글 섹션</h2>
      {comments.map((comment) => (
        <div key={comment.id} style={{ marginBottom: "20px" }}>
          <div>
            <strong>{comment.author}</strong>: {comment.content}
          </div>

          {/* 댓글에 대한 대댓글 작성 버튼 및 입력란 */}
          <button onClick={() => setCommentId(comment.id)}>
            {commentId === comment.id ? "대댓글 닫기" : "대댓글 작성"}
          </button>

          {commentId === comment.id && (
            <form onSubmit={(e) => handleReplySubmit(e, comment.id)}>
              <input
                type="text"
                value={newReply[comment.id] || ""}
                onChange={(e) =>
                  setNewReply({ ...newReply, [comment.id]: e.target.value })
                }
                placeholder="대댓글을 입력하세요"
              />
              <button type="submit">대댓글 작성</button>
            </form>
          )}

          {/* 대댓글 렌더링 */}
          {comment.replies &&
            comment.replies.map((reply) => (
              <div key={reply.id} style={{ paddingLeft: "20px" }}>
                <strong>{reply.author}</strong>: {reply.content}
              </div>
            ))}
        </div>
      ))}

      {/* 새로운 댓글 작성 폼 */}
      <form onSubmit={handleCommentSubmit}>
        <input
          type="text"
          value={newComment}
          onChange={(e) => setNewComment(e.target.value)}
          placeholder="새로운 댓글을 입력하세요"
        />
        <button type="submit">댓글 작성</button>
      </form>
    </div>
  );
};

export default CommentSection;
