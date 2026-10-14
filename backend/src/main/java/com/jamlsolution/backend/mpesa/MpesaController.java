package com.jamlsolution.backend.mpesa;

import com.jamlsolution.backend.common.ApiResponse;
import com.jamlsolution.backend.mpesa.dto.MpesaCallback;
import com.jamlsolution.backend.mpesa.dto.StkPushRequest;
import com.jamlsolution.backend.mpesa.dto.StkPushResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;

@Slf4j
@RestController
@RequestMapping("/api/v1/payments/mpesa")
@RequiredArgsConstructor
public class MpesaController {

    private final MpesaService mpesaService;

    @PostMapping("/stk-push")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public ApiResponse<StkPushResponse> stkPush(
            @Valid @RequestBody StkPushRequest request,
            HttpServletRequest httpRequest) {
        StkPushResponse response = mpesaService.initiateStkPush(request);
        return new ApiResponse<>(true, "STK push initiated", response, null, Instant.now(), httpRequest.getRequestURI());
    }

    /**
     * Safaricom calls this URL after the customer completes or cancels the M-Pesa prompt.
     * Must be publicly reachable (no auth header from Safaricom).
     */
    @PostMapping("/callback")
    public void callback(@RequestBody MpesaCallback callback) {
        log.info("M-Pesa callback received: resultCode={}",
                callback.body().stkCallback().resultCode());
        mpesaService.handleCallback(callback);
    }
}
