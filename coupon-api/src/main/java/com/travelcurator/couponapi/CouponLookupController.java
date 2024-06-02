package com.travelcurator.couponapi;

import com.travelcurator.couponapi.service.CouponLookupRequestService;
import com.travelcurator.couponcore.model.CouponIssue;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor
@RestController
public class CouponLookupController {
    public final CouponLookupRequestService couponLookupRequestService;
    List<CouponIssue> cll;
    @GetMapping(path = "/v1/lookup/{userId}")
    public String lookUpV1(@PathVariable long userId, Model model){
        cll = couponLookupRequestService.LookupRequestV1(userId);
        model.addAttribute("list",cll);
        return "CouponLookup";
    }
}
