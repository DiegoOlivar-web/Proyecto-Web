package com.example.Proyecto;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
class ProyectoApplicationTests {

	@Autowired
	private WebApplicationContext context;

	@Test
	void contextLoads() {
	}

	@Test
	void menuRenderizaCorrectamente() throws Exception {
		MockMvcBuilders.webAppContextSetup(context)
				.build()
				.perform(get("/menu"))
				.andExpect(status().isOk());
	}
}
