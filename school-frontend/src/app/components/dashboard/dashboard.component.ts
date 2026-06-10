import { Component, OnInit } from '@angular/core';
import { ApiService } from '../../services/api.service';

@Component({
  selector: 'app-dashboard',
  templateUrl: './dashboard.component.html',
  styleUrls: ['./dashboard.component.scss']
})
export class DashboardComponent implements OnInit {

  totalPersons = 0;
  totalCourses = 0;
  totalEnrollments = 0;
  loading = true;
  error = '';

  constructor(private apiService: ApiService) {}

  ngOnInit(): void {
    this.loadCounts();
  }

  loadCounts(): void {
    this.loading = true;

    this.apiService.getPersonsCount().subscribe({
      next: (data) => { this.totalPersons = data.totalElements || 0; },
      error: (err) => { this.error = 'Failed to load data. Is the backend running?'; console.error(err); }
    });

    this.apiService.getCoursesCount().subscribe({
      next: (data) => { this.totalCourses = data.totalElements || 0; },
      error: (err) => { console.error(err); }
    });

    this.apiService.getEnrollmentsCount().subscribe({
      next: (data) => { this.totalEnrollments = data.totalElements || 0; this.loading = false; },
      error: (err) => { console.error(err); this.loading = false; }
    });
  }
}