package com.lhsdev.cmsproject.controller;

import com.lhsdev.cmsproject.domain.product.Product;
import com.lhsdev.cmsproject.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Controller
@RequestMapping("/products")
@RequiredArgsConstructor
public class ProductController {

    @Autowired
    private ProductService productService;

    @GetMapping
    public String list(Model model) {
        // TODO: Add category filtering and pagination logic later
        List<Product> products = productService.getAllProducts(); // Currently fetches all active/inactive. Need to
                                                                  // filter active only ideally.
        // Actually getAllProducts in service fetches all ordered by created date.
        // Let's assume for user view we might want only active ones.
        // But for now, let's use what we have and maybe filter in service later.

        model.addAttribute("products", products);
        model.addAttribute("categories", productService.getActiveCategories());
        return "product/list";
    }

    @GetMapping("/{id}")
    public String detail(@PathVariable Long id, Model model) {
        Product product = productService.getProduct(id);
        if (!product.getIsActive()) {
            return "redirect:/products"; // Or show 404
        }
        model.addAttribute("product", product);
        return "product/detail";
    }
}
