package com.msa.productservice;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.msa.productservice.form.ProductForm;
import com.msa.productservice.response.ResponseProduct;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.transaction.annotation.Transactional;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class ProductControllerTest {

    @Autowired
    MockMvc mockMvc;
    @Autowired
    ObjectMapper objectMapper;
    @Autowired
    ProductService productService;

    @Test
    @DisplayName("상품 등록 성공")
    public void createProduct() throws Exception {
        //given
        ProductForm product = ProductForm.builder()
                .name("상품")
                .stock(100)
                .price(1000)
                .build();
        //when
        ResultActions resultActions = requestProductEnroll(product);
        //then
        resultActions
                .andExpect(status().isCreated())
                .andExpect(jsonPath("code").value("SUCCESS"))
                .andExpect(jsonPath("message").value("상품 등록 성공"))
                .andExpect(jsonPath("product.id").exists())
                .andExpect(jsonPath("product.name").value(product.getName()))
                .andExpect(jsonPath("product.stock").value(product.getStock()))
                .andExpect(jsonPath("product.price").value(product.getPrice()));
    }


    @Test
    @DisplayName("전체 상품 조회")
    public void findProducts() throws Exception {
        //given
        ProductForm product0 = ProductForm.builder()
                .name("상품0")
                .stock(200)
                .price(2000)
                .build();
        ProductForm product1 = ProductForm.builder()
                .name("상품1")
                .stock(300)
                .price(3000)
                .build();
        //when
        ResponseProduct savedProduct0 = productService.createProduct(product0);
        ResponseProduct savedProduct1 = productService.createProduct(product1);
        ResultActions resultActions = requestFindProducts();
        //then
        resultActions
                .andExpect(status().isOk())
                .andExpect(jsonPath("[0].id").value(savedProduct0.getId()))
                .andExpect(jsonPath("[0].name").value(savedProduct0.getName()))
                .andExpect(jsonPath("[0].stock").value(savedProduct0.getStock()))
                .andExpect(jsonPath("[0].price").value(savedProduct0.getPrice()))
                .andExpect(jsonPath("[0].createdAt").exists())
                .andExpect(jsonPath("[1].id").value(savedProduct1.getId()))
                .andExpect(jsonPath("[1].name").value(savedProduct1.getName()))
                .andExpect(jsonPath("[1].stock").value(savedProduct1.getStock()))
                .andExpect(jsonPath("[1].price").value(savedProduct1.getPrice()))
                .andExpect(jsonPath("[1].createdAt").exists());
    }

    /**
     * 상품 등록 요청
     * @param form 상품 form
     */
    private ResultActions requestProductEnroll(ProductForm form) throws Exception {
        return mockMvc.perform(post("/products")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(form)))
                .andDo(print());
    }

    /**
     * 전체 상품 조회 요청
     */
    private ResultActions requestFindProducts() throws Exception {
        return mockMvc.perform(get("/products")
                .contentType(MediaType.APPLICATION_JSON))
                .andDo(print());
    }
}