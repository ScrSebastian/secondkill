package com.xxxx.secondkill.controller;

import com.xxxx.secondkill.pojo.User;
import com.xxxx.secondkill.vo.GoodsVo;
import com.xxxx.secondkill.service.IGoodsService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.Duration;
import java.time.LocalDateTime;

/**
 * 商品页面控制器。
 */
@Controller
@RequestMapping("/goods")
public class GoodsController {

    private final IGoodsService goodsService;

    public GoodsController(IGoodsService goodsService) {
        this.goodsService = goodsService;
    }

    /**
     * 跳转商品列表页。
     *
     * @param model 页面模型
     * @param user  当前登录用户，由 UserArgumentResolver 注入
     * @return 商品列表模板
     */
    @GetMapping("/toList")
    public String toList(Model model, User user, HttpServletRequest request, HttpServletResponse response) {
        if (user == null) {
            return null;
        }
        model.addAttribute("user", user);
        model.addAttribute("goodsList", goodsService.findGoodsVo());
        return "goodsList";
    }

    /**
     * 跳转商品详情页。
     *
     * @param model 页面模型
     * @param user 当前登录用户，由 UserArgumentResolver 注入
     * @param goodsId 商品 ID
     * @return 商品详情模板，或商品列表页
     */
    @GetMapping("/toDetail/{goodsId}")
    public String toDetail(Model model, User user, @PathVariable Long goodsId,
                           HttpServletRequest request, HttpServletResponse response) throws IOException {
        if (user == null) {
            return null;
        }

        GoodsVo goods = goodsService.findGoodsVoByGoodsId(goodsId);
        if (goods == null) {
            response.sendRedirect(request.getContextPath() + "/goods/toList");
            return null;
        }

        int secKillStatus = 2;
        int remainSeconds = -1;
        if (goods.getSeckillPrice() != null && goods.getStartDate() != null && goods.getEndDate() != null) {
            LocalDateTime now = LocalDateTime.now();
            if (now.isBefore(goods.getStartDate())) {
                secKillStatus = 0;
                remainSeconds = (int) Duration.between(now, goods.getStartDate()).getSeconds();
            } else if (now.isBefore(goods.getEndDate())) {
                secKillStatus = 1;
                remainSeconds = 0;
            }
        }

        model.addAttribute("user", user);
        model.addAttribute("remainSeconds", remainSeconds);
        model.addAttribute("secKillStatus", secKillStatus);
        model.addAttribute("goods", goods);

        return "goodsDetail";
    }
}
