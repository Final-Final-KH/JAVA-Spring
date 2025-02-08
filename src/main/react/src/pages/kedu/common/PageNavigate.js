import React from "react";
import ReactPaginate from "react-paginate";
import styled from "styled-components";

const PaginationContainer = styled.div`
  display: flex;
  justify-content: center;
  align-items: center;
  margin-top: 20px;
`;

const PaginationButton = styled.div`
  margin: 0 5px;
  cursor: pointer;
  padding: 10px 15px;
  border: 1px solid #ddd;
  border-radius: 5px;
  background-color: #f8f8f8;
  font-size: 16px;

  &:hover {
    background-color: #e0e0e0;
  }

  &.active {
    background-color: #007bff;
    color: white;
    font-weight: bold;
  }

  &.disabled {
    color: #ccc;
    cursor: not-allowed;
  }
`;

const PageNavigate = ({
  totalItemsCount,
  activePage,
  itemsCountPerPage,
  onChange,
}) => {
  const pageCount = Math.ceil(totalItemsCount / itemsCountPerPage);

  return (
    <PaginationContainer>
      <ReactPaginate
        previousLabel={"Previous"} // 변경된 부분: 텍스트로 이전 버튼 표시
        nextLabel={"Next"} // 변경된 부분: 텍스트로 다음 버튼 표시
        breakLabel={"..."}
        pageCount={pageCount}
        onPageChange={({ selected }) => onChange(selected)} // 0-based index
        marginPagesDisplayed={2}
        pageRangeDisplayed={5}
        containerClassName={"pagination"}
        activeClassName={"active"}
        renderOnZeroPageCount={null}
        pageClassName={"page"}
        previousClassName={"previous"}
        nextClassName={"next"}
        disabledClassName={"disabled"}
        pageLinkClassName={"page-link"}
      />
    </PaginationContainer>
  );
};

export default PageNavigate;
