package com.hotelmanagement.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.*;

@Slf4j
@Service
public class InsightService {

    @Value("${gemini.api.key:}")
    private String geminiApiKey;

    private final RestTemplate restTemplate = new RestTemplate();

    public String getRoomRecommendations(String guestProfile) {
        if (geminiApiKey == null || geminiApiKey.isEmpty() || geminiApiKey.startsWith("your_")) {
            return getMockRecommendations(guestProfile);
        }
        try {
            String prompt = "You are a luxury hotel concierge AI. Based on this guest profile, suggest room upgrades and amenities:\n\n" + guestProfile + "\n\nProvide 3 personalized recommendations in JSON array format with fields: title, description, category (ROOM_UPGRADE/AMENITY/ACTIVITY).";
            return callGeminiApi(prompt);
        } catch (Exception e) {
            log.error("Gemini API call failed, using mock data", e);
            return getMockRecommendations(guestProfile);
        }
    }

    public String getUpsellTips(String guestProfile) {
        if (geminiApiKey == null || geminiApiKey.isEmpty() || geminiApiKey.startsWith("your_")) {
            return getMockUpsellTips();
        }
        try {
            String prompt = "You are a hotel revenue optimization AI. Suggest upsell opportunities for this guest:\n\n" + guestProfile + "\n\nProvide 3 upsell tips in JSON array format with fields: title, description, estimatedRevenue, priority (HIGH/MEDIUM/LOW).";
            return callGeminiApi(prompt);
        } catch (Exception e) {
            log.error("Gemini API call failed, using mock data", e);
            return getMockUpsellTips();
        }
    }

    @SuppressWarnings("unchecked")
    private String callGeminiApi(String prompt) {
        String url = "https://generativelanguage.googleapis.com/v1beta/models/gemini-2.0-flash:generateContent?key=" + geminiApiKey;
        Map<String, Object> body = new HashMap<>();
        List<Map<String, Object>> contents = new ArrayList<>();
        Map<String, Object> content = new HashMap<>();
        List<Map<String, String>> parts = new ArrayList<>();
        parts.add(Map.of("text", prompt));
        content.put("parts", parts);
        contents.add(content);
        body.put("contents", contents);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(body, headers);

        ResponseEntity<Map> response = restTemplate.exchange(url, HttpMethod.POST, entity, Map.class);
        Map<String, Object> responseBody = response.getBody();
        if (responseBody != null && responseBody.containsKey("candidates")) {
            List<Map<String, Object>> candidates = (List<Map<String, Object>>) responseBody.get("candidates");
            if (!candidates.isEmpty()) {
                Map<String, Object> candidate = candidates.get(0);
                Map<String, Object> contentResp = (Map<String, Object>) candidate.get("content");
                List<Map<String, String>> partsResp = (List<Map<String, String>>) contentResp.get("parts");
                return partsResp.get(0).get("text");
            }
        }
        return getMockRecommendations("");
    }

    private String getMockRecommendations(String guestProfile) {
        return "[{\"title\":\"Suite Upgrade\",\"description\":\"Upgrade to our Premium Suite with panoramic city views, separate living area, and complimentary minibar.\",\"category\":\"ROOM_UPGRADE\"},{\"title\":\"Spa & Wellness Package\",\"description\":\"Enjoy a 90-minute couples spa treatment including aromatherapy massage and access to the thermal suite.\",\"category\":\"AMENITY\"},{\"title\":\"Desert Safari Experience\",\"description\":\"Private desert safari with dune bashing, camel ride, and traditional Bedouin dinner under the stars.\",\"category\":\"ACTIVITY\"}]";
    }

    private String getMockUpsellTips() {
        return "[{\"title\":\"Late Checkout Extension\",\"description\":\"Offer late checkout until 4 PM for an additional AED 200. High conversion rate for business travelers.\",\"estimatedRevenue\":200,\"priority\":\"HIGH\"},{\"title\":\"Room Service Breakfast\",\"description\":\"Promote in-room breakfast with premium items for AED 150 per person.\",\"estimatedRevenue\":300,\"priority\":\"MEDIUM\"},{\"title\":\"Airport Transfer\",\"description\":\"Luxury sedan airport transfer service at AED 350. Convenient for international guests.\",\"estimatedRevenue\":350,\"priority\":\"LOW\"}]";
    }
}
