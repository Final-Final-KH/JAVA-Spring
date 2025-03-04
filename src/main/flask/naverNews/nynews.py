import os
import json
import urllib.request
import urllib.parse  # URL 인코딩
from flask import Flask, request, jsonify
from elasticsearch import Elasticsearch


# NYT 뉴스 검색 API 라우트
def ny_news():
    search_query = request.args.get("search")  # 검색어 가져오기 (없으면 None)
    num_pages = 100  # 최대 100페이지 (한 페이지당 10개 기사, 최대 1000개)

    all_articles = []
    # Elasticsearch 연결 (수정된 부분)
    es = Elasticsearch("http://localhost:9200")

    # NYT API 호출 (IT 뉴스 필터 적용)
    client_secret = "y9tKSafaEQe4E4AmwP3VrzZJKXzOGDqD"
    fq_filter = 'news_desk:("Technology")'  # IT 관련 뉴스 필터

    if search_query:
        for page in range(num_pages):
            url = f"https://api.nytimes.com/svc/search/v2/articlesearch.json?q={urllib.parse.quote(search_query)}&fq={urllib.parse.quote(fq_filter)}&sort=newest&api-key={client_secret}&page={page}"
    else:
        for page in range(num_pages):
            url = f"https://api.nytimes.com/svc/search/v2/articlesearch.json?fq={urllib.parse.quote(fq_filter)}&sort=newest&api-key={client_secret}&page={page}"

    try:
        req = urllib.request.Request(url)
        with urllib.request.urlopen(req) as response:
            rescode = response.getcode()
            if rescode == 200:
                response_body = response.read().decode("utf-8")  # 응답 본문 utf-8 디코딩
                data = json.loads(response_body)  # JSON 문자열을 Python 객체로 변환

                # Elasticsearch에 데이터 저장
                for article in data.get("response", {}).get("docs", []):
                    article_id = article["_id"]  # NYT 데이터의 원래 `_id` 사용
                    article_data = {k: v for k, v in article.items() if k != "_id"}  # `_id` 필드는 제거해서 저장

                    # 기존 데이터 업데이트 (upsert 방식)
                    es.index(index="ny_news", id=article_id, document=article_data)

                return jsonify(data)  # JSON 응답 반환
            else:
                return jsonify({"error": f"API Error: {rescode}"}), rescode
    except Exception as e:
        return jsonify({"error": str(e)}), 500  # 서버 오류 처리


def search_ny_news():
    es = Elasticsearch("http://localhost:9200")
    search_query = request.args.get("search", "it")  # 기본 검색어 'it'
    max_results = 10000  # 최대 10000개 기사 반환

    query = {
        "query": {
            "bool": {
                "should": [
                    {"match": {"headline.main": search_query}},
                    {"match": {"abstract": search_query}},
                    {"match": {"lead_paragraph": search_query}}
                ]
            }
        },
        "sort": [
            {"pub_date": {"order": "desc"}}  # 최신 기사부터 정렬
        ]
    }

    try:
        response = es.search(index="ny_news", body=query, size=max_results)
        hits = response.get("hits", {}).get("hits", [])  # 검색 결과 가져오기
        results = {}
        for hit in hits:
            article_id = hit["_id"]  # `_id` 필드를 기준으로 중복 제거

            if article_id not in results:
                results[article_id] = {
                    "headline": {
                        "main": hit["_source"]["headline"]["main"]
                    },
                    "abstract": hit["_source"].get("abstract", "No abstract available"),
                    "web_url": hit["_source"].get("web_url", "#"),  # URL 필드 수정
                    "pub_date": hit["_source"].get("pub_date", "Unknown Date"),
                    "snippet": hit["_source"].get("snippet", "No summary available")
                }

        # 최종 결과 리스트
        final_results = list(results.values())

        return jsonify(final_results)

    except Exception as e:
        return jsonify({"error": str(e)}), 500

