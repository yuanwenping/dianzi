package com.xinji.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.xinji.dto.PartDTO;
import com.xinji.dto.PartPageDTO;
import com.xinji.entity.Part;

public interface PartService {
    IPage<Part> pageQuery(PartPageDTO dto);
    PartDTO getById(Long id);
    PartDTO getByPartId(String partId);
    Part add(Part part);
    Part update(Part part);
    void delete(Long id);
}
