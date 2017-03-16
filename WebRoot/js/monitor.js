let cluters = null;
let initFlag = false;
let dataMap = new Map();
function getInfo(){
  $.get("./monitor",data=>{
    cluters = JSON.parse(data);
    console.log(cluters);
    cluters.map(cluter=>{
      cluter.apps.map(app=>{
        $.post("./monitor",{id:app.id,ip:cluter.ip},data=>app.excutors = JSON.parse(data))
      })
    })
    !initFlag && cluters.map(cluter=>{init(cluter)});
    initFlag && cluters.map(cluter=>update(cluter));
  });
}
setInterval(getInfo,2000);
function init(cluter){
  $(".monitor")[0].innerHTML += "<div id=\""+cluter.name+"\"><h3>"+cluter.name+
            "</h3><p>"+cluter.ip+"</p>"+
            "<div class=\"panel\"><div id=\""+cluter.name+"-cpu\" style=\"height:300px;width:45%\"></div>"+
            "<div id=\""+cluter.name+"-mem\" style=\"height:300px;width:45%\"></div></div></div>";
  dataMap.set(cluter.name+"-cpu",new chart(cluter.name+"-cpu",cluter));
  dataMap.set(cluter.name+"-mem",new chart(cluter.name+"-mem",cluter));
  initFlag = true;
}

function update(cluter){
  dataMap.forEach((value,key)=>{
    if(key.indexOf(cluter.name)>-1){
      value(cluter);
    }
  })
}

function chart(name,cluter){
  var dom = document.getElementById(name);
  var myChart = echarts.init(dom);
  option = null;
  var date = [];
  var data = [Math.random() * 150];
  var now = new Date();


  function addData(shift,newCluter) {
    if(newCluter) cluter = newCluter;
      now = [now.getHours()<10?'0'+now.getHours():now.getHours(),
        now.getMinutes()<10?'0'+now.getMinutes():now.getMinutes(),
        now.getSeconds()<10?'0'+now.getSeconds():now.getSeconds()].join(':');
      let num = 0.0;
      if(name.indexOf("cpu")>-1){
        num = parseFloat(cluter.cpuRate);
        console.log(num);
      }
      if(name.indexOf("mem")>-1){
        num = parseFloat(cluter.memRate).toFixed(2);
        //parseFloat(mem.substring(mem.indexOf("Total = ")+8,mem.indexOf("av")));
      }
      date.push(now);
      data.push(num);

      if (shift) {
          date.shift();
          data.shift();
      }

      now = new Date();
  }

  for (var i = 1; i < 60; i++) {
      addData();
  }

  option = {
      title: {
          text: name,
          x:"center"
      },
      tooltip : {
        trigger: 'axis'
      },
      xAxis: {
          type: 'category',
          boundaryGap: false,
          data: date,
          name:"时间"
      },
      yAxis: {
          boundaryGap: [0, '50%'],
          type: 'value',
          name:"%"
      },
      series: [
          {
              name:'占用',
              type:'line',
              smooth:true,
              symbol: 'none',
              stack: 'a',
              areaStyle: {
                  normal: {}
              },
              data: data
          }
      ]
  };
  function update(newCluter){
  	// console.log(dom);
  	// console.log(data);
    addData(true,newCluter);
    myChart.setOption({
        xAxis: {
            data: date
        },
        series: [{
            name:'占用',
            data: data
        }]
    });
  }
  if (option && typeof option === "object") {
      myChart.setOption(option, true);
  }
  return update;
}
