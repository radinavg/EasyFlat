import {Component, Input, OnInit} from '@angular/core';
import {EChartsOption} from "echarts";
import {FinanceService} from "../../../../services/finance.service";
import {ToastrService} from "ngx-toastr";
import {UserValuePairDto} from "../../../../dtos/expenseDto";
import {Subject} from "rxjs";


@Component({
  selector: 'app-barchart-vertical',
  templateUrl: './barchart-vertical.component.html',
  styleUrls: ['./barchart-vertical.component.scss']
})
export class BarchartVerticalComponent implements OnInit {
  @Input() reloadSubject: Subject<boolean> = new Subject();

  chartOption: EChartsOption;

  constructor(
    private financeService: FinanceService,
    private notification: ToastrService
  ) {
  }

  ngOnInit(): void {
    this.reload();

    this.reloadSubject.subscribe((reload) => {
      if (reload) {
        this.reload();
      }
    });
  }

  reload(): void {
    this.financeService.findBalanceExpenses().subscribe({
      next: (data) => {
        this.initChart(data);
      },
      error: (error) => {
        this.notification.error("Failed to load data for statistics", "Error");
      }
    });
  }


  initChart(data: UserValuePairDto[]): void {
    this.chartOption = {
      xAxis: {
        type: 'value',
        position: 'top'
      },
      yAxis: {
        type: 'category',
        axisLine: {show: false},
        axisLabel: {show: false},
        axisTick: {show: false},
        splitLine: {show: false},
        data: data.map((value) => value.user.firstName + ' ' + value.user.lastName)
      },
      series: [
        {
          type: 'bar',
          label: {
            show: true,
            formatter: '{b}'
          },
          data: data.map((value) => ({
            value: (value.value / 100.0).toFixed(2),
            itemStyle: {
              color: value.value < 0 ? '#d9534f' : '#5cb85c'
            }
          }))
        }
      ],
      tooltip: {
        trigger: 'axis',
        axisPointer: {
          type: 'shadow'
        },
        formatter: (params) => {
          const param = params[0];
          return param.marker + param.name + ': ' + param.value + '€';
        }
      }

    };
  }

}
