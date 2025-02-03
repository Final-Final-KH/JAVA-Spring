//  // 기존 직접 호출 하던 함수들. 추후 제거?
//   /**
//    * 게시글 제목 수정 처리
//    */
//   const handleEditTitle = async () => {
//     const updatedTitle = prompt("Enter new post title:", post.title);
//     if (!updatedTitle) return;

//     try {
//       const updatedPost = await ForumApi.updatePostTitle(
//         post.id,
//         { title: updatedTitle },
//         memberId,
//         isAdmin
//       );

//       setPost((prevPost) => ({
//         ...prevPost,
//         title: updatedPost.title,
//         editedByAdminTitle: updatedPost.editedByTitle === "ADMIN",
//       }));

//       toast.success("게시글 제목이 수정되었습니다."); // 성공 알림
//     } catch (error) {
//       console.error("게시글 제목 수정 중 오류 발생:", error);
//       toast.error("게시글 제목 수정에 실패했습니다."); // 오류 알림
//     }
//   };

//   /**
//    * 게시글 내용 수정 처리
//    * - 게시글 내용을 업데이트하고 상태를 동기화합니다.
//    * - 관리자는 수정 후 `editedByAdmin` 플래그를 활성화하고 잠금 상태를 유지합니다.
//    * - 작성자는 수정할 수 있지만 관리자에 의해 수정된 경우 수정이 비활성화됩니다.
//    */
//   const handleEditPost = async () => {
//     const updatedContent = prompt("Enter new post content:", post.content);
//     if (!updatedContent) return toast.warning("내용을 입력해 주세요."); // 경고 메시지

//     try {
//       const updatedPost = await ForumApi.updatePostContent(
//         post.id,
//         { content: updatedContent },
//         memberId,
//         isAdmin
//       );

//       setPost((prevPost) => ({
//         ...prevPost,
//         content: updatedPost.content,
//         editedByAdminContent: updatedPost.editedByContent === "ADMIN",
//       }));

//       toast.success("게시글 내용이 성공적으로 수정되었습니다."); // 성공 메시지
//     } catch (error) {
//       console.error("게시글 내용 수정 중 오류 발생:", error);
//       toast.error("게시글 내용 수정에 실패했습니다."); // 오류 메시지
//     }
//   };

//   /**
//    * 댓글 수정 처리
//    */
//   const handleEditComment = async (commentId) => {
//     const updatedContent = prompt("새 댓글 내용을 입력하세요:");
//     if (!updatedContent) return toast.warning("댓글 내용을 입력해 주세요."); // 경고 메시지

//     try {
//       const updatedComment = await ForumApi.editComment(
//         commentId,
//         {
//           newContent: updatedContent,
//         },
//         memberId
//       );
//       setComments((prevComments) =>
//         prevComments.map((comment) =>
//           comment.id === commentId ? updatedComment : comment
//         )
//       );

//       toast.success("댓글이 성공적으로 수정되었습니다."); // 성공 메시지
//     } catch (error) {
//       console.error("댓글 수정 중 오류 발생:", error);
//       toast.error("댓글 수정에 실패했습니다."); // 오류 메시지
//     }
//   };

//   /**
//    * 게시글 삭제 처리
//    */
//   const handleDeletePost = async () => {
//     if (window.confirm("게시글을 삭제하시겠습니까?")) {
//       try {
//         const removedBy =
//           memberId === post.memberId ? post.authorName : "ADMIN";
//         await ForumApi.deletePost(post.id, memberId, removedBy);
//         toast.success("게시글이 삭제되었습니다."); // 성공 메시지
//         navigate("/forum"); // 포럼 페이지로 이동
//       } catch (error) {
//         console.error("게시글 삭제 중 오류 발생:", error);
//         toast.error("게시글 삭제에 실패했습니다."); // 오류 메시지
//       }
//     }
//   };

//   /**
//    * 댓글 삭제 처리 함수
//    * - 사용자에게 삭제 여부를 확인한 후 백엔드 API를 호출하여 댓글 삭제 요청을 보냄.
//    * - 삭제된 댓글은 `[Removed]` 상태로 업데이트되어 UI에 반영.
//    * - 관리자와 댓글 작성자만 댓글을 삭제할 수 있음.
//    * - 비로그인 상태에서는 로그인 페이지로 리다이렉트.
//    */
//   const handleDeleteComment = async (commentId) => {
//     if (window.confirm("댓글을 삭제하시겠습니까?")) {
//       try {
//         if (!memberId) {
//           await fetchMemberData(); // 사용자 정보 가져오기
//         }
//         if (!memberId) return; // 로그인 실패 시 중단

