package com.lhsdev.cmsproject.controller.admin;

import com.lhsdev.cmsproject.domain.banner.MainBanner;
import com.lhsdev.cmsproject.service.MainBannerService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/admin/banners")
@RequiredArgsConstructor
public class BannerAdminController {

    private final MainBannerService mainBannerService;

    @GetMapping
    public String index(Model model) {
        model.addAttribute("banners", mainBannerService.getAllBanners());
        return "admin/banner/list";
    }

    @GetMapping("/form")
    public String form(@RequestParam(required = false) Long id, Model model) {
        if (id != null) {
            model.addAttribute("banner", mainBannerService.getBanner(id));
        } else {
            // MainBanner is protected no-args, so we need to be careful.
            // But we added @Setter, so Spring can instantiate it if it has a public no-arg
            // constructor?
            // Wait, @NoArgsConstructor(access = AccessLevel.PROTECTED).
            // Spring uses reflection, so protected might be fine, or might fail for direct
            // instantiation in "new MainBanner()".
            // Since we can't do "new MainBanner()", we might need a static factory or
            // change access level.
            // Let's change MainBanner constructor access to PUBLIC or use a builder to
            // create a default one.
            // Actually, for form backing object often public no-arg is best.
            // Let's fix MainBanner to have public no-arg or provide a method. Ref: JPA
            // needs protected/public.
            // But here "new MainBanner()" will fail compilation if we try to write it.
            // Solution: Use builder to create an empty one or allow public no-args.
            // I'll update MainBanner to have Public NoArgs or just use builder in the
            // controller.
            model.addAttribute("banner", MainBanner.builder().build());
        }
        return "admin/banner/form";
    }

    @PostMapping("/save")
    public String save(@ModelAttribute MainBanner banner,
            @RequestParam(value = "file", required = false) MultipartFile file,
            RedirectAttributes redirectAttributes) {
        try {
            if (banner.getId() != null) {
                mainBannerService.updateBanner(banner.getId(), banner, file);
            } else {
                mainBannerService.saveBanner(banner, file);
            }
            redirectAttributes.addFlashAttribute("message", "Banner saved successfully.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error saving banner: " + e.getMessage());
        }
        return "redirect:/admin/banners";
    }

    @PostMapping("/delete")
    public String delete(@RequestParam Long id, RedirectAttributes redirectAttributes) {
        try {
            mainBannerService.deleteBanner(id);
            redirectAttributes.addFlashAttribute("message", "Banner deleted successfully.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error deleting banner: " + e.getMessage());
        }
        return "redirect:/admin/banners";
    }
}
