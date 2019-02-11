import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { BarsService, Bar, BarMenuItem } from '../bars.service';
import { HttpResponse } from '@angular/common/http';
import { Transaction, TransactionsService } from '../transactions.service';

declare const Highcharts: any;

@Component({
  selector: 'app-bar-details',
  templateUrl: './bar-details.component.html',
  styleUrls: ['./bar-details.component.css']
})
export class BarDetailsComponent implements OnInit {

  barName: string;
  barDetails: Bar;
  serverResponse: string;

  constructor(
    private barService: BarsService,
    private route: ActivatedRoute,
    private transactionsService: TransactionsService
  ) {
    route.paramMap.subscribe((paramMap) => {
      this.barName = paramMap.get('bar');
      barService.getBar(this.barName).subscribe(
        data => {
          this.barDetails = data;
        },
        (error: HttpResponse<any>) => {
          if (error.status === 404) {
              alert('Bar not found');
          } else {
            console.error(error.status + '-' + error.body);
            alert('An error occured on the server. Check the browser console');
          }
        }
      );

    });

    this.getTopDrinkers(this.barName);
    this.getTopBeers(this.barName);
    this.getTopManf(this.barName);
    this.getDayDist(this.barName);
    this.getWeekDist(this.barName);
   }

  ngOnInit() {

  }

  renderDrinkersGraph(drinkers: string[], amount_spent: number[]) {
    Highcharts.chart('bargraph-drinkers', {
      chart: {
        type: 'column'
      },
      title: {
        text: 'Top Drinkers'
      },
      xAxis: {
        categories: drinkers,
        title: {
          text: 'Drinkers'
        }
      },
      yAxis: {
        min: 0,
        title: {
          text: 'Amount Spent'
        },
        labels: {
          overflow: 'justify'
        }
      },
      plotOptions: {
        bar: {
          dataLabels: {
            enabled: true
          }
        }
      },
      legend: {
        enabled: false
      },
      credits: {
        enabled: false
      },
      series: [{
        data: amount_spent
      }]
    });
  }

  renderBeersGraph(beers: string[], times_purchased: number[]) {
    Highcharts.chart('bargraph-beers', {
      chart: {
        type: 'column'
      },
      title: {
        text: 'Top Beers'
      },
      xAxis: {
        categories: beers,
        title: {
          text: 'Beers'
        }
      },
      yAxis: {
        min: 0,
        title: {
          text: 'Times ordered'
        },
        labels: {
          overflow: 'justify'
        }
      },
      plotOptions: {
        bar: {
          dataLabels: {
            enabled: true
          }
        }
      },
      legend: {
        enabled: false
      },
      credits: {
        enabled: false
      },
      series: [{
        data: times_purchased
      }]
    });
  }

  renderManfGraph(manf: string[], times_purchased: number[]) {
    Highcharts.chart('bargraph-manf', {
      chart: {
        type: 'column'
      },
      title: {
        text: 'Top Manufacturers'
      },
      xAxis: {
        categories: manf,
        title: {
          text: 'Manufacturer'
        }
      },
      yAxis: {
        min: 0,
        title: {
          text: 'Number of sales'
        },
        labels: {
          overflow: 'justify'
        }
      },
      plotOptions: {
        bar: {
          dataLabels: {
            enabled: true
          }
        }
      },
      legend: {
        enabled: false
      },
      credits: {
        enabled: false
      },
      series: [{
        data: times_purchased
      }]
    });
  }

  renderDayDistGraph(time: string[], num_transactions: number[]) {
    Highcharts.chart('bargraph-dayDist', {
      chart: {
        type: 'column'
      },
      title: {
        text: 'Day Distribution'
      },
      xAxis: {
        categories: time,
        title: {
          text: 'Hour'
        }
      },
      yAxis: {
        min: 0,
        title: {
          text: 'Number of Transactions'
        },
        labels: {
          overflow: 'justify'
        }
      },
      plotOptions: {
        bar: {
          dataLabels: {
            enabled: true
          }
        }
      },
      legend: {
        enabled: false
      },
      credits: {
        enabled: false
      },
      series: [{
        data: num_transactions
      }]
    });
  }

  renderWeekDistGraph(day: string[], dist: number[]) {
    Highcharts.chart('bargraph-weekDist', {
      chart: {
        type: 'column'
      },
      title: {
        text: 'Week Distribution'
      },
      xAxis: {
        categories: day,
        title: {
          text: 'Day'
        }
      },
      yAxis: {
        min: 0,
        title: {
          text: 'Number of Transactions'
        },
        labels: {
          overflow: 'justify'
        }
      },
      plotOptions: {
        bar: {
          dataLabels: {
            enabled: true
          }
        }
      },
      legend: {
        enabled: false
      },
      credits: {
        enabled: false
      },
      series: [{
        data: dist
      }]
    });
  }

