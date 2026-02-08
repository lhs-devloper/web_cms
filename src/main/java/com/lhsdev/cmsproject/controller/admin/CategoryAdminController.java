package com.lhsdev.cmsproject.controller.admin;

import com.lhsdev.cmsproject.domain.product.ProductCategory;
import com.lhsdev.cmsproject.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/admin/categories")
@RequiredArgsConstructor
public class CategoryAdminController {

    private final ProductService productService;

    @GetMapping
    public String index(Model model) {
        model.addAttribute("categories", productService.getAllCategories());
        return "admin/category/list";
    }

    @GetMapping("/form")
    public String form(@RequestParam(required = false) Long id, Model model) {
        if (id != null) {
            model.addAttribute("category", productService.getCategory(id));
        } else {
            model.addAttribute("category", new ProductCategory());
        }
        return "admin/category/form";
    }

    @PostMapping("/save")
    public String save(@ModelAttribute ProductCategory category, RedirectAttributes redirectAttributes) {
        try {
            productService.saveCategory(category);
            redirectAttributes.addFlashAttribute("message", "Category saved successfully.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error saving category: " + e.getMessage());
        }
        return "redirect:/admin/categories";
    }

    @PostMapping("/delete")
    public String delete(@RequestParam Long id, RedirectAttributes redirectAttributes) {
        try {
            productService.deleteCategory(id);
            redirectAttributes.addFlashAttribute("message", "Category deleted successfully.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error deleting category: " + e.getMessage());
        }
        return "redirect:/admin/categories";
    }
}
