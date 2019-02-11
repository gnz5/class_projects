import { Component, OnInit } from '@angular/core';
import { BeersService } from '../beers.service';
import { SelectItem } from 'primeng/components/common/selectitem';

@Component({
  selector: 'app-beers',
  templateUrl: './beers.component.html',
  styleUrls: ['./beers.component.css']
})
export class BeersComponent implements OnInit {

  beers: any[];
  manufacturerOptions: SelectItem[];
  barOptions: SelectItem[];
  originalBeerList: any[];

  constructor(private beerService: BeersService) {
    this.beerService.getBeers().subscribe(
      data => {
        this.beers = data;
        this.originalBeerList = data;
      }
    );

    this.beerService.getBeerManufacturers().subscribe(
      data => {
        this.manufacturerOptions = data.map(manf => {
          return {
            label: manf,
            value: manf
          };
        });
      }
    );
   }

  ngOnInit() {
  }

  filterBeers(manufacturer: string) {
    this.beers = this.originalBeerList;
    if (manufacturer) {
      this.beers = this.originalBeerList.filter(beer => beer.manf === manufacturer);
    }
  }

}
