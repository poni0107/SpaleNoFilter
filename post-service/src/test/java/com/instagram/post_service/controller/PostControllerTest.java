package com.instagram.post_service.controller;

import com.instagram.post_service.entity.Post;
import com.instagram.post_service.service.PostService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.List;

import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class PostControllerTest {

    private MockMvc mockMvc;

    @Mock
    private PostService postService;

    @InjectMocks
    private PostController postController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(postController).build();
    }

    @Test
    void createPost_withFiles_shouldReturn200() throws Exception {
        MockMultipartFile file = new MockMultipartFile(
                "files", "test.jpg", "image/jpeg", "fake image content".getBytes()
        );

        Post post = Post.builder().id(1L).description("Test opis").userId(1L).build();
        when(postService.createPostWithMedia(anyString(), anyLong(), anyList())).thenReturn(post);

        mockMvc.perform(multipart("/api/posts")
                        .file(file)
                        .param("description", "Test opis")
                        .param("userId", "1")
                        .characterEncoding("UTF-8")) // dodato UTF-8 kodiranje
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.description").value("Test opis"));
    }

    @Test
    void deletePost_shouldReturnSuccessMessage() throws Exception {
        doNothing().when(postService).deletePost(1L);

        mockMvc.perform(delete("/api/posts/1")
                        .characterEncoding("UTF-8")) // dodato UTF-8 kodiranje
                .andExpect(status().isOk())
                .andExpect(content().string("Post sa ID-jem 1 je uspešno obrisan."));
    }

    @Test
    void getAllPosts_shouldReturnList() throws Exception {
        Post post = Post.builder().id(1L).description("Test").userId(1L).build();
        when(postService.getAllPosts()).thenReturn(List.of(post));

        mockMvc.perform(get("/api/posts")
                        .characterEncoding("UTF-8")) // UTF-8 i ovde
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1));
    }
}