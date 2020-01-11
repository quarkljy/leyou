package com.leyou.item.search;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

import java.util.Date;
import java.util.Map;
import java.util.Set;

/**
 * 为了把数据导入es 从search微服务 搬过来的。通过feign调用总是超时，无法导入数据，所以，单独把导入数据这个搬到这边来
 */

@Data
@Document(indexName = "goods", type = "docs", shards = 1, replicas = 0)
public class Goods {
    @Id
    private Long id; // spuId
    @Field(type = FieldType.Text, analyzer = "ik_max_word")
    private String all; // 所有需要被搜索的信息, 包含标题,分类甚至品牌
    @Field(type = FieldType.Keyword, index = false)
    private String subTitle;
    private Long brandId;
    private Long cid1;
    private Long cid2;
    private Long cid3;
    private Date createTime;
    private Set<Long> price; // 多个sku价格的集合
    @Field(type = FieldType.Keyword, index = false)
    private String skus; // sku信息的json结构
    private Map<String, Object> specs; // 可搜索的规格参数, key是参数名, 值是参数
}
