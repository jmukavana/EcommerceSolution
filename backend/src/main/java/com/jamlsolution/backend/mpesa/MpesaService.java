package com.jamlsolution.backend.mpesa;

import com.jamlsolution.backend.mpesa.dto.MpesaCallback;
import com.jamlsolution.backend.mpesa.dto.StkPushRequest;
import com.jamlsolution.backend.mpesa.dto.StkPushResponse;

public interface MpesaService {
    StkPushResponse initiateStkPush(StkPushRequest request);
    void handleCallback(MpesaCallback callback);
}
