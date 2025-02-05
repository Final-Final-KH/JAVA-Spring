import { useContext, useEffect, useState } from "react";
import AxiosApi2 from "../../../api/AxiosApi2";
import { RegionSearchContext } from "../../../api/provider/RegionSearchContextProvider2";
import { useNavigate } from "react-router-dom";
import styled from "styled-components";

// 스타일 정의
const Content = styled.div`
  border: 1px solid #ccc;
  padding: 150px 0 0 0;
  border-radius: 8px;
  background-color: rgb(229, 236, 232);
  color: black;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.2);

  @media (max-width: 768px) {
    padding: 100px 0;
  }
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
  grid-template-columns: repeat(4, 1fr);
  gap: 15px;
  padding: 20px;

  @media (max-width: 768px) {
    grid-template-columns: repeat(3, 1fr);
    padding: 10px;
  }

  @media (max-width: 480px) {
    grid-template-columns: repeat(2, 1fr);
  }
`;

const Card = styled.div`
  border: 1px solid #ccc;
  border-radius: 8px;
  padding: 15px;
  background-color: rgb(234, 235, 236);
  text-align: center;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
  margin-bottom: 10px;
  font-size: 14px;
  overflow: hidden;
  cursor: pointer;

  &:hover {
    background-color: rgb(189, 192, 190);
  }

  @media (max-width: 768px) {
    padding: 10px;
    font-size: 12px;
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

  @media (max-width: 768px) {
    font-size: 12px;
    padding: 6px 12px;
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

  const [selectedRegion, setSelectedRegion] = useState(null);
  const [selectedDistrict, setSelectedDistrict] = useState(null);

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
    setSelectedRegion(city); // 지역 선택 시 구를 보여주기 위해 설정

    // 지역 선택 후 해당 위치로 스크롤 내리기
    window.scrollTo({ top: 500, behavior: "smooth" }); // 화면을 아래로 스크롤
  };

  const combine = () => {
    if (gu) {
      setSearchKeyword(city + " " + gu);
      Academy();
    }

    // 구 선택 후 해당 위치로 스크롤 내리기
    window.scrollTo({ top: 1000, behavior: "smooth" }); // 화면을 아래로 스크롤
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

      {/* 지역 선택 섹션 */}
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

      <Divider />

      {/* 구 선택 섹션 (지역을 선택하면 표시) */}
      {selectedRegion && (
        <div>
          <SectionTitle>구 선택</SectionTitle>
          <Container>
            {district.length > 0 ? (
              district.map((item, index) => (
                <div key={index}>
                  <Card
                    onClick={() => {
                      setGu(item.district_name);
                      setSelectedDistrict(item.district_name); // 구 선택 시 학원 섹션 보이기
                      combine();
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
      )}

      <Divider />

      {/* 학원 선택 섹션 (구를 선택하면 표시) */}
      {selectedDistrict && (
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
      )}
    </Content>
  );
};

export default Course;
