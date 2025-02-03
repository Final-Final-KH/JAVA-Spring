import styled from "styled-components";

// Forum Layout
export const ForumContainer = styled.div`
  background-color: #f5f6f7;
  padding: 20px;
  max-width: 1200px;
  margin: 0 auto;
  margin-top: 90px; /* Header margin adjustment */
  font-family: "Arial", sans-serif;

  @media (max-width: 768px) {
    padding: 10px;
  }
`;

// Header (optional for debugging purposes)
export const ForumHeader = styled.div`
  background-color: #ffffff;
  padding: 15px 20px;
  margin-bottom: 20px;
  border: 1px solid #ddd;
  border-radius: 8px;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);

  h2 {
    font-size: 24px;
    margin-bottom: 10px;
    color: #333;
  }

  p {
    font-size: 14px;
    color: #555;
  }
`;

// Categories Section
export const Section = styled.div`
  margin-top: 20px;
  background-color: #ffffff;
  padding: 15px;
  border-radius: 8px;
  box-shadow: 0 4px 8px rgba(0, 0, 0, 0.1);

  @media (max-width: 768px) {
    padding: 10px;
  }
`;

export const SectionTitle = styled.h2`
  font-size: 20px;
  font-weight: bold;
  color: #333;
  margin-bottom: 15px;

  @media (max-width: 768px) {
    font-size: 18px;
  }
`;

// Category Card
export const ForumCategoryCard = styled.div`
  background-color: #ffffff;
  border: 1px solid #ddd;
  border-radius: 10px;
  padding: 20px;
  margin-bottom: 15px;
  box-shadow: 0 4px 6px rgba(0, 0, 0, 0.1);
  transition: transform 0.2s ease, box-shadow 0.2s ease;

  &:hover {
    transform: translateY(-3px);
    box-shadow: 0 6px 10px rgba(0, 0, 0, 0.15);
  }

  @media (max-width: 768px) {
    padding: 15px;
  }
`;

export const CategoryTitle = styled.h3`
  font-size: 18px;
  font-weight: bold;
  color: #007bff;
  margin-bottom: 10px;

  @media (max-width: 768px) {
    font-size: 16px;
  }
`;

export const CategoryDescription = styled.p`
  font-size: 14px;
  color: #555;
  margin-bottom: 10px;

  @media (max-width: 768px) {
    font-size: 12px;
  }
`;

export const CategoryMeta = styled.small`
  font-size: 12px;
  color: #777;
  display: block;
  text-align: right;
  margin-top: 10px;

  @media (max-width: 768px) {
    font-size: 11px;
  }
`;
