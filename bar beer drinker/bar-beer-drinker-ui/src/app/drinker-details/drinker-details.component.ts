import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { HttpResponse } from '@angular/common/http';
import { DrinkersService, Drinker } from '../drinkers.service';

declare const Highcharts: any;


@Component({
  selector: 'app-drinker-details',
  templateUrl: './drinker-details.component.html',
  styleUrls: ['./drinker-details.component.css']
})
export class DrinkerDetailsComponent implements OnInit {

  name: string;
  drinker: Drinker;
  transactions_time: any;
  transactions_bar: any;
  topBeer: any;
  spendingByBar: any;

  constructor(
    private drinkerService: DrinkersService,
    private route: ActivatedRoute
  ) {

    this.name = this.route.params['_value']['drinker'];
    this.drinkerService.getDrinker(this.name).subscribe(
      data => {
        this.drinker = data;
      },
      (error: HttpResponse<any>) => {
        if (error.status === 900) {
          alert('Drinker not found');
        } else {
          console.error(error.status + '-' + error.body);
          alert('An error occured on the server. Check the browser console');
        }
      }
    );

    this.drinkerService.getDrinkerTransactions(this.name).subscribe(
      data => {
        this.transactions_time = data;
      }
    );

    this.drinkerService.getDrinkerTopBeers(this.name).subscribe(
      data => {
        const beer = [];
        const num_purchases = [];

        data.forEach(transaction => {
          beer.push(transaction['item']);
          num_purchases.push(transaction['count(i.item)']);
        });
        this.renderTopBeersGraph(beer, num_purchases);
      }
    );

    this.drinkerService.getDrinkerBarSpending(this.name).subscribe(
      data => {
        const bar = [];
        const amount_spent = [];

        data.forEach(transaction => {
          bar.push(transaction['bar']);
          amount_spent.push(transaction['total']);
        });
        this.renderbarSpendingGraph(bar, amount_spent);
      }
    );
  }

  ngOnInit() {
  }

  renderTopBeersGraph(beer: string[], num_purchases: number[]) {
    Highcharts.chart('bargraph-topBeers', {
      chart: {
        type: 'column'
      },
      title: {
        text: this.name + '\'s Top Beers'
      },
      xAxis: {
        categories: beer,
        title: {
          text: 'Times Purchased'
        }
      },
      yAxis: {
        min: 0,
        title: {
          text: 'Times Purchased'
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
        data: num_purchases
      }]
    });
  }

  renderbarSpendingGraph(bar: string[], amount_spent: number[]) {
    Highcharts.chart('bargraph-barSpending', {
      chart: {
        type: 'column'
      },
      title: {
        text: 'Spending at Different Bars'
      },
      xAxis: {
        categories: bar,
        title: {
          text: 'Bar'
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

}
