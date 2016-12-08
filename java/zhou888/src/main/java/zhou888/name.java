package zhou888;
import javax.jws.WebParam;
import javax.jws.WebMethod;
import javax.jws.WebService;
import javax.xml.ws.Endpoint;

@WebService
public class name{
@WebMethod
    public int add(  @WebParam(name = "in0") int num){
        return num+100;
    }
@WebMethod
    public int minus(  @WebParam(name = "in0") int num){
        return num-100;
    }
  public static void main(String[] args) {
   name texi = new name();
    Endpoint endpoint = Endpoint.publish("http://10.108.166.75:9098/zhou888/name", texi);
  }
}