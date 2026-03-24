package com.example.moviemuse.dto;

import java.util.ArrayList;
import java.util.List;

import lombok.Data;

@Data
public class BackfillResultDto {

    private int updated;
    private int skipped;
    private int failed;
    private List<String> messages = new ArrayList<>();
}
