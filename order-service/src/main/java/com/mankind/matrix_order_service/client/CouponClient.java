package com.mankind.matrix_order_service.client;

import com.mankind.api.coupon.dto.CouponDTO;
import com.mankind.matrix_order_service.config.FeignConfig;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(
    name = "coupon-service",
    url = "${COUPON_SERVICE_URL:http://localhost:8087}",
    configuration = FeignConfig.class
)
public interface CouponClient {
    @GetMapping("/coupons/validate")
    CouponDTO validateCoupon(@RequestParam("code") String code);

    @PostMapping("/coupons/use")
    CouponDTO useCoupon(@RequestBody UseCouponRequest request);

    class UseCouponRequest {
        private String code;
        private Long orderId;

        public UseCouponRequest() {}

        public UseCouponRequest(String code, Long orderId) {
            this.code = code;
            this.orderId = orderId;
        }

        public String getCode() {
            return code;
        }

        public void setCode(String code) {
            this.code = code;
        }

        public Long getOrderId() {
            return orderId;
        }

        public void setOrderId(Long orderId) {
            this.orderId = orderId;
        }
    }
}
