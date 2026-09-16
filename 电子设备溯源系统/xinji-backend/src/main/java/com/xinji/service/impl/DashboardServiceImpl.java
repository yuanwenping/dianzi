package com.xinji.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.xinji.dto.DashboardDTO;
import com.xinji.entity.Part;
import com.xinji.entity.TraceRecord;
import com.xinji.mapper.PartMapper;
import com.xinji.mapper.TraceRecordMapper;
import com.xinji.service.DashboardService;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class DashboardServiceImpl implements DashboardService {

    private final PartMapper partMapper;
    private final TraceRecordMapper traceRecordMapper;

    public DashboardServiceImpl(PartMapper partMapper, TraceRecordMapper traceRecordMapper) {
        this.partMapper = partMapper;
        this.traceRecordMapper = traceRecordMapper;
    }

    @Override
    public DashboardDTO getStats() {
        DashboardDTO dto = new DashboardDTO();

        // 零件总数
        dto.setTotalParts(partMapper.selectCount(null));

        // 有效零件数
        LambdaQueryWrapper<Part> activeWrapper = new LambdaQueryWrapper<>();
        activeWrapper.eq(Part::getStatus, 1);
        dto.setActiveParts(partMapper.selectCount(activeWrapper));

        // 溯源记录总数
        dto.setTotalTraceRecords(traceRecordMapper.selectCount(null));

        // 各环节类型分布
        List<TraceRecord> allRecords = traceRecordMapper.selectList(null);
        Map<String, Long> distribution = allRecords.stream()
                .collect(Collectors.groupingBy(TraceRecord::getEventType, Collectors.counting()));

        // 添加中文名
        Map<String, Long> namedDistribution = new HashMap<>();
        namedDistribution.put("生产", distribution.getOrDefault("produce", 0L));
        namedDistribution.put("封装", distribution.getOrDefault("pack", 0L));
        namedDistribution.put("分销", distribution.getOrDefault("distribute", 0L));
        namedDistribution.put("组装", distribution.getOrDefault("assemble", 0L));
        namedDistribution.put("销售", distribution.getOrDefault("sale", 0L));
        dto.setEventTypeDistribution(namedDistribution);

        return dto;
    }
}
