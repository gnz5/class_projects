import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';

@Injectable({
  providedIn: 'root'
})
export class ModificationService {

  constructor(public http: HttpClient) { }

  execute(query: string) {
    query = encodeURI(query);
    const url = '/api/modification/execute/' + query;
    return this.http.post<any[]>(url, {});
  }

  update(table: string, attribute_assignments: string, condition_on_tuples: string) {
    const url = '/api/modification/update/' + table + '/' + attribute_assignments + '/' + condition_on_tuples;
    return this.http.post<string>(url, {});
  }

  delete(table: string, condition: string) {
    const url = '/api/modification/delete/' + table + '/' + condition;
    return this.http.post<string>(url, {});
  }

  insert(table: string, tuples: string) {
    const url = '/api/modification/insert/' + table + '/' + tuples;
    return this.http.post<string>(url, {});
  }
}
