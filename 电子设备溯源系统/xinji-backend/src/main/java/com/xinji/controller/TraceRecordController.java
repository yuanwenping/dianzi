package com.xinji.controller;

import com.xinji.dto.R;
import com.xinji.dto.TraceRecordDTO;
import com.xinji.entity.TraceRecord;
import com.xinji.service.TraceRecordService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/trace-records")
public class TraceRecordController {

    private final TraceRecordService traceRecordService;

    public TraceRecordController(TraceRecordService traceRecordService) {
        this.traceRecordService = traceRecordService;
    }

    /**
     * 查询某零件的所有溯源记录（按时间排序）
     */
    @GetMapping("/part/{partId}")
    public R<List<TraceRecordDTO>> listByPartId(@PathVariable Long partId) {
        return R.ok(traceRecordService.getByPartId(partId));
    }

    /**
     * 添加溯源记录（管理员）
     */
    @PostMapping
    public R<TraceRecord> add(@RequestBody TraceRecord record) {
        return R.ok(traceRecordService.add(record));
    }
}