//         await ForumApi.deleteComment(commentId, memberId);

//         setComments((prevComments) =>
//           prevComments.map((comment) =>
//             comment.id === commentId
//               ? { ...comment, content: "[Removed]", hidden: true }
//               : comment
//           )
//         );

//         toast.success("댓글이 삭제되었습니다."); // 성공 메시지
//       } catch (error) {
//         console.error("댓글 삭제 중 오류 발생:", error);
//         toast.error("댓글 삭제에 실패했습니다."); // 오류 메시지
//       }
//     }
//   };

//   /**
//      * 게시글 신고 처리
//      */
//     const handleReportPost = async (postId, reporterId) => {
//       const reason = prompt("게시글 신고 사유를 입력하세요:");
//       if (!reason) return toast.warning("신고 사유를 입력해 주세요."); // 경고 메시지

//       try {
//         await ForumApi.reportPost(postId, reporterId, reason);
//         toast.success("게시글이 성공적으로 신고되었습니다."); // 성공 메시지
//       } catch (error) {
//         console.error("게시글 신고 처리 중 오류 발생:", error);
//         toast.error("신고 처리에 실패했습니다."); // 오류 메시지
//       }
//     };

//     /**
//      * 댓글 신고 처리
//      */
//     const handleReportComment = async (commentId, reporterId) => {
//       const reason = prompt("댓글 신고 사유를 입력하세요:");
//       if (!reason) return toast.warning("신고 사유를 입력해 주세요."); // 경고 메시지

//       try {
//         await ForumApi.reportComment(commentId, reporterId, reason);
//         toast.success("댓글이 성공적으로 신고되었습니다."); // 성공 메시지
//       } catch (error) {
//         console.error("댓글 신고 처리 중 오류 발생:", error);
//         toast.error("신고 처리에 실패했습니다."); // 오류 메시지
//       }
//     };

//     /**
//      * 게시글 복원 처리
//      * @param {number} postId - 복원할 게시글 ID
//      */
//     const handleRestorePost = async (postId) => {
//       if (!isAdmin) {
//         return toast.error("권한이 없습니다. 관리자만 복원할 수 있습니다."); // 권한 부족 메시지
//       }

//       setLoading(true);
//       try {
//         const restoredPost = await ForumApi.restorePost(postId, memberId);

//         setPost(restoredPost);

//         toast.success("게시글이 성공적으로 복원되었습니다."); // 성공 메시지
//       } catch (error) {
//         console.error("게시글 복원 중 오류 발생:", error);
//         toast.error("복원 처리에 실패했습니다."); // 오류 메시지
//       } finally {
//         setLoading(false);
//       }
//     };

//     // 댓글 데이터 업데이트를 위한 API 호출 함수
//     const fetchUpdatedComments = async () => {
//       try {
//         // 서버에서 최신 댓글 데이터를 가져옵니다.
//         const updatedComments = await ForumApi.getCommentsByPostId(postId);

//         // 가져온 데이터를 상태(state)에 반영하여 UI와 동기화합니다.
//         setComments(updatedComments);
//       } catch (error) {
//         console.error("댓글 데이터를 가져오는 중 오류 발생:", error); // 오류 로그 출력
//       }
//     };

//     /**
//      * 댓글 복원 처리
//      * @param {number} commentId - 복원할 댓글의 ID
//      */
//     const handleRestoreComment = async (commentId) => {
//       // 로딩 상태를 설정하여 UI에서 로딩 스피너를 표시합니다.
//       setLoadingCommentId(commentId);

//       try {
//         console.log("복원 시도 중인 댓글 ID:", commentId);

//         // 댓글 복원 API를 호출합니다.
//         await ForumApi.restoreComment(commentId, memberId);

//         // API 호출 후 댓글 데이터를 다시 가져와 UI와 동기화합니다.
//         await fetchUpdatedComments();

//         toast.success("댓글이 성공적으로 복원되었습니다."); // 성공 메시지
//       } catch (error) {
//         console.error("댓글 복원 중 오류 발생:", error); // 에러 로그 출력

//         // 복원 실패 시 사용자에게 알림 메시지를 표시합니다.
//         toast.error(
//           error.response?.data?.message || "댓글 복원 중 오류가 발생했습니다."
//         );
//       } finally {
//         // 로딩 상태를 해제하여 스피너를 숨깁니다.
//         setLoadingCommentId(null);
//       }
//     };
