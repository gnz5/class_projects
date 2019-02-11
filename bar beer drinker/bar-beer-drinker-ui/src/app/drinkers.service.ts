import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';

export interface Drinker {
  name: string;
  address: string;
  city: string;
  state: string;
  phone: number;
}

@Injectable({
  providedIn: 'root'
})
export class DrinkersService {

  constructor(private http: HttpClient) {}

  getDrinkers() {
    return this.http.get<Drinker[]>('/api/drinker');
  }

  getDrinker(name: string) {
    return this.http.get<Drinker>(`/api/drinker/${name}`);
  }

  getDrinkerTransactions(name: string) {
    const url = '/api/getDrinkerTransactions/' + name;
    return this.http.get<any>(encodeURI(url));
  }

  getDrinkerTopBeers(name: string) {
    const url = '/api/getDrinkerTopBeers/' + name;
    return this.http.get<any>(encodeURI(url));
  }

  getDrinkerBarSpending(name: string) {
    const url = '/api/getDrinkerBarSpending/' + name;
    return this.http.get<any>(encodeURI(url));
  }
}
