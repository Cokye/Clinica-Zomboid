import { Component, ChangeDetectionStrategy, inject } from '@angular/core';
import { RouterOutlet, RouterLink, Router } from '@angular/router';
import { CommonModule } from '@angular/common';
import { ConvenioService } from './services/convenio';

@Component({
  selector: 'app-root',
  standalone: true,
  imports: [CommonModule, RouterOutlet, RouterLink],
  templateUrl: './app.html',
  styleUrl: './app.css',
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class AppComponent {
  private router = inject(Router);
  protected convenioService = inject(ConvenioService);

  cerrarSesion(): void {
    this.convenioService.logout().subscribe({
      next: () => {
        this.convenioService.setRutSesion(null);
        this.router.navigate(['/']);
      },
      error: () => {
        this.convenioService.setRutSesion(null);
        this.router.navigate(['/']);
      }
    });
  }
}