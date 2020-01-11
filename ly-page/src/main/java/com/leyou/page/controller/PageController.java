package com.leyou.page.controller;

import com.leyou.page.service.PageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.Map;

@Controller
@RequestMapping("/portal")//增加门户网站的路径，从而使得js等文件可以正常加载
public class PageController {

    @Autowired
    private PageService pageService;

    /**
     * 不走网关
     * @param spuId
     * @param model
     * @return
     */
    @GetMapping("item/{id}.html")
    public String toItemPage(@PathVariable("id") Long spuId, Model model) {
        // 查询模型数据
        Map<String, Object> attributes = pageService.loadModel(spuId);
        // 准备模型数据
        model.addAllAttributes(attributes);
        // 返回视图
        return "item";
    }


}
