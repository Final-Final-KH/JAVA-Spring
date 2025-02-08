import styled from "styled-components";

// 카드 스타일링
export const Card = styled.div`
  border: 1px solid #ddd;
  border-radius: 8px;
  padding: 20px;
  margin: 10px;
  max-width: 300px;
  box-shadow: 0 4px 8px rgba(0, 0, 0, 0.1);
  background-color: #fff;
`;

export const CardTitle = styled.h3`
  font-size: 18px;
  color: #333;
`;

export const CardContent = styled.p`
  font-size: 14px;
  color: #555;
  margin: 5px 0;
`;
