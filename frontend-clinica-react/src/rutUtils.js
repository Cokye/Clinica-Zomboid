// Formatea automáticamente mientras se escribe (ej: 123456789 -> 12345678-9)
export const formatearRut = (valor) => {
  let limpio = valor.replace(/[^0-9kK]/g, '').toUpperCase();

  if (limpio.length <= 1) return limpio;

  const cuerpo = limpio.slice(0, -1);
  const dv = limpio.slice(-1);

  return `${cuerpo}-${dv}`;
};

// Valida únicamente presencia del guion, caracteres y longitud (8 o 9 dígitos en total)
export const validarFormatoRut = (rutCompleto) => {
  if (!rutCompleto || !rutCompleto.trim()) {
    return { valido: false, mensaje: 'El RUT es obligatorio' };
  }

  // Requiere 7 u 8 dígitos antes del guion y exactamente 1 dígito o K después
  const regex = /^\d{7,8}-[\dkK]$/;
  if (!regex.test(rutCompleto)) {
    return {
      valido: false,
      mensaje: 'El RUT debe incluir guion y tener 8 o 9 dígitos en total (ej: 12345678-9)'
    };
  }

  return { valido: true, mensaje: '' };
};