package inmethod.news;

import inmethod.commons.util.HTMLConverter;

public class News {


  public static final String FIELD_OID = "Oid";
  public static final String FIELD_CATALOG = "Catalog";
  public static final String FIELD_SUBJECT = "Subject";
  public static final String FIELD_MESSAGE = "Message";
  public static final String FIELD_UPLOAD_TYPE = "UploadType";
  public static final String FIELD_UPLOAD_NAME = "UploadName";
  public static final String FIELD_UPLOAD_PATH = "UploadPath";
  public static final String FIELD_UPDATE_DT = "UpdateDt";


  private Integer aOid = null;
  private Integer aCatalog = null;
  private String aSubject = null;
  private String aMessage = null;
  private Integer aUploadType = null;
  private String aUploadName = null;
  private String aUploadPath = null;
  private String aUpdateDt = null;
  private int iFieldCount = 8;

  public int getFieldCount(){
    return iFieldCount;
  }

  public void setOid(Integer param){
    aOid = param;
  }

  public Integer getOid(){
    return aOid;
  }

  public void setCatalog(Integer param){
    aCatalog = param;
  }

  public Integer getCatalog(){
    return aCatalog;
  }

  public void setSubject(String param){
    aSubject = param;
  }

  public String getSubject(){
    return aSubject;
  }

  public void setMessage(String param){
    aMessage = param;
  }

  public String getMessage(){
    return aMessage;
  }

  public void setUploadType(Integer param){
    aUploadType = param;
  }

  public Integer getUploadType(){
    return aUploadType;
  }

  public void setUploadName(String param){
    aUploadName = param;
  }

  public String getUploadName(){
    return aUploadName;
  }

  public void setUploadPath(String param){
    aUploadPath = param;
  }

  public String getUploadPath(){
    return aUploadPath;
  }

  public void setUpdateDt(String param){
    aUpdateDt = param;
  }

  public String getUpdateDt(){
    return aUpdateDt;
  }

  public String toJson(){
    String sJson =  "";
    if(aOid!=null)
      sJson = sJson + "\"Oid\":"+"\""+aOid+"\",";
    if(aCatalog!=null)
      sJson = sJson + "\"Catalog\":"+"\""+aCatalog+"\",";
    if(aSubject!=null)
      sJson = sJson + "\"Subject\":"+"\""+HTMLConverter.nl2br(aSubject).replace("\"","\\\"")+"\",";
    if(aMessage!=null)
      sJson = sJson + "\"Message\":"+"\""+HTMLConverter.nl2br( aMessage).replace("\"","\\\"")+"\",";
    if(aUploadType!=null)
      sJson = sJson + "\"UploadType\":"+"\""+aUploadType+"\",";
    if(aUploadName!=null)
      sJson = sJson + "\"UploadName\":"+"\""+HTMLConverter.nl2br(aUploadName).replace("\"","\\\"")+"\",";
    if(aUploadPath!=null)
      sJson = sJson + "\"UploadPath\":"+"\""+HTMLConverter.nl2br(aUploadPath).replace("\"","\\\"")+"\",";
    if(aUpdateDt!=null)
      sJson = sJson + "\"UpdateDt\":"+"\""+HTMLConverter.nl2br(aUpdateDt).replace("\"","\\\"")+"\",";

    sJson = "{"+sJson.substring(0,sJson.length()-1)+"}";
    return sJson;
  }

}