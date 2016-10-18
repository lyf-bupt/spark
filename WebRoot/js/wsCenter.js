var mockData=[];
var needUpdateURLs=[];
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
    });

})
