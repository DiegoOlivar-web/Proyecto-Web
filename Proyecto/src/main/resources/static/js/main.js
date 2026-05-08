function login() {
  const email = document.getElementById("email").value;
  const password = document.getElementById("password").value;

  const data = {
    usuario: email,
    password: password
  };

  fetch("http://localhost:8080/api/login", {
    method: "POST",
    headers: {
      "Content-Type": "application/json"
    },
    body: JSON.stringify(data)
  })
  .then(res => res.text())
  .then(respuesta => {

    // ✅ Mostrar mensaje en modal de resultado
    document.getElementById("mensajeResultado").innerText = respuesta;

    const resultadoModal = new bootstrap.Modal(document.getElementById("resultadoModal"));
    resultadoModal.show();

    // ✅ Cerrar modal de login
    const loginModalEl = document.getElementById("loginModal");
    const loginModal = bootstrap.Modal.getInstance(loginModalEl);

    if (loginModal) {
      loginModal.hide();
    }

  })
  .catch(error => {
    console.error("Error:", error);
  });
}
