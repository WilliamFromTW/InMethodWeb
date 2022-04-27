package inmethod.backend;


import inmethod.commons.util.HTMLConverter;

public class DownloadInfo {


  public static final String FIELD_FILE_ID = "FileId";
  public static final String FIELD_PRG_ID = "PrgId";
  public static final String FIELD_FILE_NAME = "FileName";
  public static final String FIELD_FILE_PATH = "FilePath";
  public static final String FIELD_CREATE_TIMESTAMP = "CreateTimestamp";


  private String aFileId = null;
  private String aPrgId = null;
  private String aFileName = null;
  private String aFilePath = null;
  private String aCreateTimestamp = null;
  private int iFieldCount = 5;

  public int getFieldCount(){
    return iFieldCount;
  }

  public void setFileId(String param){
    aFileId = param;
  }

  public String getFileId(){
    return aFileId;
  }

  public void setPrgId(String param){
    aPrgId = param;
  }

  public String getPrgId(){
    return aPrgId;
  }

  public void setFileName(String param){
    aFileName = param;
  }

  public String getFileName(){
    return aFileName;
  }

  public void setFilePath(String param){
    aFilePath = param;
  }

  public String getFilePath(){
    return aFilePath;
  }

  public void setCreateTimestamp(String param){
    aCreateTimestamp = param;
  }

  public String getCreateTimestamp(){
    return aCreateTimestamp;
  }

  public String toJson(){
    String sJson =  "";
    if(aFileId!=null)
      sJson = sJson + "\"FileId\":"+"\""+HTMLConverter.nl2br(aFileId)+"\",";
    if(aPrgId!=null)
      sJson = sJson + "\"PrgId\":"+"\""+HTMLConverter.nl2br(aPrgId)+"\",";
    if(aFileName!=null)
      sJson = sJson + "\"FileName\":"+"\""+HTMLConverter.nl2br(aFileName)+"\",";
    if(aFilePath!=null)
      sJson = sJson + "\"FilePath\":"+"\""+HTMLConverter.nl2br(aFilePath)+"\",";
    if(aCreateTimestamp!=null)
      sJson = sJson + "\"CreateTimestamp\":"+"\""+HTMLConverter.nl2br(aCreateTimestamp)+"\",";

    sJson = "{"+sJson.substring(0,sJson.length()-1)+"}";
    return sJson;
  }

}