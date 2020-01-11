package com.leyou.item.search;

import com.fasterxml.jackson.core.type.TypeReference;
import com.leyou.common.enums.ExceptionEnum;
import com.leyou.common.exception.LyException;
import com.leyou.common.utils.JsonUtils;
import com.leyou.common.utils.NumberUtils;
import com.leyou.item.pojo.*;
import com.leyou.item.service.BrandService;
import com.leyou.item.service.CategoryService;
import com.leyou.item.service.GoodsService;
import com.leyou.item.service.SpecificationService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.*;

@Slf4j
@Service
public class SearchService {
    @Autowired
    private CategoryService categoryClient;

    @Autowired
    private BrandService brandClient;

    @Autowired
    private GoodsService goodsClient;

    @Autowired
    private SpecificationService specificationClient;


    public Goods buildGoods(Spu spu) {
        // 查询分类
        List<Long> idList = Arrays.asList(spu.getCid1(), spu.getCid2(), spu.getCid3());
        List<Category> categoryList = categoryClient.queryByIds(idList);
        if (CollectionUtils.isEmpty(categoryList)) {
            throw new LyException(ExceptionEnum.CATEGORY_NOT_FOUND);
        }
        List<String> names = new ArrayList<>();
        for (Category category : categoryList) {
            names.add(category.getName());
        }
        // 查询品牌
        Brand brand = brandClient.queryById(spu.getBrandId());
        if (brand == null) {
            throw new LyException(ExceptionEnum.BRAND_NOT_FOUND);
        }

        // 搜索字段
        String all = spu.getTitle() + StringUtils.join(names, " ") + brand.getName();

        // 查询sku
        Long spuId = spu.getId();
        List<Sku> skuList = goodsClient.querySkusBySpuId(spuId);
        if (CollectionUtils.isEmpty(skuList)) {
            throw new LyException(ExceptionEnum.GOODS_SKU_NOT_FOUND);
        }
        Set<Long> priceSet = new HashSet<>();
        // 对sku进行处理, 只取部分需要的字段
        List<Map<String, Object>> skus = new ArrayList<>();
        for (Sku sku : skuList) {
            Map<String, Object> map = new HashMap<>();
            map.put("id", sku.getId());
            map.put("title", sku.getTitle());
            map.put("price", sku.getPrice());
            map.put("images", StringUtils.substringBefore(sku.getImages(), ","));
            skus.add(map);
            // 处理价格
            priceSet.add(sku.getPrice());
        }
        // 查询规格参数
        List<SpecParam> params =
                specificationClient.queryParamList(null, spu.getCid3(), true);
        if (CollectionUtils.isEmpty(params)) {
            throw new LyException(ExceptionEnum.SPEC_PARAM_NOT_FOUND);
        }
        // 查询商品详情
        SpuDetail spuDetail = goodsClient.queryDetailById(spuId);
        // 获取通用规格参数
        String genericJson = spuDetail.getGenericSpec();
        Map<Long, String> genericSpec = JsonUtils.toMap(genericJson, Long.class, String.class);
        // 获取特有规格参数
        String specialJson = spuDetail.getSpecialSpec();
        Map<Long, List<String>> specialSpec =
                JsonUtils.nativeRead(specialJson, new TypeReference<Map<Long, List<String>>>() {
                });
        // 规格参数, key是规格参数的名字, 值是规格参数的值
        Map<String, Object> specs = new HashMap<>();
        for (SpecParam param : params) {
            String key = param.getName();
            Object value = "";
            if (param.getGeneric()) {
                value = genericSpec.get(param.getId());
                // 判断是否是数值类型
                if (param.getNumeric()) {
                    // 处理成段
                    value = chooseSegment(value.toString(), param);
                }
            } else {
                value = specialSpec.get(param.getId());
            }
            // 存入map
            specs.put(key, value);
        }
        // 构建goods对象
        Goods goods = new Goods();
        goods.setId(spuId);
        goods.setBrandId(spu.getBrandId());
        goods.setCid1(spu.getCid1());
        goods.setCid2(spu.getCid2());
        goods.setCid3(spu.getCid3());
        goods.setCreateTime(spu.getCreateTime());
        goods.setAll(all);
        goods.setSkus(JsonUtils.toString(skus));
        goods.setPrice(priceSet);
        goods.setSpecs(specs);
        goods.setSubTitle(spu.getSubTitle());
        return goods;
    }

    private String chooseSegment(String value, SpecParam p) {
        double val = NumberUtils.toDouble(value);
        String result = "其它";
        // 保存数值段
        for (String segment : p.getSegments().split(",")) {
            String[] segs = segment.split("-");
            // 获取数值范围
            double begin = NumberUtils.toDouble(segs[0]);
            double end = Double.MAX_VALUE;
            if (segs.length == 2) {
                end = NumberUtils.toDouble(segs[1]);
            }
            // 判断是否在范围内
            if (val >= begin && val < end) {
                if (segs.length == 1) {
                    result = segs[0] + p.getUnit() + "以上";
                } else if (begin == 0) {
                    result = segs[1] + p.getUnit() + "以下";
                } else {
                    result = segment + p.getUnit();
                }
                break;
            }
        }
        return result;
    }

}
