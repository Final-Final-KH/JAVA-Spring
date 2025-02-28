import time
import json
from flask import Response
from pytrends.request import TrendReq


def get_trend_keyword():
    try:
        pytrends = TrendReq(hl="ko-KR", tz=540, retries=3, backoff_factor=0.1)
        kw_list = ["AI", "인공 지능", "빅데이터", "양자 컴퓨터"]

        time.sleep(1)  # 요청 간격 조절
        pytrends.build_payload(kw_list, cat=0, timeframe="today 12-m", geo="KR")

        trends = pytrends.interest_over_time()

        if trends is not None:
            trends_json = trends.reset_index().to_json(orient="records", force_ascii=False)
            return Response(trends_json, mimetype="application/json")
        else:
            return Response(json.dumps({"error": "No trend data available"}), mimetype="application/json")

    except Exception as e:
        return Response(json.dumps({"error": str(e)}), mimetype="application/json")
