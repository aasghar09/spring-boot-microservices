import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../environments/environment';

@Injectable({
  providedIn: 'root'
})
export class ApiService {

  private baseUrl = environment.apiBaseUrl;

  constructor(private http: HttpClient) {}

  // ── Dashboard counts ──────────────────────────────────────
  getPersonsCount(): Observable<any> {
    return this.http.get<any>(`${this.baseUrl}/api/persons?page=0&size=1`);
  }
  getCoursesCount(): Observable<any> {
    return this.http.get<any>(`${this.baseUrl}/api/courses?page=0&size=1`);
  }
  getEnrollmentsCount(): Observable<any> {
    return this.http.get<any>(`${this.baseUrl}/api/enrollments?page=0&size=1`);
  }

  // ── Persons — CRUD ────────────────────────────────────────
  getPersons(page: number, size: number): Observable<any> {
    return this.http.get<any>(
      `${this.baseUrl}/api/persons?page=${page}&size=${size}&sort=firstName,asc`
    );
  }

  getPersonById(id: number): Observable<any> {
    return this.http.get<any>(`${this.baseUrl}/api/persons/${id}`);
  }

  createPerson(person: any): Observable<any> {
    return this.http.post<any>(`${this.baseUrl}/api/persons`, person);
  }

  // PUT — update existing person by ID
  updatePerson(id: number, person: any): Observable<any> {
    return this.http.put<any>(`${this.baseUrl}/api/persons/${id}`, person);
  }

  deletePerson(id: number): Observable<any> {
    return this.http.delete<any>(`${this.baseUrl}/api/persons/${id}`);
  }

  // ── Persons — Search ──────────────────────────────────────
  // keyword searches across firstName, lastName, email simultaneously
  searchPersons(keyword: string, page: number, size: number): Observable<any> {
    return this.http.get<any>(
      `${this.baseUrl}/api/persons/search?keyword=${keyword}&page=${page}&size=${size}&sort=firstName,asc`
    );
  }
}