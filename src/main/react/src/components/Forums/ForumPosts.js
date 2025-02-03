import React, { useEffect, useState } from "react";
import { useParams } from "react-router-dom";
import ForumApi from "../../api/ForumApi";
import {
  PostsContainer,
  SectionHeader,
  PostsSection,
  PostCard,
  PostTitle,
  PostDetails,
  PostMeta,
  PostRightSection,
  PostStat,
  StyledLink, // StyledLink 컴포넌트 추가
} from "../../styles/ForumPostsStyles";

const ForumPosts = () => {
  useEffect(() => {
    document.body.style.backgroundColor = "#f5f6f7"; // 페이지 로드 시 배경 색상 설정
  });

  const { categoryId } = useParams(); // URL에서 categoryId 가져오기
  const [posts, setPosts] = useState([]); // 일반 게시글 상태
  const [stickyPosts, setStickyPosts] = useState([]); // 고정 게시글 상태
  const [loading, setLoading] = useState(true); // 로딩 상태 관리
  const [categoryName, setCategoryName] = useState(""); // 카테고리 이름 상태

  /**
   * API 호출: 특정 카테고리의 게시글 및 카테고리 정보 가져오기
   */
  const fetchPosts = async () => {
    try {
      const { data } = await ForumApi.getPostsByCategoryId(categoryId, 0, 10); // 카테고리별 게시글 데이터 요청
      setStickyPosts(data.filter((post) => post.sticky)); // 고정 게시글 필터링
      setPosts(data.filter((post) => !post.sticky)); // 일반 게시글 필터링
      const category = await ForumApi.getCategoryById(categoryId); // 카테고리 정보 요청
      setCategoryName(category.name); // 카테고리 이름 설정
    } catch (error) {
      console.error("게시글 데이터를 가져오는데 실패했습니다.", error); // 에러 로그 출력
    } finally {
      setLoading(false); // 데이터 로드 후 로딩 상태 해제
    }
  };

  useEffect(() => {
    fetchPosts(); // 컴포넌트 로드 시 또는 categoryId 변경 시 데이터 새로고침
  }, [categoryId]);

  if (loading) return <div>로딩 중...</div>; // 데이터 로드 중 표시

  return (
    <PostsContainer>
      <SectionHeader>{categoryName}</SectionHeader>

      {/* 고정 게시글 섹션 */}
      {stickyPosts.length > 0 && (
        <PostsSection>
          <h3>고정 게시글</h3>
          {stickyPosts.map((post) => (
            <StyledLink to={`/forum/post/${post.id}`} key={post.id}>
              <PostCard>
                <div>
                  <PostTitle>{post.title}</PostTitle>
                  <PostMeta>
                    작성일: {new Date(post.createdAt).toLocaleDateString()}
                  </PostMeta>
                  <PostDetails>작성자: {post.authorName}</PostDetails>
                </div>
                <PostRightSection>
                  <PostStat>조회수: {post.viewsCount}</PostStat>
                  <PostStat>좋아요: {post.likesCount}</PostStat>
                  <PostStat>최근 댓글: -</PostStat>
                </PostRightSection>
              </PostCard>
            </StyledLink>
          ))}
        </PostsSection>
      )}

      {/* 일반 게시글 섹션 */}
      {posts.length > 0 ? (
        <PostsSection>
          <h3>일반 게시글</h3>
          {posts.map((post) => (
            <StyledLink to={`/forum/post/${post.id}`} key={post.id}>
              <PostCard>
                <div>
                  <PostTitle>{post.title}</PostTitle>
                  <PostMeta>
                    작성일: {new Date(post.createdAt).toLocaleDateString()}
                  </PostMeta>
                  <PostDetails>작성자: {post.authorName}</PostDetails>
                </div>
                <PostRightSection>
                  <PostStat>조회수: {post.viewsCount}</PostStat>
                  <PostStat>좋아요: {post.likesCount}</PostStat>
                  <PostStat>최근 댓글: -</PostStat>
                </PostRightSection>
              </PostCard>
            </StyledLink>
          ))}
        </PostsSection>
      ) : (
        <p>아직 이 카테고리에는 게시글이 존재하지 않습니다.</p>
      )}
    </PostsContainer>
  );
};

export default ForumPosts;
