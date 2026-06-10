import { Component, OnInit } from '@angular/core';
import { ApiService } from '../../services/api.service';
import { KeycloakService } from 'keycloak-angular';

@Component({
  selector: 'app-persons',
  templateUrl: './persons.component.html',
  styleUrls: ['./persons.component.scss']
})
export class PersonsComponent implements OnInit {

  // ── Table state ───────────────────────────────────────────
  persons: any[] = [];
  totalElements = 0;
  totalPages = 0;
  currentPage = 0;
  pageSize = 10;

  // ── UI state ──────────────────────────────────────────────
  loading = true;
  error = '';
  successMessage = '';

  // ── Create form state ─────────────────────────────────────
  showCreateForm = false;
  newPerson = {
    firstName: '',
    lastName: '',
    email: '',
    phone: '',
    keycloakId: ''
  };

  // ── Edit form state ───────────────────────────────────────
  showEditForm = false;
  editPerson: any = {
    id: null,
    firstName: '',
    lastName: '',
    email: '',
    phone: '',
    keycloakId: ''
  };

  // ── Search state ──────────────────────────────────────────
  searchKeyword = '';
  isSearchMode = false;   // true = showing search results, false = showing all persons

  // ── Security state ────────────────────────────────────────
  private roles: string[] = [];

  // ── Constructor — dependency injection ───────────────────
  constructor(
    private apiService: ApiService,
    private keycloak: KeycloakService
  ) {}

  // ── ngOnInit — fires once after component is created ─────
  ngOnInit(): void {
    const tokenParsed = this.keycloak.getKeycloakInstance().tokenParsed;
    this.roles = tokenParsed?.['realm_access']?.['roles'] || [];
    this.loadPersons();
  }

  // ── Security helper ───────────────────────────────────────
  isAdmin(): boolean {
    return this.roles.includes('ADMIN');
  }

  // ── Load all persons (normal paginated mode) ──────────────
  loadPersons(): void {
    this.loading = true;
    this.error = '';

    const self = this;
    const observable = this.apiService.getPersons(this.currentPage, this.pageSize);

    observable.subscribe({
      next: function(data: any) {
        self.persons = data?.content ?? [];
        self.totalElements = data?.totalElements ?? 0;
        self.totalPages = data?.totalPages ?? 0;
        self.loading = false;
      },
      error: function(err: any) {
        self.error = 'Failed to load persons. Is the backend running?';
        console.error(err);
        self.loading = false;
      }
    });
  }

  // ── Search persons by keyword ─────────────────────────────
  // Resets to page 0 on every new search
  searchPersons(): void {

    // if search box is empty — go back to normal mode
    if (!this.searchKeyword || this.searchKeyword.trim() === '') {
      this.clearSearch();
      return;
    }

    this.loading = true;
    this.error = '';
    this.isSearchMode = true;
    this.currentPage = 0;   // always start search results from page 1

    const self = this;
    const observable = this.apiService.searchPersons(
      this.searchKeyword.trim(),
      this.currentPage,
      this.pageSize
    );

    observable.subscribe({
      next: function(data: any) {
        self.persons = data?.content ?? [];
        self.totalElements = data?.totalElements ?? 0;
        self.totalPages = data?.totalPages ?? 0;
        self.loading = false;
      },
      error: function(err: any) {
        self.error = 'Search failed. Please try again.';
        console.error(err);
        self.loading = false;
      }
    });
  }

  // ── Clear search — return to full paginated list ──────────
  clearSearch(): void {
    this.searchKeyword = '';
    this.isSearchMode = false;
    this.currentPage = 0;
    this.loadPersons();
  }

  // ── Pagination — next page ────────────────────────────────
  // Works for both normal mode and search mode
  nextPage(): void {
    const isNotLastPage = this.currentPage < this.totalPages - 1;
    if (isNotLastPage) {
      this.currentPage = this.currentPage + 1;
      if (this.isSearchMode) {
        this.searchPersons();
      } else {
        this.loadPersons();
      }
    }
  }

  // ── Pagination — previous page ────────────────────────────
  prevPage(): void {
    const isNotFirstPage = this.currentPage > 0;
    if (isNotFirstPage) {
      this.currentPage = this.currentPage - 1;
      if (this.isSearchMode) {
        this.searchPersons();
      } else {
        this.loadPersons();
      }
    }
  }

  // ── Toggle create form visibility ─────────────────────────
  toggleCreateForm(): void {
    this.showCreateForm = !this.showCreateForm;
    this.showEditForm = false;   // close edit form if open
    this.successMessage = '';
    this.error = '';
  }

  // ── Create new person ─────────────────────────────────────
  createPerson(): void {
    const self = this;
    const observable = this.apiService.createPerson(this.newPerson);

    observable.subscribe({
      next: function(response: any) {
        self.successMessage = 'Person created successfully.';
        self.newPerson = {
          firstName: '', lastName: '', email: '', phone: '', keycloakId: ''
        };
        self.showCreateForm = false;
        self.loadPersons();
      },
      error: function(err: any) {
        self.error = 'Failed to create person. Check all required fields.';
        console.error(err);
      }
    });
  }

  // ── Open edit form — pre-populate with selected person ────
  // This is called when user clicks Edit button on a table row
  openEditForm(person: any): void {
    // copy person data into editPerson object
    // we copy field by field — never assign the object directly
    // reason: direct assignment would make editPerson point to the
    // same object in the table — editing would modify the table live
    this.editPerson = {
      id:          person.id,
      firstName:   person.firstName,
      lastName:    person.lastName,
      email:       person.email,
      phone:       person.phone        || '',
      keycloakId:  person.keycloakId   || ''
    };
    this.showEditForm = true;
    this.showCreateForm = false;   // close create form if open
    this.successMessage = '';
    this.error = '';
  }

  // ── Close edit form ───────────────────────────────────────
  closeEditForm(): void {
    this.showEditForm = false;
    this.editPerson = {
      id: null, firstName: '', lastName: '', email: '', phone: '', keycloakId: ''
    };
  }

  // ── Save edited person — calls PUT /api/persons/{id} ──────
  updatePerson(): void {

    // guard — if somehow id is null, stop here
    if (this.editPerson.id === null) {
      this.error = 'Cannot update — person ID is missing.';
      return;
    }
    const self = this;
    const observable = this.apiService.updatePerson(this.editPerson.id, this.editPerson);

    observable.subscribe({
      next: function(response: any) {
        self.successMessage = 'Person updated successfully.';
        self.showEditForm = false;
        self.loadPersons();   // refresh table with updated data
      },
      error: function(err: any) {
        self.error = 'Failed to update person. Check all required fields.';
        console.error(err);
      }
    });
  }

  // ── Delete person by ID ───────────────────────────────────
  deletePerson(id: number): void {
    const userConfirmed = confirm('Are you sure you want to delete this person?');
    if (userConfirmed === false) { return; }

    const self = this;
    const observable = this.apiService.deletePerson(id);

    observable.subscribe({
      next: function(response: any) {
        self.successMessage = 'Person deleted successfully.';
        self.loadPersons();
      },
      error: function(err: any) {
        self.error = 'Failed to delete person.';
        console.error(err);
      }
    });
  }
}