package com.mankind.corporateauthservice.controller;

import com.mankind.corporateauthservice.exception.GlobalExceptionHandler;
import com.mankind.corporateauthservice.service.CorporateAuthService;
import com.mankind.corporateauthservice.service.JwtService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = CorporateAuthController.class)
@AutoConfigureMockMvc(addFilters = false)
@Import(GlobalExceptionHandler.class)
class CorporateAuthControllerAdviceTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private CorporateAuthService corporateAuthService;

    @MockitoBean
    private JwtService jwtService;

    @Test
    void registerReturnsConflictWhenDataIntegrityViolationOccurs() throws Exception {
        when(corporateAuthService.register(any()))
                .thenThrow(new DataIntegrityViolationException("Duplicate key"));

        String payload = """
                {
                  "corporateName": "Acme Corp",
                  "email": "admin@acme.com",
                  "password": "Secure123!",
                  "dateOfJoining": "2024-01-15"
                }
                """;

        mockMvc.perform(post("/api/v1/corporate/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(payload))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.status").value(409))
                .andExpect(jsonPath("$.error").value("Conflict"))
                .andExpect(jsonPath("$.message").value("Resource already exists"))
                .andExpect(jsonPath("$.path").value("/api/v1/corporate/auth/register"));
    }
}
