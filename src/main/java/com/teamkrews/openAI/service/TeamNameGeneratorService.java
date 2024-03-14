package com.teamkrews.openAI.service;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Slf4j
@RequiredArgsConstructor
@Service
public class TeamNameGeneratorService {

    @Value("${openai.url.prompt}")
    private String url;

    private final RestTemplate restTemplate;
    private final HttpHeaders httpHeaders;

    public String generateTeamName(String seedWords) {

        String[] seeds = seedWords.split(" ");

        // 시드 단어 최대 5개
        String limitedSeeds = Stream.of(seeds)
                .limit(5)
                .collect(Collectors.joining(" "));

        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("model", "gpt-3.5-turbo");
        requestBody.put("temperature", 0.8);
        requestBody.put("max_tokens", 64);
        requestBody.put("top_p", 1);
        requestBody.put("messages", new Object[] {
                new HashMap<String, String>() {{
                    put("role", "system");
                    put("content", "시드 단어들을 기반으로 한글 팀 이름을 4개 생성해줘.");
                }},
                new HashMap<String, String>() {{
                    put("role", "user");
                    put("content", String.format("시드 단어: %s.", limitedSeeds));
                }}
        });

        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(requestBody, httpHeaders);

        return restTemplate.postForObject(url, entity, String.class);
    }
}
