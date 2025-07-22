package com.example.coindesk.controller;

import com.example.coindesk.dto.CoindeskResponseDTO;
import com.example.coindesk.service.CoindeskService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/coindesk")
public class CoindeskController {

    @Autowired
    private CoindeskService coindeskService;

    @GetMapping("/raw")
    public Map<String, Object> fetchRawFromCoindesk() {
        return coindeskService.fetchOriginalData();
    }

    @GetMapping("/converted")
    public CoindeskResponseDTO convertCoindeskData() throws Exception {
        Map<String, Object> rawData = coindeskService.fetchOriginalData();
        return coindeskService.convertData(rawData);
    }
}
