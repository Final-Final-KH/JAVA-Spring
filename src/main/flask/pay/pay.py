from flask import Flask, request, jsonify
import numpy as np
from sklearn.model_selection import train_test_split
from sklearn.neighbors import KNeighborsRegressor
from sklearn.linear_model import LinearRegression

app = Flask(__name__)

# 📌 연차(year)와 연봉(pay) 데이터
year = np.array([0,1,2,3,4,5,6,7,8,9,10])
pay = np.array([3588,3734,3980,4298,4613,4854,5270,5457,5821,6194,7039])

# 데이터 분리 & 차원 변환
train_input, test_input, train_target, test_target = train_test_split(year, pay, random_state=42)
train_input = train_input.reshape(-1,1)
test_input = test_input.reshape(-1,1)

# ✅ **모델 학습 (서버 실행 시 한 번만)**
# 📌 k-NN 회귀 모델 (k=3)
knr = KNeighborsRegressor(n_neighbors=3)
knr.fit(train_input, train_target)

# 📌 선형 회귀 모델
lr = LinearRegression()
lr.fit(train_input, train_target)

# 📌 다항 회귀 모델 (2차)
train_poly = np.column_stack((train_input**2, train_input))
test_poly = np.column_stack((test_input**2, test_input))

lr_poly = LinearRegression()
lr_poly.fit(train_poly, train_target)

def predict_salary():
    """입력 연차(year)로 연봉 예측"""
    try:
        year_input = request.args.get('year', type=int)
        if year_input is None:
            return jsonify({"error": "연차(year)를 입력하세요! 예: /predict?year=8"}), 400

        # ✅ **사전 학습된 모델로 예측**
        knn_pred = knr.predict([[year_input]])[0]
        linear_pred = lr.predict([[year_input]])[0]
        poly_pred = lr_poly.predict([[year_input**2, year_input]])[0]

        result = {
            "입력 연차": year_input,
            "k-NN 회귀 예상 연봉": round(knn_pred, 2),
            "선형 회귀 예상 연봉": round(linear_pred, 2),
            "다항 회귀 예상 연봉 (2차)": round(poly_pred, 2)
        }

        return jsonify(result)

    except Exception as e:
        return jsonify({"error": str(e)}), 500
