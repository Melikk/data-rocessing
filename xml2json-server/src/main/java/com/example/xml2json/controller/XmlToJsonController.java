package com.example.xml2json.controller;

import com.example.xml2json.service.FileService;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping
@RequiredArgsConstructor
public class XmlToJsonController {

    private static final String DATE_FORMAT = "yyyy-MM-dd";
    private static final String LOG_DIRECTORY = "logs";

    private final XmlMapper xmlMapper;
    private final ObjectMapper jsonMapper;
    private final SimpleDateFormat dateFormat = new SimpleDateFormat(DATE_FORMAT);
    private final FileService fileService;

    @PostMapping(value = "/", consumes = MediaType.APPLICATION_XML_VALUE)
    public String convertXmlToJson(@RequestBody String xml) throws IOException {
        Map<String, Object> root = new HashMap<>();
        root.put("Data", xmlMapper.readValue(xml, Map.class));
        String json = jsonMapper.writeValueAsString(root);
        String type = (String) ((Map<String, Object>) root.get("Data")).get("Type");
        String fileName = type + "-" + dateFormat.format(new Date()) + ".log";

        fileService.updateLogFile(LOG_DIRECTORY, fileName, json);
        return json;
    }
}
