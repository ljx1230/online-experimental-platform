package com.ljx.file.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @Author: ljx
 * @Date: 2023/12/22 16:48
 */
@Schema(name = "树结点VO",required = true)
@Data
public class TreeNodeVo {
    @Schema(description = "节点id")
    private Long id;

    @Schema(description = "节点名")
    private String label;

    @Schema(description = "深度")
    private Long depth;

    @Schema(description = "是否被关闭")
    private String state = "closed";

    @Schema(description = "属性集合")
    private Map<String, String> attributes = new HashMap<>();

    @Schema(description = "子节点列表")
    private List<TreeNodeVo> children = new ArrayList<>();
}
