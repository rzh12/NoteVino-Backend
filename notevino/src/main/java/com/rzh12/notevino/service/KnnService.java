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

@Service
public class KnnService {

    private static final Logger logger = LoggerFactory.getLogger(KnnService.class);

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private ClimateAnalysisService climateAnalysisService;

    // Generate recommendations using the current user's userId
    public String recommendWinesByCurrentUser(double rating, double price, boolean useRegion, String region) {

        Integer userId = getCurrentUserId();

        // Fetch all wine information uploaded by the userId and calculate the frequency of region and type
        String sql = "SELECT region, type, COUNT(*) AS count " +
                "FROM user_uploaded_wines WHERE user_id = ? AND is_deleted = 0 " +
                "GROUP BY region, type ORDER BY count DESC";
        List<Map<String, Object>> userWines = jdbcTemplate.queryForList(sql, userId);

        if (userWines.isEmpty()) {
            return null;
        }

        // If no region is provided by the frontend, use the most frequently uploaded region
        if (region == null || region.isEmpty()) {
            region = (String) userWines.get(0).get("region");
        }

        // Select the most frequently occurring type
        String type = (String) userWines.get(0).get("type");

        // Call analyzeFavoriteClimateByUserId to retrieve the current user's climate code
        int climateEncoding = climateAnalysisService.analyzeFavoriteClimateByUserId(userId);

        // Pass region, type, and climateEncoding to the recommendation algorithm
        return callPythonKnn(region, type, rating, price, climateEncoding, useRegion);
    }

    private Integer getCurrentUserId() {
        UserDetailDTO currentUser = (UserDetailDTO) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return currentUser.getUserId();
    }

    public String callPythonKnn(String region, String type, double rating, double price, int climateEncoding, boolean useRegion) {
        try {
            Map<String, Object> inputData = new HashMap<>();

            // Determine if the region should be included according to the useRegion parameter
            if (useRegion) {
                inputData.put("region", region);
            }

            inputData.put("rating", rating);
            inputData.put("price", price);
            inputData.put("climate_encoding", climateEncoding);
            inputData.put("log_ratings_count", null);
            inputData.put("is_red", type.equalsIgnoreCase("Red") ? 1 : null);
            inputData.put("is_white", type.equalsIgnoreCase("White") ? 1 : null);
            inputData.put("is_rose", type.equalsIgnoreCase("Rose") ? 1 : null);
            inputData.put("is_sparkling", type.equalsIgnoreCase("Sparkling") ? 1 : null);
            inputData.put("is_dessert", type.equalsIgnoreCase("Dessert") ? 1 : null);
            inputData.put("is_fortified", type.equalsIgnoreCase("Fortified") ? 1 : null);

            ObjectMapper objectMapper = new ObjectMapper();
            String wineRequestJson = objectMapper.writeValueAsString(inputData);

            logger.info("傳遞給 Python 的資料: {}", wineRequestJson);

            // Use docker exec to invoke the knn-mysql-sb.py script inside the Python container, passing a JSON string as a parameter
            String[] cmd = {
                    "docker", "exec", "-i", "nv-network-python-1", "python3", "/usr/src/app/knn-mysql-sb.py", wineRequestJson
            };

            ProcessBuilder processBuilder = new ProcessBuilder(cmd);
            Process process = processBuilder.start();

            // Read the output result of the Python script
            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            StringBuilder result = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                result.append(line);
            }

            process.waitFor();

            logger.info("Python k-NN 結果: {}", result.toString());

            return result.toString();

        } catch (Exception e) {
            logger.error("執行 Python k-NN 發生錯誤", e);
            return null;
        }
    }
}
