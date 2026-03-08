package com.reservAR.backreservar.Filter;


import com.reservAR.backreservar.config.RateLimiterFilter;
import jakarta.servlet.FilterChain;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

class RateLimiterFilterTest {

    private final RateLimiterFilter filter = new RateLimiterFilter();

    @Test
    void shouldNotLimitLoginBecausePathHasBug() throws Exception {
        MockHttpServletRequest request = new MockHttpServletRequest("POST", "/auth/user/login");
        request.setRemoteAddr("127.0.0.1");
        MockHttpServletResponse response = new MockHttpServletResponse();
        FilterChain chain = mock(FilterChain.class);

        for (int i = 0; i < 10; i++) {
            filter.doFilter(request, response, chain);
        }

        assertEquals(200, response.getStatus() == 0 ? 200 : response.getStatus());
        verify(chain, times(10)).doFilter(any(), any());
    }
}