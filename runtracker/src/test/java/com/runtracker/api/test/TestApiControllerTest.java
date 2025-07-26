package com.runtracker.api.test;

import com.epages.restdocs.apispec.MockMvcRestDocumentationWrapper;
import com.runtracker.domain.test.controller.TestController;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders;
import org.springframework.restdocs.operation.preprocess.Preprocessors;
import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

// TODO: REST Docs 테스트를 위해 임시로 생성된 파일. 추후 삭제해야함
@WebMvcTest(TestController.class)
@AutoConfigureRestDocs
@DisplayName("REST Docs 테스트용 임시 API 테스트")
@AutoConfigureMockMvc(addFilters = false)
class TestApiControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    @DisplayName("테스트 API")
    void testApi() throws Exception {
        mockMvc.perform(RestDocumentationRequestBuilders.get("/api/test"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.body.message").value("RunTracker API Test Endpoint"))
                .andExpect(jsonPath("$.body.status").value("success"))
                .andExpect(jsonPath("$.body.description").value("This is a test endpoint for REST Docs validation"))
                .andDo(MockMvcRestDocumentationWrapper.document("api/test",
                        responseFields(
                                fieldWithPath("status").type(JsonFieldType.OBJECT).description("API 응답 상태 정보"),
                                fieldWithPath("status.statusCode").type(JsonFieldType.STRING).description("상태 코드"),
                                fieldWithPath("status.message").type(JsonFieldType.STRING).description("상태 메시지"),
                                fieldWithPath("status.description").type(JsonFieldType.STRING).description("상세 설명").optional(),
                                fieldWithPath("body").type(JsonFieldType.OBJECT).description("응답 데이터"),
                                fieldWithPath("body.message").type(JsonFieldType.STRING).description("테스트 메시지"),
                                fieldWithPath("body.status").type(JsonFieldType.STRING).description("API 상태"),
                                fieldWithPath("body.description").type(JsonFieldType.STRING).description("API 설명")
                        )
                ));
    }
}