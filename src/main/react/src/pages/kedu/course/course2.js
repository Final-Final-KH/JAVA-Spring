import { useContext, useEffect, useState } from "react";
import AxiosApi2 from "../../../api/AxiosApi2";
import { RegionSearchContext } from "../../../api/provider/RegionSearchContextProvider2";
import { useNavigate } from "react-router-dom";
import styled from "styled-components";

// 스타일 정의
const Content = styled.div`
  margin: 100px 100px; /* 양옆 여백 추가 */
  border: 1px solid #ccc;
  padding: 150px;
  border-radius: 8px;
  background-color: rgb(229, 236, 232); /* 어두운 배경 색상 */
  color: black; /* 글자 색을 흰색으로 */
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.2); /* 그림자 추가 */
`;
const SectionTitle = styled.div`
  font-size: 18px;
  font-weight: bold;
  margin: 20px 0 10px 0;
`;

const Divider = styled.div`
  width: 100%;
  height: 1px;
  background-color: #ddd;
  margin: 10px 0;
`;

const Container = styled.div`
  display: grid;
  grid-template-columns: repeat(4, 1fr); /* 4열로 표시 */
  gap: 15px; /* 카드 간 간격 */
  padding: 20px;
`;

const Card = styled.div`
  border: 1px solid #ccc;
  border-radius: 8px;
  padding: 15px; /* 카드 크기를 줄여서 콤팩트하게 */
  background-color: rgb(234, 235, 236);
  text-align: center;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
  margin-bottom: 10px;
  font-size: 14px; /* 텍스트 크기 줄여서 카드 크기에 맞게 */
  overflow: hidden; /* 내용이 넘치지 않도록 */
  cursor: pointer;

  &:hover {
    background-color: rgb(189, 192, 190);
  }
`;

const Button = styled.button`
  padding: 8px 15px;
  background-color: rgb(16, 168, 16);
  color: white;
  border: none;
  border-radius: 5px;
  cursor: pointer;
  font-size: 14px;

  &:hover {
    background-color: #45a049;
  }
`;

const NoRegionMessage = styled.div`
  text-align: center;
  font-size: 16px;
  color: #888;
`;

const Course = () => {
  const [course, setCourse] = useState([]);
  const [academy, setAcademy] = useState([]);
  const [region, setRegion] = useState([]);
  const [district, setDistrict] = useState([]);
  const [academyList, setAcademyList] = useState([]);
  const navigate = useNavigate();
  const [city, setCity] = useState("");
  const [gu, setGu] = useState("");
  const [cityGu, setCityGu] = useState("");
  const { searchKeyword, setSearchKeyword, academyName, setAcademyName } =
    useContext(RegionSearchContext);

  useEffect(() => {
    AcademyList();
    Region();
  }, []);

  useEffect(() => {
    setCityGu(city + " " + gu);
  }, [city, gu]);

  useEffect(() => {
    setSearchKeyword(cityGu);
  }, [cityGu]);

  const CsvInsert = async () => {
    const rsp = await AxiosApi2.csvInsert();
  };

  const AcademyList = async () => {
    const rsp = await AxiosApi2.academy();
    setAcademy(rsp.data.list);
  };

  const Region = async () => {
    const rsp = await AxiosApi2.region();
    setRegion(rsp.data.list);
  };

  const FindDistrict = async (city) => {
    setCity(city);
    const rsp = await AxiosApi2.district(city);
    setDistrict(rsp.data.list);
  };

  const combine = () => {
    if (gu) {
      setSearchKeyword(city + " " + gu);
      Academy();
    }
  };

  const Academy = async () => {
    const rsp = await AxiosApi2.academyList(searchKeyword);
    setAcademyList(rsp.data.list);
  };

  const ToAcademyMain = (ac, id) => {
    setAcademyName(ac);
    navigate(`/lecture/${id}`);
  };

  useEffect(() => {
    localStorage.setItem("academyName", academyName);
    localStorage.setItem("region", searchKeyword);
  }, [academyName, searchKeyword]);

  return (
    <Content>
      <h1>학원을 찾아 주세요</h1>

      {/* Region 섹션 */}
      <div>
        <SectionTitle>지역 선택</SectionTitle>
        <Container>
          {region.map((item, index) => (
            <div key={index}>
              <Card onClick={() => FindDistrict(item.city)}>
                <h3>{item.city}</h3>
              </Card>
            </div>
          ))}
        </Container>
      </div>

      {/* Divider */}
      <Divider />

      {/* District 섹션 */}
      <div>
        <SectionTitle>구 선택</SectionTitle>
        <Container>
          {district.length > 0 ? (
            district.map((item, index) => (
              <div key={index}>
                <Card
                  onClick={() => {
                    combine();
                    setGu(item.district_name);
                  }}
                >
                  <h3>{item.district_name}</h3>
                </Card>
              </div>
            ))
          ) : (
            <NoRegionMessage>
              <p>지역명을 선택해 주세요</p>
            </NoRegionMessage>
          )}
        </Container>
      </div>

      {/* Divider */}
      <Divider />

      {/* AcademyList 섹션 */}
      <div>
        <SectionTitle>학원 선택</SectionTitle>
        <Container>
          {academyList.length > 0 ? (
            academyList.map((academy, index) => (
              <div key={index}>
                <Card>
                  <h3>{academy.academy_name}</h3>
                  <Button
                    onClick={() =>
                      ToAcademyMain(academy.academy_name, academy.academy_id)
                    }
                  >
                    학원 정보 보기
                  </Button>
                </Card>
              </div>
            ))
          ) : (
            <NoRegionMessage>
              <p>지역명을 선택해 주세요</p>
            </NoRegionMessage>
          )}
        </Container>
      </div>
    </Content>
  );
};

export default Course;
