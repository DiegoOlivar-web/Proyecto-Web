document.addEventListener('DOMContentLoaded', () => {
    const stepPanels = Array.from(document.querySelectorAll('[data-checkout-step]'));
    const indicators = Array.from(document.querySelectorAll('[data-step-indicator]'));
    const deliveryFields = document.querySelector('[data-delivery-fields]');
    const pickupBox = document.querySelector('[data-pickup-box]');
    const facturaFields = document.querySelector('[data-factura-fields]');
    const tipoComprobante = document.getElementById('tipoComprobante');
    const summary = document.querySelector('.checkout-summary');
    const subtotal = Number(summary?.dataset.subtotal || 0);

    const money = (value) => `S/. ${Number(value || 0).toFixed(2)}`;

    const showStep = (step) => {
        stepPanels.forEach((panel) => {
            panel.classList.toggle('d-none', panel.dataset.checkoutStep !== String(step));
        });
        indicators.forEach((indicator) => {
            const value = Number(indicator.dataset.stepIndicator);
            indicator.classList.toggle('active', value === step);
            indicator.classList.toggle('complete', value < step);
        });
        window.scrollTo({ top: 0, behavior: 'smooth' });
    };

    const validateVisibleStep = () => {
        const activePanel = stepPanels.find((panel) => !panel.classList.contains('d-none'));
        const fields = activePanel ? Array.from(activePanel.querySelectorAll('input, select, textarea')) : [];
        const invalid = fields.find((field) => !field.disabled && !field.checkValidity());
        if (invalid) {
            invalid.reportValidity();
            return false;
        }
        return true;
    };

    document.querySelectorAll('[data-next-step]').forEach((button) => {
        button.addEventListener('click', () => {
            if (validateVisibleStep()) {
                showStep(Number(button.dataset.nextStep));
            }
        });
    });

    document.querySelectorAll('[data-prev-step]').forEach((button) => {
        button.addEventListener('click', () => showStep(Number(button.dataset.prevStep)));
    });

    const updateEntrega = () => {
        const tipo = document.querySelector('input[name="tipoEntrega"]:checked')?.value || 'DELIVERY';
        const isDelivery = tipo === 'DELIVERY';
        deliveryFields?.classList.toggle('d-none', !isDelivery);
        pickupBox?.classList.toggle('d-none', isDelivery);
        document.querySelectorAll('[data-delivery-field]').forEach((field) => {
            field.required = isDelivery;
            field.disabled = !isDelivery;
        });
        document.querySelectorAll('.checkout-option').forEach((option) => {
            const input = option.querySelector('input');
            option.classList.toggle('active', input && input.checked);
        });
    };

    const updateComprobante = () => {
        const isFactura = tipoComprobante?.value === 'FACTURA';
        facturaFields?.classList.toggle('d-none', !isFactura);
        document.querySelectorAll('[data-factura-field]').forEach((field) => {
            field.required = isFactura;
            field.disabled = !isFactura;
        });
    };

    const updateMetodoPago = () => {
        const metodo = document.querySelector('input[name="metodoPago"]:checked')?.value || 'YAPE';

        document.querySelectorAll('.checkout-payment').forEach((option) => {
            const optionInput = option.querySelector('input');
            option.classList.toggle('active', optionInput && optionInput.checked);
        });

        document.querySelectorAll('[data-payment-detail]').forEach((detail) => {
            const active = detail.dataset.paymentDetail === metodo;
            detail.classList.toggle('active', active);
            detail.classList.toggle('d-none', !active);
            detail.querySelectorAll('[data-payment-field]').forEach((field) => {
                field.disabled = !active;
                field.required = active && field.hasAttribute('data-payment-required');
            });
        });

        updateCashChange();
    };

    const formatCardNumber = (value) => value
        .replace(/\D/g, '')
        .slice(0, 16)
        .replace(/(\d{4})(?=\d)/g, '$1 ');

    const formatExpiry = (value) => {
        const digits = value.replace(/\D/g, '').slice(0, 4);
        if (digits.length <= 2) {
            return digits;
        }
        return `${digits.slice(0, 2)}/${digits.slice(2)}`;
    };

    const updateCardPreview = () => {
        const number = document.querySelector('[data-card-number]')?.value || '';
        const name = document.querySelector('[data-card-name]')?.value || '';
        const expiry = document.querySelector('[data-card-expiry]')?.value || '';
        const previewNumber = document.querySelector('[data-card-preview-number]');
        const previewName = document.querySelector('[data-card-preview-name]');
        const previewExpiry = document.querySelector('[data-card-preview-expiry]');

        if (previewNumber) {
            previewNumber.textContent = number.trim() || '0000 0000 0000 0000';
        }
        if (previewName) {
            previewName.textContent = name.trim().toUpperCase() || 'TITULAR';
        }
        if (previewExpiry) {
            previewExpiry.textContent = expiry.trim() || 'MM/AA';
        }
    };

    function updateCashChange() {
        const cashInput = document.querySelector('[data-cash-amount]');
        const changeInput = document.querySelector('[data-cash-change]');
        if (!cashInput || !changeInput) {
            return;
        }

        const paid = Number(cashInput.value || 0);
        changeInput.value = paid >= subtotal ? money(paid - subtotal) : money(0);
    }

    document.querySelectorAll('input[name="tipoEntrega"]').forEach((input) => {
        input.addEventListener('change', updateEntrega);
    });

    document.querySelectorAll('input[name="metodoPago"]').forEach((input) => {
        input.addEventListener('change', updateMetodoPago);
    });

    tipoComprobante?.addEventListener('change', updateComprobante);

    document.querySelector('[data-card-number]')?.addEventListener('input', (event) => {
        event.target.value = formatCardNumber(event.target.value);
        updateCardPreview();
    });

    document.querySelector('[data-card-name]')?.addEventListener('input', updateCardPreview);

    document.querySelector('[data-card-expiry]')?.addEventListener('input', (event) => {
        event.target.value = formatExpiry(event.target.value);
        updateCardPreview();
    });

    document.querySelector('[data-cash-amount]')?.addEventListener('input', updateCashChange);

    updateEntrega();
    updateComprobante();
    updateMetodoPago();
    updateCardPreview();
});
