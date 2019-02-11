import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';

export interface Transaction {
  id: number;
  drinker: string;
  bar: string;
  time: number;
  tax: number;
  tip: number;
  total: number;
}

@Injectable({
  providedIn: 'root'
})
export class TransactionsService {

  transactions: Transaction[];
  transactionsLoaded = 0;

  constructor(private http: HttpClient) {
   }


  getTopDrinkers(bar: string) {
    return this.http.get<Transaction[]>('/api/transactions/topDrinkers/' + bar);
  }

  getTopBeers(bar: string) {
    return this.http.get<Transaction[]>('/api/transactions/topBeers/' + bar);
  }

  getTopManf(bar: string) {
    return this.http.get<Transaction[]>('/api/transactions/topManf/' + bar);
  }

  getDayDist(bar: string) {
    return this.http.get<Transaction[]>('/api/transactions/dayDist/' + bar);
  }

  getWeekDist(bar: string) {
    return this.http.get<Transaction[]>('/api/transactions/weekDist/' + bar);
  }

  addTransaction(id: number, drinker: string, bar: string, time: number, tax: number, tip: number, total: number) {
    const url = '/api/transactions/add/' + id + '/' + drinker + '/' + bar + '/' + time + '/' + tax + '/' + tip + '/' + total;
    return this.http.post<{}>(url, {});
  }

}
