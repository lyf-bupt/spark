$(function() {

	$.post("../result", {
		type: "visio"
	}, function(req) {
		var colors = Highcharts.getOptions().colors,
			categories = [],
			name = '出租车空载率',
			data = [{
				y: 0,
				color: colors[0],
				drilldown: {
					name: '',
					categories: [],
					data: [],
					color: colors[0]
				},
				drillup: {
					name: '',
					categories: [],
					data: [],
					color: colors[0]
				}
			}, {
				y: 21.63,
				color: colors[1],
				drilldown: {
					name: '',
					categories: [],
					data: [],
					color: colors[1]
				},
				drillup: {
					name: '',
					categories: [],
					data: [],
					color: colors[1]
				}
			}];

		var byDate = [];
		var byDateKey = [];
		var byDataValue = [];
		var byHour = [];
		var byHourKey = [];
		var byHourValue = [];
		var names = [];
		req = req.replace(/\?\?/g, "粤")
			// console.log("")
		datas = req.split("\n");
		datas.forEach(function(index) {
			var keys = index.split("-");
			if (keys[1]) {
				if (keys[1].indexOf("/") > -1) {
					byDate.push(index);
				} else {
					byHour.push(index);
				}
			} else {
				names.push(index)
			}
		});
		// console.log(names);
		names.sort();
		byDate.sort();
		byHour.sort();
		var i = 0;
		names.forEach(function(index) {
			if (index.split("=>")[0]) {
				byDateKey = [];
				byDataValue = [];
				byHourKey = [];
				byHourValue = [];
				categories.push(index.split("=>")[0]);
				data[i].y = parseFloat(index.split("=>")[1]);
				// console.log(data[i].y );
				byDate.filter(function(item) {
					return item.indexOf(index.split("=>")[0]) > -1;
				}).forEach(function(ele) {
					if (ele.split("=>")[0]) {
						byDateKey.push(ele.split("=>")[0].split("-")[1]);
						byDataValue.push(parseFloat(ele.split("=>")[1]));
					}
				});
				byHour.filter(function(item) {
					return item.indexOf(index.split("=>")[0]) > -1;
				}).forEach(function(ele) {
					if (ele.split("=>")[0]) {
						var hours = parseInt(ele.split("=>")[0].split("-")[1]) * 3
						var to = ((parseInt(ele.split("=>")[0].split("-")[1])) + 1) * 3
						var ans = hours + "点~" + to + "点";
						byHourKey.push(ans);
						byHourValue.push(parseFloat(ele.split("=>")[1]));
					}
				});
				data[i].drilldown.name = index.split("=>")[0];
				data[i].drilldown.categories = byDateKey;
				data[i].drilldown.data = byDataValue;
				data[i].drillup.name = index.split("=>")[0];
				data[i].drillup.categories = byHourKey;
				data[i].drillup.data = byHourValue;
				i++;
			}
		});
		console.log(data[0]);
		i = 0;
		byHour.forEach(function(index) {});
		// console.log(req);


		function setChart(name, categories, data, type, title, color) {
			chart.options.chart.type = type;
			chart.setTitle({
				text: title
			});
			chart.xAxis[0].setCategories(categories, false);
			chart.series[0].remove(false);
			chart.addSeries({
				name: name,
				data: data,
				color: color || 'white'
			}, false);
			chart.redraw();
		}

		var chart = $('#container').highcharts({
				chart: {
					type: 'column'
				},
				title: {
					text: '总览'
				},
				subtitle: {
					text: '点击柱状图到按日期查看界面，再按一次到达按时间查看界面.'
				},
				xAxis: {
					categories: categories
				},
				yAxis: {
					title: {
						text: '空载率'
					}
				},
				plotOptions: {
					column: {
						cursor: 'pointer',
						point: {
							events: {
								click: function() {
									var drilldown = this.drilldown;
									drillup = this.drillup;
									if (drilldown) { // drill down
										setChart(drilldown.name, drilldown.categories, drilldown.data, 'line', '按日期查看', drilldown.color);
									} else { // restore
										setChart(name, categories, data, 'sum');
									}
								}
							}
						},
						dataLabels: {
							enabled: true,
							color: colors[0],
							style: {
								fontWeight: 'bold'
							},
							formatter: function() {
								return this.y + '%';
							}
						}
					},
					line: {
						cursor: 'pointer',
						point: {
							events: {
								click: function() {
									console.log(drillup);
									if (drillup) {
										setChart(drillup.name, drillup.categories, drillup.data, 'line', '按时间查看', drillup.color);
										drillup = null;
									} else {
										setChart(name, categories, data, 'column', '总览');
									}
								},
								dataLabels: {
									enabled: true,
									color: colors[0],
									style: {
										fontWeight: 'bold'
									},
									formatter: function() {
										return this.y + '%';
									}
								}
							},
							dataLabels: {
								enabled: true,
								color: colors[0],
								style: {
									fontWeight: 'bold'
								},
								formatter: function() {
									return this.y + '%';
								}
							}
						}
					}
				},
				tooltip: {
					formatter: function() {
						var point = this.point,
							s = this.x + '空载率为:<b>' + this.y + '%</b><br/>';
						if (point.drilldown) {
							s += 'Click to view ' + point.category + ' detail';
						} else {
							s += 'Click to view another view';
						}
						return s;
					}
				},
				series: [{
					name: name,
					data: data,
					color: 'white'
				}],
				exporting: {
					enabled: false
				}
			})
			.highcharts(); // return chart
	});


});