-- ============================================================
-- “芯迹” 示例数据：两个完整的零件溯源案例
-- 使用方法：先执行 init.sql，再执行本文件
-- ============================================================

USE xinji;

-- ============================================================
-- 案例一：STM32F407VET6 微控制器芯片
-- 完整生命周期：生产 → 封装 → 分销 → 组装 → 销售
-- ============================================================

INSERT INTO `part` (`part_id`, `part_name`, `part_type`, `batch_number`, `manufacturer`, `production_date`, `specification`, `status`)
VALUES (
    'ST-20260515-0001',
    'STM32F407VET6',
    '芯片',
    'B20260515A',
    '意法半导体',
    '2026-05-15',
    'ARM Cortex-M4 32位微控制器，主频168MHz，512KB Flash，192KB SRAM，LQFP-100封装，工作电压1.8V~3.6V',
    1
);

SET @part1_id = LAST_INSERT_ID();

-- 溯源节点1：生产（晶圆制造）
INSERT INTO `trace_record` (`part_id`, `event_type`, `operator`, `event_time`, `location`, `description`, `remark`)
VALUES (
    @part1_id, 'produce', '意法半导体 Crolles 晶圆厂',
    '2026-05-15 08:30:00',
    '法国 Crolles',
    '12英寸晶圆投片，采用90nm工艺制程。晶圆批次号 WF-20260515-CRL，经光刻、蚀刻、离子注入等工序完成晶圆制造。',
    '晶圆良率 98.7%，符合出厂标准'
);

-- 溯源节点2：封装
INSERT INTO `trace_record` (`part_id`, `event_type`, `operator`, `event_time`, `location`, `description`, `remark`)
VALUES (
    @part1_id, 'pack', '意法半导体 深圳封装基地',
    '2026-05-28 14:00:00',
    '中国广东省深圳市龙岗区',
    '晶圆切割、键合、LQFP-100封装、引脚镀锡。封装后经 AOI 光学检测与 X-Ray 探伤，合格率 99.2%。',
    '封装批次 PKG-20260528-SZ'
);

-- 溯源节点3：分销
INSERT INTO `trace_record` (`part_id`, `event_type`, `operator`, `event_time`, `location`, `description`, `remark`)
VALUES (
    @part1_id, 'distribute', '深圳华强电子供应链有限公司',
    '2026-06-05 10:15:00',
    '中国广东省深圳市福田区华强北',
    '从意法半导体大中华区总代提货，入库华强电子仓库。入库数量 5000 片，经扫码验收无误。',
    '采购单号 PO-20260605-HQ'
);

-- 溯源节点4：组装（SMT贴片）
INSERT INTO `trace_record` (`part_id`, `event_type`, `operator`, `event_time`, `location`, `description`, `remark`)
VALUES (
    @part1_id, 'assemble', '深圳智联电子科技有限公司',
    '2026-06-12 09:00:00',
    '中国广东省深圳市宝安区西乡街道',
    '采用 YAMAHA YSM20R 高速贴片机将芯片焊接至 IoT 主控板 PCB，回流焊温度峰值 245°C，经 SPI + AOI 双检合格。',
    'SMT产线编号 L3，贴装合格率 99.6%'
);

-- 溯源节点5：销售
INSERT INTO `trace_record` (`part_id`, `event_type`, `operator`, `event_time`, `location`, `description`, `remark`)
VALUES (
    @part1_id, 'sale', '芯迹科技（终端客户）',
    '2026-06-20 16:30:00',
    '中国上海市浦东新区张江高科技园区',
    '整机 IoT 主控板出货，随设备交付终端客户——上海某智能家居厂商。整机 SN: XJ-IOT-20260620-0088。',
    '质保期 3 年，附出厂检测报告'
);


-- ============================================================
-- 案例二：RC0603FR-0710KL 贴片电阻
-- 完整生命周期：生产 → 封装 → 分销 → 组装 → 销售
-- ============================================================

INSERT INTO `part` (`part_id`, `part_name`, `part_type`, `batch_number`, `manufacturer`, `production_date`, `specification`, `status`)
VALUES (
    'YX-20260420-0001',
    'RC0603FR-0710KL',
    '电阻',
    'B20260420B',
    '国巨电子（Yageo）',
    '2026-04-20',
    '厚膜贴片电阻，0603封装（1608公制），阻值10kΩ，精度±1%，功率0.1W，温度系数±100ppm/°C，工作温度-55°C~+155°C',
    1
);

SET @part2_id = LAST_INSERT_ID();

-- 溯源节点1：生产
INSERT INTO `trace_record` (`part_id`, `event_type`, `operator`, `event_time`, `location`, `description`, `remark`)
VALUES (
    @part2_id, 'produce', '国巨电子 高雄工厂',
    '2026-04-20 07:00:00',
    '中国台湾省高雄市楠梓区',
    '陶瓷基板印刷厚膜电阻浆料，经高温烧结、激光调阻、端电极镀镍/锡。单卷 5000 颗，卷带包装。',
    '阻值精度 ±0.8%，优于规格书要求'
);

-- 溯源节点2：封装
INSERT INTO `trace_record` (`part_id`, `event_type`, `operator`, `event_time`, `location`, `description`, `remark`)
VALUES (
    @part2_id, 'pack', '国巨电子 高雄包装中心',
    '2026-04-22 11:00:00',
    '中国台湾省高雄市楠梓区',
    '编带包装，每卷 5000 颗，防静电真空铝箔袋密封。外箱标签包含型号、批次号、数量、生产日期及 RoHS 标志。',
    '防静电袋湿度指示卡 < 10%'
);

-- 溯源节点3：分销
INSERT INTO `trace_record` (`part_id`, `event_type`, `operator`, `event_time`, `location`, `description`, `remark`)
VALUES (
    @part2_id, 'distribute', '深圳立创电子元器件商城',
    '2026-05-10 13:45:00',
    '中国广东省深圳市南山区',
    '国巨授权代理商立创商城入库，仓库温湿度管控（22°C / 45%RH），扫码入库，批次可追溯。',
    '入库单 LC-20260510-0032，库存货架 A-12-06'
);

-- 溯源节点4：组装（SMT贴片）
INSERT INTO `trace_record` (`part_id`, `event_type`, `operator`, `event_time`, `location`, `description`, `remark`)
VALUES (
    @part2_id, 'assemble', '深圳智联电子科技有限公司',
    '2026-05-25 08:00:00',
    '中国广东省深圳市宝安区西乡街道',
    '与 STM32F407 同一批次 IoT 主控板 SMT 贴装。0603 电阻经自动贴片机贴装、回流焊焊接，AOI 检测无偏移/立碑/空焊。',
    'SMT产线编号 L3，与案例一同一块 PCB'
);

-- 溯源节点5：销售
INSERT INTO `trace_record` (`part_id`, `event_type`, `operator`, `event_time`, `location`, `description`, `remark`)
VALUES (
    @part2_id, 'sale', '芯迹科技（终端客户）',
    '2026-06-20 16:30:00',
    '中国上海市浦东新区张江高科技园区',
    '随 IoT 主控板整机出货。整机经 48 小时老化测试，功能正常，电阻温升 < 5°C，符合设计要求。',
    '同批次出货 500 台，整机 SN: XJ-IOT-20260620-0001 ~ 0500'
);
