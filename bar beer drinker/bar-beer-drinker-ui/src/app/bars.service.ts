import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Transaction, TransactionsService } from './transactions.service';

export interface Bar {
  name: string;
  phone: number;
  address: string;
  state: string;
  opening: number;
  closing: number;
  tax: number;
}

export interface BarMenuItem {
  beer: string;
  manf: string;
  price: number;
  likes: number;
}

@Injectable({
  providedIn: 'root'
})
export class BarsService {

  constructor(
    public http: HttpClient,
    private transactionsService: TransactionsService
  ) { }

  getBars() {
    return this.http.get<Bar[]>('/api/bar');
  }

  getBar(bar: string) {
    let url = '/api/bar/' + encodeURI(bar);
    url = url.replace(/#/gi, '%23');
    return this.http.get<Bar>(url);
  }

  getMenu(bar: string) {
    let url = '/api/menu/' + encodeURI(bar);
    url = url.replace(/#/gi, '%23');
    return this.http.get<BarMenuItem[]>(url);
  }

  getFrequentCounts() {
    return this.http.get<any[]>('/api/frequents-data');
  }

}
