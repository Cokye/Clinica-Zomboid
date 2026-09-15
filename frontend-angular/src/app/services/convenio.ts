import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

export interface Convenio {
  key: string;
  nombre: string;
  descripcion?: string;
  ahorro?: number;
  precioMensual?: number;
}

export interface AfiliacionResponse {
  nombreConvenio: string;
  ahorro: number;
  titular: string;
  beneficiarios: string[];
}

export interface NuevoUsuario {
  rut: string;
  nombre: string;
  apellido: string;
  edad?: number;
  correo?: string;
  password?: string;
}

export interface AfiliacionRequest {
  rutTitular: string;
  convenioKey: string;
  beneficiarios: string[];
  nombre?: string;
  apellido?: string;
  edad?: number;
  correo?: string;
  nuevasCargas?: NuevoUsuario[];
}

@Injectable({
  providedIn: 'root'
})
export class ConvenioService {
  private http = inject(HttpClient);
  private apiUrl = 'http://localhost:8080/api/convenios';

  // --- Manejo de Sesión ---
  private rutAutenticado: string | null = sessionStorage.getItem('rut_usuario');

  setRutSesion(rut: string | null): void {
    this.rutAutenticado = rut;
    if (rut) {
      sessionStorage.setItem('rut_usuario', rut);
    } else {
      sessionStorage.removeItem('rut_usuario');
    }
  }

  getRutSesion(): string | null {
    return this.rutAutenticado;
  }

  estaAutenticado(): boolean {
    return this.rutAutenticado !== null && this.rutAutenticado.trim().length > 0;
  }

  login(rut: string, password: string): Observable<any> {
    return this.http.post<any>(
      'http://localhost:8080/api/auth/login',
      { rut, password },
      { withCredentials: true }
    );
  }

  logout(): Observable<any> {
    return this.http.post<any>(
      'http://localhost:8080/api/auth/logout',
      {},
      { withCredentials: true }
    );
  }

  // --- Endpoints Convenios y Pacientes ---
  getPlanes(): Observable<Convenio[]> {
    return this.http.get<Convenio[]>(this.apiUrl);
  }

  getAfiliacionPorRut(rut: string): Observable<AfiliacionResponse> {
    return this.http.get<AfiliacionResponse>(`${this.apiUrl}/afiliacion/${rut}`);
  }

  getPacientePorRut(rut: string): Observable<any> {
    return this.http.get<any>(`http://localhost:8080/api/pacientes/${rut}`);
  }

  afiliar(request: AfiliacionRequest): Observable<any> {
    return this.http.post<any>(`${this.apiUrl}/afiliar`, request);
  }

  desvincularCarga(rutCarga: string): Observable<any> {
    return this.http.delete<any>(`${this.apiUrl}/afiliacion/carga?rutCarga=${rutCarga}`);
  }

  registrarPaciente(paciente: NuevoUsuario): Observable<any> {
    return this.http.post<any>('http://localhost:8080/api/pacientes/registrar', paciente);
  }
}