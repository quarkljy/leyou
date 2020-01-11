package com.leyou.item.search;

import com.leyou.common.vo.PageResult;
import com.leyou.item.pojo.Spu;
import com.leyou.item.service.GoodsService;
import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.elasticsearch.core.ElasticsearchTemplate;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

/**
 * 导入索引库专用的
 */
@Slf4j
@RestController
public class loadDataController {

    @Autowired
    private GoodsRepository goodsRepository;

    @Autowired
    private ElasticsearchTemplate template;

    @Autowired
    private GoodsService goodsClient;

    @Autowired
    private SearchService searchService;
//    @GetMapping("/createIndex")
//    public void createIndex(){
//        try {
//            template.createIndex(Goods.class);
//
//            template.putMapping(Goods.class);
//            log.info("创建索引成功");
//        } catch (Exception e) {
//            log.info("创建索引失败：{}",e);
//
//        }
//
//    }

    @GetMapping("/loadData")
    public void loadData() {
        try {
            template.createIndex(Goods.class);

            template.putMapping(Goods.class);
            log.info("创建索引成功");
        } catch (Exception e) {
            log.info("创建索引失败：{}",e);

        }
        int page = 1;
        int rows = 100;
        int size = 0;
        do {
            // 查询spu信息
            PageResult<Spu> result = goodsClient.querySpuPage(page, rows, true, null);
            List<Spu> spuList = result.getItems();
            if (CollectionUtils.isEmpty(spuList)) {
                break;
            }
            List<Goods> goodsList = new ArrayList<>();
            for (Spu spu : spuList) {
                // 构建成goods
                Goods goods = searchService.buildGoods(spu);
                goodsList.add(goods);
            }
            // 存入索引库
            goodsRepository.saveAll(goodsList);
            log.info("-------------导入数据成功--------");
            // 翻页
            page++;

            size = spuList.size();
        } while (size == 100);

    }
}
