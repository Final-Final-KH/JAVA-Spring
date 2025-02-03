import React, { useState, useEffect } from "react";
import ForumApi from "../../api/ForumApi"; // ForumApi 모듈 가져오기
import { Link } from "react-router-dom";
import {
  ForumContainer,
  Section,
  SectionTitle,
  ForumCategoryCard,
  CategoryTitle,
  CategoryDescription,
  CategoryMeta,
} from "../../styles/ForumStyles"; // 스타일 컴포넌트 가져오기

const Forum = () => {
  useEffect(() => {
    // 페이지 로드 시 body의 배경 색상을 설정
    document.body.style.backgroundColor = "#f5f6f7";
  });
  const [categories, setCategories] = useState([]); // 포럼 카테고리 상태
  const [loading, setLoading] = useState(true); // 로딩 상태 관리

  useEffect(() => {
    const fetchData = async () => {
      try {
        // 모든 카테고리 데이터 요청
        const categoryData = await ForumApi.fetchCategories();
        console.log("Fetched Categories:", categoryData); // 디버그 로그 출력
        setCategories(categoryData); // 카테고리 상태 업데이트
      } catch (error) {
        console.error("데이터를 가져오는데 실패했습니다:", error); // 에러 로그 출력
      } finally {
        setLoading(false); // 로딩 상태 해제
      }
    };

    fetchData(); // 데이터 요청 실행
  }, []);

  if (loading) return <div>Loading categories...</div>; // 로딩 중일 경우 메시지 표시

  return (
    <ForumContainer>
      <Section>
        <SectionTitle>포럼 카테고리</SectionTitle> {/* 섹션 제목 */}
        <div className="category-grid">
          {categories.map((category) => (
            <ForumCategoryCard key={category.id}>
              {/* 카테고리 클릭 시 해당 카테고리 페이지로 이동 */}
              <Link
                to={`/forum/category/${category.id}`}
                state={{ categoryName: category.title }} // Pass category title as state
                style={{ textDecoration: "none", color: "inherit" }}
              >
                <CategoryTitle>{category.title}</CategoryTitle>{" "}
                {/* 카테고리 제목 */}
                <CategoryDescription>
                  {category.description}
                </CategoryDescription>{" "}
                {/* 카테고리 설명 */}
                <CategoryMeta>
                  {/* 카테고리 포스트 개수 표시 */}
                  {category.posts?.length > 0
                    ? `${category.posts.length} posts available`
                    : "No posts yet"}
                </CategoryMeta>
              </Link>
            </ForumCategoryCard>
          ))}
        </div>
      </Section>
    </ForumContainer>
  );
};

export default Forum;
