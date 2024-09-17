import sys
import json
from sklearn.preprocessing import StandardScaler
from sklearn.neighbors import NearestNeighbors
from sqlalchemy import create_engine
import pandas as pd

# 從命令行獲取 JSON 輸入
input_wine_json = sys.argv[1]
input_wine = json.loads(input_wine_json)

# 建立 SQLAlchemy 引擎以連接 MySQL
engine = create_engine('mysql+mysqldb://root:88888888@nv-network-mysql-1/nvdb')

# 從 MySQL 中查詢數據，使用 pandas 的 read_sql 函數
query = "SELECT * FROM wine_data"
wine_data = pd.read_sql(query, engine)

# 確定資料集中的特徵欄位
features = ['climate_encoding', 'rating', 'log_ratings_count', 'price',
            'is_red', 'is_white', 'is_rose', 'is_sparkling', 'is_dessert', 'is_fortified']

# 初始化 StandardScaler 進行標準化
scaler = StandardScaler()

# 將數據進行標準化
scaled_features = scaler.fit_transform(wine_data[features])

# 使用 k-NN 模型進行相似度推薦
knn = NearestNeighbors(n_neighbors=5, algorithm='auto')
knn.fit(scaled_features)

# 定義推薦函數，允許部分輸入
def recommend_wine(input_wine):
    # 如果提供了 region，篩選該 region 下的數據，支持部分匹配
    if 'region' in input_wine and input_wine['region'] is not None:
        region_input = input_wine['region'].lower()
        wine_data_filtered = wine_data[wine_data['region'].str.lower().str.contains(region_input, na=False)]
        del input_wine['region']  # 移除 Region，不作為特徵使用
    else:
        wine_data_filtered = wine_data

    # 創建一個空列表來存儲輸入的部分特徵
    input_features = []
    selected_columns = []

    # 遍歷 input_wine 字典，僅保存用戶提供的特徵
    for key, value in input_wine.items():
        if value is not None:  # 只考慮非空值
            input_features.append(value)
            selected_columns.append(key)

    # 動態地只使用選定的特徵
    wine_data_subset = wine_data_filtered[selected_columns].dropna()

    if len(wine_data_subset) == 0:
        print("沒有符合的葡萄酒推薦。")
        return None

    # 將 wine_data_subset 進行標準化
    scaler = StandardScaler()
    scaled_wine_data_subset = scaler.fit_transform(wine_data_subset)

    # 確保輸入的特徵轉換成 DataFrame 以保持一致的列名
    input_df = pd.DataFrame([input_features], columns=selected_columns)

    # 將輸入特徵進行標準化
    scaled_input_wine = scaler.transform(input_df)

    # 重新訓練 k-NN 模型基於提供的部分特徵
    knn_partial = NearestNeighbors(n_neighbors=5, algorithm='auto')
    knn_partial.fit(scaled_wine_data_subset)

    # 找出最近的5款酒
    distances, indices = knn_partial.kneighbors(scaled_input_wine)

    # 提取推薦的酒款
    similar_wines = wine_data_filtered.iloc[indices[0]]

    # 返回並完整列印推薦的酒款
    recommended_wines = similar_wines[['name', 'winery', 'region', 'country', 'rating', 'price', 'type']]

    # 完整輸出推薦結果到log
    # pd.set_option('display.max_rows', None)
    # pd.set_option('display.max_columns', None)
    # pd.set_option('display.expand_frame_repr', False)
    return recommended_wines

# 調用推薦函數
recommended_wines = recommend_wine(input_wine)
if recommended_wines is not None:
    # 將推薦的 DataFrame 轉換為 JSON 格式
    recommended_wines_json = recommended_wines.to_json(orient="records")
    print(recommended_wines_json)
else:
    print(json.dumps({"message": "No recommendations found."}))
