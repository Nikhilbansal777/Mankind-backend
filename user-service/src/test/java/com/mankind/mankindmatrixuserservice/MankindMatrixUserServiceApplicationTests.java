package com.mankind.mankindmatrixuserservice;

import com.mankind.mankindmatrixuserservice.mapper.UserMapperImpl;
import com.mankind.mankindmatrixuserservice.mapper.UserRegistrationMapperImpl;
import com.mankind.mankindmatrixuserservice.mapper.UserUpdateMapperImpl;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;

@SpringBootTest(properties = {
        "spring.autoconfigure.exclude=" +
        "org.springframework.boot.autoconfigure.security.oauth2.resource.OAuth2ResourceServerAutoConfiguration"
})
@Import({
        UserMapperImpl.class,
        UserRegistrationMapperImpl.class,
        UserUpdateMapperImpl.class
})
class MankindMatrixUserServiceApplicationTests {

    @Test
    void contextLoads() {
    }
}