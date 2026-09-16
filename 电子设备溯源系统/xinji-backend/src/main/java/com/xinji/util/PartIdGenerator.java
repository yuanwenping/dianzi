package com.xinji.util;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.xinji.entity.Part;
import com.xinji.mapper.PartMapper;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

@Component
public class PartIdGenerator {

    private final PartMapper partMapper;

    public PartIdGenerator(PartMapper partMapper) {
        this.partMapper = partMapper;
    }

    /**
     * 生成零件编号: XJ-年月日-四位流水号
     * 例如: XJ-20260630-0001
     */
    public String generate(String manufacturer) {
        String prefix = manufacturer.length() >= 2
                ? manufacturer.substring(0, 2).toUpperCase()
                : "XJ";
        String datePart = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        String todayPrefix = prefix + "-" + datePart + "-";

        // 查询今天已有的最大编号
        LambdaQueryWrapper<Part> wrapper = new LambdaQueryWrapper<>();
        wrapper.likeRight(Part::getPartId, todayPrefix)
               .orderByDesc(Part::getPartId)
               .last("LIMIT 1");

        Part lastPart = partMapper.selectOne(wrapper);
        int seq = 1;
        if (lastPart != null && lastPart.getPartId().length() >= 16) {
            String seqStr = lastPart.getPartId().substring(lastPart.getPartId().length() - 4);
            try {
                seq = Integer.parseInt(seqStr) + 1;
            } catch (NumberFormatException ignored) {}
        }

        return todayPrefix + String.format("%04d", seq);
    }
}
