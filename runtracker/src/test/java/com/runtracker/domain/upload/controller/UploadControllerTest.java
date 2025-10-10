package com.runtracker.domain.upload.controller;

import com.epages.restdocs.apispec.ResourceSnippetParameters;
import com.runtracker.RunTrackerDocumentApiTester;
import com.runtracker.domain.upload.service.FileStorageService;
import org.junit.jupiter.api.Test;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import static com.epages.restdocs.apispec.ResourceDocumentation.parameterWithName;
import static com.epages.restdocs.apispec.ResourceDocumentation.resource;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.multipart;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class UploadControllerTest extends RunTrackerDocumentApiTester {

    @MockitoBean
    private FileStorageService fileStorageService;

    @Test
    void uploadImage() throws Exception {
        // given
        MockMultipartFile file = new MockMultipartFile(
                "file",
                "test-image.jpg",
                MediaType.IMAGE_JPEG_VALUE,
                "test image content".getBytes()
        );

        String mockUrl = "http://localhost:8080/api/upload/image/550e8400-e29b-41d4-a716-446655440000.webp";
        when(fileStorageService.uploadImage(any())).thenReturn(mockUrl);

        // when & then
        this.mockMvc.perform(multipart("/api/upload/image")
                        .file(file))
                .andExpect(status().isOk())
                .andDo(document("upload-image",
                        resource(
                                ResourceSnippetParameters.builder()
                                        .tag("upload")
                                        .summary("이미지 파일 업로드")
                                        .description("이미지 파일을 업로드하고 URL을 반환합니다. " +
                                                "업로드된 이미지는 자동으로 WebP 포맷으로 변환되며, 필요시 리사이징됩니다 (최대 1920x1920). " +
                                                "지원 포맷: JPG, PNG, GIF, BMP. " +
                                                "최대 파일 크기: 10MB. " +
                                                "\n\n**요청 형식:** multipart/form-data, 'file' 파라미터에 이미지 파일 포함")
                                        .responseFields(
                                                fieldWithPath("status.statusCode").type(JsonFieldType.STRING).description("상태 코드"),
                                                fieldWithPath("status.message").type(JsonFieldType.STRING).description("상태 메시지"),
                                                fieldWithPath("status.description").type(JsonFieldType.STRING).description("상태 설명").optional(),
                                                fieldWithPath("body.url").type(JsonFieldType.STRING).description("업로드된 파일 URL (WebP 포맷)")
                                        )
                                        .build()
                        )
                ));
    }

    @Test
    void getImage() throws Exception {
        // given
        String filename = "550e8400-e29b-41d4-a716-446655440000.webp";
        byte[] imageBytes = "test image content".getBytes();
        Resource mockResource = new ByteArrayResource(imageBytes) {
            @Override
            public String getFilename() {
                return filename;
            }
        };
        when(fileStorageService.loadFileAsResource(anyString())).thenReturn(mockResource);

        // when & then
        this.mockMvc.perform(get("/api/upload/image/{filename}", filename))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.parseMediaType("image/webp")))
                .andDo(document("upload-get-image",
                        resource(
                                ResourceSnippetParameters.builder()
                                        .tag("upload")
                                        .summary("업로드된 이미지 조회")
                                        .description("업로드된 이미지 파일을 조회합니다. " +
                                                "브라우저나 이미지 뷰어에서 직접 표시할 수 있도록 inline으로 제공됩니다. url에 그냥 검색해도 이미지 나옵니다.")
                                        .pathParameters(
                                                parameterWithName("filename").description("조회할 파일명 (예: uuid.webp)")
                                        )
                                        .build()
                        )
                ));
    }
}