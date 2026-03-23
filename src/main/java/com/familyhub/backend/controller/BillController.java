package com.familyhub.backend.controller;

import com.familyhub.backend.common.ApiResponse;
import com.familyhub.backend.dto.BillDtos;
import com.familyhub.backend.service.BillService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.List;

@RestController
@RequestMapping("/api/v1/families/{familyId}/bills")
public class BillController extends BaseController {

    private final BillService billService;

    public BillController(BillService billService) {
        this.billService = billService;
    }

    @GetMapping
    public ApiResponse<List<BillDtos.BillResponse>> list(@PathVariable Long familyId,
                                                         @RequestParam(required = false) LocalDate start,
                                                         @RequestParam(required = false) LocalDate end,
                                                         Authentication authentication,
                                                         HttpServletRequest request) {
        return ApiResponse.success(billService.list(familyId, currentUser(authentication).userId(), start, end), trace(request));
    }

    @PostMapping
    public ApiResponse<BillDtos.BillResponse> create(@PathVariable Long familyId,
                                                     @Valid @RequestBody BillDtos.UpsertBillRequest body,
                                                     Authentication authentication,
                                                     HttpServletRequest request) {
        return ApiResponse.success(billService.create(familyId, currentUser(authentication).userId(), body), trace(request));
    }

    @PutMapping("/{billId}")
    public ApiResponse<BillDtos.BillResponse> update(@PathVariable Long familyId,
                                                     @PathVariable Long billId,
                                                     @Valid @RequestBody BillDtos.UpsertBillRequest body,
                                                     Authentication authentication,
                                                     HttpServletRequest request) {
        return ApiResponse.success(billService.update(familyId, billId, currentUser(authentication).userId(), body), trace(request));
    }

    @DeleteMapping("/{billId}")
    public ApiResponse<Void> delete(@PathVariable Long familyId, @PathVariable Long billId, Authentication authentication, HttpServletRequest request) {
        billService.delete(familyId, billId, currentUser(authentication).userId());
        return ApiResponse.success(null, trace(request));
    }

    @GetMapping("/statistics")
    public ApiResponse<BillDtos.BillStatisticsResponse> statistics(@PathVariable Long familyId,
                                                                   @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM") YearMonth month,
                                                                   Authentication authentication,
                                                                   HttpServletRequest request) {
        return ApiResponse.success(billService.statistics(familyId, currentUser(authentication).userId(), month), trace(request));
    }

    private String trace(HttpServletRequest request) {
        return String.valueOf(request.getAttribute("traceId"));
    }
}
