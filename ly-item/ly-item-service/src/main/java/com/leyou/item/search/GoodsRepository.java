package com.leyou.item.search;

import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

/**
 * 为了把数据导入es 搬过来的
 */

public interface GoodsRepository extends ElasticsearchRepository<Goods, Long> {
}