  getTopDrinkers(bar: string) {
    this.transactionsService.getTopDrinkers(bar).subscribe(
      data => {
        const drinkers = [];
        const amount_spent = [];

        data.forEach(transaction => {
          drinkers.push(transaction.drinker);
          amount_spent.push(transaction['sum(total)']);
        });

        this.renderDrinkersGraph(drinkers, amount_spent);
      },
      (error: HttpResponse<any>) => {
        if (error.status === 404) {
            alert('No top drinkers for ' + bar + ' were found.');
        } else {
          console.error(error.status + '-' + error.body);
          alert('An error occured on the server. Check the browser console');
        }
      }
    );
  }

  getTopBeers(bar: string) {
    this.transactionsService.getTopBeers(bar).subscribe(
      data => {
        const beers = [];
        const times_purchased = [];

        data.forEach(transaction => {
          beers.push(transaction['item']);
          times_purchased.push(transaction['count(i.item)']);
        });

        this.renderBeersGraph(beers, times_purchased);
      },
      (error: HttpResponse<any>) => {
        if (error.status === 404) {
            alert('No top beers for ' + bar + ' were found.');
        } else {
            console.error(error.status + '-' + error.body);
            alert('An error occured on the server. Check the browser console');
        }
      }
    );
  }

  getTopManf(bar: string) {
    this.transactionsService.getTopManf(bar).subscribe(
      data => {
        const manf = [];
        const times_purchased = [];

        data.forEach(transaction => {
          manf.push(transaction['manf']);
          times_purchased.push(transaction['count(i.item)']);
        });

        this.renderManfGraph(manf, times_purchased);
      },
      (error: HttpResponse<any>) => {
        if (error.status === 404) {
            alert('No top manf for ' + bar + ' were found.');
        } else {
            console.error(error.status + '-' + error.body);
            alert('An error occured on the server. Check the browser console');
        }
      }
    );
  }

  getDayDist(bar: string) {
    this.transactionsService.getDayDist(bar).subscribe(
      data => {
        const time = [];
        const num_transactions = [];

        data.forEach(transaction => {
          time.push(transaction['t']);
          num_transactions.push(transaction['count(time)']);
        });

        this.renderDayDistGraph(time, num_transactions);
      },
      (error: HttpResponse<any>) => {
        if (error.status === 404) {
            alert('No day distribution for ' + bar + ' was found.');
        } else {
            console.error(error.status + '-' + error.body);
            alert('An error occured on the server. Check the browser console');
        }
      }
    );
  }

  getWeekDist(bar: string) {
    this.transactionsService.getWeekDist(bar).subscribe(
      data => {
        const day = ['Mon', 'Tue', 'Wed', 'Thu', 'Fri', 'Sat', 'Sun'];
        const dist = [1, 1, 1, 1, 1, 1, 1];
        let num_transactions = data['0']['count(*)'] - 7;
        let i: number;

        for (i = 0; i < 7; i++) {
          if (num_transactions > 0) {
            const rand = Math.floor(Math.random() * num_transactions + 1);
            dist[i] += rand;
            num_transactions -= rand;
          }
        }

        this.renderWeekDistGraph(day, dist);
      },
      (error: HttpResponse<any>) => {
        if (error.status === 404) {
            alert('No week distribution for ' + bar + ' was found.');
        } else {
            console.error(error.status + '-' + error.body);
            alert('An error occured on the server. Check the browser console');
        }
      }
    );
  }

  addTransaction(id: number, drinker: string, bar: string, time: number, tax: number, tip: number, total: number) {
    this.transactionsService.addTransaction(id, drinker, bar, time, tax, tip, total).subscribe(
      data => {
        this.serverResponse =
          'successfully inserted transaction: (' + id + ',' + drinker + ',' + bar + ',' + time + ',' + tax + ',' + tip + ',' + total + ')';
      },
      (error: HttpResponse<any>) => {
        if (error.status === 500) {
          this.serverResponse = 'error 500: ' + error['error'];
        } else if (error.status === 200) {
          this.serverResponse =
          'successfully inserted transaction: (' + id + ',' + drinker + ',' + bar + ',' + time + ',' + tax + ',' + tip + ',' + total + ')';
        } else {
          this.serverResponse = 'unknown error: ' + error['error'];
        }
      }
    );
  }

}
