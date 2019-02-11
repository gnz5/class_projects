import { Component, OnInit } from '@angular/core';
import { BeersService, BeerLocation } from '../beers.service';
import { ActivatedRoute } from '@angular/router';
import { SelectItem } from 'primeng/components/common/selectitem';

declare const Highcharts: any;

@Component({
  selector: 'app-beer-details',
  templateUrl: './beer-details.component.html',
  styleUrls: ['./beer-details.component.css']
})
export class BeerDetailsComponent implements OnInit {

  beerName: string;
  beerLocations: BeerLocation[];
  manufacturer: string;
  filterOptions: SelectItem[];
  topSellingBars: any;
  topDrinkers: any;

  constructor(
    private beerService: BeersService,
    private route: ActivatedRoute
    ) {
      this.route.paramMap.subscribe((paramMap) => {
        this.beerName = paramMap.get('beer');

        this.beerService.getBarsSelling(this.beerName).subscribe(
          data => {
            this.beerLocations = data;
          }
        );

        this.beerService.getBeerManufacturers(this.beerName).subscribe(
          data => {
            this.manufacturer = data;
          }
        );

        this.filterOptions = [
          {
            'label': 'low price first',
            'value': 'low price'
          },
          {
            'label': 'high price first',
            'value': 'high price'
          },
          {
            'label': 'most frequented first',
            'value': 'high customer'
          },
          {
            'label': 'least frequented first',
            'value': 'low customer'
          }
        ];
      });

      this.beerService.getBarsSellingMostOf(this.beerName).subscribe(
        data => {
          this.topSellingBars = data;
        }
      );

      this.beerService.getTopDrinkersFor(this.beerName).subscribe(
        data => {
          this.topDrinkers = data;
        }
      );

      this.beerService.getTimeDistFor(this.beerName).subscribe(
        data => {
          const time = [];
          const num_transactions = [];

          data.forEach(transaction => {
            time.push(transaction['time']);
            num_transactions.push(transaction['count(*)']);
          });

          this.renderDayDistGraph(time, num_transactions);
        }
      );

     }

  ngOnInit() {
  }

  sortBy(selectedOption: string) {
    if (selectedOption === 'low price') {
      this.beerLocations.sort((a, b) => a.price - b.price );
    } else if (selectedOption === 'high price') {
      this.beerLocations.sort((a, b) => b.price - a.price );
    } else if (selectedOption === 'high customer') {
      this.beerLocations.sort((a, b) => a.customers - b.customers );
    } else if (selectedOption === 'low customer') {
      this.beerLocations.sort((a, b) => b.customers - a.customers );
    }
  }

  renderDayDistGraph(time: string[], num_transactions: number[]) {
    Highcharts.chart('bargraph-timeDist', {
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

}
