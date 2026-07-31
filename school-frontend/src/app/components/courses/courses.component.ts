import { Component, OnInit } from '@angular/core';
import { ApiService } from '../../services/api.service';
import { KeycloakService } from 'keycloak-angular';

@Component({
  selector: 'app-courses',
  templateUrl: './courses.component.html',
  styleUrls: ['./courses.component.scss']
})
export class CoursesComponent implements OnInit {

  // ── Table state ───────────────────────────────────────────
  courses: any[] = [];
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
  newCourse = {
    courseName: '',
    description: '',
    credits: 1,
    instructorName: ''
  };

  // ── Edit form state ───────────────────────────────────────
  showEditForm = false;
  editCourse: any = {
    id: null,
    courseName: '',
    description: '',
    credits: 1,
    instructorName: ''
  };

  // ── Search state ──────────────────────────────────────────
  searchKeyword = '';
  isSearchMode = false;   // true = showing search results, false = showing all courses

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
    this.loadCourses();
  }

  // ── Security helper ───────────────────────────────────────
  isAdmin(): boolean {
    return this.roles.includes('ADMIN');
  }

  // ── Load all courses (normal paginated mode) ──────────────
  loadCourses(): void {
    this.loading = true;
    this.error = '';

    const self = this;
    const observable = this.apiService.getCourses(this.currentPage, this.pageSize);

    observable.subscribe({
      next: function(data: any) {
        self.courses = data?.content ?? [];
        self.totalElements = data?.totalElements ?? 0;
        self.totalPages = data?.totalPages ?? 0;
        self.loading = false;
      },
      error: function(err: any) {
        self.error = 'Failed to load courses. Is the backend running?';
        console.error(err);
        self.loading = false;
      }
    });
  }

  // ── Search courses by keyword ─────────────────────────────
  searchCourses(): void {
    if (!this.searchKeyword || this.searchKeyword.trim() === '') {
      this.clearSearch();
      return;
    }

    this.loading = true;
    this.error = '';
    this.isSearchMode = true;
    this.currentPage = 0;

    const self = this;
    const observable = this.apiService.searchCourses(
      this.searchKeyword.trim(),
      this.currentPage,
      this.pageSize
    );

    observable.subscribe({
      next: function(data: any) {
        self.courses = data?.content ?? [];
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
    this.loadCourses();
  }

  // ── Pagination — next page ────────────────────────────────
  nextPage(): void {
    const isNotLastPage = this.currentPage < this.totalPages - 1;
    if (isNotLastPage) {
      this.currentPage = this.currentPage + 1;
      if (this.isSearchMode) {
        this.searchCourses();
      } else {
        this.loadCourses();
      }
    }
  }

  // ── Pagination — previous page ────────────────────────────
  prevPage(): void {
    const isNotFirstPage = this.currentPage > 0;
    if (isNotFirstPage) {
      this.currentPage = this.currentPage - 1;
      if (this.isSearchMode) {
        this.searchCourses();
      } else {
        this.loadCourses();
      }
    }
  }

  // ── Toggle create form visibility ─────────────────────────
  toggleCreateForm(): void {
    this.showCreateForm = !this.showCreateForm;
    this.showEditForm = false;
    this.successMessage = '';
    this.error = '';
  }

  // ── Create new course ─────────────────────────────────────
  createCourse(): void {
    const self = this;
    const observable = this.apiService.createCourse(this.newCourse);

    observable.subscribe({
      next: function(response: any) {
        self.successMessage = 'Course created successfully.';
        self.newCourse = {
          courseName: '', description: '', credits: 1, instructorName: ''
        };
        self.showCreateForm = false;
        self.loadCourses();
      },
      error: function(err: any) {
        self.error = 'Failed to create course. Check all required fields.';
        console.error(err);
      }
    });
  }

  // ── Open edit form — pre-populate with selected course ────
  openEditForm(course: any): void {
    this.editCourse = {
      id:             course.id,
      courseName:     course.courseName,
      description:    course.description,
      credits:        course.credits,
      instructorName: course.instructorName
    };
    this.showEditForm = true;
    this.showCreateForm = false;
    this.successMessage = '';
    this.error = '';
  }

  // ── Close edit form ───────────────────────────────────────
  closeEditForm(): void {
    this.showEditForm = false;
    this.editCourse = {
      id: null, courseName: '', description: '', credits: 1, instructorName: ''
    };
  }

  // ── Save edited course — calls PUT /api/courses/{id} ──────
  updateCourse(): void {
    if (this.editCourse.id === null) {
      this.error = 'Cannot update — course ID is missing.';
      return;
    }
    const self = this;
    const observable = this.apiService.updateCourse(this.editCourse.id, this.editCourse);

    observable.subscribe({
      next: function(response: any) {
        self.successMessage = 'Course updated successfully.';
        self.showEditForm = false;
        self.loadCourses();
      },
      error: function(err: any) {
        self.error = 'Failed to update course. Check all required fields.';
        console.error(err);
      }
    });
  }

  // ── Delete course by ID ───────────────────────────────────
  deleteCourse(id: number): void {
    const userConfirmed = confirm('Are you sure you want to delete this course?');
    if (userConfirmed === false) { return; }

    const self = this;
    const observable = this.apiService.deleteCourse(id);

    observable.subscribe({
      next: function(response: any) {
        self.successMessage = 'Course deleted successfully.';
        self.loadCourses();
      },
      error: function(err: any) {
        self.error = 'Failed to delete course.';
        console.error(err);
      }
    });
  }
}