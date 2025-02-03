import styled from "styled-components";

export const Content = styled.div`
  border: 1px solid #ccc;
  padding: 50px;
  border-radius: 8px;
  background-color: rgb(229, 236, 232); /* 어두운 배경 색상 */
  color: black; /* 글자 색을 흰색으로 */
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.2); /* 그림자 추가 */
`;
// Button 스타일
export const Button2 = styled.button`
  padding: 10px 20px;
  background-color: #4caf50;
  color: white;
  border: none;
  border-radius: 5px;
  cursor: pointer;
  font-size: 16px;
  margin-bottom: 15px;
  transition: background-color 0.3s;

  &:hover {
    background-color: #45a049; /* hover 시 색상 변경 */
  }
`;

export const Button3 = styled.button`
  padding: 10px 20px;
  background-color: rgb(230, 210, 39);
  color: black;
  border: none;
  border-radius: 5px;
  cursor: pointer;
  font-size: 16px;
  margin-bottom: 15px;
  transition: background-color 0.3s;

  &:hover {
    background-color: rgb(238, 235, 63); /* hover 시 색상 변경 */
  }
`;

// Select 스타일
export const Select2 = styled.select`
  width: 100%;
  padding: 8px;
  margin-bottom: 15px;
  border-radius: 5px;
  border: 1px solid #ccc;
`;

// Input 스타일
export const Input2 = styled.input`
  width: 100%;
  padding: 8px;
  margin-bottom: 15px;
  border-radius: 5px;
  border: 1px solid #ccc;
  font-size: 14px;
`;

// Cancel Button 스타일
export const CancelButton2 = styled.button`
  padding: 10px 20px;
  background-color: #ccc;
  color: #333;
  border: none;
  border-radius: 5px;
  cursor: pointer;
  font-size: 16px;
  margin-right: 10px;

  &:hover {
    background-color: #b3b3b3; /* hover 시 색상 변경 */
  }
`;

// 테이블 스타일
export const Table2 = styled.table`
  width: 100%;
  border-collapse: collapse;
  margin-top: 20px;
`;

// 테이블 헤더 스타일
export const TableHeader2 = styled.th`
  padding: 12px 15px;
  background-color: #4caf50;
  color: white;
  text-align: left;
  font-size: 16px;
  border: 1px solid #ddd;
`;

// 테이블 데이터 스타일
export const TableData2 = styled.td`
  padding: 12px 15px;
  border: 1px solid #ddd;
  text-align: left;
  font-size: 14px;
`;

// 테이블 행 스타일 (홀수번째와 짝수번째 행의 배경색을 다르게 설정)
export const TableRow2 = styled.tr`
  &:nth-child(even) {
    background-color: #f2f2f2;
  }

  &:hover {
    background-color: #e9e9e9; /* hover 시 색상 변경 */
  }
`;

// 테이블 컨테이너 스타일 (예시로 테이블을 감싸는 div 스타일링)
export const TableContainer2 = styled.div`
  max-width: 1200px;
  margin: 0 auto;
  overflow-x: auto;
`;

export const PaginationContainer = styled.div`
  display: flex;
  justify-content: center;
  margin-bottom: 40px;
`;

export const PageButton = styled.button`
  border: 1px solid #ddd;
  padding: 5px;
  width: 28px;
  margin: 0 5px;
  background-color: #f0f0f0;
  cursor: pointer;
  border-radius: 50%;
  transition: background-color 0.3s;

  &:hover {
    background-color: darkgray;
  }

  &:focus {
    outline: none;
    background-color: royalblue;
  }
`;
