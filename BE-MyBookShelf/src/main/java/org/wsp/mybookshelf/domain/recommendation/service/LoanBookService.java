package org.wsp.mybookshelf.domain.recommendation.service;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.time.LocalDate;
import java.util.*;

@Service
public class LoanBookService {

    @Value("${library.api.authKey}")
    private String apiKey;

    private final RestTemplate restTemplate;

    // RestTemplate을 생성자 주입을 통해 받음
    public LoanBookService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public List<Map<String, Object>> getPopularBooksByKdc(String kdc, int userAge, int pageSize) throws JSONException {
        String url = UriComponentsBuilder
                .fromHttpUrl("http://data4library.kr/api/loanItemSrch")
                .queryParam("authKey", apiKey)
                .queryParam("startDt", "2020-01-01")
                .queryParam("endDt", LocalDate.now().toString())
                .queryParam("dtl_kdc", kdc)
                .queryParam("pageNo", "1")
                .queryParam("pageSize", pageSize)
                .queryParam("age", userAge)
                .queryParam("format", "json")
                .toUriString();

        String response = restTemplate.getForObject(url, String.class);
        JSONObject json = new JSONObject(response);
        JSONObject res = json.getJSONObject("response");

        if (!res.has("docs")) {
            return List.of(Map.of("message", "검색 결과가 없습니다 (KDC: " + kdc + ")"));
        }

        JSONArray docs = res.getJSONArray("docs");
        List<Map<String, Object>> result = new ArrayList<>();

        for (int i = 0; i < docs.length(); i++) {
            JSONObject doc = docs.getJSONObject(i).getJSONObject("doc");
            Map<String, Object> book = new HashMap<>();
            book.put("ranking", doc.optString("ranking"));
            book.put("bookname", doc.optString("bookname"));
            book.put("authors", doc.optString("authors"));
            book.put("publisher", doc.optString("publisher"));
            book.put("publication_year", doc.optString("publication_year"));
            book.put("isbn13", doc.optString("isbn13"));
            book.put("class_no", doc.optString("class_no"));
            book.put("class_nm", doc.optString("class_nm"));
            book.put("loan_count", doc.optString("loan_count"));
            book.put("image", doc.optString("bookImageURL"));
            book.put("detail_url", doc.optString("bookDtlUrl"));
            result.add(book);
        }

        return result;
    }
}