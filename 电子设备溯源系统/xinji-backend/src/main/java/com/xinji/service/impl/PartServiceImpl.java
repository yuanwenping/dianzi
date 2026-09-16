package com.xinji.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.xinji.dto.PartDTO;
import com.xinji.dto.PartPageDTO;
import com.xinji.dto.TraceRecordDTO;
import com.xinji.entity.Part;
import com.xinji.entity.TraceRecord;
import com.xinji.mapper.PartMapper;
import com.xinji.mapper.TraceRecordMapper;
import com.xinji.service.PartService;
import com.xinji.util.PartIdGenerator;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class PartServiceImpl implements PartService {

    private final PartMapper partMapper;
    private final TraceRecordMapper traceRecordMapper;
    private final PartIdGenerator idGenerator;

    public PartServiceImpl(PartMapper partMapper,
                           TraceRecordMapper traceRecordMapper,
                           PartIdGenerator idGenerator) {
        this.partMapper = partMapper;
        this.traceRecordMapper = traceRecordMapper;
        this.idGenerator = idGenerator;
    }

    @Override
    public IPage<Part> pageQuery(PartPageDTO dto) {
        Page<Part> page = new Page<>(dto.getPage(), dto.getPageSize());
        LambdaQueryWrapper<Part> wrapper = new LambdaQueryWrapper<>();

        if (StringUtils.hasText(dto.getKeyword())) {
            wrapper.and(w -> w
                    .like(Part::getPartId, dto.getKeyword())
                    .or()
                    .like(Part::getPartName, dto.getKeyword())
                    .or()
                    .like(Part::getBatchNumber, dto.getKeyword())
                    .or()
                    .like(Part::getManufacturer, dto.getKeyword())
            );
        }
        wrapper.orderByDesc(Part::getCreatedAt);
        return partMapper.selectPage(page, wrapper);
    }

    @Override
    public PartDTO getById(Long id) {
        Part part = partMapper.selectById(id);
        if (part == null) return null;
        return toDTO(part);
    }

    @Override
    public PartDTO getByPartId(String partId) {
        LambdaQueryWrapper<Part> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Part::getPartId, partId);
        Part part = partMapper.selectOne(wrapper);
        if (part == null) return null;
        return toDTO(part);
    }

    @Override
    @Transactional
    public Part add(Part part) {
        part.setPartId(idGenerator.generate(part.getManufacturer()));
        part.setStatus(1);
        partMapper.insert(part);
        return part;
    }

    @Override
    public Part update(Part part) {
        partMapper.updateById(part);
        return partMapper.selectById(part.getId());
    }

    @Override
    public void delete(Long id) {
        // 逻辑删除：设置 status = 0
        Part part = partMapper.selectById(id);
        if (part != null) {
            part.setStatus(0);
            partMapper.updateById(part);
        }
    }

    private PartDTO toDTO(Part part) {
        PartDTO dto = new PartDTO();
        dto.setId(part.getId());
        dto.setPartId(part.getPartId());
        dto.setPartName(part.getPartName());
        dto.setPartType(part.getPartType());
        dto.setBatchNumber(part.getBatchNumber());
        dto.setManufacturer(part.getManufacturer());
        dto.setProductionDate(part.getProductionDate());
        dto.setSpecification(part.getSpecification());
        dto.setStatus(part.getStatus());

        // 获取溯源记录
        LambdaQueryWrapper<TraceRecord> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(TraceRecord::getPartId, part.getId())
               .orderByAsc(TraceRecord::getEventTime);
        List<TraceRecord> records = traceRecordMapper.selectList(wrapper);
        dto.setTraceRecords(records.stream().map(this::toTraceDTO).collect(Collectors.toList()));

        return dto;
    }

    private TraceRecordDTO toTraceDTO(TraceRecord record) {
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
