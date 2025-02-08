from flask import Response, request
from urllib.parse import unquote_plus
import requests
import json

# 네이버 API 정보
CLIENT_ID = 'm1ehrdynlqBavqqPCVab'
CLIENT_SECRET_PWD = 'J3c36ztXGC'


# 네이버 뉴스 API 요청 함수
def get_news(category="IT", query=None, start=1):
    base_url = "https://openapi.naver.com/v1/search/news.json"
    query_text = category if not query else f"{category} {query}"
    params = {
        "query": query_text,
        "display": 10,  # 한 번에 가져올 기사 수
        "start": start,  # 시작 위치
        "sort": "sim"  # 정렬 방식 (유사도 기준)
    }
    headers = {
        "X-Naver-Client-Id": CLIENT_ID,
        "X-Naver-Client-Secret": CLIENT_SECRET_PWD
    }
    response = requests.get(base_url, headers=headers, params=params)
    return response.json()


# 뉴스 API 호출 처리
def index(request):
    category = request.args.get('category', 'IT')
    search_query = request.args.get('search')  # 인코딩된 값
    page = request.args.get('page', 1, type=int)  # 페이지 번호 (기본값 1)

    # URL 디코딩
    if search_query:

        search_query = unquote_plus(search_query)  # 검색어를 디코딩

    # 시작 위치 계산
    start = (page - 1) * 10 + 1
    if start < 1:
        start = 1

    print(f"Category: {category}, Search query: {search_query}, Page: {page}, Start: {start}")
    news = get_news(category, search_query, start)

    if 'items' in news:
        json_data = json.dumps({"items": news['items']}, ensure_ascii=False, indent=4)
        return Response(json_data.encode('utf-8'), content_type='application/json; charset=utf-8')
    else:
        return Response(json.dumps({"error": "Failed to fetch news"}, ensure_ascii=False),
                         content_type='application/json; charset=utf-8')