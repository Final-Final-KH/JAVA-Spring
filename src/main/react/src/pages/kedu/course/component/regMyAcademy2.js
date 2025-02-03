import { useContext, useEffect, useRef, useState } from "react";
import { toast } from "react-toastify"; // react-toastify의 toast 함수를 import 합니다.
import AxiosApi2 from "../../../../api/AxiosApi2";
import { MemberInfoContext } from "../../../../api/provider/MemberInfoContextProvider2";
import { useParams } from "react-router-dom";
import { RegionSearchContext } from "../../../../api/provider/RegionSearchContextProvider2";
import { Button2, Content } from "../style/style2";

const RegMyAcademy = () => {
  const [isMyAcademy, setIsMyAcademy] = useState(false);
  const { member } = useContext(MemberInfoContext);
  const { academyId } = useParams();
  const academyNameRef = useRef("");
  const regionRef = useRef("");
  const { searchKeyword, academyName } = useContext(RegionSearchContext); // RegionSearchContext로 변경

  useEffect(() => {
    academyNameRef.current = localStorage.getItem("academyName");
    regionRef.current = localStorage.getItem("region");
  }, [searchKeyword, academyName]); // searchKeyword, academyName 값이 변경될 때마다 재호출
  useEffect(() => {
    RegAcademyCheck();
  });

  const RegAcademyCheck = async () => {
    const rsp = await AxiosApi2.regAcademyCheck(academyId, member.memberId);
    if (rsp.data === true) {
      setIsMyAcademy(true);
      toast.success("등록 가능한 학원 입니다.");
    } else {
      toast.warn("이미 등록된 학원 입니다.");
    }
  };
  const RegAcademy = async () => {
    console.log("등록된 학원이면 더 이상 추가하지마 ", isMyAcademy);
    // 이미 등록된 학원인 경우 API 요청을 실행하지 않음
    if (!isMyAcademy) {
      toast.warn("이미 등록된 학원입니다.");
      return; // 함수 실행을 중지시킴
    }

    try {
      // Axios 요청
      const rsp = await AxiosApi2.regAcademy(
        member.memberId,
        academyId,
        academyNameRef.current
      );

      if (rsp.data === true) {
        // 요청이 성공하면 성공 메시지 출력
        toast.success("학원이 성공적으로 등록되었습니다!");
      } else {
        // 등록이 실패한 경우
        toast.error("학원 등록에 실패했습니다.");
      }
    } catch (error) {
      // 요청이 실패한 경우 에러 메시지 출력
      toast.error("학원 등록에 실패했습니다.");
      console.error("Error registering academy:", error);
    }
  };
  return (
    <Content>
      <h3>나의 학원등록</h3>
      {!isMyAcademy ? (
        <h2>이미 등록된 학원 입니다.</h2>
      ) : (
        <>
          <div>
            <p>나의 학원 등록하기</p>
            <Button2 onClick={() => RegAcademy()}>
              나의 학원으로 등록하기
            </Button2>
          </div>
        </>
      )}
    </Content>
  );
};
export default RegMyAcademy;
