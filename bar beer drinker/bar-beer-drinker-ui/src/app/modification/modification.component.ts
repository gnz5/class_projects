import { Component, OnInit } from '@angular/core';
import { ModificationService } from '../modification.service';
import { HttpResponse } from '@angular/common/http';

@Component({
  selector: 'app-modification',
  templateUrl: './modification.component.html',
  styleUrls: ['./modification.component.css']
})
export class ModificationComponent implements OnInit {

  serverResponse: string[];

  constructor(private modificationService: ModificationService) { }

  ngOnInit() {
  }

  execute(query: string) {
    this.serverResponse = [];
    this.modificationService.execute(query).subscribe(
      data => {
        if (data.length === 0) {
          this.serverResponse = ['no results found'];
          return;
        }
        const keys = Object.keys(data[0]);
        data.forEach(x =>
          {
            let temp = '';
            keys.forEach(k => {
                temp += x[k] + ', ';
            });
            temp = temp.substr(0, temp.length - 2);
            this.serverResponse.push(temp);
          }
        );
      },
      (error: HttpResponse<any>) => {
        if (error.status === 200) {
            this.serverResponse = error['error']['text'];
            alert(error['error']['text']);
        } else if (error.status === 404) {
          alert('Invalid Statement');
        } else {
          alert('Invalid Statement');
        }
      }
    );
  }

  update(table: string, attribute_assignments: string, condition_on_tuples: string) {
    this.modificationService.update(table, attribute_assignments, condition_on_tuples).subscribe(
      data => {

      },
      (error: HttpResponse<any>) => {
        if (error.status === 200) {
            alert(error['error']['text']);
        } else if (error.status === 404) {
          alert('Invalid Statement');
        } else {
          alert('Invalid Statement');
        }
      }
    );
  }

  delete(table: string, condition: string) {
    this.modificationService.delete(table, condition).subscribe(
      data => {

      },
      (error: HttpResponse<any>) => {
        if (error.status === 200) {
            alert(error['error']['text']);
        } else if (error.status === 404) {
          alert('Invalid Statement');
        } else {
          alert('Invalid Statement');
        }
      }
    );
  }

  insert(table: string, tuples: string) {
    this.modificationService.insert(table, tuples).subscribe(
      data => {

      },
      (error: HttpResponse<any>) => {
        if (error.status === 200) {
            alert(error['error']['text']);
        } else if (error.status === 404) {
          alert('Invalid Statement');
        } else {
          alert('Invalid Statement');
        }
      }
    );
  }

}
