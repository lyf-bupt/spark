var mockData=[];
var needUpdateURLs=[];
var wsLists=null;
$(document).ready(function(){
  document.getElementsByClassName("tabs")[0].addEventListener("click",
    function(e){
      $(".tabs").children().removeClass("active");
      e.target.className = "active";
      var id = e.target.getAttribute("data-id");
      if(id == "manager"){
        $(".monitor-div").fadeOut(200,function(){
          $(".manager-div").fadeIn(200);
        });
      }else if(id == "monitor"){
        $(".manager-div").fadeOut(200,function(){
          $(".monitor-div").fadeIn(200);
        });
      }
    }
  );
  $.getJSON('wsCenter',function(json, textStatus) {
      /*optional stuff to do after success */
      wsLists=json;
      initTree(json);
      addButton();
  });
})

function addButton(){
  var tree = document.getElementsByClassName('tree')[0];
  var manager = document.getElementsByClassName('manager')[0];
  var button = document.createElement('button');
  button.innerHTML="add";
  button.style.marginLeft="10px";
  button.addEventListener('click',function(){
    manager.innerHTML = "";
    var form = document.createElement('form');
    form.setAttribute('action','addWebService');
    form.setAttribute('method',"POST");
    form.setAttribute('enctype','multipart/form-data');
    ['name','URL','jar'].map(function(item){
      var p = document.createElement('p');
      p.innerHTML=item+":";
      form.appendChild(p);
      var input = document.createElement('input');
      input.setAttribute('type',item.indexOf('jar')>-1?'file':'text');
      input.setAttribute('name',item);
      form.appendChild(input);
      form.innerHTML += "<br>"
    })
    var submit = document.createElement('button');
    submit.setAttribute('type','submit');
    submit.innerHTML="submit";
    form.appendChild(submit);
    manager.appendChild(form);
  })
  tree.appendChild(button);
}

  function initTree(data){
    // var data = services;
    // prepare the data
    var source =
    {
      datatype: "json",
      datafields: [
      { name: 'id' },
      { name: 'parentid' },
      { name: 'name' },
      ],
      id: 'id',
      localdata: data
    };
    // create data adapter.
    var dataAdapter = new $.jqx.dataAdapter(source);
    // perform Data Binding.
    dataAdapter.dataBind();
    // get the tree items. The first parameter is the item's id. The second parameter is the parent item's id. The 'items' parameter represents
    // the sub items collection name. Each jqxTree item has a 'label' property, but in the JSON data, we have a 'text' field. The last parameter
    // specifies the mapping between the 'text' and 'label' fields.
    var records = dataAdapter.getRecordsHierarchy('id', 'parentid', 'items', [{ name: 'name', map: 'label'}]);
    $('#tree').jqxTree({ source: records});
    $('#tree').jqxTree("refresh");
    $('#tree').on('select',function (event){
        var args = event.args;
        var item = $('#tree').jqxTree('getItem', args.element);
        var id = item.id;
        if(item.parentId != 0){
          $.post('wsCenter',{serviceId:id},function(data){
            var manager = document.getElementsByClassName('manager')[0];
            manager.innerHTML = "";
            var methods = JSON.parse(data);
            var table = document.createElement('table');
            var caption = document.createElement('caption');
            caption.innerHTML = item.label;
            table.appendChild(caption);
            var thead = document.createElement('thead');
            var headTr = document.createElement('tr');
            headTr.innerHTML="<td>#</td><td>name</td><td>method</td><td>URL</td><td>action</td>";
            thead.appendChild(headTr);
            table.appendChild(thead);
            var tbody = document.createElement('tBody');
            methods.map((method,index)=>{
              var tr = document.createElement("tr");
              tr.innerHTML = "<td>"+index+"</td><td>"+method.name+"</td><td>"+method.Method
                  +"</td><td>"+method.URL+"</td><td><button class='btn'>update</button><button class='btn'>remove</buttom>";
              tbody.appendChild(tr);
            })
            table.appendChild(tbody);
            // console.log(JSON.parse(data));
            manager.appendChild(table);
          })
        }
    });
  }