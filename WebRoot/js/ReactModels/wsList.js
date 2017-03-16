var RepoList = React.createClass({
getInitialState: function() {
  return {
    loading: true,
    error: null,
    data: null
  };
},

componentDidMount() {
  this.props.promise.then(
    value => {
      value = value.filter(x=>x.type!=null&&x.type!="null")
      this.setState({loading: false, data: value})
    },
    error => {this.setState({loading: false, error: error})})
},

getState:function(){
  for(let i in this.state.data){
    $.get(this.state.data[i].URL+"?wsdl").then(
      value => {
        this.state.data[i].state = "running";
        this.setState({data: this.state.data});
      },
      error => {
        this.state.data[i].state = "stop";
        this.setState({data: this.state.data});
      }
    )
  }
},

handleClick:function(type,index,e){
  if(e.target.className.indexOf("disable")>-1) return false;
  if(type == "run"){
    this.state.data[index].state = "pedding";
    this.setState({data: this.state.data});
    //启动webservice
    setTimeout(()=>{
      this.state.data[index].state = "running";
      this.setState({data: this.state.data});
    },1000)
  }else if(type == "stop"){
    this.state.data[index].state = "pedding";
    this.setState({data: this.state.data});
    //停止webservice
    setTimeout(()=>{
      this.state.data[index].state = "stop";
      this.setState({data: this.state.data});
    },1000)
  }
},

render: function() {
  if (this.state.loading) {
    return <h2>loading...</h2>;
  }
  else if (this.state.error !== null) {
    return <span>Error: {this.state.error.message}</span>;
  }
  else {
    var repos = this.state.data;
    var _this = this;
    var repoList = repos.map(function (repo,index) {
      return (
          <tr>
            <td>{ index }</td>
            <td>{ repo.name }</td>
            <td>{ repo.type }</td>
            <td>{ repo.URL }</td>
            <td>
              <div
                className={"flag "+repo.state}>
              </div>
              { repo.state }
            </td>
            <td>
              <button
                className={repo.state == "stop"?"btn bootButton":"btn btn-disable"}
                onClick={_this.handleClick.bind(_this,'run',index)}>
                <i className="fa fa-play"/>
                 &nbsp;run
              </button>
              <button className={repo.state == "running"?"btn stopButton":"btn btn-disable"}
                onClick={_this.handleClick.bind(_this,'stop',index)}>
                <i className="fa fa-stop"/>
                 &nbsp;stop
              </button>
            </td>
          </tr>
      );
    });
    return (
      <table>
         <caption>webservice list</caption>
         <thead>
         <tr>
           <td>#</td>
           <td>name</td>
           <td>type</td>
           <td>URL</td>
           <td>state</td>
           <td>操作</td>
         </tr>
        </thead>
        <tbody>{repoList}</tbody>
      </table>
    );
  }
}
});

ReactDOM.render(
  <RepoList promise={$.getJSON('wsCenter')} />,
  document.getElementsByClassName("monitor")[0]
);
