import { ChangeDetectorRef, Component, OnInit, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Router } from '@angular/router';
import { ConvenioService, Convenio, AfiliacionRequest } from '../../services/convenio';

@Component({
  selector: 'app-root',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './dashboard.html',
  styleUrls: ['./dashboard.css']
})
export class Dashboard implements OnInit {
  private convenioService = inject(ConvenioService);
  private router = inject(Router);
  private cdr = inject(ChangeDetectorRef); // arregla los problemas de no printar en pantalla

  // Lista visual de beneficiarios con etiqueta descriptiva
  beneficiariosUI: { rut: string; etiqueta: string }[] = [];
  
  // Acumulador de cargas que no existen en BD y debemos crear
  nuevasCargasParaCrear: { rut: string; nombre: string; apellido: string; edad: number; correo: string; password: string;}[] = [];

  // Estado temporal para cuando una carga no existe
  mostrarFormularioCarga: boolean = false;
  cargaTempRut: string = '';
  cargaTempNombre: string = '';
  cargaTempApellido: string = '';
  cargaTempEdad: number | null = null;
  cargaTempCorreo: string = '';
  cargaTempPassword: string = '';

  planes: Convenio[] = [];
  afiliacionActual: any = null;
  beneficiariosDetalle: { rut: string; nombreCompleto: string; correo: string }[] = [];
  mensaje: string = '';
  cargandoAfiliacion: boolean = false;

  // Sesión y Titular bloqueado al usuario logueado
  rutUsuarioLogueado: string = '';
  nombreTitularEncontrado: string = '';
  verificandoTitular: boolean = false;

  // Formulario de nueva afiliación
  planSeleccionado: string = '';
  nuevoBeneficiario: string = '';

  ngOnInit(): void {
    // Obtener el RUT autenticado
    this.rutUsuarioLogueado = this.convenioService.getRutSesion() || '';

    if (!this.rutUsuarioLogueado) {
      this.router.navigate(['/login']);
      return;
    }

    //  Cargar catálogo de planes y datos del usuario logueado
    this.cargarPlanes();
    this.cargarDatosTitularLogueado();
    this.cargarMiAfiliacion();
  }

  seleccionarPlan(item: any): void {
    if (!item) {
      this.planSeleccionado = '';
      this.cdr.markForCheck();
      return;
    }

    let clave = '';
    if (typeof item === 'object') {
      clave = item.key || item._key || item.id || '';
    } else {
      clave = String(item);
    }

    if (clave === 'null' || clave === 'undefined') {
      clave = '';
    }

    this.planSeleccionado = clave;
    this.cdr.markForCheck();
  }

  cargarPlanes(): void {
    this.convenioService.getPlanes().subscribe({
      next: (data) => {
        this.planes = data;
        this.cdr.markForCheck();
      },
      error: (err) => console.error('Error al cargar planes:', err)
    });
  }

  // Carga los datos del propio usuario logueado para mostrar en el formulario
  cargarDatosTitularLogueado(): void {
    this.verificandoTitular = true;
    this.convenioService.getPacientePorRut(this.rutUsuarioLogueado).subscribe({
      next: (usuario) => {
        this.nombreTitularEncontrado = usuario?.nombreCompleto || `${usuario?.nombre || ''} ${usuario?.apellido || ''}`.trim() || this.rutUsuarioLogueado;
        this.verificandoTitular = false;
        this.cdr.markForCheck();
      },
      error: () => {
        this.nombreTitularEncontrado = this.rutUsuarioLogueado;
        this.verificandoTitular = false;
        this.cdr.markForCheck();
      }
    });
  }

  // Consulta únicamente la afiliación del usuario en sesión y resuelve los datos de sus cargas
  cargarMiAfiliacion(): void {
    this.cargandoAfiliacion = true;
    this.mensaje = '';
    this.afiliacionActual = null;
    this.beneficiariosDetalle = [];

    this.convenioService.getAfiliacionPorRut(this.rutUsuarioLogueado).subscribe({
      next: (data) => {
        if (!data || !data.nombreConvenio) {
          this.afiliacionActual = null;
          this.mensaje = 'Actualmente no cuentas con un convenio de salud activo.';
          this.cargandoAfiliacion = false;
          this.cdr.markForCheck();
          return;
        }

        this.afiliacionActual = data;
        this.cargandoAfiliacion = false;
        
        // Si tiene beneficiarios, buscar nombre y datos de cada uno
        if (data.beneficiarios && Array.isArray(data.beneficiarios)) {
          data.beneficiarios.forEach((rutCarga: string) => {
            this.convenioService.getPacientePorRut(rutCarga).subscribe({
              next: (p) => {
                const nombre = p?.nombreCompleto || `${p?.nombre || ''} ${p?.apellido || ''}`.trim() || 'Sin registrar';
                this.beneficiariosDetalle.push({
                  rut: rutCarga,
                  nombreCompleto: nombre,
                  correo: p?.correo || ''
                });
                this.cdr.markForCheck();
              },
              error: () => {
                this.beneficiariosDetalle.push({
                  rut: rutCarga,
                  nombreCompleto: 'Sin nombre registrado',
                  correo: ''
                });
                this.cdr.markForCheck();
              }
            });
          });
        }
        this.cdr.markForCheck();
      },
      error: () => {
        this.afiliacionActual = null;
        this.mensaje = 'Actualmente no cuentas con un convenio de salud activo.';
        this.cargandoAfiliacion = false;
        this.cdr.markForCheck();
      }
    });
  }

