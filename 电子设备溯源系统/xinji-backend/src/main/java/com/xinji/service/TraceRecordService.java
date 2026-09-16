package com.xinji.service;

import com.xinji.dto.TraceRecordDTO;
import com.xinji.entity.TraceRecord;

import java.util.List;

public interface TraceRecordService {
    List<TraceRecordDTO> getByPartId(Long partId);
    TraceRecord add(TraceRecord record);
}
