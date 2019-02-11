import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';

export interface BeerLocation {
  bar: string;
  price: number;
  customers: number;
}

@Injectable({
  providedIn: 'root'
})
export class BeersService {

  constructor(private http: HttpClient) { }

  getBeers() {
    return this.http.get<any[]>('/api/beer');
  }

  getBarsSelling(beer: string) {
    let url = '/api/bars-selling/' + encodeURI(beer);
    url = url.replace(/#/gi, '%23');
    return this.http.get<BeerLocation[]>(url);
  }

  getBeerManufacturers(beer?: string): any {
    if (beer) {
      let url = '/api/beer-manufacturer/' + encodeURI(beer);
      url = url.replace(/#/gi, '%23');
      return this.http.get<string[]>(url);
    }
    return this.http.get<string[]>('/api/beer-manufacturer');
  }

  getBarsSellingMostOf(beer: string) {
    let url = '/api/barsSellingMost/' + encodeURI(beer);
    url = url.replace(/#/gi, '%23');
    return this.http.get<any[]>(url);
  }

  getTopDrinkersFor(beer: string) {
    let url = '/api/topDrinkersFor/' + encodeURI(beer);
    url = url.replace(/#/gi, '%23');
    return this.http.get<any[]>(url);
  }

  getTimeDistFor(beer: string) {
    let url = '/api/getTimeDistFor/' + encodeURI(beer);
    url = url.replace(/#/gi, '%23');
    return this.http.get<any[]>(url);
  }

}
