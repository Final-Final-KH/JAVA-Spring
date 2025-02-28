from flask import Flask, request, jsonify
from flask_cors import CORS
import requests
from bs4 import BeautifulSoup
import re
import yfinance as yf
import pandas as pd


def get_KOREA_sise(stock_code1, encText):
    print(stock_code1)
    stock = yf.Ticker(stock_code1)
    stock_info = stock.history(period="1d")
    df = yf.download(f'{stock_code1}.KS', '2024-02-27', '2025-02-27')
    df.to_csv(f"{encText}.csv")

    csv = pd.read_csv(f'{encText}.csv')
    print("csv", csv)

    date = pd.to_datetime(csv.iloc[3:, 0])
    close_stock = pd.to_numeric(csv.iloc[3:, 1], errors='coerce')
    high_stock = pd.to_numeric(csv.iloc[3:, 2], errors='coerce')
    low_stock = pd.to_numeric(csv.iloc[3:, 3], errors='coerce')
    open_stock = pd.to_numeric(csv.iloc[3:, 4], errors='coerce')

    stock_data = pd.DataFrame(
        {'date': date, 'close': close_stock, 'high': high_stock, 'low': low_stock, 'open': open_stock})
    print("stock_data", stock_data)

    stock_data['date'] = stock_data['date'].dt.strftime('%Y-%m-%d')
    return stock_data.to_dict(orient='records')


def get_foreign_stock_price(stock_code1, encText):
    stock = yf.Ticker(stock_code1)
    stock_info = stock.history(period="1d")
    df = yf.download(f'{stock_code1}', '2024-02-19', '2025-02-19')
    df.to_csv(f"{encText}.csv")
    csv = pd.read_csv(f'{encText}.csv')

    date = pd.to_datetime(csv.iloc[3:, 0])
    close_stock = pd.to_numeric(csv.iloc[3:, 1], errors='coerce')
    high_stock = pd.to_numeric(csv.iloc[3:, 2], errors='coerce')
    low_stock = pd.to_numeric(csv.iloc[3:, 3], errors='coerce')
    open_stock = pd.to_numeric(csv.iloc[3:, 4], errors='coerce')

    stock_data = pd.DataFrame(
        {'date': date, 'close': close_stock, 'high': high_stock, 'low': low_stock, 'open': open_stock})

    stock_data['date'] = stock_data['date'].dt.strftime('%Y-%m-%d')
    return stock_data.to_dict(orient='records')


def get_stock():
    encText = request.args.get("company")
    print("왜 안됨 ", encText)
    headers = {
        "User-Agent": "Mozilla/5.0"
    }

    session = requests.Session()
    url = f"https://search.naver.com/search.naver?where=nexearch&query={encText}"

    try:
        responses = session.get(url, headers=headers)
        if responses.status_code != 200:
            return jsonify({"error": "네이버 검색 실패"}), 400

        html = responses.text
        soup = BeautifulSoup(html, 'html.parser')
        codes = soup.select("em.t_nm")

        if not codes:
            return jsonify({"error": "검색 결과 없음"}), 404

        for code in codes:
            for span in code.find_all("span"):
                span.decompose()

            stock_code = re.search(r"[A-Za-z0-9]{1,6}", code.get_text(strip=True))
            print(stock_code)
            if stock_code:
                stock_code1 = stock_code.group()
                print(stock_code1)
                if stock_code1.isdigit():
                    stock_data = get_KOREA_sise(stock_code1, encText)
                else:
                    stock_data = get_foreign_stock_price(stock_code1, encText)

                return jsonify(stock_data)

        return jsonify({"error": "주식 코드 없음"}), 404

    except Exception as e:
        return jsonify({"error": str(e)}), 500


