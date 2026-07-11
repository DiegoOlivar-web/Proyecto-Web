package com.example.Proyecto;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import com.example.Proyecto.Entity.ClienteEntity;
import com.example.Proyecto.model.CartItem;

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

	@Test
	void checkoutRenderizaConUsuarioYCarrito() throws Exception {
		ClienteEntity cliente = new ClienteEntity();
		cliente.setId(1L);
		cliente.setNombre("Andres");
		cliente.setApellido("Quispe");
		cliente.setDni("12345678");
		cliente.setTelefono("987654321");
		cliente.setCorreo("andres@test.com");
		cliente.setDireccion("Av. Peru 123");
		cliente.setDistrito("Lima");
		cliente.setReferencia("Puerta roja");

		MockHttpSession session = new MockHttpSession();
		session.setAttribute("usuarioLogueado", cliente);
		session.setAttribute("cart", List.of(new CartItem("1", "1/4 Pollo a la Brasa", 18.0, 1)));

		MockMvcBuilders.webAppContextSetup(context)
				.build()
				.perform(get("/checkout").session(session))
				.andExpect(status().isOk());
	}
}
