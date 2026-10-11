package com.mankind.services.config;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
@Order(Ordered.HIGHEST_PRECEDENCE)
public class ApiV1PathRewriteFilter extends OncePerRequestFilter {

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        String path = request.getRequestURI();
        String contextPath = request.getContextPath();
        if (contextPath != null && !contextPath.isEmpty() && path.startsWith(contextPath)) {
            path = path.substring(contextPath.length());
        }

        String rewritten = rewrite(path);
        if (rewritten != null && !rewritten.equals(path)) {
            request.getRequestDispatcher(rewritten).forward(request, response);
            return;
        }
        filterChain.doFilter(request, response);
    }

    static String rewrite(String path) {
        if (path == null || path.isEmpty()) {
            return path;
        }

        if ("/api/v1/cart".equals(path)) {
            return "/cart";
        }
        if (path.startsWith("/api/v1/cart/")) {
            return "/cart/" + path.substring("/api/v1/cart/".length());
        }

        if ("/api/v1/users/me".equals(path)) {
            return "/users/me";
        }
        if (path.startsWith("/api/v1/users/me/")) {
            return "/users/me/" + path.substring("/api/v1/users/me/".length());
        }

        if (path.startsWith("/api/v1/auth/")) {
            return "/auth/" + path.substring("/api/v1/auth/".length());
        }

        if ("/api/v1/users".equals(path)) {
            return "/users";
        }
        if (path.startsWith("/api/v1/users/")) {
            return "/users/" + path.substring("/api/v1/users/".length());
        }

        if ("/api/v1/products".equals(path)) {
            return "/products";
        }
        if (path.startsWith("/api/v1/products/")) {
            return "/products/" + path.substring("/api/v1/products/".length());
        }

        if ("/api/v1/coupons".equals(path)) {
            return "/coupons";
        }
        if (path.startsWith("/api/v1/coupons/")) {
            return "/coupons/" + path.substring("/api/v1/coupons/".length());
        }

        if ("/api/v1/wishlist".equals(path)) {
            return "/wishlist";
        }
        if (path.startsWith("/api/v1/wishlist/")) {
            return "/wishlist/" + path.substring("/api/v1/wishlist/".length());
        }

        if (path.startsWith("/api/v1/payments/")) {
            return "/payments/" + path.substring("/api/v1/payments/".length());
        }
        if (path.startsWith("/api/v1/admin/payments/")) {
            return "/admin/payments/" + path.substring("/api/v1/admin/payments/".length());
        }

        if ("/api/v1/notifications".equals(path)) {
            return "/notifications";
        }
        if (path.startsWith("/api/v1/notifications/")) {
            return "/notifications/" + path.substring("/api/v1/notifications/".length());
        }

        if ("/api/v1/payments".equals(path)) {
            return "/payments";
        }

        if ("/api/v1/referrals".equals(path)) {
            return "/referrals";
        }
        if (path.startsWith("/api/v1/referrals/")) {
            return "/referrals/" + path.substring("/api/v1/referrals/".length());
        }

        if (path.startsWith("/api/v1/admin/orders/")) {
            return "/orders/" + path.substring("/api/v1/admin/orders/".length());
        }

        if ("/api/v1/orders".equals(path)) {
            return "/orders";
        }
        if (path.startsWith("/api/v1/orders/")) {
            return "/orders/" + path.substring("/api/v1/orders/".length());
        }

        return path;
    }
}
