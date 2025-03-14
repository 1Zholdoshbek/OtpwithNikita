package com.tashiev.otpwithnikita.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.client.RestTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

@Service
public class SmsService {

    @Value("${smspro.api.url}")
    private String apiUrl;

    @Value("${smspro.login}")
    private String login;

    @Value("${smspro.password}")
    private String password;

    @Value("${smspro.sender.id}")
    private String senderId;

    private final RestTemplate restTemplate;

    @Autowired
    public SmsService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public Map<String, String> sendOtp(String phoneNumber, String otpCode, Long otpId) {
        String xml = String.format("""
                <?xml version="1.0" encoding="UTF-8"?>
                <message>
                    <login>%s</login>
                    <pwd>%s</pwd>
                    <id>%d</id>
                    <sender>%s</sender>
                    <text>Код для входа: %s</text>
                    <phones>
                        <phone>%s</phone>
                    </phones>
                </message>
                """, login, password, otpId, senderId, otpCode, phoneNumber);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_XML);
        headers.set(HttpHeaders.CONTENT_ENCODING, StandardCharsets.UTF_8.name());
        headers.set(HttpHeaders.ACCEPT_CHARSET, StandardCharsets.UTF_8.name());

        // Convert string to bytes using UTF-8
        byte[] xmlBytes = xml.getBytes(StandardCharsets.UTF_8);
        HttpEntity<byte[]> request = new HttpEntity<>(xmlBytes, headers);

        String response = restTemplate.postForObject(apiUrl + "/message", request, String.class);

        return parseXmlResponse(response);
    }

    private Map<String, String> parseXmlResponse(String xmlResponse) {
        Map<String, String> result = new HashMap<>();
        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            // Disable external entity processing to prevent XXE attacks
            factory.setFeature("http://apache.org/xml/features/disallow-doctype-decl", true);
            factory.setFeature("http://xml.org/sax/features/external-general-entities", false);
            factory.setFeature("http://xml.org/sax/features/external-parameter-entities", false);

            DocumentBuilder builder = factory.newDocumentBuilder();
            org.w3c.dom.Document doc = builder.parse(
                    new java.io.ByteArrayInputStream(xmlResponse.getBytes(StandardCharsets.UTF_8))
            );

            result.put("status", doc.getElementsByTagName("status").item(0).getTextContent());
        } catch (Exception e) {
            // log.error("Error parsing SMS response", e);
            result.put("status", "error");
            result.put("error", e.getMessage());
        }
        return result;
    }
}