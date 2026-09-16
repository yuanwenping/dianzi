package com.xinji.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.xinji.dto.PartDTO;
import com.xinji.dto.PartPageDTO;
import com.xinji.dto.R;
import com.xinji.entity.Part;
import com.xinji.service.PartService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/parts")
public class PartController {

    private final PartService partService;

    public PartController(PartService partService) {
        this.partService = partService;
    }

    /**
     * 分页查询零件列表（管理员）
     */
    @GetMapping
    public R<IPage<Part>> list(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer pageSize,
            @RequestParam(required = false) String keyword) {
        PartPageDTO dto = new PartPageDTO();
        dto.setPage(page);
        dto.setPageSize(pageSize);
        dto.setKeyword(keyword);
        return R.ok(partService.pageQuery(dto));
    }

    /**
     * 根据零件编号查询（消费者）
     */
    @GetMapping("/query/{partId}")
    public R<PartDTO> queryByPartId(@PathVariable String partId) {
        PartDTO dto = partService.getByPartId(partId);
        if (dto == null) {
            return R.error(404, "零件不存在");
        }
        return R.ok(dto);
    }

    /**
     * 查询单个零件详情（含溯源记录）
     */
    @GetMapping("/{id}")
    public R<PartDTO> getById(@PathVariable Long id) {
        PartDTO dto = partService.getById(id);
        if (dto == null) {
            return R.error(404, "零件不存在");
        }
        return R.ok(dto);
    }

    /**
     * 新增零件
     */
    @PostMapping
    public R<Part> add(@RequestBody Part part) {
        return R.ok(partService.add(part));
    }

    /**
     * 更新零件
     */
    @PutMapping("/{id}")
    public R<Part> update(@PathVariable Long id, @RequestBody Part part) {
        part.setId(id);
        return R.ok(partService.update(part));
    }

    /**
     * 删除/停用零件
     */
    @DeleteMapping("/{id}")
    public R<Void> delete(@PathVariable Long id) {
        partService.delete(id);
        return R.ok();
    }
}
