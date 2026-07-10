document.addEventListener('DOMContentLoaded', () => {
    const cartBadge = document.getElementById('cart-count-badge');
    const cartContainer = document.getElementById('cart-items-container');
    const cartTotal = document.getElementById('cart-total');
    const confirmButton = document.getElementById('confirm-order-button');

    const money = (value) => `S/. ${Number(value || 0).toFixed(2)}`;
    const escapeHtml = (value) => String(value).replace(/[&<>"']/g, (char) => ({
        '&': '&amp;',
        '<': '&lt;',
        '>': '&gt;',
        '"': '&quot;',
        "'": '&#39;'
    }[char]));

    const renderCart = (cart) => {
        if (cartBadge) {
            cartBadge.textContent = cart.cartCount;
        }
        if (cartTotal) {
            cartTotal.textContent = money(cart.cartTotal);
        }
        if (confirmButton) {
            confirmButton.disabled = cart.cartCount === 0;
        }
        if (!cartContainer) {
            return;
        }

        if (!cart.items || cart.items.length === 0) {
            cartContainer.innerHTML = `
                <div class="text-center py-5">
                    <i class="fa-solid fa-bag-shopping fa-3x text-muted mb-3"></i>
                    <p>Tu carrito esta vacio.</p>
                </div>`;
            return;
        }

        cartContainer.innerHTML = `
            <div>
                <ul class="list-group mb-3">
                    ${cart.items.map((item) => `
                        <li class="list-group-item d-flex justify-content-between align-items-center">
                            <div class="ms-2 me-auto">
                                <div class="fw-bold">${escapeHtml(item.nombre)}</div>
                                <small>Cantidad: ${item.cantidad}</small>
                                <br>
                                <small class="text-muted">${money(item.total)}</small>
                            </div>
                            <form action="/cart/eliminar" method="post" class="mb-0">
                                <input type="hidden" name="idProducto" value="${escapeHtml(item.id)}" />
                                <button type="submit" class="btn btn-sm btn-outline-danger border-0" title="Eliminar del carrito">
                                    <i class="fa-solid fa-trash-can"></i>
                                </button>
                            </form>
                        </li>
                    `).join('')}
                </ul>
            </div>`;
    };

    document.addEventListener('submit', async (event) => {
        const form = event.target;
        if (!(form instanceof HTMLFormElement)) {
            return;
        }

        const action = form.getAttribute('action') || '';
        const isAddForm = action.endsWith('/cart/add');
        const isDeleteForm = action.endsWith('/cart/eliminar');
        if (!isAddForm && !isDeleteForm) {
            return;
        }

        event.preventDefault();
        const button = form.querySelector('button[type="submit"]');
        const originalButtonHtml = button ? button.innerHTML : '';

        if (button) {
            button.disabled = true;
            button.textContent = isDeleteForm ? 'Borrando...' : 'Agregando...';
        }

        try {
            const response = await fetch(form.action, {
                method: 'POST',
                body: new FormData(form),
                headers: {
                    'X-Requested-With': 'XMLHttpRequest'
                }
            });

            if (!response.ok) {
                throw new Error('No se pudo agregar el producto.');
            }

            renderCart(await response.json());
            if (button) {
                button.textContent = isDeleteForm ? 'Borrado' : 'Agregado';
            }

            const modalElement = form.closest('.modal');
            if (modalElement && window.bootstrap) {
                const modal = bootstrap.Modal.getInstance(modalElement);
                if (modal) {
                    modal.hide();
                }
            }
        } catch (error) {
            form.submit();
            return;
        } finally {
            if (button) {
                setTimeout(() => {
                    button.disabled = false;
                    button.innerHTML = originalButtonHtml;
                }, 700);
            }
        }
    });
});
