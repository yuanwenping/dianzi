package com.xinji.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.xinji.dto.TraceRecordDTO;
import com.xinji.entity.TraceRecord;
import com.xinji.mapper.TraceRecordMapper;
import com.xinji.service.TraceRecordService;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class TraceRecordServiceImpl implements TraceRecordService {

    private final TraceRecordMapper traceRecordMapper;

    public TraceRecordServiceImpl(TraceRecordMapper traceRecordMapper) {
        this.traceRecordMapper = traceRecordMapper;
    }

    @Override
    public List<TraceRecordDTO> getByPartId(Long partId) {
        LambdaQueryWrapper<TraceRecord> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(TraceRecord::getPartId, partId)
               .orderByAsc(TraceRecord::getEventTime);
        return traceRecordMapper.selectList(wrapper)
                .stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    public TraceRecord add(TraceRecord record) {
        if (record.getEventTime() == null) {
            record.setEventTime(LocalDateTime.now());
        }
        traceRecordMapper.insert(record);
        return record;
    }

    private TraceRecordDTO toDTO(TraceRecord record) {
        TraceRecordDTO dto = new TraceRecordDTO();
        dto.setId(record.getId());
        dto.setPartId(record.getPartId());
        dto.setEventType(record.getEventType());
        dto.setEventTypeName(getEventTypeName(record.getEventType()));
        dto.setOperator(record.getOperator());
        dto.setEventTime(record.getEventTime());
        dto.setLocation(record.getLocation());
        dto.setDescription(record.getDescription());
        dto.setRemark(record.getRemark());
        dto.setCreatedAt(record.getCreatedAt());
        return dto;
    }

    private String getEventTypeName(String eventType) {
        switch (eventType) {
            case "produce":    return "生产";
            case "pack":       return "封装";
            case "distribute": return "分销";
            case "assemble":   return "组装";
            case "sale":       return "销售";
            default:           return eventType;
        }
    }
}
