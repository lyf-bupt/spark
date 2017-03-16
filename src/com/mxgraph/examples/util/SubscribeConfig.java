package com.mxgraph.examples.util;

import java.io.FileInputStream; 
import java.io.FileNotFoundException; 
import java.io.FileOutputStream; 
import java.io.IOException; 
import java.util.Properties; 

/**
 * 读取发布订阅模块的配置文件
 * @author spark
 *
 */
public class SubscribeConfig 
{ 
    private Properties propertie; 
    private FileInputStream inputFile; 
    private FileOutputStream outputFile; 


    
    public SubscribeConfig() 
    { 
        propertie = new Properties(); 
    } 

    /** *//** 
     * 打开配置文件
     * @param filePath 配置文件路径
     */ 
    public SubscribeConfig(String filePath) 
    { 
        propertie = new Properties(); 
        try { 
            inputFile = new FileInputStream(filePath); 
            propertie.load(inputFile); 
            inputFile.close(); 
        } catch (FileNotFoundException ex) { 
            System.out.println("文件未找到"); 
            ex.printStackTrace(); 
        } catch (IOException ex) { 
            System.out.println("IO出错"); 
            ex.printStackTrace(); 
        } 
    }//end ReadConfigInfo() 

    /** *//** 
     * 得到配置文件数值 
     * @param key 配置文件key
     * @return key的值
     */ 
    public String getValue(String key) 
    { 
        if(propertie.containsKey(key)){ 
            String value = propertie.getProperty(key);
            return value; 
        } 
        else 
            return ""; 
    }//end getValue() 

    /**
     * 打开配置文件并读取其中的数值
     * @param fileName 配置文件地址
     * @param key 配置文件属性
     * @return 配置文件中属性的值
     */
    public String getValue(String fileName, String key) 
    { 
        try { 
            String value = ""; 
            inputFile = new FileInputStream(fileName); 
            propertie.load(inputFile); 
            inputFile.close(); 
            if(propertie.containsKey(key)){ 
                value = propertie.getProperty(key); 
                return value; 
            }else 
                return value; 
        } catch (FileNotFoundException e) { 
            e.printStackTrace(); 
            return ""; 
        } catch (IOException e) { 
            e.printStackTrace(); 
            return ""; 
        } catch (Exception ex) { 
            ex.printStackTrace(); 
            return ""; 
        } 
    }//end getValue() 

    /** *//** 
     * 释放
     */ 
    public void clear() 
    { 
        propertie.clear(); 
    }//end clear(); 
  
    /**
     * 修改内存中配置的值
     * @param key 修改的属性名
     * @param value 修改的属性值
     */
    public void setValue(String key, String value) 
    { 
        propertie.setProperty(key, value); 
    }//end setValue() 

   
    /**
     * 修改配置属性并存入配置文件中
     * @param fileName 配置文件地址
     * @param description 配置项
     */
    public void saveFile(String fileName, String description) 
    { 
        try { 
            outputFile = new FileOutputStream(fileName,true); 
            propertie.store(outputFile, description); 
            outputFile.close(); 
        } catch (FileNotFoundException e) { 
            e.printStackTrace(); 
        } catch (IOException ioe){ 
            ioe.printStackTrace(); 
        } 
    }//end saveFile() 

    public static void main(String[] args) 
    { 
        SubscribeConfig rc = new SubscribeConfig("sys_config/config.properties");

        String ip = rc.getValue("LdapAddr");
        String host = rc.getValue("Localhost"); 
        String tab = rc.getValue("TreeNode"); 
       
        System.out.println("ip = " + ip + "ip-test leng = " + "ip-test".length());
        System.out.println("ip's length = " + ip.length()); 
        System.out.println("host = " + host); 
        System.out.println("tab = " + tab); 
        SubscribeConfig cf = new SubscribeConfig();
        //        cf.clear(); 
        cf.setValue("min", "999"); 
        cf.setValue("max", "1000"); 
        cf.saveFile("sys_config/config.properties", "test"); 

//        Configuration saveCf = new Configuration(); 
//        saveCf.setValue("min", "10"); 
//        saveCf.setValue("max", "1000"); 
//        saveCf.saveFile(".\config\save.perperties"); 

    }//end main() 

}//end class ReadConfigInfo
