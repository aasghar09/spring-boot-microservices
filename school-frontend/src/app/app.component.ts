import { Component, OnInit } from '@angular/core';
import { KeycloakService } from 'keycloak-angular';

@Component({
  selector: 'app-root',
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.scss']
})
export class AppComponent implements OnInit {
  username = '';
  roles: string[] = [];

  constructor(private keycloak: KeycloakService) {}

  async ngOnInit(): Promise<void> {
    try {
      const isLoggedIn = await this.keycloak.isLoggedIn();

      if (isLoggedIn) {
        const tokenParsed = this.keycloak.getKeycloakInstance().tokenParsed;
        
        console.log('Full token:', tokenParsed);

        this.username = tokenParsed?.['preferred_username'] 
                     || tokenParsed?.['email'] 
                     || '';
                     
        this.roles = tokenParsed?.['realm_access']?.['roles'] || [];

        console.log('Username:', this.username);
        console.log('Roles:', this.roles);
      }
    } catch (err) {
      console.error('Init error:', err);
    }
  }

  logout(): void {
    this.keycloak.logout('http://localhost:4200');
  }

  isAdmin(): boolean {
    return this.roles.includes('ADMIN');
  }
}