import React, { useState, useEffect } from "react";
import ForumApi from "../../api/ForumApi";
import { Link } from "react-router-dom";
/* 기존 import + 새 styled 컴포넌트 */
import {
  ForumContainer,
  Section,
  // SectionTitle,  // (이제 사용 안 함)
  ForumCategoryCard,
  CategoryTitle,
  CategoryDescription,
  CategoryMeta,
  SectionHeaderRow,
  ForumHeaderTitle,
  CreateButtonLink,
} from "../../styles/ForumStyles";

/**
 * 포럼 메인 페이지
 *  - 카테고리 목록 표시
 *  - 상단에 "포럼 카테고리" 가운데, "새 글 작성" 버튼 (오른쪽)
 */
const Forum = () => {
  useEffect(() => {
    document.body.style.backgroundColor = "#f5f6f7";
  }, []);

  const [categories, setCategories] = useState([]);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    const fetchData = async () => {
      try {
        const categoryData = await ForumApi.fetchCategories();
        console.log("Fetched Categories:", categoryData);
        setCategories(categoryData);
      } catch (error) {
        console.error("데이터를 가져오는데 실패했습니다:", error);
      } finally {
        setLoading(false);
      }
    };
    fetchData();
  }, []);

  if (loading) return <div>Loading categories...</div>;

  return (
    <ForumContainer>
      <Section>
        {/* Header row: 가운데 제목 + 오른쪽 버튼 */}
        <SectionHeaderRow>
          <ForumHeaderTitle>포럼 카테고리</ForumHeaderTitle>
          <CreateButtonLink to="/forum/create-post">
            새 글 작성
          </CreateButtonLink>
        </SectionHeaderRow>

        {/* 카테고리 목록 */}
        <div className="category-grid">
          {categories.map((category) => (
            <ForumCategoryCard key={category.id}>
              <Link
                to={`/forum/category/${category.id}`}
                state={{ categoryName: category.title }}
                style={{ textDecoration: "none", color: "inherit" }}
              >
                <CategoryTitle>{category.title}</CategoryTitle>
                <CategoryDescription>
                  {category.description}
                </CategoryDescription>
                <CategoryMeta>
                  {category.postCount > 0
                    ? `${category.postCount} 개의 게시글이 존재`
                    : "게시글이 아직 없습니다"}
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