  desvincularse(): void {
    const rut = this.rutUsuarioLogueado;
    if (!rut) return;

    if (this.afiliacionActual?.titular === rut) {
      alert('Eres el titular del plan. Para anular el plan completo debes darlo de baja, no salirte como carga.');
      return;
    }

    if (!confirm(`¿Estás seguro de que deseas salirte del convenio familiar a nombre de ${this.afiliacionActual?.titular}?`)) {
      return;
    }

    this.convenioService.desvincularCarga(rut).subscribe({
      next: () => {
        alert('Te has desvinculado con éxito del convenio familiar. Ahora puedes contratar tu propio plan.');
        this.cargarMiAfiliacion();
      },
      error: (err) => {
        alert(err.error?.mensaje || 'Error al desvincularse');
        this.cdr.markForCheck();
      }
    });
  }

  // Formatea el input de la carga familiar agregando el guion automáticamente
  onBeneficiarioChange(valor: string): void {
    this.nuevoBeneficiario = this.formatearRutString(valor);
  }

  // Función reutilizable para limpiar caracteres extra y poner el guion
  formatearRutString(valor: string): string {
    let limpio = valor.replace(/[^0-9kK]/g, '').toUpperCase();
    if (limpio.length > 9) limpio = limpio.slice(0, 9);
    if (limpio.length <= 1) return limpio;
    return `${limpio.slice(0, -1)}-${limpio.slice(-1)}`;
  }

  agregarBeneficiario(): void {
    const rutCarga = this.nuevoBeneficiario.trim();
    if (!/^\d{7,8}-[\dkK]$/.test(rutCarga)) {
      alert('Ingresa un RUT válido para la carga (ej: 24555666-7)');
      return;
    }

    if (rutCarga === this.rutUsuarioLogueado) {
      alert('Tú como titular ya estás incluido automáticamente en el convenio.');
      return;
    }

    if (this.beneficiariosUI.some(b => b.rut === rutCarga)) {
      alert('Esta carga ya está en tu lista de beneficiarios.');
      return;
    }

    // Verificar si la carga existe en la BD
    this.convenioService.getPacientePorRut(rutCarga).subscribe({
      next: (paciente) => {
        const nombre = paciente?.nombreCompleto || `${paciente?.nombre || ''} ${paciente?.apellido || ''}`.trim() || rutCarga;
        this.beneficiariosUI.push({ rut: rutCarga, etiqueta: `${rutCarga} - ${nombre}` });
        this.nuevoBeneficiario = '';
        this.cdr.markForCheck();
      },
      error: () => {
        // No existe: abrimos el minifield para capturar sus datos
        this.cargaTempRut = rutCarga;
        this.cargaTempNombre = '';
        this.cargaTempApellido = '';
        this.cargaTempEdad = null;
        this.cargaTempCorreo = '';
        this.cargaTempPassword = '';
        this.mostrarFormularioCarga = true;
        this.cdr.markForCheck();
      }
    });
  }

  confirmarNuevaCarga(): void {
    if (!this.cargaTempNombre.trim() || !this.cargaTempApellido.trim() || !this.cargaTempPassword.trim()) {
      alert('Debes ingresar al menos el nombre, apellido de la carga y contraseña.');
      return;
    }

    this.nuevasCargasParaCrear.push({
      rut: this.cargaTempRut,
      nombre: this.cargaTempNombre.trim(),
      apellido: this.cargaTempApellido.trim(),
      edad: this.cargaTempEdad || 0,
      correo: this.cargaTempCorreo.trim(),
      password: this.cargaTempPassword.trim()
    });

    this.beneficiariosUI.push({
      rut: this.cargaTempRut,
      etiqueta: `${this.cargaTempRut} - ${this.cargaTempNombre} ${this.cargaTempApellido} (Nuevo)`
    });

    this.mostrarFormularioCarga = false;
    this.nuevoBeneficiario = '';
    this.cdr.markForCheck();
  }

  cancelarNuevaCarga(): void {
    this.mostrarFormularioCarga = false;
    this.cdr.markForCheck();
  }

  eliminarBeneficiario(index: number): void {
    const eliminado = this.beneficiariosUI[index];
    this.beneficiariosUI.splice(index, 1);
    this.nuevasCargasParaCrear = this.nuevasCargasParaCrear.filter(c => c.rut !== eliminado.rut);
    this.cdr.markForCheck();
  }

  contratarPlan(): void {
    const titular = this.rutUsuarioLogueado;
    const plan = (this.planSeleccionado || '').trim();

    if (!plan || plan === 'undefined' || plan == 'null') {
      alert('Debes seleccionar un plan antes de contratar.');
      return;
    }

    // El titular siempre se incluye en los beneficiarios (sin duplicados)
    const rutsBeneficiarios = Array.from(new Set([titular, ...this.beneficiariosUI.map(b => b.rut)]));

    // Armar el payload con el usuario logueado como titular indiscutido
    const payload: AfiliacionRequest = {
      rutTitular: titular,
      convenioKey: plan,
      beneficiarios: rutsBeneficiarios,
      nombre: '',
      apellido: '',
      edad: 0,
      correo: '',
      nuevasCargas: this.nuevasCargasParaCrear
    };

    this.convenioService.afiliar(payload).subscribe({
      next: () => {
        alert('¡Convenio contratado con éxito para ti y tus beneficiarios!');
        this.planSeleccionado = '';
        this.beneficiariosUI = [];
        this.nuevasCargasParaCrear = [];
        
        // Recargar tu información de inmediato
        this.cargarMiAfiliacion();
        this.cdr.markForCheck();
      },
      error: (err) => {
        alert('Error al contratar convenio: ' + (err.error?.mensaje || 'Error del servidor'));
        this.cdr.markForCheck();
      }
    });
  }

  cerrarSesion(): void {
    this.convenioService.logout().subscribe({
      next: () => {
        this.router.navigate(['/login']);
      },
      error: () => {
        this.router.navigate(['/login']);
      }
    });
  }
}