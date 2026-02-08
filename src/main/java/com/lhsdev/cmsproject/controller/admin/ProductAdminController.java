package com.lhsdev.cmsproject.controller.admin;

import com.lhsdev.cmsproject.domain.product.Product;
import com.lhsdev.cmsproject.domain.product.ProductCategory;
import com.lhsdev.cmsproject.domain.product.ProductStockType;
import com.lhsdev.cmsproject.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/admin/products")
@RequiredArgsConstructor
public class ProductAdminController {

    private final ProductService productService;

    @GetMapping
    public String index(Model model) {
        model.addAttribute("products", productService.getAllProducts());
        return "admin/product/list";
    }

    @GetMapping("/form")
    public String form(@RequestParam(required = false) Long id, Model model) {
        if (id != null) {
            model.addAttribute("product", productService.getProduct(id));
        } else {
            model.addAttribute("product", new Product());
        }
        model.addAttribute("categories", productService.getAllCategories());
        model.addAttribute("stockTypes", ProductStockType.values());
        // model.addAttribute("productTypes",
        // com.lhsdev.cmsproject.domain.product.ProductType.values());
        // model.addAttribute("pricePolicies",
        // com.lhsdev.cmsproject.domain.product.PricePolicy.values());
        // model.addAttribute("approvalTypes",
        // com.lhsdev.cmsproject.domain.product.ApprovalType.values());
        return "admin/product/form";
    }

    @PostMapping("/save")
    public String save(@ModelAttribute Product product,
            @RequestParam("categoryId") Long categoryId,
            @RequestParam(value = "file", required = false) MultipartFile file,
            RedirectAttributes redirectAttributes) {
        try {
            productService.saveProduct(product, categoryId, file);
            redirectAttributes.addFlashAttribute("message", "Product saved successfully.");
        } catch (Exception e) {
            e.printStackTrace();
            redirectAttributes.addFlashAttribute("error", "Error saving product: " + e.getMessage());
        }
        return "redirect:/admin/products";
    }

    @PostMapping("/delete")
    public String delete(@RequestParam Long id, RedirectAttributes redirectAttributes) {
        try {
            productService.deleteProduct(id);
            redirectAttributes.addFlashAttribute("message", "Product deleted successfully.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error deleting product: " + e.getMessage());
        }
        return "redirect:/admin/products";
    }
}
