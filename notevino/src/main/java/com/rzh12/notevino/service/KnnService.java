package com.rzh12.notevino.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.rzh12.notevino.dto.UserDetailDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class KnnService {

    private static final Logger logger = LoggerFactory.getLogger(KnnService.class);

    @Autowired
    private JdbcTemplate jdbcTemplate;

    public String recommendWinesByWineId(Long wineId, double rating, double price) {
        // 查詢 wineId 的數據
        String sql = "SELECT region, type FROM user_uploaded_wines WHERE wine_id = ?";
        Map<String, Object> wineInfo = jdbcTemplate.queryForMap(sql, wineId);

        // 獲取 region 和 type
        String region = (String) wineInfo.get("region");
        String type = (String) wineInfo.get("type");

        // 呼叫 k-NN 推薦系統，並傳遞 rating 和 price
        return callPythonKnn(region, type, rating, price);
    }

    // 根據當前用戶 userId 進行推薦
    public String recommendWinesByCurrentUser(double rating, double price) {
        // 獲取當前用戶ID
        Integer userId = getCurrentUserId();

        // 查詢 userId 上傳的所有葡萄酒資料，統計 region 和 type 出現次數
        String sql = "SELECT region, type, COUNT(*) AS count " +
                "FROM user_uploaded_wines WHERE user_id = ? AND is_deleted = 0 " +
                "GROUP BY region, type ORDER BY count DESC";
        List<Map<String, Object>> userWines = jdbcTemplate.queryForList(sql, userId);

        if (userWines.isEmpty()) {
            return null;  // 如果該使用者沒有上傳任何葡萄酒
        }

        // 選擇出現次數最多的 region 和 type
        String region = (String) userWines.get(0).get("region");
        String type = (String) userWines.get(0).get("type");

        // 將 region 和 type 傳遞給推薦算法，並使用使用者輸入的 rating 和 price
        return callPythonKnn(region, type, rating, price);
    }

    // 從 SecurityContext 中獲取當前用戶ID
    private Integer getCurrentUserId() {
        UserDetailDTO currentUser = (UserDetailDTO) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return currentUser.getUserId();
    }

    public String callPythonKnn(String region, String type, double rating, double price) {
        try {
            // 建立符合 k-NN 需求的輸入資料
            Map<String, Object> inputData = new HashMap<>();
            inputData.put("region", region);
            inputData.put("rating", rating);
            inputData.put("price", price);
            inputData.put("climate_encoding", null);
            inputData.put("log_ratings_count", null);
            inputData.put("is_red", type.equalsIgnoreCase("Red") ? 1 : null);
            inputData.put("is_white", type.equalsIgnoreCase("White") ? 1 : null);
            inputData.put("is_rose", type.equalsIgnoreCase("Rose") ? 1 : null);
            inputData.put("is_sparkling", type.equalsIgnoreCase("Sparkling") ? 1 : null);
            inputData.put("is_dessert", type.equalsIgnoreCase("Dessert") ? 1 : null);
            inputData.put("is_fortified", type.equalsIgnoreCase("Fortified") ? 1 : null);

            // 將 inputData 轉換為 JSON 字串
            ObjectMapper objectMapper = new ObjectMapper();
            String wineRequestJson = objectMapper.writeValueAsString(inputData);

            // 日誌記錄傳遞給 Python 的資料
            logger.info("傳遞給 Python 的資料: {}", wineRequestJson);

            // 使用 docker exec 調用 Python 容器內的 knn-mysql-sb.py 腳本，傳遞 JSON 字串作為參數
            String[] cmd = {
                    "docker", "exec", "-i", "nv-network-python-1", "python3", "/usr/src/app/knn-mysql-sb.py", wineRequestJson
            };

            // 建立 ProcessBuilder 來執行命令
            ProcessBuilder processBuilder = new ProcessBuilder(cmd);
            Process process = processBuilder.start();

            // 讀取 Python 腳本的輸出結果
            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            StringBuilder result = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                result.append(line);
            }

            // 等待進程結束
            process.waitFor();

            // 日誌記錄 Python k-NN 的結果
            logger.info("Python k-NN 結果: {}", result.toString());

            // 返回 Python 的推薦結果
            return result.toString();

        } catch (Exception e) {
            logger.error("執行 Python k-NN 發生錯誤", e);
            return null;
        }
    }
}
