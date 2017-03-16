var jsonObj;
$(function() {

	$.getJSON('../json/GetOnOut.json', function(json, textStatus) {
			/*optional stuff to do after success */
		console.log(json);
		jsonObj = json;
		var times = [];
		for(let i in json){
			times.push(i)
		}
		var select = document.createElement('select');
		select.onchange=function(){
			var time = this.options[this.options.selectedIndex].value;
			changeMap(time,json);
		}
		var option = document.createElement('option');
			option.innerHTML = "---";
			option.setAttribute("value",-1);
			select.appendChild(option);
		times.map(x=>{
			var option = document.createElement('option');
			option.innerHTML = x;
			option.setAttribute("value",x);
			select.appendChild(option);
		})
		document.body.appendChild(select);
		var canvas = document.createElement('div');
		canvas.style.width="100%";
		canvas.style.height="500px";
		canvas.id="container";
		document.body.appendChild(canvas);
		initMap();
	});

});

function addPoint(point,value){
	var marker = new BMap.Marker(point);
	map.addOverlay(marker);
	marker.addEventListener("click",function(){
		var infoWindow = new BMap.InfoWindow("latitude:"+point.lat+"<br>longitude:"+point.lng+"<br>counter:"+value, 
				opts);  // 创建信息窗口对象
		map.openInfoWindow(infoWindow,point); //开启信息窗口
	});
}
function initMap(){
	map = new BMap.Map("container");          // 创建地图实例
    var point = new BMap.Point(114.0641,22.543441);
    map.centerAndZoom(point, 13);             // 初始化地图，设置中心点坐标和地图级别
    map.enableScrollWheelZoom(); // 允许滚轮缩放
    heatmapOverlay = new BMapLib.HeatmapOverlay({"radius":20});
	map.addOverlay(heatmapOverlay);
	if(!isSupportCanvas()){
    	alert('您所使用的浏览器不能使用热力图功能')
    }
}
function changeMap(time,json){
	map.clearOverlays();
	var max = -1;
	var ans = json[time];
	var hotPoints =[];
	for(let i in ans){
		let x = parseFloat(i.split('-')[0]);
		let y = parseFloat(i.split('-')[1]);
		let value = ans[i];
		if(value>max) max = value;
		addPoint(new BMap.Point(x,y),value);
		let hotPoint = {};
		hotPoint['lng'] = x;
		hotPoint['lat'] = y;
		hotPoint['count'] = value;
		hotPoints.push(hotPoint);
	}
	console.log(hotPoints,max);
	heatmapOverlay.setDataSet({data:hotPoints,max:max});
	map.addOverlay(heatmapOverlay);
	heatmapOverlay.show();
}
function isSupportCanvas(){
    var elem = document.createElement('canvas');
    return !!(elem.getContext && elem.getContext('2d'));
}
var opts = {
	  width : 100,     // 信息窗口宽度
	  height: 80,     // 信息窗口高度
	  title : "hot Get on Point" , // 信息窗口标题
	  enableMessage:true,//设置允许信息窗发送短息
	  message:"ok"
}