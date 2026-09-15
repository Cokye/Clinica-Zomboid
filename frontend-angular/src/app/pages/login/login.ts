import { Component, ChangeDetectionStrategy, inject, ChangeDetectorRef } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Router, RouterLink } from '@angular/router';
import { ConvenioService, NuevoUsuario } from '../../services/convenio';

@Component({
  selector: 'app-login',
  standalone: true,
  imports: [CommonModule, FormsModule, RouterLink],
  templateUrl: './login.html',
  styleUrl: './login.css',
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class Login {
  private convenioService = inject(ConvenioService);
  private router = inject(Router);
  private cdr = inject(ChangeDetectorRef);

  modoRegistro: boolean = false;

  // Login
  rutBusqueda: string = '';
  passwordLogin: string = '';
  errorLogin: string = '';
  mensajeExito: string = '';
  cargando: boolean = false;

  // Registro
  nuevoNombre: string = '';
  nuevoApellido: string = '';
  nuevoEdad: number | null = null;
  nuevoCorreo: string = '';
  nuevoPassword: string = '';

  formatearRut(valor: string): string {
    const limpio = valor.replace(/[^0-9kK]/g, '').toUpperCase();
    if (limpio.length <= 1) return limpio;
    const cuerpo = limpio.slice(0, -1);
    const dv = limpio.slice(-1);
    return `${cuerpo}-${dv}`;
  }

  onRutBusquedaChange(valor: string): void {
    this.rutBusqueda = this.formatearRut(valor);
    this.errorLogin = '';
    this.mensajeExito = '';
    this.cdr.markForCheck();
  }

  toggleModo(): void {
    this.modoRegistro = !this.modoRegistro;
    this.errorLogin = '';
    this.mensajeExito = '';
    this.passwordLogin = '';
    this.nuevoPassword = '';
    this.cdr.markForCheck();
  }

  iniciarSesion(): void {
    const rutLimpio = this.rutBusqueda.trim();
    if (!/^\d{7,8}-[\dkK]$/.test(rutLimpio)) {
      this.errorLogin = 'Ingresa un RUT válido (ej: 12345678-9)';
      return;
    }
    if (!this.passwordLogin.trim()) {
      this.errorLogin = 'Ingresa tu contraseña';
      return;
    }

    this.cargando = true;
    this.errorLogin = '';

    this.convenioService.login(rutLimpio, this.passwordLogin.trim()).subscribe({
      next: (res) => {
        this.convenioService.setRutSesion(res.rut || rutLimpio);
        this.router.navigate(['/dashboard']);
      },
      error: (err) => {
        this.errorLogin = err.error?.mensaje || 'RUT o contraseña incorrectos.';
        this.cargando = false;
        this.cdr.markForCheck();
      }
    });
  }

  registrarUsuario(): void {
    const rutLimpio = this.rutBusqueda.trim();
    if (!/^\d{7,8}-[\dkK]$/.test(rutLimpio)) {
      this.errorLogin = 'Ingresa un RUT válido (ej: 12345678-9)';
      return;
    }
    if (!this.nuevoNombre.trim() || !this.nuevoApellido.trim()) {
      this.errorLogin = 'Nombre y apellido son obligatorios';
      return;
    }
    if (!this.nuevoPassword.trim()) {
      this.errorLogin = 'Debes ingresar una contraseña';
      return;
    }

    this.cargando = true;
    this.errorLogin = '';

    const nuevoPaciente: NuevoUsuario = {
      rut: rutLimpio,
      nombre: this.nuevoNombre.trim(),
      apellido: this.nuevoApellido.trim(),
      edad: this.nuevoEdad || undefined,
      correo: this.nuevoCorreo.trim() || undefined,
      password: this.nuevoPassword.trim()
    };

    this.convenioService.registrarPaciente(nuevoPaciente).subscribe({
      next: () => {
        // Inicia sesión automáticamente con la clave ingresada
        this.convenioService.login(rutLimpio, this.nuevoPassword.trim()).subscribe({
          next: () => {
            this.convenioService.setRutSesion(rutLimpio);
            this.router.navigate(['/dashboard']);
          },
          error: () => {
            this.modoRegistro = false;
            this.passwordLogin = this.nuevoPassword;
            this.mensajeExito = 'Registro exitoso. Presiona entrar.';
            this.cargando = false;
            this.cdr.markForCheck();
          }
        });
      },
      error: (err) => {
        this.errorLogin = err.error?.mensaje || 'Error al registrar el paciente.';
        this.cargando = false;
        this.cdr.markForCheck();
      }
    });
  }
}