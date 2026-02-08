package com.lhsdev.cmsproject.controller;

import com.lhsdev.cmsproject.config.auth.PrincipalDetails;
import com.lhsdev.cmsproject.service.MainBannerService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequiredArgsConstructor
public class MainController {

    @Autowired
    private MainBannerService mainBannerService;

    @GetMapping("/")
    public String main(Model model, @AuthenticationPrincipal PrincipalDetails principalDetails) {
        if (principalDetails != null) {
            model.addAttribute("user", principalDetails.getUser());
        }
        java.util.List<com.lhsdev.cmsproject.domain.banner.MainBanner> banners = mainBannerService.getActiveBanners();
        System.out.println("Active Banners Count: " + banners.size());
        model.addAttribute("banners", banners);
        return "main";
    }
}
