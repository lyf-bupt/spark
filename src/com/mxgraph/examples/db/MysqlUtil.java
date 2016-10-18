package com.mxgraph.examples.db;

import java.sql.*;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Vector;


public class MysqlUtil {
	private final String DBDRIVER = "com.mysql.jdbc.Driver";
    private final String DBURL = "jdbc:mysql://localhost:3306/mydb?useUnicode=true&characterEncoding=uft8";
    private final String DBUSER = "root";
    private final String DBPASSWORD = "root";
    private Connection conn = null;
    
    public MysqlUtil(){}
    public Connection getConn() {
        try {
           // Class.forName(DBDRIVER).newInstance();
        	Class.forName("com.mysql.jdbc.Driver");
            conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/registCenter","root","root");
            
        } catch (Exception e) {
        	//System.out.println();
        	e.printStackTrace();
            System.out.println("数据库连接失败");
        }
        return conn;
    }

    public void close() {
        try {
            this.conn.close();
        } catch (Exception e) {
            System.out.println("数据库关闭失败！");
        }
    }

    public ResultSet executeQuery(String sql) {
        ResultSet rs = null;
        try {
            rs = null;
            Connection conn = this.getConn();
            Statement stmt = conn.createStatement();
            rs = stmt.executeQuery(sql);
        } catch (SQLException ex) {
            System.out.println("ִ执行错误");
            ex.printStackTrace();
        }
        return rs;
    }

    public boolean executeUpdate(String strSQL) {
        try {
            Connection conn = this.getConn();
            Statement stmt = conn.createStatement();
            stmt.executeUpdate(strSQL);
        
        } catch (SQLException ex) {
            System.err.println("连接数据库失败了，异常为" + ex.getMessage());
        }
        return true;
    }
    
    public String getAllDataSource(){
    	String dataSources = "";
    	ResultSet rs = executeQuery("select * from dataSource order by id");
    	try {
			while (rs.next()) {
				dataSources += rs.getInt("id") + "."+rs.getString("name");
				if(!rs.isLast()){
					dataSources += "-";
				}
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return dataSources;
    }
    
    public String getFields(int source){
    	String dataSources = "";
    	ResultSet rs = executeQuery("select * from dataField where source = "+source+" order by value");
    	try {
			while (rs.next()) {
				dataSources += rs.getInt("value") + "."+rs.getString("tips");
				if(!rs.isLast()){
					dataSources += "-";
				}
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return dataSources;
    }
    
    public String getExamples(int source){
    	String dataSources = "";
    	ResultSet rs = executeQuery("select * from dataField where source = "+source+" order by value");
    	try {
			while (rs.next()) {
				dataSources +=rs.getString("example");
				if(!rs.isLast()){
					dataSources += "-";
				}
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return dataSources;
    }
    
    public String getModels(int source){
    	String dataSources = "";
    	ResultSet rs = executeQuery("select * from views where source = "+source+" order by id");
    	try {
			while (rs.next()) {
				dataSources += rs.getString("name")+"("+rs.getString("src")+")";
				if(!rs.isLast()){
					dataSources += "-";
				}
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return dataSources;
    }
    
    public Vector<WebServiceDbModel> getAllType(){
    	Vector<WebServiceDbModel> allType = new Vector<WebServiceDbModel>();
		ResultSet rs = executeQuery("select * from services");
		try {
			while (rs.next()) {
				WebServiceDbModel model = new WebServiceDbModel();
				model.setId(rs.getInt("id"));
				model.setParentid(rs.getInt("father"));
				model.setName(rs.getString("name"));
				model.setType(rs.getString("type"));
				model.setURL(rs.getString("URL"));
				allType.add(model);
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return allType;
    }
    
    public Vector<MethodModel> getMethods(int  serviceId){
    	Vector<MethodModel> methods = new Vector<MethodModel>();
    	ResultSet rs = executeQuery("select * from methods where serviceId="+serviceId);
    	try {
			while (rs.next()) {
				MethodModel model = new MethodModel();
				model.setId(rs.getInt("id"));
				model.setURL(rs.getString("URL"));
				model.setName(rs.getString("name"));
				model.setMethod(rs.getString("method"));
				methods.add(model);
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    	return methods;
    }
    
    public static void main(String[] args){
    	MysqlUtil ct = new MysqlUtil();
    	System.out.println(ct.getModels(1));
    	Vector<MethodModel> res = ct.getMethods(2);
    	for(MethodModel model:res){
    		System.out.println("id:"+model.getId()+" URL:"+model.getURL()+" name:"+model.getName()+" method:"+model.getMethod());
    	}
    	Vector<WebServiceDbModel>json = new Vector<WebServiceDbModel>();
		ResultSet rs = ct
				.executeQuery("select * from services");
		try {
			while (rs.next()) {
				WebServiceDbModel model = new WebServiceDbModel();
				model.setId(rs.getInt("id"));
				model.setParentid(rs.getInt("father"));
				model.setName(rs.getString("name"));
				model.setType(rs.getString("type"));
				json.add(model);
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}finally{
			ct.close();
		}
		for(WebServiceDbModel model:json){
			System.out.println("id:"+model.getId()+" father:"+model.getParentid()+" name:"+model.getName()+" type:"+model.getType());
		}
    }
}
