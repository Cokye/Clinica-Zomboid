import React, { useState, useEffect } from 'react';
import './App.css';
import { formatearRut, validarFormatoRut } from './rutUtils';

function App() {
  const [mostrarModal, setMostrarModal] = useState(false);

  // Catálogo dinámico cargado desde ArangoDB
  const [catalogoEspecialidades, setCatalogoEspecialidades] = useState([]);

  // Estados del paciente
  const [rut, setRut] = useState('');
  const [paciente, setPaciente] = useState(null);
  const [cotizacion, setCotizacion] = useState(null);
  const [error, setError] = useState('');
  const [cargando, setCargando] = useState(false);

  // Estados de la cita médica
  const [fecha, setFecha] = useState('');
  const [especialidad, setEspecialidad] = useState('');
  const [doctor, setDoctor] = useState('');

  // Fecha mínima permitida en el calendario (hoy)
  const hoyStr = new Date().toISOString().split('T')[0];

  useEffect(() => {
    fetch('http://localhost:8080/api/especialidades')
      .then((res) => {
        if (!res.ok) throw new Error('No se pudo cargar el catálogo médico');
        return res.json();
      })
      .then((data) => setCatalogoEspecialidades(data))
      .catch((err) => console.error('Error al conectar con especialidades:', err));
  }, []);

  const handleAbrirModal = () => {
    setMostrarModal(true);
    setRut('');
    setPaciente(null);
    setCotizacion(null);
    setFecha('');
    setEspecialidad('');
    setDoctor('');
    setError('');
  };

  const handleCerrarModal = () => {
    cerrarSesionReact();
    setMostrarModal(false);
  };

  // Al presionar el botón verde "Confirmar y Guardar Cita"
  const handleConfirmarCita = async () => {
    alert(`¡Cita médica confirmada con éxito para el ${fecha} con ${doctor}! Total: $${cotizacion.totalAPagar.toLocaleString('es-CL')}`);
    
    // Cerramos sesión, limpiamos la cookie y cerramos el modal
    await cerrarSesionReact();
    setMostrarModal(false);
  };

  const handleRutChange = (e) => {
    setError('');
    const formateado = formatearRut(e.target.value);
    if (formateado.length <= 10) {
      setRut(formateado);
    }
  };

  const handleEspecialidadChange = (e) => {
    setEspecialidad(e.target.value);
    setDoctor('');
  };

  const consultarPaciente = async (e) => {
    e.preventDefault();
    setError('');
    setPaciente(null);
    setCotizacion(null);

    const chequeo = validarFormatoRut(rut);
    if (!chequeo.valido) {
      setError(chequeo.mensaje);
      return;
    }

    setCargando(true);
    try {
      //  Intentar autenticar / verificar existencia
      const authRes = await fetch('http://localhost:8080/api/auth/login', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({ rut }),
        credentials: 'include'
      });

      if (!authRes.ok) {
        let mensaje = 'Paciente no registrado en el sistema';
        try {
          const errorJson = await authRes.json();
          if (errorJson && typeof errorJson.mensaje === 'string') {
            mensaje = errorJson.mensaje;
          } else if (errorJson && typeof errorJson.error === 'string') {
            mensaje = errorJson.error;
          }
        } catch {
          // Si la respuesta no es JSON válido
        }
        throw new Error(mensaje);
      }

      // Obtiene los datos del paciente
      const response = await fetch(`http://localhost:8080/api/pacientes/${rut}`, {
        credentials: 'include'
      });

      if (!response.ok) {
        if (response.status === 404) {
          throw new Error('Paciente no registrado en el sistema');
        }
        throw new Error('Error al cargar la información del paciente');
      }

      const data = await response.json();
      
      // Validación estricta: que data realmente traiga información
      if (!data || Object.keys(data).length === 0) {
        throw new Error('No se encontraron registros para este RUT');
      }

      setPaciente(data);

    } catch (err) {
      setPaciente(null);
      setCotizacion(null);
      setError(typeof err === 'string' ? err : (err?.message || 'Paciente no registrado en el sistema'));
    } finally {
      setCargando(false);
    }
  };

  const obtenerCotizacion = async () => {
    if (!fecha) {
      setError('Debes seleccionar una fecha para la atención');
      return;
    }
    if (!especialidad) {
      setError('Debes seleccionar una especialidad médica');
      return;
    }
    if (!doctor) {
      setError('Debes seleccionar un médico tratante');
      return;
    }

    setCargando(true);
    setError('');
    //Funcion donde se manda a realizar la cotizacion
    try {
      const url = `http://localhost:8080/api/pacientes/${paciente.rut}/cotizacion?especialidad=${especialidad}`;
      const response = await fetch(url, {
        credentials: 'include'
      });
      if (!response.ok) throw new Error('No se pudo calcular la cotización del servicio');
      const data = await response.json();
      setCotizacion(data);
    } catch (err) {
      setError(typeof err === 'string' ? err : (err?.message || 'Error al obtener cotización'));
    } finally {
      setCargando(false);
    }
  };

  const cerrarSesionReact = async () => {
    try {
      // Pide al backend que destruya la cookie HttpOnly
      await fetch('http://localhost:8080/api/auth/logout', {
        method: 'POST',
        credentials: 'include' 
      });
    } catch (err) {
      console.error('Error al cerrar sesión:', err);
    } finally {
      //  Limpia el localStorage por si quedaron rastros viejos
      localStorage.removeItem('token');
      localStorage.removeItem('rut');
      localStorage.clear();

      //  Resetea los estados de la interfaz
      setPaciente(null);
      setCotizacion(null);
      setRut('');
      setFecha('');
      setEspecialidad('');
      setDoctor('');
      setError('');
    }
  };

  const espSeleccionada = catalogoEspecialidades.find((esp) => esp.id === especialidad);

  // Determinar paso actual para el stepper visual
  const pasoActual = cotizacion ? 3 : paciente ? 2 : 1;

  return (
    <div className="clinica-container">
      {/* Header / Navbar */}
      <header className="clinica-header">
        <div className="header-inner">
          <div className="brand-zone">
            <div className="brand-icon-box">
              <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2.5">
                <path d="M12 4v16m8-8H4" strokeLinecap="round" strokeLinejoin="round"/>
              </svg>
            </div>
            <div>
              <h1>Clínica Zomboid</h1>
              <p>Atención médica especializada para tu supervivencia diaria</p>
            </div>
          </div>
          <div className="header-badge">
            <span className="dot-live"></span> Sistema de Reserva Online
          </div>
        </div>
      </header>

      {/* Seccion "hero" */}
      <main className="clinica-content">
        <div className="hero-card">
          <span className="hero-pill">Agendamiento Inmediato</span>
          <h2>Atención médica rápida, segura y con descuentos por convenio</h2>
          <p>
            Ingresa con tu RUT para verificar automáticamente tus beneficios de salud, 
            seleccionar médico especialista y reservar tu consulta.
          </p>
          <button className="btn-registrar" onClick={handleAbrirModal}>
            <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2">
              <rect x="3" y="4" width="18" height="18" rx="2" ry="2"></rect>
              <line x1="16" y1="2" x2="16" y2="6"></line>
              <line x1="8" y1="2" x2="8" y2="6"></line>
              <line x1="3" y1="10" x2="21" y2="10"></line>
            </svg>
            Registrar Nueva Cita
          </button>
        </div>
      </main>

      {/* Modal de Agendamiento */}
      {mostrarModal && (
        <div className="modal-overlay" onClick={(e) => e.target === e.currentTarget && handleCerrarModal()}>
          <div className="modal-content">
            <button className="btn-cerrar" onClick={handleCerrarModal} aria-label="Cerrar modal">&times;</button>

            {/* Stepper visual */}
            <div className="stepper-bar">
              <div className={`step-item ${pasoActual >= 1 ? 'step-active' : ''}`}>
                <span className="step-num">1</span>
                <span className="step-label">RUT</span>
              </div>
              <div className="step-divider"></div>
              <div className={`step-item ${pasoActual >= 2 ? 'step-active' : ''}`}>
                <span className="step-num">2</span>
                <span className="step-label">Consulta</span>
              </div>
              <div className="step-divider"></div>
              <div className={`step-item ${pasoActual >= 3 ? 'step-active' : ''}`}>
                <span className="step-num">3</span>
                <span className="step-label">Cotización</span>
              </div>
            </div>

            {/* Paso 1: Ingreso de RUT */}
            {!paciente && (
              <div className="paso-container">
                <div className="modal-title-group">
                  <h2>Identificación del Paciente</h2>
                  <p>Ingresa tu RUT sin puntos y con guion verificador.</p>
                </div>

                <form onSubmit={consultarPaciente} className="modal-form">
                  <div className="input-group">
                    <label>RUT DEL PACIENTE</label>
                    <input
                      type="text"
                      className="input-field"
                      placeholder="Ej: 24555666-7"
                      value={rut}
                      onChange={handleRutChange}
                      maxLength={10}
                      autoFocus
                    />
                  </div>

                  <button type="submit" className="btn-submit" disabled={cargando}>
                    {cargando ? 'Verificando en sistema...' : 'Continuar'}
                  </button>
                </form>
              </div>
            )}

            {error && (
              <div className="mensaje-error">
                <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2">
                  <circle cx="12" cy="12" r="10"></circle>
                  <line x1="12" y1="8" x2="12" y2="12"></line>
                  <line x1="12" y1="16" x2="12.01" y2="16"></line>
                </svg>
                <span>{String(error)}</span>
              </div>
            )}

            {/* Paso 2: Configurar Cita */}
            {paciente && !cotizacion && (
              <div className="resultado-paciente">
                <div className="paciente-badge">
                  <span className="badge-avatar">👤</span>
                  <div>
                    <h4>{paciente.nombreCompleto}</h4>
                    <span className="badge-sub">{paciente.rut} &bull; {paciente.correo || 'Sin correo'}</span>
                  </div>
                </div>

                <div className="campos-agendamiento">
                  <div className="input-group">
                    <label>Fecha de atención</label>
                    <input
                      type="date"
                      className="input-field"
                      min={hoyStr}
                      value={fecha}
                      onChange={(e) => setFecha(e.target.value)}
                    />
                  </div>

                  <div className="input-group">
                    <label>Especialidad médica</label>
                    <select className="select-field" value={especialidad} onChange={handleEspecialidadChange}>
                      <option value="">-- Selecciona Especialidad --</option>
                      {catalogoEspecialidades.map((esp) => (
                        <option key={esp.id} value={esp.id}>
                          {esp.nombre} (${esp.precioBase.toLocaleString('es-CL')})
                        </option>
                      ))}
                    </select>
                  </div>

                  <div className="input-group">
                    <label>Médico tratante</label>
                    <select
                      className="select-field"
                      value={doctor}
                      onChange={(e) => setDoctor(e.target.value)}
                      disabled={!especialidad}
                    >
                      <option value="">-- Selecciona Doctor --</option>
                      {espSeleccionada &&
                        espSeleccionada.doctores.map((doc) => (
                          <option key={doc.id || doc.nombre} value={doc.nombre}>
                            {doc.nombre}
                          </option>
                        ))}
                    </select>
                  </div>
                </div>

                <button
                  className="btn-submit"
                  onClick={obtenerCotizacion}
                  disabled={cargando}
                >
                  {cargando ? 'Calculando valores...' : 'Continuar y Cotizar'}
                </button>
              </div>
            )}

            {/* Paso 3: Desglose y Cobro */}
            {cotizacion && (
              <div className="detalle-cotizacion">
                <div className="resumen-box">
                  <h3>Resumen de Reserva</h3>
                  <div className="resumen-grid">
                    <div className="resumen-item">
                      <span className="resumen-label">Paciente</span>
                      <span className="resumen-val">{paciente.nombreCompleto}</span>
                    </div>
                    <div className="resumen-item">
                      <span className="resumen-label">Fecha</span>
                      <span className="resumen-val">{fecha}</span>
                    </div>
                    <div className="resumen-item">
                      <span className="resumen-label">Especialidad</span>
                      <span className="resumen-val">{espSeleccionada ? espSeleccionada.nombre : especialidad}</span>
                    </div>
                    <div className="resumen-item">
                      <span className="resumen-label">Médico</span>
                      <span className="resumen-val">{doctor}</span>
                    </div>
                  </div>
                </div>

                {/* Banner de Convenio si aplica */}
                <div className="convenio-status-card">
                  <div>
                    <span className="convenio-title">Convenio: {cotizacion.nombreConvenio}</span>
                    <span className="convenio-desc">Descuento aplicado sobre el arancel clínico</span>
                  </div>
                  <span className="badge-ahorro">{cotizacion.porcentajeDescuento}% DCTO</span>
                </div>

                {/* Liquidación de Pago */}
                <div className="desglose-pago">
                  <div className="fila-pago">
                    <span>Arancel consulta base:</span>
                    <span>${cotizacion.precioBase.toLocaleString('es-CL')}</span>
                  </div>
                  <div className="fila-pago fila-descuento">
                    <span>Descuento por plan:</span>
                    <span>-${cotizacion.montoDescuento.toLocaleString('es-CL')}</span>
                  </div>
                  <div className="fila-total">
                    <span>Total a pagar:</span>
                    <span className="total-monto">${cotizacion.totalAPagar.toLocaleString('es-CL')}</span>
                  </div>
                </div>

                <button
                  className="btn-submit btn-confirmar"
                  onClick={handleConfirmarCita}
                >
                  Confirmar y Guardar Cita
                </button>
              </div>
            )}
          </div>
        </div>
      )}
    </div>
  );
}

export default App;