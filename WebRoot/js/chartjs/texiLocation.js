$(function() {

	$.post("result", {
		type: "visio"
	}, function(req) {
		var select = document.createElement('select');
		select.onchange=function(){
			var time = this.options[this.options.selectedIndex].value;
			getLocation(time);
		}
		var times = req.split("\n");
		var option = document.createElement('option');
			option.innerHTML = "---";
			option.setAttribute("value",0);
			select.appendChild(option);
		times.map(x=>{
			var option = document.createElement('option');
			option.innerHTML = x;
			option.setAttribute("value",x);
			select.appendChild(option);
		})
		document.body.appendChild(select);
		var canvas = document.createElement('canvas');
		canvas.style.width="100%";
		canvas.style.height="100%";
		canvas.id="canvas";
		document.body.appendChild(canvas);
	})

});

function getLocation(time){
	$.post("result", {
		type: "visio",
		time: time
	}, function(req) {
		var locations = req.split('\n');
		var canvas = $('#canvas')[0];
		var context = canvas.getContext("2d");
		locations.map(location=>{
			if(!location) return;
			var x = (parseFloat(location.split('-')[0])-113)*200;
			var y = (parseFloat(location.split('-')[1])-22)*100;
			var r = parseFloat(location.split('-')[2]);
			console.log(x,y,r);
			console.log(location)
			draw(context,x,y,r);
		})
	})
}

function draw(context,x,y,r){
	context.beginPath();
	var intR = r>10?r/10:1;
    context.arc(x, y, 1	, 0, Math.PI * 2, true);
    //不关闭路径路径会一直保留下去，当然也可以利用这个特点做出意想不到的效果
    context.closePath();
    if(r>50){
    	context.fillStyle = 'rgba(0,255,0,0.25)';
    }else if(r>10){
    	context.fillStyle = 'rgba(255,255,0,0.25)';
	}else{
    	context.fillStyle = 'rgba(255,0,0,0.25)';
    }
    
    context.fill();
}