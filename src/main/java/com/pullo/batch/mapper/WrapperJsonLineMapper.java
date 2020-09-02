package com.pullo.batch.mapper;

import com.pullo.batch.model.PoetryDTO;
import org.springframework.batch.item.file.LineMapper;
import org.springframework.batch.item.file.mapping.JsonLineMapper;

import java.util.Map;

public class WrapperJsonLineMapper implements LineMapper<PoetryDTO> {
    private JsonLineMapper jsonLineMapper;

    public WrapperJsonLineMapper(JsonLineMapper jsonLineMapper) {
        this.jsonLineMapper = jsonLineMapper;
    }

    @Override
    public PoetryDTO mapLine(String s, int i) throws Exception {
        PoetryDTO poetryDTO = new PoetryDTO();
        Map<String, Object> userMap = jsonLineMapper.mapLine(s, i);
        poetryDTO.setAuthor((String) userMap.get("author"));
        poetryDTO.setTitle((String) userMap.get("title"));
        poetryDTO.setText((String) userMap.get("paragraphs"));
        return poetryDTO;
    }
}
